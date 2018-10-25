package com.android.akhdmny.FireBaseNotification;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.akhdmny.Activities.Bid;
import com.android.akhdmny.MainActivity;
import com.android.akhdmny.NetworkManager.Network;
import com.android.akhdmny.NetworkManager.NetworkConsume;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class TrackerService extends Service {

    private static final String TAG = TrackerService.class.getSimpleName();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
     //   requestLocationUpdates();
       // listner();

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
