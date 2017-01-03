package com.anhubo.anhubo.ui.activity.unitDetial;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by LUOLI on 2017/1/3.
 */
public class CellActivity extends BaseActivity {
    @InjectView(R.id.cell_webView)
    WebView webView;
    private String url;
    private String uid;
    private String planId;
    private String unitId;

    @Override
    protected void initConfig() {
        uid = SpUtils.getStringParam(mActivity, Keys.UID);
        Intent intent = getIntent();
        planId = intent.getStringExtra(Keys.PLANID);
        unitId = intent.getStringExtra(Keys.UNITID);
        if(TextUtils.isEmpty(unitId)){
            unitId = "";
        }

        url = Urls.Url_Unit_Cell;
    }
    @Override
    protected int getContentViewId() {
        return R.layout.activity_cell;
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        setTopBarDesc("加入单元");
        topPb.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initViews() {
// 获取uid拼接参数
        String uid = SpUtils.getStringParam(mActivity, Keys.UID);
        //System.out.println("uid是==========" + uid);
        String newUrl = url + "?uid=" + uid+"&unit_id="+unitId+"&plan_id="+planId;

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeViewClient());
        webView.setWebViewClient(new WebViewClient() {
            //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

        });

        webView.addJavascriptInterface(new MyJavaScriptInterface(), "ADAPP");
        // 加载界面
        webView.loadUrl(newUrl);
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
    class MyJavaScriptInterface {
        public MyJavaScriptInterface() {
        }

        @JavascriptInterface
        public void alertAPP() {
            //ToastUtils.showToast(mActivity, "请选择答案");
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
