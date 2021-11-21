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
import java.sql.ResultSet;
import java.sql.SQLException;

public class EntOffer {
    private int offerId;
    private int carId;
    private BigDecimal loanAmount;
    private BigDecimal capitalSum;
    private BigDecimal interestSum;
    private BigDecimal totalSum;
    private double interestRate;
    private double termMo;
    private String installments;
    private boolean claimed;

    /**
     * Populates EntOffer from a JDBC result containing an offer.
     *
     * @param rs : ResultSet object already pointing to a result row, containing an offer
     */
    public void loadResultSet(ResultSet rs) throws SQLException {
        offerId = rs.getInt("offer_id");
        carId = rs.getInt("car_id");
        loanAmount = rs.getBigDecimal("loan_amount");
        capitalSum = rs.getBigDecimal("capital_sum");
        interestSum = rs.getBigDecimal("interest_sum");
        totalSum = rs.getBigDecimal("total_sum");
        interestRate = rs.getDouble("interest_rate");
        termMo = rs.getDouble("term_mo");
        installments = rs.getString("installments");
        claimed = rs.getBoolean("claimed");
    }

    public int getOfferId() {
        return offerId;
    }

    public int getCarId() {
        return carId;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public BigDecimal getCapitalSum() {
        return capitalSum;
    }

    public BigDecimal getInterestSum() {
        return interestSum;
    }

    public BigDecimal getTotalSum() {
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