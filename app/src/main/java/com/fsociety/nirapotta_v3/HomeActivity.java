package com.fsociety.nirapotta_v3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
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
import com.fsociety.nirapotta_v3.Remote.IGoogleAPI;
import com.fsociety.nirapotta_v3.Subscription.BuyPackage;
import com.fsociety.nirapotta_v3.Subscription.CheckSubscription;
import com.fsociety.nirapotta_v3.Subscription.MyCallback;
import com.fsociety.nirapotta_v3.Subscription.Operations;
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, NavigationView.OnNavigationItemSelectedListener {
    //BroadcastReceiver
    private MyReceiver myReceiver;

    //play services
    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICE_RES_REQUEST = 7001;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    public static Location mLastLocation;

    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    DatabaseReference drivers;
    GeoFire geoFire;

    FirebaseAuth auth;

    SharedPreferences sp;
    Marker mCurrent;
    //MaterialAnimatedSwitch location_switch;
    Switch location_switch;
    ConstraintLayout mLinearLayout;
    TextView balance;
    int remainingRequests;

    IFCMService mService1;
    IGoogleAPI mService2;

    CardView card1, card2, card3; //taking the cards
    public static boolean emergency_boolean, healthy_boolean, sex_boolean = false;
    private TextView tv1, tv2, tv3;

    boolean isDriverFound;
    String driverId = "";
    double radius = 0.1;//km
    double distance = 0.1;//3 km
    private static final int LIMIT = 1;


    HashSet<String> driversFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sp = getSharedPreferences(StartingActivity.uid, MODE_PRIVATE);
        Log.d("radif", "onDataCreate:" + sp.getString("uid", ""));
        balance = findViewById(R.id.balance);
        Operations.currentUid = sp.getString("uid", "");
        Toolbar toolbar = findViewById(R.id.toolbarx);
        setSupportActionBar(toolbar);
        remainingRequests = -1;
        Operations.retrieveBalance(new MyCallback() {
            @Override
            public void onCallback(String value) {
//                remainingRequests=Integer.parseInt(Operations.balance);
                String txt = "Requests remaining : " + value;
                balance.setText(txt);
                remainingRequests = Integer.parseInt(value);
            }

            @Override
            public void onCallbackId(String value) {

            }
        });


        //for creating burger icon and making it open drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_id);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.opener, R.string.closer);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //for making nav clickable
        NavigationView nav = (NavigationView) findViewById(R.id.nav_id);
        nav.setNavigationItemSelectedListener(this);


//        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
//        filter.addAction(Intent.ACTION_SCREEN_OFF);
//        filter.addAction(Intent.ACTION_USER_PRESENT);
//        myReceiver = new MyReceiver();


        mLinearLayout = findViewById(R.id.layout_home);

//        btnPickupRequest = findViewById(R.id.buttonpickupreq);
//        btnPickupRequest.setEnabled(false);//eije
//        btnPickupRequest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(!requested) {
//                    requestPickupHere(FirebaseAuth.getInstance().getCurrentUser().getUid());
//                    btnPickupRequest.setBackground(getResources().getDrawable(R.drawable.button_red));
//                    btnPickupRequest.setText("STOP REQUESTING");
//                    driversFound = new HashSet<>();
//                    requested=true;
//                }
//                else {
//                   FirebaseDatabase.getInstance().getReference("PickupRequest").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue(new DatabaseReference.CompletionListener() {
//                       @Override
//                       public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                           Toast.makeText(HomeActivity.this,"Request Stopped",Toast.LENGTH_SHORT).show();
//                           btnPickupRequest.setBackground(getResources().getDrawable(R.drawable.register_button));
//                           btnPickupRequest.setText("REQUEST FOR HELP");
//                           requested=false;
//                       }
//                   });
//                }
//            }
//        });
        auth = FirebaseAuth.getInstance();


        tv1 = findViewById(R.id.medical_tv);
        tv2 = findViewById(R.id.sex_tv);
        tv3 = findViewById(R.id.emertv);

        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);

        if (remainingRequests <= 0 || !sp.getBoolean("on", false)) {
            card1.setEnabled(false);
            card2.setEnabled(false);
            card3.setEnabled(false);
        } else {
            card1.setEnabled(true);
            card2.setEnabled(true);
            card3.setEnabled(true);
        }


        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("card", "dhukse");
                if (!healthy_boolean) {
                    healthy_boolean = true;
                    tv1.setText("Getting help..");
                    card2.setEnabled(false);
                    card3.setEnabled(false);
                    Operations.updateBalance(("" + --remainingRequests));
                    balance.setText("Requests remaining : " + remainingRequests);
                    try {
                        requestPickupHere(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    } catch (NullPointerException np) {
                        sp = getSharedPreferences(StartingActivity.uid, MODE_PRIVATE);
                        requestPickupHere(sp.getString("uid", ""));
                    }
                    driversFound = new HashSet<>();

                } else {
                    healthy_boolean = false;
                    tv1.setText("Medical Condition");
                    card2.setEnabled(true);
                    card3.setEnabled(true);
                    checkRequestRemaining();
//                    removeGeoFire();
                    clean2();

                }
            }
        });

        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sex_boolean) {
                    sex_boolean = true;
                    tv2.setText("Getting help..");
                    card1.setEnabled(false);
                    card3.setEnabled(false);
                    Operations.updateBalance(("" + --remainingRequests));
                    balance.setText("Requests remaining : " + remainingRequests);
                    try {
                        requestPickupHere(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    } catch (NullPointerException np) {
                        sp = getSharedPreferences(StartingActivity.uid, MODE_PRIVATE);
                        requestPickupHere(sp.getString("uid", ""));
                    }
                    driversFound = new HashSet<>();
                } else {
                    sex_boolean = false;
                    tv2.setText("Sexual Harrasment");
                    card1.setEnabled(true);
                    card3.setEnabled(true);
                    checkRequestRemaining();
//                    removeGeoFire();
                    clean2();
                }
            }
        });

        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emergency_boolean == false) {
                    emergency_boolean = true;
                    tv3.setText("Getting help..");
                    card1.setEnabled(false);
                    card2.setEnabled(false);
                    Operations.updateBalance(("" + --remainingRequests));
                    balance.setText("Requests remaining : " + remainingRequests);
                    try {
                        requestPickupHere(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    } catch (NullPointerException np) {
                        sp = getSharedPreferences(StartingActivity.uid, MODE_PRIVATE);
                        requestPickupHere(sp.getString("uid", ""));
                    }
                    driversFound = new HashSet<>();
                } else {
                    emergency_boolean = false;
                    tv3.setText("Immediate Urgency");
                    card1.setEnabled(true);
                    card2.setEnabled(true);
                    checkRequestRemaining();
//                    removeGeoFire();
                    clean2();
                }
            }
        });


        mService1 = Common.getFCMService();

        location_switch = findViewById(R.id.location_switch);
        sp = getSharedPreferences(StartingActivity.switchState, MODE_PRIVATE);
        if (sp.getBoolean("on", false)) {
            Log.d("radif", "onCreate: true");
            location_switch.setChecked(true);
        } else {
            Log.d("radif", "onCreate: False");
            location_switch.setChecked(false);
        }

        location_switch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (isLocationEnabled(HomeActivity.this) == false) {
                        prompt_the_user();
                        location_switch.setChecked(false);
                    } else {
                        if (remainingRequests <= 0) {
                            card1.setEnabled(false);
                            card2.setEnabled(false);
                            card3.setEnabled(false);
                        } else {
                            card1.setEnabled(true);
                            card2.setEnabled(true);
                            card3.setEnabled(true);
                        }


                        sp = getSharedPreferences(StartingActivity.switchState, MODE_PRIVATE);
                        sp.edit().putBoolean("on", true).apply();
                        Log.d("true", "onCheckedChanged: " + sp.getBoolean("on", false));
//                    registerReceiver(myReceiver, filter);
//                    Intent i = new Intent(HomeActivity.this, UpdateService.class);
//                    startService(i);
                        startLocationUpdates();
                        updateLocation();
                        Snackbar.make(mLinearLayout, "You are online", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    card1.setEnabled(false);
                    card2.setEnabled(false);
                    card3.setEnabled(false);
                    sp = getSharedPreferences(StartingActivity.switchState, MODE_PRIVATE);
                    sp.edit().putBoolean("on", false).apply();
                    Log.d("true", "onCheckedChanged: " + sp.getBoolean("on", false));
//                    if (myReceiver != null)
//                    {
//                        unregisterReceiver(myReceiver);
//                        myReceiver = null;
//                    }
//                    Intent i = new Intent(HomeActivity.this, UpdateService.class);
//                    stopService(i);
                    stopLocationUpdates();
                    Snackbar.make(mLinearLayout, "You are offline", Snackbar.LENGTH_SHORT).show();
//                    removeGeoFire();
                    clean();
                }
            }

//            @Override
//            public void onCheckedChanged(boolean b) {
//                if (b) {
//                    card1.setEnabled(true);
//                    card2.setEnabled(true);
//                    card3.setEnabled(true);
//
////                    registerReceiver(myReceiver, filter);
////                    Intent i = new Intent(HomeActivity.this, UpdateService.class);
////                    startService(i);
//                    startLocationUpdates();
//                    updateLocation();
//                    Snackbar.make(mLinearLayout, "You are online", Snackbar.LENGTH_SHORT).show();
//                } else {
//                    card1.setEnabled(false);
//                    card2.setEnabled(false);
//                    card3.setEnabled(false);
//
////                    if (myReceiver != null)
////                    {
////                        unregisterReceiver(myReceiver);
////                        myReceiver = null;
////                    }
////                    Intent i = new Intent(HomeActivity.this, UpdateService.class);
////                    stopService(i);
//                    stopLocationUpdates();
//                    Snackbar.make(mLinearLayout, "You are offline", Snackbar.LENGTH_SHORT).show();
//                    clean();
//                }
//            }
        });
        //geo fire
        drivers = FirebaseDatabase.getInstance().getReference("Drivers");
        geoFire = new GeoFire(drivers);


        setUpLocations();
        mService2 = Common.getGoogleAPI();

        updateFirebaseToken();

    }

    private void checkRequestRemaining() {
        if (remainingRequests <= 0) {
            card1.setEnabled(false);
            card2.setEnabled(false);
            card3.setEnabled(false);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mGoogleApiClient == null)
            buildGoogleApiClient();
    }

    //    @Override
//    protected void onResume() {
//
//        //Start updating user location
//
//        startLocationUpdates();
//        updateLocation();
//        Toast.makeText(this,"You are now Online",Toast.LENGTH_LONG).show();
//        super.onResume();
//    }

    private void requestPickupHere(String uid) {

        DatabaseReference dbRequest = FirebaseDatabase.getInstance().getReference("PickupRequest");
        GeoFire mGeoFire = new GeoFire(dbRequest);
        mGeoFire.setLocation(uid, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), (key, error) -> Log.d("tag", "onComplete: Crash"));

        //btnPickupRequest.setText("Getting your driver...");
//        findDrivers();
        loadAllAvailableDrivers();
    }

    private void findDrivers() {
        final DatabaseReference drivers = FirebaseDatabase.getInstance().getReference("Drivers");
        GeoFire gfDrivers = new GeoFire(drivers);

        GeoQuery geoQuery = gfDrivers.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //if found
                if (!isDriverFound) {

                    isDriverFound = true;
                    driverId = key;
                    Toast.makeText(HomeActivity.this, "" + key, Toast.LENGTH_SHORT).show();
                    Log.d("key", "" + key);
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
                //if still not found, increase radius
                if (!isDriverFound) {
                    radius++;
                    findDrivers();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

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
                    distance = distance + 0.2;
                    loadAllAvailableDrivers();

                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_CODE);
            return;
        }
//        if (!isLocationEnabled(HomeActivity.this)) {
//            prompt_the_user();
//        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    private void stopLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }

    private void setUpLocations() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //request run time permission
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_CODE);
        } else {

            if (checkPlayServices()) {

                buildGoogleApiClient();
                createLocationRequest();
                updateLocation();
//                if (location_switch.isChecked()) {
//                    updateLocation();//etar kaaj ase
//                    // displayLocation2();
//                }

            }
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICE_RES_REQUEST).show();
            } else {
                Toast.makeText(this, "This device is not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public void prompt_the_user() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Location Services Not Active");
        builder.setMessage("Please enable Location Services and GPS to use Nirapotta");
        builder.setPositiveButton("I got it!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                // Show location settings when the user acknowledges the alert dialog
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        }).setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    // to prevent back button from closing dialog
                    // so it checks again wwhether location still off :P loop e cholbe
                    if (isLocationEnabled(HomeActivity.this) == false) {
                        prompt_the_user();
                    }
                }
                return false;
            }

        });

        Dialog alertDialog = builder.create();
        if (!alertDialog.isShowing()) {
            alertDialog.setCanceledOnTouchOutside(false);
            builder.setCancelable(false);
            alertDialog.show();
        }
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    @SuppressLint("RestrictedApi")
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void sendRequestToDriver(String driverId) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(Common.token_table);
        tokens.orderByKey().equalTo(driverId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Token token = postSnapshot.getValue(Token.class);
                            Log.d("check", "onDataChange: " + healthy_boolean + sex_boolean + emergency_boolean);
//                            String json_lat_lng= new Gson().toJson(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                            Notification data = new Notification(".CustomerCall", "bomb", "Someone needs help! ", "Tap to show location");
                            Data mData = new Data(("" + mLastLocation.getLatitude()), ("" + mLastLocation.getLongitude()), (String.format("%s", healthy_boolean)), (String.format("%s", sex_boolean)), (String.format("%s", emergency_boolean)));
                            Sender content = new Sender(token.getToken(), data, mData);

                            mService1.sendMessage(content)//what the
                                    .enqueue(new Callback<FCMResponse>() {
                                        @Override
                                        public void onResponse(@NonNull Call<FCMResponse> call, @NonNull Response<FCMResponse> response) {
                                            if (response.body().success == 1) {
                                                Toast.makeText(HomeActivity.this, "Request Sent", Toast.LENGTH_SHORT).show();
                                            }
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

    private void updateLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            if (location_switch.isChecked()) {
                final double latitude = mLastLocation.getLatitude();
                final double longitude = mLastLocation.getLongitude();
                SharedPreferences sp = getSharedPreferences("mLastLocation", MODE_PRIVATE);
                sp.edit().putString("lat", ("" + latitude)).apply();
                sp.edit().putString("lon", ("" + longitude)).apply();
                String uid = getUID();
                //update firebase now
                geoFire.setLocation(uid,
                        new GeoLocation(latitude, longitude),
                        new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                //Marker adding
                                if (mCurrent != null) {
                                    mCurrent.remove();//remove already marker

                                }

                            }

                        });

            }

        } else {

            Log.d("ERROR", "Cant get your location");
        }
    }

    private String getUID() {
        try {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (NullPointerException np) {
            sp = getSharedPreferences(StartingActivity.uid, MODE_PRIVATE);
            return sp.getString("uid", "");
        }
    }

    private void updateFirebaseToken() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference(Common.token_table);


        Token token = new Token(FirebaseInstanceId.getInstance().getToken());
        try {
            tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(token);
        } catch (NullPointerException np) {
            sp = getSharedPreferences(StartingActivity.uid, MODE_PRIVATE);
            tokens.child(sp.getString("uid", ""))
                    .setValue(token);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices()) {


                        createLocationRequest();
                        if (location_switch.isChecked()) {
                            updateLocation();
                        }

                    }

                }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (myReceiver != null)
//        {
//            unregisterReceiver(myReceiver);
//            myReceiver = null;
//        }


    }

    private void clean() {
        try {
            FirebaseDatabase.getInstance().getReference("Drivers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
        } catch (NullPointerException np) {
            sp = getSharedPreferences(StartingActivity.uid, MODE_PRIVATE);
            FirebaseDatabase.getInstance().getReference("Drivers").child(sp.getString("uid", "")).removeValue();
        }
    }

    private void clean2() {
        try {
            FirebaseDatabase.getInstance().getReference("PickupRequest").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    Toast.makeText(HomeActivity.this, "Removed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (NullPointerException np) {
            sp = getSharedPreferences(StartingActivity.uid, MODE_PRIVATE);
            FirebaseDatabase.getInstance().getReference("PickupRequest").child(sp.getString("uid", "")).removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    Toast.makeText(HomeActivity.this, "Removed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void removeGeoFire() {
        try {
            DatabaseReference dbRequest = FirebaseDatabase.getInstance().getReference("PickupRequest");
            GeoFire mGeoFire = new GeoFire(dbRequest);
            mGeoFire.removeLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    Toast.makeText(HomeActivity.this, "Request stopped.", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (NullPointerException np) {
            DatabaseReference dbRequest = FirebaseDatabase.getInstance().getReference("PickupRequest");
            GeoFire mGeoFire = new GeoFire(dbRequest);
            sp = getSharedPreferences(StartingActivity.uid, MODE_PRIVATE);
            mGeoFire.removeLocation(sp.getString("uid", ""), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    Toast.makeText(HomeActivity.this, "Request stopped.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        updateLocation();//login er shate shate call hoi
        Log.d("KEY", "onConnected: ");

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        SharedPreferences sp = getSharedPreferences("mLastLocation", MODE_PRIVATE);
        sp.edit().putString("lat", ("" + mLastLocation.getLatitude())).apply();
        sp.edit().putString("lon", ("" + mLastLocation.getLongitude())).apply();
        updateLocation();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater mif = getMenuInflater();
        mif.inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //update R
        if (item.getItemId() == R.id.logout) {
            SharedPreferences sp = getSharedPreferences(StartingActivity.my_preferernce, MODE_PRIVATE);
            Log.d("radif123", "onOptionsItemSelected: is logged in? " + sp.getBoolean("logged", false));
            if (sp.getBoolean("logged", true)) {
                sp.edit().putBoolean("logged", false).apply();
                sp = getSharedPreferences(StartingActivity.switchState, MODE_PRIVATE);
                sp.edit().putBoolean("on", false).apply();
                Log.d("true", "onCheckedChanged: " + sp.getBoolean("on", false));
                clean();
//                Log.d("logout2",FirebaseAuth.getInstance().getCurrentUser().getUid());
//                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(this, StartingActivity.class);
                startActivity(i);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //basically removed the super method so it does nothing now :P huhu whatte cheeky cunt i am

        //for the drawer to close only
        DrawerLayout dl = findViewById(R.id.drawer_id);
        if (dl.isDrawerOpen(GravityCompat.START)) {
            dl.closeDrawer(GravityCompat.START);
        } else {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Intent intent;

        //for checking
        if (item.getItemId() == R.id.use_case) {
            intent = new Intent(HomeActivity.this, HowTo.class);
            startActivity(intent);

        }

        if (item.getItemId() == R.id.subscription) {

            checkSubscriber();

        }

        if (item.getItemId() == R.id.about_us) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            LayoutInflater inflater = LayoutInflater.from(this);
            View login_layout = inflater.inflate(R.layout.abt_us_layout, null);
            dialog.setView(login_layout);

            dialog.setPositiveButton("Okay!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }

        if (item.getItemId() == R.id.feedback) {
            intent = new Intent(HomeActivity.this, FeedbackActivity.class);
            startActivity(intent);
        }

        //for closing the drawer
        DrawerLayout dl = findViewById(R.id.drawer_id);
        dl.closeDrawer(GravityCompat.START);//as drawer attached to start not end in nav layout_gravity

        return true;
    }


    private void checkSubscriber() {

        Operations.getSubscriberID(new MyCallback() {
            @Override
            public void onCallback(String value) {

            }

            @Override
            public void onCallbackId(String value) {
                if (value.equals("")) {
                    startActivity(new Intent(HomeActivity.this, CheckSubscription.class));

                } else {

                    sp = getSharedPreferences(StartingActivity.my_preferernce, MODE_PRIVATE);
                    sp.edit().putBoolean("logged", true).apply();
                    Intent i = new Intent(HomeActivity.this, BuyPackage.class);
                    startActivity(i);
                }
            }
        });

        //////////////////////////////////////////////////////
//    class UpdateService extends Service{
//
//        @Nullable
//        @Override
//        public IBinder onBind(Intent intent) {
//            return null;
//        }
//
//        @Override
//        public int onStartCommand(Intent intent, int flags, int startId) {
//            Log.d(TAG, "onStartCommand: Service started");
//            return super.onStartCommand(intent, flags, startId);
//        }
//
//        @Override
//        public void onDestroy() {
//            super.onDestroy();
//        }
//    }
    }
}

