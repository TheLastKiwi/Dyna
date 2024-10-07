package com.flying_kiwi.dyna;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.flying_kiwi.dyna.Utils.FileManager;

public class HomeScreen extends Fragment {
    FileManager fm;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);

        view = inflater.inflate(R.layout.home_screen_fragment,container, false);
        fm = new FileManager(requireContext());

        NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);

        view.findViewById(R.id.btnLiveData).setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("session", new Session(SessionType.LIVE_DATA));
            navController.navigate(R.id.action_homeScreen_to_liveDataView, bundle);
        });

        view.findViewById(R.id.btnRepeater).setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("session", new Session(SessionType.REPEATER));
            navController.navigate(R.id.action_homeScreen_to_repeatersSettings);
        });

        view.findViewById(R.id.btnPeakLoad).setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("session", new Session(SessionType.PEAK_LOAD));
            navController.navigate(R.id.action_homeScreen_to_peakLoadLiveData, bundle);
        });
        view.findViewById(R.id.btnHistorical).setOnClickListener(view -> {
            navController.navigate(R.id.action_homeScreen_to_historicalSelection);
        });
        view.findViewById(R.id.btnProfile).setOnClickListener(view -> {
            navController.navigate(R.id.action_homeScreen_to_swapProfile);
        });
        view.findViewById(R.id.btnCriticalForce).setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            Session session = new Session(SessionType.CRITICAL_FORCE);
            session.setNumReps(24);
            session.setNumSets(1);
            session.setRestTime(3);
            session.setWorkTime(7);
            session.setCountdown(3);
            bundle.putSerializable("session", session);
            navController.navigate(R.id.action_homeScreen_to_criticalForceLiveData, bundle);
        });
        return view;
    }
}
