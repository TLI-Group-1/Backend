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

import tech.autodirect.api.interfaces.TableOffersInterface;

import javax.management.InstanceAlreadyExistsException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.List;
import java.util.Map;

public class





TableOffers extends Table implements TableOffersInterface {
    public Connection dbConn;
    private String tableName = null;
    private final String[] tableColumns = {
        "car_id", "loan_amount", "capital_sum", "interest_sum", "total_sum",
        "interest_rate", "term_mo", "installments", "claimed"
    };

    /**
     * Create a new TableOffers object with a public database connection object.
     *
     * @param dbName : name of the database to connect to
     */
    public TableOffers(String dbName) throws SQLException {
        this.dbConn = Conn.getConn(dbName);
    }

    public String newTable(String userId) throws SQLException {
        // create the "offers" schema if it does not exist yet
        Statement stmtCreateSchema = this.dbConn.createStatement();
        stmtCreateSchema.executeUpdate(
            "CREATE SCHEMA IF NOT EXISTS offers;"
        );
        stmtCreateSchema.close();

        // construct the name of the table following "offers.offers_userid"
        this.tableName = "offers.offers_" + userId;

        // create and execute the SQL statement that will create an offer table
        Statement stmt = this.dbConn.createStatement();
        stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS " + this.tableName + " (" +
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

    public String getTableName() {
        return this.tableName;
    }

    public void useExistingTable(String tableName) throws InstanceAlreadyExistsException {
        if (this.tableName == null) {
            this.tableName = tableName;
        }
        else {
            throw new InstanceAlreadyExistsException(
                "This object already carries a table name. \n" +
                "You may not reuse the same TableOffers object for different tables. \n" +
                "Please create a new TableOffers object for an individual table."
            );
        }
    }

    public int addOffer(
        int carId,
        double loanAmount,
        double capitalSum,
        double interestSum,
        double totalSum,
        double interestRate,
        double termMo,
        String installments,
        boolean claimed
    ) throws SQLException {
        // construct a prepared SQL statement inserting the specified values
        PreparedStatement stmt = this.dbConn.prepareStatement(
            "INSERT INTO " + this.tableName + " (" +
                String.join(", ", tableColumns) +
            ")" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);"
        );
        stmt.setInt(1, carId);
        stmt.setBigDecimal(2, BigDecimal.valueOf(loanAmount));
        stmt.setBigDecimal(3, BigDecimal.valueOf(capitalSum));
        stmt.setBigDecimal(4, BigDecimal.valueOf(interestSum));
        stmt.setBigDecimal(5, BigDecimal.valueOf(totalSum));
        stmt.setDouble(6, interestRate);
        stmt.setDouble(7, termMo);
        stmt.setString(8, installments);
        stmt.setBoolean(9, claimed);

        // execute and close the above SQL statement
        stmt.executeUpdate();
        stmt.close();

        // retrieve the offer ID and save it to "offer_id"
        Statement stmt_lastval = this.dbConn.createStatement();
        ResultSet rs = stmt_lastval.executeQuery("SELECT LASTVAL();");
        rs.next();
        int offerId = rs.getInt("lastval");
        stmt.close();

        return offerId;
    }

    public void removeOfferByOfferId(int offerId) throws SQLException {
        // construct a prepared SQL statement deleting the specified offer
        PreparedStatement stmt = this.dbConn.prepareStatement(
            "DELETE FROM " + this.tableName + " WHERE offer_id = ?;"
        );
        stmt.setInt(1, offerId);

        // execute and close the above SQL statement
        stmt.executeUpdate();
        stmt.close();
    }

    public void removeAllOffers() throws SQLException {
        Statement stmt = this.dbConn.createStatement();
        stmt.executeUpdate("DELETE FROM " + this.tableName + ";");
        stmt.close();
    }

    public Map<String, Object> getOfferByOfferId(int offerId) throws SQLException {
        // construct a prepared SQL statement selecting the specified offer
        PreparedStatement stmt = this.dbConn.prepareStatement(
                "SELECT * FROM " + this.tableName + " WHERE offer_id = ?;"
        );
        stmt.setInt(1, offerId);

        // execute the above SQL statement and extract result into a Map
        ResultSet rs = stmt.executeQuery();
        Map<String, Object> offer = resultSetToList(rs).get(0);
        stmt.close();
        return offer;
    }

    public List<Map<String, Object>> getAllOffers() throws SQLException {
        // construct a SQL statement selecting all offers
        Statement stmt = this.dbConn.createStatement();
        ResultSet rs = stmt.executeQuery(
            "SELECT * FROM " + this.tableName + ";"
        );
        List<Map<String, Object>> offers = resultSetToList(rs);
        stmt.close();
        return offers;
    }

    public List<Map<String, Object>> getClaimedOffers() throws SQLException {
        // construct a prepared SQL statement selecting all offers
        // where "claimed" is true
        Statement stmt = this.dbConn.createStatement();
        ResultSet rs = stmt.executeQuery(
            "SELECT * FROM " + this.tableName + " WHERE 'claimed' = true;"
        );
        List<Map<String, Object>> offers = resultSetToList(rs);
        stmt.close();
        return offers;
    }

    public void markOfferClaimed(int offerId) throws SQLException {
        // construct a prepared SQL marking the specified offer claimed
        PreparedStatement stmt = this.dbConn.prepareStatement(
            "UPDATE " + this.tableName +
            " SET 'claimed' = true WHERE offer_id = ?;"
        );
        stmt.setInt(1, offerId);

        // execute and close the above SQL statement
        stmt.executeUpdate();
        stmt.close();
    }

    public void markOfferUnclaimed(int offerId) throws SQLException {
        // construct a prepared SQL marking the specified offer unclaimed
        PreparedStatement stmt = this.dbConn.prepareStatement(
            "UPDATE " + this.tableName +
            " SET 'claimed' = false WHERE offer_id = ?;"
        );
        stmt.setInt(1, offerId);

        // execute and close the above SQL statement
        stmt.executeUpdate();
        stmt.close();
    }

    public void dropTable() throws SQLException {
        // construct a prepared SQL marking the specified offer unclaimed and execute
        PreparedStatement stmt = this.dbConn.prepareStatement("DROP TABLE " + this.tableName + ";");
        stmt.executeUpdate();
        stmt.close();
    }

    public void dropTable(String tableName) throws SQLException {
        // construct a prepared SQL marking the specified offer unclaimed and execute
        PreparedStatement stmt = this.dbConn.prepareStatement("DROP TABLE " + tableName + ";");
        stmt.executeUpdate();
        stmt.close();
    }
}
