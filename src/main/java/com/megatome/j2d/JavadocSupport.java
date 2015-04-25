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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.apache.commons.io.FileUtils.getFile;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

public final class JavadocSupport {
    private static final Logger LOG = LoggerFactory.getLogger(JavadocSupport.class);

    private static final Pattern parentPattern = Pattern.compile("span|code|i|b", Pattern.CASE_INSENSITIVE);

    private JavadocSupport() {}

    public static IndexData findIndexFile(String javadocRoot) throws BuilderException {
        final IndexData indexData = new IndexData();
        final File javadocDir = getFile(javadocRoot);
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

    public static List<SearchIndexValue> findSearchIndexValues(List<File> filesToIndex) throws BuilderException {
        final List<SearchIndexValue> values = new ArrayList<>();
        for (final File f : filesToIndex) {
            values.addAll(indexFile(f));
        }
        return values;
    }

    private static List<SearchIndexValue> indexFile(File f) throws BuilderException {
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
}
