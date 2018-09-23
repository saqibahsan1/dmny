package com.android.akhdmny;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.akhdmny.Activities.DefaultDialogsActivity;
import com.android.akhdmny.Activities.DefaultMessagesActivity;
import com.android.akhdmny.Activities.MyCart;
import com.android.akhdmny.Activities.Profile;
import com.android.akhdmny.ApiResponse.OrderConfirmation;
import com.android.akhdmny.Fragments.FragmentComplaints;
import com.android.akhdmny.Fragments.FragmentNotification;
import com.android.akhdmny.Fragments.FragmentOrder;
import com.android.akhdmny.Fragments.FragmentHome;
import com.android.akhdmny.Fragments.FragmentSettings;
import com.android.akhdmny.Fragments.FargmentService;
import com.android.akhdmny.NetworkManager.NetworkConsume;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    static LinearLayout btn_layout;


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
    private int activeMenu;
    private Handler handlerSaveId = new Handler();
    private long DRAWER_CLOSE_DELAY = 350;
    private String ID_MENU_ACTIVE = "IdMenuActive";
    Button cartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        tvTitle = (TextView) toolbar.findViewById(R.id.tvTitle);
        btn_layout = findViewById(R.id.btn_layout);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if(null == savedInstanceState){
            activeMenu = R.id.home;
            setFragment(fragmentHome);

            Gson gson = new Gson();
            String json = NetworkConsume.getInstance().getDefaults("myObject",MainActivity.this);
            OrderConfirmation obj = gson.fromJson(json, OrderConfirmation.class);
            if (json == null){
                btn_layout.setVisibility(View.VISIBLE);
            }else {
                btn_layout.setVisibility(View.GONE);
            }
           // btn_layout.setVisibility(View.VISIBLE);
            tvTitle.setText("Home");

        }else{
            activeMenu = savedInstanceState.getInt(ID_MENU_ACTIVE);
            btn_layout.setVisibility(View.GONE);
        }
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

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
                        btn_layout.setVisibility(View.VISIBLE);
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
                setFragment(fargmentService);
                btn_layout.setVisibility(View.GONE);
                activeMenu = R.id.Btn_Services;
                cartButton.setVisibility(View.VISIBLE);
                tvTitle.setText("Services");
            }
        });
    }


    private void switchFragment(int activeMenu) {
        switch (activeMenu){
            case R.id.home:
                setFragment(fragmentHome);
                tvTitle.setText("Home");
               // btn_layout.setVisibility(View.VISIBLE);
                cartButton.setVisibility(View.VISIBLE);
                String json = NetworkConsume.getInstance().getDefaults("myObject",MainActivity.this);
                if (json == null){
                    btn_layout.setVisibility(View.VISIBLE);
                }else {
                    btn_layout.setVisibility(View.GONE);
                }
                break;
            case R.id.orders:
                setFragment(fragmentOrder);
                tvTitle.setText("Orders");
                btn_layout.setVisibility(View.GONE);
                break;
            case R.id.settings:
                setFragment(fragmentSettings);
                btn_layout.setVisibility(View.GONE);
                tvTitle.setText("Settings");
                cartButton.setVisibility(View.GONE);
                break;
            case R.id.complaints:
                setFragment(fragmentComplaints);
                btn_layout.setVisibility(View.GONE);
                tvTitle.setText("Complaint");
                cartButton.setVisibility(View.GONE);
                break;
            case R.id.notifications:
                setFragment(fragmentComplaints);
                btn_layout.setVisibility(View.GONE);
                tvTitle.setText("Notifications");
                cartButton.setVisibility(View.GONE);
                break;
            case R.id.contact:
               // setFragment(fragmentComplaints);
                startActivity(new Intent(MainActivity.this, DefaultMessagesActivity.class));
                btn_layout.setVisibility(View.GONE);
                cartButton.setVisibility(View.GONE);
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
