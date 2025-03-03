package com.example.app006.admin.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app006.R;
import com.example.app006.adapters.LeaveRequestAdapter;
import com.example.app006.models.AdminEmp;
import com.example.app006.models.LeaveRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class LeaveManagementFragment extends Fragment {

    private RecyclerView recyclerView;
    private LeaveRequestAdapter adapter;
    private List<LeaveRequest> leaveRequestList;
    private FirebaseFirestore firestore;
    private String currentAdminEmail;

    public LeaveManagementFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment_leave, container, false);

        recyclerView = view.findViewById(R.id.rv_leave_requests);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firestore = FirebaseFirestore.getInstance();
        leaveRequestList = new ArrayList<>();
        currentAdminEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail(); // Get current admin email

        adapter = new LeaveRequestAdapter(getContext(), leaveRequestList, new LeaveRequestAdapter.OnLeaveRequestActionListener() {
            @Override
            public void onApproveClick(LeaveRequest leaveRequest, int position) {
                updateLeaveStatus(leaveRequest, "Approved", position);
            }

            @Override
            public void onRejectClick(LeaveRequest leaveRequest, int position) {
                updateLeaveStatus(leaveRequest, "Rejected", position);
            }
        });

        recyclerView.setAdapter(adapter);

        fetchEmployeeLeaveRequests(); // Fetch leave requests for employees managed by current admin

        return view;
    }

    // Fetch employees under the current admin's email
    private void fetchEmployeeLeaveRequests() {
        CollectionReference adminEmpRef = firestore.collection("hr-employees");

        adminEmpRef
                .whereEqualTo("adminEmail", currentAdminEmail) // Get employees associated with the admin
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("FirestoreError", "Error fetching employee data: ", error);
                            Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (value == null || value.isEmpty()) {
                            Toast.makeText(getContext(), "No employees found for this admin", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        List<String> employeeEmails = new ArrayList<>();
                        for (DocumentSnapshot document : value.getDocuments()) {
                            AdminEmp adminEmp = document.toObject(AdminEmp.class);
                            if (adminEmp != null) {
                                employeeEmails.add(adminEmp.getEmpEmail()); // Get the employee email
                            }
                        }

                        // Now fetch leave requests for these employees
                        fetchLeaveRequestsForEmployees(employeeEmails);
                    }
                });
    }

    // Fetch leave requests based on employee emails
    private void fetchLeaveRequestsForEmployees(List<String> employeeEmails) {
        CollectionReference leaveRequestsRef = firestore.collection("leave-requests");

        leaveRequestsRef
                .whereIn("employeeEmail", employeeEmails) // Fetch leave requests for specific employees
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("FirestoreError", "Error fetching leave requests: ", error);
                            Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (value == null || value.isEmpty()) {
                            Toast.makeText(getContext(), "No leave requests found for employees", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        leaveRequestList.clear();
                        for (DocumentSnapshot document : value.getDocuments()) {
                            LeaveRequest leaveRequest = document.toObject(LeaveRequest.class);
                            if (leaveRequest != null) {
                                leaveRequest.setRequestId(document.getId()); // Store document ID for updates
                                leaveRequestList.add(leaveRequest);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void updateLeaveStatus(LeaveRequest leaveRequest, String newStatus, int position) {
        String docId = leaveRequest.getRequestId();

        firestore.collection("leave-requests").document(docId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Leave " + newStatus, Toast.LENGTH_SHORT).show();
                    leaveRequestList.remove(position);
                    adapter.notifyItemRemoved(position);
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Failed to update status", e);
                    Toast.makeText(getContext(), "Failed to update status", Toast.LENGTH_SHORT).show();
                });
    }
}
