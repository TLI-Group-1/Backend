package tech.autodirect.api.services;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import tech.autodirect.api.database.TableOffers;
import tech.autodirect.api.interfaces.TableOffersInterface;

import java.sql.SQLException;


// This annotation allows us to use a non-static BeforeAll/AfterAll methods
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SvcUnclaimOfferTest {
    private static final String DB_NAME = "testing";
    private final String testUserId = "SvcUnclaimOfferTest_test_user";

    @Test
    void testUnclaimOffer() {
        try {
            SvcUnclaimOffer svcUnclaimOffer = new SvcUnclaimOffer();
            TableOffersInterface tableOffers = new TableOffers(DB_NAME);
            tableOffers.setUser(testUserId);

            // Add a claimed offerId to the offers table
            int offerId = tableOffers.addOffer(1, 2, 3, 4, 5, 6, 7, "TEST", true);

            // Verify the offer is initially claimed
            boolean claimedInit = (boolean) tableOffers.getOfferByOfferId(offerId).get("claimed");
            assert claimedInit;

            // Claim the offer
            svcUnclaimOffer.unclaimOffer(tableOffers, testUserId, Integer.toString(offerId));

            // Check that the offer is now unclaimed
            boolean claimedFinal = (boolean) tableOffers.getOfferByOfferId(offerId).get("claimed");
            assert !claimedFinal;
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
            TableOffers table = new TableOffers(DB_NAME);
            String tableName = TableOffersInterface.createTableName(testUserId);
            table.dropTable(tableName); // drop table if already exists
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public void tearDownAll() {
        try {
            TableOffers table = new TableOffers(DB_NAME);
            String tableName = TableOffersInterface.createTableName(testUserId);
            table.dropTable(tableName); // drop table if already exists
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}