package com.transitor.group28;

import java.sql.Time;
import java.time.Duration;
import java.time.LocalTime;

/**
 * This is the object that we will use to measure the total time that it will take to reach a certain destination
 * by going through two bus stops plus the walking distance involved.
 */

public class PathTime {

    private BusStop stopA;
    private BusStop stopB;
    private PostalCode source;
    private PostalCode destination;

    private double totalTime;

    //*Postal codes in this class represent the source and destination set by the user
    public PathTime(PostalCode source, PostalCode destination, BusStop stopA, BusStop stopB){
        this.source = source;
        this.destination = destination;
        this.stopA = stopA;
        this.stopB = stopB;
        this.totalTime = setTime(source, destination, stopA, stopB);
    }

    //*This method will compute the total time.
    public double setTime(PostalCode source, PostalCode dest, BusStop stopA, BusStop stopB){
        double time = 0; //total time that it takes in minutes

        DistanceCalculator calc = new DistanceCalculator();

        //Calculates the total time taken for the trip.
        //For walking distance it only takes into consideration aerial distance and not the actual route distance.
        //It does not take into account the time in which the user has made the search.
        //These method calls are too long... These are a code smell... I apologize for this
        time = calc.walkingTime(calc.haversineDistance(source.getLat(), source.getLon(), stopA.getLat(), stopA.getLon()))
                + durationBusTimes(stopA.getDepartureTime(), stopB.getDepartureTime())
                + calc.walkingTime(calc.haversineDistance(stopB.getLat(), stopB.getLon(), dest.getLat(), dest.getLon()));

        return time;
    }

    //*Helper method for setTime
    public float durationBusTimes(Time source, Time destination) {
        long secondsDifference;
        if (destination.after(source)) {
            secondsDifference = Duration.between(source.toLocalTime(), destination.toLocalTime()).getSeconds();
        } else if (destination.before(source)) {
            // If destination time is earlier than source time (crossing midnight),
            // add the seconds in a day to the difference
            secondsDifference = Duration.between(source.toLocalTime(), LocalTime.MAX).getSeconds() +
                    Duration.between(LocalTime.MIN, destination.toLocalTime()).getSeconds();
        } else {
            // If source and destination times are the same, return 0
            return 0;
        }
        long minutes = secondsDifference / 60;
        return minutes;
    }



    public BusStop getStopA() {
        return stopA;
    }

    public BusStop getStopB() {
        return stopB;
    }

    public PostalCode getSource() {
        return source;
    }

    public PostalCode getDestination() {
        return destination;
    }

    public double getTotalTime() {
        return totalTime;
    }



}
