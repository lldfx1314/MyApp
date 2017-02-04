package com.anhubo.anhubo.bean;

import java.io.Serializable;

/**
 * 这是新增设备的Bean 类
 * Created by Administrator on 2016/9/22.
 */

public class Add_Device_Bean extends BaseEntity {

    public Data data;

    public class Data implements Serializable{
        public String device_id;
    }
}