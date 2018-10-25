
package com.android.akhdmny.Requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AcceptDriverRequest {

    @SerializedName("orderId")
    @Expose
    private String orderId;
    @SerializedName("driverId")
    @Expose
    private Integer driverId;
    @SerializedName("bid")
    @Expose
    private Integer bid;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("userInfo")
    @Expose
    private UserInfo userInfo;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public Integer getBid() {
        return bid;
    }

    public void setBid(Integer bid) {
        this.bid = bid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

}
