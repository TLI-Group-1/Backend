package tech.autodirect.api.database;

import org.junit.jupiter.api.*;
import tech.autodirect.api.entities.EntOffer;
import tech.autodirect.api.interfaces.TableOffersInterface;

import java.sql.SQLException;
import java.util.*;


// This annotation allows us to use a non-static BeforeAll/AfterAll methods (TODO: check if ok)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TableOffersTests {
    private static final String dbName = "testing";

    // Note: throughout this test class, we don't assume that testUserId exists in the users table.
    // We don't need to assume this since we never actually access this user in the users table.
    // We only use this userId to create offers (to help name the offers table).
    // TODO (Nada): read this and make sure you understand. Don't delete the comment.
    private final String testUserId = "TableOffersTests_test_user";


    /**
     * Create a new offers table, then verify it exists.
     */
    @Test
    void testNewTable() {
        try {
            // Create new table for testUserId. setUpEach() ensures table doesn't already exist.
            TableOffers table = new TableOffers(dbName);
            String tableName = table.newTable(testUserId);

            // check if the created table exists under the correct name (both should work)
            assert table.checkTableExists();
            assert table.checkTableExists(tableName);
        } catch (SQLException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    void testAddOfferAndGetOfferClaimed() {
        try {
            // Create new table for testUserId. setUpEach() ensures table doesn't already exist.
            TableOffers table = new TableOffers(dbName);
            table.newTable(testUserId);

            int offerId = table.addOffer(1, 2, 3, 4, 5, 6, 7, "TEST", true);
            Map<String, Object> offerMap = table.getOfferByOfferId(offerId);
            EntOffer offer = new EntOffer();
            offer.loadFromMap(offerMap);

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
        } catch (SQLException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    void testAddOfferAndGetOfferNotClaimed() {
        try {
            // Create new table for testUserId. setUpEach() ensures table doesn't already exist.
            TableOffers table = new TableOffers(dbName);
            table.newTable(testUserId);

            int offerId = table.addOffer(1, 2, 3, 4, 5, 6, 7, "TEST", false);
            Map<String, Object> offerMap = table.getOfferByOfferId(offerId);
            EntOffer offer = new EntOffer();
            offer.loadFromMap(offerMap);

            assert offer.getOfferId() == offerId;
            assert offer.getCarId() == 1;
            assert offer.getLoanAmount() == 2;
            assert offer.getCapitalSum() == 3;
            assert offer.getInterestSum() == 4;
            assert offer.getTotalSum() == 5;
            assert offer.getInterestRate() == 6;
            assert offer.getTermMo() == 7;
            assert Objects.equals(offer.getInstallments(), "TEST");
            assert !offer.isClaimed();
        } catch (SQLException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    void testGetOfferWhenDoesNotExist() {
        try {
            // Create new table for testUserId. setUpEach() ensures table doesn't already exist.
            TableOffers table = new TableOffers(dbName);
            table.newTable(testUserId);

            // We know that this offerId does not exist in this offers table
            // since we just created the table (there are no offers in this table yet).
            Map<String, Object> emptyMap = Collections.emptyMap();
            Map<String, Object> offerMap = table.getOfferByOfferId(1);
            assert offerMap == emptyMap;
        } catch (SQLException e) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public void tearDownEach() {
        try {
            TableOffers table = new TableOffers(dbName);
            table.dropTable(TableOffersInterface.createTableName(testUserId)); // drop table is already exists
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}