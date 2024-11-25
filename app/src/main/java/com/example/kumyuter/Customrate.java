package com.example.kumyuter;

public class Customrate {
    private String id;
    private String passengercode;
    private String drivercode;
    private String rate;
    private String message;
    private String datecreated;

    public Customrate() {
    }

    public Customrate(String id, String passengercode, String drivercode, String rate, String message, String datecreated) {
        this.id = id;
        this.passengercode = passengercode;
        this.drivercode = drivercode;
        this.rate = rate;
        this.message = message;
        this.datecreated = datecreated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassengercode() {
        return passengercode;
    }

    public void setPassengercode(String passengercode) {
        this.passengercode = passengercode;
    }

    public String getDrivercode() {
        return drivercode;
    }

    public void setDrivercode(String drivercode) {
        this.drivercode = drivercode;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDatecreated() {
        return datecreated;
    }

    public void setDatecreated(String datecreated) {
        this.datecreated = datecreated;
    }
}
