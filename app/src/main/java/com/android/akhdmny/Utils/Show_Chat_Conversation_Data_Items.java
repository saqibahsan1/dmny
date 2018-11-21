package com.android.akhdmny.Utils;



public class Show_Chat_Conversation_Data_Items {
    private String body;
    private String id;
    private Boolean isDelivered;
    private Boolean isRead;
    private String senderId;
    private String senderImage;
    private String senderName;
    private String time;
    private int type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getIsDelivered() {
        return isDelivered;
    }

    public void setIsDelivered(Boolean isDelivered) {
        this.isDelivered = isDelivered;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderImage() {
        return senderImage;
    }

    public void setSenderImage(String senderImage) {
        this.senderImage = senderImage;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Show_Chat_Conversation_Data_Items(String body, String id, Boolean isDelivered,
                                             Boolean isRead,String senderId,String senderImage,
                                             String senderName,String time,int type) {
        this.body = body;
        this.id = id;
        this.isDelivered = isDelivered;
        this.isRead = isRead;
        this.senderId =senderId;
        this.senderImage = senderImage;
        this.senderName= senderName;
        this.time = time;
        this.type = type;
    }



    public Show_Chat_Conversation_Data_Items()
    {
    }



    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}

