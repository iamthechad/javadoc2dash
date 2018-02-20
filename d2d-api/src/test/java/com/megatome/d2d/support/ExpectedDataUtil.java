package com.megatome.d2d.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.megatome.d2d.support.javadoc.JavadocMatchType;

public class ExpectedDataUtil {
    private final Map<JavadocMatchType, Integer> EXPECTED_TYPES = new HashMap<>();
    private final Map<String, Integer> EXPECTED_DATABASE_TYPES = new HashMap<>();
    private int EXPECTED_ENTRY_COUNT = 0;

    private static final ExpectedDataUtil INSTANCE = new ExpectedDataUtil();

    private ExpectedDataUtil() {
        EXPECTED_TYPES.put(JavadocMatchType.CLASS, 1);
        EXPECTED_TYPES.put(JavadocMatchType.INTERFACE, 1);
        EXPECTED_TYPES.put(JavadocMatchType.CONSTRUCTOR, 5);
        EXPECTED_TYPES.put(JavadocMatchType.METHOD, 2);
        EXPECTED_TYPES.put(JavadocMatchType.PACKAGE, 6);
        EXPECTED_TYPES.put(JavadocMatchType.EXCEPTION, 1);
        EXPECTED_TYPES.put(JavadocMatchType.ERROR, 1);
        EXPECTED_TYPES.put(JavadocMatchType.FIELD, 1);
        EXPECTED_TYPES.put(JavadocMatchType.ENUM, 1);
        EXPECTED_TYPES.put(JavadocMatchType.NOTATION, 1);
        EXPECTED_TYPES.put(JavadocMatchType.STATIC_METHOD, 3);

        for (final Integer count : EXPECTED_TYPES.values()) {
            EXPECTED_ENTRY_COUNT += count;
        }

        for (final Map.Entry<JavadocMatchType, Integer> entry : EXPECTED_TYPES.entrySet()) {
            final String dbColumnName = entry.getKey().getTypeName();
            int count = entry.getValue();
            if (EXPECTED_DATABASE_TYPES.containsKey(dbColumnName)) {
                count += EXPECTED_DATABASE_TYPES.get(dbColumnName);
            }
            EXPECTED_DATABASE_TYPES.put(dbColumnName, count);
        }
    }

    public static ExpectedDataUtil getExpectedData() {
        return INSTANCE;
    }

    public Map<JavadocMatchType, Integer> getExpectedTypes() {
        return Collections.unmodifiableMap(EXPECTED_TYPES);
    }

    public Map<String, Integer> getExpectedDataBaseTypes() {
        return Collections.unmodifiableMap(EXPECTED_DATABASE_TYPES);
    }

    public int getExpectedEntryCount() {
        return EXPECTED_ENTRY_COUNT;
    }
}
