package com.anhubo.anhubo.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/9/18.
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener , View.OnSystemUiVisibilityChangeListener {

    protected Activity mActivity;
    protected static final String INTENT_FINISH = "intent_finish";
    protected ImageButton iv_basepager_left;
    protected ImageView ivTopBarleftUnitMenu;
    protected ImageView ivTopBarRightUnitMsg;
    protected ImageView ivTopBarleftBuildPen;
    protected RelativeLayout llTop;
    protected TextView tvToptitle;
    protected TextView tvTopBarRight;
    protected RelativeLayout progressBar;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        requestWindowFeature(Window.FEATURE_ACTION_MODE_OVERLAY);
        setStatusBarTransparent();
        super.onCreate(savedInstanceState);

        mActivity = this;
        //setBar();
        initConfig();
        if (getContentViewId() != 0) {
            setContentView(getContentViewId());
        }
    }
    /**设置浸入式状态栏*/
    private void setStatusBarTransparent(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            //托盘重叠显示在Activity上
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    |View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(uiOptions);
            decorView.setOnSystemUiVisibilityChangeListener(this);
            // 设置托盘透明
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            //Log.d("CP_Common","VERSION.SDK_INT =" + VERSION.SDK_INT);
        }else{
            //Log.d("CP_Common", "SDK 小于19不设置状态栏透明效果");
        }

    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.inject(mActivity);
        // 设置title上的返回键的点击事件
        initDefaultViews();
        // 加载标题栏的布局
        initTitleView();// 找到标题栏布局
        initTitleBar();//设置标题栏的具体事件
        initProgressBar();//初始化加载进度条
        initViews();
        initEvents();
        onLoadDatas();
        initFinishReceiver();
        //initDialog();

    }


    private void initProgressBar() {
        progressBar = (RelativeLayout) findViewById(R.id.rl_progress);

    }




    /**
     * 初始化结束的广播监听
     */
    private void initFinishReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(INTENT_FINISH);
        registerReceiver(finishReceiver, filter);
    }
    /**注册广播接收*/
    private BroadcastReceiver finishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //收到广播后finishing
            if (INTENT_FINISH.equals(intent.getAction())) {
                finish();
            }
        }
    };

    /**
     * 初始化，setContentView调用之前调用
     */
    protected void initConfig() {}

    /**
     * 获取布局id
     * @return
     */
    protected abstract int getContentViewId();

    private final void initDefaultViews() {
        setTopBarLeftView(R.drawable.left, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**加载Title布局*/
    private void initTitleView() {
        // 找到顶部控件
        iv_basepager_left = (ImageButton) findViewById(R.id.ivTopBarLeft);//左上角返回按钮
        ivTopBarleftUnitMenu = (ImageView) findViewById(R.id.ivTopBarleft_unit_menu);//左上角菜单按钮
        ivTopBarRightUnitMsg = (ImageView) findViewById(R.id.ivTopBarRight_unit_msg);//右上角信息按钮
        tvTopBarRight = (TextView) findViewById(R.id.tvTopBarRight);//右上角列表
        ivTopBarleftBuildPen = (ImageView) findViewById(R.id.ivTopBarleft_build_pen);//左上角铅笔按钮
        tvToptitle = (TextView) findViewById(R.id.tvAddress);//标题
        llTop = (RelativeLayout) findViewById(R.id.ll_Top); // 顶部标题栏

    }
    /**设置标题栏*/
    protected void initTitleBar() {}
    /**初始化布局*/
    protected abstract void initViews();
    /**事件处理*/
    protected void initEvents() {}
    /**网络请求的处理都在这儿*/
    protected abstract void onLoadDatas();
    /**
     * 设置顶部左侧按钮图片以及点击的监听事件
     * @param res
     */
    protected final void setTopBarLeftView(int res, View.OnClickListener listener) {
        ImageView ivTopBarLeft = (ImageView) findViewById(R.id.ivTopBarLeft);
        if (ivTopBarLeft!=null) {
            ivTopBarLeft.setImageResource(res);
            ivTopBarLeft.setOnClickListener(listener);
        }
    }

    /**
     *
     * @param str 设置顶部状态栏显示文字
     *
     */
    protected final void setTopBarDesc(String str) {

            tvToptitle.setTextColor(getResources().getColor(R.color.backgroud_white));
            tvToptitle.setText(str);

    }
    /**
     *
     * @param str 设置顶部状态栏右侧显示文字
     *
     */
    protected final void setTopBarRight(String str) {

        tvTopBarRight.setTextColor(getResources().getColor(R.color.backgroud_white));
        tvTopBarRight.setText(str);

    }


    /**
     * 跳转到指定的Activity里面
     */
    public void goToActivity(Class clas){
        Intent intent = new Intent(this,clas);

        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(finishReceiver);
    }

    private void setBar() {
        Window window = mActivity.getWindow();

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        boolean hideStatusBarBackground = false;
        if (hideStatusBarBackground) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(Color.TRANSPARENT);
            }
            //隐藏状态栏的阴影window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }

        ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mChildView, new OnApplyWindowInsetsListener() {
                @Override
                public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                    return insets;
                }
            });
            ViewCompat.setFitsSystemWindows(mChildView, false);
            ViewCompat.requestApplyInsets(mChildView);

        }
    }
}
