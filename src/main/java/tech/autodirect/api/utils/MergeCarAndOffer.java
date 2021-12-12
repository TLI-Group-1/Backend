package tech.autodirect.api.utils;

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
            put("interest_rate", offer.getInterestRate());
            put("term_mo", offer.getTermMo());
            put("installments", offer.getInstallments());
            put("claimed", offer.isClaimed());
            // Computed stuff
            put("payment_mo", offer.getTotalSum() / offer.getTermMo());
        }};
    }
}
