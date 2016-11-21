package com.anhubo.anhubo.bean;

import java.util.List;

/**
 * Created by LUOLI on 2016/11/21.
 */
public class LocationBean extends BaseEntity {
    public Data data;

    public class Data {

        public int page;
        public List<String> business;
        public List<String> building;
    }
}
