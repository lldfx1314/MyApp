package com.anhubo.anhubo.ui.activity.unitDetial;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by LUOLI on 2016/11/4.
 */
public class FeedbackSuccessActivity extends BaseActivity {
    @InjectView(R.id.wv_feedback)
    WebView wvFeedback;
    private String uid;

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

    }

    @Override
    protected void initEvents() {
        super.initEvents();
        int userAddScore = FeedbackActivity.userAddScore;
        String url = "http://anhubo.com/s/html/feedBackOK.html";
        String newUrl = url+"?uid="+userAddScore+"&score="+userAddScore;
        WebSettings settings = wvFeedback.getSettings();
        settings.setJavaScriptEnabled(true);
        wvFeedback.setWebViewClient(new WebViewClient() {
            //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

        });
        // 加载界面
        wvFeedback.loadUrl(newUrl);
    }

    @Override
    protected void onLoadDatas() {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }
}
