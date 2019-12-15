package com.fsociety.nirapotta_v3.Subscription;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.fsociety.nirapotta_v3.HomeActivity;
import com.fsociety.nirapotta_v3.StartingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.CountDownLatch;

public class Operations {
    public static String currentUid = "";
    public static String subId = "";
    public static String balance = "";

    public static void subscribeUser(String subscriberId) {
        if (currentUid.equals("")) {
            try {
                currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            } catch (NullPointerException np) {
            }
        }
        FirebaseDatabase.getInstance().getReference("Users").child(currentUid).child("subscriberID").setValue(subscriberId);

    }

//    public  static String getSubscriberID(){
//
//        FirebaseDatabase.getInstance().getReference("Users").child(Operations.currentUid).child("subscriberID").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
////                                        subCount[0] = String.valueOf(dataSnapshot.getValue());
//                String subCount=String.valueOf(dataSnapshot.getValue());
//                Log.d("radif", "onDataChange: "+subCount);
//                Operations.subId=subCount;
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        return subId;
//    }

    public static void updateBalance(String amount) {
        FirebaseDatabase.getInstance().getReference("Users").child(currentUid).child("subscription").setValue(amount);
    }

    //    public static void retrieveBalance() throws InterruptedException {
//        Log.d("radif", "retrieveBalance: ");
//        CountDownLatch downLatch=new CountDownLatch(1);
//        FirebaseDatabase.getInstance().getReference("Users").child(currentUid).child("subscription").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
////                                        subCount[0] = String.valueOf(dataSnapshot.getValue());
//                balance=String.valueOf(dataSnapshot.getValue());
//                Log.d("radif", "retrieveBalance: "+balance);
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//    }
    public static void retrieveBalance(MyCallback myCallback) {
        Log.d("radif", "retrieveBalance: ");
        FirebaseDatabase.getInstance().getReference("Users").child(currentUid).child("subscription").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("radif", "onDataChange: retrieving balance...");
                String value = dataSnapshot.getValue(String.class);
                myCallback.onCallback(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void getSubscriberID(MyCallback myCallback) {
        FirebaseDatabase.getInstance().getReference("Users").child(Operations.currentUid).child("subscriberID").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("radif", "onDataChange: getting subcriberId...");
//                                        subCount[0] = String.valueOf(dataSnapshot.getValue());
                String value = dataSnapshot.getValue(String.class);
                myCallback.onCallbackId(value);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
