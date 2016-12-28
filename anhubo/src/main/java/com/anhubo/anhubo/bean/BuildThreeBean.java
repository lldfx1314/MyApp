package com.anhubo.anhubo.bean;

/**
 * 这是Nfc扫描界面的Bean对象
 * Created by Administrator on 2016/9/21.
 */

import java.io.Serializable;

public class BuildThreeBean extends BaseEntity implements Serializable {

    public Data data;

    public class Data {
        public int green;
        public int red;
        public int yellow;


    }
}




