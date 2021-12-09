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
import tech.autodirect.api.entities.EntCar;
import tech.autodirect.api.entities.EntOffer;
import tech.autodirect.api.entities.EntUser;
import tech.autodirect.api.interfaces.SensoApiInterface;
import tech.autodirect.api.interfaces.TableCarsInterface;
import tech.autodirect.api.interfaces.TableOffersInterface;
import tech.autodirect.api.interfaces.TableUsersInterface;
import tech.autodirect.api.utils.MergeCarAndOffer;
import tech.autodirect.api.utils.ParseChecker;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

/**
 * Responsible for searching through all cars given a user's ID and search settings.
 */
public class SvcSearch {
    private final TableCarsInterface tableCars;
    private final TableUsersInterface tableUsers;
    private final TableOffersInterface tableOffers;
    private final SensoApiInterface sensoApi;
    private final List<String> valuesOfSortBy
            = Arrays.asList("price", "payment_mo", "interest_rate", "total_sum", "term_mo");
    private final List<String> valuesOfSortAsc = Arrays.asList("true", "false");

    public SvcSearch(
        TableCarsInterface tableCars,
        TableUsersInterface tableUsers,
        TableOffersInterface tableOffers,
        SensoApiInterface sensoApi
    ) {
        this.tableCars = tableCars;
        this.tableUsers = tableUsers;
        this.tableOffers = tableOffers;
        this.sensoApi = sensoApi;
    }

    /**
     * Perform a car search. If userId is not empty string or "null", only get cars which have offers for this user
     * (post-login search). Otherwise, get all cars (pre-login search).
     *
     * Note that default value for sortBy is "price" (used when sortBy is invalid format)
     * and default value for sortAsc is "true" (used when sortAsc is invalid format).
     */
    public List<Map<String, Object>> search(
        String userId,
        String downPaymentString,
        String budgetMoString,
        String sortBy,
        String sortAscString
    ) throws SQLException, IOException, InterruptedException, ResponseStatusException {
        // Check good sort params. If yes, convert sortAscString to boolean sortAsc.
        checkGoodSortParams(sortBy, sortAscString);
        boolean sortAsc = Boolean.parseBoolean(sortAscString);

        if (userId.equals("") || userId.equals("null")) {
            // If no userId, run pre-login search (return all cars)
            return searchAllCars(sortBy, sortAsc);
        } else {
            // User is logged in and search params are valid. So, only return cars which have offers for this user.
            // However, check good downPaymentString and budgetMoString params beforehand.
            checkGoodMoneyParams(downPaymentString, budgetMoString);
            double downPayment = Double.parseDouble(downPaymentString);
            double budgetMo = Double.parseDouble(budgetMoString);
            return searchOnlyCarsWithOffer(userId, downPayment, budgetMo, sortBy, sortAsc);
        }
    }

    /**
     * Check that sortBy and sortAsc search params are valid. Throw descriptive 400 ERROR if not.
     */
    public void checkGoodSortParams(
            String sortBy,
            String sortAsc
    ) throws ResponseStatusException {
        if (!valuesOfSortBy.contains(sortBy)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "invalid sortBy value, must be one of " + valuesOfSortBy
            );
        }
        if (!valuesOfSortAsc.contains(sortAsc)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "invalid sortAsc value, must be one of " + valuesOfSortAsc
            );
        }
    }

    /**
     * Check that sortBy and sortAsc search params are valud. Throw descriptive 400 ERROR if not.
     */
    public void checkGoodMoneyParams(
            String downPaymentString,
            String budgetMoString
    ) throws ResponseStatusException {
        if (!ParseChecker.isParsableToDouble(downPaymentString)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "invalid downPayment value, must parsable to double"
            );
        }
        if (!ParseChecker.isParsableToDouble(budgetMoString)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "invalid budgetMo value, must parsable to double"
            );
        }
    }

    /**
     * Get a list of all cars from the database. Can only sort by car "price"
     * since user is not logged in (no user info).
     */
    private List<Map<String, Object>> searchAllCars(
        String sortBy,
        boolean sortAsc
    ) throws SQLException {
        if (!sortBy.equals("price")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "invalid search params, sortBy must be \"price\" for pre-login search."
            );
        }

        List<Map<String, Object>> carsMapsAll = this.tableCars.getAllCars();
        return sortCars(carsMapsAll, sortBy, sortAsc);
    }

    /**
     * Get a list of cars from the database for which a loan offer is pre-approved by the Senso /rate Api.
     */
    private List<Map<String, Object>> searchOnlyCarsWithOffer(
        String userId,
        double downPayment,
        double budgetMo,
        String sortBy,
        boolean sortAsc
    ) throws SQLException, IOException, InterruptedException, ResponseStatusException {
        // Get user information from database and populate user entity with user info
        Map<String, Object> userEntry = this.tableUsers.getUserById(userId);
        EntUser user = new EntUser();
        user.loadFromMap(userEntry);

        // If user's search params are different to previous search (what's in the database), reset their offers table
        // according to this information, compute all loan offers, add to offers table, and return maps that contain
        // car and offers information.
        // If user's params are the same as previous search, get all return maps that contain
        // car and offers information, but just get the offers as they exist in the table (do not reset it
        // or re-call senso Api to check whether loan offers are approved).
        boolean newSearchParams = user.getDownPayment() == downPayment || user.getBudgetMo() == budgetMo;
        if (newSearchParams) {
            return searchCarsWithOfferNewParams(user, budgetMo, downPayment, sortBy, sortAsc);
        } else {
            return searchCarsWithOfferOldParams(user, sortBy, sortAsc);
        }
    }

    /**
     *  Resets user's offers table, queries Senso Api for new loan offer information using search params,
     *  adds approved offers to the user's offers table, and return maps that contain car and offers information
     *  (made using MergeCarAndOffer.mergeCarAndOffer()) using the approved offers that were added to the user's offers table.
     *
     *  Run this method if user's search params are different to previous search (what's currently in the database).
     *  Thus, we need to reset the offers table and check loan offer approval for each car with the senso /rate api
     *  before repopulating.
     *
     *  As a result of this call, the user's loan offers 'cart' is emptied (their offers table) as it is no longer valid
     *  given their new search params.
     */
    private List<Map<String, Object>> searchCarsWithOfferNewParams(
            EntUser user,
            double budgetMo,
            double downPayment,
            String sortBy,
            boolean sortAsc
    ) throws SQLException, IOException, InterruptedException {
        // Set user in offers table object
        tableOffers.setUser(user.getUserId());

        // Clear the user's current loan offers table (new params means new loan offers)
        tableOffers.removeAllOffers();

        // Get list of all cars
        List<Map<String, Object>> carMapsAll = this.tableCars.getAllCars();

        // Fill carAndOfferInfoMaps with maps containing car-offer information for cars for which a loan offer
        // was pre-approved by the senso /rate Api.
        List<Map<String, Object>> carAndOfferInfoMaps = new ArrayList<>();
        for (Map<String, Object> carMap : carMapsAll) {
            // Create a car entity for this carMap
            EntCar car = new EntCar();
            car.loadFromMap(carMap);

            // Get the offer for this car (calls the senso /rate api to get offer info)
            EntOffer offer = createOfferFromUserAndCar(user, car, budgetMo, downPayment);

            // If offer is not null, an offer was approved, merge car and offer entities into a single map
            // and add to carAndOfferInfoMaps. Otherwise, if null, no offer was approved and move on to the next
            // carMap in the loop.
            if (offer != null) {
                // Merge car and offer to create a carAndOfferInfoMap
                Map<String, Object> carAndOfferInfoMap = MergeCarAndOffer.mergeCarAndOffer(car, offer);
                carAndOfferInfoMaps.add(carAndOfferInfoMap);
            }
        }
        // Return a sorted version of carAndOfferInfoMaps according to the sort settings
        return sortCars(carAndOfferInfoMaps, sortBy, sortAsc);
    }

    /**
     * Returns maps that contain car and offers information (made using MergeCarAndOffer.mergeCarAndOffer())
     * using the offers that are currently in the user's offers table (does not reset the offers table or re-call
     * senso Api to check whether loan offers are approved, since these are already assumed to be approved
     * in a previous search query with the same search params).
     */
    private List<Map<String, Object>> searchCarsWithOfferOldParams(
            EntUser user,
            String sortBy,
            boolean sortAsc
    ) throws SQLException {
        // Set user in offers table object
        tableOffers.setUser(user.getUserId());

        // Get list of all offers in offers table
        List<Map<String, Object>> offerMapsAll = tableOffers.getAllOffers();

        // Fill carAndOfferInfoMaps with maps containing car-offer information for offers in the offers table
        List<Map<String, Object>> carAndOfferInfoMaps = new ArrayList<>();
        for (Map<String, Object> offerMap : offerMapsAll) {
            EntOffer offer = new EntOffer();
            offer.loadFromMap(offerMap);

            Map<String, Object> carMap = tableCars.getCarById(offer.getCarId());
            EntCar car = new EntCar();
            car.loadFromMap(carMap);

            // Merge car and offer to create a carAndOfferInfoMap
            Map<String, Object> carAndOfferInfoMap = MergeCarAndOffer.mergeCarAndOffer(car, offer);
            carAndOfferInfoMaps.add(carAndOfferInfoMap);
        }
        // Return a sorted version of carAndOfferInfoMaps according to the sort settings
        return sortCars(carAndOfferInfoMaps, sortBy, sortAsc);
    }

    /**
     * Creates an offer in the users offers table if loan was approved by the senso /rate api and
     * returns the corresponding offer entity.  If no loan offer was approved by the senso api,
     * returns null (and nothing is added to the loan offers table).
     */
    private EntOffer createOfferFromUserAndCar (
            EntUser user,
            EntCar car,
            double budgetMo,
            double downPayment
    ) throws IOException, InterruptedException, SQLException {
        // Set the tableOffers user (just in case it was not set before)
        tableOffers.setUser(user.getUserId());

        // Query senso Api for this car and user information
        Map<String, Object> queryResult = this.sensoApi.getLoanOffer(
                Double.toString(car.getPrice()), // loanAmount
                Integer.toString(user.getCreditScore()), // creditScore
                Double.toString(budgetMo), // budget
                car.getBrand(), // vehicleMake
                car.getModel(), // vehicleModel
                Integer.toString(car.getYear()), // vehicleYear
                Double.toString(car.getKms()), // vehicleKms
                Double.toString(car.getPrice()), // listPrice
                Double.toString(downPayment) // downpayment
        );

        // If api gave successful loan preapproval, add offer to user's offers table and return the corresponding
        // offer entity. Otherwise, there was no loan offer for this car and the current search params, so return null.
        if (queryResult.get("status").equals(200)) {
            Map queryBody = (Map) queryResult.get("body");
            int carId = car.getCarId();
            double loanAmount = (double) queryBody.get("amount");
            double capitalSum = (double) queryBody.get("capitalSum");
            double interestSum = (double) queryBody.get("interestSum");
            double totalSum = (double) queryBody.get("sum");
            double interestRate = (double) queryBody.get("interestRate");
            double termMo = Double.parseDouble((String) queryBody.get("term"));
            String installments = (queryBody.get("installments")).toString();
            boolean claimed = false;

            // Add offer information to offers table
            int offerId = tableOffers.addOffer(
                    carId,
                    loanAmount,
                    capitalSum,
                    interestSum,
                    totalSum,
                    interestRate,
                    termMo,
                    installments,
                    claimed
            );

            // Create an offer entity
            Map<String, Object> offerMap = tableOffers.getOfferByOfferId(offerId);
            EntOffer offer = new EntOffer();
            offer.loadFromMap(offerMap);

            return offer;
        } else {
            // No loan offer not available with current settings, return null.
            return null;
        }
    }

    /**
     * Sort a list of maps according to key and sortAsc. We use the corresponding value of key for each map
     * (which is assumed to be castable to double) to sort the list of maps. sortAsc defines whether we sort
     * by ascending order or not.
     */
    private List<Map<String, Object>> sortCars(
            List<Map<String, Object>> maps,
            String key,
            boolean sortAsc
    ) {
        List<Map<String, Object>> sorted = new ArrayList<>();

        // Place all maps from carAndOfferInfoMaps in sortedCarAndOfferInfoMaps (in sorted order).
        Map<String, Object> map;
        for (int i = 0; i < maps.size(); i++) {
            List<Map<String, Object>> subList = maps.subList(i, maps.size());

            // Get the map which we will remove from carAndOfferInfoMaps and add to sorted
            if (sortAsc) {
                map = getSmallest(subList, key);
            } else {
                map = getBiggest(subList, key);
            }

            maps.remove(map);
            sorted.add(map);
        }

        return sorted;
    }

    /**
     * Return the 'smallest' map in maps, where smallest is defined as the map which has the lowest value
     * corresponding to the key.
     *
     * The value corresponding to key must be castable to double.
     */
    private Map<String, Object> getSmallest(List<Map<String, Object>> maps, String key) {
        Map<String, Object> smallestMapSoFar = maps.get(0);
        for (Map<String, Object> map : maps) {
            if (toDouble(map.get(key)) < toDouble(smallestMapSoFar.get(key))) {
                smallestMapSoFar = map;
            }
        }
        return smallestMapSoFar;
    }

    /**
     * Return the 'biggest' map in maps, where biggest is defined as the map which has the highest value
     * corresponding to the key.
     *
     * The value corresponding to key must be castable to double.
     */
    private Map<String, Object> getBiggest(List<Map<String, Object>> maps, String key) {
        Map<String, Object> biggestMapSoFar = maps.get(0);
        for (Map<String, Object> map : maps) {
            if (toDouble(map.get(key)) > toDouble(biggestMapSoFar.get(key))) {
                biggestMapSoFar = map;
            }
        }
        return biggestMapSoFar;
    }

    private double toDouble(Object num) {
        if (num instanceof BigDecimal) {
            return ((BigDecimal) num).doubleValue();
        } else {
            return (double) num;
        }
    }
}
