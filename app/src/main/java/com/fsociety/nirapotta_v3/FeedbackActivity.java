package com.fsociety.nirapotta_v3;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class FeedbackActivity extends AppCompatActivity implements View.OnClickListener {


    Button feedback_btn;
    EditText name, msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);


        Toolbar toolbar = findViewById(R.id.toolbar_feedback);
        setSupportActionBar(toolbar);

        ActionBar bb = getSupportActionBar();
//        bb.setDisplayHomeAsUpEnabled(true);


        feedback_btn = findViewById(R.id.feedback_btn);
        name = findViewById(R.id.name_id);
        msg = findViewById(R.id.msg_id);

        feedback_btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.feedback_btn) {

            String user_name = name.getText().toString();
            String user_msg = msg.getText().toString();
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/email");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{"team.fsociety007@gmail.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, "Feedback from Nirapotta");
            i.putExtra(Intent.EXTRA_TEXT, user_name);
            i.putExtra(Intent.EXTRA_TEXT, user_msg);
            startActivity(Intent.createChooser(i, "Feedback with "));
        }
    }
}
