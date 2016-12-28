package com.anhubo.anhubo.ui.activity.unitDetial;

import android.content.Intent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.Keys;

import butterknife.InjectView;

/**
 * Created by LUOLI on 2016/12/16.
 */
public class HuBaoPlanActivity extends BaseActivity {
    @InjectView(R.id.wv_hubaoplan)
    WebView wvHubaoplan;
    private String newUrl;
    private String url;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_hubaoplan;
    }

    @Override
    protected void initViews() {
        // 设置状态栏显示的提示内容
        setTopBarDesc("计划详情");
        progressBar.setVisibility(View.VISIBLE);
        iv_basepager_left.setOnClickListener(this);
    }

    @Override
    protected void onLoadDatas() {
        Intent intent = getIntent();
        String planId = intent.getStringExtra(Keys.PLANID);
        String massId = intent.getStringExtra(Keys.MASSID);
        url = Urls.Url_HuBaoPlan;
        newUrl = url + "?mass_id=" + massId+"&plan_id="+planId;
        WebSettings settings = wvHubaoplan.getSettings();
        settings.setJavaScriptEnabled(true);

        wvHubaoplan.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                progressBar.setVisibility(View.VISIBLE);
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
        wvHubaoplan.loadUrl(newUrl);
    }

    @Override
    public void onBackPressed() {
        if (wvHubaoplan.canGoBack()){
            if(wvHubaoplan.getUrl().equals(newUrl)){
                super.onBackPressed();
            }else{
                wvHubaoplan.goBack();
            }
        }else{
            super.onBackPressed();
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivTopBarLeft:
                if (wvHubaoplan.canGoBack()){
                    if(wvHubaoplan.getUrl().equals(newUrl)){
                        super.onBackPressed();
                    }else{
                        wvHubaoplan.goBack();
                    }
                }else{
                    finish();
                }
                break;
        }
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }
}
