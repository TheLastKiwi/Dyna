package com.example.dyna;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class HomeScreen extends Fragment {
    FileManager fm;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);

        view = inflater.inflate(R.layout.home_screen,container, false);
        fm = new FileManager(requireContext());
//        getSharedPreferences("Settings",Context.MODE_PRIVATE).edit().clear().commit();
        String activeUser = requireContext().getSharedPreferences("Settings", Context.MODE_PRIVATE).getString("ActiveUser", null);
        //If no profile present, create a default profile

        if(activeUser == null) {

            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Settings", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("ActiveUser", "Default");
            editor.apply();
            activeUser = "Default";
            Profile defaultProfile = new Profile("Default");
            fm.saveProfile(defaultProfile);
        }
        Profile activeProfile = fm.getProfile(activeUser);

        ((TextView)view.findViewById(R.id.txtCurrentProfile)).setText(activeProfile.displayName);

        NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);

        view.findViewById(R.id.btnLiveData).setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("session", new Session(SessionType.LIVE_DATA));
            navController.navigate(R.id.action_homeScreen_to_liveDataView, bundle);

//            Intent intent = new Intent(this, LiveDataView.class);
//            startActivity(intent);
        });

        view.findViewById(R.id.btnRepeater).setOnClickListener(view -> {
            navController.navigate(R.id.action_homeScreen_to_repeatersSettings);
//            Intent intent = new Intent(this, RepeatersSettings.class);
//            startActivity(intent);
        });

        view.findViewById(R.id.btnPeakLoad).setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("session", new Session(SessionType.PEAK_LOAD));
            navController.navigate(R.id.action_homeScreen_to_peakLoadLiveData, bundle);
//            Intent intent = new Intent(this, PeakLoadLiveData.class);
//            startActivity(intent);
        });
        view.findViewById(R.id.btnHistorical).setOnClickListener(view -> {
            navController.navigate(R.id.action_homeScreen_to_historicalSelection);
//            Intent intent = new Intent(this, HistoricalSelection.class);
//            startActivity(intent);
        });
        view.findViewById(R.id.btnProfile).setOnClickListener(view -> {
            navController.navigate(R.id.action_homeScreen_to_swapProfile);
//            Intent intent = new Intent(this, SwapProfile.class);
//            startActivity(intent);
        });
        //Load settings from somewhere to set Profile based on last activity
        return view;
    }
}
