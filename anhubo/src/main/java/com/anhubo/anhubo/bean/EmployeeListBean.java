package com.anhubo.anhubo.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LUOLI on 2017/1/11.
 */
public class EmployeeListBean extends BaseEntity implements Serializable {
    public  Data data;
    public class Data implements Serializable{

        public String user_num;
        public List<User_info> user_info;

        public class User_info implements Serializable {

            public int user_type;
            public String pic_path;
            public int status;
            public String username;
            public String uid;

        }


    }

}