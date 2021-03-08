package com.example.runningapp;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class ExampleService extends Service implements LocationListener {

    private static final String TAG = "ExampleService"; //Debugging Tag

    private IBinder mBinder = new LocalBinder();

    private static ServiceCallback callback;

    public interface ServiceCallback {
        void getLocation(Location location);
    }

    //Location
    LocationManager locationManager;
    boolean isReady;

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //Permission check (We can't make the user change permissions in a service, we have that and an additional check in the activity
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        }
    }

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
        callback.getLocation(location);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    public static void setCallbacks(ServiceCallback callbacks) {
        callback = callbacks;
    }
}
