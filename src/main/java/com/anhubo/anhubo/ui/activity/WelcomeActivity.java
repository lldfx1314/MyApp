package com.anhubo.anhubo.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.receiver.MyReceiver;
import com.anhubo.anhubo.ui.activity.Login_Register.Login_Message;
import com.anhubo.anhubo.ui.activity.Login_Register.RegisterActivity2;
import com.anhubo.anhubo.ui.activity.buildDetial.TestActivity;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;

/**
 * Created by Administrator on 2016/10/9.
 */
public class WelcomeActivity extends BaseActivity {

    private String uid;
    private String bulidingid;
    private String businessid;
    private MyReceiver receiver;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void initViews() {
        // 极光推送
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
        // 注册广播接受者
        System.out.println("111111111********");
        registerMessageReceiver();
    }
    @Override
    protected void initEvents() {
        System.out.println("222222222222********");
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

    public void registerMessageReceiver() {
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        System.out.println("1111222222********");
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        //设置REGISTRATION_ID的Action
        filter.addAction(JPushInterface.ACTION_REGISTRATION_ID);
        mActivity.registerReceiver(receiver, filter);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivity.unregisterReceiver(receiver);
    }
}
