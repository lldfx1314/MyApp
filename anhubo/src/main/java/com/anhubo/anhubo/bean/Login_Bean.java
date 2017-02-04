package com.anhubo.anhubo.bean;

/**
 * 这是短信登录的实体类
 * Created by Administrator on 2016/9/29.
 */
<<<<<<< HEAD
public class Login_Bean extends BaseEntity{


=======
public class Login_Bean {

    public String code;
    public String msg;
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
    public Data data;

    public class Data {
        public String uid;
        public String business_id;
<<<<<<< HEAD
        public String business_name;
=======
        public String building_id;
        public String business_name;
        public String building_name;
        public int exict;
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
    }
}
