package com.anhubo.anhubo.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.ui.activity.Login_Register.Login_Message;
import com.anhubo.anhubo.ui.activity.Login_Register.RegisterActivity2;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;

/**
 * Created by Administrator on 2016/10/9.
 */
public class WelcomeActivity extends BaseActivity {

    private String uid;
    private String bulidingid;
    private String businessid;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initEvents() {
        uid = SpUtils.getStringParam(mActivity, Keys.UID);
        bulidingid = SpUtils.getStringParam(mActivity, Keys.BULIDINGID);
        businessid = SpUtils.getStringParam(mActivity, Keys.BUSINESSID);
    }

    @Override
    protected void onLoadDatas() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!TextUtils.isEmpty(uid)) {
                    if(!TextUtils.isEmpty(bulidingid)||!TextUtils.isEmpty(businessid)) {
                        //跳转到主页面
                        enterHome();
                    }else{
                        // 跳到注册第二个界面
                        enterRegister2();
                    }

                }else{
                    // 无uid，跳到登录界面
                    enterLogin();

                }

            }


        }, 2000);

    }

    private void enterRegister2() {
        Intent intent = new Intent(WelcomeActivity.this, RegisterActivity2.class);
        intent.putExtra(Keys.UID, uid);
        startActivity(intent);
    }

    private void enterLogin() {
        startActivity(new Intent(WelcomeActivity.this, Login_Message.class));
        finish();
    }

    private void enterHome() {
        startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {

    }
}
