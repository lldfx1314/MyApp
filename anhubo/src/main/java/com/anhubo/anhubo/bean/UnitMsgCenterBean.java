package com.anhubo.anhubo.bean;

import java.util.List;

/**
 * Created by LUOLI on 2016/10/20.
 */
public class UnitMsgCenterBean extends BaseEntity {


    public Data data;

    public class Data{

        public int page;
        public List<Msg_list> msg_list;

        public class Msg_list {

            public String msg;
            public String time;
            public String title;

        }


    }
}