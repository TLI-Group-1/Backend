package tech.autodirect.api.database;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.*;
import org.springframework.web.server.ResponseStatusException;
import tech.autodirect.api.entities.EntOffer;
import tech.autodirect.api.interfaces.TableOffersInterface;

import javax.management.InstanceAlreadyExistsException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;


// This annotation allows us to use a non-static BeforeAll/AfterAll methods (TODO: check if ok)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TableOffersTests {
    private static final String dbName = "testing";

    // Note: throughout this test class, we don't assume that testUserId exists in the users table.
    // We don't need to assume this since we never actually access this user in the users table.
    // We only use this userId to create offers (to help name the offers table).
    private final String testUserId = "TableOffersTests_test_user";


    /**
     * Use setUser() to create an offers table for a user that does not have one, then verify it exists.
     */
    @Test
    void testSetUserAndCheckTableExists() {
        try {
            // Creates new offers table for testUserId. setUpEach() ensures table doesn't already exist.
            TableOffers table = new TableOffers(dbName);
            String tableName = table.setUser(testUserId);

            // check if the created table exists under the correct name (both should work)
            assert table.checkTableExists();
            assert table.checkTableExists(tableName);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            assert false;
        }
    }

    /**
     * Test that getTableName() gives a tableName corresponding to TableOffersInterface.createTableName().
     */
    @Test
    void testGetTableName() {
        try {
            // Creates new offers table for testUserId. setUpEach() ensures table doesn't already exist.
            TableOffers table = new TableOffers(dbName);
            table.setUser(testUserId);

            // Check that getTableName() gives a tableName corresponding to TableOffersInterface.createTableName()
            assert table.getTableName().equals(TableOffersInterface.createTableName(testUserId));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            assert false;
        }
    }

    /**
     * Tests addOffer method and getOfferByOfferId when offer is claimed
     */
    @Test
    void testAddOfferAndGetOfferClaimed() {
        try {
            // Create new table for testUserId. setUpEach() ensures table doesn't already exist.
            TableOffers table = new TableOffers(dbName);
            table.setUser(testUserId);

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
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            assert false;
        }
    }

    /**
     * Tests addOffer method and getOfferByOfferId when offer is not claimed
     */
    @Test
    void testAddOfferAndGetOfferNotClaimed() {
        try {
            // Create new table for testUserId. setUpEach() ensures table doesn't already exist.
            TableOffers table = new TableOffers(dbName);
            table.setUser(testUserId);

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
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            assert false;
        }
    }

    /**
     * Tests getOfferByOfferId when offer does not exist
     */
    @Test
    void testGetOfferWhenDoesNotExist() {
        try {
            // Create new table for testUserId. setUpEach() ensures table doesn't already exist.
            TableOffers table = new TableOffers(dbName);
            table.setUser(testUserId);

            // We know that this offerId does not exist in this offers table,
            // so it should throw an HTTP 404 exception.
            table.getOfferByOfferId(1);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            assert false;
        } catch (Exception e) {
            assert e instanceof ResponseStatusException;
            assert Objects.equals(e.getMessage(), "404 NOT_FOUND \"offer not found\"");
        }
    }

    /**
     * Tests checkOfferExists() method when offer exists.
     */
    @Test
    void testCheckOfferExistsWhenExists() {
        try {
            // Create new table for testUserId. setUpEach() ensures table doesn't already exist.
            TableOffers table = new TableOffers(dbName);
            table.setUser(testUserId);
            int offerId = table.addOffer(1, 2, 3, 4, 5, 6, 7, "TEST", false);
            assert table.checkOfferExists(offerId);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            assert false;
        }
    }

    /**
     * Tests checkOfferExists() method when offer does not exists.
     */
    @Test
    void testCheckOfferExistsWhenNotExists() {
        try {
            // Create new table for testUserId. setUpEach() ensures table doesn't already exist.
            TableOffers table = new TableOffers(dbName);
            table.setUser(testUserId);
            assert !table.checkOfferExists(1);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            assert false;
        }
    }

    /**
     * Tests removeOfferByOfferId().
     */
    @Test
    void testRemoveOfferByOfferId() {
        try {
            // Create new table for testUserId
            TableOffers table = new TableOffers(dbName);
            table.setUser(testUserId);

            int offerId = table.addOffer(1, 2, 3, 4, 5, 6, 7, "TEST", false);
            table.removeOfferByOfferId(offerId);

            table.getOfferByOfferId(offerId); // Should throw 404 error
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            assert false;
        } catch (ResponseStatusException e) {
            assert true;
            assert Objects.equals(e.getMessage(), "404 NOT_FOUND \"offer not found\"");
        }
    }

    /**
     * Tests removeAllOffers().
     */
    @Test
    void testRemoveAllOffers() {
        try {
            // Create new table for testUserId
            TableOffers table = new TableOffers(dbName);
            table.setUser(testUserId);

            int offerId1 = table.addOffer(1, 2, 3, 4, 5, 6, 7, "TEST", false);
            int offerId2 = table.addOffer(8, 9, 10, 11, 12, 13, 7, "TEST", false);

            assert table.getAllOffers().size() > 0;
            table.removeAllOffers();
            assert table.getAllOffers().size() == 0;

            table.getOfferByOfferId(offerId1); // Should throw 404 error
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            assert false;
        } catch (ResponseStatusException e) {
            assert true;
            assert Objects.equals(e.getMessage(), "404 NOT_FOUND \"offer not found\"");
        }
    }

    /**
     * Tests getAllOffers() with more than one offer being returned.
     */
    @Test
    void testGetAllOffers() {
        try {
            TableOffers table = new TableOffers(dbName);
            table.setUser(testUserId);

            // Add offers and get all offers as maps in a list
            int offerId1 = table.addOffer(1, 2, 3, 4, 5, 6, 7, "TEST", false);
            int offerId2 = table.addOffer(8, 9, 10, 11, 12, 13, 7, "TEST", false);
            List<Map<String, Object>> offersList = table.getAllOffers();
            assert offersList.size() == 2;

            // Check whether offers referred to by offerId1 and offerId2 are in offersList
            boolean offerId1InOffersList = false;
            boolean offerId2InOffersList = false;
            for (Map<String, Object> offerMap : offersList) {
                EntOffer offer = new EntOffer();
                offer.loadFromMap(offerMap);

                if (offer.getOfferId() == offerId1) {
                    offerId1InOffersList = true;
                }
                if (offer.getOfferId() == offerId2) {
                    offerId2InOffersList = true;
                }

            }
            assert offerId1InOffersList && offerId2InOffersList;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            assert false;
        }
    }

    /**
     * Tests getClaimedOffers() when there are no claimed offers
     */
    @Test
    void testGetClaimedOffersWhenNoClaimedOffers() {
        try {
            // Create new table for testUserId
            TableOffers table = new TableOffers(dbName);
            table.setUser(testUserId);

            int offerId1 = table.addOffer(1, 2, 3, 4, 5, 6, 7, "TEST", false);
            int offerId2 = table.addOffer(8, 9, 10, 11, 12, 13, 7, "TEST", false);

            List<Map<String, Object>> claimedOffers = table.getClaimedOffers();


            assert Objects.equals(claimedOffers, Collections.emptyList());

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            assert false;
        }
    }

    /**
     * Tests getClaimedOffers() when there are some claimed and some unclaimed offers.
     */
    @Test
    void testGetClaimedOffersWhenSomeClaimed() {
        try {
            // Create new table for testUserId
            TableOffers table = new TableOffers(dbName);
            table.setUser(testUserId);

            int offerId1 = table.addOffer(1, 2, 3, 4, 5, 6, 7, "TEST", true);
            int offerId2 = table.addOffer(8, 9, 10, 11, 12, 13, 7, "TEST", false);

            // Check that get claimedOffers only contains the offer referred to by offerId1
            List<Map<String, Object>> claimedOffers = table.getClaimedOffers();
            Map<String, Object> offer = table.getOfferByOfferId(offerId1);
            assert claimedOffers.size() == 1 && claimedOffers.contains(offer);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            assert false;
        }
    }

    /**
     * Tests getMarkOfferClaimed()
     */
    @Test
    void testMarkOfferClaimed() {
        try {
            // Create new table for testUserId. setUpEach() ensures table doesn't already exist.
            TableOffers table = new TableOffers(dbName);
            table.setUser(testUserId);

            int offerId = table.addOffer(1, 2, 3, 4, 5, 6, 7, "TEST", false);
            table.markOfferClaimed(offerId);

            Map<String, Object> offerMap = table.getOfferByOfferId(offerId);
            EntOffer offer = new EntOffer();
            offer.loadFromMap(offerMap);

            assert offer.isClaimed();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            assert false;
        }
    }

    /**
     * Tests getMarkOfferClaimed()
     */
    @Test
    void testMarkOfferUnclaimed() {
        try {
            // Create new table for testUserId. setUpEach() ensures table doesn't already exist.
            TableOffers table = new TableOffers(dbName);
            table.setUser(testUserId);

            int offerId = table.addOffer(1, 2, 3, 4, 5, 6, 7, "TEST", true);
            table.markOfferUnclaimed(offerId);

            Map<String, Object> offerMap = table.getOfferByOfferId(offerId);
            EntOffer offer = new EntOffer();
            offer.loadFromMap(offerMap);

            assert !offer.isClaimed();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            assert false;
        }
    }

    /**
     * Tests dropTable() and CheckTableExists() methods.
     */
    @Test
    void testDropTableAndCheckTableExists() {
        try {
            // Create new table for testUserId and then drop it. Verify that it is dropped.
            TableOffers table = new TableOffers(dbName);
            table.setUser(testUserId);
            assert table.checkTableExists();
            table.dropTable();
            assert !table.checkTableExists();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            assert false;
        }
    }

    /**
     * Tests checkTableExists() method when table does not exist.
     */
    @Test
    void testCheckTableExistsWhenNotExists() {
        try {
            // Create new table for testUserId, then create a new offer and check it exists.
            TableOffers table = new TableOffers(dbName);
            assert !table.checkTableExists("fake table");
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