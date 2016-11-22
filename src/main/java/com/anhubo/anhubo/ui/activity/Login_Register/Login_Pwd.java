package com.anhubo.anhubo.ui.activity.Login_Register;

import android.content.Intent;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.Login_Bean;
import com.anhubo.anhubo.protocol.RequestResultListener;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.ui.activity.HomeActivity;
import com.anhubo.anhubo.utils.InputWatcher;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.utils.Utils;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;

import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by Administrator on 2016/9/27.
 */
public class Login_Pwd extends BaseActivity {

    @InjectView(R.id.et_pwdLogin_phone)
    EditText etPwdLoginPhone;
    @InjectView(R.id.btn_pwdLogin_phone)
    Button btnPwdLoginPhone;
    @InjectView(R.id.et_pwdLogin_pwd)
    EditText etPwdLoginPwd;
    @InjectView(R.id.btn_pwdLogin_pwd)
    Button btnPwdLoginPwd;
    @InjectView(R.id.btn_loginPwd)
    Button btnLoginPwd;
    @InjectView(R.id.btn_pwdLogin_pwdx)
    Button btnPwdLoginPwdX;
    private String phoneNumber;
    private boolean pwdIsVisible;
    private String pwd;

    @Override
    protected void initConfig() {
        super.initConfig();
        // 在这儿先获取到uid
        phoneNumber = getIntent().getStringExtra(Keys.PHONE);


    }

    @Override
    protected int getContentViewId() {
        return R.layout.login_pwd_activity;
    }

    @Override
    protected void initViews() {

    }


    @Override
    protected void initEvents() {
        super.initEvents();
        // 设置手机号输入框的默认显示内容
        if (etPwdLoginPhone != null) {
            etPwdLoginPhone.setText(phoneNumber);
        }
        //设置当号码输入框有内容时显示小圆叉
        etPwdLoginPhone.addTextChangedListener(new InputWatcher(btnPwdLoginPhone, etPwdLoginPhone));
        etPwdLoginPwd.addTextChangedListener(new InputWatcher(btnPwdLoginPwdX, etPwdLoginPwd));
    }

    @Override
    protected void onLoadDatas() {

    }

    @OnClick({R.id.btn_pwdLogin_phone,R.id.btn_pwdLogin_pwdx, R.id.btn_pwdLogin_pwd, R.id.btn_loginPwd})
    public void onClick(View view) {
        getInputData();
        switch (view.getId()) {
            case R.id.btn_pwdLogin_phone:
                // 号码小圆叉
                btnPwdLoginPhone.setText("");
                break;
            case R.id.btn_pwdLogin_pwdx:
                // 密码小圆叉
                btnPwdLoginPwdX.setText("");
                break;
            case R.id.btn_pwdLogin_pwd:
                // 改变密码的可见状态
                changePwdVisible(etPwdLoginPwd);
                break;
            case R.id.btn_loginPwd:
                // 点击密码登录

                if (TextUtils.isEmpty(phoneNumber)) {
                    ToastUtils.showToast(mActivity, "请输入手机号码");
                    return;
                }
                if (phoneNumber.length() != 11) {
                    ToastUtils.showToast(mActivity, "手机号码长度为11");
                    return;
                }
                if (!Utils.judgePhoneNumber(phoneNumber)) {
                    ToastUtils.showToast(mActivity, "请输入正确的手机号码");
                    return;
                }
                if (TextUtils.isEmpty(pwd)) {
                    ToastUtils.showToast(mActivity, "请输入密码");
                    return;
                }

                if (!Utils.isRightPwd(pwd)) {
                    ToastUtils.showLongToast(mActivity, "请输入8-16位数字和字母的组合");
                    return;
                }
                pwdLogin();
                break;
        }
    }

    /**
     * 获取输入的内容
     */
    private void getInputData() {


        // 在这儿获取到传过来的手机号
        phoneNumber = getIntent().getStringExtra(Keys.PHONE);
        //获取输入的手机号
        phoneNumber = etPwdLoginPhone.getText().toString().trim();
        //第二次密码
        pwd = etPwdLoginPwd.getText().toString().trim();
    }

    private void pwdLogin() {

        // 创建网络请求登录
        String url = Urls.Url_LoginPwd;
        HashMap<String, String> params = new HashMap<>();
        params.put("telphone", phoneNumber);
        params.put("password", pwd);

        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback());


    }

    class MyStringCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {

            System.out.println("Login_Pwd+++===没拿到数据" + e.getMessage());
        }

        @Override
        public void onResponse(String response) {
            Login_Bean bean = new Gson().fromJson(response, Login_Bean.class);
            if (bean != null) {
                // 获取到数据
                String code = bean.code;
                String msg = bean.msg;
                Login_Bean.Data data = bean.data;
                String uid = data.uid;
                String buildingName = data.building_name;
                String buildingId = data.building_id;
                String businessName = data.business_name;
                String businessId = data.business_id;

                // 根据code值判断跳转到那个界面
                switch (code) {
                    case "101"://网络错误
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
                    case "107"://密码错误
                        ToastUtils.showToast(mActivity, msg);
                        break;
                    case "108"://手机号码未注册
                        goToPwdRegisterActivity();
                        break;
                    case "0":// 登录成功，携带返回的参数跳转到单位界面，同时存一下把uid等参数保存到本地
                        // 保存参数
                        SpUtils.putParam(mActivity,Keys.UID,uid);
                        SpUtils.putParam(mActivity,Keys.BUSINESSID,businessId);
                        SpUtils.putParam(mActivity,Keys.BULIDINGID,buildingId);
                        SpUtils.putParam(mActivity,Keys.BUILDINGNAME,buildingName);
                        SpUtils.putParam(mActivity,Keys.BUSINESSNAME,businessName);
                        //跳转到主页面
                        enterHome();
                        break;
                }
            } else {
                System.out.println("Login_Pwd界面没获取到bean对象");
            }
        }
    }

        /**
         * 携带参数跳转到注册的第二个界面
         */
        private void goTo_Activity(String uid) {
            if (uid != null) {
                Intent intent = new Intent(Login_Pwd.this, RegisterActivity2.class);
                intent.putExtra(Keys.UID, uid);
                startActivity(intent);
            } else {
                ToastUtils.showToast(mActivity, "网络错误，请重试");
            }
        }

        /**
         * 携带输入的手机号跳转到密码注册界面
         */
        private void goToPwdRegisterActivity() {
            if (phoneNumber != null) {
                Intent intent = new Intent(Login_Pwd.this, PwdRegisterActivity.class);
                intent.putExtra(Keys.PHONE, phoneNumber);
                startActivity(intent);
            } else {
                ToastUtils.showToast(mActivity, "网络错误，请重试");
            }
        }




    private void enterHome() {
        startActivity(new Intent(Login_Pwd.this, HomeActivity.class));
        // 发送一条广播，登录完成后关闭登录的所有界面
        mActivity.sendBroadcast(new Intent(INTENT_FINISH));
    }

    /**
     * 改变密码的可见状态
     *
     * @param editText
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
}
