package com.megatome.d2d.support.jsdoc;

import static com.megatome.d2d.support.jsdoc.ExpectedJSDocDataUtil.getExpectedData;
import static org.apache.commons.io.FileUtils.getFile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.megatome.d2d.exception.BuilderException;
import com.megatome.d2d.util.IndexData;
import com.megatome.d2d.util.SearchIndexValue;

public class JSDocSupportTest {
    private static final File resourcesRoot = getFile("src", "test", "resources");
    private static final File regularJSDoc = getFile(System.getProperty("d2d-sample-jsdoc"));
    private static final String NOT_JAVADOC_DIR = "not-javadoc";

    @Test(expected = BuilderException.class)
    public void testMissingJSDocDir() throws Exception {
        (new JSDocSupport()).findIndexFile(getFile(resourcesRoot, "FOO"));
    }

    @Test(expected = BuilderException.class)
    public void testJSDocDirIsFile() throws Exception {
        (new JSDocSupport()).findIndexFile(getFile(regularJSDoc, "index.json"));
    }

    @Test(expected = BuilderException.class)
    public void testNonJSDocDir() throws Exception {
        (new JSDocSupport()).findIndexFile(getFile(resourcesRoot, NOT_JAVADOC_DIR));
    }

    @Test
    public void testIndexFilesFound() throws Exception {
        verifyFoundIndexValues( getAndVerifyIndexFiles( 1, regularJSDoc ) );
    }

    private IndexData getAndVerifyIndexFiles(int expectedFileCount, File javadocDir) throws Exception {
        final IndexData indexData = (new JSDocSupport()).findIndexFile(javadocDir);
        assertNotNull(indexData);
        final String indexFile = indexData.getDocsetIndexFile();
        assertNotNull(indexFile);
        assertThat("index.html", is(indexFile));
        final List<File> files = indexData.getFilesToIndex();
        assertNotNull(files);
        assertThat(expectedFileCount, is(files.size()));
        return indexData;
    }

    private void verifyFoundIndexValues(final IndexData indexData) throws Exception {
        final List<SearchIndexValue> indexValues = (new JSDocSupport()).findSearchIndexValues(indexData.getFilesToIndex());
        assertNotNull(indexValues);
        assertThat(indexValues.size(), is(getExpectedData().getExpectedEntryCount()));
        final Map<JSDocMatchType, List<String>> valueMap = new HashMap<>();
        for (final SearchIndexValue value: indexValues) {
            List<String> nameSet = valueMap.get(value.getType());
            if (nameSet == null) {
                nameSet = new ArrayList<>();
            }
            assertThat(nameSet, not(hasItem(value.getName())));
            nameSet.add(value.getName());
            valueMap.put((JSDocMatchType)value.getType(), nameSet);
        }

        final Map<JSDocMatchType, Integer> expectedTypes = getExpectedData().getExpectedTypes();
        assertThat(valueMap.size(), is(expectedTypes.keySet().size()));
        for (final Map.Entry<JSDocMatchType,Integer> expectedType: expectedTypes.entrySet()) {
            assertThat(valueMap, hasKey(expectedType.getKey()));
            final List<String> namesForType = valueMap.get(expectedType.getKey());
            assertNotNull(namesForType);
            assertThat("Wrong count for " + expectedType, namesForType.size(), is(expectedType.getValue()));
        }
    }
}


