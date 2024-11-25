package com.example.kumyuter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Databasehelper extends SQLiteOpenHelper{

    private static final String DATABASE = "kumyuter.db";
    private static final String USERINFO = "userinfo";
    private static final int VERSION = 1;

    private static final String CREATE_USER = "CREATE TABLE " + USERINFO + "(ID integer primary key autoincrement, USERCODE text, NAME text, CONTACT text, ADDRESS text, EMAIL text, USER text, PASS text, ROLE text, STATUS text)";

    public Databasehelper(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USERINFO);
        onCreate(db);
    }

    public void close(){
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen()){
            db.close();
        }
    }

    public boolean checkusername(String code){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ USERINFO +" WHERE USER=?", new String[]{code});
        if (cursor.getCount() > 0){
            return false;
        } else {
            return true;
        }
    }

    public boolean checkuser(String code){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ USERINFO +" WHERE USERCODE=?", new String[]{code});
        if (cursor.getCount() > 0){
            return false;
        } else {
            return true;
        }
    }

    public boolean usersave(String code, String name, String contact, String address, String email, String user, String pass, String role, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("USERCODE", code);
        contentValues.put("NAME", name);
        contentValues.put("CONTACT", contact);
        contentValues.put("ADDRESS", address);
        contentValues.put("EMAIL", email);
        contentValues.put("USER", user);
        contentValues.put("PASS", pass);
        contentValues.put("ROLE", role);
        contentValues.put("STATUS", status);
        long insert = db.insert(USERINFO, null, contentValues);
        if (insert == -1){
            return false;
        }
        else {
            return true;
        }
    }

    public boolean userupdate(String code, String name, String contact, String address, String email, String user, String pass, String role, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("USERCODE", code);
        contentValues.put("NAME", name);
        contentValues.put("CONTACT", contact);
        contentValues.put("ADDRESS", address);
        contentValues.put("EMAIL", email);
        contentValues.put("USER", user);
        contentValues.put("PASS", pass);
        contentValues.put("ROLE", role);
        contentValues.put("STATUS", status);
        long update = db.update(USERINFO, contentValues, "USERCODE=?", new String[]{code});
        if (update == -1){
            return false;
        }
        else {
            return true;
        }
    }

    public Cursor userview(String code){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor data = db.rawQuery("SELECT * FROM "+ USERINFO +" WHERE USERCODE=?", new String[]{code});
        return data;
    }

    public Cursor userall(String status){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor data = db.rawQuery("SELECT * FROM "+ USERINFO +" WHERE STATUS=?", new String[]{status});
        return data;
    }

    public boolean userstatusupdate(String code, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("USERCODE", code);
        contentValues.put("STATUS", status);
        long update = db.update(USERINFO, contentValues, "USERCODE=?", new String[]{code});
        if (update == -1){
            return false;
        }
        else {
            return true;
        }
    }

    public void userdelete(String code){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(USERINFO, "USERCODE=?", new String[]{code});
    }
}
