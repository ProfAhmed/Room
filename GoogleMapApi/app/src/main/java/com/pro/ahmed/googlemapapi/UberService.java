package com.pro.ahmed.googlemapapi;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;


public class UberService extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uber_service);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
        } else {
            showGPSDisabledAlertToUser();
        }


        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            final Location userCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (userCurrentLocation != null) {

                final ArrayList<LatLng> latnLngs = new ArrayList<>();
                latnLngs.add(new LatLng(userCurrentLocation.getLatitude(), userCurrentLocation.getLongitude()));
                Log.v("Laaat:", String.valueOf(userCurrentLocation.getLatitude()));
                Log.v("Laaag:", String.valueOf(userCurrentLocation.getLongitude()));
                double k = 0.001;
                double y = 0.111;
                double x_lat = userCurrentLocation.getLatitude();
                double y_long = userCurrentLocation.getLongitude();
                for (int i = 0; i < 60; i++) {
                    if (i < 30) {
                        latnLngs.add(new LatLng(userCurrentLocation.getLatitude(), userCurrentLocation.getLongitude() + k));
                        k += 0.001;

                    } else {
                        latnLngs.add(new LatLng(userCurrentLocation.getLatitude() + y, userCurrentLocation.getLongitude()));
                        y += 0.001;
                    }

                }
                final MarkerOptions markerOptions[] = {new MarkerOptions(), new MarkerOptions(), new MarkerOptions()};
                final LatLng currentUserLatLang = new LatLng(userCurrentLocation.getLatitude(), userCurrentLocation.getLongitude());
                markerOptions[0].position(currentUserLatLang);
                LatLng otherUserLatLang = new LatLng(latnLngs.get(latnLngs.size() - 1).latitude, latnLngs.get(latnLngs.size() - 1).longitude);
                markerOptions[1].position(otherUserLatLang);
                markerOptions[2].icon(BitmapDescriptorFactory.fromResource(R.drawable.badge_nt));
                final PolylineOptions line = DirectionConverter.createPolyline(UberService.this, latnLngs, 2, Color.BLUE);
                //LatLng startMovePoint = new LatLng(latnLngs.get(i[0]).latitude, latnLngs.get(i[0]).longitude);
                //markerOptions[2].position(startMovePoint);
//                mMap.addMarker(markerOptions[2]);
                //final Marker[] marker = {mMap.addMarker(markerOptions[2])};
                markerOptions[0].title("You");
                markerOptions[1].title("Location");
                markerOptions[2].title("Driver");

                mMap.addMarker(markerOptions[0]);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(userCurrentLocation.getLatitude(), userCurrentLocation.getLongitude())) // Sets the center of the map to
                        .zoom(15)                   // Sets the zoom
                        .bearing(90) // Sets the orientation of the camera to east
                        .tilt(30)    // Sets the tilt of the camera to 30 degrees
                        .build();    // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                        cameraPosition));
                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentUserLatLang, 13));
                mMap.addMarker(markerOptions[1]);
                mMap.addPolyline(line);
                final Handler handler1 = new Handler();
                final int[] end = {latnLngs.size() - 1};
                CountDownTimer downTimer = new CountDownTimer(3000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {

                        LatLng movePoint = new LatLng(latnLngs.get(latnLngs.size() - 1).latitude, latnLngs.get(latnLngs.size() - 1).longitude);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(movePoint, 16.0f));
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(movePoint) // Sets the center of the map to
                                //.bearing(180) // Sets the orientation of the camera to east
                                .tilt(30)    // Sets the tilt of the camera to 30 degrees
                                .build();    // Creates a CameraPosition from the builder
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                                cameraPosition));
                        for (int a = 0; a < latnLngs.size(); a++) {
                            handler1.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    mMap.clear();
                                    Log.v("Laaat " + end[0], String.valueOf(latnLngs.get(end[0]).latitude));
                                    Log.v("Laaang " + end[0], String.valueOf(latnLngs.get(end[0]).longitude));
                                    LatLng movePoint = new LatLng(latnLngs.get(end[0]).latitude, latnLngs.get(end[0]).longitude);

                                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(movePoint, 16.0f));

                                    //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(movePoint, 13));
                                    Marker marker = mMap.addMarker(markerOptions[1]);
                                    Marker marker2 = mMap.addMarker(markerOptions[0]);
                                    marker.showInfoWindow();
                                    marker2.showInfoWindow();
                                    markerOptions[2].position(movePoint);
                                    Marker marker3 = mMap.addMarker(markerOptions[2]);
                                    marker3.showInfoWindow();
                                    mMap.addMarker(markerOptions[2]);
                                    mMap.addMarker(markerOptions[0]);
                                    mMap.addMarker(markerOptions[1]);
                                    mMap.addPolyline(line);

                                    end[0]--;
                                }
                            }, 1000 * a);
                        }
                    }
                };
                downTimer.start();


                /*GoogleDirection.withServerKey(getResources().getString(R.string.google_maps_key))
                        .from(new LatLng(37.7681994, -122.444538))
                        .to(new LatLng(37.7749003, -122.4034934))
                        .execute(new DirectionCallback() {
                            @Override
                            public void onDirectionSuccess(Direction direction, String rawBody) {
                                if (direction.isOK()) {
                                    // Do something
                                    Log.v("Directionm: ", "Success");
                                    Leg leg = direction.getRouteList().get(0).getLegList().get(0);
                                    ArrayList<LatLng> dir_pos_list = leg.getDirectionPoint();
                                    PolylineOptions line = DirectionConverter.createPolyline(UberService.this, dir_pos_list, 5, Color.RED);
                                    mMap.addPolyline(line);
                                } else {
                                    // Do something
                                    Log.v("Directionm: ", direction.getErrorMessage());
                                    PolylineOptions line = DirectionConverter.createPolyline(UberService.this, latnLngs, 5, Color.RED);
                                    mMap.addPolyline(line);
                                }
                            }

                            @Override
                            public void onDirectionFailure(Throwable t) {
                                // Do something
                                Log.v("Directionm: ", "Falier2");

                            }
                        });*/
            }
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onConnected(null);
        } else {
            Toast.makeText(UberService.this, "No Permitions Granted", Toast.LENGTH_SHORT).show();
        }
    }


    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

}