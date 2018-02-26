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
package com.megatome.d2d.support;

import com.megatome.d2d.exception.BuilderException;
import com.megatome.d2d.support.javadoc.ExpectedJavaDocDataUtil;
import com.megatome.d2d.support.javadoc.JavadocSupport;
import com.megatome.d2d.support.jsdoc.ExpectedJSDocDataUtil;
import com.megatome.d2d.support.jsdoc.JSDocSupport;
import com.megatome.d2d.util.IndexData;
import com.megatome.d2d.util.SearchIndexValue;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.io.FileUtils.getFile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertTrue;

public class DBSupportTest {
    private static final File javadocLocation = getFile(System.getProperty("d2d-sample-javadoc"));
    private static final File jsdocLocation = getFile(System.getProperty("d2d-sample-jsdoc"));

    private static final String QUERY = "SELECT COUNT(*) FROM searchIndex WHERE type = ?";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testCreateIndexDBJavadoc() throws Exception {
        buildAndVerifyIndexDB(new JavadocSupport(), javadocLocation, ExpectedJavaDocDataUtil.getExpectedData());
    }

    @Test
    public void testCreateIndexDBJSDoc() throws Exception {
        buildAndVerifyIndexDB(new JSDocSupport(), jsdocLocation, ExpectedJSDocDataUtil.getExpectedData());
    }

    @Test(expected = BuilderException.class)
    public void testCreateIndexDBBadPath() throws Exception {
        final List<SearchIndexValue> indexValues = new ArrayList<>();
        final String docFileRoot = FilenameUtils.concat(temporaryFolder.getRoot().getPath(), "Foo");
        final String dbDirName = DocSetSupport.getDBDir(docFileRoot);
        final File dbDir = getFile(dbDirName);
        FileUtils.forceMkdir(dbDir);
        DBSupport.createIndex(indexValues, dbDirName + "FOO");
    }

    private void buildAndVerifyIndexDB(DocSetParserInterface implementation, File docLocation, ExpectedDataUtil expectedDataUtil) throws Exception {
        assertThat(implementation, notNullValue());

        final IndexData indexData = implementation.findIndexFile(docLocation);
        final List<SearchIndexValue> indexValues = implementation.findSearchIndexValues(indexData.getFilesToIndex());
        final String docFileRoot = FilenameUtils.concat(temporaryFolder.getRoot().getPath(), "Foo");
        final String dbDirName = DocSetSupport.getDBDir(docFileRoot);
        final File dbDir = getFile(dbDirName);
        FileUtils.forceMkdir(dbDir);
        DBSupport.createIndex(indexValues, dbDirName);
        final File dbFile = getFile(dbDir, "docSet.dsidx");
        assertTrue("DB file does not exist", dbFile.exists());

        final Map<String, Integer> expectedTypes = expectedDataUtil.getExpectedDataBaseTypes();
        try (final Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile); final PreparedStatement stmt = connection.prepareStatement(QUERY)) {

            for (Map.Entry<String, Integer> expectedEntry : expectedTypes.entrySet()) {
                stmt.setString(1, expectedEntry.getKey());
                try (final ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        final int count = rs.getInt(1);
                        System.out.println("Count: " + count + " -> Expected Count: " + expectedEntry.getValue());
                        assertThat(expectedEntry.getValue(), is(count));
                    }
                }
            }
        }
    }
}
