package com.anhubo.anhubo.interfaces;

import android.view.View;

/**
 * Created by SHM on 2016/08/12.
 */
public interface UiInterface {

    /**
     * 初始化标题栏
     */
    void initTitleBar();
    /**
     * 获取界面布局的id
     */
    Object getContentView();

    /**
     * 初始化控件
     */
    void initView();

    /**
     * 注册监听器,适配器,注册广播接收者
     */
    void initListener();

    /**
     * 初始化界面数据
     */
    void initData();

    /**
     * 交给子类来处理BaseActivity未处理点击事件
     * @param view
     */
    void processClick(View view);
}
