package com.anhubo.anhubo.bean;

/**
 * 这是Nfc扫描界面的Bean对象
 * Created by Administrator on 2016/9/21.
 */

import java.io.Serializable;

public class BuildScoreBean extends BaseEntity implements Serializable {

    public Data data;

    public class Data{

        public String sum_score;
        public String sub_score1;
        public String sub_score2;
        public String datatime;
        public String sub_score3;
        public String sub_score4;
        public String sub_score5;
        public String sub_score6;

    }
}




