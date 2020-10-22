package com.example.clothingstore.models;

public class ModelCartItem {

    String id, pId, name, price_each, price, quantity,image;

    public ModelCartItem(){

    }

    public ModelCartItem(String id, String pId, String name, String price_each, String price, String quantity, String image) {
        this.id = id;
        this.pId = pId;
        this.name = name;
        this.price_each = price_each;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice_each() {
        return price_each;
    }

    public void setPrice_each(String price_each) {
        this.price_each = price_each;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
