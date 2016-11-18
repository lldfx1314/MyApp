package com.anhubo.anhubo.ui.impl;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseFragment;
import com.anhubo.anhubo.ui.activity.DiscoveryDetial.FeedActivity;
import com.anhubo.anhubo.ui.activity.DiscoveryDetial.NoticeActivity;

/**
 * Created by Administrator on 2016/10/8.
 */
public class FindFragment extends BaseFragment {


    RelativeLayout rlNotice;
    RelativeLayout rlFeedback;


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
        rlNotice = findView(R.id.rl_notice);
        rlFeedback = findView(R.id.rl_feedback);
    }

    @Override
    public void initListener() {
        // 设置监听
        rlNotice.setOnClickListener(this);
        rlFeedback.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void processClick(View view) {
        switch (view.getId()) {
            case R.id.rl_notice:
                startActivity(new Intent(mActivity, NoticeActivity.class));
                break;
            case R.id.rl_feedback:
                startActivity(new Intent(mActivity, FeedActivity.class));
                break;
        }
    }
}
