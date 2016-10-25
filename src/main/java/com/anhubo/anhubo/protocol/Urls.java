package com.anhubo.anhubo.protocol;

/**
 * 接口
 * Created by Administrator on 2016/9/22.
 */
public interface Urls {
    String UrlBase = "http://115.28.56.139/api/";
    //String UrlBase = "http://anhubo.com/api/";
    /**新增界面的接口*/
    String Url_Add = UrlBase+"Device/add_device_info";
    /**
     * 这个是新增界面去掉图片文件的接口
     */
    //String Url_Add = UrlBase + "Device/add_device_info_an";
    /**
     * 扫描界面的接口
     */
    String Url_Check = UrlBase + "Device/get_device_detail";
    /**
     * 设备名称选择
     */
    String Url_GetDevName = UrlBase + "Device/de_list";
    /**
     * 完成设备检查
     */
    String Url_Check_Complete = UrlBase + "Device/check_device_re";
    /**
     * 获取验证的token
     */
    String Url_Token = "http://anhubo.com/api/Sms/get_verify_token";
    /**
     * 获取验证码
     */
    String Url_Security = "http://anhubo.com/api/Sms/Send_VerifyCode";
    /**
     * 注册第一个页面的下一步按钮的接口
     */
    String Url_Enter_RegisterActivity2 = UrlBase + "User/register";
    /**短信登录*/
    String Url_LoginMsg = UrlBase + "User/duanxinLogin";
    /**密码注册*/
    String Url_PwdRegister = UrlBase + "User/registerSecond";
    /**密码登录*/
    String Url_LoginPwd = UrlBase + "User/mimaLogin";
    /**安互保协议*/
    String Url_Deal = "http://anhubo.com/s/html/registerTreaty.html";
    /**注册完成，信息填写完整*/
    String Url_RegCom = UrlBase +"User/businessRegister";
    /**学习*/
    String Url_UnitStudy = "http://anhubo.com/s/html/answerSheetAO.html";
    /**学习、检查记录*/
    String Url_studyRecord = UrlBase +"Record/get_re_list";
    String Url_Unit = UrlBase +"BusinessChart/index";
    /**完善信息*/
    String Url_MsgPerfect = UrlBase +"Quali/check_qu";
    /**完善信息员工数*/
    String Url_MsgPerfect_Member = UrlBase +"Quali/get_user_num";
    /**场所使用性质*/
    String Url_GetUsePro = UrlBase +"Pro/pro_list";
    /**上传营业执照*/
    String Url_UpLoading01 = UrlBase +"Quali/thr_pic";
    /**法人身份证*/
    String Url_UpLoading02= UrlBase +"Quali/low_pic";
    /**上传消防审批*/
    String Url_UpLoading03 = UrlBase +"Quali/notice";
    /**上传租房*/
    String Url_UpLoading04 = UrlBase +"Quali/rent_pic";
    /**上传场所使用性质*/
    String Url_UpLoading06 = UrlBase +"Pro/add_bu_pro";
    /**消息中心*/
    String Url_UnitMsgCenter = UrlBase +"Push/msg_list";
    /**单位下所有设备列表*/
    String Device_List = UrlBase +"Device/check_device_list";
    /**建筑下建筑安全指数，三色环比*/
    String Url_Build_score = UrlBase +"BulidingChart/index";
}
