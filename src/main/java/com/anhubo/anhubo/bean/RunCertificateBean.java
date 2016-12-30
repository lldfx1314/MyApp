package com.anhubo.anhubo.bean;

import java.util.List;

/**
 * 单位下互保计划
 * Created by LUOLI on 2016/12/30.
 */
public class RunCertificateBean extends BaseEntity {
    public Data data;

    public class Data {
        public String unit_name;
        public int plan_ensure;
        public String sum_money;
        public int exist_flag;
        public int pay_num;
        public int payed_num;
        public int plan_money;
        public String unit_business_num;
        public String u_status;
        public String business_num;
        public String plan_name;
        public String status;
        public int score0;
        public int score1;
        public int score2;



    }

}