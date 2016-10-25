package com.anhubo.anhubo.bean;

/**
 * 这是短信登录的实体类
 * Created by Administrator on 2016/9/29.
 */
public class Login_Bean {

    public String code;
    public String msg;
    public Data data;

    public class Data {
        public String uid;
        public String business_id;
        public String building_id;
        public String business_name;
        public String building_name;
    }
}
