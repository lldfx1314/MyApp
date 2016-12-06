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
import com.anhubo.anhubo.bean.Register1_Bean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.InputWatcher;
import com.anhubo.anhubo.utils.Keys;
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
 * 密码注册的界面
 * Created by Administrator on 2016/9/29.
 */
public class PwdRegisterActivity extends BaseActivity {

    @InjectView(R.id.et_pwdReg1)
    EditText etPwdRegFirst;
    @InjectView(R.id.btn_pwdReg1)
    Button btnPwdRegFirst;
    @InjectView(R.id.et_pwdReg2)
    EditText etPwdRegSecond;
    @InjectView(R.id.btn_pwdReg2)
    Button btnPwdRegSecond;
    @InjectView(R.id.btn_pwdRegister)
    Button btnPwdRegister;
    @InjectView(R.id.btn_pwdRegX1)
    Button btnPwdRegX1;
    @InjectView(R.id.btn_pwdRegX2)
    Button btnPwdRegX2;
    private String phoneNumber;
    private String firstPwd;
    private String secondPwd;
    private boolean pwdIsVisible = false;

    @Override
    protected void initConfig() {
        super.initConfig();

    }

    @Override
    protected int getContentViewId() {
        return R.layout.register_pwd_activity;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initEvents() {
        super.initEvents();
        etPwdRegFirst.addTextChangedListener(new InputWatcher(btnPwdRegX1, etPwdRegFirst));
        etPwdRegSecond.addTextChangedListener(new InputWatcher(btnPwdRegX2, etPwdRegSecond));
    }

    @Override
    protected void onLoadDatas() {

    }

    @OnClick({R.id.btn_pwdReg1, R.id.btn_pwdReg2, R.id.btn_pwdRegister,R.id.btn_pwdRegX1, R.id.btn_pwdRegX2})
    public void onClick(View view) {
        getInputData();
        switch (view.getId()) {
            case R.id.btn_pwdReg1://密码1的点击事件(是否可见)
                changePwdVisible(etPwdRegFirst);
                break;
            case R.id.btn_pwdReg2://密码2的点击事件(是否可见)
                changePwdVisible(etPwdRegSecond);
                break;
            case R.id.btn_pwdRegX1:
                etPwdRegFirst.setText("");
                break;
            case R.id.btn_pwdRegX2:
                etPwdRegSecond.setText("");
                break;
            case R.id.btn_pwdRegister:
                if (!Utils.isRightPwd(firstPwd)) {
                    ToastUtils.showLongToast(mActivity, "请输入8-16位数字和字母的组合");
                    return;
                }
                if (TextUtils.isEmpty(firstPwd)) {
                    ToastUtils.showToast(mActivity, "请输入密码");
                    return;
                }
                if (TextUtils.isEmpty(secondPwd)) {
                    ToastUtils.showToast(mActivity, "请再次输入密码");
                    return;
                }
                if (!TextUtils.equals(firstPwd, secondPwd)) {
                    ToastUtils.showToast(mActivity, "两次密码输入不一致");
                    return;
                }
                //　拿着号码和密码，调用接口
                enterRegister();
                break;
        }
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

    /**
     * 拿着号码和密码，调用接口
     */
    private void enterRegister() {
        String url = Urls.Url_PwdRegister;
        // 封装请求参数
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("telphone", phoneNumber);
        params.put("password", firstPwd);

        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback());
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }

    class MyStringCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {

            System.out.println("PwdRegisterActivity+++===没拿到数据" + e.getMessage());
        }

        @Override
        public void onResponse(String response) {
            Register1_Bean bean = new Gson().fromJson(response, Register1_Bean.class);
            if (bean != null) {
                int uid = bean.data.uid;
                //System.out.println("拿到uid了"+uid);
                // 获取到uid后携带uid跳转到RegisterActivity2界面
                if (uid != 0) {
                    Intent intent = new Intent(PwdRegisterActivity.this, RegisterActivity2.class);
                    //System.out.println("要传递的uid+++===+++" + uid);
                    intent.putExtra(Keys.UID, String.valueOf(uid));
                    startActivity(intent);
                } else {
                    ToastUtils.showToast(mActivity, "网络错误，请重试");
                }

            } else {
                System.out.println("PwdRegisterActivity没拿到uid");
            }
        }
    }


    /**
     * 获取输入的内容
     */
    private void getInputData() {
        // 在这儿获取到手机号
        phoneNumber = getIntent().getStringExtra(Keys.PHONE);
        //第一次密码
        firstPwd = etPwdRegFirst.getText().toString().trim();
        //第二次密码
        secondPwd = etPwdRegSecond.getText().toString().trim();
    }
}
