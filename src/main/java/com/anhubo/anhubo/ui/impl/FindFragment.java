package com.anhubo.anhubo.ui.impl;

import android.view.View;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseFragment;

/**
 * Created by Administrator on 2016/10/8.
 */
public class FindFragment extends BaseFragment {


    @Override
    public void initTitleBar() {
        //设置菜单键隐藏
        iv_basepager_left.setVisibility(View.GONE);
        tv_basepager_title.setText("发现");

    }

    @Override
    public Object getContentView() {
        return R.layout.fragment_find;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void processClick(View view) {

    }
}
