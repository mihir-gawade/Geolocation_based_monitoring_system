package com.example.app006.models;

import java.util.Objects;

public class GeofenceMapping {
    private String geofenceId;
    private double latitude;
    private double longitude;
    private double radius;
    private String company;
    private String city;

    // Default constructor
    public GeofenceMapping() {
    }

    public GeofenceMapping(String geofenceId, double latitude, double longitude, double radius, String company, String city) {
        this.geofenceId = geofenceId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.company = company;
        this.city = city;
    }

    public String getGeofenceId() {
        return geofenceId;
    }

    public void setGeofenceId(String geofenceId) {
        this.geofenceId = geofenceId;
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

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "GeofenceMapping{" +
                "geofenceId='" + geofenceId + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", radius=" + radius +
                ", company='" + company + '\'' +
                ", city='" + city + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeofenceMapping that = (GeofenceMapping) o;
        return Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.longitude, longitude) == 0 &&
                Double.compare(that.radius, radius) == 0 &&
                Objects.equals(geofenceId, that.geofenceId) &&
                Objects.equals(company, that.company) &&
                Objects.equals(city, that.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(geofenceId, latitude, longitude, radius, company, city);
    }
}
