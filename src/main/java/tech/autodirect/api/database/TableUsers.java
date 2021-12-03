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
    private final Connection db_conn;
    private final String table_name = "users";

    public TableUsers(String db_name) throws SQLException {
        this.db_conn = Conn.getConn(db_name);
    }

    @Override
    public void addUser(
        String userId,
        int creditScore,
        double downPayment,
        double budgetMonthly,
        String offersTableName
    ) throws SQLException {
        PreparedStatement stmt = this.db_conn.prepareStatement(
            "INSERT INTO " + this.table_name + " VALUES (?, ?, ?, ?, ?);"
        );
        stmt.setString(1, userId);
        stmt.setInt(2, creditScore);
        stmt.setBigDecimal(3, BigDecimal.valueOf(downPayment));
        stmt.setBigDecimal(4, BigDecimal.valueOf(budgetMonthly));
        stmt.setString(5, offersTableName);

        // execute and close the above SQL statement
        stmt.executeUpdate();
        stmt.close();

        // TODO: What happens if user already exists?
        // TODO: What happens if there is no offers table for this user yet (which is usually the case)?
    }

    @Override
    public Map<String, Object> getUserByID(String userId) throws SQLException {
        // construct a prepared SQL statement selecting the specified user
        PreparedStatement stmt = this.db_conn.prepareStatement(
            "SELECT * FROM " + this.table_name + " WHERE user_id = ?;"
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
    public void removeUserByID(String userId) throws SQLException {
        // construct a prepared SQL statement selecting the specified user
        PreparedStatement stmt = this.db_conn.prepareStatement(
            "DELETE FROM " + this.table_name + " WHERE user_id = ?;"
        );
        stmt.setString(1, userId);

        // execute the above SQL statement
        stmt.executeUpdate();
        stmt.close();
    }

    @Override
    public boolean userExists(String userId) throws SQLException {
        // construct a prepared SQL statement selecting the specified user
        PreparedStatement stmt = this.db_conn.prepareStatement(
            "SELECT 1 FROM " + this.table_name + " WHERE user_id = ?;"
        );
        stmt.setString(1, userId);

        // execute the above SQL statement and check whether the user exists
        ResultSet rs = stmt.executeQuery();
        boolean user_count = resultSetToList(rs).size() > 0; // TODO: cant be more than 1, right? Check?
        stmt.close();
        return user_count;
    }
}
