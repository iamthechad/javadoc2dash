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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static com.megatome.j2d.support.DBSupport.createIndex;
import static com.megatome.j2d.support.DocSetSupport.*;
import static com.megatome.j2d.support.JavadocSupport.findIndexFile;
import static com.megatome.j2d.support.JavadocSupport.findSearchIndexValues;

/**
 * Class responsible for creating the docset.
 */
public class Builder {
    private static final Logger LOG = LoggerFactory.getLogger(Builder.class);

    private final String docsetRoot;
    private final String displayName;
    private final String keyword;
    private final File iconFilePath;
    private final File javadocRoot;

    /**
     * Ctor
     * @param docsetName File name of docset to create
     * @param javadocRoot Root directory of the Javadoc to create the docset from
     * @param displayName Name to display in Dash. Defaults to <code>docsetName</code> if unspecified
     * @param keyword Keyword to associate this docset with. Defaults to <code>docsetName</code> is unspecified
     * @param iconFilePath Path to an icon to include in the docset. Should be a 32x32 PNG. No icon will be used if this is unspecified.
     */
    public Builder(String docsetName, File javadocRoot, String displayName, String keyword, File iconFilePath) {
        docsetRoot = docsetName;
        this.displayName = (displayName == null) ? docsetRoot : displayName;
        this.keyword = (keyword == null) ? docsetRoot : keyword;
        this.iconFilePath = iconFilePath;
        this.javadocRoot = javadocRoot;
    }

    /**
     * Build the docset.
     */
    public void build() {
        try {
            createDocSetStructure(docsetRoot);
            copyIconFile(iconFilePath, docsetRoot);
            final IndexData indexData = findIndexFile(javadocRoot);
            copyFiles(javadocRoot, docsetRoot);
            createPList(docsetRoot, displayName, keyword, indexData.getDocsetIndexFile(), docsetRoot);
            createIndex(findSearchIndexValues(indexData.getFilesToIndex()), getDBDir(docsetRoot));
            LOG.info("Finished creating docset");
        } catch (BuilderException e) {
            LOG.error("Failed to create docset", e);
        }
    }
}
