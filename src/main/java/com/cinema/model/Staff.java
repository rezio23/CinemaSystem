package com.cinema.model;

import java.time.LocalDate;

public class Staff {
    private int staffId;
    private String fullName;
    private String role;
    private LocalDate hireDate;

    public Staff() {}

    public Staff(int staffId, String fullName, String role, LocalDate hireDate) {
        this.staffId = staffId;
        this.fullName = fullName;
        this.role = role;
        this.hireDate = hireDate;
    }

    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }

    @Override
    public String toString() {
        return fullName + " - " + role;
    }
}
