package com.example.app006.models;

public class LeaveRequest {
    private String requestId; // Unique ID for the request
    private String employeeName;
    private String employeeId;
    private String leaveType;
    private String startDate;
    private String endDate;
    private String reason;
    private String status; // Pending, Approved, Rejected

    public LeaveRequest() {
        // Default constructor required for Firebase or serialization
    }

    public LeaveRequest(String requestId, String employeeName, String employeeId, String leaveType,
                        String startDate, String endDate, String reason, String status) {
        this.requestId = requestId;
        this.employeeName = employeeName;
        this.employeeId = employeeId;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = status;
    }

    // Getter for requestId
    public String getRequestId() {
        return requestId;
    }

    // Setter for requestId
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
