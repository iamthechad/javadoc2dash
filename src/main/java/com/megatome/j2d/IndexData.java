package com.megatome.j2d;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class IndexData {
    private String docsetIndexFile;
    private List<File> filesToIndex = new ArrayList<>();

    public IndexData() {
    }

    public void setDocsetIndexFile(String docsetIndexFile) {
        this.docsetIndexFile = docsetIndexFile;
    }

    public String getDocsetIndexFile() {
        return docsetIndexFile;
    }

    public void addFileToIndex(File filePath) {
        if (null != filePath) {
            filesToIndex.add(filePath);
        }
    }

    public List<File> getFilesToIndex() {
        return filesToIndex;
    }

    public boolean hasFilesToIndex() {
        return !filesToIndex.isEmpty();
    }
}
