package com.example.kumyuter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class Register extends AppCompatActivity implements View.OnClickListener{

    EditText inputName, inputContact, inputUser, inputPass;
    TextView txtLogin, txtShow, txtLoading;
    ConstraintLayout constLoading;
    View progressLoading;

    Random rand;
    Connection cd;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private Customuser customuser;
    private Progress progress;
    String code;
    int a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputName = (EditText)findViewById(R.id.inputName);
        inputContact = (EditText)findViewById(R.id.inputContact);
        inputUser = (EditText)findViewById(R.id.inputUser);
        inputPass = (EditText)findViewById(R.id.inputPass);
        txtLogin = (TextView)findViewById(R.id.txtLogin);
        txtShow = (TextView)findViewById(R.id.txtShow);
        txtLoading = (TextView)findViewById(R.id.txtLoading);
        progressLoading = (View)findViewById(R.id.progressWait);
        constLoading = (ConstraintLayout)findViewById(R.id.constLoading);

        txtLogin.setOnClickListener(this);
        txtShow.setOnClickListener(this);
        txtShow.setVisibility(View.GONE);
        inputPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

        progressLoading.setEnabled(false);
        constLoading.setBackgroundResource(R.drawable.design_round_border);
        txtLoading.setTextColor(getResources().getColor(R.color.colorPrimary));

        cd = new Connection(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        inputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Checkvalidate();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputContact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Checkvalidate();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Checkvalidate();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (inputPass.getText().length() > 0){
                    txtShow.setVisibility(View.VISIBLE);
                }
                else{
                    txtShow.setVisibility(View.GONE);
                    inputPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    inputPass.setSelection(inputPass.length());
                }
                Checkvalidate();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        progressLoading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                Register.this.getWindow().getDecorView().clearFocus();

                progress = new Progress(Register.this, view);
                progress.buttonActivated();
                progressLoading.setEnabled(false);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Register();
                    }
                }, 2000);
            }
        });
    }

    private void  Checkvalidate(){
        String fullname = inputName.getText().toString().trim();
        String contact = inputContact.getText().toString().trim();
        String username = inputUser.getText().toString().trim();
        String password = inputPass.getText().toString().trim();

        if (fullname.equals("")){
            progressLoading.setEnabled(false);
            constLoading.setBackgroundResource(R.drawable.design_round_border);
            txtLoading.setTextColor(getResources().getColor(R.color.colorPrimary));
            txtLoading.setText("R E G I S T E R");
        }
        else if (contact.equals("")){
            progressLoading.setEnabled(false);
            constLoading.setBackgroundResource(R.drawable.design_round_border);
            txtLoading.setTextColor(getResources().getColor(R.color.colorPrimary));
            txtLoading.setText("R E G I S T E R");
        }
        else if (contact.length() < 11){
            progressLoading.setEnabled(false);
            constLoading.setBackgroundResource(R.drawable.design_round_border);
            txtLoading.setTextColor(getResources().getColor(R.color.colorPrimary));
            txtLoading.setText("R E G I S T E R");
        }
        else if (username.equals("")){
            progressLoading.setEnabled(false);
            constLoading.setBackgroundResource(R.drawable.design_round_border);
            txtLoading.setTextColor(getResources().getColor(R.color.colorPrimary));
            txtLoading.setText("R E G I S T E R");
        }
        else if (password.equals("")){
            progressLoading.setEnabled(false);
            constLoading.setBackgroundResource(R.drawable.design_round_border);
            txtLoading.setTextColor(getResources().getColor(R.color.colorPrimary));
            txtLoading.setText("R E G I S T E R");
        }
        else{
            progressLoading.setEnabled(true);
            constLoading.setBackgroundResource(R.drawable.design_round_highlight_button);
            txtLoading.setTextColor(getResources().getColor(R.color.colorWhite));
            txtLoading.setText("R E G I S T E R");
        }
    }

    private  void Register(){
        if  (inputName.getText().toString().trim().isEmpty() || inputContact.getText().toString().trim().isEmpty() || inputUser.getText().toString().trim().isEmpty() || inputPass.getText().toString().trim().isEmpty()){
            progressLoading.setEnabled(false);
            progress.buttonFinished();
            constLoading.setBackgroundResource(R.drawable.design_round_border);
            txtLoading.setTextColor(getResources().getColor(R.color.colorPrimary));
            txtLoading.setText("R E G I S T E R");
            inputName.setText("");
            inputContact.setText("");
            inputUser.setText("");
            inputPass.setText("");
            Toast.makeText(this, "You have entered an empty details", Toast.LENGTH_SHORT).show();
        }
        else{
            if (cd.isConnected()){
                progressLoading.setEnabled(false);

                rand = new Random();
                a = rand.nextInt((999 - 100))+  100;

                databaseReference.child("users-info").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        int checkuser = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            String username = snapshot.child("user").getValue(String.class);
                            String password = snapshot.child("pass").getValue(String.class);

                            if (inputUser.getText().toString().trim().toLowerCase().equals(username) && inputPass.getText().toString().trim().toLowerCase().equals(password)){
                                progress.buttonFinished();
                                constLoading.setBackgroundResource(R.drawable.design_round_border);
                                txtLoading.setTextColor(getResources().getColor(R.color.colorPrimary));
                                txtLoading.setText("R E G I S T E R");
                                checkuser = checkuser + 1;
                                break;
                            }
                        }

                        if (checkuser > 0){
                            Toast.makeText(Register.this, "User account is already taken", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            customuser = new Customuser();
                            firebaseDatabase = firebaseDatabase.getInstance();
                            databaseReference = firebaseDatabase.getReference().child("users-info");

                            code = new SimpleDateFormat("yyMMdd-HHmmss-" + a).format(Calendar.getInstance().getTime());

                            customuser.setCode(code);
                            customuser.setName(Capitalize(inputName.getText().toString().trim().toLowerCase()));
                            customuser.setContact(inputContact.getText().toString().trim());
                            customuser.setAddress("Unknown");
                            customuser.setEmail("Unknown");
                            customuser.setUser(inputUser.getText().toString().trim().toLowerCase());
                            customuser.setPass(inputPass.getText().toString().trim().toLowerCase());
                            customuser.setRole("Passenger");
                            customuser.setPicture("Unknown");
                            customuser.setStatus("ACTIVE");

                            databaseReference.child(customuser.getCode()).setValue(customuser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Configuration(code, "Passenger");

                                        txtLoading.setText("R E G I S T E R");
                                        progress.buttonFinished();
                                        Toast.makeText(Register.this, "Registered successfully", Toast.LENGTH_SHORT).show();

                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent home = new Intent(Register.this, Home.class);
                                                startActivity(home);
                                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                finish();
                                                inputName.setText("");
                                                inputContact.setText("");
                                                inputUser.setText("");
                                                inputPass.setText("");
                                            }
                                        }, 1000);
                                    }
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(Register.this, "Timeout server", Toast.LENGTH_SHORT).show();
                        databaseError.toException();
                    }
                });
            }
            else{
                progressLoading.setEnabled(false);
                progress.buttonFinished();
                constLoading.setBackgroundResource(R.drawable.design_round_border);
                txtLoading.setTextColor(getResources().getColor(R.color.colorPrimary));
                txtLoading.setText("R E G I S T E R");
                inputName.setText("");
                inputContact.setText("");
                inputUser.setText("");
                inputPass.setText("");
                Toast.makeText(Register.this, "No internet connection", Toast.LENGTH_LONG).show();
            }
        }
    }

    private  void Login(){
        Intent login = new Intent(Register.this, Login.class);
        startActivity(login);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.finish();
    }

    private void Showpassword(){
        if (txtShow.getText().toString().trim().equals("Show")){
            txtShow.setText("Hide");
            inputPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            inputPass.setSelection(inputPass.length());
        }
        else{
            txtShow.setText("Show");
            inputPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            inputPass.setSelection(inputPass.length());
        }
    }

    private String Capitalize(String t){
        String result = "";
        t = t.replaceAll("()([A-Z])", "$1 $2");
        String[] persons = t.split(" ");
        for (String person : persons){
            if (person.length() > 0){
                result += Character.toUpperCase(person.charAt(0)) + person.substring(1) + " ";
            }
        }
        return result.toString().trim();
    }

    private void Configuration(String code, String role){
        SharedPreferences sharedPreferences = Register.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(Config.CODE_SHARED_PREF, code);
        editor.putString(Config.ROLE_SHARED_PREF, role);
        editor.putString(Config.PROFILE_SHARED_PREF, "Unknown");
        editor.putBoolean(Config.LOG_SHARED_PREF, true);
        editor.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtLogin:
                Login();
                break;
            case R.id.txtShow:
                Showpassword();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent login = new Intent(Register.this, Login.class);
        startActivity(login);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.finish();
    }
}