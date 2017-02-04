package com.anhubo.anhubo.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 这是设备名称的Bean类
 * Created by Administrator on 2016/9/24.
 */
public class DeviceNameBean extends BaseEntity implements Serializable {


    public DataBean data;

    public static class DataBean {
        public List<DevicesBean> devices;

        public static class DevicesBean {
            /**
             * 单位常用设备
             */
            public String system_name;
            /**
             * 二级菜单集合，例如单位常用设备
             */
            public List<ChoiceBean> choice;

            /**
             * 二级菜单具体的型号，例如手提式干粉灭火器
             */
            public static class ChoiceBean {
                public String device_id;
                public String device_name;
            }
        }
    }
}


