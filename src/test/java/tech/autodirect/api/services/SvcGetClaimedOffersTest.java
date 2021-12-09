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

import javax.management.InstanceAlreadyExistsException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


// This annotation allows us to use a non-static BeforeAll/AfterAll methods (TODO: check if ok)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SvcGetClaimedOffersTest {
    private static final String dbName = "testing";
    private final String testUserId = "SvcGetClaimedOffersTest_test_user";

    @Test
    void testGetClaimedOffers() {
        try {
            SvcGetClaimedOffers svcGetClaimedOffers = new SvcGetClaimedOffers();
            TableCarsInterface tableCars = new TableCars(dbName);
            TableOffersInterface tableOffers = new TableOffers(dbName);
            tableOffers.setUser(testUserId);

            // Add carIds for 4 cars from the database
            List<Map<String, Object>> carMaps = tableCars.getAllCars();
            assert carMaps.size() >= 4;
            List<Integer> fourCarIds = new ArrayList<>();
            for (Map<String, Object> carMap : carMaps) {
                int carId = (int) carMap.get("car_id");
                fourCarIds.add(carId);
            }

            // Add offers corresponding to the cars
            int offerId1 = tableOffers.addOffer(fourCarIds.get(0), 2, 3, 4, 5, 6, 7, "TEST", false);
            int offerId2 = tableOffers.addOffer(fourCarIds.get(1), 2, 3, 4, 5, 6, 7, "TEST", true);
            int offerId3 = tableOffers.addOffer(fourCarIds.get(2), 2, 3, 4, 5, 6, 7, "TEST", false);
            int offerId4 = tableOffers.addOffer(fourCarIds.get(3), 2, 3, 4, 5, 6, 7, "TEST", true);

            // Check that carAndOfferInfoMaps from getClaimedOffers has all the offers
            List<Map<String, Object>> carAndOfferInfoMaps
                    = svcGetClaimedOffers.getClaimedOffers(tableCars, tableOffers, testUserId);
            boolean offerId1InOffers = false;
            boolean offerId2InOffers = false;
            boolean offerId3InOffers = false;
            boolean offerId4InOffers = false;
            for (Map<String, Object> carAndOfferInfo : carAndOfferInfoMaps) {
                if ((int) carAndOfferInfo.get("offer_id") == offerId1) { offerId1InOffers = true; }
                if ((int) carAndOfferInfo.get("offer_id") == offerId2) { offerId2InOffers = true; }
                if ((int) carAndOfferInfo.get("offer_id") == offerId3) { offerId3InOffers = true; }
                if ((int) carAndOfferInfo.get("offer_id") == offerId4) { offerId4InOffers = true; }
            }

            assert offerId1InOffers && offerId2InOffers && offerId3InOffers && offerId4InOffers;
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