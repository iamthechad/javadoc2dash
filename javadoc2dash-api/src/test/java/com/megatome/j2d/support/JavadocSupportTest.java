package com.megatome.j2d.support;

import com.megatome.j2d.exception.BuilderException;
import com.megatome.j2d.util.IndexData;
import com.megatome.j2d.util.SearchIndexValue;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.io.FileUtils.getFile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JavadocSupportTest {
    private static final File resourcesRoot = getFile("src", "test", "resources");
    private static final String JAVADOC_DIR = "javadoc";
    private static final String JAVADOC_SPLIT_DIR = "javadoc-split";
    private static final String NOT_JAVADOC_DIR = "not-javadoc";
    private static final Map<MatchType, Integer> expectedTypes = new HashMap<>();
    private static int expectedEntryCount = 0;

    @BeforeClass
    public static void beforeClass() {
        expectedTypes.put(MatchType.CLASS, 1);
        expectedTypes.put(MatchType.INTERFACE, 1);
        expectedTypes.put(MatchType.CONSTRUCTOR, 5);
        expectedTypes.put(MatchType.METHOD, 5);
        expectedTypes.put(MatchType.PACKAGE, 5);
        expectedTypes.put(MatchType.EXCEPTION, 1);
        expectedTypes.put(MatchType.ERROR, 1);
        expectedTypes.put(MatchType.FIELD, 1);
        expectedTypes.put(MatchType.ENUM, 1);

        for (final Integer count : expectedTypes.values()) {
            expectedEntryCount += count;
        }
    }

    @Test
    public void testFindIndexFileNonSplit() throws Exception {
        final IndexData indexData = getAndVerifyIndexFiles(1, JAVADOC_DIR);
        final File f = indexData.getFilesToIndex().get(0);
        assertEquals("index-all.html", f.getName());
    }

    @Test
    public void testFindIndexFilesSplit() throws Exception {
        getAndVerifyIndexFiles(6, JAVADOC_SPLIT_DIR);
    }

    @Test(expected = BuilderException.class)
    public void testMissingJavadocDir() throws Exception {
        JavadocSupport.findIndexFile(getFile(resourcesRoot, "FOO"));
    }

    @Test(expected = BuilderException.class)
    public void testJavadocDirIsFile() throws Exception {
        JavadocSupport.findIndexFile(getFile(resourcesRoot, JAVADOC_DIR, "index.html"));
    }

    @Test(expected = BuilderException.class)
    public void testNonJavadocDir() throws Exception {
        JavadocSupport.findIndexFile(getFile(resourcesRoot, NOT_JAVADOC_DIR));
    }

    @Test
    public void testBuildIndexDataNonSplit() throws Exception {
        verifyFoundIndexValues(getAndVerifyIndexFiles(1, JAVADOC_DIR));
    }

    @Test
    public void testBuildIndexDataSplit() throws Exception {
        verifyFoundIndexValues(getAndVerifyIndexFiles(6, JAVADOC_SPLIT_DIR));
    }

    private IndexData getAndVerifyIndexFiles(int expectedFileCount, String javadocPath) throws Exception {
        final IndexData indexData = JavadocSupport.findIndexFile(getFile(resourcesRoot, javadocPath));
        assertNotNull(indexData);
        final String indexFile = indexData.getDocsetIndexFile();
        assertNotNull(indexFile);
        assertEquals("overview-summary.html", indexFile);
        final List<File> files = indexData.getFilesToIndex();
        assertNotNull(files);
        assertEquals(expectedFileCount, files.size());
        return indexData;
    }

    private void verifyFoundIndexValues(final IndexData indexData) throws Exception {
        final List<SearchIndexValue> indexValues = JavadocSupport.findSearchIndexValues(indexData.getFilesToIndex());
        assertNotNull(indexValues);
        assertThat(indexValues.size(), is(expectedEntryCount));
        final Map<MatchType, List<String>> valueMap = new HashMap<>();
        for (final SearchIndexValue value: indexValues) {
            List<String> nameSet = valueMap.get(value.getType());
            if (nameSet == null) {
                nameSet = new ArrayList<>();
            }
            assertThat(nameSet, not(hasItem(value.getName())));
            nameSet.add(value.getName());
            valueMap.put(value.getType(), nameSet);
        }

        assertThat(valueMap.size(), is(expectedTypes.keySet().size()));
        for (final Map.Entry<MatchType,Integer> expectedType: expectedTypes.entrySet()) {
            assertThat(valueMap, hasKey(expectedType.getKey()));
            final List<String> namesForType = valueMap.get(expectedType.getKey());
            assertNotNull(namesForType);
            assertThat("Wrong count for " + expectedType, namesForType.size(), is(expectedType.getValue()));
        }
    }
}


