package com.fsociety.nirapotta_v3.Subscription;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {
    private static SmsListener mListener;
    public Pattern p = Pattern.compile("(|^)\\d{6}");

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();
        Object[] pdus = (Object[]) data.get("pdus");

        for (int i = 0; i < pdus.length; i++) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            String sender = smsMessage.getDisplayOriginatingAddress();
            String phoneNumber = smsMessage.getDisplayOriginatingAddress();
            String senderNum = phoneNumber;
            String messageBody = smsMessage.getMessageBody();

            try {
                if (senderNum.equals(API.appPhone2)) {
                    API.subscriberId = messageBody;
                    System.out.println("PRANTIK " + API.subscriberId);
                    GetSubscriptionId.waitingdialog.dismiss();
                    API.go.setEnabled(true);
                } else if (senderNum.equals(API.appPhone1) && !API.isSub) {
                    API.isSub = true;
                    API.subscribe(true);
                }
            } catch (Exception e) {
            }
        }
    }

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }
}