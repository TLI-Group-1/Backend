package tech.autodirect.api.services;/*
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

import tech.autodirect.api.interfaces.BankApiInterface;
import tech.autodirect.api.interfaces.TableUsersInterface;

import java.sql.SQLException;
import java.util.Map;

public class SvcUserLogin {
    private TableUsersInterface tableUsers;
    private BankApiInterface bankApi;

    public SvcUserLogin(TableUsersInterface tableUsers, BankApiInterface bankApi) {
        this.tableUsers = tableUsers;
        this.bankApi = bankApi;
    }

    /**
     * if the userid exists, retrieve the username's information, if it does not exist, create a new username
     *
     * @param userId: the user string that uniquely identifies a user, same as "user_id" in the public.users table
     * @return the user's
     */
    public Map<String, Object> loginUser(String userId) throws SQLException {
        if (tableUsers.checkUser(userId)) {
            // check if user ID exits in the table of users
            Map<String, Object> userInfo = tableUsers.getUserByID(userId);
            userInfo.remove("offers_table");
            return userInfo;
        } else {
            // creates a new user ID
            Map<String, Object> userInfo = tableUsers.addUser(
                    userId,
                    bankApi.getCreditScore(userId),
                    null,
                    null,
                    null
            );
            return userInfo;
        }
    }
}
