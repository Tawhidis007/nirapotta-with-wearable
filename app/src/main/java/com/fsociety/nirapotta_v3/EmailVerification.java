package com.fsociety.nirapotta_v3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailVerification extends AppCompatActivity {

    Button resend, refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        checkVerification();
        setContentView(R.layout.activity_email_verification);

        resend = (Button) findViewById(R.id.resend);
        refresh = (Button) findViewById(R.id.refresh);

        sendVerificationEmail();

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resend.setEnabled(false);
                sendVerificationEmail();
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh.setEnabled(false);
                FirebaseAuth.getInstance().getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        boolean verified = FirebaseAuth.getInstance().getCurrentUser().isEmailVerified();

                        Log.d("radif123", "checkVerification: " + verified);

                        if (verified) {
                            SharedPreferences sp = getSharedPreferences(StartingActivity.my_preferernce, MODE_PRIVATE);
                            sp.edit().putBoolean("logged", true).apply();
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(EmailVerification.this, "Email not verified yet!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                refresh.setEnabled(true);
            }
        });
    }

//    private void checkVerification() {
//        boolean verified = FirebaseAuth.getInstance().getCurrentUser().reload().addOnCompleteListener(new View.);
//
//        Log.d("radif123", "checkVerification: "+verified);
//
//        if (verified) {
//            SharedPreferences sp=getSharedPreferences(StartingActivity.my_preferernce,MODE_PRIVATE);
//            sp.edit().putBoolean("logged",true).apply();
//            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
//            startActivity(intent);
//            finish();
//            return;
//        }
//    }

    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                resend.setEnabled(true);

                if (task.isSuccessful())
                    Toast.makeText(EmailVerification.this, "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(EmailVerification.this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
