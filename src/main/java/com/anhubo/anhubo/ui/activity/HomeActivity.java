package com.anhubo.anhubo.ui.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.HomeAdapter;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.ui.impl.BuildFragment;
import com.anhubo.anhubo.ui.impl.FindFragment;
import com.anhubo.anhubo.ui.impl.MyFragment;
import com.anhubo.anhubo.ui.impl.UnitFragment;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.view.NoScrollViewPager;

import java.util.ArrayList;

import butterknife.InjectView;

/**
 * 这是首页的主界面
 */

public class HomeActivity extends BaseActivity {

    @InjectView(R.id.viewpager)
    NoScrollViewPager viewpager;

    @InjectView(R.id.rg_home_bottom)
    RadioGroup rgHomeBottom;
    private ArrayList<Fragment> list;
    private long exitTime = 0;

    @Override
    protected void initConfig() {
        super.initConfig();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initEvents() {

        // 初始化集合
        list = new ArrayList();
        list.add(new UnitFragment());
        list.add(new BuildFragment());
        list.add(new FindFragment());
        list.add(new MyFragment());
        // 设置适配器
        viewpager.setAdapter(new HomeAdapter(getSupportFragmentManager(),list));
        // 关联底部RadioButton的五个button和ViewPager的关联
        rgHomeBottom.setOnCheckedChangeListener(new onCheckedChangeListener());
        //默认选中首页
        rgHomeBottom.check(R.id.rb_bottom_unit);//参数是默认的ID
        viewpager.setOffscreenPageLimit(0);
        // 监听viewpager的滑动
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onLoadDatas() {

    }

    @Override
    public void onClick(View v) {

    }

    class onCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb_bottom_unit:
                    viewpager.setCurrentItem(0, false);//false表示点击时没有滑动效果
                    break;
                case R.id.rb_bottom_build:
                    viewpager.setCurrentItem(1, false);
                    break;
                case R.id.rb_bottom_find:
                    viewpager.setCurrentItem(2, false);
                    break;
                case R.id.rb_bottom_my:
                    viewpager.setCurrentItem(3, false);
                    break;

            }
        }
    }


    /**设置再点击一次退出应用*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {

        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtils.showLongToast(mActivity,"再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }
}
