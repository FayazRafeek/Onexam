package com.example.onexam;

public class User {

    String userId, username;
    String fullName, phone, address,dob, sem,program;
    String dateOfJoin;

    public User() {
    }

    public User(String userId, String username, String fullName, String phone, String address, String dob, String sem, String program, String dateOfJoin) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.dob = dob;
        this.sem = sem;
        this.program = program;
        this.dateOfJoin = dateOfJoin;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getSem() {
        return sem;
    }

    public void setSem(String sem) {
        this.sem = sem;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getDateOfJoin() {
        return dateOfJoin;
    }

    public void setDateOfJoin(String dateOfJoin) {
        this.dateOfJoin = dateOfJoin;
    }
}
