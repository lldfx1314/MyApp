package com.anhubo.anhubo.ui.activity.MyDetial;

import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;

import butterknife.InjectView;

/**
 * Created by LUOLI on 2016/10/27.
 */
public class InvateActivity extends BaseActivity {
    @InjectView(R.id.wv_invate)
    WebView wvInvate;
    private String url;
    private String newUrl;

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
        newUrl = url + "?uid=" + uid;

        WebSettings settings = wvInvate.getSettings();
        settings.setJavaScriptEnabled(true);

        wvInvate.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                view.loadUrl(url);
                return true;
            }

        });
        wvInvate.addJavascriptInterface(new MyJavaScriptInterface(), "ADAPP");
        wvInvate.loadUrl(newUrl);
    }

    class MyJavaScriptInterface {
        public MyJavaScriptInterface() {
        }
        /**粘贴邀请码*/
        @JavascriptInterface
        public void clickqrcode(String qrcode)  {
            ToastUtils.showToast(mActivity, "邀请码");
        }
        /**分享微信*/
        public void weixinshare() {
            ToastUtils.showToast(mActivity, "分享微信");
        }
        /**分享朋友圈*/
        public void weixinfriendsshare() {
            ToastUtils.showToast(mActivity, "分享朋友圈");
        }
    }

    @Override
    protected void onLoadDatas() {

    }

    @Override
    public void onClick(View v) {

    }
}
