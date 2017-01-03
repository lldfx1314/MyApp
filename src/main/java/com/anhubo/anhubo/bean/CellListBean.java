package com.anhubo.anhubo.bean;

import java.util.List;

/**
 * Created by LUOLI on 2017/1/3.
 */
public class CellListBean extends BaseEntity {
    public Data data;

    public class Data {

        public int page;
        public List<Units> units;

        public class Units{

            public String unit_name;
            public String unit_id;

        }


    }

}
