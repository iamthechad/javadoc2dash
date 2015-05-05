package com.megatome.j2d;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class DocsetCreatorTest {
    private static final File CURRENT_DIR = FileUtils.getFile(".");
    private static final Map<String, Object> expectedValues = new HashMap<>();
    private static final String DOCSET_NAME = "DOCSET_NAME";
    private static final String DISPLAY_NAME = "DISPLAY_NAME";
    private static final String JAVADOC_DIR = "JAVADOC_DIR";
    private static final String KEYWORD = "KEYWORD";
    private static final String OUTPUT_DIR = "OUTPUT_DIR";
    private static final String ICON_FILE = "ICON_FILE";

    @Before
    public void setup() {
        expectedValues.clear();
        expectedValues.put(DOCSET_NAME, "Foo");
        expectedValues.put(JAVADOC_DIR, CURRENT_DIR);
        expectedValues.put(DISPLAY_NAME, "Foo");
        expectedValues.put(KEYWORD, "Foo");
        expectedValues.put(OUTPUT_DIR, CURRENT_DIR);
        expectedValues.put(ICON_FILE, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullName() {
        final DocsetCreator.Builder builder = new DocsetCreator.Builder(null, CURRENT_DIR);
        builder.build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateEmptyName() {
        final DocsetCreator.Builder builder = new DocsetCreator.Builder("", CURRENT_DIR);
        builder.build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateMissingJavadocRoot() {
        final DocsetCreator.Builder builder = new DocsetCreator.Builder("Foo", null);
        builder.build();
    }

    @Test
    public void testCreateMinimalBuilder() throws Exception {
        final DocsetCreator.Builder builder = new DocsetCreator.Builder("Foo", CURRENT_DIR);
        verifyCreatorValues(builder.build());
    }

    @Test
    public void testBuildWithDisplayName() throws Exception {
        final DocsetCreator.Builder builder = new DocsetCreator.Builder("Foo", CURRENT_DIR);
        builder.displayName(null);
        verifyCreatorValues(builder.build());

        builder.displayName("");
        verifyCreatorValues(builder.build());

        builder.displayName("Bar");
        expectedValues.put(DISPLAY_NAME, "Bar");
        verifyCreatorValues(builder.build());
    }

    @Test
    public void testBuildWithKeyword() throws Exception {
        final DocsetCreator.Builder builder = new DocsetCreator.Builder("Foo", CURRENT_DIR);
        builder.keyword(null);
        verifyCreatorValues(builder.build());

        builder.keyword("");
        verifyCreatorValues(builder.build());

        builder.keyword("Bar");
        expectedValues.put(KEYWORD, "Bar");
        verifyCreatorValues(builder.build());
    }

    @Test
    public void testBuildWithOutputDirectory() throws Exception {
        final DocsetCreator.Builder builder = new DocsetCreator.Builder("Foo", CURRENT_DIR);
        builder.outputDirectory(null);
        verifyCreatorValues(builder.build());

        final File newDir = FileUtils.getFile("src");
        builder.outputDirectory(newDir);
        expectedValues.put(OUTPUT_DIR, newDir);
        verifyCreatorValues(builder.build());
    }

    @Test
    public void testBuildWithIconFile() throws Exception {
        final DocsetCreator.Builder builder = new DocsetCreator.Builder("Foo", CURRENT_DIR);
        builder.iconFile(null);
        verifyCreatorValues(builder.build());

        final File newDir = FileUtils.getFile("src");
        builder.iconFile(newDir);
        expectedValues.put(ICON_FILE, newDir);
        verifyCreatorValues(builder.build());
    }

    private void verifyCreatorValues(final DocsetCreator creator) {
        verifyCreatorValues(expectedValues, creator);
    }

    private void verifyCreatorValues(final Map<String, Object> expectedValueMap, final DocsetCreator creator) {
        assertNotNull(creator);
        assertEquals(expectedValueMap.get(DOCSET_NAME), creator.getDocsetName());
        assertEquals(expectedValueMap.get(DISPLAY_NAME), creator.getDisplayName());
        assertEquals(expectedValueMap.get(JAVADOC_DIR), creator.getJavadocRoot());
        assertEquals(expectedValueMap.get(KEYWORD), creator.getKeyword());
        assertEquals(expectedValueMap.get(OUTPUT_DIR), creator.getOutputDirectory());
        assertEquals(expectedValueMap.get(ICON_FILE), creator.getIconFilePath());
    }
}
