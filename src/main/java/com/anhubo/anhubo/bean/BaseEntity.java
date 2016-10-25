package com.anhubo.anhubo.bean;
import java.io.Serializable;

/**
 * Created by LuoLi on 2016/9/20.
 * E-mail: 1007812935@qq.com
 * Description:  实体类的公共（每个请求回来的数据里面都包含  code 和 msg ）
 */
public class BaseEntity implements Serializable {
    /**
     * 接口返回状态码
     */
    public int code;
    /**
     * 接口返回提示字符串
     */
    public String msg;
}