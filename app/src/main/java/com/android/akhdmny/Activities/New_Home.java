package com.android.akhdmny.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.akhdmny.ApiResponse.TimeOut.OrderTimeOut;
import com.android.akhdmny.Authenticate.login;
import com.android.akhdmny.ErrorHandling.LoginApiError;
import com.android.akhdmny.MainActivity;
import com.android.akhdmny.NetworkManager.NetworkConsume;
import com.android.akhdmny.R;
import com.arsy.maps_library.MapRadar;
import com.arsy.maps_library.MapRipple;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.android.akhdmny.MainActivity.btn;
import static com.android.akhdmny.MainActivity.btn_layout;

public class New_Home extends AppCompatActivity  implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    public static final int RequestPermissionCode = 99;
    GoogleApiClient mGoogleApiClient;
    //To store longitude and latitude from map
    private double longitude;
    private double latitude;
    //Google ApiClient
    private GoogleApiClient googleApiClient;
    private String TAG = "gps";
    public static final int REQUEST_CHECK_SETTINGS = 123;
    LocationRequest mLocationRequest;
    int INTERVAL = 1000;
    int FASTEST_INTERVAL = 500;
    MapRipple mapRipple;
    MapRadar mapRadar;
    SharedPreferences prefs;
    String id ="";
    @BindView(R.id.CancelOrder)
    Button CancelOrder;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_home);
        ButterKnife.bind(this);
        id = NetworkConsume.getInstance().getDefaults("id",New_Home.this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        prefs = getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
        mapFragment.getMapAsync(this);
        //Initializing googleapi client
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        CancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeoutApi();
            }
        });
        mLocationRequest = new LocationRequest();
//        mLocationRequest.setNumUpdates(1);
//        mLocationRequest.setExpirationTime(6000);
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startRadarAnimation(LatLng latLng){
        mapRadar = new MapRadar(mMap, latLng, this);
        mapRadar.withDistance(500);
        mapRadar.withClockwiseAnticlockwiseDuration(2);
        //mapRadar.withOuterCircleFillColor(Color.parseColor("#12000000"));
        mapRadar.withOuterCircleStrokeColor(Color.parseColor("#fccd29"));
        mapRadar.withRadarColors(getResources().getColor(R.color.colorPrimaryDark),
                this.getResources().getColor(R.color.sky_blue));
        mapRadar.withOuterCircleTransparency(0.5f);
        mapRadar.withRadarTransparency(0.5f);
        mapRadar.startRadarAnimation();
    }


    private ResultCallback<LocationSettingsResult> mResultCallbackFromSettings = new ResultCallback<LocationSettingsResult>() {
        @Override
        public void onResult(LocationSettingsResult result) {
            final Status status = result.getStatus();
            //final LocationSettingsStates locationSettingsStates = result.getLocationSettingsStates();
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
                                New_Home.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException e) {
                        // Ignore the error.
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    Log.e(TAG, "Settings change unavailable. We have no way to fix the settings so we won't show the dialog.");
                    break;
            }
        }
    };


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerDragListener(this);
        //Adding a long click listener to the map
        mMap.setOnMapLongClickListener(this);

        if (checkPermission()) {
            buildGoogleApiClient();
            // Check the location settings of the user and create the callback to react to the different possibilities
            LocationSettingsRequest.Builder locationSettingsRequestBuilder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);
            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, locationSettingsRequestBuilder.build());
            result.setResultCallback(mResultCallbackFromSettings);
        } else {
            requestPermission();
        }
    }
    private void requestPermission() {

        ActivityCompat.requestPermissions(New_Home  .this, new String[]
                {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, RequestPermissionCode);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case RequestPermissionCode:

                if (grantResults.length > 0) {

                    boolean finelocation = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean coarselocation = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (finelocation && coarselocation) {

                        if (checkPermission())
                            buildGoogleApiClient();
                        Toast.makeText(New_Home.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(New_Home.this, "Permission Denied", Toast.LENGTH_LONG).show();

                    }
                }

                break;
        }
    }

    public boolean checkPermission() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED;

    }

    private void getCurrentLocation() {
        Location location = null;
        if (checkPermission()) {
            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }

        if (location != null) {
            //Getting longitude and latitude
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            //moving the map to location
            moveMap();
        }
    }
    private void moveMap() {
        //String to display current latitude and longitude
        String msg = latitude + ", " + longitude;

        //Creating a LatLng Object to store Coordinates
        LatLng latLng = new LatLng(latitude, longitude);

        //Adding marker to map
        mMap.addMarker(new MarkerOptions()
                .position(latLng) //setting position
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker))//Making the marker draggable
                .title("Current Location")); //Adding a title

        //Moving the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            startRadarAnimation(latLng);
            //Animating the camera
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        //Displaying current coordinates in toast
        //  Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
    private void TimeoutApi(){
        NetworkConsume.getInstance().setAccessKey("Bearer "+prefs.getString("access_token","12"));
        String orderId =NetworkConsume.getInstance().getDefaults("orderId",this);
        NetworkConsume.getInstance().getAuthAPI().OrderTimeOut(orderId).enqueue(new Callback<OrderTimeOut>() {
            @Override
            public void onResponse(Call<OrderTimeOut> call, Response<OrderTimeOut> response) {
                if (response.isSuccessful()){
                    OrderTimeOut timeOut = response.body();
                    Toast.makeText(New_Home.this, timeOut.getResponse().getMessage(), Toast.LENGTH_SHORT).show();
                    mMap.clear();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CurrentOrder").
                            child("User").child(id);
                    ref.child("status").setValue(6);
                    getCurrentLocation();
                    NetworkConsume.getInstance().setDefaults("orderId","",New_Home.this);
                    if (mapRadar.isAnimationRunning()){
                        mapRadar.stopRadarAnimation();
                    }
                    Intent intent = new Intent(New_Home.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                else {
                    Gson gson = new Gson();
                    NetworkConsume.getInstance().setDefaults("orderId","",New_Home.this);
                    LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
                    Toast.makeText(New_Home.this, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                    if (message.getError().getMessage().get(0).equals("Unauthorized access_token")){
                        SharedPreferences prefs = getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
                        prefs.edit().putString("access_token", "")
                                .putString("avatar","")
                                .putString("login","").commit();

                        Intent intent = new Intent(New_Home.this, login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderTimeOut> call, Throwable t) {
                NetworkConsume.getInstance().setDefaults("orderId","",New_Home.this);
                Toast.makeText(New_Home.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
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



}
