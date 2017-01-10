package com.anhubo.anhubo.ui.activity.Login_Register;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.Register1_Bean;
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
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;

import okhttp3.Call;

/**
 * 这是注册界面
 * Created by Administrator on 2016/9/27.
 */
public class RegisterActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "RegisterActivity";
    private Button btnRegister1;
    private TextView tvRegSecurity;
    private String token;
    private EditText etRegphoneNumber;
    private String phoneNumber;
    private EditText etRegPwd;
    private CheckBox cbRegAnhubo;
    private EditText etRegSecurity;
    private String securityCode;
    private String pwd;
    private Button btnRegphoneNumber;
    private boolean pwdIsVisible = false;//记录密码是否显示
    private Button btnRegPwdIsVisible;
    private Button btnRegPwd2IsVisible;
    private TextView tvDeal;
    private Button btnRegPwdX;
    private String unionid;
    private String imageUrl;
    private String weixinName;
    private String weixinforzhuce;
    private String loginforzhuce;
    private String PwdRegister;
    private AlertDialog builder;
    private Dialog showDialog;
    private EditText etRegPwd2;
    private Button btnRegPwdX2;
    private String pwd2;


    @Override
    protected void initConfig() {
        super.initConfig();
        Intent intent = getIntent();
        loginforzhuce = intent.getStringExtra(Keys.LOGINFORZHUCE);
        weixinforzhuce = intent.getStringExtra(Keys.WEIXINFORZHUCE);
        PwdRegister = intent.getStringExtra(Keys.PWDREGISTER);

        unionid = intent.getStringExtra(Keys.UNIONID);
        imageUrl = intent.getStringExtra(Keys.PROFILE_IMAGE_URL);
        weixinName = intent.getStringExtra(Keys.SCREEN_NAME);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.register_activity;
    }

    @Override
    protected void initViews() {
        builder = new AlertDialog(mActivity).builder();
        /*找控件*/
        // 下一步
        btnRegister1 = (Button) findViewById(R.id.btn_register1);
        // 获取验证码
        tvRegSecurity = (TextView) findViewById(R.id.tv_reg_security);
        // 输入手机号
        etRegphoneNumber = (EditText) findViewById(R.id.et_reg_phoneNumber);
        // 输入验证码
        etRegSecurity = (EditText) findViewById(R.id.et_reg_security);
        // 输入密码
        etRegPwd = (EditText) findViewById(R.id.et_reg_pwd);
        // 输入邀请码
        etRegPwd2 = (EditText) findViewById(R.id.et_reg_pwd2);
        // 协议的CheckBox
        cbRegAnhubo = (CheckBox) findViewById(R.id.cb_reg_anhubo);
        // 电话号码的小圆叉
        btnRegphoneNumber = (Button) findViewById(R.id.btn_reg_phoneNumber);
        // 密码1可见
        btnRegPwdIsVisible = (Button) findViewById(R.id.btn_reg_pwdIsVisible);
        // 密码1小圆叉
        btnRegPwdX = (Button) findViewById(R.id.btn_reg_pwdx);
        // 密码2可见
        btnRegPwd2IsVisible = (Button) findViewById(R.id.btn_reg_pwdIsVisible2);
        // 密码2小圆叉
        btnRegPwdX2 = (Button) findViewById(R.id.btn_reg_pwdx2);


        String phone = getIntent().getStringExtra(Keys.PHONE);
        // 设置显示电话号码
        if(!TextUtils.isEmpty(phone)){
            etRegphoneNumber.setText(phone);
        }

        // 协议
        tvDeal = (TextView) findViewById(R.id.tv_deal);
        // 获取协议里面的内容
        String anhuboDeal = "我已经阅读<<安互保服务协议>>并同意";
        SpannableString ss = new SpannableString(anhuboDeal);
        String url = Urls.Url_Deal;
        MyURLSpan myURLSpan = new MyURLSpan(url);
        ss.setSpan(myURLSpan, 5, 16, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE);
        tvDeal.setText(ss);
        tvDeal.setMovementMethod(LinkMovementMethod.getInstance());//设置可以点击超链接
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }

    class MyURLSpan extends URLSpan {

        public MyURLSpan(String url) {
            super(url);
        }

        @Override
        public void onClick(View widget) {
            //super.onClick(widget);
            Intent intent = new Intent(RegisterActivity.this, AnhubaoDeal.class);
            intent.putExtra(Keys.ANHUBAODEAL, getURL());
            startActivity(intent);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            //super.updateDrawState(ds);
            ds.setColor(Color.parseColor("#3178C8"));//设置文字的颜色
            ds.setUnderlineText(true);//设置是否显示下划线
        }
    }

    @Override
    protected void initEvents() {
        super.initEvents();

        // 设置监听
        btnRegister1.setOnClickListener(this);
        //验证码
        tvRegSecurity.setOnClickListener(this);
        // CheckBox
        cbRegAnhubo.setOnCheckedChangeListener(this);
        // 右边小圆叉
        btnRegphoneNumber.setOnClickListener(this);
        // 密码1小圆叉
        btnRegPwdX.setOnClickListener(this);
        // 密码2小圆叉
        btnRegPwdX2.setOnClickListener(this);
        // 监听号码输入框状态，控制右边小圆叉
        etRegphoneNumber.addTextChangedListener(new InputWatcher(btnRegphoneNumber, etRegphoneNumber));
        etRegPwd.addTextChangedListener(new InputWatcher(btnRegPwdX, etRegPwd));
        etRegPwd2.addTextChangedListener(new InputWatcher(btnRegPwdX2, etRegPwd2));

        btnRegPwdIsVisible.setOnClickListener(this);
        btnRegPwd2IsVisible.setOnClickListener(this);
    }

    /**
     * CheckBox的状态
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // 复选框的点击事件
        if (isChecked) {
            cbRegAnhubo.setButtonDrawable(R.drawable.checkbox2_reg_login);
        } else {
            cbRegAnhubo.setButtonDrawable(null);
        }
    }

    @Override
    protected void onLoadDatas() {

    }

    @Override
    public void onClick(View v) {
        /**获取输入框输入的内容*/
        getInputData();
        switch (v.getId()) {

            case R.id.btn_register1: // 注册提交
                submit();
                break;
            case R.id.tv_reg_security: // 获取验证码
                // 定义一个方法获取验证码
                getSecurityCode();

                break;
            case R.id.btn_reg_phoneNumber: // 号码的小圆叉
                etRegphoneNumber.setText("");
                break;
            case R.id.btn_reg_pwdx: // 密码1的小圆叉
                etRegPwd.setText("");
                break;
            case R.id.btn_reg_pwdIsVisible: // 使密码1可见
                //  改变密码1的可见状态
                changePwdVisible(etRegPwd);
                break;
            case R.id.btn_reg_pwdx2: // 密码2的小圆叉
                etRegPwd2.setText("");
                break;
            case R.id.btn_reg_pwdIsVisible2: // 使密码2可见
                //  改变密码2的可见状态
                changePwdVisible(etRegPwd2);
                break;
            default:
                break;
        }

    }

    /**
     * 改变密码的可见状态
     */
    private void changePwdVisible(EditText editText) {
        if (!pwdIsVisible) {
            //设置EditText的密码为可见的
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            //设置密码为隐藏的
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        pwdIsVisible = !pwdIsVisible;
    }

    /**
     * 获取输入的内容
     */
    private void getInputData() {
        //手机号
        phoneNumber = etRegphoneNumber.getText().toString().trim();
        //验证码
        securityCode = etRegSecurity.getText().toString().trim();
        //密码1
        pwd = etRegPwd.getText().toString().trim();
        //密码2
        pwd2 = etRegPwd2.getText().toString().trim();

    }

    /**
     * 进入注册的第二个页面
     */
    private void submit() {
        if (TextUtils.isEmpty(phoneNumber)) {

            showdialog("请输入手机号码");
            return;
        }
        if (phoneNumber.length() != 11) {

            showdialog("手机号码长度为11");
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
        if (TextUtils.isEmpty(pwd)) {

            showdialog("请输入密码");
            return;
        }
        if (!Utils.isRightPwd(pwd)) {

            showdialog("请输入8-16位数字和字母的组合");
            return;
        }

        if (TextUtils.isEmpty(pwd2)) {
            showdialog("请再次输入密码");
            return;
        }
        if (!TextUtils.equals(pwd, pwd2)) {
            showdialog("两次密码输入不一致");
            return;
        }

        if (!cbRegAnhubo.isChecked()) {
            ToastUtils.showToast(mActivity, "请先阅读协议");
            return;
        }

        showDialog = loadProgressDialog.show(mActivity, "正在处理...");

        if (!TextUtils.isEmpty(loginforzhuce)||!TextUtils.isEmpty(PwdRegister)) {
            // 再次请求网络，获取uid
            //System.out.println("=======正常注册=======");
            getUId();
        } else if (!TextUtils.isEmpty(weixinforzhuce)) {
            //System.out.println("=======微信注册=======");
            getUId_weixin();
        }
    }

    /**
     * 网络请求
     * 微信注册的网络请求
     */
    private void getUId_weixin() {
        String url = Urls.Url_Enter_RegisterActivity2;
        // 封装请求参数
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("telphone", phoneNumber);

        params.put("verify_code", securityCode);

        params.put("password", pwd);



        params.put("third_type", 2 + "");//第三方类型，2代表微信
        params.put("pic_url", imageUrl);// 头像
        params.put("unique_id", unionid);// 微信uid
        params.put("third_name", weixinName);// 微信昵称

        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback3());
    }

    /**
     * 微信注册的网络请求
     */
    class MyStringCallback3 extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {
            showDialog.dismiss();
            LogUtils.e(TAG,":getUId_weixin:",e);
        }

        @Override
        public void onResponse(String response) {
            showDialog.dismiss();
            LogUtils.eNormal(TAG+":getUId_weixin:",response);
            Register1_Bean register1Bean = JsonUtil.json2Bean(response, Register1_Bean.class);
            if (register1Bean != null) {
                String code = register1Bean.code;
                String msg = register1Bean.msg;
                String uid = register1Bean.data.uid;
                switch (code) {
                    case "0":// 注册成功
                        // 进入首页
                        enterHomeActivity(uid);
                        break;
                    case "102":// 验证码错误
                        showdialog(msg);
                        break;
                    case "103":// 邀请码错误
                        showdialog(msg);
                        break;
                    case "108":// 该手机号码已注册
                        showdialog(msg);
                        break;

                }
            }
        }
    }
    /**弹窗提示*/
    private void showdialog(String string) {
        builder
                .setTitle("提示")
                .setMsg(string)
                .setCancelable(true).show();
    }


    /**
     * 网络请求
     * 下一步的网络请求
     */
    private void getUId() {
        String url = Urls.Url_Enter_RegisterActivity2;
        // 封装请求参数
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("telphone", phoneNumber);
        params.put("verify_code", securityCode);
        params.put("password", pwd);

        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        showDialog.dismiss();
                        LogUtils.e(TAG,":getUId:",e);
                    }

                    @Override
                    public void onResponse(String response) {
                        showDialog.dismiss();
                        LogUtils.eNormal(TAG+":getUId:",response);
                        Register1_Bean bean = JsonUtil.json2Bean(response, Register1_Bean.class);
                        dealBean(bean);
                    }
                });
    }

    private void dealBean(Register1_Bean bean) {
        if (bean != null) {
            String code = bean.code;
            String msg = bean.msg;
            String uid = bean.data.uid;
            switch (code) {
                case "0":// 注册成功
                    // 进入首页
                    enterHomeActivity(uid);
                    break;
                case "102":// 验证码错误
                    showdialog(msg);
                    break;
                case "103":// 邀请码错误
                    showdialog(msg);
                    break;
                case "108":// 该手机号码已注册
                    builder
                            .setTitle("提示")
                            .setMsg(msg)
                            .setPositiveButton("去登陆", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                }
                            })
                            .setCancelable(false).show();
                    break;

            }
        }
    }

    /**
     * 进入首页界面
     */
    private void enterHomeActivity(String uid) {
        // 获取到uid后携带uid跳转到RegisterActivity2界面
        if (!TextUtils.isEmpty(uid)) {
            Intent intent = new Intent(mActivity, HomeActivity.class);
            startActivity(intent);
            // 把uid保存起来
            SpUtils.putParam(mActivity, Keys.UID, uid);
            // 发送一条广播，登录完成后关闭登录的所有界面
            mActivity.sendBroadcast(new Intent(INTENT_FINISH));
        } else {
            showdialog("网络错误，请重试");
        }
    }


    private boolean isLegal;// 记录手机号码是否合法

    /**
     * 获取验证码
     */
    private void getSecurityCode() {
        // 调用方法先判断手机号码是否合法
        isLegal = Utils.judgePhoneNumber(phoneNumber);
        if (TextUtils.isEmpty(phoneNumber)) {
            showdialog("手机号码不能为空");
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
        Utils.setEditTextSelection(etRegSecurity);
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
                .execute(new MyStringCallback1());
    }

    class MyStringCallback1 extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {
            System.out.println("UnitMenuActivity+++token===没拿到数据" + e.getMessage());
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
        Utils.setSecurityTextView(tvRegSecurity);

        String url = Urls.Url_Security;
        HashMap<String, String> params = new HashMap<>();
        params.put("mobile", phoneNumber);
        params.put("token", token);

        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback2());
    }

    class MyStringCallback2 extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {

            System.out.println("RegisterActivity+++获取验证码===没拿到数据" + e.getMessage());
        }

        @Override
        public void onResponse(String response) {
            Security_Bean bean = new Gson().fromJson(response, Security_Bean.class);

        }
    }

}