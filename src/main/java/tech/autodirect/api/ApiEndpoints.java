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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.autodirect.api.database.TableUsers;
import tech.autodirect.api.interfaces.BankApiInterface;
import tech.autodirect.api.interfaces.TableUsersInterface;
import tech.autodirect.api.services.SvcUserLogin;
import tech.autodirect.api.upstream.BankApi;
import tech.autodirect.api.upstream.SensoApi;

import java.io.IOException;
import java.sql.SQLException;

// Mark the class as a Spring.io REST application
@SpringBootApplication
@RestController
// specify hosts allowed to access the AutoDirect API
@CrossOrigin(origins = {
		"http://localhost",
		"http://localhost:8080",
		"https://autodirect.tech",
		"https://api.autodirect.tech"
})
public class ApiEndpoints {

	public static void main(String[] args) {
		SpringApplication.run(ApiEndpoints.class, args);
	}

	// demo API endpoint; use for reference
	@GetMapping("/demo")
	public Object hello(
		@RequestParam String loanAmount,
		@RequestParam String creditScore,
		@RequestParam String budget
	) {

		// TODO: Remove this hardcoding and read params from other parts of the program.
		String vehicleMake = "Honda";
		String vehicleModel = "Civic";
		String vehicleYear = "2018";
		String vehicleKms = "1";
		String listPrice = "1000";
		String downpayment = "1000";

		try {
			return SensoApi.queryApi(
				loanAmount,
				creditScore,
				budget,
				vehicleMake,
				vehicleModel,
				vehicleYear,
				vehicleKms,
				listPrice,
				downpayment
			);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return "Server Error!";
		}

	}
	// User Login endpoint
	@GetMapping("/login")
	public Object login(@RequestParam(name = "user_id") String userId) {
		try {
			TableUsersInterface tableUser = new TableUsers("autodirect");
			BankApiInterface bankApi = new BankApi();
			SvcUserLogin svcUserLogin = new SvcUserLogin(tableUser, bankApi);
			return svcUserLogin.loginUser(userId);
		} catch (SQLException e) {
			e.printStackTrace();
			return "Server Error!";
		}
	}
}