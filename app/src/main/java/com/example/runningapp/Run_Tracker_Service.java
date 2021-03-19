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

public class Run_Tracker_Service extends Service implements LocationListener{

    private static final String TAG = "Run_Tracker_Service"; //Debugging Tag

    private IBinder mBinder = new LocalBinder(); //Binds service to activity
    private static ServiceCallback callback; //Communication from service to activity

    //Location
    LocationManager locationManager;
    final int MIN_TIME_MS = 500, MIN_DISTANCE = 5;

    //Multi-threading
    Handler mainHandler = new Handler(Looper.getMainLooper()); //Communicates with UI thread
    private HandlerThread handlerThread = new HandlerThread("Tracker Thread"); //Background thread for tracking locaiton
    private Handler threadHandler;

    //Notification
    NotificationCompat.Builder builder;
    NotificationManagerCompat notificationManager;

    int counter = 0;

    public interface ServiceCallback {
        void getLocation(Location location);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, Run_Activity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, 0);

/*        Notification notification = new NotificationCompat.Builder(this, "exampleServiceChannel")
                .setContentTitle("TODO: Naruto Run")
                .setContentText("TODO: Content Text")
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(pendingIntent)
                .build();*/

        //Allows for ability to update notification
        builder = new NotificationCompat.Builder(this, "exampleServiceChannel")
                .setSmallIcon(R.drawable.ic_android)
                .setContentTitle("TODO: Naruto Run")
                .setContentText("TODO: Content Text")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        notificationManager = NotificationManagerCompat.from(this);
        //notificationManager.notify(1, builder.build());
        startForeground(1, builder.build());

        //startForeground(1, notification);
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Managing the handler thread
        handlerThread.start();
        threadHandler = new Handler(handlerThread.getLooper());
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //Permission check (We can't make the user change permissions in a service, we have that and an additional check in the activity
        if (ActivityCompat.checkSelfPermission(Run_Tracker_Service.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Run_Tracker_Service.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this, threadHandler.getLooper());
        }

    }

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

    @Override
    public void onLocationChanged(@NonNull Location location) {
        //Log.d(TAG, "onLocationChanged: Lat: " + location.getLatitude() + " Lng: " + location.getLongitude());

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.getLocation(location);
            }
        });

        //builder.setContentText("test: " + counter++);
        //notificationManager.notify();
        //notificationManager.notify(1, builder.build());
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    //Updates the notification banner
    public void updateNotification(String time, String distance, String pace) {
        //Log.d(TAG, "Time: " + time + " Distance: " + distance + " Pace: " + pace);
        builder.setContentText("Time: " + time + " Distance: " + distance + " Pace: " + pace);
        notificationManager.notify(1, builder.build());
    }

    //Terminates handlers
    public void stopTracking() {
        handlerThread.quit();
        handlerThread = null;
        locationManager.removeUpdates(this);
        locationManager = null;

        //Kills Notification
        //notificationManager.cancel(1);
        stopForeground(true);
    }

    public static void setCallbacks(ServiceCallback callbacks) {
        callback = callbacks;
    }
}
