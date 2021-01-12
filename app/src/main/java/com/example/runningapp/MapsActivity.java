package com.example.runningapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Cap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.maps.model.SquareCap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;

    final int ZOOM_LEVEL = 18;

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


        /*// Default if location is not set to on
        LatLng manila = new LatLng(15, 121);
        mMap.addMarker(new MarkerOptions().position(manila).title("Marker in Manila"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(manila));*/
        startRun();
    }

    private void startRun() {

        //TODO: Make Polyline look nicer
        //Creates settings for the runner's polyline route
        PolylineOptions runRouteSettings = new PolylineOptions();
        runRouteSettings.clickable(false)
                .width(10)
                .startCap(new RoundCap())
                .jointType(JointType.ROUND)
                .color(Color.CYAN);


        /*//Testing Polyline
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(15, 121)));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(20));*/

        getLocation(runRouteSettings);
        //requestLocation(runRoute, runRouteSettings);
    }

    private void getLocation(PolylineOptions runRouteTrack) {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("trackLog", "Lat is: " + location.getLatitude() + ", "
                        + "Lng is: " + location.getLongitude());

                LatLng curLoc = new LatLng(location.getLatitude(), location.getLongitude());
                runRouteTrack.add(curLoc);
                mMap.addPolyline(runRouteTrack);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(curLoc));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM_LEVEL));
            }
        };

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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startRun();
                }
                return;
            default:
                return;
        }
    }
}