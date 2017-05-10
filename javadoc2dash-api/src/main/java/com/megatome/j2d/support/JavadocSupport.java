/**
 * Copyright 2015 Megatome Technologies, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.megatome.j2d.support;

import com.megatome.j2d.exception.BuilderException;
import com.megatome.j2d.util.IndexData;
import com.megatome.j2d.util.SearchIndexValue;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static com.megatome.j2d.util.LogUtility.logVerbose;
import static org.apache.commons.io.FileUtils.getFile;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

/**
 * Utility class to support Javadoc related docset tasks.
 */
public final class JavadocSupport {
    private static final Pattern parentPattern = Pattern.compile("span|code|i|b", Pattern.CASE_INSENSITIVE);
    private static final List<MatchType> extraIndexingTypes = Arrays.asList(MatchType.CLASS, MatchType.INTERFACE, MatchType.ENUM, MatchType.EXCEPTION, MatchType.ERROR);

    private JavadocSupport() {}

    /**
     * Find the file to be used as the docset index and locate all Javadoc files to be indexed.
     * @param javadocDir Directory where the Javadoc is located
     * @return IndexData object
     * @throws BuilderException
     * @see IndexData
     */
    public static IndexData findIndexFile(File javadocDir) throws BuilderException {
        final IndexData indexData = new IndexData();
        if (!javadocDir.exists() || !javadocDir.isDirectory()) {
            throw new BuilderException(String.format("%s does not exist, or is not a directory", javadocDir.getAbsolutePath()));
        }

        logVerbose("Looking for javadoc files");

        String docsetIndexFile = "overview-summary.html";

        if (!getFile(javadocDir, docsetIndexFile).exists()) {
            docsetIndexFile = null;
        }

        final File indexFilesDir = getFile(javadocDir, "index-files");
        if (indexFilesDir.exists() && indexFilesDir.isDirectory()) {
            docsetIndexFile = (docsetIndexFile != null) ? docsetIndexFile : "index-1.html";
            for (File f : FileUtils.listFiles(indexFilesDir, new String[]{"html"}, false)) {
                if (f.getName().startsWith("index-")) {
                    indexData.addFileToIndex(f);
                }
            }
        } else if (getFile(javadocDir, "index-all.html").exists()){
            docsetIndexFile = (docsetIndexFile != null) ? docsetIndexFile : "index-all.html";
            indexData.addFileToIndex(getFile(javadocDir, "index-all.html"));
        }

        if (!indexData.hasFilesToIndex()) {
            throw new BuilderException(String.format("Did not find any javadoc files. Make sure that %s is a directory containing javadoc", javadocDir.getAbsolutePath()));
        }

        indexData.setDocsetIndexFile(docsetIndexFile);
        logVerbose("Found javadoc files");
        return indexData;
    }

    /**
     * Find all values to be indexed within the specified list of files.
     * @param filesToIndex List of Javadoc files to parse
     * @return List of relevant values to be indexed in the docset
     * @throws BuilderException
     */
    public static List<SearchIndexValue> findSearchIndexValues(List<File> filesToIndex) throws BuilderException {
        final List<SearchIndexValue> values = new ArrayList<>();
        for (final File f : filesToIndex) {
            final List<SearchIndexValue> indexValues = indexFile(f);
            values.addAll(indexValues);
        }
        return values;
    }

    private static List<SearchIndexValue> indexFile(File f) throws BuilderException {
        final List<SearchIndexValue> values = new ArrayList<>();
        final Elements elements = loadAndFindLinks(f);
        for (final Element e : elements) {
            Element parent = e.parent();
            if (!parent.child(0).equals(e)) {
                continue;
            }
            String parentTagName = parent.tagName();
            if (parentPattern.matcher(parentTagName).matches()) {
                parent = parent.parent();
                parentTagName = parent.tagName();
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

            final MatchType type = getMatchingType(text, className);

            if (null == type) {
                System.err.println(String.format("Unknown type found. Please submit a bug report. (Text: %s, Name: %s, className: %s)", text, name, className));
                continue;
            }
            try {
                final String linkPath = URLDecoder.decode(e.attr("href"), "UTF-8");

                values.add(new SearchIndexValue(name, type, linkPath));
            } catch (UnsupportedEncodingException ex) {
                throw new BuilderException("Error decoding a link", ex);
            }
        }
        return values;
    }

    private static Elements loadAndFindLinks(final File f) throws BuilderException {
        try {
            final Document doc = Jsoup.parse(f, "UTF-8");
            return doc.select("a");
        } catch (IOException e) {
            throw new BuilderException("Failed to index javadoc files", e);
        }
    }

    private static MatchType getMatchingType(String text, String className) {
        for (final MatchType matchType : MatchType.values()) {
            if (matchType.matches(text, className)) {
                return matchType;
            }
        }
        return null;
    }
}
