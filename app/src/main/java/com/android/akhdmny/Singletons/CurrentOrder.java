package com.android.akhdmny.Singletons;

import com.android.akhdmny.ApiResponse.AcceptModel.Driver;
import com.android.akhdmny.ApiResponse.AcceptModel.Order;
import com.android.akhdmny.ApiResponse.AcceptModel.User;
import com.android.akhdmny.ApiResponse.MyOrderDetails.OrderDetail;

public class CurrentOrder {

    public static CurrentOrder shared;

    public Driver driver;
    public User user;
    public OrderDetail order;
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
