package tech.autodirect.api.upstream;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SensoApi{

    public static Map<String, Object> queryApi(String budget) throws IOException, InterruptedException {
        // create request body
        String inputJson = "{\n" +
                "   \"loanAmount\": 10000,\n" +
                "   \"creditScore\": 780,\n" +
                "   \"pytBudget\": " + budget + ",\n" +
                "   \"vehicleMake\": \"Honda\",\n" +
                "   \"vehicleModel\": \"Civic\",\n" +
                "   \"vehicleYear\": 2021,\n" +
                "   \"vehicleKms\": 1000\n" +
                "}";

        // create a request
        var request = HttpRequest.newBuilder()
                .uri(URI.create("https://auto-loan-api.senso.ai/rate"))
                .header("Content-Type", "application/json")
                .header("x-api-key", System.getenv("SENSO_API_KEY"))
                .POST(HttpRequest.BodyPublishers.ofString(inputJson))
                .build();

        // create an HTTP client
        var client = HttpClient.newHttpClient();
        // use the client to send the request
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // construct the return data as a HashMap
        Map<String, Object> return_data = new HashMap();
        return_data.put("status", response.statusCode());
        return_data.put("body", response.body());

        return return_data;

    }
}