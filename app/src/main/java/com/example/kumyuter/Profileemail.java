package com.example.kumyuter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profileemail extends AppCompatActivity implements View.OnClickListener{

    ImageView btnBack;
    TextView txtEmail, txtLoading;
    EditText inputEmail;
    ConstraintLayout constLoading;
    View progressLoading;

    String usercode;
    Connection connection;
    Databasehelper databasehelper;
    DatabaseReference databaseReference;
    private Progress progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileemail);

        btnBack = (ImageView)findViewById(R.id.btnBack);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtLoading = (TextView)findViewById(R.id.txtLoading);
        inputEmail = (EditText)findViewById(R.id.inputEmail);
        progressLoading = (View)findViewById(R.id.progressWait);
        constLoading = (ConstraintLayout) findViewById(R.id.constLoading);

        btnBack.setOnClickListener(this);

        progressLoading.setEnabled(false);
        constLoading.setBackgroundResource(R.drawable.design_round_border);
        txtLoading.setTextColor(getResources().getColor(R.color.colorPrimary));

        inputEmail.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

        connection = new Connection(this);
        databasehelper = new Databasehelper(this);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        usercode = sharedPreferences.getString(Config.CODE_SHARED_PREF, null);

        inputEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (inputEmail.getText().toString().trim().length() > 0){
                    progressLoading.setEnabled(true);
                    constLoading.setBackgroundResource(R.drawable.design_round_highlight_button);
                    txtLoading.setTextColor(getResources().getColor(R.color.colorWhite));
                    txtLoading.setText("Save");
                }
                else{
                    progressLoading.setEnabled(false);
                    constLoading.setBackgroundResource(R.drawable.design_round_border);
                    txtLoading.setTextColor(getResources().getColor(R.color.colorPrimary));
                    txtLoading.setText("Save");
                }
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
                Profileemail.this.getWindow().getDecorView().clearFocus();

                progress = new Progress(Profileemail.this, view);
                progress.buttonActivated();
                progressLoading.setEnabled(false);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Usernameupdate();
                    }
                }, 2000);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Userdisplay();
    }

    private void Userdisplay(){
        Cursor data = databasehelper.userview(usercode);

        if (data.getCount() > 0) {
            if (data.moveToFirst()) {
                do {
                    String email = data.getString(5).toString();

                    txtEmail.setText(email);
                } while (data.moveToNext());
            }
        }
        databasehelper.close();
    }

    private  void Usernameupdate(){
        final String email = inputEmail.getText().toString().trim().toLowerCase();

        if (email.isEmpty()) {
            progressLoading.setEnabled(false);
            progress.buttonFinished();
            constLoading.setBackgroundResource(R.drawable.design_round_border);
            txtLoading.setTextColor(getResources().getColor(R.color.colorPrimary));
            txtLoading.setText("Save");
            inputEmail.setText("");
            Toast.makeText(Profileemail.this, "You have entered an empty email", Toast.LENGTH_SHORT).show();
        }
        else if (txtEmail.getText().toString().trim().toLowerCase().equals(email)) {
            progressLoading.setEnabled(false);
            progress.buttonFinished();
            constLoading.setBackgroundResource(R.drawable.design_round_border);
            txtLoading.setTextColor(getResources().getColor(R.color.colorPrimary));
            txtLoading.setText("Save");
            inputEmail.setText("");
            Toast.makeText(Profileemail.this, "That email is already being used", Toast.LENGTH_SHORT).show();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString().trim().toLowerCase()).matches()) {
            progressLoading.setEnabled(false);
            progress.buttonFinished();
            constLoading.setBackgroundResource(R.drawable.design_round_border);
            txtLoading.setTextColor(getResources().getColor(R.color.colorPrimary));
            txtLoading.setText("Save");
            inputEmail.setText("");
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
        }
        else{
            if (connection.isConnected()){
                progressLoading.setEnabled(false);

                databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("users-info").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            String id = snapshot.getKey();

                            if (usercode.equals(id)){
                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                DatabaseReference databaseReference = firebaseDatabase.getReference().child("users-info");
                                databaseReference.child(usercode).child("email").setValue(email);

                                txtLoading.setText("Save");
                                progress.buttonFinished();

                                Toast.makeText(Profileemail.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                                txtEmail.setText(email);
                                inputEmail.setText("");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Profileemail.this, "Timeout server", Toast.LENGTH_SHORT).show();
                        databaseError.toException();
                    }
                });
            } else{
                progressLoading.setEnabled(false);
                progress.buttonFinished();
                constLoading.setBackgroundResource(R.drawable.design_round_border);
                txtLoading.setTextColor(getResources().getColor(R.color.colorPrimary));
                txtLoading.setText("Save");
                inputEmail.setText("");
                Toast.makeText(Profileemail.this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                super.finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}