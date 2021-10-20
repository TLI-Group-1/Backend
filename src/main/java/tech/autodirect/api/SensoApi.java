package tech.autodirect.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SensoApi{

    public static void queryApi() throws IOException, InterruptedException {
        // create a client
        String inputJson = "{\n" +
                "   \"loanAmount\": 10000,\n" +
                "   \"creditScore\": 780,\n" +
                "   \"pytBudget\": 800,\n" +
                "   \"vehicleMake\": \"Honda\",\n" +
                "   \"vehicleModel\": \"Civic\",\n" +
                "   \"vehicleYear\": 2021,\n" +
                "   \"vehicleKms\": 1000\n" +
                "}";

        // create a request
        var request = HttpRequest.newBuilder()
                .uri(URI.create("https://auto-loan-api.senso.ai/rate"))
                .header("Content-Type", "application/json")
                .header("x-api-key", "AIzaSyCD_-qCdXqrvWGHN1tpe2PH6Rf8zpnTdXs")
                .POST(HttpRequest.BodyPublishers.ofString(inputJson))
                .build();

        // use the client to send the request
        var client = HttpClient.newHttpClient();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // the response:
        System.out.println(response.statusCode());
        System.out.println(response.body());

    }

    public static void inputs() throws IOException {
        System.out.print("Enter your name: ");
        BufferedReader nameReader =
                new BufferedReader(new InputStreamReader(System.in));
        String name = nameReader.readLine();
        System.out.println("Hello " + name + "! Pretty please help us by inputting the following information...");

        System.out.print("Enter the loan amount: ");
        BufferedReader loanReader =
                new BufferedReader(new InputStreamReader(System.in));
        String loanAmount = loanReader.readLine();

        System.out.print("Enter your credit score: ");
        BufferedReader creditReader =
                new BufferedReader(new InputStreamReader(System.in));
        String creditScore = creditReader.readLine();

        System.out.print("Enter your monthly payment Budget: ");
        BufferedReader pytReader =
                new BufferedReader(new InputStreamReader(System.in));
        String pytBudget = pytReader.readLine();
    }
}