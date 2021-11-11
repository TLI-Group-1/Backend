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

    public TableOffers(String db_name) throws SQLException {
        // create a database connection object based on supplied database name
        this.db_conn = Conn.getConn(db_name);
    }

    public void newTable(String user_id) throws SQLException {
        // create the "offers" schema if it does not exist yet
        Statement stmt_create_schema = this.db_conn.createStatement();
        stmt_create_schema.executeUpdate(
        "CREATE SCHEMA IF NOT EXISTS offers;"
        );
        stmt_create_schema.close();

        // construct the name of the table following "offers.offer_userid"
        this.table_name = "offers.offer_" + user_id;

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
    }

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

        // retrieve the offer ID
        Statement stmt_lastval = this.db_conn.createStatement();
        ResultSet rs = stmt_lastval.executeQuery("SELECT LASTVAL();");
        rs.next();
        int offer_id = rs.getInt("lastval");
        stmt.close();

        return offer_id;
    }

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

    public void removeAllOffers() throws SQLException {
        Statement stmt = this.db_conn.createStatement();
        stmt.executeUpdate("DELETE FROM " + this.table_name + ";");
        stmt.close();
    }

    private static HashMap offerResultToHashMap(ResultSet rs) throws SQLException {
        return new HashMap<String, Object>() {{
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

    public HashMap getOfferByOfferId(int offer_id) throws SQLException {
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

    public ArrayList getAllOffers() throws SQLException {
        // construct a prepared SQL statement selecting all offers
        Statement stmt = this.db_conn.createStatement();
        ResultSet rs = stmt.executeQuery(
            "SELECT * FROM " + this.table_name + ";"
        );

        // loop through all results and append their HashMap to an ArrayList
        ArrayList<HashMap> offers_list = new ArrayList<>();
        while (rs.next()) {
            offers_list.add(offerResultToHashMap(rs));
        }
        stmt.close();

        return offers_list;
    }

//    public Array getClaimedOffers() {
//
//    }
//
//    public void markOfferClaimed() {
//
//    }
//
//    public void markOfferUnclaimed() {
//
//    }
}
