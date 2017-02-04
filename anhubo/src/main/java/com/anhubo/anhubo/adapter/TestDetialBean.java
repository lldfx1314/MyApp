package com.anhubo.anhubo.adapter;

import com.anhubo.anhubo.bean.BaseEntity;

import java.io.Serializable;

/**
 * Created by LUOLI on 2016/11/25.
 */
public class TestDetialBean extends BaseEntity {
    public Data data;

    public class Data implements Serializable {

        public String require_desc;

    }
}
