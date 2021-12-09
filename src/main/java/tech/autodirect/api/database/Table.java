package tech.autodirect.api.database;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.sql.*;
import java.util.*;

public class Table {
    /**
     * Convert a JDBC ResultSet to a List of Maps (where each Map is a single entry in the ResultSet).
     * NOTE: Do not execute rs.next() BEFORE calling this method on rs (unless you have good reason to).
     *
     * @param rs A ResultSet containing the resulting entries of a JDBC query to the database server.
     * @return A List of Maps where each Map is a single entry in the JDBC database query result.
     */
    public List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        List<Map<String, Object>> list = new ArrayList<>(50);

        while (rs.next()) {
            Map<String, Object> row = new HashMap<>(columns);
            for (int i = 1; i <= columns; ++i) {
                row.put(md.getColumnName(i), rs.getObject(i));
            }
            list.add(row);
        }
        return list;
    }

    /**
     * Get an entry with id from the database using dbConn, schemaName, and tableName.
     *
     * entName is the name of the entity (like "user" or "car").
     */
    public List<Map<String, Object>> getAllEntries(
            String schemaName,
            String tableName,
            Connection dbConn
    ) throws SQLException {
        // Construct and execute a prepared SQL statement selecting all entries
        PreparedStatement stmt = dbConn.prepareStatement(
                "SELECT * FROM " + schemaName + "." + tableName
        );
        ResultSet rs = stmt.executeQuery();
        List<Map<String, Object>> maps = resultSetToList(rs);
        stmt.close();
        return maps;
    }

    /**
     * Get an entry with id from the database using dbConn, schemaName, and tableName.
     *
     * entName is the name of the entity (like "user" or "car").
     */
    public Map<String, Object> getEntryById(
            Object id,
            String schemaName,
            String tableName,
            Connection dbConn,
            String entName
    ) throws SQLException, ResponseStatusException {
        if (!checkEntryExists(id, schemaName, tableName, dbConn, entName)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, entName + " not found"
            );
        }

        // construct a prepared SQL statement selecting the specified entry
        PreparedStatement stmt = dbConn.prepareStatement(
                "SELECT * FROM " + schemaName + "." + tableName + " WHERE " + entName + "_id = ?;"
        );
        stmt.setObject(1, id);

        ResultSet rs = stmt.executeQuery();
        List<Map<String, Object>> rsList = resultSetToList(rs);
        stmt.close();
        if (rsList.size() == 0) {
            return Collections.emptyMap();
        } else {
            return rsList.get(0);
        }
    }

    /**
     * Remove an entry with id from the database using dbConn, schemaName, and tableName.
     *
     * entName is the name of the entity (like "user" or "car").
     */
    public void removeEntryById(
            Object id,
            String schemaName,
            String tableName,
            Connection dbConn,
            String entName
    ) throws SQLException, ResponseStatusException {
        if (!checkEntryExists(id, schemaName, tableName, dbConn, entName)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "user not found"
            );
        }

        // construct a prepared SQL statement selecting the specified entry
        PreparedStatement stmt = dbConn.prepareStatement(
                "DELETE FROM " + schemaName + "." + tableName + " WHERE " + entName + "_id = ?;"
        );
        stmt.setObject(1, id);

        // execute the above SQL statement
        stmt.executeUpdate();
        stmt.close();
    }

    /**
     * Remove all entries in the database table given by dbConn, schemaName, and tableName.
     */
    public void removeAllEntries(
            String schemaName,
            String tableName,
            Connection dbConn
    ) throws SQLException {
        PreparedStatement stmt = dbConn.prepareStatement(
                "DELETE FROM " + schemaName + "." + tableName + ";"
        );
        stmt.executeUpdate();
        stmt.close();
    }

    /**
     * Check an entry with id exists in the database using dbConn, schemaName, and tableName.
     *
     * entName is the name of the entity (like "user" or "car").
     */
    public boolean checkEntryExists(
            Object id,
            String schemaName,
            String tableName,
            Connection dbConn,
            String entName
    ) throws SQLException {
        // construct a prepared SQL statement selecting the specified entry
        PreparedStatement stmt = dbConn.prepareStatement(
                "SELECT 1 FROM " + schemaName + "." + tableName + " WHERE " + entName + "_id = ?;"
        );
        stmt.setObject(1, id);

        // execute the above SQL statement and check whether the entry exists
        ResultSet rs = stmt.executeQuery();
        boolean userCount = resultSetToList(rs).size() > 0;
        stmt.close();
        return userCount;
    }

    /**
     * Update the column of an entry given by the params.
     *
     * entName is the name of the entity (like "user" or "car").
     */
    public void updateEntryColumn(
            Object id,
            String schemaName,
            String tableName,
            Connection dbConn,
            String entName,
            String columnName,
            Object newValue
    ) throws SQLException {
        // construct a prepared SQL statement selecting the specified entry
        PreparedStatement stmt = dbConn.prepareStatement(
                "UPDATE " + schemaName + "." + tableName +
                " SET " + columnName + " = ?" +
                " WHERE " + entName + "_id = ?;"
        );
        stmt.setObject(1, newValue);
        stmt.setObject(2, id);
        stmt.executeUpdate();
        stmt.close();
    }
}
