package com.anhubo.anhubo.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LUOLI on 2016/11/21.
 */
public class LocationBean extends BaseEntity {
    public Data data;

    public class Data {

        public int page;
        public List<Business> business;

        public List<Building> building;

        public class Business implements Serializable {

            public String name;
            public String poi_id;

        }

        public class Building implements Serializable {

            public String name;
            public String poi_id;

        }
    }
}
