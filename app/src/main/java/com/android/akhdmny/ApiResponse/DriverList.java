
package com.android.akhdmny.ApiResponse;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DriverList {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("response")
    @Expose
    private List<DriverListInsideResponse> response = null;
    @SerializedName("error")
    @Expose
    private Object error;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<DriverListInsideResponse> getResponse() {
        return response;
    }

    public void setResponse(List<DriverListInsideResponse> response) {
        this.response = response;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

}
