package tech.autodirect.api.database;

import org.junit.jupiter.api.*;

import java.sql.SQLException;

// This annotation allows us to use a non-static BeforeAll/AfterAll methods (TODO: check if ok)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class TableUsersTest {

    private static final String dbName = "testing";

    // Note: throughout this test class, we don't assume that testUserId exists in the users table.
    // We don't need to assume this since we never actually access this user in the users table.
    // We only use this userId to create offers (to help name the offers table).
    // TODO (Nada): read this and make sure you understand. Don't delete the comment.
    private final String testUserId = "TableOffersTests_test_user";


    /**
     * Create a new offers table, then verify it exists.
     */
    @Test
    void testNewTable() {
        try {
            // Create new table for testUserId. setUpEach() ensures table doesn't already exist.
            TableUsers table = new TableUsers(dbName);
            String tableName = table.newTable(testUserId);

            // check if the created table exists under the correct name (both should work)
            assert table.checkTableExists();
            assert table.checkTableExists(tableName);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            assert false;
        }
    }
}
