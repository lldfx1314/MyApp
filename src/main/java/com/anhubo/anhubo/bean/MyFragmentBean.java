package com.anhubo.anhubo.bean;

import java.io.Serializable;

/**
 * Created by LUOLI on 2016/10/28.
 */
public class MyFragmentBean extends BaseEntity implements Serializable{
    public  Data data;
    public class Data implements Serializable{

        public String weixin_name  ;
        public String business_name  ;
        public String building_name  ;
        public String img  ;
        public String phone  ;
        public String sex  ;
        public String name  ;
        public String qq_name  ;
        public String weibo_name  ;
        public String age  ;

    }
}
