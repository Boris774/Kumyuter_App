package com.example.kumyuter;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Connection {
    Context context;

    public Connection(Context context){
        this.context = context;
    }

    public boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Service.CONNECTIVITY_SERVICE);
        if (cm != null){
            NetworkInfo network = cm.getActiveNetworkInfo();
            if (network != null){
                if (network.getState() == NetworkInfo.State.CONNECTED){
                    return true;
                }
            }
        }
        return false;
    }
}
