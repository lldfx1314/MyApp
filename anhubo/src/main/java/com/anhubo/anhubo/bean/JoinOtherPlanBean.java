package com.anhubo.anhubo.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LUOLI on 2017/3/24.
 *加入其它计划的接口
 */
public class JoinOtherPlanBean extends BaseEntity {
    public Data data;

    public class Data implements Serializable {
        public List<PlanList> list;

        public class PlanList implements Serializable {

            public String plan_id;
            public String plan_name;
            public String status;

        }


    }

}