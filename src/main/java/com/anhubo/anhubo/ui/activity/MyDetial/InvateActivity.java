package com.anhubo.anhubo.ui.activity.MyDetial;

import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;

import butterknife.InjectView;

/**
 * Created by LUOLI on 2016/10/27.
 */
public class InvateActivity extends BaseActivity {
    @InjectView(R.id.wv_invate)
    WebView wvInvate;
    private String url;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_invate;
    }

    @Override
    protected void initViews() {
// 设置状态栏显示的提示内容
        setTopBarDesc("邀请");
    }

    @Override
    protected void initEvents() {
        super.initEvents();
        String uid = SpUtils.getStringParam(mActivity, Keys.UID);
        url = Urls.Url_MyInvare;
        url = url+"?uid="+uid;
        wvInvate.loadUrl(url);

        wvInvate.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                view.loadUrl(url);
                return true;
            }

        });
    }

    @Override
    protected void onLoadDatas() {

    }

    @Override
    public void onClick(View v) {

    }
}
