package com.anhubo.anhubo.bean;

/**
 * Created by LUOLI on 2016/12/15.
 */
public class Check_UpDateBean extends BaseEntity{
    public Data data;
    public class Data{
        public String new_version;
        public String type;
        public String url;
    }
}
