package com.project.ticketgo.ticketgo;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneyConstants;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;
import com.payumoney.core.entity.TransactionResponse;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Ref;
import java.util.Iterator;

/**
 * Created by Mustafa on 19-01-2018.
 */

/**
 *
 * Project Name: 	    IoT based prototype for Automated E-ticketing in suburban railways.
 * Author List: 		Mustafa Bhatkar
 * Filename: 		    WalletFragment.java
 * Functions:           launchPayUMoneyFlow(),generateHashFromServer.
 * Global Variables:	None
 */

public class WalletFragment extends Fragment {
    FirebaseUser user;
    private DatabaseReference balRef;
    Button btnInitiatePayment;
    EditText txtAmount;
    TextView txtBalance;
    private PayUmoneySdkInitializer.PaymentParam mPaymentParams;
    String phone ;/*= "8080808080"*/
    String productName = "WalletFragment Recharge";
    Double amount;
    String firstName ;
    String txnId = "" + System.currentTimeMillis();
    String email;
    String sUrl = "https://test.payumoney.com/mobileapp/payumoney/success.php";
    String fUrl = "https://test.payumoney.com/mobileapp/payumoney/failure.php";
   public Double curBal , uBal;

    String key = "LLKwG0";
    String merchantId = "393463";
    String salt = "qauKbEAJ";
    String udf1 = "";
    String udf2 = "";
    String udf3 = "";
    String udf4 = "";
    String udf5 = "";
    String udf6 = "";
    String udf7 = "";
    String udf8 = "";
    String udf9 = "";
    String udf10 = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wallet_layout, container , false);
        btnInitiatePayment = view.findViewById(R.id.btnInitiatePayment);

        txtAmount = view.findViewById(R.id.txtAmount);
        txtBalance = view.findViewById(R.id.txtBalance);

        user = FirebaseAuth.getInstance().getCurrentUser();
        email = user.getEmail();

        balRef = FirebaseDatabase.getInstance().getReference("USERS");


        // Read from the database
        balRef.child(user.getUid()).child("balance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                Double curBal = dataSnapshot.getValue(Double.class);
                txtBalance.setText("Balance : "+curBal.toString());
                Log.d("bal", "Fragment Value is: " + curBal);
                //Intent curBalIntent = new Intent(WalletFragment.this.getActivity(), ProfileActivity.class);
               // curBalIntent.putExtra("curBal",curBal);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("bal", "Failed to read value.", error.toException());
            }
        });
        balRef.child(user.getUid()).child("phone").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                phone= dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("bal", "Failed to read value.", error.toException());
            }
        });
        balRef.child(user.getUid()).child("uname").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                firstName= dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("bal", "Failed to read value.", error.toException());
            }
        });


        btnInitiatePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtAmount.getText().length()>0) {
                    amount = Double.parseDouble(txtAmount.getText().toString());

                    launchPayUMoneyFlow();
                } else {
                    Toast.makeText(getActivity(), "PLease Enter The Amount!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void launchPayUMoneyFlow() {

        PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();

        payUmoneyConfig.setPayUmoneyActivityTitle("Add Money");
        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();

        builder.setAmount(amount)
                .setTxnId(txnId)
                .setPhone(phone)
                .setProductName(productName)
                .setFirstName(firstName)
                .setEmail(email)
                .setsUrl(sUrl)
                .setfUrl(fUrl)
                .setUdf1(udf1)
                .setUdf2(udf2)
                .setUdf3(udf3)
                .setUdf4(udf4)
                .setUdf5(udf5)
                .setUdf6(udf6)
                .setUdf7(udf7)
                .setUdf8(udf8)
                .setUdf9(udf9)
                .setUdf10(udf10)
                .setIsDebug(true)
                .setKey(key)
                .setMerchantId(merchantId);

        try {
            mPaymentParams = builder.build();


            generateHashFromServer(mPaymentParams);




        } catch (Exception e) {
            // some exception occurred
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void generateHashFromServer(PayUmoneySdkInitializer.PaymentParam paymentParam) {
        //nextButton.setEnabled(false); // lets not allow the user to click the button again and again.

        // lets create the post params
        StringBuffer postParamsBuffer = new StringBuffer();
        postParamsBuffer.append(concatParams(PayUmoneyConstants.KEY, key));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.AMOUNT, Double.toString(amount)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.TXNID,txnId));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.EMAIL, email));
        postParamsBuffer.append(concatParams("productinfo", productName));
        postParamsBuffer.append(concatParams("firstname", firstName));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF1, udf1));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF2, udf2));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF3, udf3));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF4, udf4));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF5, udf5));

        String postParams = postParamsBuffer.charAt(postParamsBuffer.length() - 1) == '&' ? postParamsBuffer.substring(0, postParamsBuffer.length() - 1).toString() : postParamsBuffer.toString();

        // lets make an api call
        GetHashesFromServerTask getHashesFromServerTask = new GetHashesFromServerTask();
        getHashesFromServerTask.execute(postParams);

    }


    protected String concatParams(String key, String value) {
        return key + "=" + value + "&";
    }


    private class GetHashesFromServerTask extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... postParams) {

            String merchantHash = "";
            try {
                //TODO Below url is just for testing purpose, merchant needs to replace this with their server side hash generation url
                URL url = new URL("https://payu.herokuapp.com/get_hash");

                String postParam = postParams[0];

                byte[] postParamsByte = postParam.getBytes("UTF-8");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postParamsByte);

                InputStream responseInputStream = conn.getInputStream();
                StringBuffer responseStringBuffer = new StringBuffer();
                byte[] byteContainer = new byte[1024];
                for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                    responseStringBuffer.append(new String(byteContainer, 0, i));
                }

                JSONObject response = new JSONObject(responseStringBuffer.toString());

                Iterator<String> payuHashIterator = response.keys();
                while (payuHashIterator.hasNext()) {
                    String key = payuHashIterator.next();
                    switch (key) {
                        case "payment_hash":
                            merchantHash = response.getString(key);
                            break;
                        default:
                            break;
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return merchantHash;
        }

        @Override
        protected void onPostExecute(String merchantHash) {
            super.onPostExecute(merchantHash);

            progressDialog.dismiss();

            if (merchantHash.isEmpty() || merchantHash.equals("")) {
                Toast.makeText(getActivity(), "Could not generate hash", Toast.LENGTH_SHORT).show();
            } else {
                mPaymentParams.setMerchantHash(merchantHash);
                PayUmoneyFlowManager.startPayUMoneyFlow(
                        mPaymentParams,
                        getActivity(),
                        R.style.AppTheme_Blue,
                        false);


            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

        }



