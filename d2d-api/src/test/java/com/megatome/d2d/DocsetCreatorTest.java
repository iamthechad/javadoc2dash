/*
 * Copyright 2018 Megatome Technologies, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.megatome.d2d;

import com.megatome.d2d.support.BuilderImplementation;
import com.megatome.d2d.support.javadoc.JavadocSupport;
import com.megatome.d2d.support.jsdoc.JSDocSupport;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DocsetCreatorTest {
    private static final File CURRENT_DIR = FileUtils.getFile(".");
    private static final Map<String, Object> expectedValues = new HashMap<>();
    private static final String DOCSET_NAME = "DOCSET_NAME";
    private static final String DISPLAY_NAME = "DISPLAY_NAME";
    private static final String JAVADOC_DIR = "JAVADOC_DIR";
    private static final String KEYWORD = "KEYWORD";
    private static final String OUTPUT_DIR = "OUTPUT_DIR";
    private static final String ICON_FILE = "ICON_FILE";
    private static final String IMPLEMENTATION_TYPE = "IMPLEMENTATION_TYPE";

    @Before
    public void setup() {
        expectedValues.clear();
        expectedValues.put(DOCSET_NAME, "Foo");
        expectedValues.put(JAVADOC_DIR, CURRENT_DIR);
        expectedValues.put(DISPLAY_NAME, "Foo");
        expectedValues.put(KEYWORD, "Foo");
        expectedValues.put(OUTPUT_DIR, CURRENT_DIR);
        expectedValues.put(ICON_FILE, null);
        expectedValues.put(IMPLEMENTATION_TYPE, JavadocSupport.class);
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
    public void testCreateMinimalBuilder() {
        final DocsetCreator.Builder builder = new DocsetCreator.Builder("Foo", CURRENT_DIR);
        verifyCreatorValues(builder.build());
    }

    @Test
    public void testBuildWithDisplayName() {
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
    public void testBuildWithKeyword() {
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
    public void testBuildWithOutputDirectory() {
        final DocsetCreator.Builder builder = new DocsetCreator.Builder("Foo", CURRENT_DIR);
        builder.outputDirectory(null);
        verifyCreatorValues(builder.build());

        final File newDir = FileUtils.getFile("src");
        builder.outputDirectory(newDir);
        expectedValues.put(OUTPUT_DIR, newDir);
        verifyCreatorValues(builder.build());
    }

    @Test
    public void testBuildWithIconFile() {
        final DocsetCreator.Builder builder = new DocsetCreator.Builder("Foo", CURRENT_DIR);
        builder.iconFile(null);
        verifyCreatorValues(builder.build());

        final File newDir = FileUtils.getFile("src");
        builder.iconFile(newDir);
        expectedValues.put(ICON_FILE, newDir);
        verifyCreatorValues(builder.build());
    }

    @Test
    public void testBuildWithImplementationType() {
        final DocsetCreator.Builder builder = new DocsetCreator.Builder("Foo", CURRENT_DIR);
        builder.implementation((String) null);
        expectedValues.put(IMPLEMENTATION_TYPE, JavadocSupport.class);
        verifyCreatorValues(builder.build());

        builder.implementation(BuilderImplementation.JSDOC);
        expectedValues.put(IMPLEMENTATION_TYPE, JSDocSupport.class);
        verifyCreatorValues(builder.build());

        builder.implementation("javadoc");
        expectedValues.put(IMPLEMENTATION_TYPE, JavadocSupport.class);
        verifyCreatorValues(builder.build());

        builder.implementation(new JSDocSupport());
        expectedValues.put(IMPLEMENTATION_TYPE, JSDocSupport.class);
        verifyCreatorValues(builder.build());
    }

    private void verifyCreatorValues(final DocsetCreator creator) {
        verifyCreatorValues(expectedValues, creator);
    }

    private void verifyCreatorValues(final Map<String, Object> expectedValueMap, final DocsetCreator creator) {
        assertNotNull(creator);
        assertEquals(expectedValueMap.get(DOCSET_NAME), creator.getDocsetName());
        assertEquals(expectedValueMap.get(DISPLAY_NAME), creator.getDisplayName());
        assertEquals(expectedValueMap.get(JAVADOC_DIR), creator.getDocRoot());
        assertEquals(expectedValueMap.get(KEYWORD), creator.getKeyword());
        assertEquals(expectedValueMap.get(OUTPUT_DIR), creator.getOutputDirectory());
        assertEquals(expectedValueMap.get(ICON_FILE), creator.getIconFilePath());
        assertEquals(creator.getImplementation().getClass(), expectedValueMap.get(IMPLEMENTATION_TYPE));
    }
}
