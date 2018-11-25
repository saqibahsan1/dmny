
package com.android.akhdmny.ApiResponse.CartApi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CartItem {

    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("amount")
    @Expose
    private float amount;
    @SerializedName("category_id")
    @Expose
    private Object categoryId;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("distance")
    @Expose
    private float distance;
    @SerializedName("document")
    @Expose
    private Object document;
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("images")
    @Expose
    private String[] images;
    @SerializedName("lat")
    @Expose
    private float lat;
    @SerializedName("long")
    @Expose
    private float longField;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("type")
    @Expose
    private Object type;
    @SerializedName("voice")
    @Expose
    private String voice;

    public void setAddress(String address){
        this.address = address;
    }
    public String getAddress(){
        return this.address;
    }
    public void setAmount(float amount){
        this.amount = amount;
    }
    public float getAmount(){
        return this.amount;
    }
    public void setCategoryId(Object categoryId){
        this.categoryId = categoryId;
    }
    public Object getCategoryId(){
        return this.categoryId;
    }
    public void setCreatedAt(String createdAt){
        this.createdAt = createdAt;
    }
    public String getCreatedAt(){
        return this.createdAt;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public String getDescription(){
        return this.description;
    }
    public void setDistance(float distance){
        this.distance = distance;
    }
    public float getDistance(){
        return this.distance;
    }
    public void setDocument(Object document){
        this.document = document;
    }
    public Object getDocument(){
        return this.document;
    }
    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }
    public void setImage(String image){
        this.image = image;
    }
    public String getImage(){
        return this.image;
    }
    public void setImages(String[] images){
        this.images = images;
    }
    public String[] getImages(){
        return this.images;
    }
    public void setLat(float lat){
        this.lat = lat;
    }
    public float getLat(){
        return this.lat;
    }
    public void setLongField(float longField){
        this.longField = longField;
    }
    public float getLongField(){
        return this.longField;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getTitle(){
        return this.title;
    }
    public void setType(Object type){
        this.type = type;
    }
    public Object getType(){
        return this.type;
    }
    public void setVoice(String voice){
        this.voice = voice;
    }
    public String getVoice(){
        return this.voice;
    }
}
