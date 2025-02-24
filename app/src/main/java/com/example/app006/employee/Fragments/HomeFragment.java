package com.example.app006.employee.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.app006.R;
import com.example.app006.auth.LoginActivity;

public class HomeFragment extends Fragment {

    private TextView employeeName, employeeEmail, employeeRole, employeeUsername;
    private Button logoutButton;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment_home, container, false);

        // Initialize UI components
        employeeName = view.findViewById(R.id.employee_name);
        employeeEmail = view.findViewById(R.id.employee_email);
        employeeRole = view.findViewById(R.id.employee_role);
        employeeUsername = view.findViewById(R.id.employee_username);
        logoutButton = view.findViewById(R.id.logout_button);

        // Load user details from SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("name", "John Doe");
        String email = sharedPreferences.getString("email", "johndoe@example.com");
        String role = sharedPreferences.getString("role", "Software Engineer");
        String username = sharedPreferences.getString("username", "johndoe");

        // Set user details
        employeeName.setText("Name:     " + name);
        employeeEmail.setText("Email:            " + email);
        employeeRole.setText("Role:              " + role);
        employeeUsername.setText("Username:    " + username);

        // Logout button functionality
        logoutButton.setOnClickListener(v -> logoutUser());

        return view;
    }

    private void logoutUser() {
        // Clear user session
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Redirect to LoginActivity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        Toast.makeText(getActivity(), "Logged out successfully", Toast.LENGTH_SHORT).show();
        requireActivity().finish();
    }
}
