package com.hungdt.waterplant.model;

public class VipDetail {
    int img;
    String title;
    String des;

    public VipDetail(int img, String title, String des) {
        this.img = img;
        this.title = title;
        this.des = des;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}
