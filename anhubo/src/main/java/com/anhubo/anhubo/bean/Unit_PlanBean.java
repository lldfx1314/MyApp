package com.anhubo.anhubo.bean;

import java.util.List;

/**
 * 单位下互保计划
 * Created by LUOLI on 2016/11/11.
 */
public class Unit_PlanBean extends BaseEntity {
    public Data data;

<<<<<<< HEAD
    public class Data {
        public List<Certs> certs;

        public class Certs {

            public String plan_ensure;
            public String plan_money_proportion;
            public String plan_money;
            public String plan_ensure_proportion;
            public String plan_id;
            public String time;
            public String plan_name;
            public String score;
            public String unit_id;
=======
    public class Data{
        public List<Certs> certs;

        public class Certs{

            public String max_each_money;
            public String plan_money_last;
            public String max_plan_ensure;
            public String plan_id;
            public String plan_name;
            public String status;
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce

        }
    }
}
