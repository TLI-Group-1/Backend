package tech.autodirect.api.database;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import tech.autodirect.api.interfaces.TableUsersInterface;

import java.sql.SQLException;

// This annotation allows us to use a non-static BeforeAll/AfterAll methods (TODO: check if ok)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TableUsersTest {
    private static final String dbName = "testing";

    private TableUsersInterface tableUsers;

    // Note: throughout this test class, we don't assume that testUserId exists in the users table.
    // We don't need to assume this since we never actually access this user in the users table.
    // We only use this userId to create offers (to help name the offers table).
    private final String testUserId = "TableUsersTests_test_user";


    @Test
    void addUser() {
    }

    @Test
    void getUserByID() {
    }

    @Test
    void removeUserByID() {
    }

    @Test
    void userExists() {
    }
    @BeforeEach
    public void setUpEach() {
        try {
            // Drop testUserId's offers table before each test.
            // This is especially important for tests which test the creation new tables
            // (no point in testing the creation of a new table if it already exists).
            TableUsers tableUsers = new TableUsers(dbName);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public void tearDownAll() {
        try {
            tableUsers = new TableUsers(dbName);
            tableUsers.removeUserByID(testUserId);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}