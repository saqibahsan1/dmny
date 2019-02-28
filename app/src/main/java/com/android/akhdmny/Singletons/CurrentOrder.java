package com.android.akhdmny.Singletons;

import com.android.akhdmny.ApiResponse.AcceptModel.Driver;
import com.android.akhdmny.ApiResponse.AcceptModel.Order;
import com.android.akhdmny.ApiResponse.AcceptModel.User;
import com.android.akhdmny.ApiResponse.MyOrderDetails.OrderDetail;
import com.android.akhdmny.ApiResponse.OrderModel.DriverInfo;
import com.android.akhdmny.ApiResponse.OrderModel.OrderDetails;
import com.android.akhdmny.ApiResponse.OrderModel.UserInfo;

public class CurrentOrder {

    public static CurrentOrder shared;

    public DriverInfo driver;
    public UserInfo user;
    public OrderDetails order;
    public int orderId;
    public int userId;
    public int driverId;


    public static CurrentOrder getInstance(){
        if (shared == null){ //if there is no instance available... create new one
            shared = new CurrentOrder();
        }

        return shared;
    }

}
