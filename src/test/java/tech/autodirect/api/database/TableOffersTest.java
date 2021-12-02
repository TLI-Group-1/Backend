package tech.autodirect.api.database;

import org.junit.jupiter.api.*;
import tech.autodirect.api.interfaces.TableCarsInterface;
import tech.autodirect.api.services.SvcSearch;
import tech.autodirect.api.upstream.SensoApi;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


// This annotation allows us to use a non-static BeforeAll/AfterAll methods (TODO: check if ok)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TableOffersTest {
    private static final String db_name = "testing";

    /**
     * Create a new offers.offers_ table in the offers schema, then check if it exists.
     */
    @Test void newTable() {
        try {
            // create a new table for "test_user"
            String username = "test_user";
            TableOffers table = new TableOffers(db_name);
            table.newTable(username);

            // check if the created table exists under the correct name
            Statement stmt = table.db_conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT EXISTS (" +
                    "SELECT 1 " +
                    "FROM   information_schema.tables " +
                    "WHERE  table_schema = 'offers'" +
                    "   and table_name=" + "'offer_" + username + "'" +
                ");"
            );
            rs.next();
            // this result should return true if the specified table exists
            boolean result = rs.getBoolean("exists");
            stmt.close();
            assert result;
        }
        catch (SQLException e) {
            e.printStackTrace();
            assert false;
        }
    }
    @Test
    void testAddOfferClaimed() {
        try {
            // create a new table for "test_user"
            String userId = "test_user";
            TableOffers table = new TableOffers(db_name);
            table.newTable("test_offer");

            int offerId = table.addOffer(5, 1000, 50, 50, 50, 50, 50, "TEST", true);
            Map<String, Object> offer = table.getOfferByOfferId(offerId);
            assert (int) offer.get("offer_id") == offerId;
            assert (int) offer.get("car_id") == 5;
            assert (double) offer.get("loan_amount") == 1000;
            assert (double) offer.get("capital_sum") == 50;
            assert (double) offer.get("interest_sum") == 50;
            assert (double) offer.get("total_sum") == 50;
            assert (double) offer.get("interest_rate") == 50;
            assert (double) offer.get("term_mo") == 50;
            assert Objects.equals((String) offer.get("installments"), "TEST");
            assert (boolean) offer.get("claimed");
        }
        catch (SQLException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    void testGetOfferByOfferIdEmpty(){
        try{
            //create an offer Id
            int offerId = 123;
            TableOffers table = new TableOffers(db_name);
            Map<String, Object> emptyMap = Collections.emptyMap();
            assert table.getOfferByOfferId(offerId) == emptyMap;
        }
        catch (SQLException e){
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    void testGetOfferByOfferId(){
        try{
            //create an offer Id
            TableOffers table = new TableOffers(db_name);
            Map<String, Object> offerMap = new HashMap<String, Object>()
            {
                {
                    put("offer_id", 123);
                    put("car_id", 5);
                    put("loan_amount", 1000);
                    put("capital_sum", 50);
                    put("interest_sum", 50);
                    put("total_sum", 50);
                    put("interest_rate", 50);
                    put("term_mo", 50);
                    put("installments", "TEST");
                    put("claimed", true);
                }
            };
            assert Objects.equals(table.getOfferByOfferId(123), offerMap);
        }
        catch (SQLException e){
            e.printStackTrace();
            assert false;
        }
    }


//    @BeforeAll
//    public void setUpAll() {
//        try {
//            this.tableUser = new TableUsers("autodirect");
//            this.tableOffers = new TableOffers("autodirect");
//            SensoApiInterface sensoApi = new SensoApi();
//            this.svcSearch = new SvcSearch(tableCars, tableUser, sensoApi);
//
//            // testUserId user will be created in tests, ensure it doesn't exist yet
//            if (tableUser.userExists(testUserId)) {
//                tableUser.removeUserByID(testUserId);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @AfterEach
//    public void tearDownEach() {
//        try {
//            if (tableUser.userExists(testUserId)) {
//                tableUser.removeUserByID(testUserId);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
}
