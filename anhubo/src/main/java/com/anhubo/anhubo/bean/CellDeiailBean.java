package com.anhubo.anhubo.bean;

import java.util.List;

/**
 * Created by LUOLI on 2017/1/3.
 */
public class CellDeiailBean extends BaseEntity {
    public Data data;

    public class Data {

        public int page;
        public List<Businesses> businesses;

        public class Businesses {

            public String business_name;
            public String color;

        }


    }

}
