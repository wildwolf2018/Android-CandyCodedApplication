package com.pluralsight.candycoded;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InfoActivity extends AppCompatActivity {
    public static final String LOCATION_URL_PREFIX = "https://www.google.com/maps/search/?api=1&query=";
    public static final String CHOOSE_APP = "Choose App";
    private TextView geoLocationView;
    private TextView phoneNumberView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Uri uri = Uri.parse("android.resource://com.codeschool.candycoded/" + R.drawable.store_front);
        ImageView candyStoreImageView = (ImageView)findViewById(R.id.image_view_candy_store);
        Picasso.with(this).
                load(uri).
                placeholder(R.drawable.store_front).
                into(candyStoreImageView);

        geoLocationView = (TextView)this.findViewById(R.id.text_view_address);
        phoneNumberView = (TextView)this.findViewById(R.id.text_view_phone);

        geoLocationView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                displayLocation();
            }
        });
        phoneNumberView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                makePhoneCall();
            }
        });

    }

    // ***
    // TODO - Task 2 - Launch the Google Maps Activity
    // ***
    public void createMapIntent(View view){
       Uri uri = Uri.parse("geo:0,0?q=618 E South St Orlando, FL 32801");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if(mapIntent.resolveActivity(getPackageManager()) != null){
            startActivity(mapIntent);
        }
    }

    //Display Candy Store location on a map
    private  void displayLocation(){
        String location = geoLocationView.getText().toString();

        //Regular expression to match GPS coordinates or specific address
        String pattern = "(^(\\+|-)?\\d+\\s?,?\\s(\\+|-)?\\d+$)|([\\d]+[\\s?,?\\sA-Za-z0-9]+)";
        boolean isValid = validExp(location, pattern);

        if(isValid){
            //Launch the Google Maps Activity
            Uri locUri = Uri.parse(LOCATION_URL_PREFIX + location );
            Intent locationIntent = new Intent(Intent.ACTION_VIEW);
            locationIntent.setData(locUri);
            launchActivity(this, locationIntent);
        } else{
            Toast.makeText(this, "Invalid address", Toast.LENGTH_SHORT).show();
        }
    }

    //Launches an activity using an implicit intent
    private void launchActivity(Context context, Intent intent){
        PackageManager packageManager = context.getPackageManager();
        //Query for the number of activities that can handle this intent
        List<ResolveInfo> activitiesList = packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL);
        if (activitiesList.size() > 1) {
            Intent chooser = Intent.createChooser(intent, CHOOSE_APP);
            context.startActivity(chooser);
        }
        else if(intent.resolveActivity(packageManager) != null){
            //Launch default app or activity if there's only single app that can handle intent
            context.startActivity(intent);
        }
    }

    //Validates an expression using a regular expression pattern
    private  boolean validExp(String expression, String pattern){
        Pattern compiledExp = Pattern.compile(pattern);
        Matcher matcher = compiledExp.matcher(expression);
        return matcher.find();
    }


    // ***
    // TODO - Task 3 - Launch the Phone Activity
    // ***

    //Launches a phone activity with specified phone number
    private void makePhoneCall(){
        String phoneNumber = phoneNumberView.getText().toString();

        //Regular expression to match GPS coordinates or specific address
        String pattern = "^[+]?[(]?[0-9]{1,4}[)]?[-{0,1}\\s?\\d]*$";
        boolean isValid = validExp(phoneNumber, pattern);

        if(isValid){
            // Launch the Phone Activity
            Uri locUri = Uri.parse("tel:" + phoneNumber);
            Intent locationIntent = new Intent(Intent.ACTION_VIEW);
            locationIntent.setData(locUri);
            launchActivity(this, locationIntent);
        } else{
            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
        }
    }
}
