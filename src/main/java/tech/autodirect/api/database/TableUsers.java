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
import tech.autodirect.api.interfaces.TableUsersInterface;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TableUsers extends Table implements TableUsersInterface {
    private final String dbName;
    private final Connection dbConn;
    private final String schemaName = "public";
    private final String tableName = "users";

    /**
     * Create a new TableUsers object with a public database connection object.
     *
     * @param dbName : name of the database to connect to
     */
    public TableUsers(String dbName) throws SQLException, ClassNotFoundException {
        this.dbName = dbName;
        this.dbConn = Conn.getConn(dbName);
    }

    @Override
    public void addUser(
            String userId,
            int creditScore,
            double downPayment,
            double budgetMonthly
    ) throws SQLException, ClassNotFoundException {
        PreparedStatement stmt = this.dbConn.prepareStatement(
                "INSERT INTO " + this.schemaName + "." + this.tableName + " VALUES (?, ?, ?, ?, ?);"
        );
        stmt.setString(1, userId);
        stmt.setInt(2, creditScore);
        stmt.setBigDecimal(3, BigDecimal.valueOf(downPayment));
        stmt.setBigDecimal(4, BigDecimal.valueOf(budgetMonthly));
        stmt.setString(5, TableOffersInterface.createTableName(userId));

        // Create offers for this user
        TableOffersInterface tableOffers = new TableOffers(dbName);
        tableOffers.setUser(userId);

        // execute and close the above SQL statement
        stmt.executeUpdate();
        stmt.close();
    }

    @Override
    public Map<String, Object> getUserById(String userId) throws SQLException {
        if (!checkUserExists(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "user not found"
            );
        }

        // construct a prepared SQL statement selecting the specified user
        PreparedStatement stmt = this.dbConn.prepareStatement(
                "SELECT * FROM " + this.schemaName + "." + this.tableName + " WHERE user_id = ?;"
        );
        stmt.setString(1, userId);

        ResultSet rs = stmt.executeQuery();
        List<Map<String, Object>> rsList = resultSetToList(rs);
        stmt.close();
        if (rsList.size() == 0) {
            return Collections.emptyMap();
        } else {
            return rsList.get(0);
        }
    }

    @Override
    public void removeUserById(String userId) throws SQLException {
        if (!checkUserExists(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "user not found"
            );
        }

        // construct a prepared SQL statement selecting the specified user
        PreparedStatement stmt = this.dbConn.prepareStatement(
                "DELETE FROM " + this.schemaName + "." + this.tableName + " WHERE user_id = ?;"
        );
        stmt.setString(1, userId);

        // execute the above SQL statement
        stmt.executeUpdate();
        stmt.close();
    }

    @Override
    public boolean checkUserExists(String userId) throws SQLException {
        // construct a prepared SQL statement selecting the specified user
        PreparedStatement stmt = this.dbConn.prepareStatement(
                "SELECT 1 FROM " + this.schemaName + "." + this.tableName + " WHERE user_id = ?;"
        );
        stmt.setString(1, userId);

        // execute the above SQL statement and check whether the user exists
        ResultSet rs = stmt.executeQuery();
        boolean userCount = resultSetToList(rs).size() > 0; // TODO: cant be more than 1, right? Check?
        stmt.close();
        return userCount;
    }
}
