package com.example.app006.admin;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.app006.R;
import com.example.app006.auth.LoginActivity;
import com.example.app006.admin.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Log.d("AdminActivity", "AdminActivity started");

        // Set welcome text
        TextView welcomeText = findViewById(R.id.welcome_text);
        if (welcomeText != null) {
            welcomeText.setText("Welcome, Admin");
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.admin_bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);

        // Set default fragment on start
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.admin_fragment_container, new EmployeeLookupFragment())
                    .commit();
        }

        // Handle back press for logout
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.admin_fragment_container);
                if (currentFragment instanceof ProfileFragment) {
                    Log.d("AdminActivity", "Back button pressed on ProfileFragment. Logging out...");
                    logoutUser();
                } else {
                    finish(); // Close activity if not on profile
                }
            }
        });
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        if (item.getItemId() == R.id.nav_employee_lookup) {
            selectedFragment = new EmployeeLookupFragment();
        } else if (item.getItemId() == R.id.nav_reporting) {
            selectedFragment = new ReportingFragment();
        } else if (item.getItemId() == R.id.nav_profile) {
            selectedFragment = new ProfileFragment();
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.admin_fragment_container, selectedFragment)
                    .commit();
        }
        return true;
    }

    private void logoutUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finishAffinity(); // Ensures all activities are closed
    }
}
