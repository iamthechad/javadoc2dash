package com.megatome.j2d;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class DocsetCreatorTest {

    @Test(expected = IllegalArgumentException.class)
    public void testCreateMissingName() {
        final DocsetCreator.Builder builder = new DocsetCreator.Builder(null, FileUtils.getFile("."));
        builder.build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateMissingJavadocRoot() {
        final DocsetCreator.Builder builder = new DocsetCreator.Builder("Foo", null);
        builder.build();
    }
}
