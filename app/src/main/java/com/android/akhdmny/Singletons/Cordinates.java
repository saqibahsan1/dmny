package com.android.akhdmny.Singletons;

import com.android.akhdmny.Models.CoordinatesModel;

public class Cordinates {

    private static Cordinates cordinates;

    public CoordinatesModel model;
    public String currency;
    public int isBid;
    private Cordinates(){ }

    public static Cordinates getInstance(){

        if (cordinates ==null){
            cordinates = new Cordinates();
        }
        return cordinates;
    }
}
