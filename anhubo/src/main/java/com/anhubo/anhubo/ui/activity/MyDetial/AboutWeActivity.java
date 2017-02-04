package com.anhubo.anhubo.ui.activity.MyDetial;

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.protocol.Urls;

import butterknife.InjectView;

/**
 * Created by LUOLI on 2016/10/27.
 */
public class AboutWeActivity extends BaseActivity {
    @InjectView(R.id.wv_aboutwe)
    WebView wvAboutwe;
    private String url;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_we;
    }

    @Override
    protected void initViews() {
// 设置状态栏显示的提示内容
        setTopBarDesc("关于我们");
        topPb.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initEvents() {
        super.initEvents();
        url = Urls.Url_MyAboutWe;
        wvAboutwe.loadUrl(url);
        wvAboutwe.setWebChromeClient(new WebChromeViewClient());
        wvAboutwe.setWebViewClient(new WebViewClient() {
            //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                view.loadUrl(url);
                return true;
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
