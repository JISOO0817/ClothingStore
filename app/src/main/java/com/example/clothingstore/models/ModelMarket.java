package com.example.clothingstore.models;

public class ModelMarket {
    private String uid,email,name,marketName,phone,deliveryFee,address1,address2,
            timestamp,accountType,online,marketOpen,profileImage;

    public ModelMarket(){

    }

    public ModelMarket(String uid, String email, String name, String marketName, String phone, String deliveryFee, String address1, String address2, String timestamp, String accountType, String online, String marketOpen, String profileImage) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.marketName = marketName;
        this.phone = phone;
        this.deliveryFee = deliveryFee;
        this.address1 = address1;
        this.address2 = address2;
        this.timestamp = timestamp;
        this.accountType = accountType;
        this.online = online;
        this.marketOpen = marketOpen;
        this.profileImage = profileImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(String deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getMarketOpen() {
        return marketOpen;
    }

    public void setMarketOpen(String marketOpen) {
        this.marketOpen = marketOpen;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
