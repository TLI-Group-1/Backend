package tech.autodirect.api.entities;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


class EntOfferTest {

    @Test
    void testLoadFromMap() {
        try {
            Map<String, Object> offerMap = new HashMap<>() {
                {
                    put("offer_id", 1);
                    put("car_id", 2);
                    put("loan_amount", BigDecimal.valueOf(3));
                    put("capital_sum", BigDecimal.valueOf(4));
                    put("interest_sum", BigDecimal.valueOf(5));
                    put("total_sum", BigDecimal.valueOf(6));
                    put("interest_rate", 7F);
                    put("term_mo", 8F);
                    put("installments", "9");
                    put("claimed", true);
                }
            };

            EntOffer offer = new EntOffer();
            offer.loadFromMap(offerMap);

            assert offer.getOfferId() == 1;
            assert offer.getCarId() == 2;
            assert offer.getLoanAmount() == 3;
            assert offer.getCapitalSum() == 4;
            assert offer.getInterestSum() == 5;
            assert offer.getTotalSum() == 6;
            assert offer.getInterestRate() == 7;
            assert offer.getTermMo() == 8;
            assert offer.getInstallments().equals("9");
            assert offer.isClaimed();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}