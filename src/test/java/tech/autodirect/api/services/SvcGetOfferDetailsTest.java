package tech.autodirect.api.services;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import tech.autodirect.api.database.TableOffers;
import tech.autodirect.api.entities.EntOffer;
import tech.autodirect.api.interfaces.TableOffersInterface;

import java.sql.SQLException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

// This annotation allows us to use a non-static BeforeAll/AfterAll methods (TODO: check if ok)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SvcGetOfferDetailsTest {
    private static final String dbName = "testing";
    private final String testUserId = "TableOffersTests_test_user";

    @Test
    void getOfferDetails() {
        try {
            SvcGetOfferDetails svcGetOfferDetails = new SvcGetOfferDetails();
            TableOffersInterface tableOffers = new TableOffers(dbName);
            tableOffers.setUser(testUserId);

            // Add an offer
            int offerId = tableOffers.addOffer(1, 2, 3, 4, 5, 6, 7, "TEST", true);

            // Get offer details
            EntOffer offer = svcGetOfferDetails.getOfferDetails(tableOffers, testUserId, Integer.toString(offerId));

            // Checks
            assert offer.getOfferId() == offerId;
            assert offer.getCarId() == 1;
            assert offer.getLoanAmount() == 2;
            assert offer.getCapitalSum() == 3;
            assert offer.getInterestSum() == 4;
            assert offer.getTotalSum() == 5;
            assert offer.getInterestRate() == 6;
            assert offer.getTermMo() == 7;
            assert Objects.equals(offer.getInstallments(), "TEST");
            assert offer.isClaimed();
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