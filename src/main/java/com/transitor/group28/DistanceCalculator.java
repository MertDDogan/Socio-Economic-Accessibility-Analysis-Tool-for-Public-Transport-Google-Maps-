package com.transitor.group28;

/**
 * This class will be used for calculating distance (preferably using Haversine equation)
 * 
 * We will define the attributes later for both points in origin and destination 
 * 
 * We will have a function that calculates aerial distance and maybe another one for calculating the actual route using Graphhopper 
 */

public class DistanceCalculator {

     // The Earth's radius is approximately 6371 kilometers. We'll need it for the haversine formula
     private static final int EARTHS_RADIUS = 6371;

     // Average (usual) walking speed in m/s. Source: https://www.ncbi.nlm.nih.gov/pmc/articles/PMC7806575/
     private static final double WALKING_SPEED = 1.31;

    public double haversineDistance(double latitude1, double longitude1, double latitude2, double longitude2) {

        // Determining the difference between latitudes and longitudes and converting the latitudes to radians
        double diffLat = Math.toRadians(latitude2 - latitude1);
        double diffLon = Math.toRadians(longitude2 - longitude1);
        latitude1 = Math.toRadians(latitude1);
        latitude2 = Math.toRadians(latitude2);

        // Applying the haversine formula
        double a = Math.pow(Math.sin(diffLat / 2), 2)
                + Math.pow(Math.sin(diffLon / 2), 2)
                * Math.cos(latitude1) * Math.cos(latitude2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTHS_RADIUS * c;
    }

    // Walking time calculator which returns the speed in meters per minute.
    public double walkingTime(double distance) {
        return (distance / WALKING_SPEED) / 60; // Converts seconds to minutes
    }


}
