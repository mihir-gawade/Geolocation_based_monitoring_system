package com.example.app006.admin.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app006.R;
import com.example.app006.adapters.EmployeeAdapter;
import com.example.app006.models.AdminEmp;
import com.example.app006.models.GeofenceMapping;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;
import java.util.List;

public class EmployeeManagementFragment extends Fragment {

    private EditText etEmpEmail, etSalary, etDepartment;
    private Spinner spGeofenceID;
    private Button btnAddEmployee, btnDeleteEmployee;
    private RecyclerView rvEmployeeList;
    private FirebaseFirestore db;
    private CollectionReference employeesCollection, geofenceCollection;
    private String adminEmail;
    private EmployeeAdapter employeeAdapter;
    private List<AdminEmp> employeeList;
    private List<GeofenceMapping> geofenceList;
    private ArrayAdapter<String> geofenceAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_employeemgmt_fragment, container, false);

        etEmpEmail = view.findViewById(R.id.et_emp_email);
        etSalary = view.findViewById(R.id.et_salary);
        etDepartment = view.findViewById(R.id.et_department);
        spGeofenceID = view.findViewById(R.id.spinner_geofence_id);
        btnAddEmployee = view.findViewById(R.id.btn_add_employee);
        btnDeleteEmployee = view.findViewById(R.id.btn_delete_employee);
        rvEmployeeList = view.findViewById(R.id.rv_employee_list);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        employeesCollection = db.collection("hr-employees"); // Firestore collection name
        geofenceCollection = db.collection("geofence-mapping"); // Firestore geofence collection name

        // Initialize employee list and adapter
        employeeList = new ArrayList<>();
        employeeAdapter = new EmployeeAdapter(employeeList);
        rvEmployeeList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvEmployeeList.setAdapter(employeeAdapter);

        // Initialize geofence list and adapter for Spinner
        geofenceList = new ArrayList<>();
        geofenceAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, new ArrayList<>());
        geofenceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGeofenceID.setAdapter(geofenceAdapter);

        // Retrieve admin email from SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        adminEmail = sharedPreferences.getString("email", "default@example.com");

        // Set listeners for buttons
        btnAddEmployee.setOnClickListener(v -> addEmployee());
        btnDeleteEmployee.setOnClickListener(v -> deleteEmployee());

        // Fetch existing employees from Firestore
        fetchEmployees();

        return view;
    }

    // Add employee to Firestore
    private void addEmployee() {
        String empEmail = etEmpEmail.getText().toString().trim();
        String salaryStr = etSalary.getText().toString().trim();
        String department = etDepartment.getText().toString().trim();
        String geofenceID = spGeofenceID.getSelectedItem().toString().trim();

        if (empEmail.isEmpty() || salaryStr.isEmpty() || department.isEmpty() || geofenceID.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double salary = Double.parseDouble(salaryStr);

        // Create an AdminEmp object
        AdminEmp adminEmp = new AdminEmp(adminEmail, empEmail, salary, department, geofenceID);

        // Add employee to Firestore
        employeesCollection.add(adminEmp)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getActivity(), "Employee added successfully", Toast.LENGTH_SHORT).show();
                    etEmpEmail.setText(""); // Clear input fields
                    etSalary.setText("");
                    etDepartment.setText("");
                    fetchEmployees(); // Update the list
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to add employee", Toast.LENGTH_SHORT).show();
                });
    }

    // Delete employee from Firestore
    private void deleteEmployee() {
        String empEmail = etEmpEmail.getText().toString().trim();

        if (empEmail.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter an employee email to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        // Find employee by email
        employeesCollection.whereEqualTo("empEmail", empEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            documentSnapshot.getReference().delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getActivity(), "Employee deleted successfully", Toast.LENGTH_SHORT).show();
                                        etEmpEmail.setText(""); // Clear input field
                                        fetchEmployees(); // Update the list
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getActivity(), "Failed to delete employee", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(getActivity(), "No employee found with that email", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error finding employee to delete", Toast.LENGTH_SHORT).show();
                });
    }

    // Fetch employee list from Firestore
    private void fetchEmployees() {
        employeesCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null) {
                        employeeList.clear();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            AdminEmp adminEmp = documentSnapshot.toObject(AdminEmp.class);
                            if (adminEmp != null) {
                                employeeList.add(adminEmp);
                            }
                        }
                        employeeAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to load employee list", Toast.LENGTH_SHORT).show();
                });
    }

    // Fetch geofences based on employee country
    private void fetchGeofences(String country) {
        geofenceCollection.whereEqualTo("company", country) // Filter geofences by country
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        geofenceList.clear();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            GeofenceMapping geofence = documentSnapshot.toObject(GeofenceMapping.class);
                            if (geofence != null) {
                                geofenceList.add(geofence);
                            }
                        }

                        // Extract geofence IDs for Spinner
                        List<String> geofenceIds = new ArrayList<>();
                        for (GeofenceMapping geofence : geofenceList) {
                            geofenceIds.add(geofence.getGeofenceId());
                        }

                        // Update the Spinner with geofence IDs
                        geofenceAdapter.clear();
                        geofenceAdapter.addAll(geofenceIds);
                        geofenceAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to load geofences", Toast.LENGTH_SHORT).show();
                });
    }
}
