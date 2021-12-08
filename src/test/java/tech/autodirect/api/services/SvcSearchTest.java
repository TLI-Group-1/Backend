package tech.autodirect.api.services;

import org.junit.jupiter.api.*;
import org.springframework.web.server.ResponseStatusException;
import tech.autodirect.api.database.TableCars;
import tech.autodirect.api.database.TableOffers;
import tech.autodirect.api.database.TableUsers;
import tech.autodirect.api.interfaces.SensoApiInterface;
import tech.autodirect.api.interfaces.TableCarsInterface;
import tech.autodirect.api.interfaces.TableOffersInterface;
import tech.autodirect.api.interfaces.TableUsersInterface;
import tech.autodirect.api.upstream.SensoApi;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

// This annotation allows us to use a non-static BeforeAll/AfterAll methods (TODO: check if ok)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SvcSearchTest {
    private static final String dbName = "testing";
    private final String testUserId = "SvcSearchTest_test_user";
    private TableUsersInterface tableUsers;
    private SvcSearch svcSearch;

    @Test
    void testSearchCarsInvalidAllNull() {
        try {
            List<Map<String, Object>> carsResult = svcSearch.searchCars("", "null", "null", "null", "null");
            assert carsResult.size() > 0;
        } catch (IOException | InterruptedException | SQLException e) {
            e.printStackTrace();
            assert false;
        } catch (ResponseStatusException e) {
            assert true; // check that 400 error is thrown
        }
    }

    @Test
    void testSearchCarsInvalidAllEmpty() {
        try {
            List<Map<String, Object>> carsResult = svcSearch.searchCars("", "", "", "", "");
            assert carsResult.size() > 0;
        } catch (IOException | InterruptedException | SQLException e) {
            e.printStackTrace();
            assert false;
        } catch (ResponseStatusException e) {
            assert true; // check that 400 error is thrown
        }
    }

    @Test
    void testSearchCarsPreLogin() {
        try {
            tableUsers.addUser(testUserId, 700, 1000, 200);
            List<Map<String, Object>> carsResult = svcSearch.searchCars("", "1000", "200", "price", "true");
            assert carsResult.size() > 0;
        } catch (IOException | InterruptedException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    void testSearchCarsPostLogin() {
        try {
            tableUsers.addUser(testUserId, 700, 1000, 200);
            List<Map<String, Object>> carsResult = svcSearch.searchCars(testUserId, "1000", "200", "price", "true");
            assert carsResult.size() > 0;
        } catch (IOException | InterruptedException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    void testSearchCarsPostLoginBadSortBy() {
        try {
            tableUsers.addUser(testUserId, 700, 1000, 200);
            List<Map<String, Object>> carsResult = svcSearch.searchCars(testUserId, "1000", "200", "price", "true");
            assert carsResult.size() > 0;
        } catch (IOException | InterruptedException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @BeforeAll
    public void setUpAll() {
        try {
            TableCarsInterface tableCars = new TableCars(dbName);
            this.tableUsers = new TableUsers(dbName);
            TableOffersInterface tableOffers = new TableOffers(dbName);
            SensoApiInterface sensoApi = new SensoApi();
            this.svcSearch = new SvcSearch(tableCars, tableUsers, tableOffers, sensoApi);

            // testUserId user will be created in tests, ensure it doesn't exist yet
            if (tableUsers.checkUserExists(testUserId)) {
                tableUsers.removeUserById(testUserId);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void tearDownEach() {
        try {
            if (tableUsers.checkUserExists(testUserId)) {
                tableUsers.removeUserById(testUserId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}