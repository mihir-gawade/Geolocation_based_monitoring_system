package com.example.app006.models;

public class Appraisals {

    private String empEmail;
    private String adminEmail;
    private String date;
    private int productivityScore;
    private float bonusPercentage;
    private float salaryIncrement;

    // Constructor
    public Appraisals(String empEmail, String adminEmail, String date,
                      int productivityScore, float bonusPercentage, float salaryIncrement) {
        this.empEmail = empEmail;
        this.adminEmail = adminEmail;
        this.date = date;
        this.productivityScore = productivityScore; // from EmployeeAttendance
        this.bonusPercentage = bonusPercentage;
        this.salaryIncrement = salaryIncrement;
    }

    // Getters and Setters
    public String getEmpEmail() {
        return empEmail;
    }

    public void setEmpEmail(String empEmail) {
        this.empEmail = empEmail;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getProductivityScore() {
        return productivityScore;
    }

    public void setProductivityScore(int productivityScore) {
        this.productivityScore = productivityScore;
    }

    public float getBonusPercentage() {
        return bonusPercentage;
    }

    public void setBonusPercentage(float bonusPercentage) {
        this.bonusPercentage = bonusPercentage;
    }

    public float getSalaryIncrement() {
        return salaryIncrement;
    }

    public void setSalaryIncrement(float salaryIncrement) {
        this.salaryIncrement = salaryIncrement;
    }

    // Optionally, override toString() for better representation
    @Override
    public String toString() {
        return "Appraisals{" +
                "empEmail='" + empEmail + '\'' +
                ", adminEmail='" + adminEmail + '\'' +
                ", date='" + date + '\'' +
                ", productivityScore=" + productivityScore +
                ", bonusPercentage=" + bonusPercentage +
                ", salaryIncrement=" + salaryIncrement +
                '}';
    }
}
