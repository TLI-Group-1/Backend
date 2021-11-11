package tech.autodirect.api.database;

/*
Copyright (c) 2021 Ruofan Chen, Samm Du, Nada Eldin, Shalev Lifshitz

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

import javax.management.InstanceAlreadyExistsException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TableOffers {
    public Connection db_conn;
    private String table_name = null;
    private final String[] table_columns = {
        "car_id", "loan_amount", "capital_sum", "interest_sum", "total_sum",
        "interest_rate", "term_mo", "installments", "claimed"
    };

    /**
     * Create a new TableOffers object with a public database connection object.
     *
     * @param db_name : name of the database to connect to
     */
    public TableOffers(String db_name) throws SQLException {
        this.db_conn = Conn.getConn(db_name);
    }

    /**
     * Create a new offers table for a given user inside the "offers" schema.
     *
     * @param user_id : the user string that uniquely identifies a user, same as
     *     "user_id" in the public.users table
     * @return : the name of the newly created offers table, follows
     *     "offers.offers_<userid>" format
     */
    public String newTable(String user_id) throws SQLException {
        // create the "offers" schema if it does not exist yet
        Statement stmt_create_schema = this.db_conn.createStatement();
        stmt_create_schema.executeUpdate(
            "CREATE SCHEMA IF NOT EXISTS offers;"
        );
        stmt_create_schema.close();

        // construct the name of the table following "offers.offers_userid"
        this.table_name = "offers.offers_" + user_id;

        // create and execute the SQL statement that will create an offer table
        Statement stmt = this.db_conn.createStatement();
        stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS " + this.table_name + " (" +
                "offer_id       serial      NOT NULL PRIMARY KEY, " +
                "car_id         integer     NOT NULL, " +
                "loan_amount    decimal(12) NOT NULL, " +
                "capital_sum    decimal(12) NOT NULL, " +
                "interest_sum   decimal(12) NOT NULL, " +
                "total_sum      decimal(12) NOT NULL, " +
                "interest_rate  real        NOT NULL, " +
                "term_mo        real        NOT NULL, " +
                "installments   jsonb       NOT NULL, " +
                "claimed        boolean     NOT NULL" +
            ");"
        );
        stmt.close();

        return this.getTableName();
    }

    /**
     * Public getter for the "table_name" property.
     *
     * @return : the name of the offers table, follows "offers.offers_<userid>" format
     */
    public String getTableName() {
        return this.table_name;
    }

    /**
     * Public setter for the "table_name" property.
     * Use in place of newTable() when operating on an existing offers table.
     *
     * @param table_name : name of the table to connect to, follows
     *     "offers.offers_<userid>" format
     * @throws InstanceAlreadyExistsException : when trying to set "table_name" but the
     *     object already carries a "table_name", refuse to proceed
     */
    public void useExistingTable(String table_name) throws InstanceAlreadyExistsException {
        if (this.table_name == null) {
            this.table_name = table_name;
        }
        else {
            throw new InstanceAlreadyExistsException(
                "This object already carries a table name. \n" +
                "You may not reuse the same TableOffers object for different tables. \n" +
                "Please create a new TableOffers object for an individual table."
            );
        }
    }

    /**
     * Add a new offer in the current offers table.
     *
     * @return : integer representing the offer ID of the newly inserted offer row
     */
    public int addOffer(
        int car_id,
        BigDecimal loan_amount,
        BigDecimal capital_sum,
        BigDecimal interest_sum,
        BigDecimal total_sum,
        double interest_rate,
        double term_mo,
        String installments,
        boolean claimed
    ) throws SQLException {
        // construct a prepared SQL statement inserting the specified values
        PreparedStatement stmt = this.db_conn.prepareStatement(
            "INSERT INTO " + this.table_name + " (" +
                String.join(", ", table_columns) +
            ")" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);"
        );
        stmt.setInt(1, car_id);
        stmt.setBigDecimal(2, loan_amount);
        stmt.setBigDecimal(3, capital_sum);
        stmt.setBigDecimal(4, interest_sum);
        stmt.setBigDecimal(5, total_sum);
        stmt.setDouble(6, interest_rate);
        stmt.setDouble(7, term_mo);
        stmt.setString(8, installments);
        stmt.setBoolean(9, claimed);

        // execute and close the above SQL statement
        stmt.executeUpdate();
        stmt.close();

        // retrieve the offer ID and save it to "offer_id"
        Statement stmt_lastval = this.db_conn.createStatement();
        ResultSet rs = stmt_lastval.executeQuery("SELECT LASTVAL();");
        rs.next();
        int offer_id = rs.getInt("lastval");
        stmt.close();

        return offer_id;
    }

    /**
     * Remove an offer row given an offer ID.
     *
     * @param offer_id : ID of the offer to be removed
     */
    public void removeOfferByOfferId(int offer_id) throws SQLException {
        // construct a prepared SQL statement deleting the specified offer
        PreparedStatement stmt = this.db_conn.prepareStatement(
            "DELETE FROM " + this.table_name + " WHERE offer_id = ?;"
        );
        stmt.setInt(1, offer_id);

        // execute and close the above SQL statement
        stmt.executeUpdate();
        stmt.close();
    }

    /**
     * Remove all offers in the current offers table.
     */
    public void removeAllOffers() throws SQLException {
        Statement stmt = this.db_conn.createStatement();
        stmt.executeUpdate("DELETE FROM " + this.table_name + ";");
        stmt.close();
    }

    /**
     * Helper method that extracts a HashMap object from a JDBC result containing an offer
     * row.
     *
     * @param rs : ResultSet object already pointing to a result row, containing an offer
     * @return : HashMap representation of the current offer result
     */
    private static HashMap<String, Object> offerResultToHashMap(ResultSet rs) throws SQLException {
        return new HashMap<>() {{
            put("offer_id", rs.getInt("offer_id"));
            put("car_id", rs.getInt("car_id"));
            put("loan_amount", rs.getBigDecimal("loan_amount"));
            put("capital_sum", rs.getBigDecimal("capital_sum"));
            put("interest_sum", rs.getBigDecimal("interest_sum"));
            put("total_sum", rs.getBigDecimal("total_sum"));
            put("interest_rate", rs.getDouble("interest_rate"));
            put("term_mo", rs.getDouble("term_mo"));
            put("installments", rs.getString("installments"));
            put("claimed", rs.getBoolean("claimed"));
        }};
    }

    /**
     * Retrieve an offer in HashMap format given an offer ID.
     *
     * @param offer_id : ID of the offer to be retrieved
     * @return : HashMap representation of the retrieved offer result
     */
    public HashMap<String, Object> getOfferByOfferId(int offer_id) throws SQLException {
        // construct a prepared SQL statement selecting the specified offer
        PreparedStatement stmt = this.db_conn.prepareStatement(
            "SELECT FROM " + this.table_name + " WHERE offer_id = ?;"
        );
        stmt.setInt(1, offer_id);

        // execute the above SQL statement and extract result into a HashMap
        ResultSet rs = stmt.executeQuery();
        rs.next();
        HashMap<String, Object> offer_result = offerResultToHashMap(rs);
        stmt.close();

        return offer_result;
    }

    /**
     * Retrieve all offers in the current offers table.
     *
     * @return : ArrayList of HashMaps, each representing a retrieved offer
     */
    public ArrayList<HashMap<String, Object>> getAllOffers() throws SQLException {
        // construct a SQL statement selecting all offers
        Statement stmt = this.db_conn.createStatement();
        ResultSet rs = stmt.executeQuery(
            "SELECT * FROM " + this.table_name + ";"
        );

        // loop through all results and append their HashMap to an ArrayList
        ArrayList<HashMap<String, Object>> offers_list = new ArrayList<>();
        while (rs.next()) {
            offers_list.add(offerResultToHashMap(rs));
        }
        stmt.close();

        return offers_list;
    }

    /**
     * Retrieve all offers in the current offers table whose "claimed" field is set to
     * true.
     *
     * @return : ArrayList of HashMaps, each representing a retrieved offer
     */
    public ArrayList<HashMap<String, Object>> getClaimedOffers() throws SQLException {
        // construct a prepared SQL statement selecting all offers
        // where "claimed" is true
        Statement stmt = this.db_conn.createStatement();
        ResultSet rs = stmt.executeQuery(
            "SELECT * FROM " + this.table_name + " WHERE 'claimed' = true;"
        );

        // loop through all results and append their HashMap to an ArrayList
        ArrayList<HashMap<String, Object>> offers_list = new ArrayList<>();
        while (rs.next()) {
            offers_list.add(offerResultToHashMap(rs));
        }
        stmt.close();

        return offers_list;
    }

    /**
     * Set the "claimed" field of a given offer to true.
     *
     * @param offer_id : ID of the offer to be marked as claimed
     */
    public void markOfferClaimed(int offer_id) throws SQLException {
        // construct a prepared SQL marking the specified offer claimed
        PreparedStatement stmt = this.db_conn.prepareStatement(
            "UPDATE " + this.table_name +
            " SET 'claimed' = true WHERE offer_id = ?;"
        );
        stmt.setInt(1, offer_id);

        // execute and close the above SQL statement
        stmt.executeUpdate();
        stmt.close();
    }

    /**
     * Set the "claimed" field of a given offer to false.
     *
     * @param offer_id : ID of the offer to be marked as unclaimed
     */
    public void markOfferUnclaimed(int offer_id) throws SQLException {
        // construct a prepared SQL marking the specified offer unclaimed
        PreparedStatement stmt = this.db_conn.prepareStatement(
            "UPDATE " + this.table_name +
            " SET 'claimed' = false WHERE offer_id = ?;"
        );
        stmt.setInt(1, offer_id);

        // execute and close the above SQL statement
        stmt.executeUpdate();
        stmt.close();
    }
}
