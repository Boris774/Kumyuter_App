package com.example.kumyuter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class Passengercomment extends AppCompatActivity implements View.OnClickListener{
    ImageButton btnBack, btnSend;
    EditText inputComment;
    RecyclerView recView;
    LinearLayout linMessage;
    ConstraintLayout constWait;

    Random rand;
    Connection cd;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private Customcomment customcomment;
    private ArrayList<Customcomment> list;
    private Customcommentadapter customcommentadapter;
    int a, b;
    String commentcode, usercode, drivercode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passengercomment);

        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnSend = (ImageButton) findViewById(R.id.btnSend);
        inputComment = (EditText) findViewById(R.id.inputComment);
        constWait = (ConstraintLayout)findViewById(R.id.constWait);
        linMessage = (LinearLayout)findViewById(R.id.linMessage);
        recView = (RecyclerView)findViewById(R.id.recView);
        recView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(true);
        recView.setLayoutManager(linearLayoutManager);

        btnBack.setOnClickListener(this);
        btnSend.setOnClickListener(this);

        Intent i = this.getIntent();
        usercode = i.getExtras().getString("PASSENGERCODE").toString();
        drivercode = i.getExtras().getString("DRIVERCODE").toString();

        constWait.setVisibility(View.VISIBLE);
        recView.setVisibility(View.GONE);

        cd = new Connection(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        inputComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (inputComment.getText().toString().trim().isEmpty()){
                    btnSend.setVisibility(View.GONE);
                } else{
                    btnSend.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Checkvalidate();

        Query query = FirebaseDatabase.getInstance().getReference().child("comments-info").orderByChild("drivercode").startAt(drivercode).endAt(drivercode + "\uf8ff");
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
                constWait.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        list = new ArrayList<>();
        customcommentadapter = new Customcommentadapter(this, list);
        recView.setAdapter(customcommentadapter);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Customcomment customcomment = dataSnapshot.getValue(Customcomment.class);
                    list.add(customcomment);
                }
                customcommentadapter.notifyDataSetChanged();
                recView.scrollToPosition(list.size() - 1);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException();
            }
        });
    }

    public void Checkvalidate(){
        if (inputComment.getText().toString().trim().length() < 1){
            btnSend.setVisibility(View.GONE);
        }
    }

    private  void Commentsave(){
        if (cd.isConnected()){
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            this.getWindow().getDecorView().clearFocus();

            btnSend.setVisibility(View.GONE);

            rand = new Random();
            a = rand.nextInt((99 - 10) + 1) + 10;
            b = rand.nextInt((999 - 100))+  100;

            commentcode = new SimpleDateFormat("yyyyMMdd-HHmmss-" + a + "-" + b).format(Calendar.getInstance().getTime());

            customcomment = new Customcomment();
            firebaseDatabase = firebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference().child("comments-info");

            customcomment.setCommentcode(commentcode);
            customcomment.setUsercode(usercode);
            customcomment.setDrivercode(drivercode);
            customcomment.setComment(inputComment.getText().toString().trim());
            customcomment.setDate(new SimpleDateFormat("yyyy/MM/dd h:mm a", Locale.getDefault()).format(new Date()));

            databaseReference.child(customcomment.getCommentcode()).setValue(customcomment).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(Passengercomment.this, "Sent", Toast.LENGTH_SHORT).show();
                        inputComment.setText("");
                    }
                }
            });
        }
        else{
            Toast.makeText(Passengercomment.this, "No internet connection", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        customcommentadapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        customcommentadapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSend:
                Commentsave();
                break;
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