package com.example.app006.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.app006.R;
import com.example.app006.admin.AdminActivity;
import com.example.app006.employee.EmployeeActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText loginUsername, loginPassword;
    private Button loginButton;
    private TextView signupRedirectText;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginUsername = findViewById(R.id.login_username);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirectText);

        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);

        if (isUserLoggedIn()) {
            navigateToDashboard();
            return;
        }

        loginButton.setOnClickListener(view -> {
            if (validateInputs()) {
                checkUser();
            }
        });

        signupRedirectText.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, SignupActivity.class)));
    }

    private boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    private boolean validateInputs() {
        return validateField(loginUsername, "Username cannot be empty") &&
                validateField(loginPassword, "Password cannot be empty");
    }

    private boolean validateField(EditText field, String errorMessage) {
        String val = field.getText().toString().trim();
        if (val.isEmpty()) {
            field.setError(errorMessage);
            field.requestFocus();
            return false;
        }
        field.setError(null);
        return true;
    }

    private void checkUser() {
        String userUsername = loginUsername.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String passwordFromDB = userSnapshot.child("password").getValue(String.class);

                        if (userPassword.equals(passwordFromDB)) {
                            saveUserSession(userSnapshot);
                            navigateToDashboard();
                        } else {
                            loginPassword.setError("Invalid Credentials");
                            loginPassword.requestFocus();
                        }
                    }
                } else {
                    loginUsername.setError("User does not exist");
                    loginUsername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Database error. Try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserSession(DataSnapshot userSnapshot) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("name", userSnapshot.child("name").getValue(String.class));
        editor.putString("email", userSnapshot.child("email").getValue(String.class));
        editor.putString("username", userSnapshot.child("username").getValue(String.class));
        editor.putString("role", userSnapshot.child("userType").getValue(String.class));
        editor.apply();
    }

    private void navigateToDashboard() {
        String role = sharedPreferences.getString("role", "");
        Intent intent;
        if ("Admin".equals(role)) {
            intent = new Intent(LoginActivity.this, AdminActivity.class);
        } else {
            intent = new Intent(LoginActivity.this, EmployeeActivity.class);
        }
        intent.putExtra("name", sharedPreferences.getString("name", ""));
        intent.putExtra("email", sharedPreferences.getString("email", ""));
        intent.putExtra("username", sharedPreferences.getString("username", ""));
        intent.putExtra("role", role);
        startActivity(intent);
        finish();
    }
}
