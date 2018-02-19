package com.megatome.j2d.support;

import static com.megatome.j2d.support.ExpectedDataUtil.getExpectedData;
import static org.apache.commons.io.FileUtils.getFile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.megatome.j2d.exception.BuilderException;
import com.megatome.j2d.util.IndexData;
import com.megatome.j2d.util.SearchIndexValue;

public class JavadocSupportTest {
    private static final File resourcesRoot = getFile("src", "test", "resources");
    private static final File regularJavadoc = getFile(System.getProperty("j2d-sample-javadoc"));
    private static final File splitJavadoc = getFile(System.getProperty("j2d-sample-javadoc-split"));
    private static final String NOT_JAVADOC_DIR = "not-javadoc";
    private static final String INDEX_ALL_BAD_TAG_HTML = "index-all-bad-tag.html";

    @Test(expected = BuilderException.class)
    public void testMissingJavadocDir() throws Exception {
        (new JavadocSupport()).findIndexFile(getFile(resourcesRoot, "FOO"));
    }

    @Test(expected = BuilderException.class)
    public void testJavadocDirIsFile() throws Exception {
        (new JavadocSupport()).findIndexFile(getFile(regularJavadoc, "index.html"));
    }

    @Test(expected = BuilderException.class)
    public void testNonJavadocDir() throws Exception {
        (new JavadocSupport()).findIndexFile(getFile(resourcesRoot, NOT_JAVADOC_DIR));
    }

    @Test
    public void testBuildIndexDataNonSplit() throws Exception {
        verifyFoundIndexValues(getAndVerifyIndexFiles(1, regularJavadoc));
    }

    @Test
    public void testBuildIndexDataSplit() throws Exception {
        verifyFoundIndexValues(getAndVerifyIndexFiles(6, splitJavadoc));
    }

    @Test
    public void testWarnsOfStrayTags() throws Exception {
        final URI uri = this.getClass().getResource(INDEX_ALL_BAD_TAG_HTML).toURI();
        final List<File> filesToIndex = Collections.singletonList(new File(uri));

        final ByteArrayOutputStream errStream = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errStream));
        try {
            (new JavadocSupport()).findSearchIndexValues(filesToIndex);
        }
        finally {
            System.setErr(null);
        }

        final String err = errStream.toString();

        assertThat(err, containsString("Something went wrong with parsing a link, possibly unescaped tags" +
                                       " in Javadoc. (Name: , Type: CONSTRUCTOR, Link: )"));
        assertThat(err, containsString("Most recently parsed value was: (Name: SampleClass, Type: CLASS,"
                                       + " Path: ./com/megatome/j2d/sample/clazz/SampleClass.html)"));
    }

    private IndexData getAndVerifyIndexFiles(int expectedFileCount, File javadocDir) throws Exception {
        final IndexData indexData = (new JavadocSupport()).findIndexFile(javadocDir);
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
        final List<SearchIndexValue> indexValues = (new JavadocSupport()).findSearchIndexValues(indexData.getFilesToIndex());
        assertNotNull(indexValues);
        assertThat(indexValues.size(), is(getExpectedData().getExpectedEntryCount()));
        final Map<MatchTypeInterface, List<String>> valueMap = new HashMap<>();
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


