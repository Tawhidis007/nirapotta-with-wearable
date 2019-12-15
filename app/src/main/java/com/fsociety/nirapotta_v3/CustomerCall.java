package com.fsociety.nirapotta_v3;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fsociety.nirapotta_v3.Common.Common;
import com.fsociety.nirapotta_v3.Remote.IGoogleAPI;

import de.hdodenhof.circleimageview.CircleImageView;


public class CustomerCall extends AppCompatActivity {

    TextView txtTime, txtDistance, txtAddress;
    MediaPlayer mediaPlayer;
    IGoogleAPI mService2;
    Button send_me;
    double lat;
    double lon;
    boolean med, harrass, emergency = false;
    TextView notification_tv;
    String updates1;
    int updates2;
    // CircleImageView iv;
    ImageView iv;
    String passable;
    int photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_customer_call);

        Bundle mBundle = getIntent().getExtras();

        med = Boolean.parseBoolean((String) mBundle.get("med"));
        harrass = Boolean.parseBoolean((String) mBundle.get("harrass"));
        emergency = Boolean.parseBoolean((String) mBundle.get("emergency"));


        mService2 = Common.getGoogleAPI();

        notification_tv = findViewById(R.id.notification_tv);
        iv = findViewById(R.id.notification_image);
        send_me = findViewById(R.id.send_me);


        //init views
//        txtAddress = findViewById(R.id.txtAddress);
//        txtDistance = findViewById(R.id.txtDistance);
//        txtTime = findViewById(R.id.txtTime);
        send_me = findViewById(R.id.send_me);

        mediaPlayer = MediaPlayer.create(this, R.raw.bomb);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        Log.d("check", "onCreate: " + Boolean.parseBoolean((String) mBundle.get("emergency")) + Boolean.parseBoolean((String) mBundle.get("harrass")) + Boolean.parseBoolean((String) mBundle.get("med")));


        if (med) {
            passable = "Medical Urgency!";
            photo = R.drawable.med;
        } else if (harrass) {
            passable = "Sexual Harrassment Urgency!";
            photo = R.drawable.sexual;
        } else if (emergency) {
            passable = "IMMEDIATE Urgency!";
            photo = R.drawable.emergency;
        }


        if (getIntent() != null) {

            lat = Double.parseDouble((String) mBundle.get("title"));
            lon = Double.parseDouble((String) mBundle.get("detail"));


            Log.d("check", "" + lat + " " + lon);
            //   getDirection(); // direction api use kora lage kori nai problems may arise
            // updates1 = getIntent().getStringExtra("text");


            notification_tv.setText(passable);
            iv.setImageResource(photo);


            send_me.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CustomerCall.this, VictimLocation.class);
                    intent.putExtra("lat", lat);
                    intent.putExtra("lng", lon);
                    startActivity(intent);
                }
            });
        }


    }

    @Override
    protected void onStop() {
        mediaPlayer.release();
        super.onStop();
    }

    @Override
    protected void onPause() {
        mediaPlayer.release();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
