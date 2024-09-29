package com.example.dyna;

import android.bluetooth.BluetoothManager;
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

        Button startButton = findViewById(R.id.btnLiveData);
        Button repeaterButton = findViewById(R.id.btnRepeater);


        // Button to start scanning
        startButton.setOnClickListener(view -> {
            Intent intent = new Intent(this,LiveDataView.class);
            startActivity(intent);
        });

        repeaterButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, RepeatersSettings.class);
            startActivity(intent);
        });

    }

}

