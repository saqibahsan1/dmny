
package com.android.akhdmny.Requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateOrderRequest {

    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("long")
    @Expose
    private Double _long;
    @SerializedName("discount_percent")
    @Expose
    private Integer discountPercent;
    @SerializedName("tip")
    @Expose
    private Integer tip;
    @SerializedName("is_bid")
    @Expose
    private Integer isBid;
    @SerializedName("code")
    @Expose
    private String code;


    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLong() {
        return _long;
    }

    public void setLong(Double _long) {
        this._long = _long;
    }

    public Integer getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Integer discountPercent) {
        this.discountPercent = discountPercent;
    }

    public Integer getTip() {
        return tip;
    }

    public void setTip(Integer tip) {
        this.tip = tip;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }


    public Integer getIsBid() {
        return isBid;
    }

    public void setIsBid(Integer isBid) {
        this.isBid = isBid;
    }
}
