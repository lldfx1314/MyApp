package com.anhubo.anhubo.ui.activity.unitDetial;

<<<<<<< HEAD
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
=======
import android.os.Bundle;
import android.view.View;
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

<<<<<<< HEAD
import com.anhubo.anhubo.MyApp;
=======
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;
<<<<<<< HEAD
import com.anhubo.anhubo.utils.ToastUtils;
=======
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce

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
<<<<<<< HEAD
    private String newUrl;
=======
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce

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
<<<<<<< HEAD
        topPb.setVisibility(View.VISIBLE);
        iv_basepager_left.setOnClickListener(this);
=======
        progressBar.setVisibility(View.VISIBLE);
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
    }

    @Override
    protected void initEvents() {
        super.initEvents();
        int userAddScore = FeedbackActivity.userAddScore;
        String url = Urls.Url_FeedBackSuccess;
<<<<<<< HEAD
        newUrl = url+"?uid="+userAddScore+"&score="+userAddScore;
        WebSettings settings = wvFeedback.getSettings();
        settings.setJavaScriptEnabled(true);
        wvFeedback.setWebChromeClient(new WebChromeViewClient());
        wvFeedback.setWebViewClient(new WebViewClient() {
            //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                topPb.setVisibility(View.VISIBLE);
=======
        String newUrl = url+"?uid="+userAddScore+"&score="+userAddScore;
        WebSettings settings = wvFeedback.getSettings();
        settings.setJavaScriptEnabled(true);
        wvFeedback.setWebViewClient(new WebViewClient() {
            //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                progressBar.setVisibility(View.VISIBLE);
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
<<<<<<< HEAD
=======
                // 当页面加载完成后调用,在此隐藏进度条
                progressBar.setVisibility(View.GONE);
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
                super.onPageFinished(view, url);
            }
        });
        // 加载界面
        wvFeedback.loadUrl(newUrl);
    }

<<<<<<< HEAD
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
        sendBroadcast(new Intent(FeedbackActivity.FEEDBACK_FINISH));
        finish();
    }


=======
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
    @Override
    protected void onLoadDatas() {

    }

    @Override
    public void onClick(View v) {

<<<<<<< HEAD
        switch (v.getId()){
            case R.id.ivTopBarLeft:
                sendBroadcast(new Intent(FeedbackActivity.FEEDBACK_FINISH));
                finish();
                break;
        }
=======
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
    }


    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }
}
