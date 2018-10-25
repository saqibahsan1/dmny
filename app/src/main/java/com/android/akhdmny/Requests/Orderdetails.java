
package com.android.akhdmny.Requests;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Orderdetails {

    @SerializedName("cartitems")
    @Expose
    private List<Cartitem> cartitems = null;
    @SerializedName("discount")
    @Expose
    private Integer discount;
    @SerializedName("final_amount")
    @Expose
    private Integer finalAmount;
    @SerializedName("service_amount")
    @Expose
    private Integer serviceAmount;

    public List<Cartitem> getCartitems() {
        return cartitems;
    }

    public void setCartitems(List<Cartitem> cartitems) {
        this.cartitems = cartitems;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public Integer getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(Integer finalAmount) {
        this.finalAmount = finalAmount;
    }

    public Integer getServiceAmount() {
        return serviceAmount;
    }

    public void setServiceAmount(Integer serviceAmount) {
        this.serviceAmount = serviceAmount;
    }

}
