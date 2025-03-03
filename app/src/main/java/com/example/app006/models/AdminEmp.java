package com.example.app006.models;

import java.util.Objects;

public class AdminEmp {
    private String adminEmail;
    private String empEmail;
    private double salary;
    private String department;
    private String geofenceID;

    // Default constructor (required for Firebase)
    public AdminEmp() {
    }

    public AdminEmp(String adminEmail, String empEmail, double salary, String department, String geofenceID) {
        this.adminEmail = adminEmail;
        this.empEmail = empEmail;
        this.salary = salary;
        this.department = department;
        this.geofenceID = geofenceID;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getEmpEmail() {
        return empEmail;
    }

    public void setEmpEmail(String empEmail) {
        this.empEmail = empEmail;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getGeofenceID() {
        return geofenceID;
    }

    public void setGeofenceID(String geofenceID) {
        this.geofenceID = geofenceID;
    }

    @Override
    public String toString() {
        return "AdminEmp{" +
                "adminEmail='" + adminEmail + '\'' +
                ", empEmail='" + empEmail + '\'' +
                ", salary=" + salary +
                ", department='" + department + '\'' +
                ", geofenceID='" + geofenceID + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdminEmp adminEmp = (AdminEmp) o;
        return Double.compare(adminEmp.salary, salary) == 0 &&
                Objects.equals(adminEmail, adminEmp.adminEmail) &&
                Objects.equals(empEmail, adminEmp.empEmail) &&
                Objects.equals(department, adminEmp.department) &&
                Objects.equals(geofenceID, adminEmp.geofenceID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(adminEmail, empEmail, salary, department, geofenceID);
    }
}
