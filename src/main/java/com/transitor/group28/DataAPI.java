package com.transitor.group28;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataAPI {

    private String latitude;
    private String longitude;
    private String zipcode;

    public void getDataZipCode(String postcode) {
        try {
            // Create a HttpClient
            HttpClient client = HttpClient.newHttpClient();

            // JSON payload for the request; adjust this according to your API's needs
            String json = "{\"postcode\": \"" + postcode + "\"}";

            // Construct the URI (without query parameters since we'll use POST)
            String uriString = "https://project12.ashish.nl/get_coordinates";

            zipcode = postcode;

            // Create a request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uriString))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json)) // This is how you change to a POST request
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String responseBody = response.body();

            System.out.println(responseBody);

            latitude = getValue(responseBody, "\"latitude\":\\s*\"([^\"]+)\"");
            longitude = getValue(responseBody, "\"longitude\":\\s*\"([^\"]+)\"");

            System.out.println("API successfully accessed");

        } catch (Exception e) {
            // Handle any errors and return an empty string
            System.out.println("Error during API call: " + e.getMessage());
        }
    }

    private String getValue(String input, String regex) {
        Pattern APIpattern = Pattern.compile(regex);
        Matcher patternMatcher = APIpattern.matcher(input);
        if (patternMatcher.find()) {
            return patternMatcher.group(1);
        } else {
            return null;
        }
    }

    public String getLatitude(){
        return latitude;
    }

    public String getLongitude(){
        return longitude;
    }

    public String getZipcode(){
        return zipcode;
    }

    @Override
    public String toString() {
        return "DataAPI{" +
                "latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", zipcode='" + zipcode + '\'' +
                '}';
    }

    public static void main(String[] args) {
        DataAPI dataAPI = new DataAPI();
        dataAPI.getDataZipCode("6229EN");
    }


}
