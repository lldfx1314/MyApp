package com.anhubo.anhubo.bean;

import java.util.List;

/**
 * Created by LUOLI on 2016/11/14.
 */
public class Pending_FeedbackBean extends BaseEntity{
    public  Data data;
    public class Data{

        public String is_content;
        public String is_time;
        public List<String> is_pic;

    }
}
