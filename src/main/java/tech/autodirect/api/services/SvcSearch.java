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

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SvcSearch {
    private final TableCarsInterface tableCars;
    private final TableUsersInterface tableUsers;
    private final SensoApiInterface sensoApi;

    public SvcSearch(TableCarsInterface tableCars, TableUsersInterface tableUsers, SensoApiInterface sensoApi) {
        this.tableCars = tableCars;
        this.sensoApi = sensoApi;
        this.tableUsers = tableUsers;
    }

    /**
     * Get a list of cars (from the database) for which a loan offer is pre-approved by the Senso /rate Api.
     *
     * @return A list of car entities.
     */
    public List<EntCar> searchCars(
            String userId,
            double downpayment,
            double budgetMo,
            String sortBy,
            Boolean sortAsc,
            String keywords
    ) throws SQLException, IOException, InterruptedException {
        // Get user information from database and populate user entity with user info
        Map<String, Object> userEntry = this.tableUsers.getUserByID(userId);
        EntUser user = new EntUser();
        user.loadFromList(userEntry);

        // Get list of all cars and add all cars for which a Senso /rate Api loan offer is approved to carsWithOffer
        List<Map<String, Object>> cars = this.tableCars.getAllCars();
        List<EntCar> carsWithOffer = new ArrayList<>();
        for (Map<String, Object> entry : cars) {
            EntCar car = new EntCar();
            car.loadFromList(entry);

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
                carsWithOffer.add(car);
            }
        }
        return carsWithOffer;
    }
}
