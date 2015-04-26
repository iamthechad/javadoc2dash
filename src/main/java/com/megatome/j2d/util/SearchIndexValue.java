package com.megatome.j2d.util;

public class SearchIndexValue {
    private final String name;
    private final String type;
    private final String path;

    public SearchIndexValue(String name, String type, String path) {
        this.name = name;
        this.type = type;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getPath() {
        return path;
    }
}
