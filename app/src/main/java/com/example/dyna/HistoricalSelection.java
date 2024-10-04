package com.example.dyna;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class HistoricalSelection extends AppCompatActivity {
    FileManager fm;
    Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historcial_selection);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        fm = new FileManager(this);
        Intent intent = getIntent();
        profile = (Profile) intent.getSerializableExtra("profile");
        initializeButtons();
    }

    public void initializeButtons() {
        findViewById(R.id.btnHistoricalPeak).setOnClickListener(view -> {
            addButtonsToLayout(SessionType.PEAK_LOAD);
        });
        findViewById(R.id.btnHistoricalRepeater).setOnClickListener(view -> {
            addButtonsToLayout(SessionType.REPEATER);
        });
    }

    public void addButtonsToLayout(SessionType sessionType) {
        LinearLayout buttonContainer = findViewById(R.id.llSessions);
        buttonContainer.removeAllViews();
        ArrayList<Session> sessions = fm.getAllSessions(this, sessionType);
        // Iterate over the ArrayList and create a button for each item
        for (Session session : sessions) {
            Button button = getButton(session);

            // Add the button to the layout
            buttonContainer.addView(button);
        }

    }

    private @NonNull Button getButton(Session session) {
        Button button = new Button(this);
        button.setText(session.name);

        button.setOnClickListener(v -> {
            Intent intent;
            switch (session.sessionType){
                case PEAK_LOAD:
                    intent = new Intent(this, PeakLoadLiveData.class);
                    break;
                case REPEATER:
                    intent = new Intent(this, RepeaterLiveData.class);
                    break;
                default:
                    intent = new Intent(this, HistoricalSelection.class);
            }
            intent.putExtra("historical",true);
            intent.putExtra("profile", profile);
            intent.putExtra("session", session);
            startActivity(intent);
        });
        return button;
    }
}