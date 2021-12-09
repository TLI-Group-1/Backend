package tech.autodirect.api;

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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tech.autodirect.api.database.TableCars;
import tech.autodirect.api.database.TableOffers;
import tech.autodirect.api.database.TableUsers;
import tech.autodirect.api.interfaces.*;
import tech.autodirect.api.services.*;
import tech.autodirect.api.upstream.BankApi;
import tech.autodirect.api.upstream.SensoApi;

import java.io.IOException;
import java.sql.SQLException;


// Mark the class as a Spring.io REST application
@SpringBootApplication
// specify hosts allowed to access the AutoDirect API
@CrossOrigin(origins = {
        "http://0.0.0.0",
        "http://localhost",
        "http://localhost:8080",
        "https://autodirect.tech",
        "https://api.autodirect.tech"
})
@RestController
public class ApiEndpoints extends SpringBootServletInitializer {
    // Name of the database to access
    private final String dbName = "autodirect";

    // Initialize Frameworks & Drivers
    private TableCarsInterface tableCars;
    private TableUsersInterface tableUsers;
    private TableOffersInterface tableOffers;
    private SensoApiInterface sensoApi;
    private BankApiInterface bankApi;

    // Initialize Services (Use Cases)
    private SvcClaimOffer svcClaimOffer;
    private SvcGetClaimedOffers svcGetClaimedOffers;
    private SvcGetOfferDetails svcGetOfferDetails;
    private SvcMockBankApi svcMockBankApi;
    private SvcSearch svcSearch;
    private SvcUnclaimOffer svcUnclaimOffer;
    private SvcUpdateLoanAmount svcUpdateLoanAmount;
    private SvcUserLogin svcUserLogin;

    // server error that gets thrown to the frontend upon catching other errors
    private ResponseStatusException SERVER_ERROR;


    public static void main(String[] args) {
        SpringApplication.run(ApiEndpoints.class, args);
    }

    public ApiEndpoints() {
        try {
            // Instantiate Frameworks & Drivers
            tableCars = new TableCars(dbName);
            tableUsers = new TableUsers(dbName);
            tableOffers = new TableOffers(dbName);
            sensoApi = new SensoApi();
            bankApi = new BankApi();

            // Instantiate Services (Use Cases)
            svcClaimOffer = new SvcClaimOffer();
            svcGetClaimedOffers = new SvcGetClaimedOffers();
            svcGetOfferDetails = new SvcGetOfferDetails();
            svcMockBankApi = new SvcMockBankApi();
            svcSearch = new SvcSearch(tableCars, tableUsers, tableOffers, sensoApi);
            svcUnclaimOffer = new SvcUnclaimOffer();
            svcUpdateLoanAmount = new SvcUpdateLoanAmount();
            svcUserLogin = new SvcUserLogin();

            // Instantiate Server Error
            SERVER_ERROR = new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Server Error!"
            );
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ApiEndpoints.class);
    }

    @GetMapping("/")
    public Object index() {
        return "<center><h1>AutoDirect Backend API</h1></center>";
    }

    @GetMapping("/search")
    public Object search(
            @RequestParam(name = "user_id") String userId,
            @RequestParam(name = "downpayment") String downPayment,
            @RequestParam(name = "budget_mo") String budgetMo,
            @RequestParam(name = "sort_by") String sortBy,
            @RequestParam(name = "sort_asc") String sortAsc
    ) {
        try {
            return svcSearch.search(userId, downPayment, budgetMo, sortBy, sortAsc);
        } catch (IOException | InterruptedException | SQLException e) {
            e.printStackTrace();
            throw SERVER_ERROR;
        }
    }

    @GetMapping("/login")
    public Object login(@RequestParam(name = "user_id") String userId) {
        try {
            return svcUserLogin.loginUser(tableUsers, bankApi, userId);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw SERVER_ERROR;
        }
    }

    @GetMapping("/claimOffer")
    public Object claimOffer(
            @RequestParam(name = "user_id") String userId,
            @RequestParam(name = "offer_id") String offerId
    ) {
        try {
            svcClaimOffer.claimOffer(tableOffers, userId, offerId);
            return "";
        } catch (SQLException e) {
            e.printStackTrace();
            throw SERVER_ERROR;
        }
    }

    @GetMapping("/unclaimOffer")
    public Object unclaimOffer(
            @RequestParam(name = "user_id") String userId,
            @RequestParam(name = "offer_id") String offerId
    ) {
        try {
            svcUnclaimOffer.unclaimOffer(tableOffers, userId, offerId);
            return "";
        } catch (SQLException e) {
            e.printStackTrace();
            throw SERVER_ERROR;
        }
    }

    @GetMapping("/getClaimedOffers")
    public Object getClaimedOffers(@RequestParam(name = "user_id") String userId) {
        try {
            return svcGetClaimedOffers.getClaimedOffers(tableCars, tableOffers, userId);
        } catch (SQLException e) {
            e.printStackTrace();
            throw SERVER_ERROR;
        }
    }

    @GetMapping("/getOfferDetails")
    public Object getOfferDetails(
            @RequestParam(name = "user_id") String userId,
            @RequestParam(name = "offer_id") String offerId
    ) {
        try {
            return svcGetOfferDetails.getOfferDetails(tableCars, tableOffers, userId, offerId);
        } catch (SQLException e) {
            e.printStackTrace();
            throw SERVER_ERROR;
        }
    }

    @GetMapping("/updateLoanAmount")
    public Object updateLoanAmount(
            @RequestParam(name = "user_id") String userId,
            @RequestParam(name = "offer_id") String offerId,
            @RequestParam(name = "loan_amount") String newLoanAmount
    ) {
        try {
            return svcUpdateLoanAmount.updateLoanAmount(
                    tableCars,
                    tableUsers,
                    tableOffers,
                    sensoApi,
                    userId,
                    offerId,
                    newLoanAmount
            );
        } catch (SQLException | IOException | InterruptedException e) {
            e.printStackTrace();
            throw SERVER_ERROR;
        }
    }
}
