package com.project.ticketgo.ticketgo;

/**
 * Created by Pranav on 02-02-2018.
 */

/**
 * Project Name: 	    IoT based prototype for Automated E-ticketing in suburban railways.
 * Author List: 		Pranav Gawane
 * Filename: 		    transactionDetails.java
 * Functions: 	        None
 * Global Variables:	None
 */

public class transactionDetails {

    public String source , destination;
    public int passengers ;
    public String timestamp = ""; //time of checkin.

     transactionDetails(){

    }

    String getTimestamp()
    {
        return this.timestamp;
    }

    void setTimestamp(String newTimeStamp)
    {
        this.timestamp = newTimeStamp;
    }

    transactionDetails(String source, String destination, Integer passengers) {
        this.source = source;
        this.destination = destination;
        this.passengers = passengers;

    }
}
