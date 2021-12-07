package tech.autodirect.api.database;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
