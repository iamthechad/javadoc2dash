package com.megatome.j2d.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

enum MatchType {
    CLASS("Class", "class", "Class in", "- class"),
    STATIC_METHOD("Method", "method", "Static method in"),
    FIELD("Field", "field", "Static variable in", "Field in"),
    CONSTRUCTOR("Constructor", "constructor", "Constructor"),
    METHOD("Method", null, "Method in"),
    VARIABLE("Field", null, "Variable in"),
    INTERFACE("Interface", "interface", "Interface in", "- interface"),
    EXCEPTION("Exception", "exception", "Exception in", "- exception"),
    ERROR("Error", "error", "Error in", "- error"),
    ENUM("Enum", "enum", "Enum in", "- enum"),
    TRAIT("Trait", null, "Trait in"),
    NOTATION("Notation", "annotation", "Annotation Type"),
    PACKAGE("Package", "package", "package");

    private String typeName;
    private List<String> matchingText = new ArrayList<>();
    private String classSuffix;

    MatchType(String typeName, String classSuffix, String... textMatches) {
        this.typeName = typeName;
        Collections.addAll(this.matchingText, textMatches);
        this.classSuffix = classSuffix;
    }

    public String getTypeName() {
        return typeName;
    }

    public boolean matches(String target, String className) {
        for (final String searchString : matchingText) {
            if (containsIgnoreCase(target, searchString)) {
                return true;
            }
        }

        return null != classSuffix && null != className && containsIgnoreCase(className, classSuffix);
    }
}
