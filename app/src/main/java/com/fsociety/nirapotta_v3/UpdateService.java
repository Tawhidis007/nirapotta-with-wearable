package com.fsociety.nirapotta_v3;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.fsociety.nirapotta_v3.Common.Common;
import com.fsociety.nirapotta_v3.Model.Data;
import com.fsociety.nirapotta_v3.Model.FCMResponse;
import com.fsociety.nirapotta_v3.Model.Notification;
import com.fsociety.nirapotta_v3.Model.Sender;
import com.fsociety.nirapotta_v3.Model.Token;
import com.fsociety.nirapotta_v3.Remote.IFCMService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final int LIMIT = 3;
    private int distance = 1;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    GeoFire geoFire;

    HashSet<String> driversFound;
    IFCMService mService1;

    @Override
    public void onCreate() {
        super.onCreate();
        // register receiver that handles screen on and screen off logic
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        BroadcastReceiver mReceiver = new MyReceiver();
        registerReceiver(mReceiver, filter);
        Log.d("tag", "onCreate: service created");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("tag", "onStartCommand: triggered");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return START_STICKY;
        }
        geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference("Drivers"));
        buildGoogleApiClient();
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        mService1 = Common.getFCMService();
        requestPickupHere(FirebaseAuth.getInstance().getCurrentUser().getUid());
        driversFound = new HashSet<>();
        return START_STICKY;

    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void requestPickupHere(String uid) {

        DatabaseReference dbRequest = FirebaseDatabase.getInstance().getReference("PickupRequest");
        GeoFire mGeoFire = new GeoFire(dbRequest);
        mGeoFire.setLocation(uid, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), (key, error) -> Log.d("tag", "onComplete: Crash"));

        //btnPickupRequest.setText("Getting your driver...");
//        findDrivers();
        loadAllAvailableDrivers();
    }

    public void loadAllAvailableDrivers() {
        //load all avaliable drivers in 3km
        DatabaseReference driversLocation = FirebaseDatabase.getInstance().getReference("Drivers");
        GeoFire gf = new GeoFire(driversLocation);

        GeoQuery geoQuery = gf.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), distance);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, final GeoLocation location) {

                //use key to get email  from table USERS
                //table USERS is the table used for when we signed up
                if (!driversFound.contains(key) && !key.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                    driversFound.add(key);

                    Log.d("KEY", FirebaseAuth.getInstance().getCurrentUser().getUid() + " found     " + key + "     " + location.latitude + "      " + location.longitude);

                    sendRequestToDriver(key);
                }

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (distance <= LIMIT) {
                    distance++;
                    loadAllAvailableDrivers();

                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    private void sendRequestToDriver(String driverId) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(Common.token_table);
        tokens.orderByKey().equalTo(driverId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Token token = postSnapshot.getValue(Token.class);

                            //String json_lat_lng= new Gson().toJson(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                            Data mData = new Data(("" + mLastLocation.getLatitude()), ("" + mLastLocation.getLongitude()), "false", "false", "true");
                            Notification data = new Notification(".CustomerCall", "Bomb", "Someone needs help!", "Tap to show location");

                            Sender content = new Sender(token.getToken(), data, mData);

                            mService1.sendMessage(content)//what the
                                    .enqueue(new Callback<FCMResponse>() {
                                        @Override
                                        public void onResponse(@NonNull Call<FCMResponse> call, @NonNull Response<FCMResponse> response) {
                                        }

                                        @Override
                                        public void onFailure(Call<FCMResponse> call, Throwable t) {
                                            Log.d("send", t.getMessage());
                                            Log.d("send", "onFailure: failed");
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onDestroy() {
        Log.d("tag", "onDestroy: service stopped");
        super.onDestroy();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        updateLocation();//login er shate shate call hoi
        Log.d("KEY", "onConnected: ");

    }

    private void updateLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {

            final double latitude = mLastLocation.getLatitude();
            final double longitude = mLastLocation.getLongitude();

            //update firebase now
            geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    new GeoLocation(latitude, longitude),
                    new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            //Marker adding

                        }

                    });


        } else {

            Log.d("ERROR", "Cant get your location");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}