package com.example.dyna;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    String deviceName;
    //Create an ActivityResultLauncher for Bluetooth enable request

    FileManager fm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fm = new FileManager(this);
//        getSharedPreferences("Settings",Context.MODE_PRIVATE).edit().clear().commit();
        String activeUser = getSharedPreferences("Settings", Context.MODE_PRIVATE).getString("ActiveUser", null);
        //If no profile present, create a default profile

        if(activeUser == null) {

            SharedPreferences sharedPreferences = this.getSharedPreferences("Settings", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("ActiveUser", "Default");
            editor.apply();
            activeUser = "Default";
            Profile defaultProfile = new Profile("Default");
            fm.saveProfile(defaultProfile);
        } 
        Profile activeProfile = fm.getProfile(activeUser);

        ((TextView)findViewById(R.id.txtCurrentProfile)).setText(activeProfile.displayName);
        // Button to start scanning
        findViewById(R.id.btnLiveData).setOnClickListener(view -> {
            Intent intent = new Intent(this, LiveDataView.class);
            startActivity(intent);
        });

        findViewById(R.id.btnRepeater).setOnClickListener(view -> {
            Intent intent = new Intent(this, RepeatersSettings.class);
            startActivity(intent);
        });

        findViewById(R.id.btnPeakLoad).setOnClickListener(view -> {
            Intent intent = new Intent(this, PeakLoadLiveData.class);
            startActivity(intent);
        });
        findViewById(R.id.btnHistorical).setOnClickListener(view -> {
            Intent intent = new Intent(this, HistoricalSelection.class);
            startActivity(intent);
        });
        findViewById(R.id.btnProfile).setOnClickListener(view -> {
            Intent intent = new Intent(this, SwapProfile.class);
            startActivity(intent);
        });
        //Load settings from somewhere to set Profile based on last activity

    }

}

