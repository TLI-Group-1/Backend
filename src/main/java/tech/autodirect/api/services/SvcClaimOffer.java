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
import tech.autodirect.api.interfaces.TableOffersInterface;

import java.sql.SQLException;

public class SvcClaimOffer {
    /**
     * Claim a specified offer for the specified user.
     */
    public void claimOffer(
            TableOffersInterface tableOffers,
            String userId,
            String offerId
    ) throws SQLException {
        if (userId.equals("") || offerId.equals("")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "empty userId or offerId"
            );
        }

        tableOffers.setUser(userId);
        tableOffers.markOfferClaimed(Integer.parseInt(offerId));
    }
}
