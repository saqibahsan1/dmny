
package com.android.akhdmny.ApiResponse.ParcelPost;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response {

    @SerializedName("order")
    @Expose
    private Order order;
    @SerializedName("order_item")
    @Expose
    private OrderItem orderItem;
    @SerializedName("currency")
    @Expose
    private String currency;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

}
