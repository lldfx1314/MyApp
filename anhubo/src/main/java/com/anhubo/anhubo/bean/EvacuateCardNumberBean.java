package com.anhubo.anhubo.bean;

/**
 * Created by LUOLI on 2017/3/7.
 */
public class EvacuateCardNumberBean extends BaseEntity{
    public Data data;
    public class Data {
        public String location;
        public String area_name;
        public String qr_id;
        public String floor;
        public String status;
    }

}
