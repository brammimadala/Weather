package com.lasys.app.geoweather.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.lasys.app.geoweather.R;

import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lasys.app.geoweather.constants.InternetPermission;
import com.lasys.app.geoweather.constants.RequestPermission;
import com.lasys.app.geoweather.model.locaddress.AddressModel;
import com.lasys.app.geoweather.sqldb.LocationDetailsDB;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MapView extends AppCompatActivity implements View.OnClickListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mgoogleMap;
    private GoogleApiClient mgoogleApiClient;
    private LocationRequest mlocationRequest;

    private Marker currentLocationUserMarker;
    private static final int Request_user_location_code = 99;

    private View view;
    private String message = "No internet connection!";
    private int duration = Snackbar.LENGTH_LONG;

    private Button _saveLocation;
    private ImageView _arrow_back_mapview;

    private LocationDetailsDB locationDetailsDB;
    private SQLiteDatabase sqLiteDatabase;

    List<Address> addresses;
    AddressModel addressModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (googleServiceAvailable()) {
            setContentView(R.layout.activity_map_view);
            view = findViewById(R.id.toolbar_mapview);
            initMap();
        } else {
            Toast.makeText(this, "Google Services Not Available", Toast.LENGTH_SHORT).show();
        }

        if (!InternetPermission.isOnline(MapView.this)) {
            showSnackbar(view, message, duration);
        }

        RequestPermission permission = new RequestPermission(this);
        permission.requestPermission();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            statusCheck();
        }

        _saveLocation = findViewById(R.id.saveLocation);
        _arrow_back_mapview = findViewById(R.id.arrow_back_mapview);

        _saveLocation.setOnClickListener(this);
        _arrow_back_mapview.setOnClickListener(this);

    }


    private boolean googleServiceAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvalable = api.isGooglePlayServicesAvailable(MapView.this);

        if (isAvalable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvalable)) {
            Dialog dialog = api.getErrorDialog(MapView.this, isAvalable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "can't connect to playservices", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    //Obtain the SupportMapFragment and get notified when the map is ready to be used.
    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
    }


    //Initializing googleapi client
    protected synchronized void buildGoogleApi() {
        mgoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        /*if (mgoogleApiClient == null) {
            mgoogleApiClient = new GoogleApiClient.Builder(MapView.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            mgoogleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            //**************************
            builder.setAlwaysShow(true); //this is the key ingredient
            //**************************

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mgoogleApiClient, builder.build());

            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can initialize location
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(
                                        MapView.this, 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }*/

        mgoogleApiClient.connect();
    }

    public String getAddressFromLocation(final double latitude, final double longitude) {

        Geocoder geocoder;
        addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        String address = "", city, state, country, postalCode, knownName, addressLine;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && addresses.size() > 0) {
            Log.i("response ==> ", "" + addresses);

            addressLine = addresses.get(0).getAddressLine(0);
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();
            knownName = addresses.get(0).getFeatureName();

            addressModel = new AddressModel(
                    addresses.get(0).getAddressLine(0),
                    addresses.get(0).getFeatureName(),
                    addresses.get(0).getAdminArea(),
                    addresses.get(0).getLocality(),
                    addresses.get(0).getPostalCode(),
                    addresses.get(0).getCountryName(),
                    String.valueOf(addresses.get(0).getLatitude()),
                    String.valueOf(addresses.get(0).getLongitude())
            );

            address = addressLine;  // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

        } else {
            Toast.makeText(this, "Unable to get the Address", Toast.LENGTH_SHORT).show();
        }

        return address;
    }


    public void showSnackbar(View view, String message, int duration) {
        Snackbar.make(view, message, duration).show();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.arrow_back_mapview: {
                finish();
                break;
            }
            case R.id.saveLocation: {
                if (InternetPermission.isOnline(MapView.this)) {

                    if (addresses != null) {

                        locationDetailsDB = new LocationDetailsDB(MapView.this);

                        sqLiteDatabase = locationDetailsDB.getWritableDatabase();

                        ContentValues cv = new ContentValues();

                        //latitude , longitude , addressLine ,  TOWN OR CITY , ADMIN ,  countryName ,  postalCode //
                        cv.put(locationDetailsDB.TABLE1_COL2, addressModel.getLatitude());
                        cv.put(locationDetailsDB.TABLE1_COL3, addressModel.getLongitude());
                        cv.put(locationDetailsDB.TABLE1_COL4, addressModel.getAddressLine());
                        cv.put(locationDetailsDB.TABLE1_COL5, addressModel.getLocality());
                        cv.put(locationDetailsDB.TABLE1_COL6, addressModel.getAdmin());
                        cv.put(locationDetailsDB.TABLE1_COL7, addressModel.getCountryName());
                        cv.put(locationDetailsDB.TABLE1_COL8, addressModel.getPostalCode());

                        long result = sqLiteDatabase.insert(locationDetailsDB.TABLE_NAME1, null, cv);

                        //Toast.makeText(this, "result == >"+result, Toast.LENGTH_SHORT).show();

                        if (result != -1) {
                            Toast.makeText(this, "Address Added Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Error response...", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        showSnackbar(view, "Address details are not found. Please Try again", duration);
                    }

                } else {
                    showSnackbar(view, message, duration);
                }
                break;
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mlocationRequest = new LocationRequest();
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mlocationRequest.setInterval(3000);
        mlocationRequest.setFastestInterval(3000);


        //Toast.makeText(this, "onConnected", Toast.LENGTH_SHORT).show();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mgoogleApiClient, mlocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        if (currentLocationUserMarker != null) {
            currentLocationUserMarker.remove();
        }

        if (location == null) {
            Toast.makeText(this, "Can't get Current Location", Toast.LENGTH_SHORT).show();
        } else {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            placeMarker(latLng);

            if (mgoogleApiClient != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mgoogleApiClient, this);
            }

        }

    }

    private void placeMarker(LatLng latLng) {
        // LatLng ll = new LatLng(latLng.latitude, latLng.longitude);
        mgoogleMap.clear();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(getAddressFromLocation(latLng.latitude, latLng.longitude));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        markerOptions.anchor(0.5f, 0.5f);
        markerOptions.snippet("Drag me to change Location");

        currentLocationUserMarker = mgoogleMap.addMarker(markerOptions);
        currentLocationUserMarker.setDraggable(true);
        currentLocationUserMarker.showInfoWindow();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        mgoogleMap.animateCamera(cameraUpdate);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mgoogleMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            buildGoogleApi();

            mgoogleMap.setMyLocationEnabled(true);

            mgoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    //Toast.makeText(MapView.this, "onMyLocationButtonClick", Toast.LENGTH_SHORT).show();
                    Location location = mgoogleMap.getMyLocation();
                    if (location != null) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        placeMarker(latLng);
                    }

                    return false;
                }
            });

            mgoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {
                }

                @Override
                public void onMarkerDrag(Marker marker) {
                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    LatLng latLng = marker.getPosition();
                    placeMarker(latLng);
                }
            });

            //Toast.makeText(this, "onMapReady", Toast.LENGTH_SHORT).show();

        }

        // Changing map type
        //googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        // googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        // googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        // googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);


        // Enable / Disable zooming controls
        mgoogleMap.getUiSettings().setZoomControlsEnabled(true);

        // Enable / Disable my location button
        mgoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Enable / Disable Compass icon
        mgoogleMap.getUiSettings().setCompassEnabled(true);

        // Enable / Disable Rotate gesture
        mgoogleMap.getUiSettings().setRotateGesturesEnabled(true);

        // Enable / Disable zooming functionality
        mgoogleMap.getUiSettings().setZoomGesturesEnabled(true);

    }

   /* private boolean checkUserLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_user_location_code);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_user_location_code);

            }
           return true;
        }
        else
        {
            return false;
        }
    }*/

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    //**
    // Aleart dialog box shows GPSLocation Enabled or not ask user to enable enable it
    // **//
    private void buildAlertMessageNoGps() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final android.app.AlertDialog alert = builder.create();
        alert.show();
    }


}
