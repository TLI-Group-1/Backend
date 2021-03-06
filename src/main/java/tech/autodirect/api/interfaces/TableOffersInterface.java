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
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * TableOffersInterface defines the behaviour of classes that interact with an offers table for a specific user.
 * setUser() must be called before any other method.
 */
public interface TableOffersInterface {
    enum OfferColumns {
        LOAN_AMOUNT, CAPITAL_SUM, INTEREST_SUM, TOTAL_SUM, INTEREST_RATE, TERM_MO, INSTALLMENTS
    }

    /**
     * Sets this object to refer to userId's offers table (creates offers table if it does not exist)
     *
     * @param userId : the user string that uniquely identifies a user, same as "userId" in the public.users table
     * @return : the name of the newly created offers table, follows TableOffersInterface.createTableName(userId).
     */
    String setUser(String userId) throws SQLException;

    /**
     * Public getter for the "tableName" property.
     *
     * @return : the name of the offers table, follows TableOffersInterface.createTableName(userId).
     */
    String getTableName();

    /**
     * Add a new offer in the current offers table.
     *
     * @return : integer representing the offer ID of the newly inserted offer row
     */
    int addOffer(
            int carId,
            double loanAmount,
            double capitalSum,
            double interestSum,
            double totalSum,
            double interestRate,
            double termMo,
            String installments,
            boolean claimed
    ) throws SQLException;

    /**
     * Remove an offer row given an offer ID.
     *
     * @param offerId : ID of the offer to be removed
     */
    void removeOfferByOfferId(int offerId) throws SQLException;

    /**
     * Remove all offers in the current offers table.
     */
    void removeAllOffers() throws SQLException;

    /**
     * Retrieve an offer in Map format given an offer ID.
     *
     * @param offerId : ID of the offer to be retrieved
     * @return A Map representing an offer entry in the database.
     */
    Map<String, Object> getOfferByOfferId(int offerId) throws SQLException;

    /**
     * Retrieve all offers in the current offers table.
     *
     * @return A List of Maps where each Map is a single offer entry in the database.
     */
    List<Map<String, Object>> getAllOffers() throws SQLException;

    /**
     * Retrieve all offers in the current offers table whose "claimed" field is set to
     * true.
     *
     * @return A List of Maps where each Map is a single offer entry in the database.
     */
    List<Map<String, Object>> getClaimedOffers() throws SQLException;

    /**
     * Set the "claimed" field of a given offer to true.
     *
     * @param offer_id : ID of the offer to be marked as claimed
     */
    void markOfferClaimed(int offer_id) throws SQLException;

    /**
     * Set the "claimed" field of a given offer to false.
     *
     * @param offer_id : ID of the offer to be marked as unclaimed
     */
    void markOfferUnclaimed(int offer_id) throws SQLException;

    /**
     * Delete the current offers table given by the "tableName" property.
     *
     */
    void dropTable() throws SQLException;

    /**
     * Delete the offers table given by tableName.
     *
     */
    void dropTable(String tableName) throws SQLException;

    /**
     * Create and return the offers table name for the given userId.
     * <p>
     * This is a very important method since, without it, we need to manually create the offers table name.
     * This means that if we decide to change how we create the offers table name, we must change that everywhere.
     * So, this method prevents that and also prevents issues that might arise when getting the name creation
     * process for an offers table incorrect. Anyway, this is important, please use it!
     */
    static String createTableName(String userId) throws SQLException {
        return "offers_" + userId.toLowerCase(Locale.ROOT);
    }

    /**
     * Return whether the current offers table given by the "tableName" property exists.
     */
    boolean checkTableExists() throws SQLException;

    /**
     * Return whether the offers table given by the tableName exists.
     */
    boolean checkTableExists(String tableName) throws SQLException;

    /**
     * Return whether a specific offer exists (by offerId).
     */
    boolean checkOfferExists(int offerId) throws SQLException;

    /**
     * Update the column's value in the offer corresponding to offerId.
     */
    void updateOfferColumn(int offerId, OfferColumns column, Object newValue) throws SQLException;
}
