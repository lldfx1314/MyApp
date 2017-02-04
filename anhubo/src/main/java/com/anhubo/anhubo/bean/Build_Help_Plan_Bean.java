package com.anhubo.anhubo.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 互助计划
 * Created by LUOLI on 2016/11/23.
 */
public class Build_Help_Plan_Bean extends BaseEntity {
    public Data data;

    public class Data implements Serializable {
        public List<Plans> plans;

        public class Plans implements Serializable {


            public String mass_name;
            public String mass_id;
            public String plan_ensure;
            public String plan_id;
            public String plan_join;
            public String plan_person;
            public String plan_range;
            public String plan_name;

        }


    }
}
