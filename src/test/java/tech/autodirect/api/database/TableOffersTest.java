package tech.autodirect.api.database;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TableOffersTest {
    private static final String db_name = "testing";

    /**
     * Create a new offer_ table in the offers schema, then check if it exists.
     */
    @Test void newTable() {
        try {
            // create a new table for "test_user"
            String username = "test_user";
            TableOffers table = new TableOffers(db_name);
            table.newTable(username);

            // check if the created table exists under the correct name
            Statement stmt = table.db_conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT EXISTS (" +
                    "SELECT 1 " +
                    "FROM   information_schema.tables " +
                    "WHERE  table_schema = 'offers'" +
                    "   and table_name=" + "'offer_" + username + "'" +
                ");"
            );
            rs.next();
            // this result should return true if the specified table exists
            boolean result = rs.getBoolean("exists");
            stmt.close();
            assert result;
        }
        catch (SQLException e) {
            e.printStackTrace();
            assert false;
        }
    }


    /**
     * Drop the entire "offers" schema from the testing database
     */
    @AfterAll public static void cleanupOffersTables() throws SQLException {
//        Connection db_conn = Conn.getConn(db_name);
//        Statement stmt = db_conn.createStatement();
//        stmt.executeUpdate("DROP SCHEMA offers CASCADE;");
//        stmt.close();
    }
}
