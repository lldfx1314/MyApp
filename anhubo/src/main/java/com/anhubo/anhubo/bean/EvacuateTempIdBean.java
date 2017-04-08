package com.anhubo.anhubo.bean;

import java.io.Serializable;

/**
 * Created by LUOLI on 2017/3/16.
 */
public class EvacuateTempIdBean extends BaseEntity {
    public Data data;

    public class Data implements Serializable {
        public String temp_id;
    }
}
