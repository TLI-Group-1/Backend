package tech.autodirect.api.entities;

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

public class EntUser {
    private String userId;
    private int creditScore;
    private double downPayment;
    private double budgetMo;
    private String offersTable;

    /**
     * Populates EntUser from a Map containing representing a user entry in the database.
     *
     * @param entry : A Map containing representing a user entry in the database.
     */
    public void loadFromList(Map<String, Object> entry) throws SQLException {
        userId = (String) entry.get("user_id");
        creditScore = (int) entry.get("credit_score");
        downPayment = (double) entry.get("down_payment");
        budgetMo = (double) entry.get("budget_mo");
        offersTable = (String) entry.get("offers_table");
    }

    public String getUserId() {
        return userId;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public double getDownPayment() {
        return downPayment;
    }

    public double getBudgetMo() {
        return budgetMo;
    }

    public String getOffersTable() {
        return offersTable;
    }
}
