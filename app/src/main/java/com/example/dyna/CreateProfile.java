package com.example.dyna;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CreateProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        findViewById(R.id.btnSaveProfile).setOnClickListener(v -> {
            FileManager fm = new FileManager(this);
            String userName = ((EditText) findViewById(R.id.editTextName)).getText().toString();
            fm.saveProfile(new Profile(userName));
            Intent intent = new Intent(this, SwapProfile.class);
            startActivity(intent);
        });
    }
}