package com.megatome.j2d.support;

import com.megatome.j2d.exception.BuilderException;
import com.megatome.j2d.util.IndexData;
import com.megatome.j2d.util.SearchIndexValue;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

import static org.apache.commons.io.FileUtils.getFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DBSupportTest {
    private static final File resourcesRoot = getFile("src", "test", "resources");
    private static final String JAVADOC_DIR = "javadoc";

    private static final String QUERY = "SELECT COUNT(*) FROM searchIndex WHERE type = ?";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static final Map<String, Integer> expectedTypes = new HashMap<>();

    @BeforeClass
    public static void beforeClass() {
        expectedTypes.put("Class", 8);
        expectedTypes.put("Package", 4);
        expectedTypes.put("Exception", 1);
    }

    @Test
    public void testCreateIndexDB() throws Exception {
        final IndexData indexData = JavadocSupport.findIndexFile(getFile(resourcesRoot, JAVADOC_DIR));
        final List<SearchIndexValue> indexValues = JavadocSupport.findSearchIndexValues(indexData.getFilesToIndex());
        final String docFileRoot = FilenameUtils.concat(temporaryFolder.getRoot().getPath(), "Foo");
        final String dbDirName = DocSetSupport.getDBDir(docFileRoot);
        final File dbDir = getFile(dbDirName);
        FileUtils.forceMkdir(dbDir);
        DBSupport.createIndex(indexValues, dbDirName);
        final File dbFile = getFile(dbDir, "docSet.dsidx");
        assertTrue(dbFile.exists());

        try (final Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
             final PreparedStatement stmt = connection.prepareStatement(QUERY)){

            for (Map.Entry<String, Integer> expectedEntry : expectedTypes.entrySet()) {
                stmt.setString(1, expectedEntry.getKey());
                try (final ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        final int count = rs.getInt(1);
                        assertEquals(expectedEntry.getValue().intValue(), count);
                    }
                }
            }
        }
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
}
