package com.anhubo.anhubo.ui.activity.Login_Register;

import android.app.Dialog;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.Login_AlterPwdBean;
import com.anhubo.anhubo.bean.Security_Bean;
import com.anhubo.anhubo.bean.Security_Token_Bean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.InputWatcher;
import com.anhubo.anhubo.utils.JsonUtil;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.utils.Utils;
import com.anhubo.anhubo.view.AlertDialog;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;

import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by LUOLI on 2016/12/22.
 */
public class FindPwdActivity extends BaseActivity {
    private static final String TAG = "FindPwdActivity";
    @InjectView(R.id.et_alterPwd_phoneNumber)
    EditText etAlterPwdPhoneNumber;
    @InjectView(R.id.btn_alterPwd_phoneNumber)
    Button btnAlterPwdPhoneNumber;
    @InjectView(R.id.et_alterPwd_security)
    EditText etAlterPwdSecurity;
    @InjectView(R.id.tv_alterPwd_security)
    TextView tvAlterPwdSecurity;
    @InjectView(R.id.et_alterPwd1)
    EditText etAlterPwd1;
    @InjectView(R.id.btn_alterPwdIsVisible1)
    Button btnAlterPwdIsVisible1;
    @InjectView(R.id.btn_alterPwdX1)
    Button btnAlterPwdX1;
    @InjectView(R.id.et_alterPwd2)
    EditText etAlterPwd2;
    @InjectView(R.id.btn_alterPwdIsVisible2)
    Button btnAlterPwdIsVisible2;
    @InjectView(R.id.btn_alterPwdX2)
    Button btnAlterPwdX2;
    @InjectView(R.id.btn_alterPwd)
    Button btnAlterPwd;
    private boolean pwdIsVisible = false;//记录密码是否显示
    private String phoneNumber;
    private String security;
    private String pwd1;
    private String pwd2;
    private String token;
    private Dialog showDialog;
    private String phone;

    @Override
    protected void initConfig() {
        super.initConfig();
        phone = getIntent().getStringExtra(Keys.PHONE);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_pwd;
    }

    @Override
    protected void initViews() {

        // 设置手机号输入框的默认显示内容
        if (etAlterPwdPhoneNumber != null&&!TextUtils.isEmpty(phone)) {
            etAlterPwdPhoneNumber.setText(phone);
        }


        //设置当号码输入框有内容时显示小圆叉
        etAlterPwdPhoneNumber.addTextChangedListener(new InputWatcher(btnAlterPwdPhoneNumber, etAlterPwdPhoneNumber));
        etAlterPwd1.addTextChangedListener(new InputWatcher(btnAlterPwdX1, etAlterPwd1));
        etAlterPwd2.addTextChangedListener(new InputWatcher(btnAlterPwdX2, etAlterPwd2));
    }

    @Override
    protected void onLoadDatas() {

    }


    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }


    @OnClick({R.id.btn_alterPwd_phoneNumber, R.id.btn_alterPwdIsVisible1, R.id.btn_alterPwdX1, R.id.btn_alterPwdIsVisible2, R.id.btn_alterPwdX2, R.id.tv_alterPwd_security, R.id.btn_alterPwd})
    public void onClick(View view) {
        /**获取输入框输入的内容*/
        getInputData();
        switch (view.getId()) {
            case R.id.btn_alterPwdIsVisible1:
                changePwdVisible(etAlterPwd1);
                break;
            case R.id.btn_alterPwdIsVisible2:
                changePwdVisible(etAlterPwd2);
                break;
            case R.id.btn_alterPwd_phoneNumber:// 号码小园茶
                etAlterPwdPhoneNumber.setText("");
                break;
            case R.id.btn_alterPwdX1:
                etAlterPwd1.setText("");
                break;
            case R.id.btn_alterPwdX2:
                etAlterPwd2.setText("");
                break;
            case R.id.tv_alterPwd_security:// 发送验证码
                // 定义一个方法获取验证码
                getSecurityCode();
                break;
            case R.id.btn_alterPwd:// 提交更改
                submit();
                break;
        }
    }

    /**
     * 提交更改的密码
     */
    private void submit() {

        if (TextUtils.isEmpty(phoneNumber)) {
            showdialog("请输入手机号");
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
        if (TextUtils.isEmpty(security)) {
            showdialog("请输入验证码");
            return;
        }
        if (security.length() != 4) {
            showdialog("验证码长度为4");
            return;
        }

        if (!TextUtils.isEmpty(pwd1)) {
            boolean isnewPwd1 = Utils.isRightPwd(pwd1);
            if (!isnewPwd1) {
                showdialog("请输入8-16位数字和字母的组合");
                return;
            }
        } else {
            showdialog("请输入新密码");
            return;
        }
        if (!TextUtils.isEmpty(pwd2)) {
            if (!TextUtils.equals(pwd1, pwd2)) {

                showdialog("两次输入不一致");
                return;
            }
        } else {
            showdialog("请再次输入新密码");
            return;
        }

        findPwd();
    }

    private void findPwd() {
        showDialog = loadProgressDialog.show(mActivity, "正在找回密码...");
        String url = Urls.Url_Login_AlterPwd;
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", phoneNumber);
        params.put("verify_code", security);
        params.put("password", pwd1);

        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback2());
    }

    private void showdialog(String string) {
        new AlertDialog(mActivity).builder()
                .setTitle("提示")
                .setMsg(string)
                .setCancelable(true).show();
    }

    Handler handler = new Handler();

    class MyStringCallback2 extends StringCallback {

        @Override
        public void onError(Call call, Exception e) {
            showDialog.dismiss();
            LogUtils.e(TAG,":findPwd:",e);
        }

        @Override
        public void onResponse(String response) {
            LogUtils.eNormal(TAG+":findPwd:",response);
            Login_AlterPwdBean bean = JsonUtil.json2Bean(response, Login_AlterPwdBean.class);
            if (bean != null) {
                showDialog.dismiss();
                int code = bean.code;
                if (code == 0) {
                    new AlertDialog(mActivity).builder()
                            .setTitle("提示")
                            .setMsg("找回密码成功，前去登录")
                            .setPositiveButton("", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            finish();
                                        }
                                    }, 500);
                                }
                            })
                            .setCancelable(false).show();
                }else if(code == 1){
                    new AlertDialog(mActivity).builder()
                            .setTitle("提示")
                            .setMsg("验证码错误")
                            .setCancelable(true).show();
                }
            }
        }
    }

    /**
     * 获取输入框输入的内容
     */
    private void getInputData() {
        phoneNumber = etAlterPwdPhoneNumber.getText().toString().trim();
        security = etAlterPwdSecurity.getText().toString().trim();
        pwd1 = etAlterPwd1.getText().toString().trim();
        pwd2 = etAlterPwd2.getText().toString().trim();
    }

    private boolean isLegal;// 记录手机号码是否合法

    private void getSecurityCode() {
        // 调用方法先判断手机号码是否合法
        isLegal = Utils.judgePhoneNumber(phoneNumber);
        if (TextUtils.isEmpty(phoneNumber)) {
            showdialog("请输入手机号");
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
        Utils.setEditTextSelection(etAlterPwdSecurity);
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
        Utils.setSecurityTextView(tvAlterPwdSecurity);

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
        @Override
        public void onError(Call call, Exception e) {

            System.out.println("RegisterActivity+++获取验证码===没拿到数据" + e.getMessage());
        }

        @Override
        public void onResponse(String response) {
            Security_Bean bean = new Gson().fromJson(response, Security_Bean.class);

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
}
