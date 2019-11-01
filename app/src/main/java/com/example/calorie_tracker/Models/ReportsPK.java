package com.example.calorie_tracker.Models;

import java.util.Date;

public class ReportsPK {
    private int uid;
    private Date reportDate;

    public ReportsPK(int uid, Date reportDate) {
        this.uid = uid;
        this.reportDate = reportDate;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }
}
