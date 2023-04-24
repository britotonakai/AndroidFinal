package com.example.homescreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("Send position")){
            int position = intent.getIntExtra("position", 0);
            Log.d("position", "" + position);
        }
    }
}
