package com.fsociety.nirapotta_v3.Subscription;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.widget.Button;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import dmax.dialog.SpotsDialog;

public class API {
    protected static final String phone = "21213";
    protected static final String appPhone1 = "21213_A6622";
    protected static final String appPhone2 = "21213_ni";
    private static final String message1 = "START nir";
    private static final String message2 = "nir";
    protected static Button go;
    protected static Intent nextIntent;
    protected static Intent nextIntent2;
    protected static Context context;
    protected static String subscriberId;
    private static final String queryBalanceURL = "http://www.nirapotta.xyz/caas/Query.php";
    private static final String debitURL = "http://www.nirapotta.xyz/caas/Debit.php";
    protected static boolean isSub = false;

    protected static void subscribe(boolean ni) {
        PendingIntent pi = PendingIntent.getActivity(context, 0, nextIntent, 0);
        SmsManager sms = SmsManager.getDefault();
        final android.app.AlertDialog waitingdialog = new SpotsDialog.Builder().setContext(context).build();
        waitingdialog.show();
        if (!ni) {
            sms.sendTextMessage(phone, null, message1, null, null);
        } else {
            waitingdialog.dismiss();
            sms.sendTextMessage(phone, null, message2, pi, null);

        }

    }
//    protected static void subscribed(){
//        PendingIntent pi = PendingIntent.getActivity(context, 0, nextIntent2, 0);
//
//    }

    protected static String queryBalance(final String subscriberId) {
        final String[] result = {""};

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream = null;

                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(queryBalanceURL);

                    String json = "";

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("subscriberId", subscriberId);

                    json = jsonObject.toString();
                    StringEntity se = new StringEntity(json);

                    httpPost.setEntity(se);
                    httpPost.setHeader("Content-type", "application/json");

                    HttpResponse httpResponse = httpclient.execute(httpPost);
                    inputStream = httpResponse.getEntity().getContent();

                    // 10. convert inputstream to string
                    if (inputStream != null)
                        result[0] = convertInputStreamToString(inputStream);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    JSONObject json = new JSONObject(result[0]);
                    if (json.getString("statusCode").equals("E1601")) result[0] = "1000.0";
                    else result[0] = json.getString("chargeableBalance");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();
        while (t.isAlive()) {
        }

        System.out.println("123456 " + result[0]);

        return result[0];
    }


//    protected static String queryBalance(final String subscriberId) {
//        final String[] result = {""};
//
//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                InputStream inputStream = null;
//
//                try {
//                    HttpClient httpclient = new DefaultHttpClient();
//                    HttpPost httpPost = new HttpPost(queryBalanceURL);
//
//                    String json = "";
//
//                    JSONObject jsonObject = new JSONObject();
//                    jsonObject.put("subscriberId", subscriberId);
//
//                    json = jsonObject.toString();
//                    StringEntity se = new StringEntity(json);
//
//                    httpPost.setEntity(se);
//                    httpPost.setHeader("Content-type", "application/json");
//
//                    HttpResponse httpResponse = httpclient.execute(httpPost);
//                    inputStream = httpResponse.getEntity().getContent();
//
//                    // 10. convert inputstream to string
//                    if (inputStream != null)
//                        result[0] = convertInputStreamToString(inputStream);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                try {
//                    JSONObject json = new JSONObject(result[0]);
//                    result[0] = json.getString("chargeableBalance");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        t.start();
//        while (t.isAlive()) {
//        }
//
//        System.out.println("123456 " + result[0]);
//
//        return result[0];
//    }

    protected static Boolean debitBalance(final String subscriberId, final String externalTrxId, final String amount) {
        final String[] result = {""};

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream = null;

                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(debitURL);

                    String json = "";

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("subscriberId", subscriberId);
                    jsonObject.put("externalTrxId", externalTrxId);
                    jsonObject.put("amount", amount);

                    json = jsonObject.toString();
                    StringEntity se = new StringEntity(json);

                    httpPost.setEntity(se);
                    httpPost.setHeader("Content-type", "application/json");

                    HttpResponse httpResponse = httpclient.execute(httpPost);
                    inputStream = httpResponse.getEntity().getContent();

                    // 10. convert inputstream to string
                    if (inputStream != null)
                        result[0] = convertInputStreamToString(inputStream);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    JSONObject json = new JSONObject(result[0]);
                    result[0] = json.getString("statusDetail");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();
        while (t.isAlive()) {
        }

        System.out.println("123456 " + result[0]);

        if (result[0].equals("Request was successfully processed")) return true;
        else return false;
    }

    protected static boolean checkAndRequestPermissions(Context context, Activity activity) {
        int sendsms = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS);
        int receivesms = ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS);
        int readsms = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS);
        int internet = ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET);

        if (sendsms != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.SEND_SMS},
                    0);
        }

        if (receivesms != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.RECEIVE_SMS},
                    1);
        }

        if (readsms != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_SMS},
                    2);
        }

        if (internet != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.INTERNET},
                    3);
        }

        return true;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }
}
