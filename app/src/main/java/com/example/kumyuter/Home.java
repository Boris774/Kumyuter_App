package com.example.kumyuter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity implements View.OnClickListener{

    CircleImageView imgPicture;
    TextView txtName, txtRole, txtScan, txtMessage;
    RelativeLayout btnScan, btnHistory, btnProfile;

    Connection connection;
    Databasehelper databasehelper;
    DatabaseReference databaseReference;

    String usercode, userrole, userprofile, drivercode;
    private boolean driverlog = false;
    private static long backpressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnScan = (RelativeLayout) findViewById(R.id.btnScan);
        btnHistory = (RelativeLayout) findViewById(R.id.btnHistory);
        btnProfile = (RelativeLayout) findViewById(R.id.btnProfile);
        imgPicture = (CircleImageView) findViewById(R.id.imgPicture);
        txtName = (TextView) findViewById(R.id.txtName);
        txtRole = (TextView) findViewById(R.id.txtRole);
        txtScan = findViewById(R.id.txtScan);
        txtMessage = findViewById(R.id.txtMessage);

        btnScan.setOnClickListener(this);
        btnHistory.setOnClickListener(this);
        btnProfile.setOnClickListener(this);

        connection = new Connection(this);
        databasehelper = new Databasehelper(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        usercode = sharedPreferences.getString(Config.CODE_SHARED_PREF, null);
        userrole = sharedPreferences.getString(Config.ROLE_SHARED_PREF, null);
        userprofile = sharedPreferences.getString(Config.PROFILE_SHARED_PREF, null);
        drivercode = sharedPreferences.getString(Config.DRIVERCODE_SHARED_PREF, null);
        driverlog = sharedPreferences.getBoolean(Config.DRIVERLOG_SHARED_PREF, false);

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

        Userinfo();
    }

    ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(), result->
    {
        if(result.getContents() !=null)
        {
            if (connection.isConnected()){
                txtScan.setText(result.getContents());
                txtMessage.setText("");
                databaseReference.child("drivers-info").orderByChild("code").startAt(result.getContents().toString()).endAt(result.getContents().toString() + "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int counter = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            String id = snapshot.getKey();
                            String drivercode = snapshot.child("code").getValue(String.class);

                            if (drivercode.toLowerCase().equals(txtScan.getText().toString().toLowerCase())){
                                Intent driver = new Intent(Home.this, Driverinfo.class);
                                driver.putExtra("DRIVERCODE", txtScan.getText().toString());
                                startActivity(driver);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                                counter = counter + 1;
                                break;
                            }
                        }

                        if (counter == 0){
                            txtMessage.setText("There was no driver found.");
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(Home.this, "Timeout server", Toast.LENGTH_SHORT).show();
                        databaseError.toException();
                    }
                } );
            }
            else{
                txtMessage.setText("No internet connection.");
            }
        }
    });

    @Override
    protected void onResume() {
        super.onResume();

        if(driverlog){
            Intent driverinfo = new Intent(Home.this, Driverinfo.class);
            driverinfo.putExtra("DRIVERCODE", drivercode.toString());
            driverinfo.putExtra("DRIVERSET", 1);
            startActivity(driverinfo);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

        Userinfo();
    }

    private void Userinfo(){
        if (connection.isConnected()){
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("users-info");

            Query query = databaseReference.orderByChild("code").startAt(usercode).endAt(usercode + "\uf8ff");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            final String code = snapshot.child("code").getValue().toString();
                            final String name = snapshot.child("name").getValue().toString();
                            final String contact = snapshot.child("contact").getValue().toString();
                            final String address = snapshot.child("address").getValue().toString();
                            final String email = snapshot.child("email").getValue().toString();
                            final String user = snapshot.child("user").getValue().toString();
                            final String pass = snapshot.child("pass").getValue().toString();
                            final String role = snapshot.child("role").getValue().toString();
                            final String picture = snapshot.child("picture").getValue().toString();
                            final String status = snapshot.child("status").getValue().toString();

                            boolean checkuser = databasehelper.checkuser(usercode);
                            if (checkuser){
                                databasehelper.usersave(code, name, contact, address, email, user, pass, role, status);
                                databasehelper.close();
                            } else{
                                databasehelper.userupdate(code, name, contact, address, email, user, pass, role, status);
                                databasehelper.close();
                            }

                            txtName.setText(name);
                            txtRole.setText(role);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    databaseError.toException();
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnScan:
                ScanOptions options = new ScanOptions();
                options.setPrompt("Scan a QR code to identify a driver");
                options.setBeepEnabled(true);
                options.setOrientationLocked(true);
                options.setCaptureActivity(Capturescanner.class);
                barLaucher.launch(options);
                break;
            case R.id.btnHistory:
                Intent history = new Intent(Home.this, History.class);
                startActivity(history);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.btnProfile:
                Intent profile = new Intent(Home.this, Profile.class);
                startActivity(profile);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (backpressed + 2000 > System.currentTimeMillis()) {
            moveTaskToBack(true);
            super.finish();
        } else {
            Toast.makeText(this,"Press once again to exit",Toast.LENGTH_SHORT).show();
            backpressed = System.currentTimeMillis();
        }
    }
}