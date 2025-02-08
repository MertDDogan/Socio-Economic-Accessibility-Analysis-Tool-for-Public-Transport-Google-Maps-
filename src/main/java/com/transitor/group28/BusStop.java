package com.transitor.group28;

import java.sql.Time;
import java.time.LocalTime;

/**
 * This class will contain the atributes of the bus stops from the databases, 
 * such as: stop ID, departure time, etc. 
 * 
 * 
 * We will need to set the attributes for this object and create some getter methods for this one for the algorithm 
 */

public class BusStop {
    
    
    private double stopLat, stopLon;
    private String stopID; 
    private Time departureTime; //departure time of the bus stop 

    
    
    public BusStop(String stopID, double stopLat, double stopLon, Time departureTime){
        this.stopLat = stopLat; 
        this.stopLon = stopLon; 
        this.stopID = stopID;
        this.departureTime = departureTime; 
    }



    public void setDepartureTime(Time departureTime) {
        this.departureTime = departureTime;
    }

    public double getLat() {
        return stopLat;
    }

    public double getLon() {
        return stopLon;
    }

    public Time getDepartureTime() {
        return departureTime;
    }

    public String getStopID() {
        return stopID;
    }


}
