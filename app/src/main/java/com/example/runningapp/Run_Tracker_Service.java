package com.example.runningapp;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

public class Run_Tracker_Service extends Service implements LocationListener{

    private static final String TAG = "Run_Tracker_Service"; //Debugging Tag

    private IBinder mBinder = new LocalBinder(); //Binds service to activity
    private static ServiceCallback callback; //Communication from service to activity

    //Android Location Library
    LocationManager locationManager;
    final int MIN_TIME_MS = 500, MIN_DISTANCE = 5;

    //Multi-threading
    private Handler mainHandler = new Handler(Looper.getMainLooper()); //Communicates with UI thread
    private HandlerThread handlerThread = new HandlerThread("Tracker Thread"); //Background thread for tracking location

    //Notification
    NotificationCompat.Builder builder;
    NotificationManagerCompat notificationManager;

    //Polyline for map
    static Polyline runRoute;
    static PolylineOptions runRouteOptions;

    //Running data
    float totalDist = 0;
    Location pastLoc;
    float unitConversion = -1;

    //Flags
    boolean isRunning = false, initialState = true, pause = false; //Flags that make up the behaviour of the GPS tracking

/*    //TEST
    int testCounter = 0;
    Handler mHandler;
    int maxValue = 5000;*/

    //Service Callback Interface for service to activity communication
    public interface ServiceCallback {
        void getLocation(Location location);
        void initialState(LatLng curLoc);
        void updateTrail();
        void animateCamera(LatLng curLoc);
        void updateDistance(float totalDist);
        void updateNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, Run_Activity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, 0);

        //Allows for ability to update notification
        builder = new NotificationCompat.Builder(this, "exampleServiceChannel")
                .setSmallIcon(R.drawable.ic_android)
                .setContentText("Start moving!")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManager = NotificationManagerCompat.from(this);
        startForeground(1, builder.build());

        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Managing the handler thread
        handlerThread.start();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        initPolyline();

        //Permission check (We can't make the user change permissions in a service, we have that and an additional check in the activity
        if (ActivityCompat.checkSelfPermission(Run_Tracker_Service.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Run_Tracker_Service.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this, handlerThread.getLooper());
        }
    }

/*    private void test() {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(testCounter >= maxValue) {
                    mHandler.removeCallbacks(this);
                }
                else {
                    Log.d(TAG, "run: " + testCounter);
                    testCounter += 100;
                    mHandler.postDelayed(this, 500);
                }
            }
        };
        mHandler.postDelayed(runnable, 500);
    }*/


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        Run_Tracker_Service getService() {
            return Run_Tracker_Service.this;
        }
    }

    public void setUnitConversion(float unitConversion) {
        this.unitConversion = unitConversion;
    }

    //Initializes lines seen on map while running
    public void initPolyline() {
        //For line graphing
        runRouteOptions = new PolylineOptions(); //Makes a new polyline options object everytime this method is called. (For separating polyline when pausing and moving)
        runRouteOptions.clickable(false)
                .width(15)
                .startCap(new RoundCap())
                .endCap(new RoundCap())
                .jointType(JointType.ROUND)
                .color(0xFF4CAF50); //This color is green
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                LatLng curLoc = new LatLng(location.getLatitude(), location.getLongitude());
                callback.getLocation(location);

                //In charge of how map is displayed to the runner (Running vs. Not Running)
                if (!isRunning && !initialState) { //Map shows users location before run starts
                    callback.animateCamera(curLoc);
                    pastLoc = location;
                }
                else if (isRunning && !initialState && !pause) { //User clicks start run

                    float dist = pastLoc.distanceTo(location) / unitConversion; //converts it from meters to kilometers
                    pastLoc = location; //Updates the past location to the current location
                    totalDist += dist; //Adds to the total distance ran
                    //totalDist += 0.025; //For testing purposes

                    runRouteOptions.add(curLoc);

                    //Callbacks for Run_Activity to do work
                    callback.updateDistance(totalDist);
                    callback.updateTrail();
                    callback.animateCamera(curLoc);
                    callback.updateNotification();
                }
                else if (pause) {
                    pastLoc = location;
                }
                else { //Initial map preparations before the user starts run
                    callback.initialState(curLoc);
                    pastLoc = location;
                    initialState = false;
                }
            }
        });
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    //Updates the notification banner
    public void updateNotification(String time, String distance, String unit, String pace) {
        //Log.d(TAG, "Time: " + time + " Distance: " + distance + " Pace: " + pace);
        builder.setContentText("Time: " + time + " Distance: " + distance + " " + unit + " Pace: " + pace);
        notificationManager.notify(1, builder.build());
    }

    //Terminates handlers, location manager, and notification banner
    public void stopTracking() {
        handlerThread.quit();
        handlerThread = null;
        locationManager.removeUpdates(this);
        locationManager = null;

        //Kills Notification
        stopForeground(true);
    }

    public static void setCallbacks(ServiceCallback callbacks) {
        callback = callbacks;
    }

    public void setPause(boolean flag) {
        pause = flag;
    }

    public void setIsRunning(boolean flag) {
        isRunning = flag;
    }

}
