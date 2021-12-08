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
import java.util.Map;

public interface TableCarsInterface {

    /**
     * Gets all cars from the cars table in the database, using full-text keyword search (TODO).
     *
     * @return A List of Maps where each Map is a single entry in the JDBC query result.
     */
    List<Map<String, Object>> getAllCars() throws SQLException;

    /**
     * Get a specific car entry by the car ID.
     *
     * @return A Map representing a car entry in the database.
     */
    Map<String, Object> getCarById(String carId) throws SQLException;

    /**
     * Check if car exists in database.
     */
    boolean checkCarExists(String carId) throws SQLException;
}
