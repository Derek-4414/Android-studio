package com.example.garbagesorting.bean;


import java.io.Serializable;

public class Rubbish implements Serializable {
    private Integer id;
    private String name;//名称
    private String img;//头像
    private double latitude;//纬度
    private double longitude;//经度

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Rubbish(Integer id, String name, String img, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.img = img;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
