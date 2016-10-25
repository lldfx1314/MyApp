package com.anhubo.anhubo.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/13.
 */
public class MyPolygonBean implements Serializable{

    private String[] text;
    private int[] area;
    public void setText(String[] text) {
        this.text = text;
    }
    public String[] getText() {
        return text;
    }

    public void setArea(int[] area) {
        this.area = area;
    }

    public int[] getArea() {
        return area;
    }
}
