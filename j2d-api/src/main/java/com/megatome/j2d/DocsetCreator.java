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
package com.megatome.j2d;

import com.megatome.j2d.exception.BuilderException;
import com.megatome.j2d.util.IndexData;
import com.megatome.j2d.util.RuntimeConfig;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

import static com.megatome.j2d.support.DBSupport.createIndex;
import static com.megatome.j2d.support.DocSetSupport.*;
import static com.megatome.j2d.support.JavadocSupport.findIndexFile;
import static com.megatome.j2d.support.JavadocSupport.findSearchIndexValues;

/**
 * Class responsible for creating the docset.
 */
public class DocsetCreator {
    private final String docsetName;
    private final String displayName;
    private final String keyword;
    private final File iconFilePath;
    private final File javadocRoot;
    private final File outputDirectory;

    public static class Builder {
        private final String docsetName;
        private final File javadocRoot;

        private String displayName;
        private String keyword;
        private File iconFilePath = null;
        private File outputDirectory = FileUtils.getFile(".");

        public Builder(String docsetName, File javadocRoot) {
            if (null == docsetName || docsetName.isEmpty()) {
                throw new IllegalArgumentException("The docsetName must be specified");
            }
            if (null == javadocRoot) {
                throw new IllegalArgumentException("The javadocRoot must be specified");
            }
            this.docsetName = docsetName;
            this.javadocRoot = javadocRoot;
            this.displayName = docsetName;
            this.keyword = docsetName;
        }

        public Builder displayName(String displayName) {
            if (null != displayName && !displayName.isEmpty()) {
                this.displayName = displayName;
            }
            return this;
        }

        public Builder keyword(String keyword) {
            if (null != keyword && !keyword.isEmpty()) {
                this.keyword = keyword;
            }
            return this;
        }

        public Builder outputDirectory(File outputDirectory) {
            if (null != outputDirectory) {
                this.outputDirectory = outputDirectory;
            }
            return this;
        }

        public Builder iconFile(File iconFile) {
            if (null != iconFile) {
                this.iconFilePath = iconFile;
            }
            return this;
        }

        public DocsetCreator build() {
            return new DocsetCreator(this);
        }
    }

    private DocsetCreator(Builder builder) {
        this.docsetName = builder.docsetName;
        this.displayName = builder.displayName;
        this.keyword = builder.keyword;
        this.iconFilePath = builder.iconFilePath;
        this.javadocRoot = builder.javadocRoot;
        this.outputDirectory = builder.outputDirectory;
    }

    /**
     * Ctor
     * @param docsetName File name of docset to create
     * @param javadocRoot Root directory of the Javadoc to create the docset from
     * @param displayName Name to display in Dash. Defaults to <code>docsetName</code> if unspecified
     * @param keyword Keyword to associate this docset with. Defaults to <code>docsetName</code> is unspecified
     * @param iconFilePath Path to an icon to include in the docset. Should be a 32x32 PNG. No icon will be used if this is unspecified.
     * @param outputDirectory Location for the created docset
     */
    /*public DocsetCreator(String docsetName, File javadocRoot, String displayName, String keyword, File iconFilePath, File outputDirectory) {
        this.docsetName = docsetName;
        this.displayName = (displayName == null) ? docsetName : displayName;
        this.keyword = (keyword == null) ? docsetName : keyword;
        this.iconFilePath = iconFilePath;
        this.javadocRoot = javadocRoot;
        this.outputDirectory = outputDirectory;
    }*/

    /**
     * Build the docset.
     */
    public void makeDocset() {
        final String docsetRoot = FilenameUtils.concat(outputDirectory.getAbsolutePath(), docsetName);
        try {
            createDocSetStructure(docsetRoot);
            copyIconFile(iconFilePath, docsetRoot);
            final IndexData indexData = findIndexFile(javadocRoot);
            copyFiles(javadocRoot, docsetRoot);
            createPList(docsetName, displayName, keyword, indexData.getDocsetIndexFile(), docsetRoot);
            createIndex(findSearchIndexValues(indexData.getFilesToIndex()), getDBDir(docsetRoot));
            System.out.println("Finished creating docset: " + docsetRoot);
        } catch (BuilderException e) {
            System.out.println("Failed to create docset: " + e.getMessage());
            if (RuntimeConfig.isVerbose()) {
                e.printStackTrace();
            }
        }
    }
}
