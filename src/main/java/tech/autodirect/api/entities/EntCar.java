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

import tech.autodirect.api.utils.UnitConv;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

/**
 * This entity represents a car in our program.
 */
public class EntCar {
    private int carId;
    private String brand;
    private String model;
    private int year;
    private double price;
    private double kms;

    /**
     * Populates EntCar from a Map containing representing a car entry in the database.
     *
     * @param entry : A Map containing representing a car entry in the database.
     */
    public void loadFromMap(Map<String, Object> entry) throws SQLException {
        carId = (int) entry.get("car_id");
        brand = (String) entry.get("brand");
        model = (String) entry.get("model");
        year = (int) entry.get("year");
        price = ((BigDecimal) entry.get("price")).doubleValue();
        kms = UnitConv.mileToKm(((Float) entry.get("mileage")).doubleValue());
    }

    public int getCarId() {
        return carId;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public double getPrice() {
        return price;
    }

    public double getKms() {
        return kms;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof EntCar) {
            EntCar car = (EntCar) o;

            return getCarId() == car.getCarId() &&
                    Objects.equals(getBrand(), car.getBrand()) &&
                    getKms() == car.getKms() &&
                    Objects.equals(getModel(), car.getModel()) &&
                    getYear() == car.getYear() &&
                    getPrice() == car.getPrice();
        } else {
            return false;
        }
    }

}
