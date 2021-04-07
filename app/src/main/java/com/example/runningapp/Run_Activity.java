package com.example.runningapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.navigation.NavigationView;

import java.text.DecimalFormat;

public class Run_Activity extends AppCompatActivity implements OnMapReadyCallback, Run_Tracker_Service.ServiceCallback, LocationSource, NavigationView.OnNavigationItemSelectedListener, Run_Button_Start_Fragment.StartButtonListener, Run_Button_Pause_Fragment.PauseButtonListener, Run_Button_ResumeStop_Fragment.Resume_StopListener {

    private static final String TAG = "MapsActivity";

    //Related to Google's Map SDK
    private GoogleMap mMap;
    CameraPosition initialCamera;

    //Related to Android's location management system
    private OnLocationChangedListener mapLocationListener = null; //Switching Google's location listener for Androids

    //Fragments
    Fragment initializeRunFragment, startButtonFragment, runningFragment, pauseButtonFragment, resume_stopFragment, finishRunFragment; //All of the various fragments that this activity switches between
    SupportMapFragment mapFragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    float totalDist = 0; //In km for now
    float unitConversion; //Conversion factor for miles and kilometers
    String unit = "error";

    //Related to changing the UI elements the user can see
    RelativeLayout maps_rl_fragment; //Fragment pertaining to the current data
    FrameLayout maps_fl_fragment; //Fragment which contains the buttons the user may interact with

    //Weights for layouts
    final float FULLSCREEN = 7f, SPLIT_SCREEN = 1f; //Manages map fragment visibility
    final float PRESENT_B = 1f, MISSING_B = 0f; //Manages button fragment
    //View flipper index for run session statistics
    final int RUN_LARGE = 0, RUN_SMALL = 1;

    boolean mapShown = true; //Flag for whether map is shown or not

    RunSession runStats; //Object that contains the end of run data

    //Service
    private Run_Tracker_Service trackerService;
    private boolean isBound = false;
    private boolean discardFlag = false;

    //test
    String mode = "ERROR";
    //test
    private DrawerLayout drawer;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "OnServiceConnnected: connected to service");
            Run_Tracker_Service.LocalBinder binder = (Run_Tracker_Service.LocalBinder) service;
            trackerService = binder.getService();
            Run_Tracker_Service.setCallbacks(Run_Activity.this);
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "OnServiceConnnected: disconnected to service");
            isBound = false;
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initializeRunFragment = new Run_Initialize_Fragment();
        startButtonFragment = new Run_Button_Start_Fragment();
        runningFragment = new Run_Running_Fragment();
        pauseButtonFragment = new Run_Button_Pause_Fragment();
        resume_stopFragment = new Run_Button_ResumeStop_Fragment();
        finishRunFragment = new Run_Finish_Fragment();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps); //Connects activity to layout layer

        startService(); //Starts location service

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        NavigationView navView = findViewById(R.id.nav_view);
        navView.setCheckedItem(R.id.nav_run);
        navView.setNavigationItemSelectedListener(this);

        drawer = findViewById(R.id.drawer_layout);



        enableLocation(); //Checks for permissions

        //Initialize fragments
        fragmentManager(R.id.maps_rl_fragment, initializeRunFragment);
        /* I also initialize the StartButtonFragment later in the code.
           Specifically, I start it in the onLocationChanged method in the service when the
           initialState flag is raised (In the else block of that large if/else statement).
           We do this so that the application has time to find the user's location and move
           the camera to that specific location
         */

        SharedPreferences sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        unitConversion = getUnitConversion(sharedPreferences.getString("distance_units", "Miles"));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startService() {
        Intent intent = new Intent(this, Run_Tracker_Service.class);
        Log.d(TAG, "startService: Service binded");
        startForegroundService(intent); //Not sure if I need this as well
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!discardFlag && isBound && trackerService != null) {
            trackerService.stopTracking();

            Intent serviceIntent = new Intent(this, Run_Tracker_Service.class);
            stopService(serviceIntent);
            //unbindService(serviceConnection); //[NOTE] Unbinding crashes the activity (Not sure if I need to unbind a service that I've already stopped)
        }
    }

    private float getUnitConversion(String unit) {
        switch(unit) {
            case "Miles":
                this.unit = "mi";
                return 1609.34f;
            case "Kilometers":
                this.unit = "km";
                return 1000;
            default:
                return -1; //If there is an error, the distance will be negative
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.finish_run_menu, menu);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(false); //Hides back button
        menu.findItem(R.id.menu_run_panel).setVisible(true);

        return super.onCreateOptionsMenu(menu);
    }

    //Listener for the buttons within the menu bar. (Back button and Delete Session seen in Finish Run Fragment)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_run_panel:
                /*fragmentManager(R.id.maps_rl_fragment, runningFragment);
                viewChanger(FULLSCREEN);
                viewButton(PRESENT_B);*/
                if(drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                else {
                    drawer.openDrawer(GravityCompat.START);
                }
                return true;
            case R.id.menu_delete:
                new AlertDialog.Builder(this)
                        .setTitle("Discard Run")
                        .setMessage("Are you sure you want to discard this session?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                discardFlag = true; //So the service does not unbind when restarting the activity
                                Run_Running_Fragment.timerReset();
                                Intent startIntent = getIntent();
                                finish();
                                startActivity(startIntent);
                            }
                        })
                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Enables my location layer
        enableLocation();
    }

    //Checks for permissions
    private void enableLocation() {
        //For finding current location of device
        //locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //Checks for permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //If permissions are granted
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this); //In charge of the frequency that the app checks for distance (normally 500 and 5)
                if(mMap != null) {
                    mMap.setMyLocationEnabled(true); //Enables my location layer
                    mMap.setLocationSource(this); //Changes the location data from beta fusedlocationproviderclient to chad Android.location.Location
                }
            }
            //If permissions are not granted
            else {
                requestPermissions(new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);
            }
        }
    }

    //In charge of swapping fragments within the activity
    private void fragmentManager(int layout, Fragment fragment) {
        //Starts the Maps Activity with the initialize run fragment
        Fragment prev;
        fragmentManager = getSupportFragmentManager();
        prev = fragmentManager.findFragmentById(layout);

        if (prev != null) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            fragmentTransaction.remove(prev).commitAllowingStateLoss();
        }
        FragmentTransaction addTransaction = fragmentManager.beginTransaction();
        addTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        addTransaction.add(layout, fragment).commitAllowingStateLoss();
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
        }
    }

    @Override
    public void initialState(LatLng curLoc) {
        initCamera(curLoc);
        fragmentManager(R.id.maps_fl_buttonPlacement, startButtonFragment); //Waits for camera and location to be initialized before allowed user to click start
    }

    @Override
    public void getLocation(Location location) {
        mapLocationListener.onLocationChanged(location); //This is where it switches poopy default fusedLocationProviderClient to Android.location.Location
    }

    @Override
    public void animateCamera(LatLng curLoc) {
        mMap.animateCamera(CameraUpdateFactory.newLatLng(curLoc));
    }

    @Override
    public void updateTrail() {
        trackerService.runRoute = mMap.addPolyline(trackerService.runRouteOptions);
    }

    @Override
    public void updateDistance(float totalDist) {
        Run_Running_Fragment.updateDistance(totalDist);
        this.totalDist = totalDist;
    }

    @Override
    public void updateNotification() {
        DecimalFormat dfRound = new DecimalFormat("#.##");
        trackerService.updateNotification(Run_Running_Fragment.formatTime, dfRound.format(totalDist), unit, Run_Running_Fragment.formatPace);
    }

    //Initializes camera on Activity start up
    private void initCamera(LatLng curLoc) {
        initialCamera = new CameraPosition.Builder()
                .target(curLoc)
                .zoom(18)
                .bearing(0)
                .tilt(0)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(initialCamera), 500, null);
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

    //Listeners for button fragments
    @Override
    public void onStartPressed(boolean startPressed) { //start fragment
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED); //Disables the ability to open the side panel
        trackerService.setIsRunning(startPressed);
        viewChanger(FULLSCREEN);
        fragmentManager(R.id.maps_rl_fragment, runningFragment);
        fragmentManager(R.id.maps_fl_buttonPlacement, pauseButtonFragment);
    }

    //Pause Fragment
    @Override
    public void onPausePressed(boolean pause) {
        trackerService.initPolyline(); //Separates the polyline if the user pauses and moves.

        fragmentManager(R.id.maps_fl_buttonPlacement, resume_stopFragment);

        viewChanger(SPLIT_SCREEN);

        ViewFlipper vf = findViewById(R.id.viewFlipper);
        vf.setDisplayedChild(RUN_SMALL);

        //this.pause = pause;
        trackerService.setPause(pause);
        Run_Running_Fragment.timerPause(pause);
    }

    //TODO: Change how this is implemented (The button design is too similar to Strava's)
    @Override
    public void onMapShownPressed() {
        ViewFlipper vf = (ViewFlipper) findViewById(R.id.viewFlipper);

        if(mapShown) {
            viewChanger(FULLSCREEN);
            vf.setDisplayedChild(RUN_LARGE);
        }
        else {
            viewChanger(SPLIT_SCREEN);
            vf.setDisplayedChild(RUN_SMALL);
        }
    }

    //resume/stop fragment
    @Override
    public void onResumePressed(boolean pause) {
        //this.pause = pause;
        trackerService.setPause(pause);
        fragmentManager(R.id.maps_fl_buttonPlacement, pauseButtonFragment);
        Run_Running_Fragment.timerPause(pause);
        viewChanger(FULLSCREEN);
        ViewFlipper vf = (ViewFlipper) findViewById(R.id.viewFlipper);
        vf.setDisplayedChild(RUN_LARGE);
    }

    @Override
    public void onStopPressed(boolean stop) {
        //Grabbing data for RunSession
        runStats = new RunSession(mode, Run_Running_Fragment.totalTime, totalDist, Run_Running_Fragment.minutePace, (int) Run_Running_Fragment.secondsPace);
        //Session additions
        runStats.setFormatPace(Run_Running_Fragment.formatPace);
        runStats.setFormatTime(Run_Running_Fragment.formatTime);

        //Test
        Log.d("timeOutput", "RunningFragment: " + Run_Running_Fragment.totalTime);
        Log.d("timeOutput", "runStats: " + runStats.getTotalTime());

        //Fragment Behavior
        fragmentManager(R.id.maps_rl_fragment, finishRunFragment);
        viewChanger(FULLSCREEN);
        viewButton(MISSING_B);
    }

    //Method for showing/hiding map and changing size of fragments
    private void viewChanger(float weight) {
        //Changes the weight of the relative layout embedded in the linear layout
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, weight);
        maps_rl_fragment = findViewById(R.id.maps_rl_fragment);
        maps_rl_fragment.setLayoutParams(param);

        if(weight == FULLSCREEN) { //We want the stats to be fullscreen so we hide map
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_up)
                    .hide(mapFragment)
                    .commit();
            mapShown = false; //Setting flag to false for onMapShownPressed() and others
        }
        else if (weight == SPLIT_SCREEN){ //We want a split screen with the map, so we show map
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_up)
                    .show(mapFragment)
                    .commit();
            mapShown = true; //Setting flag to true for onMapShownPressed() and others
        }
    }

    private void viewButton(float weight) {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, weight);
        maps_fl_fragment = findViewById(R.id.maps_fl_buttonPlacement);
        maps_fl_fragment.setLayoutParams(param);
    }


    //Determines the actions of the back button
    @Override
    public void onBackPressed() {

        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(initializeRunFragment.isResumed()) {
            //Switches Activity to home page
            startActivity(new Intent(Run_Activity.this, HomePage.class));
            finish();
        }
        else if(runningFragment.isResumed()) {
            //Toast.makeText(this, "RunningFragment is true", Toast.LENGTH_SHORT).show();
            ViewFlipper vf = (ViewFlipper) findViewById(R.id.viewFlipper);
            if(vf.getDisplayedChild() == RUN_SMALL) {
                viewChanger(FULLSCREEN);
                vf.setDisplayedChild(RUN_LARGE);
            }
            else {
                new AlertDialog.Builder(this)
                        .setTitle("Discard Run")
                        .setMessage("Are you sure you want to discard this session?")
                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Run_Running_Fragment.timerReset(); //Resets timer

                                //Switches Activity to home page
                                startActivity(new Intent(Run_Activity.this, HomePage.class));
                                finish();
                            }
                        })
                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            }
        }
        else if(finishRunFragment.isResumed()) {
            fragmentManager(R.id.maps_rl_fragment, runningFragment);
            viewChanger(FULLSCREEN);
            viewButton(PRESENT_B);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {
            case R.id.nav_run:
                Toast.makeText(this, "Selected: Run", Toast.LENGTH_SHORT).show();
                mode = "Run";
                break;
            case R.id.nav_walk:
                Toast.makeText(this, "Selected: Walk", Toast.LENGTH_SHORT).show();
                mode = "Walk";
                break;
            case R.id.nav_hike:
                Toast.makeText(this, "Selected: Hike", Toast.LENGTH_SHORT).show();
                mode = "Hike";
                break;
            case R.id.nav_bike:
                Toast.makeText(this, "Selected: Bike", Toast.LENGTH_SHORT).show();
                mode = "Bike";
                break;
        }

        Run_Initialize_Fragment.setMode(mode);
        return true;
    }

}
