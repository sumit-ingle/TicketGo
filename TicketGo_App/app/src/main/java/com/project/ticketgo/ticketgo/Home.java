package com.project.ticketgo.ticketgo;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


/**
 * Project Name: 	    IoT based prototype for Automated E-ticketing in suburban railways.
 * Author List: 		Pranav Gawane
 * Filename: 		    Home.java
 * Functions: 	        None
 * Global Variables:	None
 */

public class Home extends Fragment {
    String[] passenger_count = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", }; //array of values for spinner.
    // to get no of passengers from the user.

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
       // return inflater.inflate(R.layout.home, container , false);
        final View view = inflater.inflate(R.layout.home, container , false);

        final Spinner spinner =  view.findViewById(R.id.spinner);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,passenger_count );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent checkInIntent = new Intent(Home.this.getActivity(), Checkin.class);
                checkInIntent.putExtra("noOfpassengers",spinner.getSelectedItemPosition()); //pass nop to checkin activity.

                startActivity(checkInIntent); //start Checkin activity.
            }
        });

        return view;

         }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Home");

    }
}
