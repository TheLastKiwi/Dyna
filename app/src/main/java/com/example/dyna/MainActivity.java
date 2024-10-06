package com.example.dyna;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.Manifest;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.dyna.Utils.FileManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    // Will use later if I get access to a Tindeq to show that data.
    String deviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        NavController navController = navHostFragment.getNavController();

        findViewById(R.id.btnTempBack).setOnClickListener(v ->{
            if(navController.getCurrentDestination().getId() != R.id.homeScreen){
                navController.popBackStack();
            }

        });
        findViewById(R.id.btnTmpProfile).setOnClickListener(v -> {

        });

        SharedPreferences settings = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String activeUser = settings.getString("ActiveUser", null);
        //If no profile present, create a default profile and set the active user to it
        FileManager fm = new FileManager(this);
        if(activeUser == null) {
            settings.edit().putString("ActiveUser", "Default").apply();
            activeUser = "Default";
            Profile defaultProfile = new Profile("Default");
            fm.saveProfile(defaultProfile);
        }
        Profile activeProfile = fm.getProfile(activeUser);

        ((TextView)findViewById(R.id.btnTmpProfile)).setText(activeProfile.getDisplayName());

        checkAndRequestPermissions();
    }

    private void checkAndRequestPermissions() {
        // Array of required permissions
        String[] basePermissions = {
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        ArrayList<String> arrayPermissions = new ArrayList<>(Arrays.asList(basePermissions));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayPermissions.add(Manifest.permission.BLUETOOTH_SCAN);
        }

        // Check which permissions are not granted
        boolean allPermissionsGranted = true;
        for (String permission : arrayPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }

        if (!allPermissionsGranted) {
            // Request the permissions
            ActivityCompat.requestPermissions(this, arrayPermissions.toArray(new String[0]), REQUEST_BLUETOOTH_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (!allPermissionsGranted) {
                // Optionally inform the user why permissions are necessary
                showPermissionExplanation();
            }
        }
    }

    private void showPermissionExplanation() {
        // Show an explanation dialog to the user
        new AlertDialog.Builder(this)
                .setTitle("Permissions Required")
                .setMessage("This app requires Bluetooth permissions to function properly.")
                .setPositiveButton("Try Again", (dialog, which) -> checkAndRequestPermissions())
                .setNegativeButton("Cancel", (dialog, which) -> {
                    System.exit(0);})
                .show();
    }

}

