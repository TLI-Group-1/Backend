package tech.autodirect.api.database;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.web.server.ResponseStatusException;
import tech.autodirect.api.entities.EntUser;
import tech.autodirect.api.interfaces.TableOffersInterface;
import tech.autodirect.api.interfaces.TableUsersInterface;

import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

// This annotation allows us to use a non-static BeforeAll/AfterAll methods
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TableUsersTest {
    private static final String DB_NAME = "testing";
    private final String testUserId = "TableUsersTests_test_user";
    private TableUsersInterface tableUsers;

    /**
     * Tests addUser() using checkUserExists().
     */
    @Test
    void testAddUser() {
        try {
            tableUsers.addUser(testUserId, 1, 2, 3);
            assert tableUsers.checkUserExists(testUserId);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests getUserById().
     */
    @Test
    void testGetUserById() {
        try {
            tableUsers.addUser(testUserId, 1, 2, 3);

            // Get user entry and convert to use entity
            Map<String, Object> userMap = tableUsers.getUserById(testUserId);
            EntUser user = new EntUser();
            user.loadFromMap(userMap);

            assert user.getUserId().equals(testUserId);
            assert user.getCreditScore() == 1;
            assert user.getDownPayment() == 2;
            assert user.getBudgetMo() == 3;
            assert user.getOffersTable().equals(TableOffersInterface.createTableName(testUserId));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests checkUserExists() when it exists.
     */
    @Test
    void testCheckUserExistsWhenExists() {
        try {
            tableUsers.addUser(testUserId, 1, 2, 3);
            assert tableUsers.checkUserExists(testUserId);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests checkUserExists() when it does not exist.
     */
    @Test
    void testCheckUserExistsWhenNotExists() {
        try {
            assert !tableUsers.checkUserExists(testUserId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests removeUserById().
     */
    @Test
    void testRemoveUserByUserId() {
        try {
            tableUsers.addUser(testUserId, 1, 2, 3);
            assert tableUsers.checkUserExists(testUserId);
            tableUsers.removeUserById(testUserId);
            assert !tableUsers.checkUserExists(testUserId);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests updateUserColumn() when monthly budget is being updated
     */
    @Test
    void testUpdateUserColumnWhenUpdatingBudgetMo() {
        try {
            tableUsers.addUser(testUserId, 1, 2, 3);
            TableUsersInterface.UserColumns monthlyBudget = TableUsersInterface.UserColumns.BUDGET_MO;
            tableUsers.updateUserColumn(testUserId, monthlyBudget, 5);

            // Get user entry and convert to use entity
            Map<String, Object> userMap = tableUsers.getUserById(testUserId);
            EntUser user = new EntUser();
            user.loadFromMap(userMap);


            assert user.getUserId().equals(testUserId);
            assert user.getCreditScore() == 1;
            assert user.getDownPayment() == 2;
            assert user.getBudgetMo() == 5;
            assert user.getOffersTable().equals(TableOffersInterface.createTableName(testUserId));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests updateUserColumn() when down payment is being updated
     */
    @Test
    void testUpdateUserColumnWhenUpdatingDownPayment() {
        try {
            tableUsers.addUser(testUserId, 1, 2, 3);
            TableUsersInterface.UserColumns downPayment = TableUsersInterface.UserColumns.DOWN_PAYMENT;
            tableUsers.updateUserColumn(testUserId, downPayment, 5);

            // Get user entry and convert to use entity
            Map<String, Object> userMap = tableUsers.getUserById(testUserId);
            EntUser user = new EntUser();
            user.loadFromMap(userMap);


            assert user.getUserId().equals(testUserId);
            assert user.getCreditScore() == 1;
            assert user.getDownPayment() == 5;
            assert user.getBudgetMo() == 3;
            assert user.getOffersTable().equals(TableOffersInterface.createTableName(testUserId));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests that 404 error is raised when user does not exist.
     */
    @Test
    void test404Error() {
        try {
            tableUsers.getUserById(testUserId); // Should return 404 error
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ResponseStatusException e) {
            assert true;
            assert Objects.equals(e.getMessage(), "404 NOT_FOUND \"user not found\"");
        }
    }

    @BeforeEach
    public void setUpEach() {
        try {
            tableUsers = new TableUsers(DB_NAME);
            if (tableUsers.checkUserExists(testUserId)) {
                tableUsers.removeUserById(testUserId);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public void tearDownAll() {
        try {
            tableUsers = new TableUsers(DB_NAME);
            if (tableUsers.checkUserExists(testUserId)) {
                tableUsers.removeUserById(testUserId);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
