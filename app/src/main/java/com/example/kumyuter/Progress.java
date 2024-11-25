package com.example.kumyuter;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

public class Progress {

    private CardView cardLoading;
    private ConstraintLayout constLoading;
    private ProgressBar progressLoading;
    private TextView txtLoading;

    Animation fadeIn;

    Progress(Context ct, View view){
        fadeIn = AnimationUtils.loadAnimation(ct, R.anim.fade_in);
        cardLoading = view.findViewById(R.id.cardLoading);
        constLoading = view.findViewById(R.id.constLoading);
        progressLoading = view.findViewById(R.id.progressLoading);
        txtLoading = view.findViewById(R.id.txtLoading);
    }

    void buttonActivated(){
        progressLoading.setAnimation(fadeIn);
        progressLoading.setVisibility(View.VISIBLE);
        txtLoading.setAnimation(fadeIn);
        txtLoading.setText("Please wait...");
    }

    void buttonFinished(){
        progressLoading.setVisibility(View.GONE);
    }
}
