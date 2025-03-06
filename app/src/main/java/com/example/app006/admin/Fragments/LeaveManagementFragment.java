package com.example.app006.admin.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.app006.R;
import com.example.app006.admin.Adapters.LeaveRequestAdapter;
import com.example.app006.models.LeaveRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LeaveManagementFragment extends Fragment {
    private ListView lvLeaveRequests;
    private Button btnApprove, btnReject;
    private LeaveRequestAdapter adapter;
    private List<LeaveRequest> leaveRequests;
    private DatabaseReference dbRef;
    private int selectedPosition = -1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment_leave, container, false);

        lvLeaveRequests = view.findViewById(R.id.lv_leave_requests);
        btnApprove = view.findViewById(R.id.btn_approve_leave);
        btnReject = view.findViewById(R.id.btn_reject_leave);

        leaveRequests = new ArrayList<>();
        adapter = new LeaveRequestAdapter(requireContext(), leaveRequests);
        lvLeaveRequests.setAdapter(adapter);

        dbRef = FirebaseDatabase.getInstance().getReference("dbleave");

        lvLeaveRequests.setOnItemClickListener((parent, view1, position, id) -> selectedPosition = position);

        btnApprove.setOnClickListener(v -> updateLeaveStatus("Approved"));
        btnReject.setOnClickListener(v -> updateLeaveStatus("Rejected"));

        fetchLeaveRequests();
        return view;
    }

    private void fetchLeaveRequests() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                leaveRequests.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    LeaveRequest request = data.getValue(LeaveRequest.class);
                    if (request != null && request.getStatus() != null && request.getStatus().equals("Pending")) {
                        request.setRequestId(data.getKey()); // Set request ID from Firebase key
                        leaveRequests.add(request);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateLeaveStatus(String status) {
        if (selectedPosition < 0 || selectedPosition >= leaveRequests.size()) {
            Toast.makeText(getContext(), "Please select a valid request", Toast.LENGTH_SHORT).show();
            return;
        }

        LeaveRequest selectedRequest = leaveRequests.get(selectedPosition);
        String requestId = selectedRequest.getRequestId(); // Get the request ID from Firebase

        if (requestId == null) {
            Toast.makeText(getContext(), "Error: Request ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        dbRef.child(requestId).child("status").setValue(status)
                .addOnSuccessListener(aVoid -> {
                    selectedRequest.setStatus(status);
                    adapter.notifyDataSetChanged();
                    selectedPosition = -1; // Reset selection
                    Toast.makeText(getContext(), "Leave " + status, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
