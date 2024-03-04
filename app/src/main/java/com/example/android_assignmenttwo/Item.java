package com.example.android_assignmenttwo;

public class Item {
    private String itemName;
    private int amount;
    private float pricePerOne;
    private float totalPrice;

    public Item(){
    }

    public Item(String itemName, int amount, float pricePerOne){
        this.itemName = itemName;
        this.amount = amount;
        this.pricePerOne = pricePerOne;
        this.totalPrice = amount * pricePerOne;
    }

    public String getItemName(){
        return itemName;
    }

    public int getAmount(){
        return amount;
    }

    public void setAmount(int val){
        amount = val;
        updateTotalPrice();
    }

    public void setPricePerOne(float val){
        pricePerOne = val;
        updateTotalPrice();
    }

    public float getTotalPrice(){
        return totalPrice;
    }

    public void updateTotalPrice(){
        this.totalPrice = this.amount * this.pricePerOne;
    }
}
