package com.example.kumyuter;

public class Customcomment {
    private String commentcode;
    private String usercode;
    private String drivercode;
    private String comment;
    private String date;

    public Customcomment() {
    }

    public Customcomment(String commentcode, String usercode, String drivercode, String comment, String date) {
        this.commentcode = commentcode;
        this.usercode = usercode;
        this.drivercode = drivercode;
        this.comment = comment;
        this.date = date;
    }

    public String getCommentcode() {
        return commentcode;
    }

    public void setCommentcode(String commentcode) {
        this.commentcode = commentcode;
    }

    public String getUsercode() {
        return usercode;
    }

    public void setUsercode(String usercode) {
        this.usercode = usercode;
    }

    public String getDrivercode() {
        return drivercode;
    }

    public void setDrivercode(String drivercode) {
        this.drivercode = drivercode;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
