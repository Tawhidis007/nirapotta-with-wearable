package com.fsociety.nirapotta_v3.Subscription;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.fsociety.nirapotta_v3.R;
import com.fsociety.nirapotta_v3.StartingActivity;

public class CheckSubscription extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_subscription);

        SharedPreferences sp = getSharedPreferences(StartingActivity.uid, MODE_PRIVATE);
        Operations.currentUid = sp.getString("uid", "");

        API.context = this;
        API.nextIntent = new Intent(this, GetSubscriptionId.class);
        Activity currentActivity = this;

        API.checkAndRequestPermissions(API.context, currentActivity);

        Button subscribe = (Button) findViewById(R.id.subscribe);

        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subscribe.setEnabled(false);
                API.subscribe(false);
            }
        });
    }

}
