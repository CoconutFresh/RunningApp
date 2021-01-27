package com.example.runningapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.firebase.database.annotations.NotNull;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, LocationSource, RunningFragment.RunningListener, InitializeRunFragment.InitRunListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    Polyline runRoute;
    PolylineOptions runRouteOptions;
    CameraPosition initialCamera;

    SupportMapFragment mapFragment;
    Fragment initializeRun;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    LatLng pastLoc;
    double totalDistRan = 0; //In km for now

    private OnLocationChangedListener mapLocationListener = null;

    boolean isRunning = false, initialState = true, pause = false;

    RelativeLayout maps_rl_fragment;
    final int FULLSCREEN = 8, SPLIT_SCREEN = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps); //Connects activity to layout layer

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initPolyline(); //For line graphing
        enableLocation();
        initFrag(); //Initialize fragments
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Enables my location layer
        enableLocation();
    }

    //Initializes lines seen on map while running
    private void initPolyline() {

        //For line graphing
        runRouteOptions = new PolylineOptions();
        runRouteOptions.clickable(false)
                .width(20)
                .startCap(new RoundCap())
                .jointType(JointType.ROUND)
                .color(Color.CYAN);
    }

    //Checks for permissions
    private void enableLocation() {
        //For finding current location of device
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //Checks for permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //If permissions are granted
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, this);
                if(mMap != null) {
                    mMap.setMyLocationEnabled(true); //Enables my location layer
                    mMap.setLocationSource(this); //Changes the location data from beta fusedlocationproviderclient to chad Android.location.Location
                }
            }
            //If permissions are not granted
            else {
            //if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);
            }
        }
    }

    //Initializes fragment seen on activity start up
    private void initFrag() {
        //Starts the Maps Activity with the initialize run fragment
        initializeRun = new InitializeRunFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.maps_rl_fragment, initializeRun).commit();
    }

    //If user gives permissions on activity start up
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableLocation();
                }
                return;
            default:
                return;
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        Log.d("trackLog", "Lat is: " + location.getLatitude() + ", "
                + "Lng is: " + location.getLongitude());

        mapLocationListener.onLocationChanged(location); //This is where it switches poopy default fusedLocationProviderClient to Android.location.Location
        LatLng curLoc = new LatLng(location.getLatitude(), location.getLongitude());

        //In charge of how map is displayed to the runner (Running vs. Not Running)
        if (!isRunning && !initialState) { //Map shows users location before run starts
            mMap.animateCamera(CameraUpdateFactory.newLatLng(curLoc));
            pastLoc = curLoc;
        }
        else if (isRunning && !initialState && !pause) { //User clicks start run
            //Testing distance algorithm
            double dist = getDistanceKm(pastLoc, curLoc);
            Log.d("dist", "Distance Traveled: " + dist);
            if (dist > 0.01) { //To account for gps inaccuracies TODO: Haven't actually tested this lmao
                totalDistRan += dist; //Adds to the total distance ran
                pastLoc = curLoc; //Updates the past location to the current location
            }
            //TODO: Concerning pause button, the trail will probably connect where the person paused, gotta unchain the location point from where they unpause!
            updateTrail(curLoc);
        }
        else if (pause) { //
            pastLoc = null;
        }
        else if (initialState) { //Initial map preparations before the user starts run
            initCamera(curLoc);
            initialState = false;
        }
    }

    //For tracking, in charge of following device and drawing lines between past locations with present
    private void updateTrail(LatLng location) {

        //TODO: Make Polyline look nicer

        //Log.d("polylineDebug", "This is being called");
        runRouteOptions.add(location);
        runRoute = mMap.addPolyline(runRouteOptions);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(location));

        //TODO:Add a way to make a new polyline object after pause in order to break the line into segments
    }

    //Initializes camera on Activity start up
    private void initCamera(LatLng curLoc) {
        initialCamera = new CameraPosition.Builder()
                .target(curLoc)
                .zoom(18)
                .bearing(0)
                .tilt(0)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(initialCamera));
    }

    //Haversine formula for calculating distance between two sets of LatLng
    private double getDistanceKm(@NotNull LatLng point1,@NotNull LatLng point2) {
        if(point1 == null)
            return 0;
        int earthRadius = 6371;
        double lat1 = point1.latitude, lat2 = point2.latitude;

        double dLat = deg2Rad(point2.latitude - point1.latitude);
        double dLng = deg2Rad(point2.longitude - point1.longitude);
        double a =
                Math.sin(dLat/2) * Math.sin(dLat/2)
                        + Math.cos(deg2Rad(lat1)) * Math.cos(deg2Rad(lat2))
                        * Math.sin(dLng/2) * Math.sin(dLng/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = earthRadius * c;

        return d;
    }

    private double deg2Rad(double deg) { //Helper function for getDistanceKm()
        return deg * (Math.PI/180);
    }

    //Activity to Fragment communication methods
    protected double getTotalDist() {
        return totalDistRan;
    }

    protected boolean getInitialState() {
        return initialState;
    }

    //Necessary methods in order to change the locationSource to chad Android.location.Location
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        this.mapLocationListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        this.mapLocationListener = null;
    }

    //Method for interacting with InitializeRunFragment
    @Override
    public void onStartRunPressed(boolean isRunning) { //Starts run when bt_startRun in initializeRunFragment is pressed
        this.isRunning = isRunning;

        viewChanger(FULLSCREEN);
    } //Start Button

    //Methods for interacting with RunningFragment
    @Override
    public void onPauseRunPressed(boolean pause) {
        this.pause = pause;
        if(pause) {
            viewChanger(SPLIT_SCREEN);
        }
        else {
            viewChanger(FULLSCREEN);
        }
    } //Pause Button

    @Override
    public void onStopRunPressed(boolean isRunning) { //Stops run when bt_stopRun in RunningFragment is pressed
        this.isRunning = isRunning;
        viewChanger(SPLIT_SCREEN);
    } //Stop Button

    //Method for showing/hiding map and changing size of fragments
    private void viewChanger(int weight) {
        //Changes the weight of the relative layout embedded in the linear layout
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, weight);

        if(weight == FULLSCREEN) { //We want the stats to be fullscreen so we hide map
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_up)
                    .hide(mapFragment)
                    .commit();
        }
        else if (weight == SPLIT_SCREEN){ //We want a split screen with the map, so we show map
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_up)
                    .show(mapFragment)
                    .commit();
        }

        maps_rl_fragment = findViewById(R.id.maps_rl_fragment);
        maps_rl_fragment.setLayoutParams(param);
    }

    //Makes sure that if the user hits the back button, we no longer track their location
    @Override
    public void onBackPressed() {
        locationManager.removeUpdates(this);
        locationManager = null;
        startActivity(new Intent(this, HomePage.class));
        finish();
        super.onBackPressed();
    }
}