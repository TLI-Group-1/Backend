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


// This annotation allows us to use a non-static BeforeAll/AfterAll methods (TODO: check if ok)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SvcUserLoginTest {
    private static final String dbName = "autodirect";
    private final String testUserId = "SvcUserLoginTest.test_user.700";
    private TableUsersInterface tableUser;
    private SvcUserLogin svcUserLogin;

    @Test
    void testSearchCarsAllNull() {
        try {
            Map<String, Object> userMap = svcUserLogin.loginUser(testUserId);
            assert userMap.size() > 0;
            assert userMap.get("user_id").equals(testUserId);
            assert tableUser.userExists(testUserId);
        } catch (SQLException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @BeforeAll
    public void setUpAll() {
        try {
            this.tableUser = new TableUsers(dbName);
            BankApiInterface bankApi = new BankApi();
            this.svcUserLogin = new SvcUserLogin(tableUser, bankApi);

            // testUserId user will be created in tests, ensure it doesn't exist yet
            if (tableUser.userExists(testUserId)) {
                tableUser.removeUserByID(testUserId);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void tearDownEach() {
        try {
            if (tableUser.userExists(testUserId)) {
                tableUser.removeUserByID(testUserId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
