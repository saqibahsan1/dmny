package com.android.akhdmny.FireBaseNotification;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.akhdmny.Activities.Bid;
import com.android.akhdmny.Activities.Driver_Ratings;
import com.android.akhdmny.ApiResponse.AcceptModel.AcceptOrderApiModel;
import com.android.akhdmny.ApiResponse.OrderModel.OrderDetailsModel;
import com.android.akhdmny.Interfaces.ObserverInterface;
import com.android.akhdmny.MainActivity;
import com.android.akhdmny.NetworkManager.NetworkConsume;

import com.android.akhdmny.Singletons.CurrentOrder;
import com.android.akhdmny.Singletons.OrderManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
//import static com.android.akhdmny.MainActivity.btn_layout;


public class TrackerService extends Service {

    private static final String TAG = TrackerService.class.getSimpleName();
    String orderId = "";
    boolean isOpen = false;

    public ObserverInterface observerListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Toast.makeText(this, " MyService Started", Toast.LENGTH_LONG).show();
        listner();
        return START_STICKY;
    }
    @Override
    public void onCreate() {
        super.onCreate();
     //   requestLocationUpdates();

    }

    public class LocalBinder extends Binder {
        public TrackerService getServiceInstance(){
            return TrackerService.this;
        }
    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "received stop broadcast");
            // Stop the service when the notification is tapped
            unregisterReceiver(stopReceiver);
            stopSelf();
        }
    };

    private void listner(){
        String id = NetworkConsume.getInstance().getDefaults("id",TrackerService.this);
//        String id = "31";
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CurrentOrder").child("User")
                .child(id);
        OrderManager.getInstance().observer = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (Objects.equals(dataSnapshot1.getKey(), "orderId")){
                        orderId = Objects.requireNonNull(dataSnapshot1.getValue()).toString();
                    }
                    if (Objects.equals(dataSnapshot1.getKey(), "status")) {
                        if (observerListener != null) {
                            observerListener.statusChanged(dataSnapshot1.getValue().toString());
                        }
                    }
                    if (Objects.equals(dataSnapshot1.getKey(), "status") && dataSnapshot1.getValue().toString().equals("0")){

                        NetworkConsume.getInstance().setDefaults("orderId",orderId,TrackerService.this);
//                        Intent start = new Intent(TrackerService.this,New_Home.class);
//                        start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(start);
                    }
                    if (Objects.equals(dataSnapshot1.getKey(), "status") && dataSnapshot1.getValue().toString().equals("1")){
                        NetworkConsume.getInstance().setDefaults("orderId",orderId,TrackerService.this);

                            Intent start = new Intent(TrackerService.this, Bid.class);
                            start.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(start);
                    }
                    if (Objects.equals(dataSnapshot1.getKey(), "status") && dataSnapshot1.getValue().toString().equals("2")){
                        NetworkConsume.getInstance().setDefaults("orderId",orderId,TrackerService.this);

                        getOrderDetails();

                    }
                    if (Objects.equals(dataSnapshot1.getKey(), "status") && dataSnapshot1.getValue().toString().equals("3")){
                        CurrentOrder.shared = null;
                        Intent start = new Intent(TrackerService.this,Driver_Ratings.class);
                        start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        FirebaseDatabase.getInstance().getReference("CurrentOrder").child("User").child(id).child("status").setValue(6);
                        startActivity(start);

                    }
                    if (Objects.equals(dataSnapshot1.getKey(), "status") && dataSnapshot1.getValue().toString().equals("4")){
                        CurrentOrder.shared = null;
                        Intent start = new Intent(TrackerService.this,MainActivity.class);
                        FirebaseDatabase.getInstance().getReference("CurrentOrder").child("User").child(id).child("status").setValue(6);
                        Toast.makeText(TrackerService.this, "Your Order has been cancelled", Toast.LENGTH_SHORT).show();
                        start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(start);

                    }
                    if (Objects.equals(dataSnapshot1.getKey(), "status") && dataSnapshot1.getValue().toString().equals("5")){
                        CurrentOrder.shared = null;
                        Intent start = new Intent(TrackerService.this,MainActivity.class);
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CurrentOrder").
                                child("User").child(id);
                        ref.child("status").setValue(6);
                        Toast.makeText(TrackerService.this, "Your Order has been Timed Out", Toast.LENGTH_SHORT).show();
                        start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(start);

                    }

//                    * Status 0 = move to home screen then start loading map
//* status 1 = stop loading then open bid screen and show bids
//* status 2 = move to home screen then call getOrderDetails and then setup map and start tracking
//* status 3 = stop tracking and return to normal state of map and show message “your order has been delivered” and then show rating screen
//* status 4 = stop tracking and return to normal state of map and show message “your order has been cancelled”
//* status 5 = move to normal state of map and show message “Order timeout”
//* status 6 = move to normal state of map and do nothing.
//                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getOrderDetails(){
        SharedPreferences prefs = TrackerService.this.getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
//        NetworkConsume.getInstance().ShowProgress(TrackerService.this);
        NetworkConsume.getInstance().setAccessKey("Bearer " + prefs.getString("access_token", "12"));
        String orderId = NetworkConsume.getInstance().getDefaults("orderId", TrackerService.this);
        NetworkConsume.getInstance().getAuthAPI().GetOrderDetails(orderId).enqueue(new Callback<OrderDetailsModel>() {
            @Override
            public void onResponse(@NonNull Call<OrderDetailsModel> call, @NonNull Response<OrderDetailsModel> response) {
                if (response.isSuccessful()){
                    OrderDetailsModel orderDetails = response.body();
                    if (orderDetails != null) {
                        CurrentOrder.getInstance().driver = orderDetails.getResponse().getDriverInfo();
                        CurrentOrder.getInstance().user = orderDetails.getResponse().getUserInfo();
                        CurrentOrder.getInstance().order = orderDetails.getResponse().getOrderDetails();
                        CurrentOrder.getInstance().userId = orderDetails.getResponse().getUserId();
                        CurrentOrder.getInstance().driverId = orderDetails.getResponse().getDriverId();
                        CurrentOrder.getInstance().orderId = orderDetails.getResponse().getOrderDetails().getOrderId();

                        Log.i("GetOrderDetails",  "success");

                        Intent start = new Intent(TrackerService.this, MainActivity.class);
                        start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(start);

                    }else{
//                        NetworkConsume.getInstance().HideProgress(TrackerService.this);
                    }
                }else{
//                    NetworkConsume.getInstance().HideProgress(TrackerService.this);
                }
            }
            @Override
            public void onFailure(@NonNull Call<OrderDetailsModel> call, @NonNull Throwable t) {
//                NetworkConsume.getInstance().HideProgress(TrackerService.this);
                Toast.makeText(TrackerService.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.i("GetOrderDetails", t.getMessage() + " error");

            }
        });
    }


//    private void listner(){
//        String id = NetworkConsume.getInstance().getDefaults("id",TrackerService.this);
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CurrentOrder").child("User").
//                child(id);
//        ref.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
//
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
//                if (dataSnapshot.getKey().equals("orderId")){
//                    NetworkConsume.getInstance().setDefaults("orderId",dataSnapshot.getValue().toString(),TrackerService.this);
//                }
//                if (dataSnapshot.getKey().equals("status") && dataSnapshot.getValue().toString().equals("1")){
//                    Intent start = new Intent(TrackerService.this,Bid.class);
//                    start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(start);
//                }
//                if (dataSnapshot.getKey().equals("status") && dataSnapshot.getValue().toString().equals("3")){
//                    Intent start = new Intent(TrackerService.this,MainActivity.class);
//                    start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    NetworkConsume.getInstance().setDefaults("D_model",null,TrackerService.this);
//                    NetworkConsume.getInstance().setDefaults("O_model",null,TrackerService.this);
//                    NetworkConsume.getInstance().setDefaults("U_model",null,TrackerService.this);
//                    NetworkConsume.getInstance().setDefaults("orderId",null,TrackerService.this);
//                    startActivity(start);
//
//                }
//
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.w(TAG, "onCancelled", databaseError.toException());
//            }
//        });
//    }

//    private void requestLocationUpdates() {
//        LocationRequest request = new LocationRequest();
//        request.setInterval(10000);
//        request.setFastestInterval(50000);
//        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
//        final String path = "Tracking/" + 23;
//        int permission = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION);
//        if (permission == PackageManager.PERMISSION_GRANTED) {
//            SharedPreferences prefs = getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
//
//            // Request location updates and when an update is
//            // received, store the location in Firebase
//            client.requestLocationUpdates(request, new LocationCallback() {
//                @Override
//                public void onLocationResult(LocationResult locationResult) {
//                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
//                    Location location = locationResult.getLastLocation();
//                    if (location != null) {
//                        UpdateLoc(prefs.getInt("id",1),location.getLatitude(),location.getLongitude());
//                        Map<String, Double> map = new HashMap<String, Double>();
//                        map.put("latitude", location.getLatitude());
//                        map.put("longitude", location.getLongitude());
//                        map.put("heading", location.getAltitude());
//                        map.put("status", Double.valueOf("0"));
//                        Log.d(TAG, "location update " + location);
//                        ref.setValue(map);
//                    }
//                }
//            }, null);
//        }
//    }
}
