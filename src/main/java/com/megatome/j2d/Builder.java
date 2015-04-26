package com.megatome.j2d;

import com.megatome.j2d.exception.BuilderException;
import com.megatome.j2d.util.IndexData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.megatome.j2d.support.DBSupport.createIndex;
import static com.megatome.j2d.support.DocSetSupport.*;
import static com.megatome.j2d.support.JavadocSupport.findIndexFile;
import static com.megatome.j2d.support.JavadocSupport.findSearchIndexValues;

public class Builder {
    private static final Logger LOG = LoggerFactory.getLogger(Builder.class);

    private final String docsetRoot;
    private final String displayName;
    private final String keyword;
    private final String iconFilePath;
    private final String docsetDir;
    private final String javadocRoot;

    public Builder(String docsetName, String javadocRoot, String displayName, String keyword, String iconFilePath) {
        docsetRoot = docsetName;
        this.displayName = (displayName == null) ? docsetRoot : displayName;
        this.keyword = (keyword == null) ? docsetRoot : keyword;
        this.iconFilePath = iconFilePath;
        this.docsetDir = docsetRoot + ".docset";
        this.javadocRoot = javadocRoot;
    }

    public void build() {
        try {
            createDocSetStructure(docsetRoot, docsetDir);
            copyIconFile(iconFilePath, docsetDir);
            final IndexData indexData = findIndexFile(javadocRoot);
            copyFiles(javadocRoot, docsetDir);
            createPList(docsetRoot, displayName, keyword, docsetDir, indexData);
            createIndex(findSearchIndexValues(indexData.getFilesToIndex()), getDBDir(docsetDir));
            LOG.info("Finished creating docset");
        } catch (BuilderException e) {
            LOG.error("Failed to create docset", e);
        }
    }
}
