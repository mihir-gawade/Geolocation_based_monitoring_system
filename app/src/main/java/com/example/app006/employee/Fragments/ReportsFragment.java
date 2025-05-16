package com.example.app006.employee.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app006.R;
import com.example.app006.adapters.ReportAdapter;
import com.example.app006.models.Report;

import java.util.ArrayList;
import java.util.List;

public class ReportsFragment extends Fragment {

    private RecyclerView reportsRecyclerView;
    private ReportAdapter reportAdapter;
    private Button filterButton;

    private List<Report> reportList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.employee_fragment_reports, container, false);

        // Initialize the RecyclerView
        reportsRecyclerView = view.findViewById(R.id.reportsRecyclerView);
        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize filter button
        filterButton = view.findViewById(R.id.filterButton);

        // Add dummy reports data
        populateReportData();

        // Set adapter to RecyclerView
        reportAdapter = new ReportAdapter(reportList);
        reportsRecyclerView.setAdapter(reportAdapter);

        // Filter button click listener
        filterButton.setOnClickListener(v -> {
            // Here you can implement filter logic if needed
            Toast.makeText(getContext(), "Filter functionality coming soon.", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void populateReportData() {
        reportList.add(new Report("Attendance", "Employee Attendance Report for March", "20/03/2023"));
        reportList.add(new Report("Performance", "Employee Performance Review for Q1", "15/03/2023"));
        reportList.add(new Report("Attendance", "Employee Attendance Report for February", "28/02/2023"));
        reportList.add(new Report("Performance", "Employee Performance Review for Q2", "10/04/2023"));
        // Add more reports as needed
    }
}
