package com.anhubo.anhubo.ui.impl;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseFragment;
import com.anhubo.anhubo.ui.activity.Login_Register.AnhubaoDeal;
import com.anhubo.anhubo.ui.activity.Login_Register.Login_Message;
import com.anhubo.anhubo.ui.activity.MyDetial.InvateActivity;
import com.anhubo.anhubo.ui.activity.MyDetial.SettingActivity;
import com.anhubo.anhubo.ui.activity.WelcomeActivity;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;

/**
 * Created by Administrator on 2016/10/8.
 */
public class MyFragment extends BaseFragment {


    private LinearLayout llMyDetial;
    private LinearLayout llInvate;
    private LinearLayout llSetting;
    private TextView tvLogOut;

    @Override
    public void initTitleBar() {
        //设置菜单键隐藏
        iv_basepager_left.setVisibility(View.GONE);
//        tv_basepager_title.setText("我的界面");
    }

    @Override
    public Object getContentView() {
        return R.layout.fragment_my;
    }

    @Override
    public void initView() {
        // 找控件
        llMyDetial = findView(R.id.ll_My_Detial);
        llInvate = findView(R.id.ll_invate);
        llSetting = findView(R.id.ll_setting);
        tvLogOut = findView(R.id.tv_logOut);
    }

    @Override
    public void initListener() {
        // 设置监听
        llMyDetial.setOnClickListener(this);
        llInvate.setOnClickListener(this);
        llSetting.setOnClickListener(this);
        tvLogOut.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void processClick(View v) {
        switch (v.getId()) {
            case R.id.ll_My_Detial:

                break;
            case R.id.ll_invate:
                // 邀请
                enterInvate();
                break;
            case R.id.ll_setting:
                //设置
                enterSetting();
                break;
            case R.id.tv_logOut:
                logOut();
                break;
            default:
                break;
        }

    }
    /**进入设置界面*/
    private void enterSetting() {
        startActivity(new Intent(mActivity, SettingActivity.class));
    }

    /**进入邀请界面*/
    private void enterInvate() {
        Intent intent = new Intent(mActivity, InvateActivity.class);
        startActivity(intent);
    }
    /**登出方法*/
    private void logOut() {
        // 保存参数
        SpUtils.putParam(mActivity, Keys.UID,null);
        SpUtils.putParam(mActivity,Keys.BUSINESSID,null);
        SpUtils.putParam(mActivity,Keys.BULIDINGID,null);
        SpUtils.putParam(mActivity,Keys.BUILDINGNAME,null);
        SpUtils.putParam(mActivity,Keys.BUSINESSNAME,null);
        //跳转到主页面
        startActivity(new Intent(mActivity, Login_Message.class));
        getActivity().finish();
    }
}
