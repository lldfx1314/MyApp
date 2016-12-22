package com.anhubo.anhubo.ui.activity.unitDetial;

import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;

import butterknife.InjectView;

/**
 * Created by Administrator on 2016/9/30.
 */
public class Unit2Study extends BaseActivity {
    @InjectView(R.id.wv_study)
    WebView wvStudy;
    private String url;

    @Override
    protected void initConfig() {
        url = Urls.Url_UnitStudy;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_unitstudy;
    }

    @Override
    protected void initViews() {
        // 设置状态栏显示的提示内容
        setTopBarDesc("学习");
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initEvents() {
        // 获取uid拼接参数
        String uid = SpUtils.getStringParam(mActivity, Keys.UID);
        //System.out.println("uid是==========" + uid);
        String newUrl = url + "?uid=" + uid;

        WebSettings settings = wvStudy.getSettings();
        settings.setJavaScriptEnabled(true);
        wvStudy.setWebViewClient(new WebViewClient() {
            //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                // 当页面加载完成后调用,在此隐藏进度条
                progressBar.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }

        });

        wvStudy.addJavascriptInterface(new MyJavaScriptInterface(), "ADAPP");
        // 加载界面
        wvStudy.loadUrl(newUrl);
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }


    class MyJavaScriptInterface {
        public MyJavaScriptInterface() {
        }

        @JavascriptInterface
        public void alertAPP() {
            ToastUtils.showToast(mActivity, "请选择答案");
        }
    }

    @Override
    protected void onLoadDatas() {

    }

    @Override
    public void onClick(View v) {

    }

}
