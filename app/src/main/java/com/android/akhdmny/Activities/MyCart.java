package com.android.akhdmny.Activities;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
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
import com.android.akhdmny.ApiResponse.createOrder.CreateOrderResp;
import com.android.akhdmny.Authenticate.login;
import com.android.akhdmny.ErrorHandling.LoginApiError;
import com.android.akhdmny.Fragments.ServicesActivity;
import com.android.akhdmny.MainActivity;
import com.android.akhdmny.NetworkManager.NetworkConsume;
import com.android.akhdmny.R;
import com.android.akhdmny.Requests.CreateOrderRequest;
import com.android.akhdmny.Requests.requestOrder;
import com.android.akhdmny.Singletons.Cordinates;
import com.android.akhdmny.Utils.GPSActivity;
import com.android.akhdmny.Utils.SwipeController;
import com.android.akhdmny.Utils.SwipeControllerActions;
import com.google.gson.Gson;
import com.llollox.androidtoggleswitch.widgets.ToggleSwitch;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyCart extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    ArrayList<CartItem> list;
    ArrayList<com.android.akhdmny.ApiResponse.CartApi.Response> listResponse;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.MyCartLayout)
    LinearLayout MyCartLayout;

   @BindView(R.id.my_Cart_LL)
    LinearLayout my_Cart_LL;

   @BindView(R.id.topBar)
    LinearLayout topBar;

    @BindView(R.id.cart_sendBtn)
    Button cart_sendBtn;

    @BindView(R.id.five)
    Button five;

    @BindView(R.id.ten)
    Button ten;

    @BindView(R.id.twenty)
    Button twenty;


    @BindView(R.id.plusImg)
    ImageView plusImgBtn;



    SharedPreferences prefs;
    AlertDialog alertDialog;
    GPSActivity gpsActivity;
//    SpotsDialog dialog;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toggleButtons)
    ToggleSwitch toogleButtons;

    EditText editText;
    int progress = 0;
    @BindView(R.id.total_services)
    TextView total_services;

    @BindView(R.id.discountValue)
    TextView discountValue;

    @BindView(R.id.total_tip)
    TextView total_tip;
    AVLoadingIndicatorView avi;
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
    requestOrder request;
    TextureView textureViewBlurred;
    RenderScript mRS;
    ScriptIntrinsicBlur scriptIntrinsicBlur;
    Allocation allocOriginalScreenshot, allocBlurred;
    String currency= "";
    SwipeController swipeController = null;
    double finalAmount = 0;
    int tip = 0;
    MyCartAdapter myAdapter;
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
        CartApi("");
        itemTouchLister();

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        recyclerView.setAdapter(myAdapter);
        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                super.onRightClicked(position);
                RemoveCartOrderApi(list.get(position).getId());
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });
        toogleButtons.setCheckedPosition(0);
        toogleButtons.setOnChangeListener(i -> {
            if (i == 0) {
                Cordinates.getInstance().isBid = 0;
            }else {
                Cordinates.getInstance().isBid = 1;
            }

        });
    }
    private void CartApi(String code){
        NetworkConsume.getInstance().ShowProgress(MyCart.this);
        NetworkConsume.getInstance().setAccessKey("Bearer "+prefs.getString("access_token","12"));
        NetworkConsume.getInstance().getAuthAPI().CartOrders(Cordinates.getInstance().model.getLatitude(),Cordinates.getInstance().model.getLongitude(),code).
                enqueue(new Callback<CartApiResp>() {
                    @Override
                    public void onResponse(Call<CartApiResp> call, Response<CartApiResp> response) {
                        if (response.isSuccessful()) {
                            list = new ArrayList<>();
                            NetworkConsume.getInstance().HideProgress(MyCart.this);
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
                                if (cartOrder.getResponse().getDiscountAmount() == 0){
                                    NetworkConsume.getInstance().setDefaults("coupon", "", MyCart.this);
                                }
                                listResponse.add(cartOrder.getResponse());
//                                total_tip.setText();
                                final_Total.setText(String.valueOf(cartOrder.getResponse().getFinalAmount().floatValue() + " " + cartOrder.getResponse().getCurrency()));
                                finalTotalstr = String.valueOf(cartOrder.getResponse().getFinalAmount().floatValue());
                                finalAmount = cartOrder.getResponse().getFinalAmount();
                                servicTotalstr =String.valueOf(cartOrder.getResponse().getCartItems().size());
                                discount = String.valueOf(cartOrder.getResponse().getDiscountPercent());
                                discountValue.setText(cartOrder.getResponse().getDiscountAmount()+"%");
                                currency =cartOrder.getResponse().getCurrency();
                                total_services.setText(String.valueOf(cartOrder.getResponse().getCartItems().size()));
                                myAdapter = new MyCartAdapter(MyCart.this,list,cartOrder.getResponse().getCurrency());
                                recyclerView.setAdapter(myAdapter);
                                myAdapter.notifyDataSetChanged();

                                NetworkConsume.getInstance().HideProgress(MyCart.this);
                            }catch (Exception e){
                                Log.i("err",e.getLocalizedMessage());
                            }


                        }else {
                            NetworkConsume.getInstance().HideProgress(MyCart.this);
                            Gson gson = new Gson();
                            LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
                            if (message.getError().getMessage().get(0).equals("Unauthorized access_token")){
                                SharedPreferences prefs = getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
                                prefs.edit().putString("access_token", "")
                                        .putString("avatar","")
                                        .putString("login","").commit();

                                Intent intent = new Intent(MyCart.this, login.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                            Toast.makeText(MyCart.this, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<CartApiResp> call, Throwable t) {
                        NetworkConsume.getInstance().HideProgress(MyCart.this);
                        Toast.makeText(MyCart.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
        }

    @Override
    protected void onResume() {
        super.onResume();
        String tip = NetworkConsume.getInstance().getDefaults("tip",MyCart.this);

        if (tip == null || tip.equals(""))
        {

        }else{
            try {
                setTip(Integer.parseInt(tip));
            }catch (Exception e){
                total_tip.setText(tip);
            }
        }
        String cart=  NetworkConsume.getInstance().getDefaults("coupon",  MyCart.this);
        if (cart == null || cart.equals("")){

        }else {
            CartApi(cart);
        }
    }

    public void setTip(int tip) {
        this.tip = tip;
        total_tip.setText(String.valueOf(tip));
        float tempNum = (float)this.finalAmount + (float)tip;
        this.final_Total.setText(String.valueOf(tempNum) + " " + this.currency);
    }

    private void itemTouchLister() {

        topBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyCart.this,CouponActivity.class));
            }
        });

        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTip(5);
            }
        });
        ten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTip(10);
            }
        });
        twenty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTip(20);
            }
        });

        plusImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startActivity(new Intent(MyCart.this,Add_tip.class));

            }
        });

        cart_sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder order_popup = new AlertDialog.Builder(MyCart.this);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View viewOrder = inflater.inflate(R.layout.confirm_order, null);
                Button btnDone = viewOrder.findViewById(R.id.OrderDone);
                TextView finalTotal = viewOrder.findViewById(R.id.final_Total);
                avi = viewOrder.findViewById(R.id.avi);
                TextView discountVal = viewOrder.findViewById(R.id.discountValue);
                TextView serviceTotal = viewOrder.findViewById(R.id.total_services);
                 editText = viewOrder.findViewById(R.id.et_tip);
                float tempNum = (float)finalAmount + (float)tip;
                finalTotal.setText(String.valueOf(tempNum) + " " + currency);
                discountVal.setText(discount+"%");
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

//                        if (editText.getText().toString().equals("")){
//                            Toast.makeText(MyCart.this, "Please enter a tip...", Toast.LENGTH_SHORT).show();
//                        }else {
                            createOrderApi(Integer.parseInt(discountVal.getText().toString()));
                        //}


                    }
                });

                order_popup.setView(viewOrder);
                order_popup.setCancelable(true);
                alertDialog = order_popup.create();
                alertDialog.show();
            }
        });

        recyclerView.addOnItemTouchListener(new CategoryDetailActivity.RecyclerTouchListener(this, recyclerView,
                new ServicesActivity.ClickListener() {
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
//                for (int i =0; i<list.get(position).getImages().length-1;i++) {
//                    photos.add(list.get(position).getImages());
//                }
                for (String o : list.get(position).getImages()) {
                    photos.add(o);
                }

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
                textViewprice.setText(list.get(position).getAmount()+" "+listResponse.get(position).getCurrency());
                textDialogMsg.setText(list.get(position).getDescription());
                ADD_Cart.setView(viewCart);
                ADD_Cart.setCancelable(true);
                alertDialog = ADD_Cart.create();
                alertDialog.show();

                btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        RemoveCartOrderApi(list.get(position).getId());
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

    // Performs the actual blur calculation
    void executeBlur() {

        scriptIntrinsicBlur.setInput(allocOriginalScreenshot);
        scriptIntrinsicBlur.forEach(allocBlurred);

        allocBlurred.ioSend();
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
    TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            // Once the surface is ready, execute the blur
            allocBlurred.setSurface(new Surface(surfaceTexture));

            executeBlur();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };

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

    private void createOrderApi(int discount){
        NetworkConsume.getInstance().ShowProgress(MyCart.this);
        CreateOrderRequest request = new CreateOrderRequest();
        request.setLat(Cordinates.getInstance().model.getLatitude());
        request.setLong(Cordinates.getInstance().model.getLongitude());
        request.setTip(Integer.valueOf(total_tip.getText().toString()));
        request.setDiscountPercent(discount);
        request.setIsBid(Cordinates.getInstance().isBid);
//        request.setDiscountPercent(Integer.parseInt(discount));
        request.setCode("");
        NetworkConsume.getInstance().getAuthAPI().CreateOrder(request).enqueue(new Callback<CreateOrderResp>() {
            @Override
            public void onResponse(@NonNull Call<CreateOrderResp> call, @NonNull Response<CreateOrderResp> response) {
                if (response.isSuccessful()){
                    CreateOrderResp resp = response.body();
                    assert resp != null;
                    if (resp.getStatus()) {
                        NetworkConsume.getInstance().setDefaults("orderId",""+resp.getResponse().getOrderId(),MyCart.this);
                      //  startActivity(new Intent(MyCart.this,MainActivity.class));
//                        Intent i = new Intent(MyCart.this, MainActivity.class);
//                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(i);

                        Intent start = new Intent(MyCart.this,New_Home.class);
                        start.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(start);
                        finish();
                        alertDialog.dismiss();
                        NetworkConsume.getInstance().HideProgress(MyCart.this);
                    }
                }else {

                    NetworkConsume.getInstance().HideProgress(MyCart.this);
                    Gson gson = new Gson();
                    LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
                    Toast.makeText(MyCart.this, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CreateOrderResp> call, Throwable t) {
                NetworkConsume.getInstance().HideProgress(MyCart.this);
            }
        });
    }

    private void RemoveCartOrderApi(int id){
        NetworkConsume.getInstance().ShowProgress(MyCart.this);
        NetworkConsume.getInstance().setAccessKey("Bearer "+prefs.getString("access_token","12"));
        NetworkConsume.getInstance().getAuthAPI().RemoveCartOrders(id,Cordinates.getInstance().model.getLatitude(),Cordinates.getInstance().model.getLongitude()).
                enqueue(new Callback<CartApiResp>() {
                    @Override
                    public void onResponse(Call<CartApiResp> call, Response<CartApiResp> response) {
                        if (response.isSuccessful()) {
                            listResponse = new ArrayList<>();
                            list = new ArrayList<>();
                            if (alertDialog != null) {
                                alertDialog.hide();
                            }
                            CartApiResp cartOrder = response.body();
                            for (int i=0; i< cartOrder.getResponse().getCartItems().size(); i++){
                                if (cartOrder.getResponse().getCartItems().size() == 0)
                                {

                                }else {
                                    list.add(cartOrder.getResponse().getCartItems().get(i));
                                    listResponse.add(cartOrder.getResponse());
                                }
                            }
                            MyCartAdapter myAdapter = new MyCartAdapter(MyCart.this,list,cartOrder.getResponse().getCurrency());
                            NetworkConsume.getInstance().HideProgress(MyCart.this);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(myAdapter);
                            myAdapter.notifyDataSetChanged();
                            NetworkConsume.getInstance().SnackBarSucccess(MyCartLayout,MyCart.this,R.string.remove_item);

                        }else {
                            NetworkConsume.getInstance().HideProgress(MyCart.this);
                            Gson gson = new Gson();
                            LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
                            Toast.makeText(MyCart.this, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<CartApiResp> call, Throwable t) {
                        NetworkConsume.getInstance().HideProgress(MyCart.this);
                        Toast.makeText(MyCart.this, t.getMessage(), Toast.LENGTH_SHORT).show();

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopPlaying();
    }
}
