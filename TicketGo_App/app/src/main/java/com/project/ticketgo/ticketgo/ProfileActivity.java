package com.project.ticketgo.ticketgo;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;


/**
 *
 * Project Name: 	    IoT based prototype for Automated E-ticketing in suburban railways.
 * Author List: 	    Pranav Gawane
 * Filename: 		    ProfileActivity.java
 * Functions: 	        displaySelectedScreen()
 * Global Variables:	None
 */


public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,AdapterView.OnItemSelectedListener{
    private FirebaseAuth firebaseAuth;
    private TextView textViewUserEmail ;
    Double  curBal , uBal , amt ;
    /* curBal to get users current balance from firebase , uBal to update user balance amount ,
     amt to get the amount of payment transaction made by the user.*/
    FirebaseUser user;
    private DatabaseReference mRef; // database reference object.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        } //will clear the activitystack and exit from app on back button pressed in profileactivity.

        user = FirebaseAuth.getInstance().getCurrentUser(); //get current user logged in the app.
        mRef = FirebaseDatabase.getInstance().getReference("USERS");
        // Read from the database

        mRef.child(user.getUid()).child("balance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                curBal = dataSnapshot.getValue(Double.class);
                Log.d("pbal", "Value is: " + curBal);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("pbal", "Failed to read value.", error.toException());
            }
        });



        firebaseAuth = FirebaseAuth.getInstance();

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);
       textViewUserEmail = hView.findViewById(R.id.textViewUserEmail);
        textViewUserEmail.setText(firebaseAuth.getCurrentUser().getEmail());


        displaySelectedScreen(R.id.nav_home); //this method loads the fragment onto the profile activity.
        // Home is the default fragment hence called here.

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            //clears the activity stack and updates the exit status.
            Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent); //intent checks app exit status in onCreate().
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    /**
     *
     * Function Name: 	displaySelectedScreen()
     * Input: 		item id of fragment view from nav drawer.
     * Output: 		selected fragment
     * Logic: 		Function gets the item id of fragment from nav drawer view &
     *              replaces the fragments.
     * Example Call:		 displaySelectedScreen(R.id.nav_home);
     */
    private void displaySelectedScreen(int itemId) {

        android.support.v4.app.Fragment fragment = null;
        //creating fragment object


        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_home:
                getSupportActionBar().setTitle("Home");
                fragment = new Home();
                break;
            case R.id.nav_payment:
                getSupportActionBar().setTitle("Wallet");
                fragment = new WalletFragment();
                break;
            case R.id.nav_signout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(this , LoginActivity.class));
                break;
            case R.id.nav_about:
                getSupportActionBar().setTitle("About Us");
                fragment = new AboutUs();
                break;

        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        displaySelectedScreen(item.getItemId()); //item id passed to the function.
        return true;
    }


    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
        //Toast.makeText(getApplicationContext(),country[position] ,Toast.LENGTH_LONG).show();
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

//this method is called when the payment transaction is completed & returned to wallet fragment.
    //this method is used to update the recharged amount by user to the users balance in our database.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

// Result Code is -1 send from Payumoney activity
        Log.d("Profile", "request code " + requestCode + " resultcode " + resultCode);
        ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == -1 && data != null) {
            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE);
            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
                if(transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)){
//Successful Transaction
                    Toast.makeText(getApplicationContext(),"Payment successful", Toast.LENGTH_LONG).show();
                   String payuResponse = transactionResponse.getPayuResponse(); // Response string from Payumoney.
/** We are using the response string recieved from PayU money gateway
 to retrieve the amount of transaction made by user to add that amount to users balance. */
                    Integer i = payuResponse.lastIndexOf("amount"); //to get the index of amount value from the payuResponse string.
                    String test = payuResponse.substring(i+15,i+25); //this string gets the substring which contains the amount value.
                    //in the payuResponse string the actual amount value starts from index i+15 , so we got the substring from i+15 to i+25.
                    String amount = ""; //to get value of amount.
                    int j = 0; // loop iteration variable
                    /** this loop will run between the Quotes("") which contain amount value.
                     * Eg :- "50" will get value 50 in string "amount"*/
                    while(test.charAt(j)!='\"')
                    {
                        amount = amount + Character.toString(test.charAt(j));
                        j++;
                    }
                    Log.d("trans","trans detail: "+payuResponse+"amount: "+amount);
                    amt = Double.parseDouble(amount); // to parse the string 'amount' to get value in Double for transaction amount.

                    uBal = curBal + amt; // Updated balance = current balance + payed amount.
                    mRef.child(user.getUid()).child("balance").setValue(uBal);// updates users balance on firebase.
                    Log.d("ubal","curbal"+curBal+" amt "+amt+"updated balance "+uBal);

                } else{
//Failure Transaction
                }


// Response from SURl and FURL
                String merchantResponse = transactionResponse.getTransactionDetails();
                //Toast.makeText(getApplicationContext(),"PayU response"+payuResponse, Toast.LENGTH_SHORT).show();

               // Toast.makeText(getApplicationContext(),"Merchant response"+merchantResponse, Toast.LENGTH_SHORT).show();
            }  else if (resultModel != null && resultModel.getError() != null) {
                Log.d("Wallet", "Error response : " + resultModel.getError().getTransactionResponse());
            } else {
                Log.d("Wallet", "Both objects are null!");
            }
        }
    }

}
