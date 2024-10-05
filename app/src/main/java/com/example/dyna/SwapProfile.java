package com.example.dyna;

import android.content.Context;
import android.content.SharedPreferences;
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

import java.util.ArrayList;

public class SwapProfile extends Fragment {
    //TODO This whole activity will probably eventually become a dropdown on the main page
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);

        view = inflater.inflate(R.layout.activity_swap_profile,container, false);
        addButtons();
        NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
        view.findViewById(R.id.btnCreateProfile).setOnClickListener(v ->{
            navController.navigate(R.id.action_swapProfile_to_createProfile);
        });
        return view;
    }
    public void addButtons(){
        FileManager fm = new FileManager(requireContext());
        LinearLayout llProfiles = view.findViewById(R.id.llProfiles);
        llProfiles.removeAllViews();
        ArrayList<Profile> profiles = fm.getAllProfiles();
        for(Profile p : profiles){
            llProfiles.addView(getButton(p));
        }
    }
    private @NonNull Button getButton(Profile profile) {
        Button button = new Button(requireContext());
        button.setText(profile.displayName);
        NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
        //TODO: Customize button style here
        button.setOnClickListener(v -> {
            //Set active user
            changeUser(profile.name);
            navController.popBackStack();
        });
        return button;
    }
    public void changeUser(String userName) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ActiveUser", userName);
        editor.apply();
    }
}