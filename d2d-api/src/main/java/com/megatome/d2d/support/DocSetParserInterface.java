/*
 * Copyright 2018 Megatome Technologies, LLC
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
package com.megatome.d2d.support;

import java.io.File;
import java.util.List;

import com.megatome.d2d.exception.BuilderException;
import com.megatome.d2d.util.IndexData;
import com.megatome.d2d.util.SearchIndexValue;

public interface DocSetParserInterface {

    /**
     * Find the file to be used as the docset index and locate all Javadoc files to be indexed.
     * @param javadocDir Directory where the Javadoc is located
     * @return IndexData object
     * @throws BuilderException in case of errors
     * @see IndexData
     */
    abstract public IndexData findIndexFile(File javadocDir) throws BuilderException;

    /**
     * Find all values to be indexed within the specified list of files.
     * @param filesToIndex List of Javadoc files to parse
     * @return List of relevant values to be indexed in the docset
     * @throws BuilderException in case of errors
     */
    abstract public List<SearchIndexValue> findSearchIndexValues(List<File> filesToIndex) throws BuilderException;
}
