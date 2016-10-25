package com.anhubo.anhubo.ui.impl;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseFragment;
import com.anhubo.anhubo.ui.activity.Login_Register.Login_Message;

/**
 * Created by Administrator on 2016/10/8.
 */
public class MyFragment extends BaseFragment {

    private Button btnClickLogin;

    @Override
    public void initTitleBar() {
        //设置菜单键隐藏
        iv_basepager_left.setVisibility(View.GONE);
        tv_basepager_title.setText("我的界面");
    }

    @Override
    public Object getContentView() {
        return R.layout.fragment_my;
    }

    @Override
    public void initView() {
        btnClickLogin = findView(R.id.btn_click_login);

    }

    @Override
    public void initListener() {
        // 设置监听
        btnClickLogin.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void processClick(View v) {
        switch (v.getId()) {
            case R.id.btn_click_login:
                startActivity(new Intent(mActivity, Login_Message.class));
                break;
            default:
                break;
        }

    }
}
