package com.android.akhdmny.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.akhdmny.ApiResponse.DriverListInsideResponse;
import com.android.akhdmny.ApiResponse.FireBaseBids;
import com.android.akhdmny.R;
import com.android.akhdmny.Service.ClickListener;
import com.fuzzproductions.ratingbar.RatingBar;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class BidAdapter extends RecyclerView.Adapter<BidAdapter.BidHolder> {

    private Context mCtx;

    //we are storing all the products in a list
    private ArrayList<DriverListInsideResponse> productList;
    private final ClickListener listener;
    public BidAdapter(ArrayList<DriverListInsideResponse> listInsideResponses,Context contex,ClickListener clickListenert){
        this.mCtx = contex;
        this.productList = listInsideResponses;
        this.listener = clickListenert;
    }
    @NonNull
    @Override
    public BidHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.order_test, null);
        return new BidHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BidHolder holder, int position) {

        DriverListInsideResponse response = productList.get(position);
        holder.listenerRef = new WeakReference<>(listener);
        try {
            holder.price.setText(response.getBid()+" "+response.getCurrency());
            holder.textViewTitle.setText(response.getName());
        }catch (Exception e){

        }
        if (response.getDistance() !=null){
            holder.distance.setText(response.getDistance()+" Km");
        }
        if (response.getCar() !=null){
            holder.TxtViewAddress.setText(response.getCar());
        }
        if (response.getRate()!=null) {
            holder.ratingBar.setRating(response.getRate());
        }
        if (response.getImage() == null){
            Picasso.get().load(R.drawable.place_holder).error(R.drawable.place_holder).into(holder.imageView);
        }else {
            Picasso.get().load(response.getImage()).error(R.drawable.place_holder).into(holder.imageView);
        }
        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.listenerRef.get().onPositionClicked(position);
            }
        });

    }



    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class BidHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, TxtViewAddress,distance,price;
        Button btnAccept;
        WeakReference<ClickListener> listenerRef;
        ImageView imageView;
        RatingBar ratingBar;

        public BidHolder(View itemView) {
            super(itemView);
            distance = itemView.findViewById(R.id.distance);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            price = itemView.findViewById(R.id.price);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            TxtViewAddress = itemView.findViewById(R.id.TVAddress);
            btnAccept = itemView.findViewById(R.id.BtnAccept);
            imageView = itemView.findViewById(R.id.imgResturaunt);
        }
    }
}
