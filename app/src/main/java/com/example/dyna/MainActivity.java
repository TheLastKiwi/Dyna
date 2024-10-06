package com.example.dyna;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.dyna.Utils.FileManager;

public class MainActivity extends AppCompatActivity {
    // Will use later if I get access to a Tindeq to show that data.
    String deviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        NavController navController = navHostFragment.getNavController();

        findViewById(R.id.btnTempBack).setOnClickListener(v ->{
            navController.popBackStack();
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
    }
}

