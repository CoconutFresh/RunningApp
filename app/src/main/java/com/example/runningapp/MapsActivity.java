package com.example.runningapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Cap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.maps.model.SquareCap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, LocationListener {
    private GoogleMap mMap;
    LocationManager locationManager;
    PolylineOptions runRouteOptions;
    CameraPosition initialCamera;

    Fragment initializeRunFragment, runningFragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    Button bt_startRun, bt_stopRun, bt_startRun_test;

    boolean isRunning = false, initialState = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //For line graphing
        runRouteOptions = new PolylineOptions();
        runRouteOptions.clickable(false)
                .width(20)
                .startCap(new RoundCap())
                .jointType(JointType.ROUND)
                .color(Color.CYAN);

        //For finding current location of device
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //Checks for permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);
                return;
            }

        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, this);

        //test
        bt_startRun_test = findViewById(R.id.bt_startRun_test);
        bt_startRun_test.setOnClickListener(this);

        /*bt_startRun = findViewById(R.id.bt_startRun);
        bt_startRun.setOnClickListener(this);
        bt_stopRun = findViewById(R.id.bt_stopRun);
        bt_stopRun.setOnClickListener(this);*/

        /*initializeRunFragment = new InitializeRunFragment();
        runningFragment = new RunningFragment();*/

        /*fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl_saveme, initializeRunFragment);
        fragmentTransaction.commit();*/

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM_LEVEL));
    }

    private void startRun(LatLng location) {

        //TODO: Make Polyline look nicer

        Log.d("polylineDebug", "This is being called");
        runRouteOptions.add(location);
        mMap.addPolyline(runRouteOptions);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(location));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //startRun();
                }
                return;
            default:
                return;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_startRun_test:
                /*fragmentTransaction = fragmentManager.beginTransaction();
                //fragmentTransaction.replace(R.id.fl_saveme, runningFragment);
                fragmentTransaction.commit();*/
                isRunning = true;
                //startRun();
                break;
            case R.id.bt_stopRun:
                /*fragmentTransaction = fragmentManager.beginTransaction();
                //fragmentTransaction.replace(R.id.fl_saveme, initializeRunFragment);
                fragmentTransaction.commit();
                isRunning = false;*/
            default:
                break;
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        Log.d("trackLog", "Lat is: " + location.getLatitude() + ", "
                + "Lng is: " + location.getLongitude());

        LatLng curLoc = new LatLng(location.getLatitude(), location.getLongitude());

        if (isRunning) {
            startRun(curLoc);
            Toast.makeText(this, "Starting run", Toast.LENGTH_SHORT).show();
        }
        else if (!isRunning && initialState) {
            initialCamera = new CameraPosition.Builder()
                    .target(curLoc)
                    .zoom(18)
                    .bearing(0)
                    .tilt(0)
                    .build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(initialCamera));
            initialState = false;
        } else if (!isRunning && !initialState) {
            mMap.animateCamera(CameraUpdateFactory.newLatLng(curLoc));
        }
    }
}