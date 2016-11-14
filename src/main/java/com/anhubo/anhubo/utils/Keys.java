package com.anhubo.anhubo.utils;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Administrator on 2016/9/28.
 */
public interface Keys {
    /**
     * 跳转到新增页面
     */
    String CARDNUMBER = "cardnumber";
    /**
     * 跳转到修改页面
     */
    String DEVICEINFO = "deviceInfo";
    String PHONE = "phone";
    /**
     * 安互保协议
     */
    String ANHUBAODEAL = "anhubaoDeal";
    /**
     * uid
     */
    String UID = "uid";
    /**
     * 登录成功的字段
     */
    String BUSINESSID = "businessId";
    /**
     * bulidingId字段
     */
    String BULIDINGID = "bulidingId";
    String BUILDINGNAME = "buildingName";
    String BUSINESSNAME = "businessName";
    String STR = "str";
    /**
     * 完善信息  营业执照
     */
    String ISCLICK1 = "isclick1";
    /**
     * 完善信息  法人身份证
     */
    String ISCLICK2 = "isclick2";
    /**
     * 完善信息  消防审批
     */
    String ISCLICK3 = "isclick3";
    /**
     * 完善信息  租房合同
     */
    String ISCLICK4 = "isclick4";
    /**
     * 新增设备
     */
    String NEWDEVICE = "newDevice";
    /**
     * 检查
     */
    String CHECK = "Check";
    /**
     * 演练
     */
    String EXERCISE = "Exercise";
    /**
     * 检查完成的数
     */
    String CHECKCOMPLETE_BEAN = "checkcomplete_bean";
    /**
     * 扫描类型
     */
    String SCAN_TYPE = "scan_type";
    /**已经能检查的设备数，用来做缓存*/
    String DEVICECHECKEDNUM = "DEVICECHECKEDNUM";
    /**添加的设备总数，用来做缓存*/
    String DEVICESNUM = "DEVICESNUM";

    /**头像保存成功，给上个页面传值*/
    String HEADERICON = "headericon";
    /**我的界面传递图片给个人信息*/
    String MYBEAN = "mybean";
    /**给我的界面传递名字*/
    String NEWNAME = "NEWNAME";
    /**给我的界面传递性别*/
    String NEWGENDER = "newGender";
    /**给我的界面传递年龄*/
    String NEWAGE = "newage";
    /**微信登录界面跳转到注册界面的三个信息*/
    String UNIONID = "unionid";
    String PROFILE_IMAGE_URL = "profile_image_url";
    String SCREEN_NAME = "screen_name";

    /**正常登录界面跳到注册界面*/
    String LOGINFORZHUCE = "loginforzhuce";
    /**微信登录界面跳到注册界面*/
    String WEIXINFORZHUCE = "weixinforzhuce";
    /**微信绑定后显示头像并传到我的界面*/
    String HEADERICON_WEIXIN = "headericon_weixin";

    /**微信的昵称*/
    String SCREENNAME = "screenName";
    /**微信头像的url*/
    String WEIXINIMG = "weixinImg";
    /**传递deviceId*/
    String DeviceId = "deviceId";
    /**待处理反馈isid*/
    String IsId = "isId";
}
