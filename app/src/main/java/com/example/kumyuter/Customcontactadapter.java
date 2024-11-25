package com.example.kumyuter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Customcontactadapter extends RecyclerView.Adapter<Customcontactadapter.myviewholder> {

    ArrayList<Customcontact> mlist;
    Context context;
    Connection connection;
    String code;

    public Customcontactadapter(Context context, ArrayList<Customcontact> mlist){
        this.mlist = mlist;
        this.context = context;
    }

    @NonNull
    @Override
    public Customcontactadapter.myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(context).inflate(R.layout.layout_contact, parent ,false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Customcontactadapter.myviewholder holder, final int position) {
        final Customcontact model = mlist.get(position);

        SharedPreferences sharedPreferences = context.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        code = sharedPreferences.getString(Config.CODE_SHARED_PREF, null);

        connection = new Connection(context);

        holder.txtName.setText(model.getEmergencyname());
        holder.txtContact.setText(model.getContact());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+holder.txtContact.getText().toString()));
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public static class myviewholder extends RecyclerView.ViewHolder{
        TextView txtName, txtContact;
        CardView cardView;

        public myviewholder(@NonNull View itemView) {
            super(itemView);

            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtContact = (TextView) itemView.findViewById(R.id.txtContact);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
        }
    }
}