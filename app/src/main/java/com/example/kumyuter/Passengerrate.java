package com.example.kumyuter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class Passengerrate extends AppCompatActivity implements View.OnClickListener{

    CircleImageView imgPicture;
    ImageButton btnBack;
    EditText inputComment;
    TextView txtName, txtMessage;
    RatingBar ratingStars;
    Button btnRate;

    float myrating = 0;
    String id, passengercode, drivercode;
    Connection connection;
    Random random;
    int a;
    private Customrate customrate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passengerrate);

        btnBack = (ImageButton) findViewById(R.id.btnBack);
        imgPicture = (CircleImageView) findViewById(R.id.imgPicture);
        inputComment = (EditText) findViewById(R.id.inputComment);
        txtName = (TextView) findViewById(R.id.txtName);
        txtMessage = (TextView) findViewById(R.id.txtMessage);
        ratingStars = (RatingBar) findViewById(R.id.ratingBar);
        btnRate = (Button) findViewById(R.id.btnRate);

        btnBack.setOnClickListener(this);
        btnRate.setOnClickListener(this);

        btnRate.setEnabled(false);
        btnRate.setTextColor(getResources().getColor(R.color.colorGray));
        btnRate.setBackgroundResource(R.drawable.design_round_border);

        connection = new Connection(this);

        id = getIntent().getExtras().getString("ID");
        passengercode = getIntent().getExtras().getString("PASSENGERCODE");
        drivercode = getIntent().getExtras().getString("DRIVERCODE");

        FirebaseDatabase fbUser = FirebaseDatabase.getInstance();
        DatabaseReference dbUser = fbUser.getReference("drivers-info");
        dbUser.orderByChild("code").startAt(drivercode).endAt(drivercode + "\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        final String dcode = snapshot.child("code").getValue().toString();
                        final String dname = snapshot.child("first").getValue().toString();
                        final String dpic = snapshot.child("picture").getValue().toString();

                        if (dcode.toString().trim().equals(drivercode)){
                            txtName.setText(dname.toString().trim());

                            if (!dpic.equals("Unknown")){
                                Picasso.get()
                                        .load(dpic)
                                        .placeholder(R.drawable.profile_image)
                                        .error(R.drawable.profile_error)
                                        .into(imgPicture);
                            } else{
                                Picasso.get()
                                        .load(R.drawable.profile_image)
                                        .placeholder(R.drawable.profile_image)
                                        .error(R.drawable.profile_error)
                                        .into(imgPicture);
                            }
                            break;
                        }
                    }
                }
                else{
                    txtName.setText("No Driver Name");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseError.toException();
            }
        });

        ratingStars.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean fromUser) {

                int rating = (int) v;

                String message = null;

                myrating = ratingBar.getRating();

                switch (rating){
                    case 1:
                        message = "Sorry to hear that! :(";
                        break;
                    case 2:
                        message = "You always accept suggestions!";
                        break;
                    case 3:
                        message = "Good enough!";
                        break;
                    case 4:
                        message = "Great! Thank you!";
                        break;
                    case 5:
                        message = "Awesome! You are the best!";
                        break;
                }

                txtMessage.setText(message);

                if  (myrating > 0){
                    btnRate.setEnabled(true);
                    btnRate.setTextColor(getResources().getColor(R.color.colorWhite));
                    btnRate.setBackgroundResource(R.drawable.design_pressed_button);

                }
                else{
                    btnRate.setEnabled(false);
                    btnRate.setTextColor(getResources().getColor(R.color.colorGray));
                    btnRate.setBackgroundResource(R.drawable.design_round_border);
                }
            }
        });
    }

    public void Saverate(){
        if (txtName.getText().toString().trim().equals("No Driver Name")){
            btnRate.setEnabled(false);
            btnRate.setTextColor(getResources().getColor(R.color.colorGray));
            btnRate.setBackgroundResource(R.drawable.design_round_border);
            Toast.makeText(this, "Invalid rate because there is no driver", Toast.LENGTH_SHORT).show();
        }
        else{
            if (connection.isConnected()){
                btnRate.setEnabled(false);
                btnRate.setTextColor(getResources().getColor(R.color.colorGray));
                btnRate.setBackgroundResource(R.drawable.design_round_border);

                random = new Random();
                a = random.nextInt((999 - 100))+  100;
                String ratemessage;

                if (inputComment.getText().toString().trim().isEmpty()){
                    ratemessage = "";
                }
                else{
                    ratemessage = inputComment.getText().toString().trim();
                }

                customrate = new Customrate();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("rateus");
                customrate.setId(new SimpleDateFormat("yyMMdd-HHmmss").format(Calendar.getInstance().getTime()) + "-" + a);
                customrate.setPassengercode(passengercode);
                customrate.setDrivercode(drivercode);
                customrate.setRate(String.valueOf(myrating));
                customrate.setMessage(ratemessage);
                customrate.setDatecreated(new SimpleDateFormat("yyyy/MM/dd h:mm a", Locale.getDefault()).format(new Date()));

                databaseReference.child(customrate.getId()).setValue(customrate).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
//                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//                            DatabaseReference databaseReference = firebaseDatabase.getReference().child("bookride");
//                            databaseReference.child(id).child("status").setValue("Rated");

                            Toast.makeText(Passengerrate.this, "Successfully rated", Toast.LENGTH_SHORT).show();
                            Passengerrate.super.finish();
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        }
                    }
                });
            }
            else{
                btnRate.setEnabled(false);
                btnRate.setTextColor(getResources().getColor(R.color.colorGray));
                btnRate.setBackgroundResource(R.drawable.design_round_border);
                Toast.makeText(this, "Slow or no internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRate:
                Saverate();
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