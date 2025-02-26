package com.example.app006.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import android.content.Context;
import androidx.core.content.ContextCompat;
import com.example.app006.R;
import com.example.app006.admin.AdminActivity;
import com.example.app006.employee.EmployeeActivity;
import com.example.app006.services.LocationTrackingService;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private EditText loginUsername, loginPassword;
    private Button loginButton;
    private TextView signupRedirectText;
    private SharedPreferences sharedPreferences;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginUsername = findViewById(R.id.login_username);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirectText);

        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();

        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            navigateToDashboard();
            return;
        }

        loginButton.setOnClickListener(view -> {
            if (validateInputs()) {
                checkUser();
            }
        });

        signupRedirectText.setOnClickListener(view ->
                startActivity(new Intent(LoginActivity.this, SignupActivity.class))
        );
    }

    private boolean validateInputs() {
        return validateField(loginUsername, "Please enter your username") &&
                validateField(loginPassword, "Please enter your password");
    }

    private boolean validateField(EditText field, String errorMessage) {
        String val = field.getText().toString().trim();
        if (val.isEmpty()) {
            field.setError(errorMessage);
            field.requestFocus();
            return false;
        }
        return true;
    }

    private void navigateToDashboard() {
        String role = sharedPreferences.getString("role", "");

        if ("admin".equalsIgnoreCase(role)) {
            startActivity(new Intent(LoginActivity.this, AdminActivity.class));
        } else if ("employee".equalsIgnoreCase(role)) {
            startActivity(new Intent(LoginActivity.this, EmployeeActivity.class));
            startEmployeeTracking();
        } else {
            Toast.makeText(LoginActivity.this, "Unknown role. Please contact support.", Toast.LENGTH_SHORT).show();
            return;
        }
        finish();
    }

    private void checkUser() {
        String userUsername = loginUsername.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        db.collection("users").document(userUsername).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String passwordFromDB = documentSnapshot.getString("password");
                        String userType = documentSnapshot.getString("userType");

                        if (passwordFromDB == null || userType == null) {
                            Toast.makeText(LoginActivity.this, "Invalid user data. Contact support.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (userPassword.equals(passwordFromDB)) {
                            saveUserSession(documentSnapshot, userType);
                            navigateToDashboard();
                        } else {
                            loginPassword.setError("Incorrect password");
                            loginPassword.requestFocus();
                        }
                    } else {
                        loginUsername.setError("User not found");
                        loginUsername.requestFocus();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(LoginActivity.this, "Database error. Please try again.", Toast.LENGTH_SHORT).show()
                );
    }

    private void saveUserSession(DocumentSnapshot documentSnapshot, String userType) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("name", documentSnapshot.getString("name"));
        editor.putString("email", documentSnapshot.getString("email"));
        editor.putString("username", documentSnapshot.getString("username"));
        editor.putString("role", userType);
        editor.apply();
    }

    private void startEmployeeTracking() {
        Intent intent = new Intent(this, LocationTrackingService.class);
        ContextCompat.startForegroundService(this, intent);
        ContextCompat.startForegroundService(this, intent);
    }
}
