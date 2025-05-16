package com.example.app006.employee.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.app006.R;

public class DashboardFragment extends Fragment {

    private TextView employeeName, employeeRole;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout
        View view = inflater.inflate(R.layout.employee_fragment_dashboard, container, false);

        // Initialize the TextViews for employee name and role
        employeeName = view.findViewById(R.id.employeeName);
        employeeRole = view.findViewById(R.id.employeeRole);

        // Set employee data (example, this can come from SharedPreferences or a database)
        String name = "John Doe";  // Replace with actual name
        String role = "Employee";  // Replace with actual role

        employeeName.setText(name);
        employeeRole.setText(role);

        return view;
    }
}
