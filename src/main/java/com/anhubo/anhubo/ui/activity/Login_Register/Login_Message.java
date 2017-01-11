package com.anhubo.anhubo.ui.activity.Login_Register;

import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.Login_Bean;
import com.anhubo.anhubo.bean.Login_WeiXin_Bean;
import com.anhubo.anhubo.bean.Security_Bean;
import com.anhubo.anhubo.bean.Security_Token_Bean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.ui.activity.HomeActivity;
import com.anhubo.anhubo.utils.InputWatcher;
import com.anhubo.anhubo.utils.JsonUtil;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.utils.Utils;
import com.anhubo.anhubo.view.AlertDialog;
import com.google.gson.Gson;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Administrator on 2016/9/27.
 */
public class Login_Message extends BaseActivity {

    private static final String TAG = "Login_Message";
    private Button btnLoginPwd;
    private Button btnLoginRegister;
    private EditText etLoginMsgphoneNumber;
    private Button btnLoginMsgphoneNumber;
    private EditText etLoginMsgSecurity;
    private TextView tvLoginMsgSecurity;
    private Button btnLoginMsg;
    private String phoneNumber;
    private String securityCode;
    private boolean isLegal;
    private String token;
    private ImageButton ibWeichat;
    private String profile_image_url;
    private String screen_name;
    private String unionid;
    private UMShareAPI mShareAPI;
    private Dialog showDialog;


    @Override
    protected void initConfig() {
        super.initConfig();

    }


    @Override
    protected int getContentViewId() {
        return R.layout.login_msg_activity;
    }

    @Override
    protected void initViews() {
        // 找控件
        // 输入手机号
        etLoginMsgphoneNumber = (EditText) findViewById(R.id.et_loginMsg_phoneNmber);
        // 电话号码的小圆叉
        btnLoginMsgphoneNumber = (Button) findViewById(R.id.btn_msgLogin_phoneNumber);
        // 输入验证码
        etLoginMsgSecurity = (EditText) findViewById(R.id.et_loginMsg_security);
        // 获取验证码
        tvLoginMsgSecurity = (TextView) findViewById(R.id.tv_loginMsg_security);
        // 登录
        btnLoginMsg = (Button) findViewById(R.id.btn_loginMsg);
        //密码登录
        btnLoginPwd = (Button) findViewById(R.id.btn_loginPwd);
        // 注册
        btnLoginRegister = (Button) findViewById(R.id.btn_login_register);
        // 微信登录
        ibWeichat = (ImageButton) findViewById(R.id.ib_weichat);
    }

    @Override
    protected void initEvents() {
        super.initEvents();
        // 设置监听
        btnLoginMsgphoneNumber.setOnClickListener(this);
        tvLoginMsgSecurity.setOnClickListener(this);
        btnLoginMsg.setOnClickListener(this);
        btnLoginPwd.setOnClickListener(this);
        btnLoginRegister.setOnClickListener(this);
        ibWeichat.setOnClickListener(this);
        // 监听号码输入框状态，控制机右边小圆叉
        etLoginMsgphoneNumber.addTextChangedListener(new InputWatcher(btnLoginMsgphoneNumber, etLoginMsgphoneNumber));
    }

    @Override
    protected void onLoadDatas() {

    }

    @Override
    public void onClick(View v) {
        getInputData();
        switch (v.getId()) {
            case R.id.btn_msgLogin_phoneNumber://号码小圆叉
                etLoginMsgphoneNumber.setText("");
                break;
            case R.id.tv_loginMsg_security:// 获取验证码

                // 定义一个方法获取验证码
                getSecurityCode();

                break;
            case R.id.btn_loginMsg:        // 登录
                // 调用接口登录的方法
                submit();
                break;
            case R.id.btn_loginPwd:// 密码登录
                // 当用户点击密码登录时携带本页面里的手机号到另一个界面
                goToLoginPwd();
                break;
            case R.id.btn_login_register://跳到注册
                Intent intent = new Intent(mActivity, RegisterActivity.class);
                intent.putExtra(Keys.LOGINFORZHUCE, "LoginforZhuce");
                startActivity(intent);
                break;
            case R.id.ib_weichat://跳到微信界面
                mShareAPI = UMShareAPI.get(Login_Message.this);
                mShareAPI.doOauthVerify(Login_Message.this, SHARE_MEDIA.WEIXIN, umAuthListener);//授权

                break;
        }

    }


    /**
     * 微信授权
     */
    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            //ToastUtils.showToast(mActivity, "授权成功");
            mShareAPI.getPlatformInfo(mActivity, SHARE_MEDIA.WEIXIN, umGetUserInfoListener);//获取用户信息

        }


        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            ToastUtils.showToast(mActivity, "登录失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            ToastUtils.showToast(mActivity, "取消登录");
        }
    };
    /**
     * 获取微信详细信息
     */
    private UMAuthListener umGetUserInfoListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            // 从map结合里面获取unionid
            unionid = map.get("unionid");
            profile_image_url = map.get("profile_image_url");
            screen_name = map.get("screen_name");

            /**微信授权后走的微信登录接口*/
            String url = Urls.Url_LoginWEIXIN;
            // 封装请求参数
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("reg_mode", 2 + "");// 2代表微信
            params.put("unique_id", unionid);

            OkHttpUtils.post()//
                    .url(url)//
                    .params(params)//
                    .build()//
                    .execute(new MyStringCallback3());
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {

        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {

        }
    };


    /**
     * 微信登录
     */
    class MyStringCallback3 extends StringCallback {
        @Override
        public void onError(okhttp3.Call call, Exception e) {
            LogUtils.e(TAG,":Login_Message+++微信登录:",e);
        }

        @Override
        public void onResponse(String response) {
            LogUtils.eNormal(TAG+":Login_Message+++微信登录:",response);
            Login_WeiXin_Bean bean = JsonUtil.json2Bean(response, Login_WeiXin_Bean.class);
            if (bean != null) {
                String code = bean.code;
                String msg = bean.msg;
                Login_WeiXin_Bean.Data data = bean.data;
                String uid = data.uid;
                String businessId = data.business_id;
//                String buildingId = data.building_id;
//                String buildingName = data.building_name;
                String businessName = data.business_name;
                int exict = data.exict;
                if (exict == 0||exict == 1) {
                    // 正常登录

                    SpUtils.putParam(mActivity, Keys.UID, uid);
                    SpUtils.putParam(mActivity, Keys.BUSINESSID, businessId);
                    SpUtils.putParam(mActivity, Keys.BUSINESSNAME, businessName);
//                    SpUtils.putParam(mActivity, Keys.BULIDINGID, buildingId);
//                    SpUtils.putParam(mActivity, Keys.BUILDINGNAME, buildingName);

                    //跳转到主页面
                    enterHome(uid);
                }else if (exict == 2) {
                    // 去注册uid unionid 头像 profile_image_url   姓名  screen_name
                    Intent intent = new Intent(mActivity, RegisterActivity.class);
                    intent.putExtra(Keys.WEIXINFORZHUCE, "weixinforzhuce");
                    intent.putExtra(Keys.UNIONID, unionid);
                    intent.putExtra(Keys.PROFILE_IMAGE_URL, profile_image_url);
                    intent.putExtra(Keys.SCREEN_NAME, screen_name);
                    startActivity(intent);
                }

            }
        }
    }


    /**
     * 当用户点击密码登录时携带本页面里的手机号到另一个界面
     */
    private void goToLoginPwd() {
        if (etLoginMsgphoneNumber != null) {
            Intent intent = new Intent(Login_Message.this, Login_Pwd.class);
            //System.out.println("要传递的uid+++===+++" + uid);
            intent.putExtra(Keys.PHONE, phoneNumber);
            startActivity(intent);
        }
    }

    /**
     * 调用接口登录的方法
     */
    private void submit() {
        if (TextUtils.isEmpty(phoneNumber)) {
            showdialog("请输入手机号码");
            return;
        }
        if (!Utils.judgePhoneNumber(phoneNumber)) {
            showdialog("请输入正确的手机号码");
            return;
        }
        if (TextUtils.isEmpty(securityCode)) {
            showdialog("请输入验证码");
            return;
        }
        if (securityCode.length() != 4) {
            showdialog("验证码长度为4");
            return;
        }
        login();
    }

    /**
     * 弹窗提示
     */
    private void showdialog(String string) {

        new AlertDialog(mActivity).builder()
                .setTitle("提示")
                .setMsg(string)
                .setCancelable(true).show();
    }


    private void login() {
        showDialog = loadProgressDialog.show(mActivity, "正在登录...");
        String url = Urls.Url_LoginMsg;
        // 封装请求参数
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("telphone", phoneNumber);
        params.put("verify_code", securityCode);

        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback2());


    }

    class MyStringCallback2 extends StringCallback {

        @Override
        public void onError(okhttp3.Call call, Exception e) {
            showDialog.dismiss();
            LogUtils.e(TAG,":login:",e);
        }

        @Override
        public void onResponse(String response) {
            showDialog.dismiss();
            LogUtils.eNormal(TAG+":login:",response);
            Login_Bean bean = JsonUtil.json2Bean(response, Login_Bean.class);
            if (bean != null) {
                // 获取到数据
                int code = bean.code;
                String msg = bean.msg;
                Login_Bean.Data data = bean.data;
                String uid = data.uid;
                String businessId = data.business_id;
                String businessName = data.business_name;
//                String buildingId = data.building_id;
//                String buildingName = data.building_name;

                // 根据code值判断跳转到那个界面
                switch (code) {
                    case 101://网络错误
                        showdialog(msg);
                        break;
                    case 102://验证码错误
                        showdialog(msg);
                        break;
                    case 104://该手机号码没注册，携带输入的手机号跳转到密码注册界面
                        goToPwdRegisterActivity();
                        break;
//                    case "105":// 跳转到注册的第二步
                        // 获取到uid后携带uid跳转到RegisterActivity2界面
//                        goTo_Activity(uid);
//                        break;
//                    case "106":// 跳转到注册的第二步
//                        goTo_Activity(uid);
//                        break;
                    case 0:// 登录成功，携带返回的参数跳转到单位界面，同时存一下把uid等参数保存到本地

                        // 保存参数
                        SpUtils.putParam(mActivity, Keys.UID, uid);
                        SpUtils.putParam(mActivity, Keys.BUSINESSID, businessId);
                        SpUtils.putParam(mActivity, Keys.BUSINESSNAME, businessName);
//                        SpUtils.putParam(mActivity, Keys.BULIDINGID, buildingId);
//                        SpUtils.putParam(mActivity, Keys.BUILDINGNAME, buildingName);
                        //跳转到主页面
                        enterHome(uid);
                        break;
                }

            }
        }
    }


    /**
     * 携带输入的手机号跳转到密码注册界面
     */
    private void goToPwdRegisterActivity() {
        if (phoneNumber != null) {
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("该账号不存在，请注册")
                    .setPositiveButton("去注册", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mActivity, PwdRegisterActivity.class);
                            intent.putExtra(Keys.PHONE, phoneNumber);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    })
                    .setCancelable(false).show();

        } else {

            showdialog("网络错误，请重试");
        }
    }

    /**
     * 携带参数跳转到注册的第二个界面
     */
//    private void goTo_Activity(String uid) {
//        if (uid != null) {
//            Intent intent = new Intent(Login_Message.this, RegisterActivity2.class);
//            intent.putExtra(Keys.UID, uid);
//            startActivity(intent);
//        } else {
//            new AlertDialog(mActivity).builder()
//                    .setTitle("提示")
//                    .setMsg("网络错误，请重试")
//                    .setCancelable(true).show();
//        }
//    }


    private void enterHome(String uid) {
        Intent intent = new Intent(mActivity, HomeActivity.class);
        startActivity(intent);
        // 把uid保存起来
        SpUtils.putParam(mActivity, Keys.UID, uid);
        // 发送一条广播，登录完成后关闭登录的所有界面
        mActivity.sendBroadcast(new Intent(INTENT_FINISH));
    }

    /**
     * 获取验证码
     */
    private void getSecurityCode() {
        // 调用方法先判断手机号码是否合法
        isLegal = Utils.judgePhoneNumber(phoneNumber);
        if (TextUtils.isEmpty(phoneNumber)) {
            showdialog("请输入手机号码");
            return;
        } else if (!isLegal) {
            // 不合法，弹出对话框提示用户
            showdialog("请输入正确的手机号码");
            return;
        } else {
            // 合法，就获取验证码
            getSecuritys();
        }
    }

    /**
     * 获取验证码
     */
    private void getSecuritys() {
        // 把光标移动到验证码输入框
        Utils.setEditTextSelection(etLoginMsgSecurity);
        // 第一次请求获取验证的token
        getToken();

    }

    /**
     * 第一次请求获取验证的token
     */
    private void getToken() {

        String url = Urls.Url_Token;
        OkHttpUtils.post()//
                .url(url)//
                .build()//
                .execute(new MyStringCallback());


    }

    class MyStringCallback extends StringCallback {

        @Override
        public void onError(okhttp3.Call call, Exception e) {
            System.out.println("Login_Message+++token===没拿到数据" + e.getMessage());
        }

        @Override
        public void onResponse(String response) {
            Security_Token_Bean bean = new Gson().fromJson(response, Security_Token_Bean.class);
            if (bean != null) {
                // 拿到checkCompleteBean，获取token
                token = bean.data.token;
                // 第二次请求获取验证码
                if (token != null) {
                    // 第一次请求获取验证的token不为为空，则第二次请求获取验证码
                    getSecurity();

                } else {
                    // 为空，则弹吐司提示用户
                    showdialog("网络有误，请稍后再试");
                }
            }
        }
    }

    /**
     * 第二次请求获取验证码
     */
    private void getSecurity() {
        // 更改获取验证码的TextView显示的内容
        Utils.setSecurityTextView(tvLoginMsgSecurity);

        String url = Urls.Url_Security;
        HashMap<String, String> params = new HashMap<>();
        params.put("mobile", phoneNumber);
        params.put("token", token);

        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback1());
    }

    class MyStringCallback1 extends StringCallback {
        /*@Override
        public void onError(Call call, Exception e) {


        }*/

        @Override
        public void onError(okhttp3.Call call, Exception e) {
            System.out.println("Login_Message+++验证码===没拿到数据" + e.getMessage());
        }

        @Override
        public void onResponse(String response) {
            Security_Bean bean = new Gson().fromJson(response, Security_Bean.class);
        }
    }


    /**
     * 获取输入的内容
     */
    private void getInputData() {
        //手机号
        phoneNumber = etLoginMsgphoneNumber.getText().toString().trim();
        //验证码
        securityCode = etLoginMsgSecurity.getText().toString().trim();
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }
}
