package com.example.app006.admin.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.RadioButton;
import android.widget.BaseAdapter;

import com.example.app006.R;
import com.example.app006.models.LeaveRequest;

import java.util.List;

public class LeaveRequestAdapter extends BaseAdapter {
    private Context context;
    private List<LeaveRequest> leaveRequests;
    private int selectedPosition = -1; // Track the selected item

    public LeaveRequestAdapter(Context context, List<LeaveRequest> leaveRequests) {
        this.context = context;
        this.leaveRequests = leaveRequests;
    }

    @Override
    public int getCount() {
        return leaveRequests.size();
    }

    @Override
    public Object getItem(int position) {
        return leaveRequests.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_leave_request, parent, false);
        }

        TextView tvEmployeeName = convertView.findViewById(R.id.tv_employee_name);
        TextView tvLeaveType = convertView.findViewById(R.id.tv_leave_type);
        TextView tvDateRange = convertView.findViewById(R.id.tv_date_range);
        RadioButton rbSelect = convertView.findViewById(R.id.rb_select);

        LeaveRequest request = leaveRequests.get(position);

        tvEmployeeName.setText(request.getEmployeeName());
        tvLeaveType.setText("Type: " + request.getLeaveType());
        tvDateRange.setText("From " + request.getStartDate() + " to " + request.getEndDate());

        rbSelect.setChecked(position == selectedPosition);
        rbSelect.setOnClickListener(v -> selectedPosition = position);

        return convertView;
    }

    public LeaveRequest getSelectedRequest() {
        if (selectedPosition != -1) {
            return leaveRequests.get(selectedPosition);
        }
        return null;
    }
}
