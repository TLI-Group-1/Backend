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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

	public static void main(String[] args) {
		SpringApplication.run(ApiEndpoints.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(ApiEndpoints.class);
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
			TableCarsInterface tableCars = new TableCars("autodirect");
			TableUsersInterface tableUser = new TableUsers("autodirect");
			SensoApiInterface sensoApi = new SensoApi();
			SvcSearch svcSearch = new SvcSearch(tableCars, tableUser, sensoApi);
			return svcSearch.searchCars(userId, downPayment, budgetMo, sortBy, sortAsc);
		} catch (IOException | InterruptedException | SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			return "Server Error!";
		}
	}

	@GetMapping("/login")
	public Object login(@RequestParam(name = "user_id") String userId) {
		try {
			TableUsersInterface tableUser = new TableUsers("autodirect");
			BankApiInterface bankApi = new BankApi();
			SvcUserLogin svcUserLogin = new SvcUserLogin();
			return svcUserLogin.loginUser(tableUser, bankApi, userId);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			return "Server Error!";
		}
	}

	@GetMapping("/claimOffer")
	public Object claimOffer(
			@RequestParam(name = "user_id") String userId,
			@RequestParam(name = "offer_id") String offerId
	) {
		try {
			TableOffersInterface tableOffers = new TableOffers("autodirect");
			SvcClaimOffer svcClaimOffer = new SvcClaimOffer();
			svcClaimOffer.claimOffer(tableOffers, userId, offerId);
			return "";
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			return "Server Error!";
		}
	}

	@GetMapping("/unclaimOffer")
	public Object unclaimOffer(
			@RequestParam(name = "user_id") String userId,
			@RequestParam(name = "offer_id") String offerId
	) {
		try {
			TableOffersInterface tableOffers = new TableOffers("autodirect");
			SvcUnclaimOffer svcUnclaimOffer = new SvcUnclaimOffer();
			svcUnclaimOffer.unclaimOffer(tableOffers, userId, offerId);
			return "";
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			return "Server Error!";
		}
	}

	@GetMapping("/getClaimedOffers")
	public Object getClaimedOffers(@RequestParam(name = "user_id") String userId) {
		try {
			TableOffersInterface tableOffers = new TableOffers("autodirect");
			SvcGetClaimedOffers svcGetClaimedOffers = new SvcGetClaimedOffers();
			return svcGetClaimedOffers.getClaimedOffers(tableOffers, userId);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			return "Server Error!";
		}
	}

	@GetMapping("/getOfferDetails")
	public Object login(
			@RequestParam(name = "user_id") String userId,
			@RequestParam(name = "offer_id") String offerId
	) {
		try {
			TableOffersInterface tableOffers = new TableOffers("autodirect");
			SvcGetOfferDetails svcGetOfferDetails = new SvcGetOfferDetails();
			return svcGetOfferDetails.getOfferDetails(tableOffers, userId, offerId);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			return "Server Error!";
		}
	}
}
