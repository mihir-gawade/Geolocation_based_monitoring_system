package com.example.app006.models;

import java.util.Objects;
import java.time.LocalDate;
import java.time.LocalTime;

public class GeofenceEmpTracking {
    private String empEmail;
    private LocalDate date;
    private LocalTime time;
    private double latitude;
    private double longitude;
    private boolean insideGeofence;

    // Default constructor
    public GeofenceEmpTracking() {
    }

    public GeofenceEmpTracking(String empEmail, LocalDate date, LocalTime time, double latitude, double longitude, boolean insideGeofence) {
        this.empEmail = empEmail;
        this.date = date;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.insideGeofence = insideGeofence;
    }

    public String getEmpEmail() {
        return empEmail;
    }

    public void setEmpEmail(String empEmail) {
        this.empEmail = empEmail;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isInsideGeofence() {
        return insideGeofence;
    }

    public void setInsideGeofence(boolean insideGeofence) {
        this.insideGeofence = insideGeofence;
    }

    @Override
    public String toString() {
        return "GeofenceEmpTracking{" +
                "empEmail='" + empEmail + '\'' +
                ", date=" + date +
                ", time=" + time +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", insideGeofence=" + insideGeofence +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeofenceEmpTracking that = (GeofenceEmpTracking) o;
        return Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.longitude, longitude) == 0 &&
                insideGeofence == that.insideGeofence &&
                Objects.equals(empEmail, that.empEmail) &&
                Objects.equals(date, that.date) &&
                Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(empEmail, date, time, latitude, longitude, insideGeofence);
    }
}
