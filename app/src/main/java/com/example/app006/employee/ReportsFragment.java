package com.example.app006.employee;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.app006.R;

public class ReportsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.employee_reports, container, false);

        // Find the TextView and set the message (optional, since it's already in XML)
        TextView reportsMessage = view.findViewById(R.id.reports_message);

        if (reportsMessage != null) {
            reportsMessage.setText("Reports can be generated here");
        }

        return view;
    }
}
