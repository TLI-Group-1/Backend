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

import org.yaml.snakeyaml.error.MissingEnvironmentVariableException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.Gson;
import tech.autodirect.api.interfaces.SensoApiInterface;


public class SensoApi implements SensoApiInterface {
    // API connection parameters
    private static String sensoUrl;
    private static String sensoKey;
    // Gson object for JSON/Map conversions
    private static final Gson GSON = new Gson();

    public Map<String, Object> getLoanOffer(
            String loanAmount,
            String creditScore,
            String budget,
            String vehicleMake,
            String vehicleModel,
            String vehicleYear,
            String vehicleKms,
            String listPrice,
            String downPayment
    ) throws IOException, InterruptedException {
        // create request body
        Map<String, String> queryMap = new HashMap<>() {{
            put("loanAmount", loanAmount);
            put("creditScore", creditScore);
            put("pytBudget", budget);
            put("vehicleMake", vehicleMake);
            put("vehicleModel", vehicleModel);
            put("vehicleYear", vehicleYear);
            put("vehicleKms", vehicleKms);
            put("listPrice", listPrice);
            put("downpayment", downPayment);
        }};
        // convert request body to JSON string
        String queryBody = GSON.toJson(queryMap, Map.class);

        // get the Senso API URL and KEY from environment variables
        getEnvVars();

        // make the API call and collect response
        HttpResponse<String> response = httpRequest(queryBody);

        // construct the return data as a HashMap
        return new HashMap<>() {{
            put("status", response.statusCode());
            put("body", GSON.fromJson(response.body(), Map.class));
        }};
    }

    /**
     * Gets necessary environment variables.
     */
    private static void getEnvVars() throws MissingEnvironmentVariableException {
        // attempt to obtain the relevant environment variables
        String url = System.getenv("SENSO_API_URL");
        String key = System.getenv("SENSO_API_KEY");

        // if a relevant environment variable is not set, throw an error
        if (url == null) {
            throw new MissingEnvironmentVariableException(
                    "\n\n\t> \"SENSO_API_URL\" not specified in environment variables. \n"
            );
        }
        if (key == null) {
            throw new MissingEnvironmentVariableException(
                    "\n\n\t> \"SENSO_API_KEY\" not specified in environment variables. \n"
            );
        }

        // if the environment variables are set, set the corresponding private variables
        sensoUrl = url;
        sensoKey = key;
    }

    /**
     * Make an httpRequest with to the Senso /rate api with the specified requestBody.
     */
    private static HttpResponse<String> httpRequest(String requestBody)
            throws IOException, InterruptedException {
        // create an HTTP POST request
        var request = HttpRequest.newBuilder()
                .uri(URI.create(sensoUrl + "/rate"))
                .header("Content-Type", "application/json")
                .header("x-api-key", sensoKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        // create an HTTP client
        var client = HttpClient.newHttpClient();

        // use the HTTP client to send the request
        // TODO: Add exception catching for actual SensoApi query
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response;
    }

}