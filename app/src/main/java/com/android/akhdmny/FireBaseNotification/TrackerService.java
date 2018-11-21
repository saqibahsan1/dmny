package com.android.akhdmny.FireBaseNotification;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.akhdmny.Activities.Bid;
import com.android.akhdmny.Activities.Driver_Ratings;
import com.android.akhdmny.Activities.New_Home;
import com.android.akhdmny.MainActivity;
import com.android.akhdmny.NetworkManager.Network;
import com.android.akhdmny.NetworkManager.NetworkConsume;

import com.arsy.maps_library.MapRipple;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.android.akhdmny.MainActivity.btn;
import static com.android.akhdmny.MainActivity.btn_layout;


public class TrackerService extends Service {

    private static final String TAG = TrackerService.class.getSimpleName();
    String orderId = "";
    boolean isOpen = false;
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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CurrentOrder").child("User")
                .child(id);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (dataSnapshot1.getKey().equals("orderId")){
                        orderId = dataSnapshot1.getValue().toString();
                    }
                    if (dataSnapshot1.getKey().equals("status") && dataSnapshot1.getValue().toString().equals("1")){
                        NetworkConsume.getInstance().setDefaults("orderId",orderId,TrackerService.this);

                            Intent start = new Intent(TrackerService.this, Bid.class);
                            start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(start);
                    }
                    if (dataSnapshot1.getKey().equals("status") && dataSnapshot1.getValue().toString().equals("3")){
                        Intent start = new Intent(TrackerService.this,Driver_Ratings.class);
                        start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(start);

                    }
                    if (dataSnapshot1.getKey().equals("status") && dataSnapshot1.getValue().toString().equals("0")){
                        NetworkConsume.getInstance().setDefaults("orderId",orderId,TrackerService.this);
                        Intent start = new Intent(TrackerService.this,New_Home.class);
                        start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(start);

                    }
                    if (dataSnapshot1.getKey().equals("status") && dataSnapshot1.getValue().toString().equals("4")){
                        Intent start = new Intent(TrackerService.this,MainActivity.class);
                        NetworkConsume.getInstance().setDefaults("orderId","",TrackerService.this);
                        NetworkConsume.getInstance().setDefaults("D_model","",TrackerService.this);
                        NetworkConsume.getInstance().setDefaults("O_model","",TrackerService.this);
                        NetworkConsume.getInstance().setDefaults("U_model","",TrackerService.this);
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CurrentOrder").
                                child("User").child(id);
                        ref.child("status").setValue(6);
                        Toast.makeText(TrackerService.this, "Your Order has been cancelled", Toast.LENGTH_SHORT).show();
                        start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(start);

                    }
                    if (dataSnapshot1.getKey().equals("status") && dataSnapshot1.getValue().toString().equals("5")){
                        Intent start = new Intent(TrackerService.this,MainActivity.class);
                        NetworkConsume.getInstance().setDefaults("orderId","",TrackerService.this);
                        NetworkConsume.getInstance().setDefaults("D_model","",TrackerService.this);
                        NetworkConsume.getInstance().setDefaults("O_model","",TrackerService.this);
                        NetworkConsume.getInstance().setDefaults("U_model","",TrackerService.this);
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
//        ref.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
//                if (dataSnapshot.getKey().equals("orderId")){
//                    NetworkConsume.getInstance().setDefaults("orderId",dataSnapshot.getValue().toString(),TrackerService.this);
//                }
//                if (dataSnapshot.getKey().equals("status") && dataSnapshot.getValue().toString().equals("0")){
//                    Intent start = new Intent(TrackerService.this,AcceptOrderActivity.class);
//                    start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                   startActivity(start);
//                }
//                if (dataSnapshot.getKey().equals("status") && dataSnapshot.getValue().toString().equals("2")){
//                    Intent start = new Intent(TrackerService.this,DriverOrders.class);
//                    start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(start);
//                    requestLocationUpdatesOnRoute();
//                }
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
