package com.example.dyna;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class CreateProfile extends Fragment {
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);

        view = inflater.inflate(R.layout.activity_create_profile,container, false);
        view.findViewById(R.id.btnSaveProfile).setOnClickListener(v -> {
            FileManager fm = new FileManager(requireContext());
            String userName = ((EditText) view.findViewById(R.id.editTextName)).getText().toString();
            fm.saveProfile(new Profile(userName));

            NavController navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
            navController.popBackStack();

//            Intent intent = new Intent(this, SwapProfile.class);
//            startActivity(intent);
        });
        return view;
    }
}