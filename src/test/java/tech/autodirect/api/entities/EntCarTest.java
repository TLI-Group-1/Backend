package tech.autodirect.api.entities;

import org.junit.jupiter.api.Test;
import tech.autodirect.api.utils.UnitConv;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


class EntCarTest {

    @Test
    void testLoadFromMap() {
        try {
            Map<String, Object> carMap = new HashMap<>() {
                {
                    put("id", 1);
                    put("brand", "nissan");
                    put("model", "sedan");
                    put("year", 2017);
                    put("price", BigDecimal.valueOf(6700));
                    put("mileage", ((Integer) 62280).floatValue());
                }
            };

            EntCar car = new EntCar();
            car.loadFromMap(carMap);

            assert car.getCarId() == 1;
            assert car.getBrand().equals("nissan");
            assert car.getModel().equals("sedan");
            assert car.getYear() == 2017;
            assert car.getPrice() == 6700;
            assert car.getKms() == UnitConv.mileToKm(62280);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testEqualsWhenSame() {
        try {
            Map<String, Object> carMap1 = new HashMap<>() {
                {
                    put("id", 1);
                    put("brand", "nissan");
                    put("model", "sedan");
                    put("year", 2017);
                    put("price", BigDecimal.valueOf(6700));
                    put("mileage", ((Integer) 62280).floatValue());
                }
            };

            // Make EntCar objects
            EntCar car1 = new EntCar();
            car1.loadFromMap(carMap1);
            EntCar car2 = new EntCar();
            car2.loadFromMap(carMap1);

            assert car1.equals(car2);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testEqualsWhenNotSame() {
        try {
            Map<String, Object> carMap1 = new HashMap<>() {
                {
                    put("id", 1);
                    put("brand", "nissan");
                    put("model", "sedan");
                    put("year", 2017);
                    put("price", BigDecimal.valueOf(6700));
                    put("mileage", ((Integer) 62280).floatValue());
                }
            };
            Map<String, Object> carMap2 = new HashMap<>() {
                {
                    put("id", 7);
                    put("brand", "ford");
                    put("model", "mustang");
                    put("year", 2019);
                    put("price", BigDecimal.valueOf(34100));
                    put("mileage", ((Integer) 10167).floatValue());
                }
            };

            // Make EntCar objects
            EntCar car1 = new EntCar();
            car1.loadFromMap(carMap1);
            EntCar car2 = new EntCar();
            car2.loadFromMap(carMap2);

            assert !car1.equals(car2);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}