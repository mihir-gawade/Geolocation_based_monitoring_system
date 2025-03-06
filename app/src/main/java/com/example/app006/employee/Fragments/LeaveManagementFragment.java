package com.example.app006.employee.Fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
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
import com.example.app006.models.LeaveRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Calendar;

public class LeaveManagementFragment extends Fragment {

    private EditText leaveReason;
    private Button startDateButton, endDateButton, applyLeaveButton;
    private TextView leaveStatus;
    private String startDate, endDate;

    private FirebaseAuth auth;
    private DatabaseReference leaveDatabase;

    public LeaveManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.employee_fragment_leave, container, false);

        // Initialize UI components
        leaveReason = view.findViewById(R.id.leave_reason);
        startDateButton = view.findViewById(R.id.start_date_button);
        endDateButton = view.findViewById(R.id.end_date_button);
        applyLeaveButton = view.findViewById(R.id.apply_leave_button);
        leaveStatus = view.findViewById(R.id.leave_status);

        auth = FirebaseAuth.getInstance();
        leaveDatabase = FirebaseDatabase.getInstance().getReference("dbleave");

        // Set date pickers for start and end date buttons
        startDateButton.setOnClickListener(v -> openDatePicker(true));
        endDateButton.setOnClickListener(v -> openDatePicker(false));

        // Apply leave button action
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
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String reason = leaveReason.getText().toString().trim();
        if (reason.isEmpty() || startDate == null || endDate == null) {
            Toast.makeText(getContext(), "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        String requestId = leaveDatabase.push().getKey();

        LeaveRequest leaveRequest = new LeaveRequest(
                requestId, currentUser.getDisplayName(), userId, "General Leave",
                startDate, endDate, reason, "Pending"
        );

        if (requestId != null) {
            leaveDatabase.child(requestId).setValue(leaveRequest)
                    .addOnSuccessListener(aVoid -> {
                        leaveStatus.setText("Leave Status: Pending");
                        Toast.makeText(getContext(), "Leave Applied Successfully!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error submitting leave!", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
