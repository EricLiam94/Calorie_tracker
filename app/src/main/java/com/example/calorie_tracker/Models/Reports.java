package com.example.calorie_tracker.Models;

public class Reports {
    protected ReportsPK reportsPK;
    private double calorieConsumed;
    private double calorieBurned;
    private int steps;
    private double goal;
    private Users users;

    public Reports(ReportsPK reportsPK, double calorieConsumed, double calorieBurned, int steps, double goal, Users users) {
        this.reportsPK = reportsPK;
        this.calorieConsumed = calorieConsumed;
        this.calorieBurned = calorieBurned;
        this.steps = steps;
        this.goal = goal;
        this.users = users;
    }

    public ReportsPK getReportsPK() {
        return reportsPK;
    }

    public void setReportsPK(ReportsPK reportsPK) {
        this.reportsPK = reportsPK;
    }

    public double getCalorieConsumed() {
        return calorieConsumed;
    }

    public void setCalorieConsumed(double calorieConsumed) {
        this.calorieConsumed = calorieConsumed;
    }

    public double getCalorieBurned() {
        return calorieBurned;
    }

    public void setCalorieBurned(double calorieBurned) {
        this.calorieBurned = calorieBurned;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public double getGoal() {
        return goal;
    }

    public void setGoal(double goal) {
        this.goal = goal;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }
}
