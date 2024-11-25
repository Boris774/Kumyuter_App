package com.example.kumyuter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class Forgotpassword extends AppCompatActivity implements View.OnClickListener {

    ImageButton btnBack;
    EditText inputEmail;
    TextView txtSave;
    ConstraintLayout constSave;
    View progressLoading;

    Random rand;
    Connection cd;
    DatabaseReference databaseReference;
    private Progress progress;
    int generate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        btnBack = (ImageButton) findViewById(R.id.btnBack);
        inputEmail = (EditText)findViewById(R.id.inputEmail);
        txtSave = (TextView)findViewById(R.id.txtSave);
        progressLoading = (View)findViewById(R.id.progressWait);
        constSave = (ConstraintLayout) findViewById(R.id.constSave);

        btnBack.setOnClickListener(this);

        progressLoading.setEnabled(false);
        constSave.setBackgroundResource(R.drawable.design_round_border);
        txtSave.setTextColor(getResources().getColor(R.color.colorPrimary));

        cd = new Connection(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        inputEmail.addTextChangedListener(new TextWatcher() {
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

        progressLoading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(inputEmail.getWindowToken(), 0);

                progress = new Progress(Forgotpassword.this, view);
                progress.buttonActivated();
                progressLoading.setEnabled(false);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Submitthruemail();
                    }
                }, 2000);
            }
        });
    }

    private void  Checkvalidate(){
        final String email = inputEmail.getText().toString().trim();

        if (email.equals("")){
            progressLoading.setEnabled(false);
            constSave.setBackgroundResource(R.drawable.design_round_border);
            txtSave.setTextColor(getResources().getColor(R.color.colorPrimary));
            txtSave.setText("S U B M I T");
        }
        else{
            progressLoading.setEnabled(true);
            constSave.setBackgroundResource(R.drawable.design_round_highlight_button);
            txtSave.setTextColor(getResources().getColor(R.color.colorWhite));
            txtSave.setText("S U B M I T");
        }
    }
    private void Submitthruemail() {
        final String email = inputEmail.getText().toString().trim().toLowerCase();

        if (email.isEmpty()){
            progressLoading.setEnabled(false);
            progress.buttonFinished();
            constSave.setBackgroundResource(R.drawable.design_round_border);
            txtSave.setTextColor(getResources().getColor(R.color.colorPrimary));
            txtSave.setText("S U B M I T");
            inputEmail.setText("");
            Toast.makeText(Forgotpassword.this, "You have entered an empty email address", Toast.LENGTH_SHORT).show();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            progressLoading.setEnabled(false);
            progress.buttonFinished();
            constSave.setBackgroundResource(R.drawable.design_round_border);
            txtSave.setTextColor(getResources().getColor(R.color.colorPrimary));
            txtSave.setText("S U B M I T");
            inputEmail.setText("");
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
        }
        else{
            if (cd.isConnected()){
                progressLoading.setEnabled(false);

                databaseReference.child("users-info").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int counter = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            String code = snapshot.getKey();
                            //String queryusercode = snapshot.child("studentcode").getValue(String.class);
                            String queryuseremail = snapshot.child("email").getValue(String.class);

                            if (email.toLowerCase().equals(queryuseremail)){

                                rand = new Random();
                                generate = rand.nextInt((9999 - 1000) + 1000);

                                final String email_sender = "email mo didi @gmail.com";
                                final String email_pass = "password san email";
                                final String email_subject = "Temporarily Password";
                                String email_message = "This is generated to the temporarily password for your account. Your password is: "+generate+"\n\nThank you!";
//                                SendMail mail = new SendMail(email_sender, email_pass,
//                                        email,
//                                        email_subject,
//                                        email_message);
//                                mail.execute();

                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                DatabaseReference databaseReference = firebaseDatabase.getReference().child("users-info");
                                databaseReference.child(code).child("pass").setValue(String.valueOf(generate));
                                Toast.makeText(Forgotpassword.this, "Email sent", Toast.LENGTH_LONG).show();

                                txtSave.setText("S U B M I T");
                                progress.buttonFinished();
                                inputEmail.setText("");

                                counter = counter + 1;
                                break;
                            }
                        }

                        if (counter == 0){
                            progressLoading.setEnabled(false);
                            progress.buttonFinished();
                            constSave.setBackgroundResource(R.drawable.design_round_border);
                            txtSave.setTextColor(getResources().getColor(R.color.colorPrimary));
                            txtSave.setText("S U B M I T");
                            inputEmail.setText("");
                            Toast.makeText(Forgotpassword.this, "Incorrect email", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Forgotpassword.this, "Timeout server", Toast.LENGTH_SHORT).show();
                        databaseError.toException();
                    }
                } );

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            else {
                progressLoading.setEnabled(false);
                progress.buttonFinished();
                constSave.setBackgroundResource(R.drawable.design_round_border);
                txtSave.setTextColor(getResources().getColor(R.color.colorPrimary));
                txtSave.setText("S U B M I T");
                inputEmail.setText("");
                Toast.makeText(Forgotpassword.this, "No internet connection", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnBack) {
            super.finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    @Override
    public void onBackPressed() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
