package tech.autodirect.api.interfaces;

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
import java.sql.ResultSet;
import java.sql.SQLException;

public interface TableUsersInterface {
    /**
     * Add a user entry to the users table in the database.
     */
    public void addUser(
            String userId,
            int creditScore,
            BigDecimal downPayment,
            BigDecimal budgetMonthly,
            String offersTableName
    ) throws SQLException;

    /**
     * Get a specific user entry by the user ID.
     * @return A ResultSet containing the user entry.
     */
    public ResultSet getUserByID(String userId) throws SQLException;

    /**
     * Remove a specific user entry by the user ID.
     */
    public void removeUserByID(String userId) throws SQLException;
}
