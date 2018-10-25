package com.android.akhdmny.FireBaseNotification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.akhdmny.Activities.Bid;
import com.android.akhdmny.ApiResponse.UpdateTokenResponse;
import com.android.akhdmny.ErrorHandling.LoginApiError;
import com.android.akhdmny.MainActivity;
import com.android.akhdmny.NetworkManager.Network;
import com.android.akhdmny.NetworkManager.NetworkConsume;
import com.android.akhdmny.R;
import com.android.akhdmny.Utils.UserDetails;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FCM_service extends FirebaseMessagingService {
    static int i= 0;
    String TAG = "CreateOrderResponse";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        try {

        }catch (Exception e){}
        Map<String, String> params = remoteMessage.getData();
        JSONObject object = new JSONObject(params);
        Log.e("JSON_OBJECT", object.toString());
        sendNotification(remoteMessage.getNotification().getBody());


    }


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        String id = NetworkConsume.getInstance().getDefaults("id",this);
        if ( id != null){
            UpdateToken(Integer.parseInt(id),s);
        }

    }
    private void UpdateToken(int id,String token){

        Network.getInstance().getAuthAPINew().Token(id,token).enqueue(new Callback<UpdateTokenResponse>() {
            @Override
            public void onResponse(Call<UpdateTokenResponse> call, Response<UpdateTokenResponse> response) {
                UpdateTokenResponse updateTokenResponse = response.body();
                if (response.isSuccessful()){
                    //  Toast.makeText(FCM_service.this, updateTokenResponse.getResponse().getToken(), Toast.LENGTH_SHORT).show();

                }else {
                    Gson gson = new Gson();
                    LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
                    Toast.makeText(FCM_service.this, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UpdateTokenResponse> call, Throwable t) {
                Toast.makeText(FCM_service.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, Bid.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "Akhdmny";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.splash_logo_bg)
                        .setContentTitle("Akhdmny app")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        if (notificationManager != null) {
            notificationManager.notify(i, notificationBuilder.build());
            i++;
        }
      //  notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
