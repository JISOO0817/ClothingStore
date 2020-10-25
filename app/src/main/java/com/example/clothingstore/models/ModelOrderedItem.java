package com.example.clothingstore.models;

public class ModelOrderedItem {

    private String pId, name, cost, price_each, quantity,image;

    public ModelOrderedItem(){

    }

    public ModelOrderedItem(String pId, String name, String cost, String price_each, String quantity,String image) {
        this.pId = pId;
        this.name = name;
        this.cost = cost;
        this.price_each = price_each;
        this.quantity = quantity;
        this.image = image;
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

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getPrice_each() {
        return price_each;
    }

    public void setPrice_each(String price_each) {
        this.price_each = price_each;
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
