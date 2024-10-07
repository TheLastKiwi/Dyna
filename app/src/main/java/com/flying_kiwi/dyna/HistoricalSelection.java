package com.flying_kiwi.dyna;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.flying_kiwi.dyna.Utils.FileManager;

import java.util.ArrayList;

public class HistoricalSelection extends Fragment {
    FileManager fm;

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);

        view = inflater.inflate(R.layout.historical_selection_fragment,container, false);

        fm = new FileManager(requireContext());
        initializeButtons();
        return view;
    }

    public void initializeButtons() {
        //Dynamically add all buttons to the layout??? Iterate over SessionType? Sounds good to me
        view.findViewById(R.id.btnHistoricalPeak).setOnClickListener(v -> {
            addButtonsToLayout(SessionType.PEAK_LOAD);
        });
        view.findViewById(R.id.btnHistoricalRepeater).setOnClickListener(v -> {
            addButtonsToLayout(SessionType.REPEATER);
        });
        view.findViewById(R.id.btnHistoricalCritical).setOnClickListener(v -> {
            addButtonsToLayout(SessionType.CRITICAL_FORCE);
        });
    }

    public void addButtonsToLayout(SessionType sessionType) {
        LinearLayout buttonContainer = view.findViewById(R.id.llSessions);
        buttonContainer.removeAllViews();
        ArrayList<Session> sessions = fm.getAllSessions(sessionType);
        // Iterate over the ArrayList and create a button for each item
        for (Session session : sessions) {
            Button button = getButton(session);

            //TODO: Customize button style here
            buttonContainer.addView(button);

        }

    }

    private @NonNull Button getButton(Session session) {
        Button button = new Button(requireContext());
        button.setText(session.getName());

        button.setOnClickListener(v -> {

            NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
            Bundle bundle = new Bundle();
            bundle.putSerializable("session", session);
            bundle.putBoolean("historical",true);
            switch (session.getSessionType()){
                case PEAK_LOAD:
                    navController.navigate(R.id.action_historicalSelection_to_peakLoadLiveData, bundle);
                    break;
                case REPEATER:
                    navController.navigate(R.id.action_historicalSelection_to_repeaterLiveData, bundle);
                    break;
                case CRITICAL_FORCE:
                    navController.navigate(R.id.action_historicalSelection_to_criticalForceLiveData, bundle);
                    break;
                default:
                    // Uhoh
                    navController.navigate(R.id.historicalSelection, bundle);
            }
        });
        return button;
    }
}