package com.anhubo.anhubo.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/3/22.
 * 计划的实体类
 */
public class PlanBean extends BaseEntity {
    public Data data;

    public class Data implements Serializable {

        public int status;//状态（0没有加入计划，1加入计划不是管理者，2计划管理者）
        // 计划列表
        public List<PlanList> list;
        // 计划凭证
        public List<Cert> cert;
        // 计划管理
        public List<Manage> manage;

        public class Cert implements Serializable {
            public double plan_ensure;//计划名称
            public double plan_money;//预付保障金
            public String plan_id;
            public String plan_name;//计划名称
            public String status;//计划阶段

        }

        public class PlanList {
            public String plan_id;
            public String plan_name;
            public String plan_ensure;
            public String plan_money;
            public String join_num;// 加入人数
            public String payed_num;//互助案例数
            public String status;

        }

        public class Manage implements Serializable {
            public String act_num;
            public String payed_num;
            public String sum_money;
            public String plan_num;
            public String plan_id;
            public String plan_name;
        }
    }
}
