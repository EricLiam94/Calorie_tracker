package com.example.calorie_tracker.Models;

import java.util.Date;

public class ConsumptionPK {
    private int foodId;

    public ConsumptionPK(int foodId, int uid, Date consumeDate) {
        this.foodId = foodId;
        this.uid = uid;
        this.consumeDate = consumeDate;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Date getConsumeDate() {
        return consumeDate;
    }

    public void setConsumeDate(Date consumeDate) {
        this.consumeDate = consumeDate;
    }

    private int uid;
    private Date consumeDate;


}
