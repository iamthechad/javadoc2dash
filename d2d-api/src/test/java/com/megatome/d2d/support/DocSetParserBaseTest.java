package com.megatome.d2d.support;

import com.megatome.d2d.util.IndexData;
import com.megatome.d2d.util.SearchIndexValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;

public class DocSetParserBaseTest {
    protected void verifyFoundIndexValues(final DocSetParserInterface parser, final IndexData indexData, final ExpectedDataUtil expectedData) throws Exception {
        final List<SearchIndexValue> indexValues = parser.findSearchIndexValues(indexData.getFilesToIndex());
        assertNotNull(indexValues);
        assertThat(indexValues.size(), is(expectedData.getExpectedEntryCount()));
        final Map<MatchTypeInterface, List<String>> valueMap = new HashMap<>();
        for (final SearchIndexValue value : indexValues) {
            List<String> nameSet = valueMap.get(value.getType());
            if (nameSet == null) {
                nameSet = new ArrayList<>();
            }
            assertThat(nameSet, not(hasItem(value.getName())));
            nameSet.add(value.getName());
            valueMap.put(value.getType(), nameSet);
        }

        final Map<MatchTypeInterface, Integer> expectedTypes = expectedData.getExpectedTypes();
        assertThat(valueMap.size(), is(expectedTypes.keySet().size()));
        for (final Map.Entry<MatchTypeInterface, Integer> expectedType : expectedTypes.entrySet()) {
            assertThat(valueMap, hasKey(expectedType.getKey()));
            final List<String> namesForType = valueMap.get(expectedType.getKey());
            assertNotNull(namesForType);
            assertThat("Wrong count for " + expectedType, namesForType.size(), is(expectedType.getValue()));
        }
    }
}
