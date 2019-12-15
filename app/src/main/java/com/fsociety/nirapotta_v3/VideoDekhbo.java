package com.fsociety.nirapotta_v3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class VideoDekhbo extends AppCompatActivity {
    WebView wb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_dekhbo);

        wb = findViewById(R.id.web_id);

        WebSettings ws = wb.getSettings();
        ws.setJavaScriptEnabled(true);

        wb.setWebViewClient(new WebViewClient());
        wb.loadUrl("https://youtu.be/tJIH5RjQWJc");

        wb.saveWebArchive("youtuu");


    }
}
