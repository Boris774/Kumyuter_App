package com.example.kumyuter;

public class Customrequest {
    private String code;
    private String usercode;
    private String passengername;
    private String passengercontact;
    private String passengeraddress;
    private String note;
    private String dateon;
    private String status;

    public Customrequest() {
    }

    public Customrequest(String code, String usercode, String passengername, String passengercontact, String passengeraddress, String note, String dateon, String status) {
        this.code = code;
        this.usercode = usercode;
        this.passengername = passengername;
        this.passengercontact = passengercontact;
        this.passengeraddress = passengeraddress;
        this.note = note;
        this.dateon = dateon;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUsercode() {
        return usercode;
    }

    public void setUsercode(String usercode) {
        this.usercode = usercode;
    }

    public String getPassengername() {
        return passengername;
    }

    public void setPassengername(String passengername) {
        this.passengername = passengername;
    }

    public String getPassengercontact() {
        return passengercontact;
    }

    public void setPassengercontact(String passengercontact) {
        this.passengercontact = passengercontact;
    }

    public String getPassengeraddress() {
        return passengeraddress;
    }

    public void setPassengeraddress(String passengeraddress) {
        this.passengeraddress = passengeraddress;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDateon() {
        return dateon;
    }

    public void setDateon(String dateon) {
        this.dateon = dateon;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
