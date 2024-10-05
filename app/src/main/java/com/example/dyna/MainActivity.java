package com.example.dyna;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

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
    }
}

