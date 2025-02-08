package com.transitor.group28;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;

public class PostalCodeRetriever {

    private static final String filePath = "/MassZipLatlon.csv";

    public PostalCode getDataZipCode(String zipcode) {
        PostalCode postalCode = null;

        try (InputStream inputStream = getClass().getResourceAsStream(filePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 3 && values[0].equals(zipcode)) {
                    double latitude = Double.parseDouble(values[1]);
                    double longitude = Double.parseDouble(values[2]);
                    postalCode = new PostalCode(latitude, longitude);
                    return postalCode; // Found the data, no need to continue searching
                }
            }

            // If data not found in CSV, fetch it from the API
            if (zipcode != null && !zipcode.isEmpty()) {
                DataAPI dataAPI = new DataAPI();
                dataAPI.getDataZipCode(zipcode);
                double latitude = Double.parseDouble(dataAPI.getLatitude());
                double longitude = Double.parseDouble(dataAPI.getLongitude());
                postalCode = new PostalCode(latitude, longitude);
                return postalCode;
            }

        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately, this might involve logging or other actions
        }

        return postalCode;
    }


}
