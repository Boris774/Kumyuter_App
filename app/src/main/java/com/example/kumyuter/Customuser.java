package com.example.kumyuter;

public class Customuser {
    private String code;
    private String name;
    private String contact;
    private String address;
    private String email;
    private String user;
    private String pass;
    private String role;
    private String picture;
    private String status;

    public Customuser() {
    }

    public Customuser(String code, String name, String contact, String address, String email, String user, String pass, String role, String picture, String status) {
        this.code = code;
        this.name = name;
        this.contact = contact;
        this.address = address;
        this.email = email;
        this.user = user;
        this.pass = pass;
        this.role = role;
        this.picture = picture;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
