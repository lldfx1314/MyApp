package com.anhubo.anhubo.bean;

import java.io.Serializable;

/**
 * 这是检查完成的实体类
 * Created by Administrator on 2016/9/22.
 */

public class CheckComplete_Bean extends BaseEntity implements Serializable{

    public Data data;

    public class Data implements Serializable{

        public int device_checked_num;
        public String devices_num;
    }
}