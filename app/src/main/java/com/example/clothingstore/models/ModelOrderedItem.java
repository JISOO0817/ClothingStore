package com.example.clothingstore.models;

public class ModelOrderedItem {

    private String pId, name, price, priceEach, quantity;

    public ModelOrderedItem(){

    }

    public ModelOrderedItem(String pId, String name, String price, String priceEach, String quantity) {
        this.pId = pId;
        this.name = name;
        this.price = price;
        this.priceEach = priceEach;
        this.quantity = quantity;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPriceEach() {
        return priceEach;
    }

    public void setPriceEach(String priceEach) {
        this.priceEach = priceEach;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
