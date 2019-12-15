package com.fsociety.nirapotta_v3.Subscription;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.fsociety.nirapotta_v3.HomeActivity;
import com.fsociety.nirapotta_v3.R;
import com.fsociety.nirapotta_v3.StartingActivity;

import dmax.dialog.SpotsDialog;

public class GetSubscriptionId extends AppCompatActivity {

    public static android.app.AlertDialog waitingdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_subscription_id);
        waitingdialog = new SpotsDialog.Builder().setContext(GetSubscriptionId.this).build();
        waitingdialog.show();
        API.nextIntent2 = new Intent(GetSubscriptionId.this, BuyPackage.class);
        API.go = (Button) findViewById(R.id.go);
        API.go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String subscriberId = API.subscriberId;
                //Store subscriberId in firebase
                SharedPreferences sp = getSharedPreferences(StartingActivity.uid, MODE_PRIVATE);
                Operations.currentUid = sp.getString("uid", "");
                Operations.subscribeUser(subscriberId);
                sp = getSharedPreferences(StartingActivity.my_preferernce, MODE_PRIVATE);
                sp.edit().putBoolean("logged", true).apply();
                Intent intent = new Intent(getApplicationContext(), BuyPackage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                ActivityCompat.finishAffinity(GetSubscriptionId.this);

            }
        });

    }

    @Override
    public void onBackPressed() {

    }
}
