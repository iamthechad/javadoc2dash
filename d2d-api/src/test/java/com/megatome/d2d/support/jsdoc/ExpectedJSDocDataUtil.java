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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ExpectedJSDocDataUtil {
    private final Map<JSDocMatchType, Integer> EXPECTED_TYPES = new HashMap<>();
    private final Map<String, Integer> EXPECTED_DATABASE_TYPES = new HashMap<>();
    private int EXPECTED_ENTRY_COUNT = 0;

    private static final ExpectedJSDocDataUtil INSTANCE = new ExpectedJSDocDataUtil();

    private ExpectedJSDocDataUtil() {
        EXPECTED_TYPES.put(JSDocMatchType.FUNCTION, 1);

        for (final Integer count : EXPECTED_TYPES.values()) {
            EXPECTED_ENTRY_COUNT += count;
        }

        for (final Map.Entry<JSDocMatchType, Integer> entry : EXPECTED_TYPES.entrySet()) {
            final String dbColumnName = entry.getKey().getTypeName();
            int count = entry.getValue();
            if (EXPECTED_DATABASE_TYPES.containsKey(dbColumnName)) {
                count += EXPECTED_DATABASE_TYPES.get(dbColumnName);
            }
            EXPECTED_DATABASE_TYPES.put(dbColumnName, count);
        }
    }

    public static ExpectedJSDocDataUtil getExpectedData() {
        return INSTANCE;
    }

    public Map<JSDocMatchType, Integer> getExpectedTypes() {
        return Collections.unmodifiableMap(EXPECTED_TYPES);
    }

    public Map<String, Integer> getExpectedDataBaseTypes() {
        return Collections.unmodifiableMap(EXPECTED_DATABASE_TYPES);
    }

    public int getExpectedEntryCount() {
        return EXPECTED_ENTRY_COUNT;
    }
}
