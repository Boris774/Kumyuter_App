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

public class Profilename extends AppCompatActivity implements View.OnClickListener{

    ImageView btnBack;
    TextView txtName, txtLoading;
    EditText inputName;
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
        setContentView(R.layout.activity_profilename);

        btnBack = (ImageView)findViewById(R.id.btnBack);
        txtName = (TextView)findViewById(R.id.txtName);
        txtLoading = (TextView)findViewById(R.id.txtLoading);
        inputName = (EditText)findViewById(R.id.inputName);
        progressLoading = (View)findViewById(R.id.progressWait);
        constLoading = (ConstraintLayout) findViewById(R.id.constLoading);

        btnBack.setOnClickListener(this);

        progressLoading.setEnabled(false);
        constLoading.setBackgroundResource(R.drawable.design_round_border);
        txtLoading.setTextColor(getResources().getColor(R.color.colorPrimary));

        connection = new Connection(this);
        databasehelper = new Databasehelper(this);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        usercode = sharedPreferences.getString(Config.CODE_SHARED_PREF, null);

        inputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (inputName.getText().toString().trim().length() > 0){
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
                Profilename.this.getWindow().getDecorView().clearFocus();

                progress = new Progress(Profilename.this, view);
                progress.buttonActivated();
                progressLoading.setEnabled(false);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Userupdate();
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
                    String name = data.getString(2).toString();

                    txtName.setText(name);
                } while (data.moveToNext());
            }
        }
        databasehelper.close();
    }

    private  void Userupdate(){
        final String name = inputName.getText().toString().trim().toLowerCase();
        if (name.isEmpty()) {
            progressLoading.setEnabled(false);
            progress.buttonFinished();
            constLoading.setBackgroundResource(R.drawable.design_round_border);
            txtLoading.setTextColor(getResources().getColor(R.color.colorPrimary));
            txtLoading.setText("Save");
            inputName.setText("");
            Toast.makeText(Profilename.this, "You have entered an empty name", Toast.LENGTH_SHORT).show();
        }
        else if (txtName.getText().toString().trim().toLowerCase().equals(name)) {
            progressLoading.setEnabled(false);
            progress.buttonFinished();
            constLoading.setBackgroundResource(R.drawable.design_round_border);
            txtLoading.setTextColor(getResources().getColor(R.color.colorPrimary));
            txtLoading.setText("Save");
            inputName.setText("");
            Toast.makeText(Profilename.this, "That name is already being used", Toast.LENGTH_SHORT).show();
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
                                databaseReference.child(usercode).child("name").setValue(Capitalize(name));

                                txtLoading.setText("Save");
                                progress.buttonFinished();

                                Toast.makeText(Profilename.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                                txtName.setText(Capitalize(name));
                                inputName.setText("");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Profilename.this, "Timeout server", Toast.LENGTH_SHORT).show();
                        databaseError.toException();
                    }
                });
            } else{
                progressLoading.setEnabled(false);
                progress.buttonFinished();
                constLoading.setBackgroundResource(R.drawable.design_round_border);
                txtLoading.setTextColor(getResources().getColor(R.color.colorPrimary));
                txtLoading.setText("Save");
                inputName.setText("");
                Toast.makeText(Profilename.this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
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