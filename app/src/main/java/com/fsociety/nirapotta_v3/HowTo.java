package com.fsociety.nirapotta_v3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HowTo extends AppCompatActivity {

    Button webview_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to);

        webview_btn = findViewById(R.id.webview_btn);
        webview_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(HowTo.this, VideoDekhbo.class);
                startActivity(i);

            }
        });

    }
}
