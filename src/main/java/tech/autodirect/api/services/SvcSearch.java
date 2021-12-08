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
import tech.autodirect.api.entities.EntUser;
import tech.autodirect.api.interfaces.SensoApiInterface;
import tech.autodirect.api.interfaces.TableCarsInterface;
import tech.autodirect.api.interfaces.TableUsersInterface;
import tech.autodirect.api.utils.ParseChecker;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * Responsible for searching through all cars given a user's ID and search settings.
 */
public class SvcSearch {
    private final TableCarsInterface tableCars;
    private final TableUsersInterface tableUsers;
    private final SensoApiInterface sensoApi;
    private final Set<String> valuesOfSortBy = new HashSet<>(
        Arrays.asList("price", "payment_mo", "apr", "total_sum", "term_length")
    );

    public SvcSearch(
        TableCarsInterface tableCars,
        TableUsersInterface tableUsers,
        SensoApiInterface sensoApi
    ) {
        this.tableCars = tableCars;
        this.sensoApi = sensoApi;
        this.tableUsers = tableUsers;
    }

    /**
     * Perform a car search. If userId is not empty string or "null", only get cars which have offers for this user
     * (post-login search). Otherwise, get all cars (pre-login search).
     */
    public List<EntCar> searchCars(
        String userId,
        String downPaymentString,
        String budgetMoString,
        String sortBy,
        String sortAscString
    ) throws SQLException, IOException, InterruptedException, ResponseStatusException {
        // Set sortBy and sortAsc search params to default values if not in correct format
        if (!valuesOfSortBy.contains(sortBy)) { sortBy = "apr"; }
        if (!Objects.equals(sortAscString, "false")) { sortAscString = "true"; }

        // Convert sortAscString to boolean sortAsc
        boolean sortAsc = Boolean.parseBoolean(sortAscString);

        if (userId.equals("") || userId.equals("null")) {
            // If no userId, run pre-login search (return all cars)
            return searchCarsAll(sortBy, sortAsc);
        } else {
            // Check if other values are good, throw BAD_REQUEST 400 error if bad values
            boolean goodDownPaymentString = ParseChecker.isParsableToDouble(downPaymentString);
            boolean goodBudgetMoString = ParseChecker.isParsableToDouble(budgetMoString);
            boolean goodSortBy = valuesOfSortBy.contains(sortBy);
            if (!goodDownPaymentString || !goodBudgetMoString || !goodSortBy) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid search params");
            }

            // User is logged in and search params are valid. So, only return cars which have offers for this user.
            double downPayment = Double.parseDouble(downPaymentString);
            double budgetMo = Double.parseDouble(budgetMoString);
            return searchCarsWithOffer(userId, downPayment, budgetMo, sortBy, sortAsc);
        }
    }

    /**
     * Get a list of all cars from the database.
     */
    private List<EntCar> searchCarsAll(
        String sortBy,
        boolean sortAsc
    ) throws SQLException {
        List<Map<String, Object>> carsMapsAll = this.tableCars.getAllCars();

        // Convert each entry of carsMapsAll to EntCar
        List<EntCar> carEntsAll = new ArrayList<>();
        for (Map<String, Object> carMap : carsMapsAll) {
            EntCar car = new EntCar();
            car.loadFromMap(carMap);
            carEntsAll.add(car);
        }
        return sortCars(carEntsAll, sortBy, sortAsc);
    }

    /**
     * Get a list of cars from the database for which a loan offer is pre-approved by the Senso /rate Api.
     */
    private List<EntCar> searchCarsWithOffer(
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

        // Get list of all cars and add all cars for which a Senso /rate Api loan offer is approved to carsWithOffer
        List<Map<String, Object>> carMapsAll = this.tableCars.getAllCars();
        List<EntCar> carEntsWithOffer = new ArrayList<>();
        for (Map<String, Object> carMap : carMapsAll) {
            EntCar car = new EntCar();
            car.loadFromMap(carMap);

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
                Double.toString(downPayment) // downpayment
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
     */
    private List<EntCar> sortCars(List<EntCar> carEnts, String sortBy, boolean sortAsc) {
        return carEnts; // TODO
    }
}
