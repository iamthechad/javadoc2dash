package com.megatome.j2d.support;

import com.megatome.j2d.exception.BuilderException;
import com.megatome.j2d.util.IndexData;
import com.megatome.j2d.util.SearchIndexValue;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.*;

import static org.apache.commons.io.FileUtils.getFile;
import static org.junit.Assert.*;

public class JavadocSupportTest {
    private static final File resourcesRoot = getFile("src", "test", "resources");
    private static final String JAVADOC_DIR = "javadoc";
    private static final String JAVADOC_SPLIT_DIR = "javadoc-split";
    private static final String NOT_JAVADOC_DIR = "not-javadoc";
    private static final Map<String, Integer> expectedTypes = new HashMap<>();

    @BeforeClass
    public static void beforeClass() {
        expectedTypes.put("Class", 8);
        expectedTypes.put("Package", 4);
        expectedTypes.put("Exception", 1);
    }

    @Test
    public void testFindIndexFileNonSplit() throws Exception {
        final IndexData indexData = getAndVerifyIndexFiles(1, JAVADOC_DIR);
        final File f = indexData.getFilesToIndex().get(0);
        assertEquals("index-all.html", f.getName());
    }

    @Test
    public void testFindIndexFilesSplit() throws Exception {
        getAndVerifyIndexFiles(15, JAVADOC_SPLIT_DIR);
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
        verifyFoundIndexValues(getAndVerifyIndexFiles(15, JAVADOC_SPLIT_DIR));
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
        assertEquals(13, indexValues.size());
        final Map<String, Set<String>> valueMap = new HashMap<>();
        for (final SearchIndexValue value: indexValues) {
            Set<String> nameSet = valueMap.get(value.getType());
            if (nameSet == null) {
                nameSet = new HashSet<>();
            }
            assertFalse(nameSet.contains(value.getName()));
            nameSet.add(value.getName());
            valueMap.put(value.getType(), nameSet);
        }

        assertEquals(3, valueMap.size());
        for (final Map.Entry<String,Integer> expectedType: expectedTypes.entrySet()) {
            assertTrue(valueMap.containsKey(expectedType.getKey()));
            final Set<String> namesForType = valueMap.get(expectedType.getKey());
            assertNotNull(namesForType);
            assertEquals(expectedType.getValue().intValue(), namesForType.size());
        }
    }
}


