package com.anhubo.anhubo.ui.activity.Login_Register;

import android.content.Intent;
import android.telecom.Call;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.Login_Bean;
import com.anhubo.anhubo.bean.Security_Bean;
import com.anhubo.anhubo.bean.Security_Token_Bean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.ui.activity.HomeActivity;
import com.anhubo.anhubo.utils.InputWatcher;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.utils.Utils;
import com.google.gson.Gson;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UmengTool;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Created by Administrator on 2016/9/27.
 */
public class Login_Message extends BaseActivity {

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
    private String newUnionid;
    private String unionid;


    @Override
    protected void initConfig() {
        super.initConfig();
        // 第一次请求获取验证的token
        getToken();
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
                login();
                break;
            case R.id.btn_loginPwd:// 密码登录
                // 当用户点击密码登录时携带本页面里的手机号到另一个界面
                goToLoginPwd();
                break;
            case R.id.btn_login_register://跳到注册
                Intent intent = new Intent(mActivity, RegisterActivity.class);
                intent.putExtra(Keys.LOGINFORZHUCE,"LoginforZhuce");
                startActivity(intent);
                break;
            case R.id.ib_weichat://跳到微信界面
                UMShareAPI mShareAPI = UMShareAPI.get(Login_Message.this);
                mShareAPI.doOauthVerify(Login_Message.this, SHARE_MEDIA.WEIXIN, umAuthListener);//授权
                mShareAPI.getPlatformInfo(mActivity, SHARE_MEDIA.WEIXIN, umAuthListener1);//获取用户信息
                break;
        }

    }

    private UMAuthListener umAuthListener1 = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            if (map != null) {
                //转换为set

                Set<String> keySet = map.keySet();

                //遍历循环，得到里面的key值----用户名，头像....

                for (String string : keySet) {
                    //打印下
                    //System.out.println("==========11111111111=="+string);
                    // 打印完获取到的信息在下面
                   /* unionid profile_image_url country screen_name access_token city gender province
                    language expires_in refresh_token openid*/
                    //我需要的 uid unionid 头像 profile_image_url   姓名  screen_name
                }
                newUnionid = map.get("unionid");
                profile_image_url = map.get("profile_image_url");
                screen_name = map.get("screen_name");

            }
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {

        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {

        }
    };

    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            //ToastUtils.showToast(mActivity, "授权成功");

            //转换为set

            Set<String> keySet = data.keySet();

            //遍历循环，得到里面的key值----用户名，头像....

            for (String string : keySet) {
                //打印下
                //System.out.println("==========11111111111==========string111111111"+string);
                /*unionid scope  expires_in access_token openid  refresh_token*/
            }

            //得到key值得话，可以直接的到value

            unionid = data.get("unionid");

            /**微信授权后走的微信登录接口*/
            String url = Urls.Url_LoginWEIXIN;
            // 封装请求参数
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("reg_mode", 2 + "");
            params.put("unique_id", unionid);

            OkHttpUtils.post()//
                    .url(url)//
                    .params(params)//
                    .build()//
                    .execute(new MyStringCallback3());
        }


        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            ToastUtils.showToast(mActivity, "授权失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            ToastUtils.showToast(mActivity, "授权取消");
        }
    };

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }

    class MyStringCallback3 extends StringCallback {
        @Override
        public void onError(okhttp3.Call call, Exception e) {
            System.out.println("Login_Message+++微信登录===没拿到数据" + e.getMessage());
        }

        @Override
        public void onResponse(String response) {
            //System.out.println("微信登录" + response);
            Login_Bean bean = new Gson().fromJson(response, Login_Bean.class);
            if (bean != null) {
                String code = bean.code;
                String msg = bean.msg;
                Login_Bean.Data data = bean.data;
                String uid = data.uid;
                String businessId = data.business_id;
                String buildingId = data.building_id;
                String buildingName = data.building_name;
                String businessName = data.business_name;
                int exict = data.exict;
                if (exict == 1) {
                    // 正常登录

                    SpUtils.putParam(mActivity, Keys.UID, uid);
                    SpUtils.putParam(mActivity, Keys.BUSINESSID, businessId);
                    SpUtils.putParam(mActivity, Keys.BULIDINGID, buildingId);
                    SpUtils.putParam(mActivity, Keys.BUILDINGNAME, buildingName);
                    SpUtils.putParam(mActivity, Keys.BUSINESSNAME, businessName);

                    //跳转到主页面
                    enterHome();
                } else if (exict == 0) {
                    // 注册的第二步
                    goTo_Activity(uid);
                } else if (exict == 2) {
                    // 去注册uid unionid 头像 profile_image_url   姓名  screen_name
                    Intent intent = new Intent(mActivity, RegisterActivity.class);
                    intent.putExtra(Keys.WEIXINFORZHUCE,"weixinforzhuce");
                    intent.putExtra(Keys.UNIONID,unionid);
                    intent.putExtra(Keys.PROFILE_IMAGE_URL,profile_image_url);
                    System.out.println("hduiewhfi7777777777+++===="+profile_image_url);
                    intent.putExtra(Keys.SCREEN_NAME,screen_name);
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
    private void login() {
        if (TextUtils.isEmpty(phoneNumber)) {
            ToastUtils.showToast(mActivity, "手机号码不能为空");
            return;
        }
        if (!Utils.judgePhoneNumber(phoneNumber)) {
            ToastUtils.showToast(mActivity, "请输入正确的手机号码");
            return;
        }
        if (TextUtils.isEmpty(securityCode)) {
            ToastUtils.showToast(mActivity, "请输入验证码");
            return;
        }
        if (securityCode.length() != 4) {
            ToastUtils.showToast(mActivity, "验证码长度为4");
            return;
        }
        login_OKHttp();
    }


    private void login_OKHttp() {
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
        /*@Override
        public void onError(Call call, Exception e) {

            System.out.println("Login_Message+++===没拿到数据" + e.getMessage());
        }*/

        @Override
        public void onError(okhttp3.Call call, Exception e) {
            System.out.println("Login_Message+++===没拿到数据" + e.getMessage());
        }

        @Override
        public void onResponse(String response) {

//            System.out.println("Login_Message++"+response);
            Login_Bean bean = new Gson().fromJson(response, Login_Bean.class);
            if (bean != null) {
                // 获取到数据
                String code = bean.code;
                String msg = bean.msg;
                Login_Bean.Data data = bean.data;
                String uid = data.uid;
                String businessId = data.business_id;
                String buildingId = data.building_id;
                String buildingName = data.building_name;
                String businessName = data.business_name;

                // 根据code值判断跳转到那个界面
                switch (code) {
                    case "101"://网络错误
                        ToastUtils.showToast(mActivity, msg);
                        break;
                    case "102"://验证码错误
                        ToastUtils.showToast(mActivity, msg);
                        break;
                    case "104"://该手机号码没注册，携带输入的手机号跳转到密码注册界面
                        goToPwdRegisterActivity();
                        break;
                    case "105":// 跳转到注册的第二步
                        // 获取到uid后携带uid跳转到RegisterActivity2界面
                        goTo_Activity(uid);
                        break;
                    case "106":// 跳转到注册的第二步
                        goTo_Activity(uid);
                        break;
                    case "0":// 登录成功，携带返回的参数跳转到单位界面，同时存一下把uid等参数保存到本地

                        // 保存参数
                        SpUtils.putParam(mActivity, Keys.UID, uid);
                        SpUtils.putParam(mActivity, Keys.BUSINESSID, businessId);
                        SpUtils.putParam(mActivity, Keys.BULIDINGID, buildingId);
                        SpUtils.putParam(mActivity, Keys.BUILDINGNAME, buildingName);
                        SpUtils.putParam(mActivity, Keys.BUSINESSNAME, businessName);
                        //跳转到主页面
                        enterHome();
                        break;
                }

            } else {
                System.out.println("Login_Message+++===没拿到bean对象");
            }
        }
    }


    /**
     * 携带输入的手机号跳转到密码注册界面
     */
    private void goToPwdRegisterActivity() {
        if (phoneNumber != null) {
            Intent intent = new Intent(Login_Message.this, PwdRegisterActivity.class);
            intent.putExtra(Keys.PHONE, phoneNumber);
            startActivity(intent);
        } else {
            ToastUtils.showToast(mActivity, "网络错误，请重试");
        }
    }

    /**
     * 携带参数跳转到注册的第二个界面
     */
    private void goTo_Activity(String uid) {
        if (uid != null) {
            Intent intent = new Intent(Login_Message.this, RegisterActivity2.class);
            intent.putExtra(Keys.UID, uid);
            startActivity(intent);
        } else {
            ToastUtils.showToast(mActivity, "网络错误，请重试");
        }
    }


    private void enterHome() {
        startActivity(new Intent(Login_Message.this, HomeActivity.class));
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
            ToastUtils.showToast(mActivity, "手机号码不能为空");
            return;
        } else if (!isLegal) {
            // 不合法，弹出对话框提示用户
            ToastUtils.showToast(mActivity, "请输入正确的手机号码");
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
        /*// 第一次请求获取验证的token
        getToken();*/
        // 第二次请求获取验证码
        if (token != null) {
            // 第一次请求获取验证的token不为为空，则第二次请求获取验证码
            getSecurity();

        } else {
            // 为空，则弹吐司提示用户
            ToastUtils.showToast(mActivity, "网络有误，请稍后再点");

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
       /* @Override
        public void onError(Call call, Exception e) {

        }*/

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
            } else {
                System.out.println("Login_Message+++===没拿到bean对象");

            }
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
}
