package com.example.garbagesorting.bean;

import java.util.List;

/**
 * 图片返回数据
 */
public class PictureResult {
    private double score;
    private String keyword;
    private List<ItemResult> list;

    public void setScore(double score) {
        this.score = score;
    }

    public double getScore() {
        return score;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setList(List<ItemResult> list) {
        this.list = list;
    }

    public List<ItemResult> getList() {
        return list;
    }
}
