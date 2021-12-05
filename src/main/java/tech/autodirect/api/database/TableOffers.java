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
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TableOffers extends Table implements TableOffersInterface {
    private final String dbName;
    public Connection dbConn;
    private String tableName = null;
    private final String schemaName = "offers";
    private final String[] tableColumns = {
        "car_id", "loan_amount", "capital_sum", "interest_sum", "total_sum",
        "interest_rate", "term_mo", "installments", "claimed"
    };

    /**
     * Create a new TableOffers object with a public database connection object.
     *
     * @param dbName : name of the database to connect to
     */
    public TableOffers(String dbName) throws SQLException, ClassNotFoundException {
        this.dbName = dbName;
        this.dbConn = Conn.getConn(dbName);
    }

    public String newTable(String userId) throws SQLException {
        this.tableName = TableOffersInterface.createTableName(userId);

        // create the "offers" schema if it does not exist yet
        Statement stmtCreateSchema = this.dbConn.createStatement();
        stmtCreateSchema.executeUpdate(
            "CREATE SCHEMA IF NOT EXISTS offers;"
        );
        stmtCreateSchema.close();

        // create and execute the SQL statement that will create an offer table
        Statement stmt = this.dbConn.createStatement();
        stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS " + this.schemaName + "." + this.tableName + " (" +
                "offer_id       serial      NOT NULL PRIMARY KEY, " +
                "car_id         integer     NOT NULL, " +
                "loan_amount    decimal(12) NOT NULL, " +
                "capital_sum    decimal(12) NOT NULL, " +
                "interest_sum   decimal(12) NOT NULL, " +
                "total_sum      decimal(12) NOT NULL, " +
                "interest_rate  real        NOT NULL, " +
                "term_mo        real        NOT NULL, " +
                "installments   varchar(10000) NOT NULL, " +
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
            "INSERT INTO " + this.schemaName + "." + this.tableName + " (" +
                String.join(", ", tableColumns) +
            ")" + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);"
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
            "DELETE FROM " + this.schemaName + "." + this.tableName + " WHERE offer_id = ?;"
        );
        stmt.setInt(1, offerId);

        // execute and close the above SQL statement
        stmt.executeUpdate();
        stmt.close();
    }

    public void removeAllOffers() throws SQLException {
        Statement stmt = this.dbConn.createStatement();
        stmt.executeUpdate("DELETE FROM " + this.schemaName + "." + this.tableName + ";");
        stmt.close();
    }

    public Map<String, Object> getOfferByOfferId(int offerId) throws SQLException {
        // construct a prepared SQL statement selecting the specified offer
        PreparedStatement stmt = this.dbConn.prepareStatement(
                "SELECT * FROM " + this.schemaName + "." + this.tableName + " WHERE offer_id = ?;"
        );
        stmt.setInt(1, offerId);

        ResultSet rs = stmt.executeQuery();
        List<Map<String, Object>> rsList = resultSetToList(rs);
        stmt.close();
        if (rsList.size() == 0) {
            return Collections.emptyMap();
        } else {
            return rsList.get(0);
        }
    }

    public List<Map<String, Object>> getAllOffers() throws SQLException {
        // construct a SQL statement selecting all offers
        Statement stmt = this.dbConn.createStatement();
        ResultSet rs = stmt.executeQuery(
            "SELECT * FROM " + this.schemaName + "." + this.tableName + ";"
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
            "SELECT * FROM " + this.schemaName + "." + this.tableName + " WHERE 'claimed' = true;"
        );
        List<Map<String, Object>> offers = resultSetToList(rs);
        stmt.close();
        return offers;
    }

    public void markOfferClaimed(int offerId) throws SQLException {
        // construct a prepared SQL marking the specified offer claimed
        PreparedStatement stmt = this.dbConn.prepareStatement(
            "UPDATE " + this.schemaName + "." + this.tableName +
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
            "UPDATE " + this.schemaName + "." + this.tableName +
            " SET 'claimed' = false WHERE offer_id = ?;"
        );
        stmt.setInt(1, offerId);

        // execute and close the above SQL statement
        stmt.executeUpdate();
        stmt.close();
    }

    public boolean dropTable() throws SQLException {
        return dropTable(this.tableName);
    }

    public boolean dropTable(String tableName) throws SQLException {
        if (checkTableExists(tableName)) {
            PreparedStatement stmt = this.dbConn.prepareStatement(
                    "DROP TABLE " + this.schemaName + "." + tableName + ";"
            );
            stmt.executeUpdate();
            stmt.close();
            return true;
        } else {
            return false;
        }
    }

    public boolean checkTableExists() throws SQLException {
        return checkTableExists(this.tableName);
    }

    public boolean checkTableExists(String tableName) throws SQLException {
        PreparedStatement stmt = this.dbConn.prepareStatement(
                "SELECT EXISTS (" +
                        "SELECT * " +
                        "FROM   information_schema.tables " +
                        "WHERE  table_schema = 'offers'" +
                        "   and table_name= '" + tableName.toLowerCase(Locale.ROOT) + "'" +
                ");"
        );

        ResultSet rs = stmt.executeQuery();
        rs.next();
        boolean exists = rs.getBoolean("exists");
        stmt.close();

        return exists;
    }

}
