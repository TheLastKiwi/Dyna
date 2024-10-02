package com.example.dyna;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    String deviceName;
    //Create an ActivityResultLauncher for Bluetooth enable request
    Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        profile = new Profile("Matt");

        // Button to start scanning
        findViewById(R.id.btnLiveData).setOnClickListener(view -> {
            Intent intent = new Intent(this, LiveDataView.class);
            intent.putExtra("profile",profile);
            startActivity(intent);
        });

        findViewById(R.id.btnRepeater).setOnClickListener(view -> {
            Intent intent = new Intent(this, RepeatersSettings.class);
            intent.putExtra("profile",profile);
            startActivity(intent);
        });

        findViewById(R.id.btnPeakLoad).setOnClickListener(view -> {
            Intent intent = new Intent(this, PeakLoadLiveData.class);
            intent.putExtra("profile",profile);
            startActivity(intent);
        });
        findViewById(R.id.btnHistorical).setOnClickListener(view -> {
            Intent intent = new Intent(this, HistorcialSelection.class);
            intent.putExtra("profile",profile);
            startActivity(intent);
        });

        //Load settings from somewhere to set Profile based on last activity

    }

}

