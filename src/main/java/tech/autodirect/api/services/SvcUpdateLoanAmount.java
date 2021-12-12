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
import tech.autodirect.api.entities.EntCar;
import tech.autodirect.api.entities.EntOffer;
import tech.autodirect.api.entities.EntUser;
import tech.autodirect.api.interfaces.SensoApiInterface;
import tech.autodirect.api.interfaces.TableCarsInterface;
import tech.autodirect.api.interfaces.TableOffersInterface;
import tech.autodirect.api.interfaces.TableUsersInterface;
import tech.autodirect.api.utils.ParseChecker;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

/**
 * Responsible for updating a user's loan_amount value (if any add-ons were added).
 */
public class SvcUpdateLoanAmount {
    public Map<String, Object> updateLoanAmount(
            TableCarsInterface tableCars,
            TableUsersInterface tableUsers,
            TableOffersInterface tableOffers,
            SensoApiInterface sensoApi,
            String userId,
            String offerIdString,
            String newLoanAmountString
    ) throws SQLException, IOException, InterruptedException {
        // Verify that the new loan amount is valid
        if (!ParseChecker.isParsableToInt(newLoanAmountString)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "loan amount must be parsable to int");
        }
        int offerId = Integer.parseInt(offerIdString);

        // Get the user referred to by userId
        Map<String, Object> userMap = tableUsers.getUserById(userId);
        EntUser user = new EntUser();
        user.loadFromMap(userMap);

        // Set tableOffer's user and get the offer referred to by offerId
        tableOffers.setUser(userId);
        Map<String, Object> offerMap = tableOffers.getOfferByOfferId(offerId);
        EntOffer offer = new EntOffer();
        offer.loadFromMap(offerMap);

        // Get the car referred to by the offer
        Map<String, Object> carMap = tableCars.getCarById(offer.getCarId());
        EntCar car = new EntCar();
        car.loadFromMap(carMap);

        // Query senso api with the new loan_amount
        Map<String, Object> queryResult = sensoApi.getLoanOffer(
                newLoanAmountString,
                Integer.toString(user.getCreditScore()),
                Double.toString(user.getBudgetMo()),
                car.getBrand(),
                car.getModel(),
                Integer.toString(car.getYear()),
                Double.toString(car.getKms()),
                Double.toString(car.getPrice()),
                Double.toString(user.getDownPayment())
        );

        if ((int) queryResult.get("status") == 200) {
            // Offer with new loan amount was successfully approved by the senso /rate api.
            // So, update loan offer in offers table with the new loan offer from senso (for the same offer id)
            // and return offer details (including car info) to the frontend.

            // Update loan information
            @SuppressWarnings("unchecked")
            Map<String, Object> queryBody = (Map<String, Object>) queryResult.get("body");
            tableOffers.updateOfferColumn(offerId, TableOffersInterface.OfferColumns.LOAN_AMOUNT, (double) queryBody.get("amount"));
            tableOffers.updateOfferColumn(offerId, TableOffersInterface.OfferColumns.CAPITAL_SUM, (double) queryBody.get("capitalSum"));
            tableOffers.updateOfferColumn(offerId, TableOffersInterface.OfferColumns.INTEREST_RATE, (double) queryBody.get("interestSum"));
            tableOffers.updateOfferColumn(offerId, TableOffersInterface.OfferColumns.TOTAL_SUM, (double) queryBody.get("sum"));
            tableOffers.updateOfferColumn(offerId, TableOffersInterface.OfferColumns.INTEREST_RATE, (double) queryBody.get("interestRate"));
            tableOffers.updateOfferColumn(offerId, TableOffersInterface.OfferColumns.TERM_MO, Double.parseDouble((String) queryBody.get("term")));
            tableOffers.updateOfferColumn(offerId, TableOffersInterface.OfferColumns.INSTALLMENTS, (queryBody.get("installments")).toString());

            // Return offer details (including car info)
            SvcGetOfferDetails svcGetOfferDetails = new SvcGetOfferDetails();
            return svcGetOfferDetails.getOfferDetails(tableCars, tableOffers, userId, offerIdString);
        } else {
            // Loan offer request was not approved. Throw 406 error.
            throw new ResponseStatusException(
                    HttpStatus.NOT_ACCEPTABLE, "offer with the new loan amount was not approved"
            );
        }
    }
}
