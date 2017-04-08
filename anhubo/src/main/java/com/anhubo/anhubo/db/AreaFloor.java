package com.anhubo.anhubo.db;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LUOLI on 2017/3/3.
 */
public class AreaFloor extends DataSupport {
    private long id;
    private String buildName;// 建筑名称
    private String picture;// 图片地址
    private String resolution;// 图片分辨率

    private String picSpec;// 特殊图片地址
    private String resolutionSpec;// 特殊图片分辨率

    private String floor;//楼层数
    private String dotNum;//楼层对应的点的个数
    private List<Dots> dot = new ArrayList<Dots>();

    private String upNum;//地上楼层数

    private String underNum;//地下楼层数

    public String getUpNum() {
        return upNum;
    }

    public String getUnderNum() {
        return underNum;
    }

    public void setUnderNum(String underNum) {
        this.underNum = underNum;
    }

    public void setUpNum(String upNum) {
        this.upNum = upNum;
    }

    public void setBuildName(String buildName) {
        this.buildName = buildName;
    }

    public void setPicSpec(String picSpec) {
        this.picSpec = picSpec;
    }

    public void setResolutionSpec(String resolutionSpec) {
        this.resolutionSpec = resolutionSpec;
    }

    public String getPicSpec() {
        return picSpec;
    }

    public String getResolutionSpec() {
        return resolutionSpec;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public void setDotNum(String dotNum) {
        this.dotNum = dotNum;
    }

    public void setDot(List<Dots> dot) {
        this.dot = dot;
    }

    public String getBuildName() {
        return buildName;
    }

    public String getPicture() {
        return picture;
    }

    public String getResolution() {
        return resolution;
    }

    public String getFloor() {
        return floor;
    }

    public String getDotNum() {
        return dotNum;
    }

    public List<Dots> getDot() {
        return dot;
    }


}
