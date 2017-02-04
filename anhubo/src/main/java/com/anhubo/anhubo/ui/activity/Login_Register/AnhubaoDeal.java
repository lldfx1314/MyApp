package com.anhubo.anhubo.ui.activity.Login_Register;

import android.view.View;
import android.webkit.WebChromeClient;
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
        topPb.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initEvents() {
        url = getIntent().getStringExtra(Keys.ANHUBAODEAL);
        webView.loadUrl(url);
        webView.setWebChromeClient(new WebChromeViewClient());
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
    }

    private class WebChromeViewClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            topPb.setProgress(newProgress);
            if(newProgress==100){
                topPb.setVisibility(View.GONE);
            }
            super.onProgressChanged(view, newProgress);
        }

    }

    @Override
    protected void onLoadDatas() {


    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }
}
