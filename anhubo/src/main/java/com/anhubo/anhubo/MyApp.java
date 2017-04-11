package com.anhubo.anhubo;

import android.content.Context;
import android.os.Handler;

import com.squareup.okhttp.OkHttpClient;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;

import java.util.concurrent.TimeUnit;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


/**
 * Created by Administrator on 2016/9/18.
 */
public class MyApp extends LitePalApplication{
    public static MyApp mInstance;
    private static Context context;
    private static Handler handler;
    /**微信appKey*/
    {
        PlatformConfig.setWeixin("wx368b75520f5563b1", "ebf8da8d9862308a026173fa8cf4313a");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        // 异常处理，不需要处理时注释掉这两句即可！
        CrashHandler crashHandler = CrashHandler.getInstance();
        // 注册crashHandler
        //crashHandler.init(getApplicationContext());

        // 友盟
        UMShareAPI.get(this);

//        禁止默认的页面统计方式，这样将不会再自动统计Activity
//        MobclickAgent.openActivityDurationTrack(false);

        mInstance=MyApp.this;
        context = this;
        handler = new Handler();
        // OkHttp
//        OkHttpUtils.getInstance().debug("OkHttpUtils").setConnectTimeout(60000, TimeUnit.MILLISECONDS);
//        //使用https，但是默认信任全部证书
//        OkHttpUtils.getInstance().setCertificates();
        OkHttpClient client =
                OkHttpUtils.getInstance().getOkHttpClient();
        client.setConnectTimeout(60000,TimeUnit.MILLISECONDS);
        // 替换字体
//        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/xxx.ttf").setFontAttrId(R.attr.fontPath).build());
    }


    public static MyApp get(){
        return mInstance;
    }


    /** 获取Application类型的上下文 */
    public static Context getContext() {
        return context;
    }

    /** 获取一个可以运行到UI线程的Handler */
    public static Handler getHandler() {
        return handler;
    }


}
