package tech.autodirect.api.services;

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
import tech.autodirect.api.interfaces.BankApiInterface;
import tech.autodirect.api.interfaces.TableUsersInterface;

import java.sql.SQLException;
import java.util.Map;

/**
 * Responsible for handling user login.
 */
public class SvcUserLogin {

    /**
     * If the userid exists, retrieve the userId's information, if it does not exist, create a new userId.
     *
     * @param userId: the userId that uniquely identifies a user, same as "user_id" in the public.users table
     * @return the user's information that is stored in the database (excluding their offersTableName)
     */
    public Map<String, Object> loginUser(
            TableUsersInterface tableUsers,
            BankApiInterface bankApi,
            String userId
    ) throws SQLException, ClassNotFoundException, ResponseStatusException {
        if (userId.equals("")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "empty userId"
            );
        }

        if (tableUsers.checkUserExists(userId)) {
            // userId exists, return existing user info
            Map<String, Object> userMap = tableUsers.getUserById(userId);
            userMap.remove("offers_table");
            return userMap;
        } else {
            // userId does not exist, create new user with default info
            SvcMockBankApi svcMockBankApi = new SvcMockBankApi();
            int creditScore = svcMockBankApi.getCreditScore(bankApi, userId);
            double defaultDownPayment = 1000;
            double defaultBudgetMo = 250;
            tableUsers.addUser(userId, creditScore, defaultDownPayment, defaultBudgetMo);

            // Return user info from database
            Map<String, Object> userMap = tableUsers.getUserById(userId);
            userMap.remove("offers_table");
            return userMap;
        }
    }
}
