package com.anhubo.anhubo.ui.activity;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.GuidePagerAdapter;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.utils.DisplayUtil;
import com.anhubo.anhubo.view.RatioImageView;

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

    private ArrayList<ImageView> images;
    private ArrayList<LinearLayout> points;
    private GuidePagerAdapter guidePagerAdapter;

    //private static int[] imgs = new int[]{R.drawable.bg_yindao_1, R.drawable.icon_yindao_2, R.drawable.bg_yindao_3};

    @Override
    protected int getContentViewId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initViews() {
        points = new ArrayList<>();

    }

    @Override
    protected void onLoadDatas() {
        images = new ArrayList<>();

        guidePagerAdapter = new GuidePagerAdapter(images);
        vpSplash.setAdapter(guidePagerAdapter);
        //监听ViewPager的滑动事件
        vpSplash.setOnPageChangeListener(onPageChangeListener);
        btnGuideDone.setOnClickListener(this);
    }

    @Override
    protected void initEvents() {
        super.initEvents();
        for (int i = 0; i < imgs.length; i++) {
            initGuideImage(i); //初始化引导图
            initDots(i);//初始化静态点
        }
        guidePagerAdapter.notifyDataSetChanged();
    }

    /**
     * 初始化底部小圆点
     */
    private void initDots(int i) {
        ImageView imageDot = new ImageView(this);
        int dotsMargin = DisplayUtil.dp2px(mActivity,15);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DisplayUtil.dp2px(mActivity,25), DisplayUtil.dp2px(mActivity,5));
        imageDot.setLayoutParams(params);
        if (i != 0) {
            params.leftMargin = dotsMargin;
        }
        ll_guide_points.addView(imageDot);
    }

    /**
     * 初始化引导图
     */
    private void initGuideImage(int i) {
        RatioImageView imageView = new RatioImageView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(imgs[i]);
        images.add(imageView);
    }

    @Override
    public void onClick(View v) {

    }
    /**
     * ViewPager滑动的监听
     */
    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            Log.d("onPageScrolled", "onPageScrolled() called with: " + "position = [" + position + "], positionOffset = [" + positionOffset + "], positionOffsetPixels = [" + positionOffsetPixels + "]");
            int size = points.size();
            for (int i = 0; i < size; i++) {
                if (positionOffsetPixels != 0 && position == i) {
                    points.get(position).setVisibility(View.INVISIBLE);
                } else if (position == i) {
                    points.get(position).setVisibility(View.VISIBLE);
                }
                if (position == i) {
                    continue;
                }
                points.get(i).setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onPageSelected(int position) {
            //是否显示开始体验按钮
            if (position == imgs.length - 1) {
                btnGuideDone.setVisibility(View.VISIBLE);
                rlContainDots.setVisibility(View.INVISIBLE);
            } else {
                btnGuideDone.setVisibility(View.GONE);
                rlContainDots.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
