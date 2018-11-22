package com.android.akhdmny.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.akhdmny.Adapter.BidAdapter;
import com.android.akhdmny.ApiResponse.AcceptModel.AcceptOrderApiModel;
import com.android.akhdmny.ApiResponse.DriverList;
import com.android.akhdmny.ApiResponse.DriverListInsideResponse;
import com.android.akhdmny.ApiResponse.FireBaseBids;
import com.android.akhdmny.ApiResponse.TimeOut.OrderTimeOut;
import com.android.akhdmny.Authenticate.login;
import com.android.akhdmny.ErrorHandling.LoginApiError;
import com.android.akhdmny.FireBaseNotification.TrackerService;
import com.android.akhdmny.MainActivity;
import com.android.akhdmny.NetworkManager.Network;
import com.android.akhdmny.NetworkManager.NetworkConsume;
import com.android.akhdmny.R;
import com.android.akhdmny.Service.ClickListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Bid extends AppCompatActivity {
    private static final String TAG = Bid.class.getSimpleName();
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.CancelOrder)
    Button CancelOrder;
    TextView tvTitle;
    SharedPreferences prefs;
    BidAdapter adapter;
    String id = "";
    ArrayList<DriverListInsideResponse> listInsideResponses;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bid);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        id = NetworkConsume.getInstance().getDefaults("id", Bid.this);
        tvTitle = (TextView) toolbar.findViewById(R.id.tvTitle);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvTitle.setText(R.string.bid);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.bid);
        listInsideResponses = new ArrayList<>();
        prefs = getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
        //  BidAcceptApi();
        adapter = new BidAdapter(listInsideResponses, Bid.this, new ClickListener() {
            @Override
            public void onPositionClicked(int position) {
                //  Toast.makeText(Bid.this, "item at: "+position, Toast.LENGTH_SHORT).show();
                AcceptApi(position);
            }

            @Override
            public void onLongClicked(int position) {

            }
        });
        CancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeoutApi();
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        listner();
    }

    private void TimeoutApi() {
        NetworkConsume.getInstance().ShowProgress(Bid.this);
        NetworkConsume.getInstance().setAccessKey("Bearer " + prefs.getString("access_token", "12"));
        String orderId = NetworkConsume.getInstance().getDefaults("orderId", this);
        NetworkConsume.getInstance().getAuthAPI().OrderTimeOut(orderId).enqueue(new Callback<OrderTimeOut>() {
            @Override
            public void onResponse(Call<OrderTimeOut> call, Response<OrderTimeOut> response) {
                if (response.isSuccessful()) {
                    OrderTimeOut timeOut = response.body();
                    Toast.makeText(Bid.this, timeOut.getResponse().getMessage(), Toast.LENGTH_SHORT).show();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CurrentOrder").
                            child("User").child(id);
                    ref.child("status").setValue(6);

                    NetworkConsume.getInstance().setDefaults("orderId", "", Bid.this);
                    NetworkConsume.getInstance().setDefaults("D_model", null, Bid.this);
                    NetworkConsume.getInstance().setDefaults("O_model", null, Bid.this);
                    NetworkConsume.getInstance().setDefaults("U_model", null, Bid.this);
                    NetworkConsume.getInstance().HideProgress(Bid.this);
                    Intent intent = new Intent(Bid.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Gson gson = new Gson();
                    NetworkConsume.getInstance().HideProgress(Bid.this);
                    NetworkConsume.getInstance().setDefaults("orderId", "", Bid.this);
                    LoginApiError message = gson.fromJson(response.errorBody().charStream(), LoginApiError.class);
                    Toast.makeText(Bid.this, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                    if (message.getError().getMessage().get(0).equals("Unauthorized access_token")) {
                        SharedPreferences prefs = getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
                        prefs.edit().putString("access_token", "")
                                .putString("avatar", "")
                                .putString("login", "").commit();

                        Intent intent = new Intent(Bid.this, login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderTimeOut> call, Throwable t) {
                NetworkConsume.getInstance().HideProgress(Bid.this);
                NetworkConsume.getInstance().setDefaults("orderId", "", Bid.this);
                Toast.makeText(Bid.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void listner() {
        String orderId = NetworkConsume.getInstance().getDefaults("orderId", Bid.this);
        String id = NetworkConsume.getInstance().getDefaults("id", Bid.this);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Orders").child(orderId).child("acceptedDrivers");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listInsideResponses.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    // BidAcceptApi();
                    DriverListInsideResponse insideResponse = dataSnapshot1.getValue(DriverListInsideResponse.class);
                    listInsideResponses.add(insideResponse);
//                    recyclerView.notifyAll();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void BidAcceptApi() {
        NetworkConsume.getInstance().ShowProgress(Bid.this);
        String orderId = NetworkConsume.getInstance().getDefaults("orderId", Bid.this);
        Network.getInstance().getAuthAPINew().BidApi(orderId).enqueue(new Callback<DriverList>() {
            @Override
            public void onResponse(Call<DriverList> call, Response<DriverList> response) {
                if (response.isSuccessful()) {
                    NetworkConsume.getInstance().HideProgress(Bid.this);
                    DriverList driverList = response.body();
                    for (int i = 0; i < driverList.getResponse().size(); i++) {
                        listInsideResponses.add(driverList.getResponse().get(i));
                    }
                    BidAdapter adapter = new BidAdapter(listInsideResponses, Bid.this, new ClickListener() {
                        @Override
                        public void onPositionClicked(int position) {
                            //  Toast.makeText(Bid.this, "item at: "+position, Toast.LENGTH_SHORT).show();
                            AcceptApi(position);
                        }

                        @Override
                        public void onLongClicked(int position) {

                        }
                    });
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(adapter);


                } else {
                    NetworkConsume.getInstance().HideProgress(Bid.this);
                    Toast.makeText(Bid.this, R.string.error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DriverList> call, Throwable t) {
                NetworkConsume.getInstance().HideProgress(Bid.this);
                Toast.makeText(Bid.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void AcceptApi(int pos) {
        NetworkConsume.getInstance().ShowProgress(Bid.this);
        NetworkConsume.getInstance().setAccessKey("Bearer " + prefs.getString("access_token", "12"));
        NetworkConsume.getInstance().getAuthAPI().AcceptBidApi(listInsideResponses.get(pos).getOrderId(), listInsideResponses.get(pos).getBid(),
                listInsideResponses.get(pos).getDriverId()).enqueue(new Callback<AcceptOrderApiModel>() {
            @Override
            public void onResponse(Call<AcceptOrderApiModel> call, Response<AcceptOrderApiModel> response) {
                if (response.isSuccessful()) {
                    AcceptOrderApiModel acceptOrderApiModel = response.body();
                    Gson gson = new Gson();
                    String driver = gson.toJson(acceptOrderApiModel.getResponse().getDriver());
                    String user = gson.toJson(acceptOrderApiModel.getResponse().getUser());
                    String order = gson.toJson(acceptOrderApiModel.getResponse().getOrder());
                    NetworkConsume.getInstance().setDefaults("D_model", driver, Bid.this);
                    NetworkConsume.getInstance().setDefaults("U_model", user, Bid.this);
                    NetworkConsume.getInstance().setDefaults("O_model", order, Bid.this);

//                    startActivity(new Intent(Bid.this, MainActivity.class));
                    finish();

                    NetworkConsume.getInstance().HideProgress(Bid.this);

                } else {
                    NetworkConsume.getInstance().HideProgress(Bid.this);
                    Gson gson = new Gson();
                    LoginApiError message = gson.fromJson(response.errorBody().charStream(), LoginApiError.class);
                    Toast.makeText(Bid.this, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<AcceptOrderApiModel> call, Throwable t) {
                Log.w("err", t.getMessage());
                NetworkConsume.getInstance().HideProgress(Bid.this);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
////            case android.R.id.home:
////
////                finish();
////                return true;
////            default:
        return super.onOptionsItemSelected(item);
//        }

    }
}
