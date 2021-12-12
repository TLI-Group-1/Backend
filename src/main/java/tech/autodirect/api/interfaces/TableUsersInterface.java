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

import java.sql.SQLException;
import java.util.Map;

public interface TableUsersInterface {
    enum UserColumns {
        BUDGET_MO, DOWN_PAYMENT
    }

    /**
     * Add a user entry to the users table in the database.
     */
    void addUser(
            String userId,
            int creditScore,
            double downPayment,
            double budgetMonthly
    ) throws SQLException, ClassNotFoundException;

    /**
     * Get a specific user entry by the user ID.
     *
     * @return A Map representing a user entry in the database.
     */
    Map<String, Object> getUserById(String userId) throws SQLException;

    /**
     * Remove a specific user entry by the user ID.
     */
    void removeUserById(String userId) throws SQLException;

    /**
     * Check if user exists in database.
     */
    boolean checkUserExists(String userId) throws SQLException;

    /**
     * Update the column value for this user.
     */
    void updateUserColumn(String userId, UserColumns column, Object newValue) throws SQLException;
}
