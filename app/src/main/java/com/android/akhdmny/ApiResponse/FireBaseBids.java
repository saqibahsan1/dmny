
package com.android.akhdmny.ApiResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FireBaseBids {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("bid")
    @Expose
    private double bid;
    @SerializedName("driverId")
    @Expose
    private Integer driverId;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("orderId")
    @Expose
    private Integer orderId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBid() {
        return bid;
    }

    public void setBid(double bid) {
        this.bid = bid;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

}
