package tech.autodirect.api.database;

import org.junit.jupiter.api.Test;
import tech.autodirect.api.entities.EntCar;
import tech.autodirect.api.utils.UnitConv;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TableCarsTest {
    private static final String DB_NAME = "testing";

    @Test
    void testGetAllCars() {
        try {
            TableCars table = new TableCars(DB_NAME);
            List<Map<String, Object>> carMapsList = table.getAllCars();

            EntCar car1 = new EntCar();
            car1.loadFromMap(new HashMap<>() {
                {
                    put("car_id", 1);
                    put("brand", "nissan");
                    put("model", "sedan");
                    put("year", 2017);
                    put("price", BigDecimal.valueOf(6700));
                    put("mileage", ((Integer) 62280).floatValue());
                }
            });

            EntCar car2 = new EntCar();
            car2.loadFromMap(new HashMap<>() {
                {
                    put("car_id", 7);
                    put("brand", "ford");
                    put("model", "mustang");
                    put("year", 2019);
                    put("price", BigDecimal.valueOf(34100));
                    put("mileage", ((Integer) 10167).floatValue());
                }
            });

            boolean car1InCarMapsList = false;
            boolean car2InCarMapsList = false;
            for (Map<String, Object> carMap : carMapsList) {

                EntCar carOther = new EntCar();
                carOther.loadFromMap(carMap);

                if (car1.equals(carOther)) {
                    car1InCarMapsList = true;
                }
                if (car2.equals(carOther)) {
                    car2InCarMapsList = true;
                }
            }

            assert car1InCarMapsList && car2InCarMapsList;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            assert false;
        }
    }
    /**
     * Tests getCarById().
     */
    @Test
    void testGetCarById() {
        try {
            TableCars cars = new TableCars(dbName);
            cars.addCar("nissan", "sedan", 2017, 6700, 62280);

            EntCar car1 = new EntCar();
            car1.loadFromMap(new HashMap<>() {
                {
                    put("car_id", 1);
                    put("brand", "nissan");
                    put("model", "sedan");
                    put("year", 2017);
                    put("price", BigDecimal.valueOf(6700));
                    put("mileage", ((Integer) 62280).floatValue());
                }
            });
            assert car1.getCarId() == 1;
            assert car1.getYear() == 2017;
            assert car1.getModel().equals("sedan");
            assert car1.getBrand().equals("nissan");
            assert car1.getPrice() == 6700;
            assert car1.getKms() == UnitConv.mileToKm(62280);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            assert false;
        }
    }
    /**
     * Tests addCar().
     */
    @Test
    void testAddCar() {
        try {
            TableCars cars = new TableCars(dbName);
            cars.addCar("nissan", "sedan", 2017, 6700, 62280);

            assert cars.checkCarExists(1);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            assert false;
        }
    }

    /**
     * Tests checkCarExists() when it exists
     */
    @Test
    void testCheckCarExitsWhenExists() {
        try {
            TableCars cars = new TableCars(dbName);
            cars.addCar("nissan", "sedan", 2017, 6700, 62280);

            assert cars.checkCarExists(1);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            assert false;
        }
    }
    /**
     * Tests checkCarExists() when it exists
     */
    @Test
    void testCheckCarExitsWhenNotExists() {
        try {
            TableCars cars = new TableCars(dbName);
            cars.addCar("nissan", "sedan", 2017, 6700, 62280);

            assert !cars.checkCarExists(70);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            assert false;
        }
    }
}

