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

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Map;

public class EntOffer {
    private int offerId;
    private int carId;
    private double loanAmount;
    private double capitalSum;
    private double interestSum;
    private double totalSum;
    private double interestRate;
    private double termMo;
    private String installments;
    private boolean claimed;

    /**
     * Populates EntOffer from a Map containing representing an offer entry in the database.
     *
     * @param entry : A Map containing representing an offer entry in the database.
     */
    public void loadFromMap(Map<String, Object> entry) throws SQLException {
        offerId = (int) entry.get("offer_id");
        carId = (int) entry.get("car_id");
        loanAmount = ((BigDecimal) entry.get("loan_amount")).doubleValue();
        capitalSum = ((BigDecimal) entry.get("capital_sum")).doubleValue();
        interestSum = ((BigDecimal) entry.get("interest_sum")).doubleValue();
        totalSum = ((BigDecimal) entry.get("total_sum")).doubleValue();
        interestRate = ((Float) entry.get("interest_rate")).doubleValue();
        termMo = ((Float) entry.get("term_mo")).doubleValue();
        installments = (String) entry.get("installments");
        claimed = (boolean) entry.get("claimed");
    }

    public int getOfferId() {
        return offerId;
    }

    public int getCarId() {
        return carId;
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public double getCapitalSum() {
        return capitalSum;
    }

    public double getInterestSum() {
        return interestSum;
    }

    public double getTotalSum() {
        return totalSum;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public double getTermMo() {
        return termMo;
    }

    public String getInstallments() {
        return installments;
    }

    public boolean isClaimed() {
        return claimed;
    }
}