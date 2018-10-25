package com.android.akhdmny;

import android.app.Fragment;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.akhdmny.Activities.Bid;
import com.android.akhdmny.Activities.Chat;
import com.android.akhdmny.Activities.DefaultDialogsActivity;
import com.android.akhdmny.Activities.DefaultMessagesActivity;
import com.android.akhdmny.Activities.MyCart;
import com.android.akhdmny.Activities.Profile;
import com.android.akhdmny.ApiResponse.LoginInsideResponse;
import com.android.akhdmny.ApiResponse.OrderConfirmation;
import com.android.akhdmny.ApiResponse.UpdateTokenResponse;
import com.android.akhdmny.ErrorHandling.LoginApiError;
import com.android.akhdmny.FireBaseNotification.TrackerService;
import com.android.akhdmny.Fragments.FragmentComplaints;
import com.android.akhdmny.Fragments.FragmentContact;
import com.android.akhdmny.Fragments.FragmentNotification;
import com.android.akhdmny.Fragments.FragmentOrder;
import com.android.akhdmny.Fragments.FragmentHome;
import com.android.akhdmny.Fragments.FragmentSettings;
import com.android.akhdmny.Fragments.FargmentService;
import com.android.akhdmny.NetworkManager.Network;
import com.android.akhdmny.NetworkManager.NetworkConsume;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String AUTH_PREF_KEY = "com.android.akhdmny.authKey";
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.navigationView)
    NavigationView navigationView;
    @BindView(R.id.MLocation)
    Button MLocation;
    TextView tvTitle;
    @BindView(R.id.Btn_Services)
    Button btn_services;
    public static Button btn;
    public static LinearLayout btn_layout;
    public static int Device_Width;

    private static final int PERMISSION_CODE = 99;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private ActionBarDrawerToggle toggle;
    private FragmentNotification fragmentNotification = new FragmentNotification();
    private FragmentHome fragmentHome = new FragmentHome();
    private FragmentOrder fragmentOrder = new FragmentOrder();
    private FargmentService fargmentService = new FargmentService();
    private FragmentSettings fragmentSettings = new FragmentSettings();
    private FragmentComplaints fragmentComplaints = new FragmentComplaints();
    private FragmentContact fragmentContact = new FragmentContact();
    private int activeMenu;
    private Handler handlerSaveId = new Handler();
    private long DRAWER_CLOSE_DELAY = 350;
    private String ID_MENU_ACTIVE = "IdMenuActive";
    Button cartButton;
    private static final String TAG = MainActivity.class.getSimpleName();

    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        Device_Width = metrics.widthPixels;

        tvTitle = (TextView) toolbar.findViewById(R.id.tvTitle);
        btn_layout = findViewById(R.id.btn_layout);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        try {

            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken", newToken);
               String id = NetworkConsume.getInstance().getDefaults("id",this);

                UpdateToken(Integer.valueOf(id),newToken);

            });

        }catch (Exception e){}

        if(null == savedInstanceState){
            activeMenu = R.id.home;
            setFragment(fragmentHome);


            btn_layout.setVisibility(View.GONE);
            tvTitle.setText(R.string.Home);

        }else{
            activeMenu = savedInstanceState.getInt(ID_MENU_ACTIVE);
            btn_layout.setVisibility(View.GONE);
        }
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        try {

            CircleImageView imageView = headerView.findViewById(R.id.user_profile_Image);
            TextView userName = headerView.findViewById(R.id.userName);
            TextView emailText = headerView.findViewById(R.id.email);
            String jsonLoin = NetworkConsume.getInstance().getDefaults("login",MainActivity.this);
            LoginInsideResponse response = gson.fromJson(jsonLoin,LoginInsideResponse.class);
            Picasso.get().load(response.getAvatar()).into(imageView);
            userName.setText(response.getName());
            emailText.setText(response.getEmail());
        }catch (Exception e){}


        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Profile.class));
                overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        cartButton = toolbar.findViewById(R.id.btn_MyCart);

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MyCart.class));
                overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
            }
        });
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close){
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (slideOffset == 0) {
                    // drawer closed
                   // invalidateOptionsMenu();
                    if (activeMenu == R.id.home) {
                        String json = NetworkConsume.getInstance().getDefaults("U_model",MainActivity.this);
                        if (json == null){
                            btn_layout.setVisibility(View.VISIBLE);

                        }else {
                            btn_layout.setVisibility(View.GONE);


                        }
                    }else {
                        btn_layout.setVisibility(View.GONE);
                    }


                } else if (slideOffset != 0)
                {
                    btn_layout.setVisibility(View.GONE);
                }
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawerLayout.setDrawerListener(toggle);

        toggle.syncState();
        clickLIstner();

        gson = new Gson();


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
                    Toast.makeText(MainActivity.this, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UpdateTokenResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void showOrHide(boolean visibility){
        if (visibility){
            btn_layout.setVisibility(View.VISIBLE);
        }else {
            btn_layout.setVisibility(View.GONE);
        }

    }
    private void clickLIstner(){
        MLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeMenu == R.id.home) {
                   fragmentHome.getCurrentLocation();
                }else {
                    Log.i("Hide","true");
                }

            }
        });
        btn_services.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,FargmentService.class));
                overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
                btn_layout.setVisibility(View.GONE);
                activeMenu = R.id.Btn_Services;
                btn.setVisibility(View.VISIBLE);

            }
        });
    }

    private void listner(){
        String id = NetworkConsume.getInstance().getDefaults("id",MainActivity.this);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CurrentOrder").child("User").
                child(id);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
                if (dataSnapshot.getKey().equals("orderId")){
                    NetworkConsume.getInstance().setDefaults("orderId",dataSnapshot.getValue().toString(),MainActivity.this);
                }
                if (dataSnapshot.getKey().equals("status") && dataSnapshot.getValue().toString().equals("1")){
                    Intent start = new Intent(MainActivity.this,Bid.class);
                    start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(start);
                }


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

    @Override
    protected void onResume() {
        super.onResume();
        activeMenu = R.id.home;
        if (activeMenu == R.id.home) {
              btn_layout.setVisibility(View.VISIBLE);
              tvTitle.setText(getString(R.string.Home));
        }else {
            Log.i("Hide","true");
        }
        listner();
    }

    private void switchFragment(int activeMenu) {
        switch (activeMenu){
            case R.id.home:
                setFragment(fragmentHome);
                tvTitle.setText(R.string.Home);
               // btn_layout.setVisibility(View.VISIBLE);

                String json = NetworkConsume.getInstance().getDefaults("U_model",MainActivity.this);
                if (json == null){
                    btn_layout.setVisibility(View.VISIBLE);
                    btn.setVisibility(View.VISIBLE);
                }else {
                    btn_layout.setVisibility(View.GONE);
                    btn.setVisibility(View.GONE);
                }
                break;
            case R.id.orders:
                setFragment(fragmentOrder);
                tvTitle.setText(R.string.order);
                btn.setVisibility(View.GONE);
                btn_layout.setVisibility(View.GONE);
                break;
            case R.id.settings:
                setFragment(fragmentSettings);
                btn_layout.setVisibility(View.GONE);
                tvTitle.setText(R.string.settings);
                btn.setVisibility(View.GONE);
                break;
            case R.id.complaints:
                setFragment(fragmentComplaints);
                btn_layout.setVisibility(View.GONE);
                tvTitle.setText(R.string.complaint);
                btn.setVisibility(View.GONE);
                break;
            case R.id.notifications:
                setFragment(fragmentNotification);
                btn_layout.setVisibility(View.GONE);
                tvTitle.setText(R.string.notifications);
                btn.setVisibility(View.GONE);
                break;
            case R.id.contact:

               setFragment(fragmentContact);
                tvTitle.setText(R.string.contact);
                btn_layout.setVisibility(View.GONE);
                btn.setVisibility(View.GONE);
                break;
            default:
                //Default
                break;
        }
    }
    private void setFragment(Fragment fragment){
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        menuItem.setChecked(true);
        activeMenu = menuItem.getItemId();
        drawerLayout.closeDrawer(GravityCompat.START);
        handlerSaveId.postDelayed(new Runnable() {
            @Override
            public void run() {
                switchFragment(menuItem.getItemId());
            }
        }, DRAWER_CLOSE_DELAY);

        return true;
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_item, menu);
        MenuItem item = menu.findItem(R.id.button_item);
        btn = item.getActionView().findViewById(R.id.btn_MyCart);
        String json = NetworkConsume.getInstance().getDefaults("U_model",MainActivity.this);
        if (json == null){
            btn_layout.setVisibility(View.VISIBLE);
            btn.setVisibility(View.VISIBLE);
        }else {
            btn_layout.setVisibility(View.GONE);
            btn.setVisibility(View.GONE);

        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MyCart.class));
                overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
            }
        });
        return true;
    }
    /**
     * to case active id menu
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ID_MENU_ACTIVE, activeMenu);
    }
}
