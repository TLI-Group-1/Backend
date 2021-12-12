package tech.autodirect.api.services;

import org.junit.jupiter.api.*;
import tech.autodirect.api.database.TableCars;
import tech.autodirect.api.database.TableOffers;
import tech.autodirect.api.database.TableUsers;
import tech.autodirect.api.entities.EntOffer;
import tech.autodirect.api.interfaces.SensoApiInterface;
import tech.autodirect.api.interfaces.TableCarsInterface;
import tech.autodirect.api.interfaces.TableOffersInterface;
import tech.autodirect.api.interfaces.TableUsersInterface;
import tech.autodirect.api.upstream.SensoApi;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

// This annotation allows us to use a non-static BeforeAll/AfterAll methods
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SvcUpdateLoanAmountTest {
    private static final String DB_NAME = "testing";
    private final String testUserId = "SvcUpdateLoanAmountTest_test_user";
    private SvcUpdateLoanAmount svcUpdateLoanAmount;
    private SensoApiInterface sensoApi;
    private TableCarsInterface tableCars;
    private TableUsersInterface tableUsers;
    private TableOffersInterface tableOffers;

    @Test
    void testUpdateLoanAmount() {
        try {

            tableOffers.setUser(testUserId);

            // Add the user
            tableUsers.addUser(testUserId, 700, 1000, 250);

            // Add an offer to the offers table
            int offerId = tableOffers.addOffer(1, 5000, 3, 4, 5, 6, 7, "TEST", true);

            // Verify the initial loan amount
            Map<String, Object> offerMap = tableOffers.getOfferByOfferId(offerId);
            EntOffer offer = new EntOffer();
            offer.loadFromMap(offerMap);
            assert offer.getLoanAmount() == 5000;


            // Update initial loan amount
            int newLoanAmount = 6000;
            svcUpdateLoanAmount.updateLoanAmount(
                    tableCars,
                    tableUsers,
                    tableOffers,
                    sensoApi,
                    testUserId,
                    Integer.toString(offerId),
                    Integer.toString(newLoanAmount)
            );

            // Check loan amount was updated
            Map<String, Object> offerMapUpdated = tableOffers.getOfferByOfferId(offerId);
            EntOffer offerUpdated = new EntOffer();
            offerUpdated.loadFromMap(offerMapUpdated);
            assert offerUpdated.getLoanAmount() == newLoanAmount;
        } catch (SQLException | ClassNotFoundException | IOException | InterruptedException e) {
            e.printStackTrace();
            assert false;
        }
    }


    @BeforeAll
    public void setUpAll() {
        try {
            svcUpdateLoanAmount = new SvcUpdateLoanAmount();
            sensoApi = new SensoApi();
            tableCars = new TableCars(DB_NAME);
            tableUsers = new TableUsers(DB_NAME);
            tableOffers = new TableOffers(DB_NAME);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
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

            // Remove user
            if (tableUsers.checkUserExists(testUserId)) {
                tableUsers.removeUserById(testUserId);
            }
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

            // Remove user
            if (tableUsers.checkUserExists(testUserId)) {
                tableUsers.removeUserById(testUserId);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}