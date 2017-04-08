package com.anhubo.anhubo.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LUOLI on 2017/3/27.
 * 会员情况的实体类
 */
public class PlanMemberBean extends BaseEntity {
    public Data data;

    public class Data implements Serializable {

        public String actual_num;
        public String plan_num;
        public String today_num;
        public List<Business_names> business_names;
        public List<Filled_units> filled_units;
        public List<Unfilled_units> unfilled_units;

        public class Business_names implements Serializable {

            public String business_name;

        }

        public class Filled_units implements Serializable {

            public String unit_name;
            public String unit_id;

        }


        public class Unfilled_units implements Serializable {

            public String unit_name;
            public String person_num;
            public String unit_id;
            public String max_num;
        }
    }
}

