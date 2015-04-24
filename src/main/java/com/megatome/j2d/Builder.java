package com.megatome.j2d;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Builder {
    private static final String CONTENTS = "Contents";
    private static final String RESOURCES = "Resources";
    private static final String DOCUMENTS = "Documents";

    private final String docsetRoot;
    private final String javadocRoot;
    private File documentsDir;

    public static void main(String... args) {
        final String docsetName = args[0];
        final String javadocRoot = args[1];
        final Builder builder = new Builder(docsetName, javadocRoot);
        builder.build();
    }

    public Builder(String docsetName, String javadocRoot) {
        docsetRoot = docsetName;
        this.javadocRoot = javadocRoot;
    }

    public void build() {
        if (createDocSetStructure()) {
            System.out.println("Bundle created successfully");
            final File javadocDir = new File(javadocRoot);
            final IndexData indexData = findIndexFile(javadocDir);
            copyFiles(javadocDir);
            createPList(indexData);
            createIndex(javadocDir, indexData);
        } else {
            System.out.println("Failed to create directories");
        }
    }

    private void createPList(IndexData indexData) {
        final String plist = String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?><plist version=\"1.0\"><dict><key>CFBundleIdentifier</key><string>%s</string><key>CFBundleName</key><string>%s</string><key>DocSetPlatformFamily</key><string>%s</string><key>dashIndexFilePath</key><string>%s</string><key>DashDocSetFamily</key><string>java</string><key>isDashDocset</key><true/></dict></plist>",
                docsetRoot, docsetRoot, docsetRoot, indexData.getDocsetIndexFile());
        try {
            FileUtils.write(FileUtils.getFile(docsetRoot + ".docset", CONTENTS, "Info.plist"), plist);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private IndexData findIndexFile(final File javadocDir) {
        final IndexData indexData = new IndexData();
        if (!javadocDir.exists() || !javadocDir.isDirectory()) {
            System.out.println("Bad path to javadoc");
            return null;
        }

        String docsetIndexFile = "overview-summary.html";
        if (!new File(javadocRoot, docsetIndexFile).exists()) {
            docsetIndexFile = null;
        }

        final File indexFilesDir = new File(javadocDir, "index-files");
        if (indexFilesDir.exists() && indexFilesDir.isDirectory()) {
            docsetIndexFile = (docsetIndexFile != null) ? docsetIndexFile : "index-1.html";
            // TODO Loop over dir to find objects to index
        } else {
            docsetIndexFile = (docsetIndexFile != null) ? docsetIndexFile : "index-all.html";
            indexData.addFileToIndex(new File(javadocDir, "index-all.html"));
        }

        indexData.setDocsetIndexFile(docsetIndexFile);
        return indexData;
    }

    private void copyFiles(final File javadocDir) {
        try {
            FileUtils.copyDirectory(javadocDir, documentsDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createIndex(File javadocDir, IndexData indexData) {
        // Create DB file
        final String dbFilePath = CONTENTS + File.separator + RESOURCES + File.separator + "docSet.dsidx";
        final File dbFile = new File(docsetRoot + ".docset", dbFilePath);

        try (final Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            final Statement stmt = connection.createStatement()){
            stmt.execute("CREATE TABLE searchIndex(id INTEGER PRIMARY KEY, name TEXT, type TEXT, path TEXT)");
            // Index files
            final List<SearchIndexValue> indexValues = findSearchIndexValues(indexData.getFilesToIndex());
            // Update DB
            try (final PreparedStatement pst = connection.prepareStatement("INSERT INTO searchIndex(name, type, path) VALUES (?, ?, ?)")) {
                for (final SearchIndexValue value : indexValues) {
                    pst.setString(1, value.getName());
                    pst.setString(2, value.getType());
                    pst.setString(3, value.getPath());
                    pst.execute();
                }
            } catch (SQLException e) {
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<SearchIndexValue> findSearchIndexValues(List<File> filesToIndex) {
        final List<SearchIndexValue> values = new ArrayList<>();
        for (final File f : filesToIndex) {
            values.addAll(indexFile(f));
        }
        return values;
    }

    private List<SearchIndexValue> indexFile(File f) {
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
                if (!StringUtils.containsIgnoreCase(parentTagName, "dt")) {
                    continue;
                }
                final String text = parent.text();
                final String name = e.text();
                final String className = parent.className();

                String type = "unknown";
                //System.out.println("Text: " + text + ", Name: " + name + ", className: " + className);
                if (StringUtils.containsIgnoreCase(text, "Class in") || StringUtils.containsIgnoreCase(text, "- class") || (className.endsWith("class"))) {
                    type = "Class";
                } else if (StringUtils.containsIgnoreCase(text, "Static method in") || (className.endsWith("method"))) {
                    type = "Method";
                } else if (StringUtils.containsIgnoreCase(text, "Static variable in") || StringUtils.containsIgnoreCase(text, "Field in") || (className.endsWith("field"))) {
                    type = "Field";
                } else if (StringUtils.containsIgnoreCase(text, "Constructor") || (className.endsWith("constructor"))) {
                    type = "Constructor";
                } else if (StringUtils.containsIgnoreCase(text, "Method in")) {
                    type = "Method";
                } else if (StringUtils.containsIgnoreCase(text, "Variable in")) {
                    type = "Field";
                } else if (StringUtils.containsIgnoreCase(text, "Interface in") || StringUtils.containsIgnoreCase(text, "- interface") || (className.endsWith("interface"))) {
                    type = "Interface";
                } else if (StringUtils.containsIgnoreCase(text, "Exception in") || StringUtils.containsIgnoreCase(text, "- exception") || (className.endsWith("exception"))) {
                    type = "Exception";
                } else if (StringUtils.containsIgnoreCase(text, "Error in") || StringUtils.containsIgnoreCase(text, "- error") || (className.endsWith("error"))) {
                    type = "Error";
                } else if (StringUtils.containsIgnoreCase(text, "Enum in") || StringUtils.containsIgnoreCase(text, "- enum") || (className.endsWith("enum"))) {
                    type = "Enum";
                } else if (StringUtils.containsIgnoreCase(text, "Trait in")) {
                    type = "Trait";
                } else if (StringUtils.containsIgnoreCase(text, "Annotation Type") || (className.endsWith("annotation"))) {
                    type = "Notation";
                } else if (StringUtils.containsIgnoreCase(text, "package") || (className.endsWith("package"))) {
                    type = "Package";
                } else {
                    System.out.println("Unknown type. Text: " + text + ", Name: " + name + ", className: " + className);
                    continue;
                }

                final String linkPath = e.attr("href");

                values.add(new SearchIndexValue(name, type, linkPath));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return values;
    }

    private boolean createDocSetStructure() {
        final String filePath = CONTENTS + File.separator + RESOURCES + File.separator + DOCUMENTS;
        // Create dir
        //final File docsetDir = new File(docsetRoot + ".docset");
        final File docsetDir = FileUtils.getFile(docsetRoot + ".docset");
        if (docsetDir.exists()) {
            System.out.println("Directory exists. Aborting.");
            return false;
        }
        //documentsDir = new File(docsetDir, filePath);
        documentsDir = FileUtils.getFile(docsetDir, CONTENTS, RESOURCES, DOCUMENTS);
        return documentsDir.mkdirs();
    }
}
