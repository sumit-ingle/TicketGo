package com.project.ticketgo.ticketgo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 *
 * Project Name: 	    IoT based prototype for Automated E-ticketing in suburban railways.
 * Author List: 	    Sumit Ingle
 * Filename: 		    SplashScreen.java
 * Functions: 		    None
 * Global Variables:	None
 */




public class SplashScreen extends AppCompatActivity {
    /** This variable is used to maintain the ongoing checkin transaction details on firebase.*/
    String current_checkin;  /**to get the current checkin transaction from the firebase. */

    Boolean checkedIn;  /** to check whether the user is checked in at nfc terminal at station.*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Showing splashscreen while making network calls to download necessary
         * data before launching the app from firebase. Will use AsyncTask to make http call
         */

        DatabaseReference mRef;
        FirebaseUser user;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            /**that means user is already logged in
            so close this activity & decide whether profile or checkin activity is to be launched.*/
            mRef = FirebaseDatabase.getInstance().getReference("USERS"); // database ref for root's "USERS" child.
            user = FirebaseAuth.getInstance().getCurrentUser();

            mRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.

                    checkedIn = dataSnapshot.child("checkedin").getValue(Boolean.class); //read checkin status from firebase
                    current_checkin = dataSnapshot.child("current_checkin").getValue(String.class); //read current checkin transaction id from firebase
                    Log.d("checkedin", "checkedin value: " + checkedIn +"current_checkin: "+current_checkin);
                    if(checkedIn)
                    {
                        //if users has already checked into a station then start Checkin activity directly.
                        Intent checkInIntent = new Intent(getApplicationContext(),Checkin.class);
                        checkInIntent.putExtra("checkInStatus",checkedIn);
                        checkInIntent.putExtra("current_checkin",current_checkin);
                        finish();
                        startActivity(checkInIntent);
                    }
                    else
                    {
                        //user is not checked into the station
                        finish();
                        //and open profile activity
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("checkedin", "Failed to read value.", error.toException());
                }
            });

        }

        else {
            Intent loginIntent = new Intent(SplashScreen.this,LoginActivity.class);
            finish();
            startActivity(loginIntent);
        }

        //new FetchData().execute();
    }



}