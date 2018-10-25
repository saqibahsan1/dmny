package com.android.akhdmny.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.akhdmny.Adapter.BidAdapter;
import com.android.akhdmny.ApiResponse.AcceptModel.AcceptOrderApiModel;
import com.android.akhdmny.ApiResponse.DriverList;
import com.android.akhdmny.ApiResponse.DriverListInsideResponse;
import com.android.akhdmny.ApiResponse.FireBaseBids;
import com.android.akhdmny.ErrorHandling.LoginApiError;
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
import com.google.gson.Gson;

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
    SpotsDialog dialog;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    TextView tvTitle;
    SharedPreferences prefs;
    ArrayList<DriverListInsideResponse> listInsideResponses;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bid);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        tvTitle = (TextView) toolbar.findViewById(R.id.tvTitle);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvTitle.setText(R.string.bid);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.bid);
        listInsideResponses = new ArrayList<>();
        prefs = getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
      //  BidAcceptApi();
        listner();
    }

    private void listner(){
        String orderId =   NetworkConsume.getInstance().getDefaults("orderId",Bid.this);
        String id = NetworkConsume.getInstance().getDefaults("id",Bid.this);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Orders").child(orderId).child("acceptedDrivers");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                BidAcceptApi();
//                for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()) {
//                    FireBaseBids fireBaseBids = dataSnapshot1.getValue(FireBaseBids.class);
//                    listInsideResponses.add(fireBaseBids);
//                }

//                Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();
//                ArrayList<String> list = new ArrayList<>();
//                for (Map.Entry<String, String> entry : map.entrySet()) {
//                    list.add(String.valueOf(entry.getValue()));
//
//
//                }
//
//                for(String s : list) {
//                    Log.d("TAG", s);
//                }
//


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {



            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        });
    }


    private void BidAcceptApi(){
        dialog = new SpotsDialog(this,"Please wait...");
        dialog.show();
        String orderId =   NetworkConsume.getInstance().getDefaults("orderId",Bid.this);
        Network.getInstance().getAuthAPINew().BidApi(orderId).enqueue(new Callback<DriverList>() {
            @Override
            public void onResponse(Call<DriverList> call, Response<DriverList> response) {
                if (response.isSuccessful()){
                    dialog.dismiss();
                    DriverList driverList = response.body();
                    for (int i =0; i<driverList.getResponse().size(); i++) {
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


                }else {
                    dialog.dismiss();
                    Toast.makeText(Bid.this, R.string.error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DriverList> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(Bid.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void AcceptApi(int pos){
        dialog = new SpotsDialog(this,"Please wait...");
        dialog.show();
        NetworkConsume.getInstance().setAccessKey("Bearer "+prefs.getString("access_token","12"));
        NetworkConsume.getInstance().getAuthAPI().AcceptBidApi(listInsideResponses.get(pos).getOrderId(),listInsideResponses.get(pos).getBid(),
                listInsideResponses.get(pos).getDriverId()).enqueue(new Callback<AcceptOrderApiModel>() {
            @Override
            public void onResponse(Call<AcceptOrderApiModel> call, Response<AcceptOrderApiModel> response) {
                    if (response.isSuccessful()){
                        AcceptOrderApiModel acceptOrderApiModel = response.body();
                        Gson gson = new Gson();
                        String driver = gson.toJson(acceptOrderApiModel.getResponse().getDriver());
                        String user = gson.toJson(acceptOrderApiModel.getResponse().getUser());
                        String order = gson.toJson(acceptOrderApiModel.getResponse().getOrder());
                        NetworkConsume.getInstance().setDefaults("D_model",driver,Bid.this);
                        NetworkConsume.getInstance().setDefaults("U_model",user,Bid.this);
                        NetworkConsume.getInstance().setDefaults("O_model",order,Bid.this);
                        startActivity(new Intent(Bid.this,MainActivity.class));
                        finish();

                    }else {
                        dialog.hide();
                        Gson gson = new Gson();
                        LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
                        Toast.makeText(Bid.this, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                    }

            }

            @Override
            public void onFailure(Call<AcceptOrderApiModel> call, Throwable t) {
                Log.w("err",t.getMessage());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
