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
package com.megatome.d2d.support.jsdoc;

import com.megatome.d2d.exception.BuilderException;
import com.megatome.d2d.support.DocSetParserBaseTest;
import com.megatome.d2d.util.IndexData;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.apache.commons.io.FileUtils.getFile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;

public class JSDocSupportTest extends DocSetParserBaseTest {
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
        verifyFoundIndexValues(new JSDocSupport(), getAndVerifyIndexFiles(1, regularJSDoc), ExpectedJSDocDataUtil.getExpectedData());
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
}
