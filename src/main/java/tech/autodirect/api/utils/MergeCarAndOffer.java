package tech.autodirect.api.utils;

import tech.autodirect.api.entities.EntCar;
import tech.autodirect.api.entities.EntOffer;

import java.util.HashMap;
import java.util.Map;

public class MergeCarAndOffer {
    /**
     * Merge car and offer entities into a single map.
     */
    public static Map<String, Object> mergeCarAndOffer(EntCar car, EntOffer offer) {
        return new HashMap<>() {{
            // Car info
            put("car_id", car.getCarId());
            put("brand", car.getBrand());
            put("model", car.getModel());
            put("year", car.getYear());
            put("price", car.getPrice());
            put("kms", car.getKms());
            // Offer info
            put("offer_id", offer.getOfferId());
            put("loan_amount", offer.getLoanAmount());
            put("capital_sum", offer.getCapitalSum());
            put("interest_sum", offer.getInterestSum());
            put("total_sum", offer.getTotalSum());
            put("apr", offer.getInterestRate()); // TODO: why "apr"
            put("term_mo", offer.getTermMo()); // TODO: clean term_length/term_mo and all these terms
            put("installments", offer.getInstallments());
            put("claimed", offer.isClaimed());
            // Computed stuff
            put("payment_mo", offer.getTotalSum() / offer.getTermMo());
        }};
    }
}
