package com.example.kumyuter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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

public class Profilepassword extends AppCompatActivity implements View.OnClickListener{

    ImageView btnBack;
    EditText inputNew, inputConfirm;
    TextView txtLoading;
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
        setContentView(R.layout.activity_profilepassword);

        btnBack = (ImageView)findViewById(R.id.btnBack);
        inputNew = (EditText)findViewById(R.id.inputNew);
        inputConfirm = (EditText)findViewById(R.id.inputConfirm);
        txtLoading = (TextView)findViewById(R.id.txtLoading);
        progressLoading = (View)findViewById(R.id.progressWait);
        constLoading = (ConstraintLayout) findViewById(R.id.constLoading);

        btnBack.setOnClickListener(this);

        progressLoading.setEnabled(false);
        constLoading.setBackgroundResource(R.drawable.design_round_border);
        txtLoading.setTextColor(getResources().getColor(R.color.colorPrimary));

        inputNew.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        inputConfirm.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

        connection = new Connection(this);
        databasehelper = new Databasehelper(this);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        usercode = sharedPreferences.getString(Config.CODE_SHARED_PREF, null);

        inputNew.addTextChangedListener(new TextWatcher() {
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

        inputConfirm.addTextChangedListener(new TextWatcher() {
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
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                Profilepassword.this.getWindow().getDecorView().clearFocus();

                progress = new Progress(Profilepassword.this, view);
                progress.buttonActivated();
                progressLoading.setEnabled(false);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Passwordupdate();
                    }
                }, 2000);
            }
        });
    }

    private void Checkvalidate(){
        String password = inputNew.getText().toString().trim();
        String confirm = inputConfirm.getText().toString().trim();

        if (password.equals("") && confirm.equals("")){
            progressLoading.setEnabled(false);
            constLoading.setBackgroundResource(R.drawable.design_round_border);
            txtLoading.setTextColor(getResources().getColor(R.color.colorPrimary));
            txtLoading.setText("Save");
        }
        else if (!password.equals("") && confirm.equals("")){
            progressLoading.setEnabled(false);
            constLoading.setBackgroundResource(R.drawable.design_round_border);
            txtLoading.setTextColor(getResources().getColor(R.color.colorPrimary));
            txtLoading.setText("Save");
        }
        else if (password.equals("") && !confirm.equals("")){
            progressLoading.setEnabled(false);
            constLoading.setBackgroundResource(R.drawable.design_round_border);
            txtLoading.setTextColor(getResources().getColor(R.color.colorPrimary));
            txtLoading.setText("Save");
        }
        else{
            progressLoading.setEnabled(true);
            constLoading.setBackgroundResource(R.drawable.design_round_highlight_button);
            txtLoading.setTextColor(getResources().getColor(R.color.colorWhite));
            txtLoading.setText("Save");
        }
    }

    private  void Passwordupdate(){
        final String password = inputNew.getText().toString().trim().toLowerCase();
        final String confirm = inputConfirm.getText().toString().trim().toLowerCase();

        if (password.isEmpty() || confirm.isEmpty()) {
            progressLoading.setEnabled(false);
            progress.buttonFinished();
            constLoading.setBackgroundResource(R.drawable.design_round_border);
            txtLoading.setTextColor(getResources().getColor(R.color.colorPrimary));
            txtLoading.setText("Save");
            inputNew.setText("");
            inputConfirm.setText("");
            Toast.makeText(Profilepassword.this, "You have entered an empty password", Toast.LENGTH_SHORT).show();
        }
        else{
            if (connection.isConnected()){
                if (password.equals(confirm)){
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
                                    databaseReference.child(usercode).child("pass").setValue(password);

                                    txtLoading.setText("Save");
                                    progress.buttonFinished();

                                    Toast.makeText(Profilepassword.this, "Changed successfully", Toast.LENGTH_SHORT).show();
                                    inputNew.setText("");
                                    inputConfirm.setText("");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(Profilepassword.this, "Timeout server", Toast.LENGTH_SHORT).show();
                            databaseError.toException();
                        }
                    });
                }
                else{
                    progressLoading.setEnabled(false);
                    progress.buttonFinished();
                    constLoading.setBackgroundResource(R.drawable.design_round_border);
                    txtLoading.setTextColor(getResources().getColor(R.color.colorPrimary));
                    txtLoading.setText("Save");
                    inputNew.setText("");
                    inputConfirm.setText("");
                    Toast.makeText(Profilepassword.this, "Password did not matched", Toast.LENGTH_SHORT).show();
                }
            } else{
                progressLoading.setEnabled(false);
                progress.buttonFinished();
                constLoading.setBackgroundResource(R.drawable.design_round_border);
                txtLoading.setTextColor(getResources().getColor(R.color.colorPrimary));
                txtLoading.setText("Save");
                inputNew.setText("");
                inputConfirm.setText("");
                Toast.makeText(Profilepassword.this, "No internet connection", Toast.LENGTH_SHORT).show();
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