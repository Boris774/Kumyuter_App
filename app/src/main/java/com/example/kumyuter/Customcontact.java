package com.example.kumyuter;

public class Customcontact {
    private String code;
    private String emergencyname;
    private String contact;
    private String status;

    public Customcontact() {
    }

    public Customcontact(String code, String emergencyname, String contact, String status) {
        this.code = code;
        this.emergencyname = emergencyname;
        this.contact = contact;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmergencyname() {
        return emergencyname;
    }

    public void setEmergencyname(String emergencyname) {
        this.emergencyname = emergencyname;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
