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

import javax.management.InstanceAlreadyExistsException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface TableOffersInterface {
    /**
     * Create a new offers table for a given user inside the "offers" schema.
     *
     * @param user_id : the user string that uniquely identifies a user, same as
     *     "user_id" in the public.users table
     * @return : the name of the newly created offers table, follows
     *     "offers.offers_<userid>" format
     */
    public String newTable(String user_id) throws SQLException;

    /**
     * Public getter for the "table_name" property.
     *
     * @return : the name of the offers table, follows "offers.offers_<userid>" format
     */
    public String getTableName();

    /**
     * Public setter for the "table_name" property.
     * Use in place of newTable() when operating on an existing offers table.
     *
     * @param table_name : name of the table to connect to, follows
     *     "offers.offers_<userid>" format
     * @throws InstanceAlreadyExistsException : when trying to set "table_name" but the
     *     object already carries a "table_name", refuse to proceed
     */
    public void useExistingTable(String table_name) throws InstanceAlreadyExistsException;

    /**
     * Add a new offer in the current offers table.
     *
     * @return : integer representing the offer ID of the newly inserted offer row
     */
    public int addOffer(
            int car_id,
            double loan_amount,
            double capital_sum,
            double interest_sum,
            double total_sum,
            double interest_rate,
            double term_mo,
            String installments,
            boolean claimed
    ) throws SQLException;

    /**
     * Remove an offer row given an offer ID.
     *
     * @param offer_id : ID of the offer to be removed
     */
    public void removeOfferByOfferId(int offer_id) throws SQLException;

    /**
     * Remove all offers in the current offers table.
     */
    public void removeAllOffers() throws SQLException;

    /**
     * Retrieve an offer in HashMap format given an offer ID.
     *
     * @param offer_id : ID of the offer to be retrieved
     * @return A Map representing an offer entry in the database.
     */
    public Map<String, Object> getOfferByOfferId(int offer_id) throws SQLException;

    /**
     * Retrieve all offers in the current offers table.
     *
     * @return A List of Maps where each Map is a single offer entry in the database.
     */
    public List<Map<String, Object>> getAllOffers() throws SQLException;

    /**
     * Retrieve all offers in the current offers table whose "claimed" field is set to
     * true.
     *
     * @return A List of Maps where each Map is a single offer entry in the database.
     */
    public List<Map<String, Object>> getClaimedOffers() throws SQLException;

    /**
     * Set the "claimed" field of a given offer to true.
     *
     * @param offer_id : ID of the offer to be marked as claimed
     */
    public void markOfferClaimed(int offer_id) throws SQLException;

    /**
     * Set the "claimed" field of a given offer to false.
     *
     * @param offer_id : ID of the offer to be marked as unclaimed
     */
    public void markOfferUnclaimed(int offer_id) throws SQLException;

    /**
     * Delete the current offers table.
     */
    public void dropTable() throws SQLException;

    /**
     * Delete the offers table given by tableName.
     */
    public void dropTable(String tableName) throws SQLException;
}
