package com.anhubo.anhubo.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LUOLI on 2017/3/7.
 */
public class EvacuateBean extends BaseEntity {
    public Data data;

    public class Data implements Serializable {
        public String qr_num;
        public List<Qrcodes> qrcodes;

        public class Qrcodes implements Serializable {

            public String area_name;
            public String location;
            public String status;

        }


    }

}
