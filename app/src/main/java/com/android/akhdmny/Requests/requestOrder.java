package com.android.akhdmny.Requests;

import com.android.akhdmny.ApiResponse.CartInsideResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class requestOrder {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public CartInsideResponse getResponse() {
        return response;
    }

    public void setResponse(CartInsideResponse response) {
        this.response = response;
    }

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("lat")
    @Expose
    private double lat;
    @SerializedName("long")
    @Expose
    private double longitude;

    @SerializedName("orderDetails")
    @Expose
    private CartInsideResponse response;
}
