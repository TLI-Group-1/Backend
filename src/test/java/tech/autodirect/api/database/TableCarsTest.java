package tech.autodirect.api.database;

import org.junit.jupiter.api.Test;
import tech.autodirect.api.entities.EntCar;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TableCarsTest {
    private static final String dbName = "testing";

    // Note: throughout this test class, we don't assume that testUserId exists in the users table.
    // We don't need to assume this since we never actually access this user in the users table.
    // We only use this userId to create offers (to help name the offers table).
    private final String testUserId = "TableCarsTests_test_user";

    @Test
    void testGetAllCars() {
        try {// Create new table for testUserId. setUpEach() ensures table doesn't already exist.
            TableCars table = new TableCars(dbName);
            List<Map<String, Object>> carMapsList = table.getAllCars("");

            EntCar car1 = new EntCar();
            car1.loadFromMap(new HashMap<>() {
                {
                    put("id", 1);
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
                    put("id", 7);
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
}

