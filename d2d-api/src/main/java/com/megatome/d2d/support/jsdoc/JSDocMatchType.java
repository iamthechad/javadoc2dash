package com.megatome.d2d.support.jsdoc;

import com.megatome.d2d.support.MatchTypeInterface;

public enum JSDocMatchType implements MatchTypeInterface {
    FUNCTION("Function"),
    NAMESPACE("Namespace"),
    PROPERTY("Property"),
    CLASS("Class"),
    VALUE("Value");

    private String typeName;

    JSDocMatchType(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String getTypeName() {
        return typeName;
    }

    /**
     * Return the MatchType depending on the input string
     * @param module the type
     * @return the MatchType
     */
    public static JSDocMatchType type(String module) {
        switch(module) {
            case "functions": return FUNCTION;
            case "namespaces": return NAMESPACE;
            case "properties": return PROPERTY;
            case "classes": return CLASS;
        }
        return VALUE;
    }
}