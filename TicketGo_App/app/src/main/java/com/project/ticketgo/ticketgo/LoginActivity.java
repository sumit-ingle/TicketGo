package com.project.ticketgo.ticketgo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 *
 * Project Name: 	IoT based prototype for Automated E-ticketing in suburban railways.
 * Author List: 		Pranav Gawane
 * Filename: 		LoginActivity.java
 * Functions: 		userLogin()
 * Global Variables:	 None
 */


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button buttonSignIn;
    private TextView textViewSignUp;
    private EditText editTextEmail;
    private EditText editTextPassword;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private Boolean checkedIn;/** to check whether the user is checked in at nfc terminal at station.*/
    private String current_checkin;/**to get the current checkin transaction from the firebase. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth =  FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null)
        {
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }


        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        buttonSignIn = (Button)findViewById(R.id.buttonSignIn);
        textViewSignUp = (TextView)findViewById(R.id.textViewSignUp);

        buttonSignIn.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);

    }

    /**
     *
     * Function Name: 	userLogin()
     * Input: 		email , password entered by user
     * Output: 		void
     * Logic: 		Function gets the email & password entered by user to sign in
     * using .signInWithEmailAndPassword(email,password) method of Firebase Authentication. Also validate users checked in status
     * and launch next activity accordingly.
     * Example Call:		userLogin();
     *
     */

    private void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this,"Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Signing In");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("USERS");
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                progressDialog.dismiss();
                if(task.isSuccessful())
                {
                    mRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.
                            // retrive checkedin status & current_checkin from firebase
                            checkedIn = dataSnapshot.child("checkedin").getValue(Boolean.class);
                            current_checkin = dataSnapshot.child("current_checkin").getValue(String.class);
                            Log.d("checkedin", "checkedin value: " + checkedIn +"current_checkin: "+current_checkin);
                            if (checkedIn) {
                                //if user has already checkedin start checkout process.
                                Intent checkinIntent = new Intent(LoginActivity.this, Checkin.class);
                                checkinIntent.putExtra("checkInStatus", checkedIn);
                                checkinIntent.putExtra("current_checkin", current_checkin);
                                finish();
                                startActivity(checkinIntent);
                            } else {
                                //user not checked in then start profile activity.
                                finish();
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
            }
        });
    }


    @Override
    public void onClick(View view) {
        if(view == buttonSignIn)
        {
            //user login
            userLogin();
        }
        if(view == textViewSignUp){
            // user registration
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}
