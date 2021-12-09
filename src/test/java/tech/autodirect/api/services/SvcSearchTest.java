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
import tech.autodirect.api.utils.TypeConvert;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

// This annotation allows us to use a non-static BeforeAll/AfterAll methods (TODO: check if ok)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SvcSearchTest {
    private static final String dbName = "testing";
    private final String testUserId = "SvcSearchTest_test_user";
    private TableUsersInterface tableUsers;
    private SvcSearch svcSearch;

    @Test
    void testSearchCarsPreLoginWithAllNull() {
        try {
            // Since userId is "", only checks sortBy
            List<Map<String, Object>> carsResult = svcSearch.search("", "null", "null", "null", "null");
            assert carsResult.size() > 0;
        } catch (IOException | InterruptedException | SQLException e) {
            e.printStackTrace();
            assert false;
        } catch (ResponseStatusException e) {
            assert Objects.requireNonNull(e.getMessage()).startsWith("400 BAD_REQUEST");
        }
    }

    @Test
    void testSearchCarsPreLoginWithAllEmpty() {
        try {
            List<Map<String, Object>> carsResult = svcSearch.search("", "", "", "", "");
            assert carsResult.size() > 0;
        } catch (IOException | InterruptedException | SQLException e) {
            e.printStackTrace();
            assert false;
        } catch (ResponseStatusException e) {
            assert Objects.requireNonNull(e.getMessage()).startsWith("400 BAD_REQUEST");
        }
    }

    @Test
    void testSearchCarsPreLoginNonPriceSortBy() {
        try {
            tableUsers.addUser(testUserId, 700, 1000, 200);
            List<Map<String, Object>> carsResult = svcSearch.search("", "1000", "200", "term_length", "true");
            assert carsResult.size() > 0;
        } catch (IOException | InterruptedException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            assert false;
        } catch (ResponseStatusException e) {
            // pre-login search must take "price" sortBy
            assert Objects.requireNonNull(e.getMessage()).startsWith("400 BAD_REQUEST");
        }    }

    @Test
    void testSearchCarsPreLoginCheckSorting() {
        try {
            List<String> valuesOfSortBy = List.of("price");
            List<String> valuesOfSortAsc = Arrays.asList("true", "false");
            testSorting("", valuesOfSortBy, valuesOfSortAsc);
        } catch (IOException | InterruptedException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    void testSearchCarsPostLoginCheckSorting() {
        try {
            List<String> valuesOfSortBy = Arrays.asList("price", "payment_mo", "apr", "total_sum", "term_length");
            List<String> valuesOfSortAsc = Arrays.asList("true", "false");
            testSorting(testUserId, valuesOfSortBy, valuesOfSortAsc);
        } catch (IOException | InterruptedException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            assert false;
        }
    }

    /**
     * Tests that sorting works looping over valuesOfSortBy and valuesOfSortAsc with userId.
     */
    void testSorting(
            String userId,
            List<String> valuesOfSortBy,
            List<String> valuesOfSortAsc
    ) throws SQLException, ClassNotFoundException, IOException, InterruptedException {
        for (String sortBy : valuesOfSortBy) {
            for (String sortAscString : valuesOfSortAsc) {
                if (!tableUsers.checkUserExists(userId) && !userId.equals("")) {
                    tableUsers.addUser(userId, 700, 1000, 200);
                }

                List<Map<String, Object>> carsAndOffers = svcSearch.search(
                        userId, "1000", "200", sortBy, sortAscString
                );
                assert carsAndOffers.size() > 0;

                boolean sortAsc = Boolean.parseBoolean(sortAscString);
                double prev = TypeConvert.toDouble(carsAndOffers.get(0).get(sortBy));
                for (Map<String, Object> carAndOffer : carsAndOffers) {
                    double valueDouble = TypeConvert.toDouble(carAndOffer.get(sortBy));

                    if (sortAsc) {
                        assert prev <= valueDouble;
                    } else {
                        assert prev >= valueDouble;
                    }
                }
            }
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