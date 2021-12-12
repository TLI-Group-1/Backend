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
class SvcClaimOfferTest {
    private static final String DB_NAME = "testing";
    private final String testUserId = "SvcClaimOfferTest_test_user";

    @Test
    void testClaimOffer() {
        try {
            SvcClaimOffer svcClaimOffer = new SvcClaimOffer();
            TableOffersInterface tableOffers = new TableOffers(DB_NAME);
            tableOffers.setUser(testUserId);

            // Add an unclaimed offerId to the offers table
            int offerId = tableOffers.addOffer(1, 2, 3, 4, 5, 6, 7, "TEST", false);

            // Verify the offer is initially not claimed
            boolean claimedInit = (boolean) tableOffers.getOfferByOfferId(offerId).get("claimed");
            assert !claimedInit;

            // Claim the offer
            svcClaimOffer.claimOffer(tableOffers, testUserId, Integer.toString(offerId));

            // Check that the offer is now claimed
            boolean claimedFinal = (boolean) tableOffers.getOfferByOfferId(offerId).get("claimed");
            assert claimedFinal;
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