package com.example.dyna;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class SwapProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_swap_profile);
//        EdgeToEdge.enable(this);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        addButtons();
        findViewById(R.id.btnCreateProfile).setOnClickListener(v ->{
            Intent intent = new Intent(this, CreateProfile.class);
            startActivity(intent);

        });
    }
    public void addButtons(){
        FileManager fm = new FileManager(this);
        LinearLayout llProfiles = findViewById(R.id.llProfiles);
        llProfiles.removeAllViews();
        ArrayList<Profile> profiles = fm.getAllProfiles();
        for(Profile p : profiles){
            llProfiles.addView(getButton(p));
        }
    }
    private @NonNull Button getButton(Profile profile) {
        Button button = new Button(this);
        button.setText(profile.displayName);

        button.setOnClickListener(v -> {
            //Set active user
            changeUser(profile.name);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
        return button;
    }
    public void changeUser(String userName) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ActiveUser", userName);
        editor.apply();
    }
}