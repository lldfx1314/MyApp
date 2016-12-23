package com.anhubo.anhubo.ui.activity.MyDetial;

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;

import butterknife.InjectView;

/**
 * Created by LUOLI on 2016/12/13.
 */
public class OrderManagerActivity extends BaseActivity {
    @InjectView(R.id.wv_order)
    WebView wvOrder;
    private String uid;
    private String url;
    private String newUrl;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_ordermanager;
    }

    @Override
    protected void initViews() {
        // 设置状态栏显示的提示内容
        setTopBarDesc("订单管理");
        topPb.setVisibility(View.VISIBLE);
        iv_basepager_left.setOnClickListener(this);
    }

    @Override
    protected void onLoadDatas() {
        uid = SpUtils.getStringParam(mActivity, Keys.UID);
        url = Urls.Url_MyOrderManager;
        newUrl = url + "?uid=" + uid;
        WebSettings settings = wvOrder.getSettings();
        settings.setJavaScriptEnabled(true);
        wvOrder.setWebChromeClient(new WebChromeViewClient());
        wvOrder.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                topPb.setVisibility(View.VISIBLE);
                view.loadUrl(url);
                return true;
            }

        });
        wvOrder.loadUrl(newUrl);
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
    public void onBackPressed() {
        if (wvOrder.canGoBack()){
            if(wvOrder.getUrl().equals(newUrl)){
                super.onBackPressed();
            }else{
                wvOrder.goBack();
            }
        }else{
            super.onBackPressed();
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivTopBarLeft:
                if (wvOrder.canGoBack()){
                    if(wvOrder.getUrl().equals(newUrl)){
                        super.onBackPressed();
                    }else{
                        wvOrder.goBack();
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
