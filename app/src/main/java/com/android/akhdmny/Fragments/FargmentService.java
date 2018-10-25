package com.android.akhdmny.Fragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.akhdmny.Activities.CategoryDetailActivity;
import com.android.akhdmny.Activities.ParcelActivity;
import com.android.akhdmny.Adapter.RecyclerViewAdapter;
import com.android.akhdmny.ApiResponse.CatInsideResponse;
import com.android.akhdmny.ApiResponse.CategoriesResponse;
import com.android.akhdmny.ApiResponse.LoginApiResponse;
import com.android.akhdmny.Authenticate.Verification;
import com.android.akhdmny.ErrorHandling.LoginApiError;
import com.android.akhdmny.MainActivity;
import com.android.akhdmny.NetworkManager.NetworkConsume;
import com.android.akhdmny.R;
import com.android.akhdmny.Utils.CategoriesName;
import com.google.gson.Gson;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FargmentService extends AppCompatActivity {

    ArrayList<CatInsideResponse> categoriesNames;
    @BindView(R.id.recyclerview_id)
    RecyclerView recyclerview;

    RecyclerViewAdapter myAdapter;
    SharedPreferences prefs;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    TextView tvTitle;
    public FargmentService(){

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.services);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        tvTitle = (TextView) toolbar.findViewById(R.id.tvTitle);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvTitle.setText(R.string.categories);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
        categoriesNames = new ArrayList<>();
        CategoryApi();
        ItemClick();
    }


    private void CategoryApi(){
        NetworkConsume.getInstance().setAccessKey(prefs.getString("access_token","12"));
        NetworkConsume.getInstance().getAuthAPI().CategoryApi().enqueue(new Callback<CategoriesResponse>() {
            @Override
            public void onResponse(Call<CategoriesResponse> call, Response<CategoriesResponse> response) {
                if (response.isSuccessful()) {
                CategoriesResponse categoriesResponse = response.body();
                for (int i=0; i< categoriesResponse.getResponse().size(); i++){
                    categoriesNames.add(categoriesResponse.getResponse().get(i));
                }
                   // categoriesNames = new ArrayList((categoriesResponse.getResponse()));
                    myAdapter = new RecyclerViewAdapter(FargmentService.this,categoriesNames);
                    recyclerview.setLayoutManager(new GridLayoutManager(FargmentService.this,3));
                    recyclerview.setAdapter(myAdapter);



                }else {

                    Gson gson = new Gson();
                    LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
                    Toast.makeText(FargmentService.this, message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<CategoriesResponse> call, Throwable t) {
                Toast.makeText(FargmentService.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }
    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    private void ItemClick(){
        recyclerview.addOnItemTouchListener(new RecyclerTouchListener(FargmentService.this, recyclerview, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                NetworkConsume.getInstance().setDefaults("id",categoriesNames.get(position).getId().toString(),FargmentService.this);
                NetworkConsume.getInstance().setDefaults("image",categoriesNames.get(position).getIcon(),FargmentService.this);
                NetworkConsume.getInstance().setDefaults("title",categoriesNames.get(position).getTitle(),FargmentService.this);
                NetworkConsume.getInstance().setDefaults("colour",categoriesNames.get(position).getColor(),FargmentService.this);

                if (categoriesNames.get(position).getTitle().trim().equals("Parcels")){
                    startActivity(new Intent(FargmentService.this, ParcelActivity.class));
                    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
                }else {
                    startActivity(new Intent(FargmentService.this, CategoryDetailActivity.class));
                    overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
                }


            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }


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
