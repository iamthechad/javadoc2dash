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
