package com.example.kumyuter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private EditText inputUser, inputPass;
    private TextView txtRegister, txtShow, txtForgot, txtNote, txtLoading;
    private ConstraintLayout constLoading;
    private View progressLoading;
    private DatabaseReference databaseReference;
    private Progress progress;
    private static long backpressed;
    private boolean status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputUser = findViewById(R.id.inputUser);
        inputPass = findViewById(R.id.inputPass);
        txtRegister = findViewById(R.id.txtRegister);
        txtShow = findViewById(R.id.txtShow);
        txtForgot = findViewById(R.id.txtForgot);
        txtNote = findViewById(R.id.txtNote);
        txtLoading = findViewById(R.id.txtLoading);
        progressLoading = findViewById(R.id.progressWait);
        constLoading = findViewById(R.id.constLoading);

        txtRegister.setOnClickListener(this);
        txtShow.setOnClickListener(this);
        txtForgot.setOnClickListener(this);

        // Set password field to be hidden by default
        inputPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        progressLoading.setEnabled(false);
        constLoading.setBackgroundResource(R.drawable.design_round_border);
        txtLoading.setTextColor(getResources().getColor(R.color.colorPrimary));

        Connection cd = new Connection(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        inputUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkValidate();
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        inputPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                txtShow.setVisibility(inputPass.getText().length() > 0 ? View.VISIBLE : View.GONE);
                checkValidate();
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        progressLoading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                hideKeyboard();
                progress = new Progress(Login.this, view);
                progress.buttonActivated();
                progressLoading.setEnabled(false);
                txtShow.setVisibility(View.GONE);

                new Handler().postDelayed(() -> login(), 2000);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        status = sharedPreferences.getBoolean(Config.LOG_SHARED_PREF, false);

        if (status) {
            startActivity(new Intent(Login.this, Home.class));
            finish();
        }
    }

    private void checkValidate() {
        String usercode = inputUser.getText().toString().trim();
        String password = inputPass.getText().toString().trim();

        if (usercode.isEmpty() && password.isEmpty()) {
            setButtonInactive();
        } else if (!usercode.isEmpty() && password.isEmpty()) {
            setButtonInactive();
        } else if (usercode.isEmpty() && !password.isEmpty()) {
            setButtonInactive();
        } else {
            setButtonActive();
        }
    }

    private void setButtonInactive() {
        progressLoading.setEnabled(false);
        constLoading.setBackgroundResource(R.drawable.design_round_border);
        txtLoading.setTextColor(getResources().getColor(R.color.colorPrimary));
        txtLoading.setText("L O G I N");
    }

    private void setButtonActive() {
        progressLoading.setEnabled(true);
        constLoading.setBackgroundResource(R.drawable.design_round_highlight_button);
        txtLoading.setTextColor(getResources().getColor(R.color.colorWhite));
        txtLoading.setText("L O G I N");
    }

    private void login() {
        final String user = inputUser.getText().toString().trim().toLowerCase();
        final String pass = inputPass.getText().toString().trim().toLowerCase();

        if (user.isEmpty() || pass.isEmpty()) {
            handleInvalidLogin();
        } else if (new Connection(this).isConnected()) {
            authenticateUser(user, pass);
        } else {
            handleNoConnection();
        }
    }

    private void authenticateUser(final String user, final String pass) {
        progressLoading.setEnabled(false);

        databaseReference.child("users-info").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean found = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String usercode = snapshot.child("user").getValue(String.class);
                    String password = snapshot.child("pass").getValue(String.class);

                    if (user.equals(usercode.toLowerCase()) && pass.equals(password.toLowerCase())) {
                        configureUserSettings(snapshot);
                        showLoginSuccess(snapshot.child("name").getValue(String.class));
                        found = true;
                        break;
                    }
                }
                if (!found) handleInvalidLogin();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Login.this, "Timeout server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configureUserSettings(DataSnapshot snapshot) {
        SharedPreferences sharedPreferences = Login.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Config.CODE_SHARED_PREF, snapshot.getKey());
        editor.putString(Config.ROLE_SHARED_PREF, snapshot.child("role").getValue(String.class));
        editor.putString(Config.PROFILE_SHARED_PREF, snapshot.child("picture").getValue(String.class));
        editor.putBoolean(Config.LOG_SHARED_PREF, true);
        editor.apply();
    }

    private void showLoginSuccess(String name) {
        txtLoading.setText("L O G I N");
        progress.buttonFinished();
        Toast.makeText(Login.this, "Logged in as " + name, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> {
            startActivity(new Intent(Login.this, Home.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
            inputUser.setText("");
            inputPass.setText("");
        }, 1000);
    }

    private void handleInvalidLogin() {
        progressLoading.setEnabled(false);
        progress.buttonFinished();
        setButtonInactive();
        Toast.makeText(Login.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
    }

    private void handleNoConnection() {
        progressLoading.setEnabled(false);
        progress.buttonFinished();
        setButtonInactive();
        Toast.makeText(Login.this, "No internet connection", Toast.LENGTH_LONG).show();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        getWindow().getDecorView().clearFocus();
    }

    private void showPassword() {
        if (txtShow.getText().toString().trim().equals("Show")) {
            txtShow.setText("Hide");
            inputPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            txtShow.setText("Show");
            inputPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        inputPass.setSelection(inputPass.length()); // Keeps the cursor at the end
    }

    private void register() {
        startActivity(new Intent(Login.this, Register.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private void forgotPassword() {
        startActivity(new Intent(Login.this, Forgotpassword.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtRegister:
                register();
                break;
            case R.id.txtShow:
                showPassword();
                break;
            case R.id.txtForgot:
                forgotPassword();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (backpressed + 2000 > System.currentTimeMillis()) {
            moveTaskToBack(true);
            super.finish();
        } else {
            Toast.makeText(this, "Press once again to exit", Toast.LENGTH_SHORT).show();
            backpressed = System.currentTimeMillis();
        }
    }
}
