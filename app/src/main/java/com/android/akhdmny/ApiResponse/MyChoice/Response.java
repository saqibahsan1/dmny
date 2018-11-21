
package com.android.akhdmny.ApiResponse.MyChoice;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response {

    @SerializedName("meta")
    @Expose
    private Meta meta;
    @SerializedName("response")
    @Expose
    private Response_ response;
    @SerializedName("currency")
    @Expose
    private String currency;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public Response_ getResponse() {
        return response;
    }

    public void setResponse(Response_ response) {
        this.response = response;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

}
