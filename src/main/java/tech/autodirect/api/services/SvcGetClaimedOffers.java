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

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import tech.autodirect.api.database.TableCars;
import tech.autodirect.api.entities.EntCar;
import tech.autodirect.api.entities.EntOffer;
import tech.autodirect.api.interfaces.TableCarsInterface;
import tech.autodirect.api.interfaces.TableOffersInterface;
import tech.autodirect.api.utils.MergeCarAndOffer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Responsible for getting all claimed offers for a user.
 */
public class SvcGetClaimedOffers {

    /**
     * Get all claimed offers for the specified user.
     */
    public List<Map<String, Object>> getClaimedOffers(
            TableCarsInterface tableCars,
            TableOffersInterface tableOffers,
            String userId
    ) throws SQLException, ResponseStatusException {
        if (userId.equals("")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "empty userId"
            );
        }

        // Set user in offers table and get all offers
        tableOffers.setUser(userId);
        List<Map<String, Object>> offersList = tableOffers.getClaimedOffers();

        List<Map<String, Object>> carAndOfferInfoMaps = new ArrayList<>();
        for (Map<String, Object> offerMap : offersList) {
            // Get offer entity
            EntOffer offer = new EntOffer();
            offer.loadFromMap(offerMap);

            // Get car entity
            Map<String, Object> carMap = tableCars.getCarById(offer.getCarId());
            EntCar car = new EntCar();
            car.loadFromMap(carMap);

            // Merge car and offer entities into a map that has both car and offer info to return to frontend
            Map<String, Object> carAndOfferInfo = MergeCarAndOffer.mergeCarAndOffer(car, offer);
            carAndOfferInfoMaps.add(carAndOfferInfo);
        }
        return carAndOfferInfoMaps;
    }
}
