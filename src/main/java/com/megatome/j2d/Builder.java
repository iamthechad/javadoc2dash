package com.megatome.j2d;

import com.megatome.j2d.exception.BuilderException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.apache.commons.io.FileUtils.*;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

public class Builder {
    private static final Logger LOG = LoggerFactory.getLogger(Builder.class);

    private static final String CONTENTS = "Contents";
    private static final String RESOURCES = "Resources";
    private static final String DOCUMENTS = "Documents";

    private static final String PLIST_FILE = "Info.plist";
    private static final String DB_FILE = "docSet.dsidx";

    private static final String CREATE_INDEX_SQL = "CREATE TABLE searchIndex(id INTEGER PRIMARY KEY, name TEXT, type TEXT, path TEXT)";
    private static final String INSERT_INDEX_SQL = "INSERT INTO searchIndex(name, type, path) VALUES (?, ?, ?)";

    private final String docsetRoot;
    private final String docsetDir;
    private final String javadocRoot;
    private File documentsDir;
    private File javadocDir;

    public static void main(String... args) {
        final String docsetName = args[0];
        final String javadocRoot = args[1];
        final Builder builder = new Builder(docsetName, javadocRoot);
        builder.build();
    }

    public Builder(String docsetName, String javadocRoot) {
        docsetRoot = docsetName;
        this.docsetDir = docsetRoot + ".docset";
        this.javadocRoot = javadocRoot;
        this.javadocDir = getFile(javadocRoot);
    }

    public void build() {
        try {
            createDocSetStructure();
            final IndexData indexData = findIndexFile(javadocDir);
            copyFiles(javadocDir);
            createPList(indexData);
            createIndex(indexData);
            LOG.info("Finished creating docset");
        } catch (BuilderException e) {
            LOG.error("Failed to create docset", e);
        }
    }

    private void createPList(IndexData indexData) throws BuilderException {
        final String plist = String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?><plist version=\"1.0\"><dict><key>CFBundleIdentifier</key><string>%s</string><key>CFBundleName</key><string>%s</string><key>DocSetPlatformFamily</key><string>%s</string><key>dashIndexFilePath</key><string>%s</string><key>DashDocSetFamily</key><string>java</string><key>isDashDocset</key><true/></dict></plist>",
                docsetRoot, docsetRoot, docsetRoot, indexData.getDocsetIndexFile());
        try {
            write(getFile(docsetDir, CONTENTS, PLIST_FILE), plist);
            LOG.info("Created the plist file in the docset");
        } catch (IOException e) {
            throw new BuilderException("Failed to write plist file into docset", e);
        }
    }

    private IndexData findIndexFile(final File javadocDir) throws BuilderException {
        final IndexData indexData = new IndexData();
        if (!javadocDir.exists() || !javadocDir.isDirectory()) {
            throw new BuilderException(String.format("%s does not exist, or is not a directory", javadocRoot));
        }

        LOG.info("Looking for javadoc files");

        String docsetIndexFile = "overview-summary.html";

        if (!getFile(javadocRoot, docsetIndexFile).exists()) {
            docsetIndexFile = null;
        }

        final File indexFilesDir = getFile(javadocDir, "index-files");
        if (indexFilesDir.exists() && indexFilesDir.isDirectory()) {
            docsetIndexFile = (docsetIndexFile != null) ? docsetIndexFile : "index-1.html";
            // TODO Loop over dir to find objects to index
        } else {
            docsetIndexFile = (docsetIndexFile != null) ? docsetIndexFile : "index-all.html";
            indexData.addFileToIndex(getFile(javadocDir, "index-all.html"));
        }

        if (!indexData.hasFilesToIndex()) {
            throw new BuilderException(String.format("Did not find any javadoc files. Make sure that %s is a directory containing javadoc", javadocRoot));
        }

        indexData.setDocsetIndexFile(docsetIndexFile);
        LOG.info("Found javadoc files");
        return indexData;
    }

    private void copyFiles(final File javadocDir) throws BuilderException {
        try {
            copyDirectory(javadocDir, documentsDir);
            LOG.info("Copied javadoc files into docset");
        } catch (IOException e) {
            throw new BuilderException("Could not copy files into the docset", e);
        }
    }

    private void createIndex(IndexData indexData) throws BuilderException {
        // Create DB file
        final File dbFile = getFile(docsetDir, CONTENTS, RESOURCES, DB_FILE);

        try (final Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            final Statement stmt = connection.createStatement()){
            stmt.execute(CREATE_INDEX_SQL);
            // Index files
            final List<SearchIndexValue> indexValues = findSearchIndexValues(indexData.getFilesToIndex());
            // Update DB
            try (final PreparedStatement pst = connection.prepareStatement(INSERT_INDEX_SQL)) {
                for (final SearchIndexValue value : indexValues) {
                    pst.setString(1, value.getName());
                    pst.setString(2, value.getType());
                    pst.setString(3, value.getPath());
                    pst.execute();
                }
            }
        } catch (SQLException e) {
            throw new BuilderException("Error writing to SQLite DB", e);
        }
        LOG.info("Created the SQLite search index");
    }

    private List<SearchIndexValue> findSearchIndexValues(List<File> filesToIndex) throws BuilderException {
        final List<SearchIndexValue> values = new ArrayList<>();
        for (final File f : filesToIndex) {
            values.addAll(indexFile(f));
        }
        return values;
    }

    private List<SearchIndexValue> indexFile(File f) throws BuilderException {
        final Pattern parentPattern = Pattern.compile("span|code|i|b", Pattern.CASE_INSENSITIVE);
        final List<SearchIndexValue> values = new ArrayList<>();
        try {
            final Document doc = Jsoup.parse(f, "UTF-8");
            final Elements elements = doc.select("a");
            for (final Element e : elements) {
                Element parent = e.parent();
                if (!parent.child(0).equals(e)) {
                    continue;
                }
                final String parentTagName = parent.tagName();
                if (parentPattern.matcher(parentTagName).matches()) {
                    parent = parent.parent();
                    if (!parent.child(0).equals(e.parent())) {
                        continue;
                    }
                }
                if (!containsIgnoreCase(parentTagName, "dt")) {
                    continue;
                }
                final String text = parent.text();
                final String name = e.text();
                final String className = parent.className();

                String type = null;
                for (final MatchType matchType : MatchType.values()) {
                    if (matchType.matches(text, className)) {
                        type = matchType.getTypeName();
                        break;
                    }
                }

                if (null == type) {
                    LOG.error("Unknown type found. Please submit a bug report. (Text: {}, Name: {}, className: {})", text, name, className);
                    continue;
                }
                final String linkPath = e.attr("href");

                values.add(new SearchIndexValue(name, type, linkPath));
            }
        } catch (IOException e) {
            throw new BuilderException("Failed to index javadoc files", e);
        }
        return values;
    }

    private void createDocSetStructure() throws BuilderException {
        // Create dir
        final File docsetRootDir = getFile(docsetDir);
        if (docsetRootDir.exists()) {
            LOG.info("A docset named {} already exists. Trying to remove.", docsetRoot);
            try {
                deleteDirectory(docsetRootDir);
            } catch (IOException e) {
                final String message = "Failed to delete existing docset.";
                LOG.error(message, e);
                throw new BuilderException(message, e);
            }
        }

        documentsDir = getFile(docsetRootDir, CONTENTS, RESOURCES, DOCUMENTS);
        try {
            forceMkdir(documentsDir);
        } catch (IOException e) {
            final String message = "Failed to create new docset directory.";
            LOG.error(message, e);
            throw new BuilderException(message, e);
        }
        LOG.info("Docset directory structure created");
    }
}
