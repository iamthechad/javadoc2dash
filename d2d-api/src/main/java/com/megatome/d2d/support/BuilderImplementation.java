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

import java.util.Optional;

/**
 * Known builder implementations
 */
public enum BuilderImplementation {
    JAVADOC("javadoc"),
    JSDOC("jsdoc");

    private String name;

    BuilderImplementation(String builderName) {
        this.name = builderName;
    }

    public static Optional<BuilderImplementation> fromString(String builderName) {
        for (BuilderImplementation b : BuilderImplementation.values()) {
            if (b.name.equalsIgnoreCase(builderName)) {
                return Optional.of(b);
            }
        }
        return Optional.empty();
    }
}
