
package com.android.akhdmny.Requests;

import com.android.akhdmny.ApiResponse.CartInsideResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class requestOrder {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("long")
    @Expose
    private String _long;
    @SerializedName("orderDetails")
    @Expose
    private CartInsideResponse orderDetails;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLong() {
        return _long;
    }

    public void setLong(String _long) {
        this._long = _long;
    }

    public CartInsideResponse getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(CartInsideResponse orderDetails) {
        this.orderDetails = orderDetails;
    }

}
