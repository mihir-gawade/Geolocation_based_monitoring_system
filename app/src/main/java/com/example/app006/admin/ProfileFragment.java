package com.example.app006.admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.app006.R;
import com.example.app006.auth.LoginActivity;

public class ProfileFragment extends Fragment {

    private Button logoutButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        logoutButton = view.findViewById(R.id.btn_logout);
        logoutButton.setOnClickListener(v -> logoutUser());

        return view;
    }

    private void logoutUser() {
        Log.d("LogoutDebug", "Logout button clicked in ProfileFragment");

        if (getActivity() != null) {
            requireActivity().runOnUiThread(() -> {
                // Clear shared preferences
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", requireActivity().MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                // Navigate to LoginActivity
                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                // Ensure all activities are finished
                requireActivity().finishAffinity();
            });
        } else {
            Log.e("LogoutDebug", "getActivity() is null in ProfileFragment");
        }
    }
}
