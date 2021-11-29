package tech.autodirect.api.services;

import org.junit.jupiter.api.*;
import tech.autodirect.api.database.TableCars;
import tech.autodirect.api.database.TableUsers;
import tech.autodirect.api.entities.EntCar;
import tech.autodirect.api.interfaces.SensoApiInterface;
import tech.autodirect.api.interfaces.TableCarsInterface;
import tech.autodirect.api.interfaces.TableUsersInterface;
import tech.autodirect.api.upstream.SensoApi;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// This annotation allows us to use a non-static BeforeAll/AfterAll methods (TODO: check if ok)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SvcSearchTest {
    private final String testUserId = "SvcSearchTest.test_user";
    private final String testUserOffersTableName = "SvcSearchTest.test_user.offers_table";
    private TableUsersInterface tableUser;
    private SvcSearch svcSearch;

    @Test
    void testSearchCarsPrelogin() {
        try {
            List<EntCar> carsResult = svcSearch.searchCars(null, -1, -1, "", false, "");
            assert carsResult.size() > 0;
        } catch (IOException | InterruptedException | SQLException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    void testSearchCarsPostLogin() {
        try {
            // Create user in users table if not exists
            int creditScore = 700;
            double downPayment = 1000;
            double budgetMonthly = 500;
            tableUser.addUser(testUserId, creditScore, downPayment, budgetMonthly, testUserOffersTableName);

            // Perform search
            List<EntCar> carsResult = svcSearch.searchCars(testUserId, downPayment, budgetMonthly, "", false, "");
            assert carsResult.size() > 0;
        } catch (IOException | InterruptedException | SQLException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @BeforeAll
    public void setUpAll() {
        try {
            TableCarsInterface tableCars = new TableCars("testing");
            this.tableUser = new TableUsers("testing");
            SensoApiInterface sensoApi = new SensoApi();
            this.svcSearch = new SvcSearch(tableCars, tableUser, sensoApi);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}