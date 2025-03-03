package com.example.app006.models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class EmployeeAttendance {
    private String empEmail;
    private int geofenceId;
    private LocalDate date;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private boolean geo9am;
    private boolean geo5pm;
    private double productivityScore; // Based on GeofenceEmpTracking data

    // Default constructor
    public EmployeeAttendance() {
    }

    public EmployeeAttendance(String empEmail, int geofenceId, LocalDate date, LocalTime checkInTime, LocalTime checkOutTime, boolean geo9am, boolean geo5pm, double productivityScore) {
        this.empEmail = empEmail;
        this.geofenceId = geofenceId;
        this.date = date;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.geo9am = geo9am;
        this.geo5pm = geo5pm;
        this.productivityScore = productivityScore;
    }

    public String getEmpEmail() {
        return empEmail;
    }

    public void setEmpEmail(String empEmail) {
        this.empEmail = empEmail;
    }

    public int getGeofenceId() {
        return geofenceId;
    }

    public void setGeofenceId(int geofenceId) {
        this.geofenceId = geofenceId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public boolean isGeo9am() {
        return geo9am;
    }

    public void setGeo9am(boolean geo9am) {
        this.geo9am = geo9am;
    }

    public boolean isGeo5pm() {
        return geo5pm;
    }

    public void setGeo5pm(boolean geo5pm) {
        this.geo5pm = geo5pm;
    }

    public double getProductivityScore() {
        return productivityScore;
    }

    public void setProductivityScore(double productivityScore) {
        this.productivityScore = productivityScore;
    }

    @Override
    public String toString() {
        return "EmployeeAttendance{" +
                "empEmail='" + empEmail + '\'' +
                ", geofenceId=" + geofenceId +
                ", date=" + date +
                ", checkInTime=" + checkInTime +
                ", checkOutTime=" + checkOutTime +
                ", geo9am=" + geo9am +
                ", geo5pm=" + geo5pm +
                ", productivityScore=" + productivityScore +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeAttendance that = (EmployeeAttendance) o;
        return geofenceId == that.geofenceId &&
                geo9am == that.geo9am &&
                geo5pm == that.geo5pm &&
                Double.compare(that.productivityScore, productivityScore) == 0 &&
                Objects.equals(empEmail, that.empEmail) &&
                Objects.equals(date, that.date) &&
                Objects.equals(checkInTime, that.checkInTime) &&
                Objects.equals(checkOutTime, that.checkOutTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(empEmail, geofenceId, date, checkInTime, checkOutTime, geo9am, geo5pm, productivityScore);
    }
}
