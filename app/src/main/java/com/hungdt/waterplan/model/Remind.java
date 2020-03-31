package com.hungdt.waterplan.model;

import java.io.Serializable;

public class Remind implements Serializable {
    private String remindID;
    private String remindType;
    private String remindCreateDT;
    private String remindTime;
    private String careCycle;

    public Remind(String remindID, String remindType, String remindCreateDT, String remindTime, String careCycle) {
        this.remindID = remindID;
        this.remindType = remindType;
        this.remindCreateDT = remindCreateDT;
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

    public String getRemindCreateDT() {
        return remindCreateDT;
    }

    public void setRemindCreateDT(String remindCreateDT) {
        this.remindCreateDT = remindCreateDT;
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
