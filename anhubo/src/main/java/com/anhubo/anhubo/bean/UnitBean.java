package com.anhubo.anhubo.bean;

/**
 * 这是Nfc扫描界面的Bean对象
 * Created by Administrator on 2016/9/21.
 */

import java.io.Serializable;

public class UnitBean extends BaseEntity implements Serializable {

    public Data data;

    public class Data {

        public String datatime;
        public String sub_score2;
        public String sub_score1;
        public String grade;
        public String sum_score;
        public String sub_score5;
        public String sub_score6;
        public String sub_score3;
        public String sub_score4;

    }
}




