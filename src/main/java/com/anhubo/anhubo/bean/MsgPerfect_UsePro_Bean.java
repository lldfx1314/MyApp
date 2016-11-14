package com.anhubo.anhubo.bean;

import java.util.List;

/**
 * 这是短信登录的实体类
 * Created by Administrator on 2016/9/29.
 */
public class MsgPerfect_UsePro_Bean extends BaseEntity {
    public Data data;

    public class Data {
        public List<Properties> properties;

        public class Properties {

            public String property2;
            public List<Property1_arr> property1_arr;

            public class Property1_arr {

                public String property1;

            }
        }
    }
}