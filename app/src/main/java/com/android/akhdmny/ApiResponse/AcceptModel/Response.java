
package com.android.akhdmny.ApiResponse.AcceptModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response {

    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("order")
    @Expose
    private Order order;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("driver")
    @Expose
    private Driver driver;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

}
