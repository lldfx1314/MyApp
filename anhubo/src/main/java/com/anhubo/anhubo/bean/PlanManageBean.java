package com.anhubo.anhubo.bean;

import java.io.Serializable;

/**
 * Created by LUOLI on 2017/3/23.
 * 计划管理的实体类
 */
public class PlanManageBean extends BaseEntity {
    public Data data;

    public class Data implements Serializable {

        public String payed_num;
        public String act_num;
        public String sum_money;
        public int red_percent;
        public String warming;
        public String apply_num;
        public int service_money;
        public int yel_percent;
        public int green_percent;

    }

}
