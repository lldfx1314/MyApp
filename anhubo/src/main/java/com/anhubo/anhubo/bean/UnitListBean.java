package com.anhubo.anhubo.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LUOLI on 2017/3/27.
 * 计划里面单元列表的实体类
 */
public class UnitListBean extends BaseEntity {
    public Data data;

    public class Data implements Serializable {

        public int page;
        public List<Units> units;

        public class Units implements Serializable {

            public String unit_name;
            public String person_num;
            public String unit_id;
            public String max_num;
            public String type;
        }
    }
}