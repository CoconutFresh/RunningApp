package com.example.runningapp;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExampleService extends Service implements LocationListener{

    private static final String TAG = "ExampleService"; //Debugging Tag

    private IBinder mBinder = new LocalBinder();

    private static ServiceCallback callback;

    //Location
    LocationManager locationManager;
    final int MIN_TIME_MS = 500, MIN_DISTANCE = 5;

    Handler mainHandler = new Handler(Looper.getMainLooper());
    private HandlerThread handlerThread = new HandlerThread("test");
    private Handler threadHandler;

    public interface ServiceCallback {
        void getLocation(Location location);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, MapsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, "exampleServiceChannel")
                .setContentTitle("Example Service")
                .setContentText("TODO: Content Text")
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
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
        if (ActivityCompat.checkSelfPermission(ExampleService.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ExampleService.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this, threadHandler.getLooper());
        }
    }


 /*   @Override
    public void run() {
        Looper.prepare();
        serviceHandler.post(new Runnable() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(ExampleService.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ExampleService.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, ExampleService.this, serviceHandler.getLooper());
                }
            }
        });

        Looper.loop();
    }*/

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        ExampleService getService() {
            return ExampleService.this;
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d(TAG, "onLocationChanged: Lat: " + location.getLatitude() + " Lng: " + location.getLongitude());

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.getLocation(location);
            }
        });
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    public void stopTracking() {
        handlerThread.quit();
        handlerThread = null;
        locationManager.removeUpdates(this);
        locationManager = null;
    }

    public static void setCallbacks(ServiceCallback callbacks) {
        callback = callbacks;
    }
}
