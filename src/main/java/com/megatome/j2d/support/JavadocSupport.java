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

import com.megatome.j2d.util.IndexData;
import com.megatome.j2d.util.SearchIndexValue;
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

/**
 * Utility class to support Javadoc related docset tasks.
 */
public final class JavadocSupport {
    private static final Logger LOG = LoggerFactory.getLogger(JavadocSupport.class);

    private static final Pattern parentPattern = Pattern.compile("span|code|i|b", Pattern.CASE_INSENSITIVE);

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

        LOG.info("Looking for javadoc files");

        String docsetIndexFile = "overview-summary.html";

        if (!getFile(javadocDir, docsetIndexFile).exists()) {
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
            throw new BuilderException(String.format("Did not find any javadoc files. Make sure that %s is a directory containing javadoc", javadocDir.getAbsolutePath()));
        }

        indexData.setDocsetIndexFile(docsetIndexFile);
        LOG.info("Found javadoc files");
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
