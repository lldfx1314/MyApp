package com.anhubo.anhubo.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LUOLI on 2017/3/2.
 */
public class AreaBindingBean extends BaseEntity {


    public Data data;

    public class Data implements Serializable {

        public String building_name;
        public String pic;
        public String up_num;
        public String under_num;
        public String resolution;
        public List<Special_pic> special_pic;
        /**特殊楼层 包括地下楼层以及避难层*/
        public class Special_pic implements Serializable {

            public String pic;
            public String floor;
            public String resolution;

        }
    }
}
