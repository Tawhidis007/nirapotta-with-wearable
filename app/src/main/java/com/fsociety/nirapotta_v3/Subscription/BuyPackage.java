package com.fsociety.nirapotta_v3.Subscription;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.fsociety.nirapotta_v3.HomeActivity;
import com.fsociety.nirapotta_v3.R;
import com.fsociety.nirapotta_v3.StartingActivity;

public class BuyPackage extends AppCompatActivity {
    private String subscriberId = "";

    LinearLayout buy_linear;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_package);

        Button package1 = (Button) findViewById(R.id.package1);
        Button package2 = (Button) findViewById(R.id.package2);
        Button package3 = (Button) findViewById(R.id.package3);
        buy_linear = findViewById(R.id.buy_linear);

        toolbar = findViewById(R.id.toolbar_buy);
        setSupportActionBar(toolbar);
//        ActionBar bb = getSupportActionBar();
//        bb.setDisplayHomeAsUpEnabled(true);


        subscriberId = "";
        final String externalTrxId = "123";
        Operations.getSubscriberID(new MyCallback() {
            @Override
            public void onCallback(String value) {
            }

            @Override
            public void onCallbackId(String value) {
                subscriberId = value;
            }
        });

        package3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                package2.setEnabled(false);
                package1.setEnabled(false);
                package3.setEnabled(false);
                Snackbar.make(buy_linear, "Please wait a moment..", Snackbar.LENGTH_SHORT).show();
                if (Double.parseDouble(API.queryBalance(subscriberId)) > 1.0) {
                    if (API.debitBalance(subscriberId, externalTrxId, "1.0")) {
                        Operations.retrieveBalance(new MyCallback() {
                            @Override
                            public void onCallback(String value) {
                                Operations.balance = "" + Integer.parseInt(value);
                                int newBalance = Integer.parseInt(Operations.balance) + 1;
                                Operations.updateBalance("" + newBalance);
                                Toast.makeText(getApplicationContext(), "1 request bought. Use responsibly!", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(BuyPackage.this, HomeActivity.class);
                                startActivity(i);

                            }

                            @Override
                            public void onCallbackId(String value) {

                            }
                        });

                    } else {
                        Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Not enough balance", Toast.LENGTH_SHORT).show();
                }
                package3.setEnabled(true);
                package2.setEnabled(true);
                package1.setEnabled(true);
            }
        });

        package1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                package2.setEnabled(false);
                package1.setEnabled(false);
                package3.setEnabled(false);
                Snackbar.make(buy_linear, "Please wait a moment..", Snackbar.LENGTH_SHORT).show();
                if (Double.parseDouble(API.queryBalance(subscriberId)) > 8.0) {
                    if (API.debitBalance(subscriberId, externalTrxId, "8.0")) {
                        //User bought 10 credits successfully. Store it in Firebase
                        Operations.retrieveBalance(new MyCallback() {
                            @Override
                            public void onCallback(String value) {
                                Operations.balance = "" + Integer.parseInt(value);
                                int newBalance = Integer.parseInt(Operations.balance) + 10;
                                Operations.updateBalance("" + newBalance);
                                Toast.makeText(getApplicationContext(), "10 requests bought. Use responsibly!", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(BuyPackage.this, HomeActivity.class);
                                startActivity(i);
                            }

                            @Override
                            public void onCallbackId(String value) {

                            }
                        });

                    } else {
                        //Something went wrong. Payment unsuccessful
                        Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //User does not have sufficient balance
                    Toast.makeText(getApplicationContext(), "Not enough balance", Toast.LENGTH_SHORT).show();
                }
                package3.setEnabled(true);
                package2.setEnabled(true);
                package1.setEnabled(true);
            }
        });

        package2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                package2.setEnabled(false);
                package1.setEnabled(false);
                package3.setEnabled(false);
                Snackbar.make(buy_linear, "Please wait a moment..", Snackbar.LENGTH_SHORT).show();

                if (Double.parseDouble(API.queryBalance(subscriberId)) > 15.0) {
                    if (API.debitBalance(subscriberId, externalTrxId, "15.0")) {
                        //User bought 10 credits successfully. Store it in Firebase
                        Operations.retrieveBalance(new MyCallback() {
                            @Override
                            public void onCallback(String value) {
                                Operations.balance = "" + Integer.parseInt(value);
                                int newBalance = Integer.parseInt(Operations.balance) + 20;
                                Operations.updateBalance("" + newBalance);
                                Toast.makeText(getApplicationContext(), "20 requests bought. Use responsibly!", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(BuyPackage.this, HomeActivity.class);
                                startActivity(i);
                            }

                            @Override
                            public void onCallbackId(String value) {

                            }
                        });


                    } else {
                        //Something went wrong. Payment unsuccessful
                        Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //User does not have sufficient balance
                    Toast.makeText(getApplicationContext(), "Not enough balance", Toast.LENGTH_SHORT).show();
                }
                package3.setEnabled(true);
                package2.setEnabled(true);
                package1.setEnabled(true);
            }
        });


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(BuyPackage.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}
