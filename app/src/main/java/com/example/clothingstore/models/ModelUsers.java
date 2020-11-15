package com.example.clothingstore.models;

public class ModelUsers {


    private String id;
    private String userName;
    private String userImage;


    public ModelUsers() {

    }

    public ModelUsers(String id, String userName, String userImage) {
        this.id = id;
        this.userName = userName;
        this.userImage = userImage;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }
}
