package com.android.akhdmny.Singletons;

import com.android.akhdmny.ApiResponse.AcceptModel.Driver;
import com.android.akhdmny.ApiResponse.AcceptModel.Order;
import com.android.akhdmny.ApiResponse.AcceptModel.User;

public class CurrentOrder {

    public static CurrentOrder shared;

    public Driver driver;
    public User user;
    public Order order;
    public int orderId;


    public static CurrentOrder getInstance(){
        if (shared == null){ //if there is no instance available... create new one
            shared = new CurrentOrder();
        }

        return shared;
    }

}
