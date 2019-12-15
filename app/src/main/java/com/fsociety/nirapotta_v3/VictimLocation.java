package com.fsociety.nirapotta_v3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.fsociety.nirapotta_v3.Common.Common;
import com.fsociety.nirapotta_v3.Remote.IGoogleAPI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VictimLocation extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    BitmapDescriptor icon, icon2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_victim_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        icon = BitmapDescriptorFactory.fromResource(R.drawable.loci);
        icon2 = BitmapDescriptorFactory.fromResource(R.drawable.navigation);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Intent intent = getIntent();
        Double lat = intent.getExtras().getDouble("lat");
        Double lng = intent.getExtras().getDouble("lng");


        mMap.addMarker(new MarkerOptions().icon(icon)
                .position(new LatLng(lat, lng))
                .title("Victim"));
        //move camera here
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15.0f));

        SharedPreferences sp = getSharedPreferences("mLastLocation", MODE_PRIVATE);
        double ulat = Double.parseDouble(sp.getString("lat", "0.0"));
        double ulon = Double.parseDouble(sp.getString("lon", "0.0"));
        mMap.addMarker(new MarkerOptions().icon(icon2)
                .position(new LatLng(ulat, ulon))
                .title("You"));


//        LatLng victim_pos = new LatLng(lat, lng);
//        mMap.addMarker(new MarkerOptions().position(victim_pos).title("HERE!"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(victim_pos));
    }

    private void clean() {
        FirebaseDatabase.getInstance().getReference("Drivers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Toast.makeText(VictimLocation.this, "Removed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences sp = getSharedPreferences(StartingActivity.switchState, MODE_PRIVATE);
        sp.edit().putBoolean("on", false).apply();
        clean();
        Intent i = new Intent(VictimLocation.this, HomeActivity.class);
        startActivity(i);

    }
}
