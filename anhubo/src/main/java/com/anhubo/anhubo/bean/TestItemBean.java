package com.anhubo.anhubo.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LUOLI on 2016/11/24.
 */
public class TestItemBean extends BaseEntity {
    public  Data data;
    public class Data implements Serializable  {
        public String device_name;
        public List<Require> require;
        public class Require implements Serializable  {

            public String require_tag;
            public String require_desc;
            public String test_id;

        }


    }
}
