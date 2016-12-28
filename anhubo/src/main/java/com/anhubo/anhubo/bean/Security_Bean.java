package com.anhubo.anhubo.bean;

/**
 * 这是服务器返回验证码的实体类
 * Created by Administrator on 2016/9/27.
 */
public class Security_Bean extends BaseEntity {
    public Data data;

    public class Data {

        public int local_id  ;
        public Boolean newmobile  ;
        public int cd  ;

    }
}
