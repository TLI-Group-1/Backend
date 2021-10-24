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

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ApiEndpoints {

	public static void main(String[] args) {
		SpringApplication.run(ApiEndpoints.class, args);
	}

	// hosts allowed to access the AutoDirect API
	@CrossOrigin(origins = {
			"http://localhost",
			"http://localhost:8080",
			"https://autodirect.tech",
			"https://api.autodirect.tech"
	})
	// demo API endpoint; use for reference
	@GetMapping("/demo")
	public Object hello(@RequestParam String budget) {
		try {
			return SensoApi.queryApi(budget);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return "Server Error!";
		}
	}

}