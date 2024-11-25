package com.example.kumyuter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Customcommentadapter extends RecyclerView.Adapter<Customcommentadapter.myviewholder> {

    ArrayList<Customcomment> mlist;
    Context context;
    String usercode;

    SimpleDateFormat dateOnline = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    SimpleDateFormat dateOffline = new SimpleDateFormat("MMM dd   hh:mm aa");
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.getDefault());

    public Customcommentadapter(Context context , ArrayList<Customcomment> mlist){
        this.mlist = mlist;
        this.context = context;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(context).inflate(R.layout.layout_commentsmodel , parent ,false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final myviewholder holder, int position) {
        final Customcomment model = mlist.get(position);

        SharedPreferences sharedPreferences = context.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        usercode = sharedPreferences.getString(Config.CODE_SHARED_PREF, null);

        holder.txtCode.setText(model.getCommentcode());
        holder.txtComment.setText(model.getComment());

        Date date = null;
        try {
            date = dateFormat.parse(model.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        Calendar pastDate = Calendar.getInstance(Locale.getDefault());
        pastDate.setTimeInMillis(calendar.getTimeInMillis());
        String past = dateOnline.format(pastDate.getTime());

        Calendar nowDate = Calendar.getInstance(Locale.getDefault());
        String now = dateOnline.format(nowDate.getTime());

        Date datePast = null;
        Date dateNow = null;
        try {
            datePast = dateOnline.parse(past);
            dateNow = dateOnline.parse(now);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long diff = dateNow.getTime() - datePast.getTime();
        long seconds = diff / 1000;
        long minutes = diff / (60 * 1000) % 60;
        long hours = diff / (60 * 60 * 1000) % 24;
        long days = diff / (24 * 60 * 60 * 1000);

        if (days > 0){
            if (days == 1){
                holder.txtDate.setText(days + " day ago");
            } else if (days == 2){
                holder.txtDate.setText(days + " days ago");
            } else if (days == 3){
                holder.txtDate.setText(days + " days ago");
            } else{
                holder.txtDate.setText(dateOffline.format(pastDate.getTime()));
            }
        } else{
            if (hours > 0){
                if (hours == 1){
                    holder.txtDate.setText(hours + " hour ago");
                } else{
                    holder.txtDate.setText(hours + " hours ago");
                }
            } else{
                if (minutes > 0){
                    if (minutes == 1){
                        holder.txtDate.setText(minutes + " minute ago");
                    } else{
                        holder.txtDate.setText(minutes + " minutes ago");
                    }
                } else{
                    if (seconds > 0){
                        holder.txtDate.setText("A few seconds ago");
                    }
                }
            }
        }

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
        dr.child("users-info").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String code = snapshot.child("code").getValue(String.class);
                    String name = snapshot.child("name").getValue(String.class);
                    String profile = snapshot.child("picture").getValue(String.class);

                    if (code.toString().trim().equals(model.getUsercode())){
                        holder.txtName.setText(name);
                        Picasso.get()
                                .load(profile)
                                .fit()
                                .centerCrop()
                                .placeholder(R.drawable.profile_loader)
                                .error(R.drawable.profile_error)
                                .into(holder.imgPicture);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Timeout server", Toast.LENGTH_SHORT).show();
                databaseError.toException();
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (usercode.toString().equals(model.getUsercode())){
                    ViewGroup viewGroup = view.findViewById(android.R.id.content);
                    View dialogView = LayoutInflater.from(context).inflate(R.layout.layout_dialog_delete, viewGroup, false);
                    Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);
                    Button btnCancel = dialogView.findViewById(R.id.btnCancel);

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setView(dialogView);
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();

                    btnConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DatabaseReference databaseReference =  FirebaseDatabase.getInstance().getReference("comments-info").child(holder.txtCode.getText().toString());
                            databaseReference.removeValue();

                            Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }
                    });

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });
                }

                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public static class myviewholder extends RecyclerView.ViewHolder{
        CircleImageView imgPicture;
        TextView txtCode, txtName, txtComment, txtDate;
        CardView cardView;

        public myviewholder(@NonNull View itemView) {
            super(itemView);

            imgPicture = (CircleImageView)itemView.findViewById(R.id.imgPicture);
            txtCode = (TextView)itemView.findViewById(R.id.txtCode);
            txtName = (TextView)itemView.findViewById(R.id.txtName);
            txtComment = (TextView)itemView.findViewById(R.id.txtComment);
            txtDate = (TextView)itemView.findViewById(R.id.txtDate);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
        }
    }
}