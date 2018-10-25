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
        View view = inflater.inflate(R.layout.driver_list, null);
        return new BidHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BidHolder holder, int position) {

        DriverListInsideResponse response = productList.get(position);
        holder.listenerRef = new WeakReference<>(listener);
        holder.textViewTitle.setText(response.getName());
        holder.TxtViewAddress.setText(mCtx.getString(R.string.bid)+" "+String.valueOf(response.getBid()));
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
        TextView textViewTitle, TxtViewAddress;
        Button btnAccept;
        WeakReference<ClickListener> listenerRef;
        ImageView imageView;

        public BidHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            TxtViewAddress = itemView.findViewById(R.id.TVAddress);
            btnAccept = itemView.findViewById(R.id.BtnAccept);
            imageView = itemView.findViewById(R.id.imgResturaunt);
        }
    }
}
