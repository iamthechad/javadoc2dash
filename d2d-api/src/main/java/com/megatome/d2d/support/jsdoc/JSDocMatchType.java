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