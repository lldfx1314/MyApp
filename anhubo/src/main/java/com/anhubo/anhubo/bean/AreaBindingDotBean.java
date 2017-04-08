package com.anhubo.anhubo.bean;

import com.anhubo.anhubo.db.Dots;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LUOLI on 2017/3/2.
 */
public class AreaBindingDotBean extends BaseEntity {
    public Data data;

    public class Data implements Serializable {

        public String qr_num;// 区域二维码对应点的个数
        public List<Qrcodes> area_qrs;

        public class Qrcodes implements Serializable {

            public String location;

        }

    }
}
