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
package com.megatome.j2d.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

/**
 * Enumeration for matching types from parsed Javadoc files
 */
public enum MatchType {
    CLASS("Class", "class", "Class in", "- class"),
    STATIC_METHOD("Method", "method", "Static method in"),
    FIELD("Field", "field", "Static variable in", "Field in"),
    CONSTRUCTOR("Constructor", "constructor", "Constructor"),
    METHOD("Method", null, "Method in", "method.summary"),
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
