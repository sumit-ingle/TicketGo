package com.project.ticketgo.ticketgo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 *
 * Project Name: 	    IoT based prototype for Automated E-ticketing in suburban railways.
 * Author List: 		Pranav Gawane
 * Filename: 		    UserDetails.java
 * Functions: 		    saveUserDetail()
 * Global Variables:	None
 */

public class UserDetails extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private Button buttonSave;
    private EditText editTextUName, editTextPhone, editTextAadhar, editTextDOB;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        buttonSave =  findViewById(R.id.buttonSave);

        editTextUName =  findViewById(R.id.editTextUName);
        editTextPhone =  findViewById(R.id.editTextPhone);
        editTextAadhar =  findViewById(R.id.editTextAadhar);
        editTextDOB =  findViewById(R.id.editTextDOB);

        buttonSave.setOnClickListener(this);
    }

    /**
     *
     * Function Name: 	saveUserDetail()
     * Input: 		    Users name ,phone no , aadhar card no. , date of birth entered by user.
     * Output: 		    save user details on firebase.
     * Logic: 		Function  saves user details on firebase & set initial balance as 0.
     * Example Call:		saveUserDetail();
     *
     */


    private void saveUserDetail() {

        String uname = editTextUName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String aadhar = editTextAadhar.getText().toString().trim();
        String dob = editTextDOB.getText().toString().trim();
        Double balance = 0.0;
        Boolean checkedin = false;
        String current_checkin ="";
        saveUserDetails saveuserdetails = new saveUserDetails(uname, phone, aadhar, dob , balance , checkedin , current_checkin);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference.child("USERS").child(user.getUid()).setValue(saveuserdetails);

        Toast.makeText(this, "Information saved", Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onClick(View v) {

        if (v == buttonSave) {
            saveUserDetail();
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }
    }
}