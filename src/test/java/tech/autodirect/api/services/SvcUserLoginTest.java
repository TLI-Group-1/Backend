package tech.autodirect.api.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import tech.autodirect.api.database.TableUsers;
import tech.autodirect.api.interfaces.BankApiInterface;
import tech.autodirect.api.interfaces.TableUsersInterface;
import tech.autodirect.api.upstream.BankApi;

import java.sql.SQLException;
import java.util.Map;


// This annotation allows us to use a non-static BeforeAll/AfterAll methods
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SvcUserLoginTest {
    private static final String DB_NAME = "testing";
    private final String testUserId = "SvcUserLoginTest_test_user_700";
    private TableUsersInterface tableUser;
    private BankApiInterface bankApi;
    private SvcUserLogin svcUserLogin;

    @Test
    void testLoginUserNewUser() {
        try {
            Map<String, Object> userMap = svcUserLogin.loginUser(tableUser, bankApi, testUserId);
            assert userMap.size() > 0;
            assert userMap.get("user_id").equals(testUserId);
            assert tableUser.checkUserExists(testUserId);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    void testLoginUserExistingUser() {
        try {
            tableUser.addUser(testUserId, 700, 1000, 250);
            Map<String, Object> userMap = svcUserLogin.loginUser(tableUser, bankApi, testUserId);
            assert userMap.size() > 0;
            assert userMap.get("user_id").equals(testUserId);
            assert tableUser.checkUserExists(testUserId);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @BeforeAll
    public void setUpAll() {
        try {
            this.tableUser = new TableUsers(DB_NAME);
            this.bankApi = new BankApi();
            this.svcUserLogin = new SvcUserLogin();

            // testUserId user will be created in tests, ensure it doesn't exist yet
            if (tableUser.checkUserExists(testUserId)) {
                tableUser.removeUserById(testUserId);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void tearDownEach() {
        try {
            if (tableUser.checkUserExists(testUserId)) {
                tableUser.removeUserById(testUserId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
