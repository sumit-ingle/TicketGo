package com.project.ticketgo.ticketgo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static java.lang.Math.abs;

/**
 * Project Name: 	    IoT based prototype for Automated E-ticketing in suburban railways.
 * Author List: 		Pranav Gawane
 * Filename: 		    Fare.java
 * Functions: 	        calcFare()
 * Global Variables:	None
 */

public class Fare extends AppCompatActivity {

    TextView txtNOP, txtSrc, txtDest, txtFare, txtBalance;

    String src, dest; //src for getting source station & dest for destination station.
    int nop; //no. of passengers.
    Double fare, curBal, uBal;/** fare- calculated fare for journey,
     curBal- get users current balance from firebase , uBal- update user's balance amount */
    Integer stn1, stn2, diff;/* stn1- source station no , stn2- destination station no ,
    diff- gives the no of stations travelled between source & destination.*/
    FirebaseUser user;
    private DatabaseReference mRef, bRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fare);


        txtNOP = findViewById(R.id.txtNOP);
        txtSrc = findViewById(R.id.txtSrc);
        txtDest = findViewById(R.id.txtDest);
        txtFare = findViewById(R.id.txtFare);
        txtBalance = findViewById(R.id.txtBalance);

        // get nop, source & destination through intent.
        nop = getIntent().getIntExtra("passengers", 1);
        src = getIntent().getStringExtra("source");
        dest = getIntent().getStringExtra("destination");

        txtNOP.setText("Passengers : " + String.valueOf(getIntent().getIntExtra("passengers", 1)));
        txtSrc.setText("Source : " + src);
        txtDest.setText("Destination : " + dest);

        Log.d("fare", " nop " + nop);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference("STATIONS");
        bRef = FirebaseDatabase.getInstance().getReference("USERS").child(user.getUid());

        // Read current balance from firebase.
        bRef.child("balance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                curBal = dataSnapshot.getValue(Double.class);
                Log.d("stn", "curBal " + curBal);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("stn", "Failed to read value.", error.toException());
            }
        });

        // Read station no of source from firebase.
        mRef.child(src).child("stnno").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                stn1 = dataSnapshot.getValue(Integer.class);
                Log.d("stn", "stn1 " + stn1);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("stn", "Failed to read value.", error.toException());
            }
        });

        // Read station no of destination from firebase.
        mRef.child(dest).child("stnno").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                stn2 = dataSnapshot.getValue(Integer.class);
                Log.d("stn", "stn2 " + stn2);
                calcFare();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("stn", "Failed to read value.", error.toException());
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //set updated balance on firebase.
        bRef.child("balance").setValue(uBal);
        //set checkin status as false i.e checked out.
        bRef.child("checkedin").setValue(false);
    }


    /**
     *
     * Function Name: 	calcFare()
     * Input: 		stn1 , stn2 , nop ,curBal.
     * Output: 		calculated fare, remaining balance &  updated balance.
     * Logic: 		Function calculates the no of stations between source & destination
     * and calculates fare based on it & deducts it from users balance.
     * We have considered the mumbai metro stations and numbered them in order. Thus difference between station nos
     * will give us the count of stations travelled. Fare for every 4 stations is 5rs.
     * Example Call:		 calcFare();
     */
    private void calcFare() {
        diff = abs((stn1 - stn2)) + 1;//no. of stations traveled including first station.
        fare = Math.ceil((double) diff / 4) * 5 * nop; //formula for fare set by us. Refer  function logic.
        uBal = curBal - fare;// Updated balance = current balance - fare .
        Log.d("stnno", "diff " + diff + "Fare " + fare + "Bal " + uBal);
        txtFare.setText("Fare : " + fare);
        txtBalance.setText("Balance : " + uBal);
        //fare & balance display.
    }
}
