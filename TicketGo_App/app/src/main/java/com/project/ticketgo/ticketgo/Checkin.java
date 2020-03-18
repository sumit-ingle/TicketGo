package com.project.ticketgo.ticketgo;

import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.os.AsyncTask;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.Date;


import be.appfoundry.nfclibrary.activities.NfcActivity;
import be.appfoundry.nfclibrary.exceptions.InsufficientCapacityException;
import be.appfoundry.nfclibrary.exceptions.ReadOnlyTagException;
import be.appfoundry.nfclibrary.exceptions.TagNotPresentException;
import be.appfoundry.nfclibrary.tasks.interfaces.AsyncOperationCallback;
import be.appfoundry.nfclibrary.tasks.interfaces.AsyncUiCallback;
import be.appfoundry.nfclibrary.utilities.async.WriteCallbackNfcAsync;
import be.appfoundry.nfclibrary.utilities.async.WriteEmailNfcAsync;
import be.appfoundry.nfclibrary.utilities.interfaces.NfcWriteUtility;
import be.appfoundry.nfclibrary.utilities.interfaces.WriteUtility;
import be.appfoundry.nfclibrary.utilities.sync.NfcWriteUtilityImpl;

/**
 *
 * Project Name: 	    IoT based prototype for Automated E-ticketing in suburban railways.
 * Author List: 		Pranav Gawane, Sumit Ingle.
 * Filename:            Checkin.java
 * Functions:           onNewIntent(Intent), onDestroy()
 * Global Variables:	None
 */


public class Checkin extends NfcActivity {
    TextView txtPassengers, txtSource, txtTime;
    String[] nfcdata; //data received from NFC terminal
    String checkinTime, checkoutTime;
    String source, destination, current_checkin, timestamp;
    int passengers; // to get no of passengers from user/firebase.
    private boolean checkedIn; //gives checked in status of user.
    private DatabaseReference tRef, mRef; //access data from Firebase
    private FirebaseUser user; //instance of the user that gives user details
    SimpleDateFormat sdf; //to convert date into a preferred format
    NfcWriteUtilityImpl writeUtilityImpl; //write data to nfc tag
    transactionDetails td;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);
        getSupportActionBar().setTitle("Check In");
        txtPassengers = findViewById(R.id.txtPassengers);
        txtSource = findViewById(R.id.txtSource);
        txtTime = findViewById(R.id.txtTime);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference("USERS").child(user.getUid());
        tRef = FirebaseDatabase.getInstance().getReference("TRANSACTIONS");

        passengers = getIntent().getIntExtra("noOfpassengers",1)+1;
        checkedIn = getIntent().getBooleanExtra("checkInStatus",false);
        current_checkin = getIntent().getStringExtra("current_checkin");
        td = new transactionDetails();
        td.setTimestamp(current_checkin);
        sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        /*Checking if the user is already checked into a terminal. If he is checked in
        * already, then show him the checkin details from firebase*/

        if(checkedIn) {
            checkinTime = sdf.format(new Date(Long.parseLong(current_checkin)*1000));
            //Query lastQuery = tRef.orderByChild("timestamp").limitToLast(1);
            tRef.child(user.getUid()).child(current_checkin).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    source = dataSnapshot.child("source").getValue(String.class);
                    passengers = dataSnapshot.child("passengers").getValue(Integer.class);
                   // timestamp = dataSnapshot.child("current_checkin").getValue(String.class);
                    Log.d("transaction", "values: "+source +" "+passengers);
                    txtSource.setText("Source : "+source);
                    Log.d("src", "values: "+source +" "+destination+" "+passengers);
                    //txtDestination.setText("destination");
                    txtPassengers.setText("Passengers : "+String.valueOf(passengers));
                    txtTime.setText("Time In: "+checkinTime);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("bal", "Failed to read value.", error.toException());
                }
            });

        }
        /*If the user isn't checked in, he will just see the number of passengers he has
        * entered in the previous screen*/
        else {
            txtPassengers.setText("Passengers : "+String.valueOf(getIntent().getIntExtra("noOfpassengers",1)+1));
        }

    }

    /*
	* Function Name:	onNewIntent
	* Input:		    The data from NFC terminal is captured by onNewIntent
	* Output:           None
	* Logic:		    First, check if the user is checked in or not. If not: this means that
	*                   the passenger wants to check in from this terminal. Hence, update his
	*                   status to checkedIn = true and store the data from terminal to firebase
	*                   This indicates that the user has successfully checked in on this terminal.
	*
	*                   Now, if the user is already checked in ie checkedIn = true, then this means
	*                   that he wants to checkout from this terminal. In this case, send the passenger
	*                   to the fare activity to calculate the fare.
	* Example Call:     Cannot be directly called.
	*
	*/

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (!checkedIn) {
            for (String message : getNfcMessages()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                try {
                    writeUtilityImpl = new NfcWriteUtilityImpl();
                    writeUtilityImpl.writeTextToTagFromIntent(String.valueOf(passengers),intent);
                    Toast.makeText(this,"write done",Toast.LENGTH_LONG).show();
                } catch (FormatException e) {
                    e.printStackTrace();
                } catch (TagNotPresentException e) {
                    e.printStackTrace();
                } catch (ReadOnlyTagException e) {
                    e.printStackTrace();
                } catch (InsufficientCapacityException e) {
                    e.printStackTrace();
                }
                nfcdata = message.split("\\|");
                source = nfcdata[0].substring(2);
                timestamp = nfcdata[1];
                td.setTimestamp(timestamp);
                checkinTime = sdf.format(new Date(Long.parseLong(checkoutTime)*1000));
                //Log.d("nfcdata","source: "+nfcdata[0]+" timestamp: "+nfcdata[1]);
                txtSource.setText("Source: " + source);
                txtTime.setText("Time: " + timestamp);
                transactionDetails transactiondetails = new transactionDetails(source , "" , passengers);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    tRef.child(user.getUid()).child(timestamp).setValue(transactiondetails);
                    mRef.child("checkedin").setValue(true);
                    checkedIn = true;
                    mRef.child("current_checkin").setValue(timestamp);
                    Toast.makeText(this, "Checked In", Toast.LENGTH_SHORT).show();
                getSupportActionBar().setTitle("Check Out");

                }
            }

        else {
            for (String message : getNfcMessages()) {
                nfcdata = message.split("\\|");
                destination = nfcdata[0].substring(2);
                checkoutTime = nfcdata[1];
                try {
                    writeUtilityImpl = new NfcWriteUtilityImpl();
                    writeUtilityImpl.writeTextToTagFromIntent(String.valueOf(passengers),intent);
                    Toast.makeText(this,"write done",Toast.LENGTH_LONG).show();
                } catch (FormatException e) {
                    e.printStackTrace();
                } catch (TagNotPresentException e) {
                    e.printStackTrace();
                } catch (ReadOnlyTagException e) {
                    e.printStackTrace();
                } catch (InsufficientCapacityException e) {
                    e.printStackTrace();
                }
                //sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                if (!source.equals(destination)) {
                    checkoutTime = sdf.format(new Date(Long.parseLong(checkoutTime)*1000));
                    //Log.d("nfcdata","source: "+nfcdata[0]+" timestamp: "+nfcdata[1]);
                    /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    tRef.child(user.getUid()).child(transactionDetails.timestamp).child("destination").setValue(destination);
                    tRef.child(user.getUid()).child(transactionDetails.timestamp).child("checkin_time").setValue(checkinTime);
                    tRef.child(user.getUid()).child(transactionDetails.timestamp).child("checkout_time").setValue(checkoutTime);
                    mRef.child("checkedin").setValue(false);*/
                    Log.d("nfcdata","nfc message: "+message);
                    Log.d("checkout","destination: "+destination+" checkoutTime: "+checkoutTime);
                    Intent fareIntent = new Intent(Checkin.this,Fare.class);
                    //fareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    //send the user to fare activity since he is done with checkout
                    fareIntent.putExtra("source", source);
                    fareIntent.putExtra("passengers", passengers);
                    fareIntent.putExtra("destination", destination);
                    finish();
                    startActivity(fareIntent);

                }
                //Toast.makeText(this, "Checked Out", Toast.LENGTH_LONG).show();

            }

        }
    }

    /*
	* Function Name:	onDestroy
	* Input:		    None
	* Output:           None
	* Logic:		    Use the tRef database reference to store transaction details on firebase
	*                   when this activity ends.
	* Example Call:     Cannot be directly called.
	*
	*/

    @Override
    protected void onDestroy() {

        Log.d("test_chckin", "onDestroy chckin");

        if (tRef == null) {
            super.onDestroy();
        }
        else {
            timestamp = td.getTimestamp();
            super.onDestroy();
            // get destination from nfc and send it to firebase along with check in & out time.
            if (td != null) {
                tRef.child(user.getUid()).child(timestamp).child("destination").setValue(destination);
                tRef.child(user.getUid()).child(timestamp).child("checkin_time").setValue(checkinTime);
                tRef.child(user.getUid()).child(timestamp).child("checkout_time").setValue(checkoutTime);
            }
        }

    }
}

