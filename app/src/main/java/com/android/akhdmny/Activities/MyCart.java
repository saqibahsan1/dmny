package com.android.akhdmny.Activities;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.akhdmny.Adapter.ImageAdapterCart;
import com.android.akhdmny.Adapter.MyCartAdapter;
import com.android.akhdmny.ApiResponse.CartApi.CartApiResp;
import com.android.akhdmny.ApiResponse.CartApi.CartItem;
import com.android.akhdmny.ApiResponse.CartInsideResponse;
import com.android.akhdmny.ApiResponse.CartOrder;
import com.android.akhdmny.ApiResponse.Cartitem;
import com.android.akhdmny.ApiResponse.CategoriesResponse;
import com.android.akhdmny.ApiResponse.OrderId;
import com.android.akhdmny.ApiResponse.createOrder.CreateOrderResp;
import com.android.akhdmny.ErrorHandling.LoginApiError;
import com.android.akhdmny.Fragments.FargmentService;
import com.android.akhdmny.MainActivity;
import com.android.akhdmny.NetworkManager.Network;
import com.android.akhdmny.NetworkManager.NetworkConsume;
import com.android.akhdmny.R;
import com.android.akhdmny.Requests.CreateOrderRequest;
import com.android.akhdmny.Requests.RequestOrder;
import com.android.akhdmny.Utils.GPSActivity;
import com.android.akhdmny.Utils.User;
import com.android.akhdmny.Utils.UserDetails;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyCart extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    ArrayList<CartItem> list;
    ArrayList<CartInsideResponse> listResponse;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.MyCartLayout)
    LinearLayout MyCartLayout;
    @BindView(R.id.cart_sendBtn)
    Button cart_sendBtn;
    SharedPreferences prefs;
    AlertDialog alertDialog;
    GPSActivity gpsActivity;
//    SpotsDialog dialog;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.avi)
    AVLoadingIndicatorView loader;
    EditText editText;
    int progress = 0;
    @BindView(R.id.total_services)
    TextView total_services;

    @BindView(R.id.final_Total)
    TextView final_Total;
    TextView tvTitle;
    SeekBar seekBar;
    MediaPlayer mediaPlayer;
    String finalTotalstr, servicTotalstr,discount;
    private Handler mHandler;
    private Timer mTimer1;
    private TimerTask mTt1;
    private Handler mTimerHandler = new Handler();
    private Runnable mRunnable;
    private ArrayList<String> photos = new ArrayList<>();
    RequestOrder request;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_cart);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        tvTitle = (TextView) toolbar.findViewById(R.id.tvTitle);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvTitle.setText(R.string.cart);
        prefs = getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.cart);
        gpsActivity = new GPSActivity(this);
        list = new ArrayList<>();
        listResponse = new ArrayList<>();
        CartApi();
        itemTouchLister();
    }

    void startAnim(){
        loader.show();
        // or avi.smoothToShow();
    }

    void stopAnim(){
        loader.hide();
        // or avi.smoothToHide();
    }

    private void CartApi(){
       startAnim();
        NetworkConsume.getInstance().setAccessKey("Bearer "+prefs.getString("access_token","12"));
        NetworkConsume.getInstance().getAuthAPI().CartOrders(gpsActivity.getLatitude(),gpsActivity.getLongitude()).
                enqueue(new Callback<CartApiResp>() {
                    @Override
                    public void onResponse(Call<CartApiResp> call, Response<CartApiResp> response) {
                        if (response.isSuccessful()) {
                            CartApiResp cartOrder = response.body();
                            for (int i=0; i< cartOrder.getResponse().getCartItems().size(); i++){
                                if (cartOrder.getResponse().getCartItems().size() == 0)
                                {

                                }else {
                                    list.add(cartOrder.getResponse().getCartItems().get(i));
                                  //  listResponse.add(cartOrder.getResponse());
                                }
                            }
                            try {

                                final_Total.setText(String.valueOf(new DecimalFormat("##").format(cartOrder.getResponse().getFinalAmount())));
                                finalTotalstr = String.valueOf(new DecimalFormat("##").format(cartOrder.getResponse().getFinalAmount()));
                                servicTotalstr =String.valueOf(new DecimalFormat("##").format(cartOrder.getResponse().getAmount()));
                                discount = String.valueOf(new DecimalFormat("##").format(cartOrder.getResponse().getDiscountAmount()));
                                total_services.setText(String.valueOf(new DecimalFormat("##").format(cartOrder.getResponse().getAmount())));
                                MyCartAdapter myAdapter = new MyCartAdapter(MyCart.this,list);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                recyclerView.setLayoutManager(mLayoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.setAdapter(myAdapter);
                                stopAnim();
                            }catch (Exception e){
                                Log.i("err",e.getLocalizedMessage());
                            }


                        }else {
                            stopAnim();
                            Gson gson = new Gson();
                            LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
                            Toast.makeText(MyCart.this, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<CartApiResp> call, Throwable t) {
                        stopAnim();
                        Toast.makeText(MyCart.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
        }
    private void itemTouchLister() {
        cart_sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder order_popup = new AlertDialog.Builder(MyCart.this);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View viewOrder = inflater.inflate(R.layout.confirm_order, null);
                Button btnDone = viewOrder.findViewById(R.id.OrderDone);
                TextView finalTotal = viewOrder.findViewById(R.id.final_Total);
                TextView discountVal = viewOrder.findViewById(R.id.discountValue);
                TextView serviceTotal = viewOrder.findViewById(R.id.total_services);
                 editText = viewOrder.findViewById(R.id.et_tip);
                finalTotal.setText(finalTotalstr);
                discountVal.setText(discount);
                serviceTotal.setText(servicTotalstr);
                Button btnCancel = viewOrder.findViewById(R.id.CancelOrder);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.hide();
                    }
                });
                btnDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (editText.getText().toString().equals("")){
                            Toast.makeText(MyCart.this, "Please enter a tip...", Toast.LENGTH_SHORT).show();
                        }else {
                            createOrderApi();
                        }


                    }
                });

                order_popup.setView(viewOrder);
                order_popup.setCancelable(true);
                alertDialog = order_popup.create();
                alertDialog.show();
            }
        });

        recyclerView.addOnItemTouchListener(new CategoryDetailActivity.RecyclerTouchListener(this, recyclerView,
                new FargmentService.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final AlertDialog.Builder ADD_Cart = new AlertDialog.Builder(MyCart.this);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View viewCart = inflater.inflate(R.layout.remove_cart, null);
                ImageView imageView = viewCart.findViewById(R.id.img_Resturaunt);
                ImageView PlayAudio = viewCart.findViewById(R.id.PlayAudio);
                seekBar = viewCart.findViewById(R.id.seek_bar);
                TextView textViewTitle = viewCart.findViewById(R.id.Tv_title);
                RecyclerView recyclerViewPopup = viewCart.findViewById(R.id.recycler_view);
                recyclerViewPopup.setLayoutManager(new LinearLayoutManager(MyCart.this, LinearLayoutManager.HORIZONTAL, false));
                recyclerViewPopup.setHasFixedSize(true);
//                for (int i =0; i<list.get(position).getImage().size()-1;i++)
//                {
                    photos.add(list.get(position).getImage());


                ImageAdapterCart imagesAdapter = new ImageAdapterCart(MyCart.this, photos);

                recyclerViewPopup.setAdapter(imagesAdapter);
                Picasso.get().load(R.drawable.place_holder).into(imageView);
                PlayAudio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (list.get(position).getVoice() == null){
                            Toast.makeText(MyCart.this, "No Voice Found", Toast.LENGTH_SHORT).show();
                        }else {
                            if(isPlaying()){
                                stopPlaying();
                            } else {
                                startPlaying(list.get(position).getVoice());
                            }
                        }
                    }
                });

                Button btnRemove = viewCart.findViewById(R.id.buttonRemoveItem);
                TextView textDialogMsg = viewCart.findViewById(R.id.textDialog);
                TextView textViewAddress = viewCart.findViewById(R.id.TV_Mob);
                TextView textViewprice = viewCart.findViewById(R.id.tv_email);
                textViewTitle.setText(list.get(position).getTitle());
                textViewAddress.setText(list.get(position).getAddress());
                textViewprice.setText(list.get(position).getAmount().toString());
                textDialogMsg.setText(list.get(position).getDescription());
                ADD_Cart.setView(viewCart);
                ADD_Cart.setCancelable(true);
                alertDialog = ADD_Cart.create();
                alertDialog.show();

                btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //RemoveCartOrderApi(list.get(position).getId());
                    }
                });
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }
    private boolean isPlaying(){
        try
        {
            return mediaPlayer != null && mediaPlayer.isPlaying();
        } catch (Exception e){
            return false;
        }
    }
    private void startPlaying(String source){
        try {
            seekBar.setVisibility(View.VISIBLE);
            mediaPlayer = new MediaPlayer();

            mediaPlayer.setOnCompletionListener(this);

            try {
                mediaPlayer.setDataSource(source);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mediaPlayer.start();
            initializeSeekBar();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onStop() {
        super.onStop();

    }
    private void stopPlaying(){

        if(mediaPlayer != null){
            try {
                mediaPlayer.stop();
                mediaPlayer.reset();
                seekBar.setProgress(0);
                seekBar.setVisibility(View.GONE);

                if(mHandler!=null){
                    mHandler.removeCallbacks(mRunnable);
                }
            } catch (Exception e){ }
        }

    }
    private void stopTimer(){
        if(mTimer1 != null){
            mTimer1.cancel();
            mTimer1.purge();

        }
    }

//    private void startTimer(){
//
////        dialog.show();
//        mTimer1 = new Timer();
//        mTt1 = new TimerTask() {
//            public void run() {
//                mTimerHandler.post(new Runnable() {
//                    public void run(){
//                        progress ++;
//                        //TODO
//                    }
//                });
//                if (progress == 80){
//                    progress = 0;
//                    stopTimer();
//                    runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            try {
//                                progress = 0;
//                                dialog.hide();
//                                alertDialog.hide();
//                                NetworkConsume.getInstance().SnackBarSucccess(MyCartLayout,MyCart.this,R.string.success_order);
//                                Intent intent = new Intent(MyCart.this, MainActivity.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                startActivity(intent);
//                                stopTimer();
//                            }catch (Exception e){}
//
//                        }
//                    });
//
//                }
//
//            }
//
//        };
//
//        mTimer1.schedule(mTt1, 2, 100);
//    }
    protected void initializeSeekBar(){
        seekBar.setMax(mediaPlayer.getDuration());

        mRunnable = new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                }
                mHandler.postDelayed(mRunnable,50);
            }
        };
        mHandler.postDelayed(mRunnable,50);
    }

    private void createOrderApi(){
        startAnim();
        CreateOrderRequest request = new CreateOrderRequest();
        request.setLat(gpsActivity.getLatitude());
        request.setLong(gpsActivity.getLongitude());
        request.setTip(Integer.valueOf(editText.getText().toString()));
        request.setDiscountPercent(4);
        NetworkConsume.getInstance().getAuthAPI().CreateOrder(request).enqueue(new Callback<CreateOrderResp>() {
            @Override
            public void onResponse(Call<CreateOrderResp> call, Response<CreateOrderResp> response) {
                if (response.isSuccessful()){
                    CreateOrderResp resp = response.body();
                    if (resp.getStatus())
                    {
                        NetworkConsume.getInstance().setDefaults("createOrder",""+resp.getResponse().getOrderId(),MyCart.this);
                        startActivity(new Intent(MyCart.this,MainActivity.class));
                        finish();
                        alertDialog.dismiss();
                    }
                }else {

                    stopAnim();
                    Gson gson = new Gson();
                    LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
                    Toast.makeText(MyCart.this, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CreateOrderResp> call, Throwable t) {

            }
        });
    }

//    private void RemoveCartOrderApi(int id){
//        dialog = new SpotsDialog(this,"Removing item please wait...",R.style.AppTheme);
//        dialog.show();
//        NetworkConsume.getInstance().setAccessKey("Bearer "+prefs.getString("access_token","12"));
//        NetworkConsume.getInstance().getAuthAPI().RemoveCartOrders(id,gpsActivity.getLatitude(),gpsActivity.getLongitude()).
//                enqueue(new Callback<CartOrder>() {
//                    @Override
//                    public void onResponse(Call<CartOrder> call, Response<CartOrder> response) {
//                        if (response.isSuccessful()) {
//                            listResponse = new ArrayList<>();
//                            list = new ArrayList<>();
//                            alertDialog.hide();
//                            CartOrder cartOrder = response.body();
//                            for (int i=0; i< cartOrder.getResponse().getCartitems().size(); i++){
//                                if (cartOrder.getResponse().getCartitems().size() == 0)
//                                {
//
//                                }else {
//                                    list.add(cartOrder.getResponse().getCartitems().get(i));
//                                    listResponse.add(cartOrder.getResponse());
//                                }
//                            }
//                            MyCartAdapter myAdapter = new MyCartAdapter(MyCart.this,list);
//                            dialog.hide();
//                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//                            recyclerView.setLayoutManager(mLayoutManager);
//                            recyclerView.setItemAnimator(new DefaultItemAnimator());
//                            recyclerView.setAdapter(myAdapter);
//                            myAdapter.notifyDataSetChanged();
//                            NetworkConsume.getInstance().SnackBarSucccess(MyCartLayout,MyCart.this,R.string.remove_item);
//
//                        }else {
//                            dialog.hide();
//                            Gson gson = new Gson();
//                            LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
//                            Toast.makeText(MyCart.this, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<CartOrder> call, Throwable t) {
//                        dialog.hide();
//                        Toast.makeText(MyCart.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//    }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopPlaying();
    }
}
