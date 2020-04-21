package com.hungdt.waterplan.model;

public class VipDetail {
    int img;
    String titile1;
    String titile2;
    String des1;
    String des2;

    public VipDetail(int img, String titile1, String titile2, String des1, String des2) {
        this.img = img;
        this.titile1 = titile1;
        this.titile2 = titile2;
        this.des1 = des1;
        this.des2 = des2;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getTitile1() {
        return titile1;
    }

    public void setTitile1(String titile1) {
        this.titile1 = titile1;
    }

    public String getTitile2() {
        return titile2;
    }

    public void setTitile2(String titile2) {
        this.titile2 = titile2;
    }

    public String getDes1() {
        return des1;
    }

    public void setDes1(String des1) {
        this.des1 = des1;
    }

    public String getDes2() {
        return des2;
    }

    public void setDes2(String des2) {
        this.des2 = des2;
    }
}
