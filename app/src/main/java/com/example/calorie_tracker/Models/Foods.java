package com.example.calorie_tracker.Models;

public class Foods {
    private Integer foodId;
    private String name;
    private String catergory;
    private double calorie;
    private String unit;
    private double amount;
    private double fat;

    public Foods() {
    }

    public Foods(Integer foodId, String name, String catergory, double calorie, String unit, double amount, double fat) {
        this.foodId = foodId;
        this.name = name;
        this.catergory = catergory;
        this.calorie = calorie;
        this.unit = unit;
        this.amount = amount;
        this.fat = fat;
    }

    public Integer getFoodId() {
        return foodId;
    }

    public void setFoodId(Integer foodId) {
        this.foodId = foodId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCatergory() {
        return catergory;
    }

    public void setCatergory(String catergory) {
        this.catergory = catergory;
    }

    public double getCalorie() {
        return calorie;
    }

    public void setCalorie(double calorie) {
        this.calorie = calorie;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }
}
