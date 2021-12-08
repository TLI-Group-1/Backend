package tech.autodirect.api.entities;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


class EntUserTest {

    @Test
    void loadFromMap() {
        try {
            Map<String, Object> userMap = new HashMap<>() {
                {
                    put("user_id", "1");
                    put("credit_score", 2);
                    put("down_payment", BigDecimal.valueOf(3));
                    put("budget_mo", BigDecimal.valueOf(4));
                    put("offers_table", "5");
                }
            };

            EntUser user = new EntUser();
            user.loadFromMap(userMap);

            assert user.getUserId().equals("1");
            assert user.getCreditScore() == 2;
            assert user.getDownPayment() == 3;
            assert user.getBudgetMo() == 4;
            assert user.getOffersTable().equals("5");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}