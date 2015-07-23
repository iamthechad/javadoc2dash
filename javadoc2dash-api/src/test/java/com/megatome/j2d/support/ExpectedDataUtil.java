package com.megatome.j2d.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ExpectedDataUtil {
    private final Map<MatchType, Integer> EXPECTED_TYPES = new HashMap<>();
    private int EXPECTED_ENTRY_COUNT = 0;

    private static final ExpectedDataUtil INSTANCE = new ExpectedDataUtil();

    private ExpectedDataUtil() {
        EXPECTED_TYPES.put(MatchType.CLASS, 1);
        EXPECTED_TYPES.put(MatchType.INTERFACE, 1);
        EXPECTED_TYPES.put(MatchType.CONSTRUCTOR, 5);
        EXPECTED_TYPES.put(MatchType.METHOD, 5);
        EXPECTED_TYPES.put(MatchType.PACKAGE, 5);
        EXPECTED_TYPES.put(MatchType.EXCEPTION, 1);
        EXPECTED_TYPES.put(MatchType.ERROR, 1);
        EXPECTED_TYPES.put(MatchType.FIELD, 1);
        EXPECTED_TYPES.put(MatchType.ENUM, 1);

        for (final Integer count : EXPECTED_TYPES.values()) {
            EXPECTED_ENTRY_COUNT += count;
        }
    }

    public static ExpectedDataUtil getExpectedData() {
        return INSTANCE;
    }

    public Map<MatchType, Integer> getExpectedTypes() {
        return Collections.unmodifiableMap(EXPECTED_TYPES);
    }

    public int getExpectedEntryCount() {
        return EXPECTED_ENTRY_COUNT;
    }
}
