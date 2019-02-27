package com.android.akhdmny.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.akhdmny.ApiResponse.ComplainHistory.Response;
import com.android.akhdmny.R;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    //this context we will use to inflate the layout
    private Context mCtx;

    //we are storing all the products in a list
    private ArrayList<Response> historyInsideResponses;

    public HistoryAdapter(Context mCtx, ArrayList<Response> productList) {
        this.mCtx = mCtx;
        this.historyInsideResponses = productList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.history_items, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Response product = historyInsideResponses.get(position);

        //binding the data with the viewholder views
        holder.textViewTitle.setText(product.getTitle());
        holder.Tv_Date.setText(String.valueOf(product.getCreatedAt()));
    }



    @Override
    public int getItemCount() {
        return historyInsideResponses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, Tv_Date;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.titleHistory);
            Tv_Date = itemView.findViewById(R.id.timeHistory);
        }
    }
}
