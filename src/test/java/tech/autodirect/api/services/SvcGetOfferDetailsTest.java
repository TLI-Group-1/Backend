package tech.autodirect.api.services;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import tech.autodirect.api.database.TableCars;
import tech.autodirect.api.database.TableOffers;
import tech.autodirect.api.entities.EntOffer;
import tech.autodirect.api.interfaces.TableCarsInterface;
import tech.autodirect.api.interfaces.TableOffersInterface;

import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

// This annotation allows us to use a non-static BeforeAll/AfterAll methods
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SvcGetOfferDetailsTest {
    private static final String dbName = "testing";
    private final String testUserId = "SvcGetOfferDetailsTest_test_user";

    @Test
    void getOfferDetails() {
        try {
            SvcGetOfferDetails svcGetOfferDetails = new SvcGetOfferDetails();
            TableCarsInterface tableCars = new TableCars(dbName);
            TableOffersInterface tableOffers = new TableOffers(dbName);
            tableOffers.setUser(testUserId);

            // Add an offer
            int offerId = tableOffers.addOffer(1, 2, 3, 4, 5, 6, 7, "TEST", true);

            // Get offer details
            Map<String, Object> carAndOfferInfo
                    = svcGetOfferDetails.getOfferDetails(tableCars, tableOffers, testUserId, Integer.toString(offerId));

            // Checks
            assert (int) carAndOfferInfo.get("offer_id") == offerId;
            assert (int) carAndOfferInfo.get("car_id") == 1;
            assert (double) carAndOfferInfo.get("loan_amount") == 2;
            assert (double) carAndOfferInfo.get("capital_sum") == 3;
            assert (double) carAndOfferInfo.get("interest_sum") == 4;
            assert (double) carAndOfferInfo.get("total_sum") == 5;
            assert (double) carAndOfferInfo.get("interest_rate") == 6;
            assert (double) carAndOfferInfo.get("term_mo") == 7;
            assert Objects.equals(carAndOfferInfo.get("installments"), "TEST");
            assert (boolean) carAndOfferInfo.get("claimed");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @BeforeEach
    public void setUpEach() {
        try {
            // Drop testUserId's offers table before each test.
            // This is especially important for tests which test the creation new tables
            // (no point in testing the creation of a new table if it already exists).
            TableOffers table = new TableOffers(dbName);
            String tableName = TableOffersInterface.createTableName(testUserId);
            table.dropTable(tableName); // drop table if already exists
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public void tearDownAll() {
        try {
            TableOffers table = new TableOffers(dbName);
            String tableName = TableOffersInterface.createTableName(testUserId);
            table.dropTable(tableName); // drop table if already exists
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}