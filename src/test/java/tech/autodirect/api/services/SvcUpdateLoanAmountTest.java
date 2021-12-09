package tech.autodirect.api.services;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import tech.autodirect.api.database.TableOffers;
import tech.autodirect.api.entities.EntOffer;
import tech.autodirect.api.interfaces.TableOffersInterface;

import java.sql.SQLException;
import java.util.Map;

// This annotation allows us to use a non-static BeforeAll/AfterAll methods (TODO: check if ok)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SvcUpdateLoanAmountTest {
    private static final String dbName = "testing";
    private final String testUserId = "SvcUpdateLoanAmountTest_test_user";

    @Test
    void testUpdateLoanAmount() {
        try {
            SvcUpdateLoanAmount svcUpdateLoanAmount = new SvcUpdateLoanAmount();
            TableOffersInterface tableOffers = new TableOffers(dbName);
            tableOffers.setUser(testUserId);

            // Add an offer to the offers table
            int offerId = tableOffers.addOffer(1, 2, 3, 4, 5, 6, 7, "TEST", true);

            // Verify the initial principal
            Map<String, Object> offerMap = tableOffers.getOfferByOfferId(offerId);
            EntOffer offer = new EntOffer();
            offer.loadFromMap(offerMap);
            assert offer.getLoanAmount() == 2;

            boolean claimedInit = (boolean) tableOffers.getOfferByOfferId(offerId).get("loan_offer");
            assert claimedInit;

//            // Claim the offer
//            svcUpdatePrincipal.updatePrincipal(tableOffers, testUserId, "1000");
//
//            // Check that the offer is now unclaimed
//            boolean claimedFinal = (boolean) tableOffers.getOfferByOfferId(offerId).get("claimed");
//            assert !claimedFinal;
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