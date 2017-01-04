package com.anhubo.anhubo.ui.activity.unitDetial;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.anhubo.anhubo.MyApp;
import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;

import java.net.URL;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by LUOLI on 2016/11/4.
 */
public class FeedbackSuccessActivity extends BaseActivity {
    @InjectView(R.id.wv_feedback)
    WebView wvFeedback;
    private String uid;
    private String newUrl;

    @Override
    protected void initConfig() {
        super.initConfig();
        uid = SpUtils.getStringParam(mActivity, Keys.UID);

    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_feedback_success;
    }

    @Override
    protected void initViews() {
        setTopBarDesc("反馈成功");
        topPb.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initEvents() {
        super.initEvents();
        int userAddScore = FeedbackActivity.userAddScore;
        String url = Urls.Url_FeedBackSuccess;
        newUrl = url+"?uid="+userAddScore+"&score="+userAddScore;
        WebSettings settings = wvFeedback.getSettings();
        settings.setJavaScriptEnabled(true);
        wvFeedback.setWebChromeClient(new WebChromeViewClient());
        wvFeedback.setWebViewClient(new WebViewClient() {
            //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                topPb.setVisibility(View.VISIBLE);
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        // 加载界面
        wvFeedback.loadUrl(newUrl);
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
        if (wvFeedback.canGoBack()){
            if(wvFeedback.getUrl().equals(newUrl)){
                ToastUtils.showToast(mActivity,"点击了");
                sendBroadcast(new Intent(FeedbackActivity.FEEDBACK_FINISH));
                super.onBackPressed();
            }else{
                wvFeedback.goBack();
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

        switch (v.getId()){
            case R.id.ivTopBarLeft:
                if (wvFeedback.canGoBack()){
                    if(wvFeedback.getUrl().equals(newUrl)){

                        ToastUtils.showToast(mActivity,"点击了");
                        sendBroadcast(new Intent(FeedbackActivity.FEEDBACK_FINISH));
                        super.onBackPressed();
                    }else{
                        wvFeedback.goBack();
                    }
                }else{
                    super.onBackPressed();
                }
                break;
        }
    }


    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }
}
