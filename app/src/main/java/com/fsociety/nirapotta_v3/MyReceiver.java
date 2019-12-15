package com.fsociety.nirapotta_v3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Objects;

public class MyReceiver extends BroadcastReceiver {
    private static final int MIN_DELAY = 1500;
    private int powerBtnPress = 0;
    private double lastClick = -1;


    @Override
    public void onReceive(Context context, Intent intent) {

        switch (Objects.requireNonNull(intent.getAction())) {
            case Intent.ACTION_SCREEN_ON:
                if (lastClick == -1)
                    lastClick = System.currentTimeMillis();
                else {
                    double interval = System.currentTimeMillis() - lastClick;
                    if (interval < MIN_DELAY) {
                        powerBtnPress++;
                        lastClick = System.currentTimeMillis();
                    } else {
                        powerBtnPress = 0;
                        lastClick = -1;
                    }
                }
                break;

        }

        if (powerBtnPress > 1) {
            powerBtnPress = 0;
            lastClick = -1;
            Toast.makeText(context, "SERVICE STARTED ", Toast.LENGTH_LONG).show();
            Log.d("tag", "onReceive: service started");
            Intent i = new Intent(context, UpdateService.class);
            context.startService(i);
        }
    }
}


