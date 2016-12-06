package com.anhubo.anhubo.ui.activity.DiscoveryDetial;

import android.view.View;
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
 * Created by LUOLI on 2016/11/17.
 */
public class NoticeActivity extends BaseActivity {
    @InjectView(R.id.wv_notice)
    WebView wvNotice;
    private String url;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_notice;
    }

    @Override
    protected void initViews() {
        // 设置状态栏显示的提示内容
        setTopBarDesc("公告");
        progressBar.setVisibility(View.VISIBLE);

    }

    @Override
    protected void initEvents() {
        super.initEvents();
        url = Urls.Url_FindNotice;
        // 获取uid拼接参数
        String uid = SpUtils.getStringParam(mActivity, Keys.UID);
        //System.out.println("uid是==========" + uid);
        String newUrl = url + "?uid=" + uid;
        WebSettings settings = wvNotice.getSettings();
        settings.setJavaScriptEnabled(true);
        wvNotice.setWebViewClient(new WebViewClient() {
            //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

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

        // 加载界面
        wvNotice.loadUrl(newUrl);
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
