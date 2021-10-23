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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ApiEndpoints {
	public static void main(String[] args) {
		// use for reference
		SpringApplication.run(ApiEndpoints.class, args);
	}

	@GetMapping("/demo")
	// how the code tells the server where to start processing your request
	public Object hello(@RequestParam String budget) {
		try {
			return SensoApi.queryApi(budget);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return new String("Server Error!");
		}
	}
}