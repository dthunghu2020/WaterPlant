package com.hungdt.waterplan.model;

public class Remind {
    private String remindID;
    private String remindType;
    private String remindDate;
    private String remindTime;
    private String careCycle;

    public Remind(String remindID, String remindType, String remindDate, String remindTime, String careCycle) {
        this.remindID = remindID;
        this.remindType = remindType;
        this.remindDate = remindDate;
        this.remindTime = remindTime;
        this.careCycle = careCycle;
    }

    public String getRemindID() {
        return remindID;
    }

    public void setRemindID(String remindID) {
        this.remindID = remindID;
    }

    public String getRemindType() {
        return remindType;
    }

    public void setRemindType(String remindType) {
        this.remindType = remindType;
    }

    public String getRemindDate() {
        return remindDate;
    }

    public void setRemindDate(String remindDate) {
        this.remindDate = remindDate;
    }

    public String getRemindTime() {
        return remindTime;
    }

    public void setRemindTime(String remindTime) {
        this.remindTime = remindTime;
    }

    public String getCareCycle() {
        return careCycle;
    }

    public void setCareCycle(String careCycle) {
        this.careCycle = careCycle;
    }
}
