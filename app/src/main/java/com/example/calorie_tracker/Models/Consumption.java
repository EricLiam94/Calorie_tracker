package com.example.calorie_tracker.Models;

public class Consumption {
    protected ConsumptionPK consumptionPK;
    private int quantity;
    private Foods foods;
    private Users users;

    public Consumption(ConsumptionPK consumptionPK, int quantity, Foods foods, Users users) {
        this.consumptionPK = consumptionPK;
        this.quantity = quantity;
        this.foods = foods;
        this.users = users;
    }

    public ConsumptionPK getConsumptionPK() {
        return consumptionPK;
    }

    public void setConsumptionPK(ConsumptionPK consumptionPK) {
        this.consumptionPK = consumptionPK;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Foods getFoods() {
        return foods;
    }

    public void setFoods(Foods foods) {
        this.foods = foods;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }
}
