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

import org.springframework.boot.web.embedded.undertow.UndertowServletWebServer;
import tech.autodirect.api.entities.EntCar;
import tech.autodirect.api.entities.EntUser;
import tech.autodirect.api.interfaces.SensoApiInterface;
import tech.autodirect.api.interfaces.TableCarsInterface;
import tech.autodirect.api.interfaces.TableUsersInterface;
import tech.autodirect.api.utils.ParseChecker;
import tech.autodirect.api.utils.UnitConv;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class SvcSearch {
    private final TableCarsInterface tableCars;
    private final TableUsersInterface tableUsers;
    private final SensoApiInterface sensoApi;
    private final Set<String> valuesOfSortBy =
            new HashSet<>(Arrays.asList("price", "payment_mo", "apr", "total_sum", "term_length"));

    public SvcSearch(TableCarsInterface tableCars, TableUsersInterface tableUsers, SensoApiInterface sensoApi) {
        this.tableCars = tableCars;
        this.sensoApi = sensoApi;
        this.tableUsers = tableUsers;
    }

    /**
     * Perform a car search. If userId is not null, only get cars which have offers for this user.
     * Otherwise, get all cars.
     *
     * @return A list of car entities.
     */
    public List<EntCar> searchCars(
        String userId,
        String downPaymentString,
        String budgetMoString,
        String sortBy,
        String sortAscString,
        String keywords
    ) throws SQLException, IOException, InterruptedException {
        // Set sortBy, sortAsc, and keywords search params to default values if not in correct format
        if (!valuesOfSortBy.contains(sortBy) || sortBy == null) { sortBy = "price"; }
        if (sortAscString == null) { sortAscString = "true"; }
        if (keywords == null) { sortAscString = ""; }

        // Parse sortAscString to boolean
        boolean sortAsc = Boolean.parseBoolean(sortAscString);

        // If some required values are null, the body of the try will throw NullPointerException
        boolean areValidParams;
        try {
            areValidParams = !userId.equals("")
                            && !downPaymentString.equals("")
                            && !budgetMoString.equals("")
                            && ParseChecker.isParsableToDouble(downPaymentString)
                            && ParseChecker.isParsableToDouble(budgetMoString);
        } catch (NullPointerException ignored) {
            areValidParams = false;
        }

        // Run the correct search algorithm, according the validity of the search params
        if (!areValidParams) {
            // Return all cars in the database.
            return searchCarsAll(sortBy, sortAsc, keywords);
        } else {
            // User is logged in and search params are valid. So, only return cars which have offers for this user.
            double downPayment = Double.parseDouble(downPaymentString);
            double budgetMo = Double.parseDouble(budgetMoString);
            return searchCarsWithOffer(userId, downPayment, budgetMo, sortBy, sortAsc, keywords);
        }
    }

    /**
     * Get a list of all cars from the database.
     *
     * @return A list of car entities.
     */
    private List<EntCar> searchCarsAll(
            String sortBy,
            boolean sortAsc,
            String keywords
    ) throws SQLException {
        List<Map<String, Object>> carsMapsAll = this.tableCars.getAllCars(keywords);

        // Convert each entry of carsMapsAll to EntCar
        List<EntCar> carEntsAll = new ArrayList<>();
        for (Map<String, Object> carMap : carsMapsAll) {
            EntCar car = new EntCar();
            car.loadFromList(carMap);
            carEntsAll.add(car);
        }
        return sortCars(carEntsAll, sortBy, sortAsc);
    }

    /**
     * Get a list of cars from the database for which a loan offer is pre-approved by the Senso /rate Api.
     *
     * @return A list of car entities.
     */
    private List<EntCar> searchCarsWithOffer(
            String userId,
            double downpayment,
            double budgetMo,
            String sortBy,
            boolean sortAsc,
            String keywords
    ) throws SQLException, IOException, InterruptedException {
        // Get user information from database and populate user entity with user info
        Map<String, Object> userEntry = this.tableUsers.getUserByID(userId);
        EntUser user = new EntUser();
        user.loadFromList(userEntry);

        // Get list of all cars and add all cars for which a Senso /rate Api loan offer is approved to carsWithOffer
        List<Map<String, Object>> carMapsAll = this.tableCars.getAllCars(keywords);
        List<EntCar> carEntsWithOffer = new ArrayList<>();
        for (Map<String, Object> carMap : carMapsAll) {
            EntCar car = new EntCar();
            car.loadFromList(carMap);

            // Query senso Api for this car
            Map<String, Object> queryResult = this.sensoApi.getLoanOffer(
                    Double.toString(car.getPrice()), // loanAmount (TODO: verify correct)
                    Integer.toString(user.getCreditScore()), // creditScore
                    Double.toString(budgetMo), // budget
                    car.getBrand(), // vehicleMake
                    car.getModel(), // vehicleModel
                    Integer.toString(car.getYear()), // vehicleYear
                    Double.toString(car.getKms()), // vehicleKms
                    Double.toString(car.getPrice()), // listPrice
                    Double.toString(downpayment) // downpayment
            );

            // If successfully called api, add car to carsWithOffer
            if (queryResult.get("status").equals(200)) {
                carEntsWithOffer.add(car);
            }
        }
        return sortCars(carEntsWithOffer, sortBy, sortAsc);
    }

    /**
     * Sort list of EntCar objects according to params.
     *
     * @return A list of car entities.
     */
    private List<EntCar> sortCars(List<EntCar> carEnts, String sortBy, boolean sortAsc) {
        return carEnts; // TODO
    }
}
