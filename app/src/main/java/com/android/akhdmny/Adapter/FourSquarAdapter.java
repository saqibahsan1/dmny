package com.android.akhdmny.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.akhdmny.ApiResponse.MyChoice.Venue;
import com.android.akhdmny.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class FourSquarAdapter extends RecyclerView.Adapter<FourSquarAdapter.ProductViewHolder> {


    //this context we will use to inflate the layout
    private Context mCtx;

    //we are storing all the products in a list
    private ArrayList<Venue> productList;
    String currency;

    //getting the context and product list with constructor
    public FourSquarAdapter(Context mCtx, ArrayList<Venue> productList,String currency) {
        this.mCtx = mCtx;
        this.productList = productList;
        this.currency = currency;
    }

    @Override
    public FourSquarAdapter.ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.category_details_items, null);
        return new FourSquarAdapter.ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FourSquarAdapter.ProductViewHolder holder, int position) {
        //getting the product of the specified position
        Venue product = productList.get(position);

        //binding the data with the viewholder views
        holder.textViewTitle.setText(product.getName());
        holder.TxtViewAddress.setText(product.getLocation().getFormattedAddress().get(0)+", "+product.getLocation().getDistance()+" Km");
        holder.textViewPrice.setText(new DecimalFormat("##").format(product.getAmount())+" "+currency);
        Picasso.get().load(R.drawable.place_holder).error(R.drawable.place_holder).into(holder.imageView);


    }


    @Override
    public int getItemCount() {
        return productList.size();
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle, TxtViewAddress, textViewPrice;
        ImageView imageView;

        public ProductViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            TxtViewAddress = itemView.findViewById(R.id.TVAddress);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            imageView = itemView.findViewById(R.id.imgResturaunt);
        }
    }

}
