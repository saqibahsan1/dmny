package com.android.akhdmny.Fragments;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.akhdmny.Activities.MessageListActivity;
import com.android.akhdmny.ApiResponse.AcceptModel.Driver;
import com.android.akhdmny.ApiResponse.OrderModel.CartItem;
import com.android.akhdmny.ApiResponse.OrderModel.DriverInfo;
import com.android.akhdmny.ApiResponse.TimeOut.OrderTimeOut;
import com.android.akhdmny.ErrorHandling.LoginApiError;
import com.android.akhdmny.MainActivity;
import com.android.akhdmny.Models.CoordinatesModel;
import com.android.akhdmny.NetworkManager.NetworkConsume;
import com.android.akhdmny.R;
import com.android.akhdmny.Singletons.Cordinates;
import com.android.akhdmny.Singletons.CurrentOrder;
import com.arsy.maps_library.MapRadar;
import com.arsy.maps_library.MapRipple;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.android.akhdmny.MainActivity.btn;
//import static com.android.akhdmny.MainActivity.btn_layout;
import static com.android.akhdmny.MainActivity.showOrHide;


/**
 * Created by ar-android on 15/10/2015.
 */
public class FragmentHome extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener {
    private static final int PERMISSION_CODE = 99;
    private GoogleMap mMap;
    @BindView(R.id.driver_Image)
    CircleImageView driver_Image;
    @BindView(R.id.car_Image)
    ImageView car_Image;

    @BindView(R.id.moveAbleMarker)
    ImageView moveAbleMarker;

    @BindView(R.id.carModel)
    TextView carModel;

    @BindView(R.id.carNumber)
    TextView carNumber;

//    @BindView(R.id.mapLayout)
//    LinearLayout mapLayout;

    @BindView(R.id.driverName)
    TextView driverName;

    @BindView(R.id.driverRating)
    TextView driverRating;

    @BindView(R.id.driverCall)
    Button driverCall;

    @BindView(R.id.driverChat)
    Button driverChat;

    @BindView(R.id.current_order_view)
    ConstraintLayout nestedScrollView;

    @BindView(R.id.OrderCancel)
    Button CancelOrder;

    @BindView(R.id.MLocation)
    Button MLocation;

    @BindView(R.id.Btn_Services)
    Button btn_services;

    @BindView(R.id.btn_layout)
    LinearLayout btn_layout;

    String id = "";
    Context context;
    MapView mMapView;
    Marker currentLocationMarker;

    private GoogleApiClient mGoogleApiClient;
    private double longitude, lat = 0.0;
    private double latitude, lon = 0.0;
    private GoogleApiClient googleApiClient;
    private String TAG = "gps";
    public static final int REQUEST_CHECK_SETTINGS = 123;
    LocationRequest mLocationRequest;
    int INTERVAL = 1000;
    int FASTEST_INTERVAL = 500;
    private FragmentActivity myContext;
    private BottomSheetBehavior mBottomSheetBehaviour;
    Marker marker;
    private static CountDownTimer countDownTimer;
    MapRipple mapRipple;
    MapRadar mapRadar;
    SharedPreferences prefs;
    private Circle lastUserCircle;
    private long pulseDuration = 1000;
    private ValueAnimator lastPulseAnimator;
    private static final int PERMISSION_CALL = 22;
    String DriverNumber;
    Boolean isOrderLoaded = false;

    public FragmentHome() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        id = NetworkConsume.getInstance().getDefaults("id", getActivity());
        context = getActivity();
        mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        prefs = getActivity().getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
        mMapView.getMapAsync(this);
        mMapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = new LocationRequest();
//        mLocationRequest.setNumUpdates(1);
//        mLocationRequest.setExpirationTime(6000);
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        CancelOrder.setOnClickListener(v -> {
            CancelFragment cancelFragment = new CancelFragment(reason -> CancelOrder(reason));
            FragmentManager fm =getActivity().getSupportFragmentManager();
            cancelFragment.show(fm,cancelFragment.getTag());

        });
        driverCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callEvent();
            }
        });
        driverChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MessageListActivity.class));
            }
        });
        clickListner();

        return view;
    }

    private void clickListner() {
        MLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });
        btn_services.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ServicesActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
            }
        });
    }


    private void callEvent() {
        if (checkCallPermission()) {
            if (CurrentOrder.getInstance().driver != null) {
                DriverNumber = CurrentOrder.getInstance().driver.getPhone();
                String uri = "tel:" + DriverNumber;
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        } else {
            requestPermissionForCall();
        }

    }

    private void requestPermissionForCall() {

        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CALL);
        }

    }

    public boolean checkCallPermission() {
        int callPerm = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);

        return callPerm == PackageManager.PERMISSION_GRANTED;
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
                    makeCurrentLocationMarker();

                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    // Location settings are not satisfied. But could be fixed by showing the user
                    // a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(
                                getActivity(),
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

    public void makeCurrentLocationMarker() {
//        if (currentLocationMarker == null && CurrentOrder.shared == null) {
//            currentLocationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
//                    .anchor(0.5f, 0.5f)
//                    .title("current position")
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker))
//                    .draggable(true));
//            mMap.setMyLocationEnabled(false);
//            btn_layout.setVisibility(View.VISIBLE);
//        } else if (CurrentOrder.shared != null) {
//            if (currentLocationMarker != null) {
//                currentLocationMarker.remove();
//            }
//            if (CurrentOrder.shared.order != null) {
//
//            }
//            btn_layout.setVisibility(View.GONE);
//            mMap.setMyLocationEnabled(true);
//        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case PERMISSION_CODE:

                if (grantResults.length > 0) {

                    boolean finelocation = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean coarselocation = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (finelocation && coarselocation) {

                        if (checkPermission())
                            buildGoogleApiClient();
                        getCurrentLocation();
                        Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_LONG).show();

                    }
                }

                break;
        }

    }

    private void CancelOrder(String reason) {
        NetworkConsume.getInstance().ShowProgress(getActivity());

        NetworkConsume.getInstance().setAccessKey("Bearer " + prefs.getString("access_token", "12"));
        String orderId = NetworkConsume.getInstance().getDefaults("orderId", getActivity());
        NetworkConsume.getInstance().getAuthAPI().cancelOrderApi(orderId,reason).enqueue(new Callback<OrderTimeOut>() {
            @Override
            public void onResponse(Call<OrderTimeOut> call, Response<OrderTimeOut> response) {
                if (response.isSuccessful()) {
                    OrderTimeOut timeOut = response.body();
//                    NetworkConsume.getInstance().SnackBarSucccessStr(mapLayout,getActivity(),timeOut.getResponse().getMessage());
                    nestedScrollView.setVisibility(View.GONE);
                    showOrHide(true);
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CurrentOrder").child("User").child(id);
                    ref.child("status").setValue(6);
                    NetworkConsume.getInstance().HideProgress(getActivity());
                    NetworkConsume.getInstance().setDefaults("orderId", "", getActivity());
                    NetworkConsume.getInstance().setDefaults("D_model", "", getActivity());
                    NetworkConsume.getInstance().setDefaults("O_model", "", getActivity());
                    NetworkConsume.getInstance().setDefaults("U_model", "", getActivity());
                    if (mMap != null) { //prevent crashing if the map doesn't exist yet (eg. on starting activity)
                        mMap.clear();
                        getCurrentLocation();
                        btn.setVisibility(View.VISIBLE);

                    }
//                    if (mapRadar.isAnimationRunning()){
//                        mapRipple.stopRippleMapAnimation();
//                    }
                } else {
                    NetworkConsume.getInstance().HideProgress(getActivity());
                    Gson gson = new Gson();
                    LoginApiError message = gson.fromJson(response.errorBody().charStream(), LoginApiError.class);
                    Toast.makeText(getActivity(), message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderTimeOut> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                NetworkConsume.getInstance().HideProgress(getActivity());
            }
        });
    }

    protected Marker createMarker(double latitude, double longitude, String title,int res_id) {

        return marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(res_id))
                .draggable(true));
    }

    public void getCurrentLocation() {
        LatLng latLng = new LatLng(latitude, longitude);
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        mMap.animateCamera(location);
    }

    private void listener(String id) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tracking").child(id);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (dataSnapshot1.getKey().equals("latitude")) {
                        lat = Double.valueOf(dataSnapshot1.getValue().toString());
                    }
                    if (dataSnapshot1.getKey().equals("longitude")) {
                        lon = Double.valueOf(dataSnapshot1.getValue().toString());
                    }
                    if (lat != 0.0 && lon != 0.0) {
                        drawRoute(lat, lon);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void drawRoute(double lat, double lng) {
        PolylineOptions lineOptions = new PolylineOptions();
        Polyline polyline;
        List<LatLng> points = new ArrayList<>();
        if (marker == null) {
//            marker.remove();
            createMarker(lat, lng, "driver",R.drawable.car_icon);

        }
        LatLng latLng = new LatLng(lat, lng);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        lineOptions.add(new LatLng(lat, lng));
        points.add(latLng);


        // createMarker(lat,lng,"Driver");

        lineOptions.width(12);
        lineOptions.addAll(points);
        lineOptions.clickable(true);
        lineOptions.color(Color.RED);
        polyline = mMap.addPolyline(lineOptions);
        points = polyline.getPoints();
        points.add(latLng);
        polyline.setPoints(points);

        mMap.addPolyline(lineOptions);
    }


    private void setupOrder(){
        if (CurrentOrder.shared == null ){
            setupOrderFields(false);
        }else{
            setupOrderFields(true);
        }
    }
    void showMarker(){
        for (CartItem cartItem : CurrentOrder.shared.order.getCartItems()) {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(cartItem.getLat(), cartItem.getLong()))
                    .anchor(0.5f, 0.5f)
                    .title(cartItem.getTitle())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.market_marker))
                    .draggable(true));
        }
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(CurrentOrder.shared.user.getLat(), CurrentOrder.shared.user.getLong()))
                .anchor(0.5f, 0.5f)
                .title("Drop off")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker))
                .draggable(true));

    }

    private void setupOrderFields(Boolean isVisible){
        LatLng latLng = new LatLng(latitude, longitude);
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        mMap.animateCamera(location);
        if(isVisible) {
            DriverInfo obj = CurrentOrder.getInstance().driver;
            if(obj != null) {
                moveAbleMarker.setVisibility(View.GONE);
                DriverNumber = obj.getPhone();
                driverName.setText(obj.getName());
                driverRating.setText("5/" + obj.getRating());
                Picasso.get().load(obj.getAvatar()).error(R.drawable.dummy_image).fit().into(driver_Image);
                driver_Image.setBorderColor(Color.BLACK);
                driver_Image.setBorderWidth(1);
                carModel.setText(obj.getCarCompany() + " " + obj.getCarModel() + " " + obj.getCarColor());
                carNumber.setText(obj.getCarNo());
                listener(String.valueOf(obj.getId()));
//                if (currentLocationMarker == null) {
//                    currentLocationMarker.remove();
//                }
                showMarker();
                mMap.setMyLocationEnabled(true);
                btn_layout.setVisibility(View.GONE);
                btn.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.VISIBLE);
            }else{
                mMap.setMyLocationEnabled(false);
                btn_layout.setVisibility(View.VISIBLE);
                btn.setVisibility(View.VISIBLE);
                nestedScrollView.setVisibility(View.GONE);
                if (currentLocationMarker != null) {
                    currentLocationMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                            .anchor(0.5f, 0.5f)
                            .title("current position")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker))
                            .draggable(true));
                }
            }
        }else{
            mMap.setMyLocationEnabled(false);
            btn_layout.setVisibility(View.VISIBLE);
            btn.setVisibility(View.VISIBLE);
            nestedScrollView.setVisibility(View.GONE);
            if (currentLocationMarker != null) {
                currentLocationMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                        .anchor(0.5f, 0.5f)
                        .title("current position")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker))
                        .draggable(true));
            }
        }
    }

//    private void moveMap() {
//        LatLng latLng = new LatLng(latitude, longitude);
//        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(latLng, 17);
//        mMap.animateCamera(location);
//
//        if (!isOrderLoaded) {
////            isOrderLoaded = true;
//            try {
//                Gson gson = new Gson();
//                String d_model = NetworkConsume.getInstance().getDefaults("D_model", getActivity());
//                String o_model = NetworkConsume.getInstance().getDefaults("O_model", getActivity());
//                String u_model = NetworkConsume.getInstance().getDefaults("U_model", getActivity());
//
//                if (Objects.equals(d_model, "")) {
//
//                    nestedScrollView.setVisibility(View.GONE);
//                    btn_layout.setVisibility(View.VISIBLE);
//                    btn.setVisibility(View.VISIBLE);
//                    if (currentLocationMarker == null) {
//                        currentLocationMarker = mMap.addMarker(new MarkerOptions().position(latLng)
//                                .anchor(0.5f, 0.5f)
//                                .title("current position")
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker))
//                                .draggable(true));
//                        mMap.setMyLocationEnabled(false);
//                    }
//                } else {
//
//                    if (currentLocationMarker != null) {
//                        currentLocationMarker.remove();
//                    }
//
//                    btn_layout.setVisibility(View.GONE);
//                    nestedScrollView.setVisibility(View.VISIBLE);
//                    btn.setVisibility(View.INVISIBLE);
//
//                    Driver obj = gson.fromJson(d_model, Driver.class);
//                    DriverNumber = obj.getPhone();
//                    User user = gson.fromJson(u_model, User.class);
//                    listener(String.valueOf(obj.getId()));
//                    createMarker(obj.getLat(), obj.getLong(), obj.getName());
//                    driver_Image.setBorderColor(Color.BLACK);
//                    driver_Image.setBorderWidth(1);
//                    Picasso.get()
//                            .load(obj.getAvatar()).error(R.drawable.dummy_image)
//                            .fit()
//                            .into(driver_Image);
//
//                    driverName.setText(obj.getName());
//                    driverRating.setText("5/" + obj.getRating());
//                    carModel.setText(obj.getCarCompany() + " " + obj.getCarModel()
//                            + " " + obj.getCarColor());
//                    carNumber.setText(obj.getCarNo());
//                }
//            } catch (Exception e) {
//                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
//                nestedScrollView.setVisibility(View.GONE);
//                btn_layout.setVisibility(View.VISIBLE);
//                btn.setVisibility(View.VISIBLE);
//                if (currentLocationMarker == null) {
//                    currentLocationMarker = mMap.addMarker(new MarkerOptions().position(latLng)
//                            .anchor(0.5f, 0.5f)
//                            .title("current position")
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker))
//                            .draggable(true));
//                    mMap.setMyLocationEnabled(false);
//                }
//            }
//        }
//    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
            Location location = null;
            if (checkPermission()) {
                location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            }
            if (location != null) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                setupOrder();
                getCurrentLocation();
            }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // For showing a move to my location button
        //Setting onMarkerDragListener to track the marker drag
        googleMap.setOnCameraChangeListener(cameraPosition -> {
            CoordinatesModel model = new CoordinatesModel();
            model.setLatitude(cameraPosition.target.latitude);
            model.setLongitude(cameraPosition.target.longitude);
            Cordinates.getInstance().model = model;
        });
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapLongClickListener(this);

        if (checkPermission()) {
            buildGoogleApiClient();
            mMap.getUiSettings().setScrollGesturesEnabled(true);
            mMap.getUiSettings().setAllGesturesEnabled(true);
//            mMap.setMyLocationEnabled(true);
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//
//                LatLng sydney = new LatLng(latitude, longitude);
//                mMap.addMarker(new MarkerOptions().position(sydney).icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker)).
//                        title("Driver"));
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//                mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
//            }
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]
                    {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }, PERMISSION_CODE);
        }

    }

    public boolean checkPermission() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED;

    }

    @Override
    public void onAttach(Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        googleApiClient.connect();
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        googleApiClient.disconnect();
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {


    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        //Clearing all the markers
        mMap.clear();

        //Adding a new marker to the current pressed position we are also making the draggable true
//        mMap.addMarker(new MarkerOptions()
//                .position(latLng)
//                .draggable(true));

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;

        //Moving the map
//        moveMap();

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }
}
