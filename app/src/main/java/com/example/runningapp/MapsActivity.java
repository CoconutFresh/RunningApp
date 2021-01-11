package com.example.runningapp;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        /*// Default if location is not set on
        LatLng manila = new LatLng(15, 121);
        mMap.addMarker(new MarkerOptions().position(manila).title("Marker in Manila"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(manila));*/
        startRun();
    }

    private void startRun() {

        //TODO: Make Polyline look nicer
        //Creates settings for the runner's polyline route
        PolylineOptions runRouteSettings = new PolylineOptions();
        runRouteSettings.clickable(false).color(Color.CYAN);

        Polyline runRoute = mMap.addPolyline(runRouteSettings);

        /*//Testing Polyline
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(15, 121)));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(20));*/

        requestLocation(runRoute, runRouteSettings);
    }

    private void requestLocation(Polyline runRoute, PolylineOptions runRouteTrack) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.d("mylog", "Lat is: " + locationResult.getLastLocation().getLatitude() + ", "
                        + "Lng is: " + locationResult.getLastLocation().getLongitude());

                LatLng curLoc = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                runRouteTrack.add(curLoc);
                Polyline runRoute = mMap.addPolyline(runRouteTrack);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(curLoc));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(20));
            }
        };

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }
}