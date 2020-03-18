package com.project.ticketgo.ticketgo;

import  android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
/**
	*
	* Project Name: 	IoT based prototype for Automated E-ticketing in suburban railways.
	* Author List: 		Pranav Gawane
	* Filename: 		MainActivity.java
	* Functions: 		registerUser()
	* Global Variables:	None
*/

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    private Button buttonSignUp;
    private EditText editTextEmail;
    private EditText editTextPassword;

    private TextView textViewSignIn;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSignUp = findViewById(R.id.buttonSignUp);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        textViewSignIn = findViewById(R.id.textViewSignIn);

        buttonSignUp.setOnClickListener(this);
        textViewSignIn.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);


    }

    /**
	*
	* Function Name: 	registerUser()
	* Input: 		email , password entered by user
	* Output: 		void
	* Logic: 		Function gets the email & password entered by user to create a user on Firebase
	* using .createUserWithEmailAndPassword(email,password) method of Firebase Authentication.
	* Example Call:		registerUser();
	*
	*/

    private void registerUser(){

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

        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        firebaseAuth = FirebaseAuth.getInstance();
        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //checking if success
                        if (task.isSuccessful()) {
                            //call UserDetails activity to get user details.
                            Toast.makeText(MainActivity.this, "Sucessfully Registered", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(), UserDetails.class));
                        } else {
                            Toast.makeText(MainActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();

                        }
                        progressDialog.dismiss();


                    }
                });

    }



    @Override
    public void onClick(View view) {
        if(view == buttonSignUp)
        {
            registerUser();
        }
        if(view == textViewSignIn)
        {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            //will open login activity

            Toast.makeText(this,"Login", Toast.LENGTH_SHORT).show();
            return;
        }

}
}


