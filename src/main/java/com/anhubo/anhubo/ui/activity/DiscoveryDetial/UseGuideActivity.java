package com.anhubo.anhubo.ui.activity.DiscoveryDetial;

import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.protocol.Urls;

import butterknife.InjectView;

/**
 * Created by LUOLI on 2016/12/1.
 */
public class UseGuideActivity extends BaseActivity {

    @InjectView(R.id.wv_useGuide)
    WebView wvUseGuide;
    private String urlUseGuide;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_use_guide;
    }

    @Override
    protected void initViews() {
        // 设置状态栏显示的提示内容
        setTopBarDesc("使用指导");
        progressBar.setVisibility(View.VISIBLE);
    }


    @Override
    protected void initEvents() {
        super.initEvents();
        urlUseGuide = Urls.Url_UseGuide;

        WebSettings settings = wvUseGuide.getSettings();
        settings.setJavaScriptEnabled(true);

        wvUseGuide.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
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
        wvUseGuide.loadUrl(urlUseGuide);
    }

    @Override
    public void onBackPressed() {
        if (wvUseGuide.canGoBack()){
            if(wvUseGuide.getUrl().equals(urlUseGuide)){
                super.onBackPressed();
            }else{
                wvUseGuide.goBack();
            }
        }else{
            super.onBackPressed();
        }

    }

    @Override
    protected void onLoadDatas() {

    }

    @Override
    public void onClick(View v) {

    }

}
