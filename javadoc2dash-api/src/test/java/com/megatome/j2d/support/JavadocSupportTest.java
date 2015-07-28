package com.megatome.j2d.support;

import com.megatome.j2d.exception.BuilderException;
import com.megatome.j2d.util.IndexData;
import com.megatome.j2d.util.SearchIndexValue;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.megatome.j2d.support.ExpectedDataUtil.getExpectedData;
import static org.apache.commons.io.FileUtils.getFile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;

public class JavadocSupportTest {
    private static final File resourcesRoot = getFile("src", "test", "resources");
    private static final File regularJavadoc = getFile(System.getProperty("j2d-sample-javadoc"));
    private static final File splitJavadoc = getFile(System.getProperty("j2d-sample-javadoc-split"));
    private static final String NOT_JAVADOC_DIR = "not-javadoc";

    @Test(expected = BuilderException.class)
    public void testMissingJavadocDir() throws Exception {
        JavadocSupport.findIndexFile(getFile(resourcesRoot, "FOO"));
    }

    @Test(expected = BuilderException.class)
    public void testJavadocDirIsFile() throws Exception {
        JavadocSupport.findIndexFile(getFile(regularJavadoc, "index.html"));
    }

    @Test(expected = BuilderException.class)
    public void testNonJavadocDir() throws Exception {
        JavadocSupport.findIndexFile(getFile(resourcesRoot, NOT_JAVADOC_DIR));
    }

    @Test
    public void testBuildIndexDataNonSplit() throws Exception {
        verifyFoundIndexValues(getAndVerifyIndexFiles(1, regularJavadoc));
    }

    @Test
    public void testBuildIndexDataSplit() throws Exception {
        verifyFoundIndexValues(getAndVerifyIndexFiles(6, splitJavadoc));
    }

    private IndexData getAndVerifyIndexFiles(int expectedFileCount, File javadocDir) throws Exception {
        final IndexData indexData = JavadocSupport.findIndexFile(javadocDir);
        assertNotNull(indexData);
        final String indexFile = indexData.getDocsetIndexFile();
        assertNotNull(indexFile);
        assertThat("overview-summary.html", is(indexFile));
        final List<File> files = indexData.getFilesToIndex();
        assertNotNull(files);
        assertThat(expectedFileCount, is(files.size()));
        return indexData;
    }

    private void verifyFoundIndexValues(final IndexData indexData) throws Exception {
        final List<SearchIndexValue> indexValues = JavadocSupport.findSearchIndexValues(indexData.getFilesToIndex());
        assertNotNull(indexValues);
        assertThat(indexValues.size(), is(getExpectedData().getExpectedEntryCount()));
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

        final Map<MatchType, Integer> expectedTypes = getExpectedData().getExpectedTypes();
        assertThat(valueMap.size(), is(expectedTypes.keySet().size()));
        for (final Map.Entry<MatchType,Integer> expectedType: expectedTypes.entrySet()) {
            assertThat(valueMap, hasKey(expectedType.getKey()));
            final List<String> namesForType = valueMap.get(expectedType.getKey());
            assertNotNull(namesForType);
            assertThat("Wrong count for " + expectedType, namesForType.size(), is(expectedType.getValue()));
        }
    }
}


