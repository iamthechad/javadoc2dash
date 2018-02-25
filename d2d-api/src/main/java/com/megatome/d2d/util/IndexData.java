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
package com.megatome.d2d.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class that identifies the docset index file and any files that need to be parsed and indexed.
 */
public class IndexData {
    private String docsetIndexFile;
    private List<File> filesToIndex = new ArrayList<>();

    /**
     * Set the docset index file
     * @param docsetIndexFile Index file
     */
    public void setDocsetIndexFile(String docsetIndexFile) {
        this.docsetIndexFile = docsetIndexFile;
    }

    /**
     * Get the docset index file
     * @return Index file
     */
    public String getDocsetIndexFile() {
        return docsetIndexFile;
    }

    /**
     * Add a file to be indexed
     * @param fileToIndex File
     */
    public void addFileToIndex(File fileToIndex) {
        if (null != fileToIndex) {
            filesToIndex.add(fileToIndex);
        }
    }

    /**
     * Get the list of files to index
     * @return List of files
     */
    public List<File> getFilesToIndex() {
        return Collections.unmodifiableList(filesToIndex);
    }

    /**
     * Determine if there are any files to index
     * @return True if one or more files have been added to index
     */
    public boolean hasFilesToIndex() {
        return !filesToIndex.isEmpty();
    }
}
