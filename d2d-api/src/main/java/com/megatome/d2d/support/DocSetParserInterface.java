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
