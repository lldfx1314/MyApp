package com.anhubo.anhubo.bean;

/**
 * 这是Nfc扫描界面的Bean对象
 * Created by Administrator on 2016/9/21.
 */

import java.io.Serializable;
import java.util.List;

public class ScanBean extends BaseEntity implements Serializable{
    public DeviceInfo data;
    /**接口返回的信息*/
    public class DeviceInfo implements Serializable{
        public String device_type_name;
        public int device_exist;
        public String device_id;
        public int require_date_flag;
        public List<String> require_list;
    }
}




