package com.android.akhdmny.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.akhdmny.Activities.OrderDetails;
import com.android.akhdmny.Adapter.OrdersAdapter;
import com.android.akhdmny.ApiResponse.MyOrderDetails.MyOrders;
import com.android.akhdmny.ApiResponse.MyOrderDetails.Order;
import com.android.akhdmny.Authenticate.login;
import com.android.akhdmny.ErrorHandling.LoginApiError;
import com.android.akhdmny.MainActivity;
import com.android.akhdmny.NetworkManager.NetworkConsume;
import com.android.akhdmny.R;
import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by ar-android on 15/10/2015.
 */
public class FragmentOrder extends Fragment {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    ArrayList<Order> orderArrayList = new ArrayList<>();
    SharedPreferences prefs;
    public FragmentOrder() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        ButterKnife.bind(this,view);
        prefs = getActivity().getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
        touchEvent();
        GetOrdersList();
        return view;
    }

    private void GetOrdersList(){
        NetworkConsume.getInstance().ShowProgress(getActivity());
        NetworkConsume.getInstance().setAccessKey(prefs.getString("access_token","12"));
        NetworkConsume.getInstance().getAuthAPI().MyOrders().enqueue(new Callback<MyOrders>() {
            @Override
            public void onResponse(Call<MyOrders> call, Response<MyOrders> response) {
                if (response.isSuccessful()){
                    MyOrders orders = response.body();
                    for (int i=0; i<orders.getResponse().getOrders().size();i++){
                        orderArrayList.add(orders.getResponse().getOrders().get(i));
                    }
                    OrdersAdapter myAdapter = new OrdersAdapter(getActivity(),orderArrayList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(myAdapter);
                    NetworkConsume.getInstance().HideProgress(getActivity());
                }
                else {
                    NetworkConsume.getInstance().HideProgress(getActivity());
                    Gson gson = new Gson();
                    LoginApiError message=gson.fromJson(response.errorBody().charStream(),LoginApiError.class);
                    if (message.getError().getMessage().get(0).equals("Unauthorized access_token")){
                        SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.AUTH_PREF_KEY, Context.MODE_PRIVATE);
                        prefs.edit().putString("access_token", "")
                                .putString("avatar","")
                                .putString("login","").commit();

                        Intent intent = new Intent(getActivity(), login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        getActivity().finish();
                    }

                    Toast.makeText(getActivity(), message.getError().getMessage().get(0), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MyOrders> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                NetworkConsume.getInstance().HideProgress(getActivity());
            }
        });
    }
    private void touchEvent(){
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Gson gson = new Gson();
                String order = gson.toJson(orderArrayList.get(position));
//                String orderItems = gson.toJson(orderArrayList.get(position));
                NetworkConsume.getInstance().setDefaults("order",order,getActivity());
//                NetworkConsume.getInstance().setDefaults("order_details",orderItems,getActivity());
                startActivity(new Intent(getActivity(),OrderDetails.class));

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
    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

}
