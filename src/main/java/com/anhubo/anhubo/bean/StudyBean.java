package com.anhubo.anhubo.bean;


import java.io.Serializable;
import java.util.List;

public class StudyBean extends BaseEntity implements Serializable {

    public Data data;

    public class Data {
        public int page;
        public List<Records> records;

        public class Records implements Serializable {

            public String time;
            public List<Record_list> record_list;

            public class Record_list implements Serializable {

                public String user_name;
                public String type_id;

            }

        }
    }
}