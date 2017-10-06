package com.example.keiichi.androidopdracht;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class EnterMarker extends AppCompatActivity {
    Double longitude;
    Double latitude;

    SqlLitehelper helper;
    EditText DescriptionText;
    Button markerButton;
    EditText longtext;
    EditText latText;
    LatLng testposition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_marker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        helper = new SqlLitehelper(this);

        testposition = getIntent().getParcelableExtra("location");

        latText = (EditText)findViewById(R.id.LatText);
        longtext = (EditText)findViewById(R.id.longText);
        DescriptionText = (EditText)findViewById(R.id.DescriptionText);
        markerButton = (Button) findViewById(R.id.AddMarkerButton);

        longitude = testposition.longitude;
        latitude = testposition.latitude;
        latText.setText(String.valueOf(testposition.latitude));
        longtext.setText(String.valueOf(testposition.longitude));



        markerButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if ( DescriptionText.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),
                            "Schrijf een beschrijving!", Toast.LENGTH_LONG).show();
                }else {
                    try{
                        helper.addLocation(latitude,longitude,DescriptionText.getText().toString());
                        Toast.makeText(getApplicationContext(),
                                "Marker added!", Toast.LENGTH_LONG).show();
                        finish();

                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),
                                e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }


            }
        });




    }

}
