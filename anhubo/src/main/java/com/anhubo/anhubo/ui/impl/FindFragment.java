package com.anhubo.anhubo.ui.impl;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseFragment;
import com.anhubo.anhubo.ui.activity.DiscoveryDetial.FeedActivity;
import com.anhubo.anhubo.ui.activity.DiscoveryDetial.NoticeActivity;
import com.anhubo.anhubo.ui.activity.DiscoveryDetial.UseGuideActivity;
import com.anhubo.anhubo.ui.activity.buildDetial.TestActivity;
import com.anhubo.anhubo.ui.activity.unitDetial.QrScanActivity;
import com.anhubo.anhubo.utils.Keys;

/**
 * Created by Administrator on 2016/10/8.
 */
public class FindFragment extends BaseFragment {


    private RelativeLayout rlNotice;
    private RelativeLayout rlFeedback;
    private RelativeLayout rlTest;
    private RelativeLayout rlUseGuide;


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
        rlTest = findView(R.id.rl_test);
        rlUseGuide = findView(R.id.rl_useGuide);
    }

    @Override
    public void initListener() {
        // 设置监听
        rlNotice.setOnClickListener(this);
        rlFeedback.setOnClickListener(this);
        rlTest.setOnClickListener(this);
        rlUseGuide.setOnClickListener(this);
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
            case R.id.rl_test:
                Intent intent = new Intent(mActivity, QrScanActivity.class);
                intent.putExtra(Keys.TEST, "test");
                startActivity(intent);
                break;
            case R.id.rl_useGuide:
                startActivity(new Intent(mActivity, UseGuideActivity.class));
                break;
        }
    }
}
