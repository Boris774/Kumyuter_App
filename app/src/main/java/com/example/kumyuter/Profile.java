package com.example.kumyuter;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity implements View.OnClickListener{

    ImageButton btnBack, btnName, btnContact, btnAddress, btnEmail, btnUsername, btnPassword;
    CircleImageView imgPicture;
    TextView txtName, txtContact, txtAddress, txtEmail, txtUsername, txtPicture;
    RelativeLayout btnLogout;

    String usercode, userrole, userprofile;
    Connection connection;
    Databasehelper databasehelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imgPicture = (CircleImageView)findViewById(R.id.imgPicture);
        txtName = (TextView)findViewById(R.id.txtName);
        txtContact = (TextView)findViewById(R.id.txtContact);
        txtAddress = (TextView)findViewById(R.id.txtAddress);
        txtEmail = (TextView)findViewById(R.id.txtEmail);
        txtUsername = (TextView)findViewById(R.id.txtUsername);
        txtPicture = (TextView)findViewById(R.id.txtPicture);
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnName = (ImageButton) findViewById(R.id.btnName);
        btnContact = (ImageButton) findViewById(R.id.btnContact);
        btnAddress = (ImageButton) findViewById(R.id.btnAddress);
        btnEmail = (ImageButton) findViewById(R.id.btnEmail);
        btnUsername = (ImageButton) findViewById(R.id.btnUsername);
        btnPassword = (ImageButton) findViewById(R.id.btnPassword);
        btnLogout = (RelativeLayout) findViewById(R.id.btnLogout);

        btnBack.setOnClickListener(this);
        btnName.setOnClickListener(this);
        btnContact.setOnClickListener(this);
        btnAddress.setOnClickListener(this);
        btnEmail.setOnClickListener(this);
        btnUsername.setOnClickListener(this);
        btnPassword.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
//        txtPicture.setOnClickListener(this);

        connection = new Connection(this);
        databasehelper = new Databasehelper(this);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        usercode = sharedPreferences.getString(Config.CODE_SHARED_PREF, null);
        userrole = sharedPreferences.getString(Config.ROLE_SHARED_PREF, null);
        userprofile = sharedPreferences.getString(Config.PROFILE_SHARED_PREF, null);

        if (!userprofile.equals("Unknown")){
            Picasso.get()
                    .load(userprofile)
                    .placeholder(R.drawable.profile_loader)
                    .error(R.drawable.profile_error)
                    .into(imgPicture);
        } else{
            Picasso.get()
                    .load(R.drawable.profile_image)
                    .placeholder(R.drawable.profile_loader)
                    .error(R.drawable.profile_error)
                    .into(imgPicture);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Userinfo();

        if (!userprofile.equals("Unknown")){
            Picasso.get()
                    .load(userprofile)
                    .placeholder(R.drawable.profile_loader)
                    .error(R.drawable.profile_error)
                    .into(imgPicture);
        } else{
            Picasso.get()
                    .load(R.drawable.profile_image)
                    .placeholder(R.drawable.profile_loader)
                    .error(R.drawable.profile_error)
                    .into(imgPicture);
        }
    }

    private void Userinfo(){
        Cursor datauser = databasehelper.userview(usercode);

        if (datauser.getCount() > 0) {
            if (datauser.moveToFirst()) {
                do {
                    String id, name, contact, address, email, user, pass;
                    String contact_a, contact_b, contact_c;

                    id = datauser.getString(1).toString();
                    name = datauser.getString(2).toString();
                    contact = datauser.getString(3).toString();
                    address = datauser.getString(4).toString();
                    email = datauser.getString(5).toString();
                    user = datauser.getString(6).toString();
                    pass = datauser.getString(7).toString();

                    txtName.setText(name);
                    if (contact.equals("Unknown")){
                        txtContact.setText("No Phone Number");
                    } else{
                        contact_a = datauser.getString(3).toString().substring(0, 4);
                        contact_b = datauser.getString(3).toString().substring(4, 8);
                        contact_c = datauser.getString(3).toString().substring(8, 11);

                        txtContact.setText(contact_a + "  " + contact_b + "  "+ contact_c);
                    }
                    txtAddress.setText(address);
                    txtEmail.setText(email);
                    txtUsername.setText(user);

                } while (datauser.moveToNext());
            }
        }
        databasehelper.close();
    }

    private void Logout(){
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.layout_dialog_logout, viewGroup, false);
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connection.isConnected()) {
                    SharedPreferences preferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean(Config.LOG_SHARED_PREF, false);
                    editor.commit();

                    Intent login = new Intent(Profile.this, Login.class);
                    startActivity(login);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                } else{
                    alertDialog.dismiss();
                    Toast.makeText(Profile.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                super.finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
            case R.id.btnName:
                Intent name = new Intent(Profile.this, Profilename.class);
                startActivity(name);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.btnContact:
                Intent contact = new Intent(Profile.this, Profilecontact.class);
                startActivity(contact);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.btnAddress:
                Intent address = new Intent(Profile.this, Profileaddress.class);
                startActivity(address);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.btnEmail:
                Intent email = new Intent(Profile.this, Profileemail.class);
                startActivity(email);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.btnUsername:
                Intent username = new Intent(Profile.this, Profileusername.class);
                startActivity(username);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.btnPassword:
                Intent password = new Intent(Profile.this, Profilepassword.class);
                startActivity(password);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.btnLogout:
                Logout();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}