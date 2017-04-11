package com.anhubo.anhubo.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LUOLI on 2017/3/28.
 * 互助金总额实体类
 */
public class PlanHelpSumMoneyBean extends BaseEntity {
    public Data data;

    public class Data implements Serializable {

        public String sum_money;
        public String c_time;
        public int page;
        public List<Pics> pics;

        public class Pics implements Serializable {

            public String year;
            public List<Pic> pic;

            public class Pic implements Serializable {

                public String pic_url;
                public String upload_date;
                public String status;
            }
        }
    }
}
