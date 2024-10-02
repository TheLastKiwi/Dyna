package com.example.dyna;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    String deviceName;
    //Create an ActivityResultLauncher for Bluetooth enable request


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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


    }

}

