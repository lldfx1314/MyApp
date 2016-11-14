package com.anhubo.anhubo.ui.activity.Login_Register;

import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.utils.Keys;

import butterknife.InjectView;

/**
 * Created by Administrator on 2016/9/30.
 */
public class AnhubaoDeal extends BaseActivity {
    @InjectView(R.id.webView)
    WebView webView;

    private String url;

    @Override
    protected void initConfig() {

    }

    @Override
    protected int getContentViewId() {
        return R.layout.anhubao_activity;
    }

    @Override
    protected void initViews() {
        // 设置状态栏显示的提示内容
        setTopBarDesc("安互保协议");

    }

    @Override
    protected void initEvents() {
        url = getIntent().getStringExtra(Keys.ANHUBAODEAL);
        webView.loadUrl(url);
       /* WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(true);*/
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                view.loadUrl(url);
                return true;
            }

        });
    }

    @Override
    protected void onLoadDatas() {



    }

    @Override
    public void onClick(View v) {

    }

}
