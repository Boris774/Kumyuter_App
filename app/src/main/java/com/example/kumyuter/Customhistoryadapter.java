package com.example.kumyuter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Customhistoryadapter extends RecyclerView.Adapter<Customhistoryadapter.myviewholder> {

    ArrayList<Customhistory> mlist;
    Context context;
    Connection connection;
    String code;

    public Customhistoryadapter(Context context, ArrayList<Customhistory> mlist){
        this.mlist = mlist;
        this.context = context;
    }

    @NonNull
    @Override
    public Customhistoryadapter.myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(context).inflate(R.layout.layout_history, parent ,false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Customhistoryadapter.myviewholder holder, final int position) {
        final Customhistory model = mlist.get(position);

        SharedPreferences sharedPreferences = context.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        code = sharedPreferences.getString(Config.CODE_SHARED_PREF, null);

        connection = new Connection(context);

        holder.txtID.setText(model.getCode());
        holder.txtCode.setText(model.getUsercode());
        holder.txtName.setText(model.getDrivername());
        holder.txtContact.setText(model.getDrivercontact());
        holder.txtType.setText("Driver");
        holder.txtAddress.setText(model.getDriveraddress());
        holder.txtDlcode.setText(model.getDlcode());
        holder.txtDlexpiry.setText(model.getDlexpiry());
        holder.txtMotornumber.setText(model.getMotorplate());
        holder.txtFranchiseexpiry.setText(model.getFranchiseexpriy());
        holder.txtDate.setText(model.getDateon());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent driver = new Intent(context, Driverinfo.class);
                driver.putExtra("DRIVERCODE", model.getDrivercode().toString());
                driver.putExtra("DRIVERSET", 2);
                context.startActivity(driver);
                ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
//                final View bottomSheetview = LayoutInflater.from(context.getApplicationContext())
//                        .inflate(
//                                R.layout.layout_patient_bottom,
//                                (LinearLayout)view.findViewById(R.id.dialogBottom)
//                        );
//
//                final TextView dialogid = bottomSheetview.findViewById(R.id.dialogID);
//                final TextView dialogcode = bottomSheetview.findViewById(R.id.dialogCode);
//                final TextView dialogcondition = bottomSheetview.findViewById(R.id.dialogCondition);
//                final TextView dialogtype = bottomSheetview.findViewById(R.id.dialogType);
//                final TextView dialogEmergency = bottomSheetview.findViewById(R.id.dialogEmergency);
//                final TextView dialogLocation = bottomSheetview.findViewById(R.id.dialogLocation);
//                final TextView dialogContact = bottomSheetview.findViewById(R.id.dialogContact);
//                final LinearLayout dialogbtnView = bottomSheetview.findViewById(R.id.dialogbtnView);
//                final LinearLayout dialogbtnMedicine = bottomSheetview.findViewById(R.id.dialogbtnMedicine);
//                final LinearLayout dialogbtnSchedule = bottomSheetview.findViewById(R.id.dialogbtnSchedule);
//                final LinearLayout dialogbtnCall = bottomSheetview.findViewById(R.id.dialogbtnCall);
//                final LinearLayout dialogNature = bottomSheetview.findViewById(R.id.dialogNature);
//                final Button dialogbtnApprove = bottomSheetview.findViewById(R.id.dialogbtnApprove);
//                final Button dialogbtnComplete = bottomSheetview.findViewById(R.id.dialogbtnComplete);
//
//                dialogid.setText(model.getId());
//                dialogcode.setText(model.getCode());
//                dialogcondition.setText(model.getCondition());
//                dialogtype.setText(model.getType());
//
//                if (holder.txtContact.getText().toString().trim().equals("Unknown")) {
//                    dialogContact.setText("No phone number");
//                }
//                else{
//                    dialogContact.setText(holder.txtContact.getText().toString().trim());
//                }
//
//                if (model.getType().toString().trim().equals("On Call")){
//                    dialogEmergency.setText(model.getNatureemergency());
//                    dialogLocation.setText(model.getLocation());
//                    dialogEmergency.setVisibility(View.VISIBLE);
//                    dialogLocation.setVisibility(View.VISIBLE);
//                    dialogNature.setVisibility(View.VISIBLE);
//                }
//                else{
//                    dialogEmergency.setText(model.getNatureemergency());
//                    dialogLocation.setText(model.getLocation());
//                    dialogEmergency.setVisibility(View.GONE);
//                    dialogLocation.setVisibility(View.GONE);
//                    dialogNature.setVisibility(View.GONE);
//                }
//
//                if  (model.getStatus().toString().trim().equals("Pending")){
//                    dialogbtnApprove.setVisibility(View.VISIBLE);
//                    dialogbtnComplete.setVisibility(View.GONE);
//                    dialogbtnView.setVisibility(View.GONE);
//                    dialogbtnMedicine.setVisibility(View.GONE);
//                    dialogbtnSchedule.setVisibility(View.GONE);
//                    dialogbtnCall.setVisibility(View.GONE);
//                } else{
//                    dialogbtnApprove.setVisibility(View.GONE);
//                    dialogbtnComplete.setVisibility(View.VISIBLE);
//                    dialogbtnView.setVisibility(View.VISIBLE);
//                    dialogbtnMedicine.setVisibility(View.VISIBLE);
//                    dialogbtnSchedule.setVisibility(View.VISIBLE);
//                    dialogbtnCall.setVisibility(View.VISIBLE);
//                }
//
//                dialogbtnApprove.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        ViewGroup viewGroup = view.findViewById(android.R.id.content);
//                        View dialogView = LayoutInflater.from(context).inflate(R.layout.layout_dialog_pending, viewGroup, false);
//                        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);
//                        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
//
//                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                        builder.setView(dialogView);
//                        final AlertDialog alertDialog = builder.create();
//                        alertDialog.setCancelable(false);
//                        alertDialog.show();
//
//                        btnConfirm.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                if (connection.isConnected()) {
//                                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//                                    DatabaseReference databaseReference = firebaseDatabase.getReference().child("patients-info");
//                                    databaseReference.child(dialogid.getText().toString()).child("nurseid").setValue(code);
//                                    databaseReference.child(dialogid.getText().toString()).child("status").setValue("Approved");
//
//                                    alertDialog.dismiss();
//                                    bottomSheetDialog.dismiss();
//                                } else{
//                                    alertDialog.dismiss();
//                                    Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//
//                        btnCancel.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                alertDialog.dismiss();
//                            }
//                        });
//                    }
//                });
//
//                dialogbtnComplete.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        ViewGroup viewGroup = view.findViewById(android.R.id.content);
//                        View dialogView = LayoutInflater.from(context).inflate(R.layout.layout_dialog_complete, viewGroup, false);
//                        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);
//                        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
//
//                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                        builder.setView(dialogView);
//                        final AlertDialog alertDialog = builder.create();
//                        alertDialog.setCancelable(false);
//                        alertDialog.show();
//
//                        btnConfirm.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                if (connection.isConnected()) {
//                                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//                                    DatabaseReference databaseReference = firebaseDatabase.getReference().child("patients-info");
//                                    databaseReference.child(dialogid.getText().toString()).child("status").setValue("Completed");
//
//                                    alertDialog.dismiss();
//                                    bottomSheetDialog.dismiss();
//                                } else{
//                                    alertDialog.dismiss();
//                                    Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//
//                        btnCancel.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                alertDialog.dismiss();
//                            }
//                        });
//                    }
//                });
//
//                dialogbtnView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent viewpatient = new Intent(context, Patientview.class);
//                        viewpatient.putExtra("REQUESTID", dialogid.getText().toString());
//                        viewpatient.putExtra("USERCODE", dialogcode.getText().toString());
//                        context.startActivity(viewpatient);
//                        bottomSheetDialog.dismiss();
//                    }
//                });
//
//                dialogbtnMedicine.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent schedule = new Intent(context, Patientmedicine.class);
//                        schedule.putExtra("REQUESTID", dialogid.getText().toString());
//                        schedule.putExtra("USERCODE", dialogcode.getText().toString());
//                        context.startActivity(schedule);
//                        bottomSheetDialog.dismiss();
//                    }
//                });
//
//                dialogbtnSchedule.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent schedule = new Intent(context, Patientschedule.class);
//                        schedule.putExtra("REQUESTID", dialogid.getText().toString());
//                        schedule.putExtra("USERCODE", dialogcode.getText().toString());
//                        context.startActivity(schedule);
//                        bottomSheetDialog.dismiss();
//                    }
//                });
//
//                dialogbtnCall.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if (!holder.txtContact.getText().toString().trim().equals("No phone number")){
//                            Intent intent = new Intent(Intent.ACTION_DIAL);
//                            intent.setData(Uri.parse("tel:" + holder.txtContact.getText().toString()));
//                            context.startActivity(intent);
//                            bottomSheetDialog.dismiss();
//                        }
//                    }
//                });
//
//                bottomSheetDialog.setContentView(bottomSheetview);
//                bottomSheetDialog.show();
//            }
//        });
//
//        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//        DatabaseReference databaseReference = firebaseDatabase.getReference("users-info");
//
//        Query queryUser = databaseReference.orderByChild("code").startAt(model.getCode()).endAt(model.getCode() + "\uf8ff");
//        queryUser.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()){
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                        holder.txtName.setText(snapshot.child("name").getValue().toString());
//
//                        if (snapshot.child("role").getValue().toString().equals("Student")){
//                            holder.txtRole.setText(snapshot.child("role").getValue().toString());
//                            holder.txtRole.setText(snapshot.child("role").getValue().toString() + "  ●  " +snapshot.child("course").getValue().toString() + " " + snapshot.child("section").getValue().toString());
//                        } else{
//                            holder.txtRole.setText(snapshot.child("role").getValue().toString());
//                        }
//
//                        if (snapshot.child("contact").getValue().toString().equals("Unknown")){
//                            holder.txtContact.setText("No phone number");
//                        } else{
//                            holder.txtContact.setText(snapshot.child("contact").getValue().toString());
//                        }
//
//                        if (!snapshot.child("picture").getValue().toString().equals("Unknown")){
//                            Picasso.get()
//                                    .load(snapshot.child("picture").getValue().toString())
//                                    .placeholder(R.drawable.profile_loader)
//                                    .error(R.drawable.profile_error)
//                                    .into(holder.imgPicture);
//                        } else{
//                            Picasso.get()
//                                    .load(R.drawable.profile_image)
//                                    .placeholder(R.drawable.profile_loader)
//                                    .error(R.drawable.profile_error)
//                                    .into(holder.imgPicture);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                databaseError.toException();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public static class myviewholder extends RecyclerView.ViewHolder{
        CircleImageView imgPicture;
        TextView txtID, txtCode, txtName, txtContact, txtType, txtAddress, txtDlcode, txtDlexpiry, txtMotornumber, txtFranchiseexpiry, txtDate;
        CardView cardView;

        public myviewholder(@NonNull View itemView) {
            super(itemView);

            imgPicture = (CircleImageView) itemView.findViewById(R.id.imgPicture);
            txtID = (TextView) itemView.findViewById(R.id.txtID);
            txtCode = (TextView) itemView.findViewById(R.id.txtCode);
            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtContact = (TextView) itemView.findViewById(R.id.txtContact);
            txtType = (TextView) itemView.findViewById(R.id.txtType);
            txtAddress = (TextView) itemView.findViewById(R.id.txtAddress);
            txtDlcode = (TextView) itemView.findViewById(R.id.txtDlcode);
            txtDlexpiry = (TextView) itemView.findViewById(R.id.txtDlexpiry);
            txtMotornumber = (TextView) itemView.findViewById(R.id.txtMotornumber);
            txtFranchiseexpiry = (TextView) itemView.findViewById(R.id.txtFranchiseexpiry);
            txtDate = (TextView) itemView.findViewById(R.id.txtDate);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
        }
    }
}