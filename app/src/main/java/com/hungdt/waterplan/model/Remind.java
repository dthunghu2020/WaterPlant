package com.hungdt.waterplan.model;

public class Remind {
    private String remindID;
    private String remindType;
    private String remindDateTime;
    private String careCycle;

    public Remind( String remindID, String remindType, String remindDateTime, String careCycle) {
        this.remindID = remindID;
        this.remindType = remindType;
        this.remindDateTime = remindDateTime;
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

    public String getRemindDateTime() {
        return remindDateTime;
    }

    public void setRemindDateTime(String remindDateTime) {
        this.remindDateTime = remindDateTime;
    }

    public String getCareCycle() {
        return careCycle;
    }

    public void setCareCycle(String careCycle) {
        this.careCycle = careCycle;
    }
}
