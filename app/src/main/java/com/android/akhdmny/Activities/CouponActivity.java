package com.android.akhdmny.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.akhdmny.Adapter.CouponAdapter;
import com.android.akhdmny.ApiResponse.Coupon.Coupon;
import com.android.akhdmny.ApiResponse.Coupon.CouponApiResponse;
import com.android.akhdmny.ErrorHandling.LoginApiError;
import com.android.akhdmny.MainActivity;
import com.android.akhdmny.NetworkManager.NetworkConsume;
import com.android.akhdmny.R;
import com.android.akhdmny.Service.ClickListener;
import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CouponActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.my_Cart_LL)
    LinearLayout my_Cart_LL;
    SharedPreferences prefs;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    TextView tvTitle;
        ArrayList<Coupon> couponArrayList = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coupoun);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        tvTitle = (TextView) toolbar.findViewById(R.id.tvTitle);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvTitle.setText(R.string.coupons);
        prefs = getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
        CouponApi();
    }

    private void CouponApi(){
        NetworkConsume.getInstance().ShowProgress(CouponActivity.this);
        NetworkConsume.getInstance().setAccessKey(prefs.getString("access_token","12"));
        NetworkConsume.getInstance().getAuthAPI().GetCoupon().enqueue(new Callback<CouponApiResponse>() {
            @Override
            public void onResponse(Call<CouponApiResponse> call, Response<CouponApiResponse> response) {
                if (response.isSuccessful()){
                    CouponApiResponse apiResponse = response.body();
                    if (apiResponse.getResponse().getCoupons().size()>0){
                        for (int i=0;i<apiResponse.getResponse().getCoupons().size();i++){
                            couponArrayList.add(apiResponse.getResponse().getCoupons().get(i));
                        }

                        CouponAdapter adapter = new CouponAdapter(CouponActivity.this, couponArrayList, new ClickListener() {
                            @Override
                            public void onPositionClicked(int position) {

                            }

                            @Override
                            public void onLongClicked(int position) {

                            }
                        });
                        NetworkConsume.getInstance().HideProgress(CouponActivity.this);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(adapter);
                    }else {
                        NetworkConsume.getInstance().SnackBarError(my_Cart_LL,CouponActivity.this,R.string.error);
                        NetworkConsume.getInstance().HideProgress(CouponActivity.this);
                    }

                }else {
                    NetworkConsume.getInstance().HideProgress(CouponActivity.this);
                    Gson gson = new Gson();
                    LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
                    Toast.makeText(CouponActivity.this, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CouponApiResponse> call, Throwable t) {

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

}
