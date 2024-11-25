package com.example.kumyuter;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.example.kumyuter.R.id.txtDestination;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Driverinfo extends AppCompatActivity implements View.OnClickListener{

    ImageButton btnBack, btnNote, btnRecorder, btnBell;
    TextView txtName, txtDlcode, txtDlexpiry, txtFranchiseexpiry, txtMotornumber, progressText, txtRide, txtComment, txtRate, txtRatings, txtOrigin, txtDestination, txtExpiredriver, txtExpirefranchise;
    RecyclerView recView;
    LinearLayout linWait, linMessage;
    Connection connection;
    String code, requestcode, usercode, drivercode, driverpin, passengername, passengercontact, passengeraddress, drivercontact, driveraddress, note;
    Databasehelper databasehelper;
    Double rating = 0.0;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd h:mm a");
    SimpleDateFormat dateViewdate = new SimpleDateFormat("MMMM dd, yyyy");
    SimpleDateFormat dateViewtime = new SimpleDateFormat("h:mm a");
    DecimalFormat formatter = new DecimalFormat("##.0");
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Random rand;

    int a, b, voice = 0, driverset;
    private static final int REQUEST_CODE = 101;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    MediaRecorder mediaRecorder;
    private static String mFileName = null;
    private ArrayList<Customcontact> list;
    private Customcontactadapter customcontactadapter;
    private Customhistory customhistory;
    private Customrequest customrequest;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driverinfo);

        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnNote = (ImageButton) findViewById(R.id.btnNote);
        btnRecorder = (ImageButton) findViewById(R.id.btnRecorder);
        btnBell = (ImageButton) findViewById(R.id.btnBell);
        txtName = (TextView) findViewById(R.id.txtName);
        txtDlcode = (TextView) findViewById(R.id.txtDlcode);
        txtDlexpiry = (TextView) findViewById(R.id.txtDlexpiry);
        txtFranchiseexpiry = (TextView) findViewById(R.id.txtFranchiseexpiry);
        txtMotornumber = (TextView) findViewById(R.id.txtMotornumber);
        txtExpiredriver = (TextView) findViewById(R.id.txtExpiredriver);
        txtExpirefranchise = (TextView) findViewById(R.id.txtExpirefranchise);
        progressText = (TextView) findViewById(R.id.progressText);
        txtRatings = (TextView) findViewById(R.id.txtRatings);
        txtRide = (TextView) findViewById(R.id.txtRide);
        txtComment = (TextView) findViewById(R.id.txtComment);
        txtRate = (TextView) findViewById(R.id.txtRate);
        txtOrigin = (TextView) findViewById(R.id.txtOrigin);
        txtDestination = (TextView) findViewById(R.id.txtDestination);
        linWait = (LinearLayout) findViewById(R.id.linWait);
        linMessage = (LinearLayout) findViewById(R.id.linMessage);
        recView = (RecyclerView) findViewById(R.id.recView);
        recView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recView.setLayoutManager(linearLayoutManager);

        linWait.setVisibility(View.VISIBLE);
        recView.setVisibility(View.GONE);

        btnBack.setOnClickListener(this);
        btnNote.setOnClickListener(this);
        btnBell.setOnClickListener(this);
        txtComment.setOnClickListener(this);
        txtRate.setOnClickListener(this);

        connection = new Connection(this);
        databasehelper = new Databasehelper(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        usercode = sharedPreferences.getString(Config.CODE_SHARED_PREF, null);
        driverpin = sharedPreferences.getString(Config.DRIVERSET_SHARED_PREF, null);
        note = sharedPreferences.getString(Config.EMERGENCYNOTE_SHARED_PREF, null);

        Intent i = this.getIntent();
        drivercode = i.getExtras().getString("DRIVERCODE").toString();
        driverset = i.getExtras().getInt("DRIVERSET");

        txtExpiredriver.setVisibility(View.INVISIBLE);
        txtExpirefranchise.setVisibility(View.INVISIBLE);

        if (driverset == 2){
            txtRide.setEnabled(false);
            txtRide.setTextColor(getResources().getColor(R.color.colorSecondary));
            txtRide.setBackgroundResource(R.drawable.design_round_border);
        }
        else if (driverset == 1){
            txtRide.setEnabled(true);
            txtRide.setTextColor(getResources().getColor(R.color.colorWhite));
            txtRide.setBackgroundResource(R.drawable.design_round_highlight_text_gray);
        }

        if (driverpin != null){
            txtOrigin.setText(driverpin);
            txtRide.setText("Stop");
            Toast.makeText(Driverinfo.this, "Set Point A", Toast.LENGTH_SHORT).show();
        }

        User();
        Userinfo();

        Query query = FirebaseDatabase.getInstance().getReference().child("contact-info").limitToLast(20);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null){
                    linMessage.setVisibility(View.GONE);
                    recView.setVisibility(View.VISIBLE);
                }
                else{
                    linMessage.setVisibility(View.VISIBLE);
                    recView.setVisibility(View.GONE);
                }
                linWait.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        list = new ArrayList<>();
        customcontactadapter = new Customcontactadapter(this, list);
        recView.setAdapter(customcontactadapter);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Customcontact customcontact = dataSnapshot.getValue(Customcontact.class);
                    list.add(customcontact);
                }

                if (list.size() == 0){
                    linMessage.setVisibility(View.VISIBLE);
                    recView.setVisibility(View.GONE);
                }
                customcontactadapter.notifyDataSetChanged();            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException();
            }
        });

        mediaRecorder = new MediaRecorder();

        txtRate.setText("Rated");
        txtRate.setEnabled(false);
        txtRate.setTextColor(getResources().getColor(R.color.colorSecondary));
        txtRate.setBackgroundResource(R.drawable.design_round_border);

        btnRecorder.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (voice == 0){
                    if (CheckPermissions()) {

                    }
                    else{
                        RequestPermissions();
                    }
                    try {
                        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

                        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +"/Kumyuter");
                        if (!folder.exists()){
                            folder.mkdirs();
                        }

                        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                        File file = new File(path, "/Kumyuter/Kumyuter "+ new SimpleDateFormat("yyMMdd-HHmmss").format(Calendar.getInstance().getTime()) +".3gp");

                        mediaRecorder.setOutputFile(file);
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    Toast.makeText(Driverinfo.this, "Start audio recording", Toast.LENGTH_SHORT).show();

                    voice = 1;
                }
                else if (voice == 1){
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    Toast.makeText(Driverinfo.this, "Stop audio recording", Toast.LENGTH_SHORT).show();
                    voice = 0;
                }
            }
        });

        txtRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

                if (txtRide.getText().toString().equals("Ride")){
                    getcurrentLocation();
                }
                else if (txtRide.getText().toString().equals("Stop")){
                    getcurrentLocation();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Userinfo();
        User();
    }

    private  void Savelocation(){
        if (connection.isConnected()){
            if (!txtOrigin.getText().toString().trim().isEmpty() && !txtDestination.getText().toString().trim().isEmpty()){

                txtRide.setEnabled(false);
                txtRide.setTextColor(getResources().getColor(R.color.colorSecondary));
                txtRide.setBackgroundResource(R.drawable.design_round_border);

                rand = new Random();
                a = rand.nextInt((999 - 100))+  100;

                customhistory = new Customhistory();
                firebaseDatabase = firebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference().child("history-info");

                code = new SimpleDateFormat("yyMMdd-HHmmss-" + a).format(Calendar.getInstance().getTime());

                customhistory.setCode(code);
                customhistory.setUsercode(usercode);
                customhistory.setPassengername(passengername);
                customhistory.setPassengercontact(passengercontact);
                customhistory.setPassengeraddress(passengeraddress);
                customhistory.setDrivercode(drivercode);
                customhistory.setDrivername(txtName.getText().toString().trim());
                customhistory.setDrivercontact(drivercontact);
                customhistory.setDriveraddress(driveraddress);
                customhistory.setDlcode(txtDlcode.getText().toString());
                customhistory.setDlexpiry(txtDlexpiry.getText().toString());
                customhistory.setMotorplate(txtMotornumber.getText().toString());
                customhistory.setFranchiseexpriy(txtFranchiseexpiry.getText().toString());
                customhistory.setOrigin(txtOrigin.getText().toString().trim());
                customhistory.setDestination(txtDestination.getText().toString().trim());
                customhistory.setDateon(new SimpleDateFormat("yyyy-MM-dd  h:mm a", Locale.getDefault()).format(new Date()));

                databaseReference.child(customhistory.getCode()).setValue(customhistory);

                Toast.makeText(Driverinfo.this, "Saved", Toast.LENGTH_SHORT).show();
                txtOrigin.setText("");
                txtDestination.setText("");
            }
        }
        else{
            Toast.makeText(Driverinfo.this, "No internet connection", Toast.LENGTH_LONG).show();
        }
    }

    public boolean CheckPermissions() {
        // this method is used to check permission
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions() {
        ActivityCompat.requestPermissions(Driverinfo.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (REQUEST_CODE){
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED) {
                    getcurrentLocation();
                }
                break;
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    private void getcurrentLocation(){
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this
                , Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION
                    }, REQUEST_CODE);
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    currentLocation = location;

                    try {
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                        String address = addresses.get(0).getAddressLine(0);
                        //Address address = addresses.get(0);
                        //String street = address.getThoroughfare();
                        //etOrigin.setText(street);
                        if (txtRide.getText().toString().equals("Ride")){
                            txtOrigin.setText(address);
                            txtRide.setText("Stop");
                            Toast.makeText(Driverinfo.this, "Set Point A", Toast.LENGTH_SHORT).show();

                            SharedPreferences sharedPreferences = Driverinfo.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            editor.putString(Config.DRIVERCODE_SHARED_PREF, drivercode);
                            editor.putString(Config.DRIVERSET_SHARED_PREF, address.toString());
                            editor.putBoolean(Config.DRIVERLOG_SHARED_PREF, true);
                            editor.commit();

                            currentLocation = null;
                        }
                        else if (txtRide.getText().toString().equals("Stop")){
                            txtDestination.setText(address);
                            txtRide.setText("Ride");
                            txtRide.setEnabled(false);
                            txtRide.setTextColor(getResources().getColor(R.color.colorSecondary));
                            txtRide.setBackgroundResource(R.drawable.design_round_border);
                            Toast.makeText(Driverinfo.this, "Set Point B", Toast.LENGTH_SHORT).show();

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Savelocation();

                                    SharedPreferences sharedPreferences = Driverinfo.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();

                                    editor.putString(Config.DRIVERCODE_SHARED_PREF, null);
                                    editor.putString(Config.DRIVERSET_SHARED_PREF, null);
                                    editor.putBoolean(Config.DRIVERLOG_SHARED_PREF, false);
                                    editor.commit();

                                    currentLocation = null;
                                }
                            }, 5000);
                        }

                        Rateus();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void Userinfo(){
        if (connection.isConnected()){
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("drivers-info");

            Query query = databaseReference.orderByChild("code").equalTo(drivercode);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            final String code = snapshot.child("code").getValue().toString();
                            final String name = snapshot.child("first").getValue().toString() + " " + snapshot.child("last").getValue().toString();
                            final String dlcode = snapshot.child("dlnum").getValue().toString() + "  -  " + snapshot.child("dlcode").getValue().toString();
                            final String contact = snapshot.child("contact").getValue().toString();
                            final String address = snapshot.child("address").getValue().toString();
                            final String dlexpiry = snapshot.child("dlexpiry").getValue().toString();
                            final String motorplate = snapshot.child("motorplate").getValue().toString();
                            final String franchiseexpiry = snapshot.child("franchiseexpiry").getValue().toString();

                            txtName.setText(name);
                            txtDlcode.setText(dlcode);
                            txtDlexpiry.setText(dlexpiry);
                            txtMotornumber.setText(motorplate);
                            txtFranchiseexpiry.setText(franchiseexpiry);
                            drivercontact = contact.toString();
                            driveraddress = address.toString();

                            String dn = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            Date datenowdriver = null;
                            try {
                                datenowdriver = sdf.parse(dn);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }

                            Date datecreatedriver = null;
                            try {
                                datecreatedriver = sdf.parse(dlexpiry);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }

                            if(datenowdriver.compareTo(datecreatedriver) >= 0)
                            {
                                txtExpiredriver.setVisibility(View.VISIBLE);
                            }
                            else{
                                txtExpiredriver.setVisibility(View.INVISIBLE);
                            }

                            Date datenowfranchise = null;
                            try {
                                datenowfranchise = sdf.parse(dn);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }

                            Date datecreatefranchise = null;
                            try {
                                datecreatefranchise = sdf.parse(franchiseexpiry);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }

                            if(datenowfranchise.compareTo(datecreatefranchise) >= 0)
                            {
                                txtExpirefranchise.setVisibility(View.VISIBLE);
                            }
                            else{
                                txtExpirefranchise.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    databaseError.toException();
                }
            });

            Rateus();
        }
    }

    private void Rateus(){
        FirebaseDatabase fbRate = FirebaseDatabase.getInstance();
        DatabaseReference dbRate = fbRate.getReference("rateus");
        dbRate.orderByChild("drivercode").startAt(drivercode.toString().trim()).endAt(drivercode.toString().trim() + "\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    int ctr = 0;

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                        final String passengercode = snapshot.child("passengercode").getValue().toString();
                        final String driverrate = snapshot.child("rate").getValue().toString();

                        if (passengercode.toString().equals(usercode.toString())){
                            txtRate.setText("Rated");
                            txtRate.setEnabled(false);
                            txtRate.setTextColor(getResources().getColor(R.color.colorSecondary));
                            txtRate.setBackgroundResource(R.drawable.design_round_border);
                        }

                        rating = rating + Double.parseDouble(driverrate);

                        ctr++;
                    }

                    txtRatings.setText(String.valueOf(formatter.format(rating / ctr)));
                    rating = 0.0;
                }
                else{
                    txtRatings.setText("No rate");

                    if (txtRide.getText().toString().equals("Stop")){
                        txtRate.setText("Rated");
                        txtRate.setEnabled(true);
                        txtRate.setTextColor(getResources().getColor(R.color.colorWhite));
                        txtRate.setBackgroundResource(R.drawable.design_round_highlight_text_gray);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException();
            }
        });
    }

    private void User(){
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

                    passengername = name.toString();
                    passengeraddress = address.toString();
                    if (contact.equals("Unknown")){
                        passengercontact = "No Phone Number";
                    } else{
                        contact_a = datauser.getString(3).toString().substring(0, 4);
                        contact_b = datauser.getString(3).toString().substring(4, 8);
                        contact_c = datauser.getString(3).toString().substring(8, 11);

                        passengercontact = contact_a + "  " + contact_b + "  "+ contact_c;
                    }

                } while (datauser.moveToNext());
            }
        }
        databasehelper.close();
    }

    private void Emergencynote(){
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.layout_dialog_note, viewGroup, false);
        EditText inputNote = dialogView.findViewById(R.id.inputNote);
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        if (note != null){
            inputNote.setText(note.toString().trim());
        }

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connection.isConnected()) {
                    if (inputNote.getText().toString().trim().isEmpty()){
                        Toast.makeText(Driverinfo.this, "Emergency note is empty", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        SharedPreferences preferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(Config.EMERGENCYNOTE_SHARED_PREF, inputNote.getText().toString().trim());
                        editor.commit();

                        note = inputNote.getText().toString().trim();

                        alertDialog.dismiss();
                        Toast.makeText(Driverinfo.this, "Emergency note has been set", Toast.LENGTH_SHORT).show();
                    }
                } else{
                    alertDialog.dismiss();
                    Toast.makeText(Driverinfo.this, "No internet connection", Toast.LENGTH_SHORT).show();
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

    private void Sendrequest(){
        if (connection.isConnected()){
            if (note == null){
                Toast.makeText(Driverinfo.this, "Set emergency note first", Toast.LENGTH_SHORT).show();
            }
            else {
                btnBell.setEnabled(false);
                btnBell.setVisibility(View.INVISIBLE);

                rand = new Random();
                b = rand.nextInt((999 - 100)) + 100;

                customrequest = new Customrequest();
                firebaseDatabase = firebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference().child("emergency-info");

                requestcode = new SimpleDateFormat("yyMMdd-HHmmss-" + b).format(Calendar.getInstance().getTime());

                customrequest.setCode(requestcode);
                customrequest.setUsercode(usercode);
                customrequest.setPassengername(passengername);
                customrequest.setPassengercontact(passengercontact);
                customrequest.setPassengeraddress(passengeraddress);
                customrequest.setNote(note);
                customrequest.setDateon(new SimpleDateFormat("yyyy-MM-dd  h:mm a", Locale.getDefault()).format(new Date()));
                customrequest.setStatus("0");

                databaseReference.child(customrequest.getCode()).setValue(customrequest);

                Toast.makeText(Driverinfo.this, "Sent successfully", Toast.LENGTH_SHORT).show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnBell.setEnabled(true);
                        btnBell.setVisibility(View.VISIBLE);
                    }
                }, 8000);
            }
        }
        else{
            Toast.makeText(Driverinfo.this, "No internet connection", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        customcontactadapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        customcontactadapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                super.finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
            case R.id.btnNote:
                Emergencynote();
                break;
            case R.id.btnBell:
                Sendrequest();
                break;
            case R.id.txtComment:
                Intent passengercomment = new Intent(Driverinfo.this, Passengercomment.class);
                passengercomment.putExtra("PASSENGERCODE", usercode.toString().trim());
                passengercomment.putExtra("DRIVERCODE", drivercode.toString().trim());
                startActivity(passengercomment);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.txtRate:
                Intent passengerrate = new Intent(Driverinfo.this, Passengerrate.class);
                passengerrate.putExtra("ID", usercode.toString().trim());
                passengerrate.putExtra("PASSENGERCODE", usercode.toString().trim());
                passengerrate.putExtra("DRIVERCODE", drivercode.toString().trim());
                startActivity(passengerrate);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}