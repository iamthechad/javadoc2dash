package com.megatome.j2d.support;

import com.megatome.j2d.util.SearchIndexValue;
import com.megatome.j2d.exception.BuilderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.*;
import java.util.List;

import static org.apache.commons.io.FileUtils.getFile;

public final class DBSupport {
    private static final Logger LOG = LoggerFactory.getLogger(DBSupport.class);

    private static final String DB_FILE = "docSet.dsidx";

    private DBSupport() {}

    private static final String CREATE_INDEX_SQL = "CREATE TABLE searchIndex(id INTEGER PRIMARY KEY, name TEXT, type TEXT, path TEXT)";
    private static final String INSERT_INDEX_SQL = "INSERT INTO searchIndex(name, type, path) VALUES (?, ?, ?)";

    public static void createIndex(List<SearchIndexValue> indexValues, File dbFileDir) throws BuilderException {
        final File dbFile = getFile(dbFileDir, DB_FILE);
        // Create DB file
        try (final Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
             final Statement stmt = connection.createStatement()){
            stmt.execute(CREATE_INDEX_SQL);
            // Update DB
            try (final PreparedStatement pst = connection.prepareStatement(INSERT_INDEX_SQL)) {
                for (final SearchIndexValue value : indexValues) {
                    pst.setString(1, value.getName());
                    pst.setString(2, value.getType());
                    pst.setString(3, value.getPath());
                    pst.execute();
                }
            }
        } catch (SQLException e) {
            throw new BuilderException("Error writing to SQLite DB", e);
        }
        LOG.info("Created the SQLite search index");
    }
}
