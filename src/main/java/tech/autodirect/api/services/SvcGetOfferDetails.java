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
import tech.autodirect.api.entities.EntOffer;
import tech.autodirect.api.interfaces.TableOffersInterface;

import java.sql.SQLException;
import java.util.Map;

/**
 * Responsible for getting the details for an offer.
 */
public class SvcGetOfferDetails {

    /**
     * Get details for an offer (as an EntOffer object).
     */
    public EntOffer getOfferDetails(
            TableOffersInterface tableOffers,
            String userId,
            String offerId
    ) throws SQLException, ResponseStatusException {
        if (userId.equals("") || offerId.equals("")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "empty userId or offerId"
            );
        }

        tableOffers.setUser(userId);

        Map<String, Object> offerMap = tableOffers.getOfferByOfferId(Integer.parseInt(offerId));
        EntOffer offer = new EntOffer();
        offer.loadFromMap(offerMap);

        return offer;
    }
}
