package com.example.app006.employee;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.app006.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EmployeeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        // Get the welcome text view
        TextView welcomeText = findViewById(R.id.welcome_text);

        // Get the user's name from Intent or SharedPreferences
        String userName = getIntent().getStringExtra("USER_NAME");
        if (userName == null || userName.isEmpty()) {
            userName = "Employee";  // Default if name is not provided
        }

        // Update the TextView with the user's name
        welcomeText.setText("Welcome, " + userName);

        BottomNavigationView bottomNavigationView = findViewById(R.id.employee_bottom_navigation);

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(new AttendanceFragment());
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_attendance) {
                selectedFragment = new AttendanceFragment();
            } else if (item.getItemId() == R.id.nav_reporting) {
                selectedFragment = new ReportsFragment();
            } else if (item.getItemId() == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
