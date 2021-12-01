package tech.autodirect.api.database;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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


    /**
     * Drop the entire "offers" schema from the testing database
     */
    @AfterAll public static void cleanupOffersTables() throws SQLException {
//        Connection db_conn = Conn.getConn(db_name);
//        Statement stmt = db_conn.createStatement();
//        stmt.executeUpdate("DROP SCHEMA offers CASCADE;");
//        stmt.close();
    }
}
