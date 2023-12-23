package com.example.garbagesorting.bean;

import java.io.Serializable;

/**
 * 留言
 */
public class Message implements Serializable {
    private Integer id;//
    private Integer userId;//用户ID
    private String content;//内容
    private String date;//日期

    private String name;//昵称
    private String photo;//头像

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Message(Integer id, Integer userId, String content, String date, String name, String photo) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.date = date;
        this.name = name;
        this.photo = photo;
    }
}
