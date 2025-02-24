package com.example.app006.admin;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.app006.R;
import com.example.app006.admin.Fragments.HomeFragment;
import com.example.app006.admin.Fragments.EmployeeManagementFragment;
import com.example.app006.admin.Fragments.DashboardFragment;
import com.example.app006.admin.Fragments.ReportsFragment;
import com.example.app006.admin.Fragments.LeaveManagementFragment;
import com.example.app006.admin.Fragments.AppraisalsFragment;
import com.example.app006.admin.Fragments.LiveTrackingFragment;
import com.example.app006.auth.LoginActivity;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;

public class AdminActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Update greeting message
        View headerView = navigationView.getHeaderView(0);
        TextView greetingMessage = headerView.findViewById(R.id.greeting_message);
        greetingMessage.setText(getGreetingMessage());

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            navigationView.setCheckedItem(R.id.admin_nav_home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        int itemId = item.getItemId();

        if (itemId == R.id.admin_nav_home) {
            selectedFragment = new HomeFragment();
        } else if (itemId == R.id.admin_nav_employee_management) {
            selectedFragment = new EmployeeManagementFragment();
        } else if (itemId == R.id.admin_nav_dashboard) {
            selectedFragment = new DashboardFragment();
        } else if (itemId == R.id.admin_nav_reports) {
            selectedFragment = new ReportsFragment();
        } else if (itemId == R.id.admin_nav_leave) {
            selectedFragment = new LeaveManagementFragment();
        } else if (itemId == R.id.admin_nav_appraisals) {
            selectedFragment = new AppraisalsFragment();
        } else if (itemId == R.id.admin_nav_live_tracking) {
            selectedFragment = new LiveTrackingFragment();
        } else if (itemId == R.id.admin_nav_logout) {
            logoutUser();
            return true;
        }

        if (selectedFragment != null) {
            loadFragment(selectedFragment);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    private void logoutUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finishAffinity();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    private String getGreetingMessage() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        if (hour >= 5 && hour < 12) {
            return "Good Morning!";
        } else if (hour >= 12 && hour < 17) {
            return "Good Afternoon!";
        } else if (hour >= 17 && hour < 21) {
            return "Good Evening!";
        } else {
            return "Good Night!";
        }
    }
}
