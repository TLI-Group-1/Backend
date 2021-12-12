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

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import tech.autodirect.api.interfaces.TableOffersInterface;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("SqlResolve")  // no need to connect to database in IDE
public class TableOffers extends Table implements TableOffersInterface {
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
        this.dbConn = Conn.getConn(dbName);
    }

    @Override
    public String setUser(String userId) throws SQLException {
        this.tableName = TableOffersInterface.createTableName(userId);

        // create the "offers" schema if it does not exist yet
        PreparedStatement stmtCreateSchema = this.dbConn.prepareStatement(
                "CREATE SCHEMA IF NOT EXISTS " + this.schemaName + " AUTHORIZATION tli;"
        );
        stmtCreateSchema.executeUpdate();
        stmtCreateSchema.close();

        // create and execute the SQL statement that will create an offer table
        PreparedStatement stmt = this.dbConn.prepareStatement(
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
        stmt.executeUpdate();
        stmt.close();

        return this.getTableName();
    }

    @Override
    public String getTableName() {
        return this.tableName;
    }

    @Override
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
        @SuppressWarnings("SqlInsertValues")  // inspector fails to recognize String.join
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
        PreparedStatement stmt_lastval = this.dbConn.prepareStatement("SELECT LASTVAL();");
        ResultSet rs = stmt_lastval.executeQuery();
        rs.next();
        int offerId = rs.getInt("lastval");
        stmt.close();

        return offerId;
    }

    @Override
    public void removeOfferByOfferId(int offerId) throws SQLException, ResponseStatusException {
        removeEntryById(offerId, schemaName, tableName, dbConn, "offer");
    }

    @Override
    public void removeAllOffers() throws SQLException {
        removeAllEntries(schemaName, tableName, dbConn);
    }

    @Override
    public Map<String, Object> getOfferByOfferId(int offerId) throws SQLException, ResponseStatusException {
        return getEntryById(offerId, schemaName, tableName, dbConn, "offer");
    }

    @Override
    public List<Map<String, Object>> getAllOffers() throws SQLException {
        return getAllEntries(schemaName, tableName, dbConn);
    }

    @Override
    public List<Map<String, Object>> getClaimedOffers() throws SQLException {
        // construct a prepared SQL statement selecting all offers
        // where "claimed" is true
        PreparedStatement stmt = this.dbConn.prepareStatement(
                "SELECT * FROM " + this.schemaName + "." + this.tableName + " WHERE claimed = true;"
        );
        ResultSet rs = stmt.executeQuery();
        List<Map<String, Object>> offers = resultSetToList(rs);
        stmt.close();
        return offers;
    }

    @Override
    public void markOfferClaimed(int offerId) throws SQLException, ResponseStatusException {
        if (!checkOfferExists(offerId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "offer not found"
            );
        }

        // construct a prepared SQL marking the specified offer claimed
        PreparedStatement stmt = this.dbConn.prepareStatement(
                "UPDATE " + this.schemaName + "." + this.tableName +
                        " SET claimed = true WHERE offer_id = ?;"
        );
        stmt.setInt(1, offerId);

        // execute and close the above SQL statement
        stmt.executeUpdate();
        stmt.close();
    }

    @Override
    public void markOfferUnclaimed(int offerId) throws SQLException, ResponseStatusException {
        if (!checkOfferExists(offerId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "offer not found"
            );
        }

        // construct a prepared SQL marking the specified offer unclaimed
        PreparedStatement stmt = this.dbConn.prepareStatement(
                "UPDATE " + this.schemaName + "." + this.tableName +
                        " SET claimed = false WHERE offer_id = ?;"
        );
        stmt.setInt(1, offerId);

        // execute and close the above SQL statement
        stmt.executeUpdate();
        stmt.close();
    }

    @Override
    public void dropTable() throws SQLException {
        dropTable(this.tableName);
    }

    @Override
    public void dropTable(String tableName) throws SQLException {
        PreparedStatement stmt = this.dbConn.prepareStatement(
                "DROP TABLE " + this.schemaName + "." + tableName + ";"
        );
        stmt.executeUpdate();
        stmt.close();
    }

    @Override
    public boolean checkTableExists() throws SQLException {
        return checkTableExists(this.tableName);
    }

    @Override
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

    @Override
    public boolean checkOfferExists(int offerId) throws SQLException {
        return checkEntryExists(offerId, schemaName, tableName, dbConn, "offer");
    }

    @Override
    public void updateOfferColumn(int userId, OfferColumns column, Object newValue) throws SQLException {
        if (column == OfferColumns.LOAN_AMOUNT) {
            updateEntryColumn(userId, schemaName, tableName, dbConn, "offer", "loan_amount", newValue);
        } else if (column == OfferColumns.CAPITAL_SUM) {
            updateEntryColumn(userId, schemaName, tableName, dbConn, "offer", "capital_sum", newValue);
        } else if (column == OfferColumns.INTEREST_SUM) {
            updateEntryColumn(userId, schemaName, tableName, dbConn, "offer", "interest_sum", newValue);
        } else if (column == OfferColumns.TOTAL_SUM) {
            updateEntryColumn(userId, schemaName, tableName, dbConn, "offer", "total_sum", newValue);
        } else if (column == OfferColumns.INTEREST_RATE) {
            updateEntryColumn(userId, schemaName, tableName, dbConn, "offer", "interest_rate", newValue);
        } else if (column == OfferColumns.TERM_MO) {
            updateEntryColumn(userId, schemaName, tableName, dbConn, "offer", "term_mo", newValue);
        } else if (column == OfferColumns.INSTALLMENTS) {
            updateEntryColumn(userId, schemaName, tableName, dbConn, "offer", "installments", newValue);
        }
    }
}
