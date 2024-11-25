package com.example.kumyuter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class History extends AppCompatActivity implements View.OnClickListener{

    ImageButton btnBack;
    RecyclerView recView;
    TextView txtTitle, progressText;
    LinearLayout linWait, linMessage;

    Connection cd;
    String code;

    private ArrayList<Customhistory> list;
    private Customhistoryadapter customhistoryadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        btnBack = (ImageButton) findViewById(R.id.btnBack);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        progressText = (TextView) findViewById(R.id.progressText);
        linWait = (LinearLayout) findViewById(R.id.linWait);
        linMessage = (LinearLayout) findViewById(R.id.linMessage);
        recView = (RecyclerView) findViewById(R.id.recView);
        recView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recView.setLayoutManager(linearLayoutManager);

        btnBack.setOnClickListener(this);

        linWait.setVisibility(View.VISIBLE);
        recView.setVisibility(View.GONE);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        code = sharedPreferences.getString(Config.CODE_SHARED_PREF, null);

        cd = new Connection(this);

        Query query = FirebaseDatabase.getInstance().getReference().child("history-info").orderByChild("usercode").startAt(code).endAt(code + "\uf8ff").limitToLast(100);
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
        customhistoryadapter = new Customhistoryadapter(this, list);
        recView.setAdapter(customhistoryadapter);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    final String historycode = dataSnapshot.child("usercode").getValue().toString();

                    if  (historycode.toString().trim().equals(code)){
                        Customhistory customhistory = dataSnapshot.getValue(Customhistory.class);
                        list.add(customhistory);
                    }
                }

                if (list.size() == 0){
                    linMessage.setVisibility(View.VISIBLE);
                    recView.setVisibility(View.GONE);
                }
                customhistoryadapter.notifyDataSetChanged();            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        customhistoryadapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        customhistoryadapter.notifyDataSetChanged();
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