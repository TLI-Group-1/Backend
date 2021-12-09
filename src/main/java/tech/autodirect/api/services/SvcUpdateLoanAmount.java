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

import tech.autodirect.api.interfaces.SensoApiInterface;
import tech.autodirect.api.interfaces.TableOffersInterface;

import java.sql.SQLException;

/**
 * Responsible for updating a user's loan_amount value (if any add-ons were added).
 */
public class SvcUpdateLoanAmount {
    public void updateLoanAmount(
            TableOffersInterface tableOffers,
            SensoApiInterface sensoApi,
            String userId,
            String offerId,
            String newLoanAmount
    ) throws SQLException {
        //

        tableOffers.setUser(userId);

        // Query senso api with the new loan_amount
//        sensoApi.getLoanOffer(
//                newLoanAmount,
//
//        )
//        tableOffers.updateLoanAmount(Integer.parseInt(offerId), Double.parseDouble(newLoanAmount));

        // Check with Senso Api with updated loan amount
        // If failure, throw 406 error with message "new loan amount did not results in a valid offer"
        // If success, update loan offer in offers table for this user and return CarAndOfferInfo. (call svcGetOfferDetails)
        // update with senso result, dont change offer id.

        //
    }
}
