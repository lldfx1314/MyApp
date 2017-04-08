package com.anhubo.anhubo.ui.activity.Login_Register;

import android.app.Dialog;
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

import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.okhttp.Request;

/**
 * Created by Administrator on 2016/9/27.
 */
public class Login_Pwd extends BaseActivity {

    private static final String TAG = "Login_Pwd";
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
    @InjectView(R.id.btn_find_Pwd)
    Button btnFindPwd;
    private String phoneNumber;
    private boolean pwdIsVisible;
    private String pwd;
    private Dialog showDialog;
    private int pwdCount = 0;
    private String phone;

    @Override
    protected void initConfig() {
        super.initConfig();
        phone = getIntent().getStringExtra(Keys.PHONE);


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
        if (etPwdLoginPhone != null&&!TextUtils.isEmpty(phone)) {
            etPwdLoginPhone.setText(phone);
        }
        //设置当号码输入框有内容时显示小圆叉
        etPwdLoginPhone.addTextChangedListener(new InputWatcher(btnPwdLoginPhone, etPwdLoginPhone));
        etPwdLoginPwd.addTextChangedListener(new InputWatcher(btnPwdLoginPwdX, etPwdLoginPwd));
    }

    @Override
    protected void onLoadDatas() {

    }

    @OnClick({R.id.btn_pwdLogin_phone,R.id.btn_pwdLogin_pwdx, R.id.btn_pwdLogin_pwd, R.id.btn_loginPwd,R.id.btn_find_Pwd})
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
                    showdialog("请输入手机号码");
                    return;
                }
                if (phoneNumber.length() != 11) {
                    showdialog("请输入手机号码");
                    return;
                }
                if (!Utils.judgePhoneNumber(phoneNumber)) {
                    showdialog("请输入正确的手机号码");
                    return;
                }
                if (TextUtils.isEmpty(pwd)) {
                    showdialog("请输入密码");
                    return;
                }

//                if (!Utils.isRightLoginPwd(pwd)) {
                if (pwd.length()<8) {
                    showdialog("请输入正确密码");
                    return;
                }
                pwdLogin();
                break;
            case R.id.btn_find_Pwd:
                // 进入更改密码界面
                enterAlterPwd();
                break;
        }
    }

    private void showdialog(String string) {
        new AlertDialog(mActivity).builder()
                .setTitle("提示")
                .setMsg(string)
                .setCancelable(true).show();
    }

    /**进入更改密码界面 */
    private void enterAlterPwd() {
        Intent intent = new Intent(mActivity, FindPwdActivity.class);
        intent.putExtra(Keys.PHONE,phoneNumber);
        startActivity(intent);
    }

    /**
     * 获取输入的内容
     */
    private void getInputData() {

        //获取输入的手机号
        phoneNumber = etPwdLoginPhone.getText().toString().trim();
        //第二次密码
        pwd = etPwdLoginPwd.getText().toString().trim();
    }

    private void pwdLogin() {
        showDialog = loadProgressDialog.show(mActivity, "正在登录...");
        // 创建网络请求登录
        String url = Urls.Url_LoginPwd;
        HashMap<String, String> params = new HashMap<>();
        params.put("telphone", phoneNumber);
        params.put("password", pwd);
        params.put("version", versionName);
        LogUtils.eNormal(TAG,":密码登录:"+versionName);

        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback());


    }



    class MyStringCallback extends StringCallback {
        @Override
        public void onError(Request request, Exception e) {
            showDialog.dismiss();
            LogUtils.e(TAG,":pwdLogin:",e);
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("网络有问题，请检查")
                    .setCancelable(true).show();
        }

        @Override
        public void onResponse(String response) {
            showDialog.dismiss();
            LogUtils.eNormal(TAG+":pwdLogin:",response);
            Login_Bean bean = JsonUtil.json2Bean(response, Login_Bean.class);
            if (bean != null) {
                // 获取到数据
                int code = bean.code;
                String msg = bean.msg;
                Login_Bean.Data data = bean.data;
                String uid = data.uid;
                String buildingName = data.building_name;
                String buildingId = data.building_id;
                String businessId = data.business_id;
                String businessName = data.business_name;

                // 根据code值判断跳转到那个界面
                switch (code) {
                    case 101://网络错误
                        showdialog(msg);
                        break;
                    case 104://该手机号码没注册，携带输入的手机号跳转到密码注册界面
                        goToRegisterActivity();
                        break;
                    case 107://密码错误
                        pwdCount = pwdCount+1;
                        LogUtils.eNormal(TAG+":pwdCount:",pwdCount);
                        if(pwdCount == 3){
                            pwdCount = 0;
                            // 密码错误超过3次，弹窗提醒
                            showPwdErrorDialog();
                        }else {
                            // 密码错误弹窗
                            showdialog(msg);
                        }

                        break;
                    case 0:// 登录成功，携带返回的参数跳转到单位界面，同时存一下把uid等参数保存到本地
                        // 保存参数
                        SpUtils.putParam(mActivity,Keys.UID,uid);
                        SpUtils.putParam(mActivity,Keys.BUSINESSID,businessId);
                        SpUtils.putParam(mActivity,Keys.BUSINESSNAME,businessName);
                        SpUtils.putParam(mActivity, Keys.BULIDINGID, buildingId);
                        SpUtils.putParam(mActivity, Keys.BUILDINGNAME, buildingName);
                        //跳转到主页面
                        enterHome(uid);
                        break;
                }
            }
        }
    }
    /**密码错误超过3次，弹窗*/
    private void showPwdErrorDialog() {
       new AlertDialog(mActivity).builder()
                .setTitle("提示")
                .setMsg("连续输错3次密码,建议找回密码或者验证码登录")
                .setPositiveButton("验证码登录", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                })
                .setNegativeButton("找回密码", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        enterAlterPwd();
                    }
                })
                .setCancelable(false).show();
    }

        /**
         * 携带输入的手机号跳转到密码注册界面
         */
        private void goToRegisterActivity() {
            if (phoneNumber != null) {
                new AlertDialog(mActivity).builder()
                .setTitle("提示")
                        .setMsg("该账号不存在，请注册")
                        .setPositiveButton("去注册", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mActivity, RegisterActivity.class);
                                intent.putExtra(Keys.PHONE, phoneNumber);
                                intent.putExtra(Keys.PWDREGISTER,"PwdRegister");
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




    private void enterHome(String uid) {
        Intent intent = new Intent(mActivity, HomeActivity.class);
        startActivity(intent);
        // 把uid保存起来
        SpUtils.putParam(mActivity, Keys.UID, uid);
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

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }
}
