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
package com.megatome.d2d.util;

import com.megatome.d2d.support.MatchTypeInterface;

/**
 * Represents information that needs to be saved to the docset index.
 */
public class SearchIndexValue {
    private final String name;
    private final MatchTypeInterface type;
    private final String path;

    /**
     * Ctor.
     * @param name Entry name
     * @param type Entry type
     * @param path Path to the entry
     */
    public SearchIndexValue(String name, MatchTypeInterface type, String path) {
        this.name = name;
        this.type = type;
        this.path = path;
    }

    /**
     * Get the entry name
     * @return Name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the entry type
     * @return Type
     */
    public MatchTypeInterface getType() {
        return type;
    }

    /**
     * Get the entry path
     * @return Path
     */
    public String getPath() {
        return path;
    }
}
