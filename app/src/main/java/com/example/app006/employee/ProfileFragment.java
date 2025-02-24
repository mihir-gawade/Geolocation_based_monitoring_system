package com.example.app006.employee;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.app006.auth.LoginActivity;
import com.example.app006.R;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {
    private Button logoutButton;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.employee_profile, container, false);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize logout button
        logoutButton = view.findViewById(R.id.logout_button);

        // Set logout button click listener
        logoutButton.setOnClickListener(v -> logoutUser());

        return view;
    }

    private void logoutUser() {
        if (getActivity() == null) return; // Prevent null pointer exception

        // Sign out from Firebase
        auth.signOut();

        // Clear SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LoginPrefs", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Redirect to LoginActivity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
