package com.example.app006.employee.Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.app006.R;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class LeaveManagementFragment extends Fragment {

    private EditText leaveReason;
    private Button startDateButton, endDateButton, applyLeaveButton;
    private TextView leaveStatus;
    private String startDate, endDate;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    private String employeeEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.employee_fragment_leave, container, false);

        leaveReason = view.findViewById(R.id.leave_reason);
        startDateButton = view.findViewById(R.id.start_date_button);
        endDateButton = view.findViewById(R.id.end_date_button);
        applyLeaveButton = view.findViewById(R.id.apply_leave_button);
        leaveStatus = view.findViewById(R.id.leave_status);

        db = FirebaseFirestore.getInstance();
        sharedPreferences = requireActivity().getSharedPreferences("LeavePrefs", Context.MODE_PRIVATE);

        // Fetch email from SharedPreferences
        SharedPreferences loginPrefs = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        employeeEmail = loginPrefs.getString("email", "");

        if (employeeEmail.isEmpty()) {
            Toast.makeText(getContext(), "Error: No employee email found.", Toast.LENGTH_SHORT).show();
            return view;
        }

        loadLeaveData();

        startDateButton.setOnClickListener(v -> openDatePicker(true));
        endDateButton.setOnClickListener(v -> openDatePicker(false));
        applyLeaveButton.setOnClickListener(v -> applyLeave());

        return view;
    }

    private void openDatePicker(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    if (isStartDate) {
                        startDate = selectedDate;
                        startDateButton.setText("Start Date: " + startDate);
                    } else {
                        endDate = selectedDate;
                        endDateButton.setText("End Date: " + endDate);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void applyLeave() {
        String reason = leaveReason.getText().toString().trim();
        if (reason.isEmpty() || startDate == null || endDate == null) {
            Toast.makeText(getContext(), "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        applyLeaveButton.setEnabled(false);
        Map<String, Object> leaveRequest = new HashMap<>();
        leaveRequest.put("employeeEmail", employeeEmail);
        leaveRequest.put("reason", reason);
        leaveRequest.put("startDate", startDate);
        leaveRequest.put("endDate", endDate);
        leaveRequest.put("status", "Pending");

        db.collection("leave-requests")
                .add(leaveRequest)
                .addOnSuccessListener(documentReference -> {
                    saveLeaveData(reason);
                    leaveStatus.setText("Leave Status: Pending");
                    Toast.makeText(getContext(), "Leave Applied Successfully!", Toast.LENGTH_SHORT).show();
                    applyLeaveButton.setEnabled(true);
                })
                .addOnFailureListener(e -> {
                    Log.e("LeaveManagement", "Error submitting leave: " + e.getMessage());
                    Toast.makeText(getContext(), "Error submitting leave!", Toast.LENGTH_SHORT).show();
                    applyLeaveButton.setEnabled(true);
                });
    }

    private void saveLeaveData(String reason) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("leaveReason", reason);
        editor.putString("leaveStatus", "Pending");
        editor.putString("startDate", startDate);
        editor.putString("endDate", endDate);
        editor.apply();
    }

    private void loadLeaveData() {
        leaveReason.setText(sharedPreferences.getString("leaveReason", ""));
        startDate = sharedPreferences.getString("startDate", null);
        endDate = sharedPreferences.getString("endDate", null);
        leaveStatus.setText("Leave Status: " + sharedPreferences.getString("leaveStatus", "Not Applied"));

        if (startDate != null) {
            startDateButton.setText("Start Date: " + startDate);
        }
        if (endDate != null) {
            endDateButton.setText("End Date: " + endDate);
        }
    }
}
