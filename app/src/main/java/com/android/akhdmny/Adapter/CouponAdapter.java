package com.android.akhdmny.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.akhdmny.ApiResponse.Coupon.Coupon;
import com.android.akhdmny.NetworkManager.NetworkConsume;
import com.android.akhdmny.R;
import com.android.akhdmny.Service.ClickListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.ProductViewHolder> {

    private Context mCtx;

    //we are storing all the products in a list
    private ArrayList<Coupon> productList;
    private final ClickListener listener;
    int mSelectedItem = -1;
    //getting the context and product list with constructor
    public CouponAdapter(Context mCtx, ArrayList<Coupon> productList,ClickListener clickListenert) {
        this.mCtx = mCtx;
        this.productList = productList;
        this.listener = clickListenert;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.coupon_items, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Coupon coupon = productList.get(position);
        holder.coupon.setText(coupon.getTitleEn());
        holder.listenerRef = new WeakReference<>(listener);
        holder.date.setText(coupon.getExpiry());

        holder.apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedItem = position;
                NetworkConsume.getInstance().setDefaults("coupon", coupon.getTitleEn(), mCtx);
                notifyDataSetChanged();
                holder.listenerRef.get().onPositionClicked(position);
            }


        });
        if (mSelectedItem == position) {
            holder.apply.setBackgroundColor(mCtx.getResources().getColor(R.color.green));

        }else {
            holder.apply.setBackgroundColor(mCtx.getResources().getColor(R.color.colorPrimaryDark));

        }

    }



    @Override
    public int getItemCount() {
        return  productList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        Button coupon,apply;
        TextView date;
        WeakReference<ClickListener> listenerRef;

        public ProductViewHolder(View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.validDate);
            coupon = itemView.findViewById(R.id.cpn);
            apply = itemView.findViewById(R.id.cpn_add);
        }
    }
}
