package com.anhubo.anhubo.bean;

import java.util.List;

/**
 * Created by LUOLI on 2016/10/22.
 */
public class DeviceListBean extends BaseEntity {
    public Data data;

    public class Data {
        public List<Devices> devices;

        public class Devices{

            public String device_name;
            public String device_id;
        }

    }
}
