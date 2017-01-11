package com.anhubo.anhubo.ui.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.GuidePagerAdapter;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.ui.activity.Login_Register.Login_Message;
import com.anhubo.anhubo.ui.fragment.GuideFragmentA;
import com.anhubo.anhubo.ui.fragment.GuideFragmentB;
import com.anhubo.anhubo.ui.fragment.GuideFragmentC;
import com.anhubo.anhubo.utils.DisplayUtil;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.Utils;

import java.util.ArrayList;

import butterknife.InjectView;

/**
 * Created by LUOLI on 2016/12/5.
 */
public class GuideActivity extends BaseActivity {
    @InjectView(R.id.vp_splash)
    ViewPager vpSplash;
    @InjectView(R.id.btn_guide_done)
    ImageView btnGuideDone;
    @InjectView(R.id.rl_contain_dots)
    RelativeLayout rlContainDots;
    @InjectView(R.id.ll_guide_point)
    LinearLayout llGuidePoint;
    @InjectView(R.id.iv_guide_point_red)
    ImageView ivGuidePointRed;

    private GuidePagerAdapter guidePagerAdapter;
    private ArrayList<Fragment> fragments;
    private String uid;
    private String bulidingid;
    private String businessid;
    private String versionName;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();

    }

    @Override
    protected void initViews() {
        //初始化界面，加载fragment
        setData();
    }

    private void setData() {

        fragments = new ArrayList<Fragment>();
        GuideFragmentA fragment1 = new GuideFragmentA();
        GuideFragmentB fragment2 = new GuideFragmentB();
        GuideFragmentC fragment3 = new GuideFragmentC();
        fragments.add(fragment1);
        fragments.add(fragment2);
        fragments.add(fragment3);
        guidePagerAdapter = new GuidePagerAdapter(getSupportFragmentManager(), fragments);
        vpSplash.setAdapter(guidePagerAdapter);
        vpSplash.setCurrentItem(0);
        //监听ViewPager的滑动事件
        vpSplash.setOnPageChangeListener(onPageChangeListener);

    }

    @Override
    protected void onLoadDatas() {

        btnGuideDone.setOnClickListener(this);

    }

    @Override
    protected void initEvents() {
        super.initEvents();
        uid = SpUtils.getStringParam(mActivity, Keys.UID);
//        bulidingid = SpUtils.getStringParam(mActivity, Keys.BULIDINGID);
        businessid = SpUtils.getStringParam(mActivity, Keys.BUSINESSID);
        String[] split = Utils.getAppInfo(mActivity).split("#");
        versionName = split[1];


        for (int i = 0; i < fragments.size(); i++) {
            //initGuideImage(i); //初始化引导图
            initDots(i);//初始化静态点
        }
    }

    /**
     * 初始化底部小圆点
     */
    private void initDots(int i) {

        ImageView imageDot = new ImageView(this);
        imageDot.setBackgroundResource(R.drawable.dot_white_shape);
        int dotsMargin = DisplayUtil.dp2px(mActivity, 10);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dotsMargin,dotsMargin);
        imageDot.setLayoutParams(params);
        if (i != 0) {
            params.leftMargin = dotsMargin;
        }
        llGuidePoint.addView(imageDot);
    }


    @Override
    public void onClick(View v) {
        //SpUtils.putParam(this, Keys.GUIDE_DONE, true);//保存是否完成引导页
        SpUtils.putParam(mActivity, Keys.VERSIONNAME,versionName);
        //System.out.println("versionName+111+"+versionName);
        if(!TextUtils.isEmpty(uid)) {
            enterHome(uid);

//            if(!TextUtils.isEmpty(bulidingid)||!TextUtils.isEmpty(businessid)) {
//                //跳转到主页面
//                enterHome();
//            }else{
//                // 跳到注册第二个界面
//                enterRegister2();
//            }

        }else{
            // 无uid，跳到登录界面
            enterLogin();

        }
    }

    /**
     * ViewPager滑动的监听
     */
    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //Log.d("onPageScrolled", "onPageScrolled() called with: " + "position = [" + position + "], positionOffset = [" + positionOffset + "], positionOffsetPixels = [" + positionOffsetPixels + "]");
            // 红点滚动 红点移动的距离
            int redMove = (int) ((position + positionOffset) * DisplayUtil.dp2px(mActivity,20));
            // 获取控件的宽和高的属性
            android.widget.RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) ivGuidePointRed
                    .getLayoutParams();

            params.leftMargin = redMove;// 把移动的距离设置为红点距离左边的距离
            ivGuidePointRed.setLayoutParams(params);
        }

        @Override
        public void onPageSelected(int position) {
            //是否显示开始体验按钮
            if (position == fragments.size() - 1) {
                btnGuideDone.setVisibility(View.VISIBLE);
            } else {
                btnGuideDone.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
//    private void enterRegister2() {
//        Intent intent = new Intent(mActivity, RegisterActivity2.class);
//        intent.putExtra(Keys.UID, uid);
//        startActivity(intent);
//    }

    private void enterLogin() {
        startActivity(new Intent(mActivity, Login_Message.class));
        finish();
    }

    private void enterHome(String uid) {
        Intent intent = new Intent(mActivity, HomeActivity.class);
        //System.out.println("要传递的uid+++===+++" + uid);
        intent.putExtra(Keys.UID, String.valueOf(uid));
        startActivity(intent);
        finish();
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }
}
