package com.anhubo.anhubo.db;

import org.litepal.crud.DataSupport;

/**
 * Created by LUOLI on 2017/3/3.
 */
public class Dots extends DataSupport {
    private long id;
    private String location;
    private String areaName;

    public void setLocation(String location) {
        this.location = location;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getAreaName() {
        return areaName;
    }

    public String getLocation() {
        return location;
    }


}
