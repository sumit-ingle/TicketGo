package com.project.ticketgo.ticketgo;

/**
 * Created by Pranav on 26-12-2017.
 */
/**
 * Project Name: 	    IoT based prototype for Automated E-ticketing in suburban railways.
 * Author List: 		Pranav Gawane
 * Filename: 		    saveUserDetails.java
 * Functions: 	        None
 * Global Variables:	None
 */

public class saveUserDetails {

    public String uname , phone , aadhar , dob , current_checkin;/*uname- username, phone-  mobile no.,
     aadhar - Aadhar card no.,dob - date of birth. ,current_checkin - timestamp of current checkin */
    public  Double balance; // value of users balance. Initially null.
    public  Boolean checkedin; //checked in status. Initially false.


    public saveUserDetails(){

    }

    public saveUserDetails(String uname, String phone, String aadhar, String dob , Double balance , Boolean checkedin, String current_checkin) {
        this.uname = uname;
        this.phone = phone;
        this.aadhar = aadhar;
        this.dob = dob;
        this.balance = balance;
        this.checkedin = checkedin;
        this.current_checkin = current_checkin;
    }
}
