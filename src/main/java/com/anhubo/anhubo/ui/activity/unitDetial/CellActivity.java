package com.anhubo.anhubo.ui.activity.unitDetial;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.view.ShowBottonDialog;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import butterknife.InjectView;

/**
 * Created by LUOLI on 2017/1/3.
 */
public class CellActivity extends BaseActivity {
    private static final int SHARE_FAIL = 1;
    private static final int SHARE_CANCLE = 2;
    @InjectView(R.id.cell_webView)
    WebView webView;
    private String url;
    private String uid;
    private String planId;
    private String unitId;
    private Dialog dialog;
    private Button btnWeixin;
    private Button btnweixinCircle;
    private String shareString;

    @Override
    protected void initConfig() {
        uid = SpUtils.getStringParam(mActivity, Keys.UID);
        Intent intent = getIntent();
        planId = intent.getStringExtra(Keys.PLANID);
        unitId = intent.getStringExtra(Keys.UNITID);
        if (TextUtils.isEmpty(unitId)) {
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
        iv_basepager_left.setOnClickListener(this);
        // 获取uid拼接参数
        String uid = SpUtils.getStringParam(mActivity, Keys.UID);
        //System.out.println("uid是==========" + uid);
        String newUrl = url + "?uid=" + uid + "&unit_id=" + unitId + "&plan_id=" + planId;

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
            if (newProgress == 100) {
                topPb.setVisibility(View.GONE);
            }
            super.onProgressChanged(view, newProgress);
        }

    }

    class MyJavaScriptInterface {
        public MyJavaScriptInterface() {

        }

        @JavascriptInterface
        public void shareAPP(String string) {
            shareString = string;
            showDialog();
        }
    }

    /**
     * 弹出对话框
     */
    private void showDialog() {
        // 创建一个对象
        View view = View.inflate(mActivity, R.layout.dialog_share, null);
        View btnCancel = view.findViewById(R.id.btn_share_Dialog_cancel);//取消按钮
        //显示对话框
        ShowBottonDialog showBottonDialog = new ShowBottonDialog(mActivity, view, btnCancel);
        dialog = showBottonDialog.show();
        //拍照按钮
        btnWeixin = (Button) view.findViewById(R.id.btn_weixin);
        //相册获取
        btnweixinCircle = (Button) view.findViewById(R.id.btn_weixin_circle);
        // 设置监听
        btnWeixin.setOnClickListener(this);
        btnweixinCircle.setOnClickListener(this);
    }

    @Override
    protected void onLoadDatas() {

    }

    @Override
    public void onClick(View v) {
        final String newUrl = Urls.Url_Cell_WeiXin + "?plan_id=" + planId;
        final String content = TextUtils.isEmpty(shareString) ? Urls.Url_Cell_WeiXin : shareString;
        final String context = "加入互助计划,时刻享用安全保障,前行路上更安心";
        switch (v.getId()) {
            case R.id.ivTopBarLeft:
                sendBroadcast(new Intent(CellListActivity.CELLLIST_FINISH));
                finish();
                break;
            case R.id.btn_weixin:
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ShareAction shareAction = new ShareAction(mActivity);
                        shareAction
                                .setPlatform(SHARE_MEDIA.WEIXIN)
                                .withText(context)
                                .withTitle(content)
                                .withTargetUrl(newUrl)
                                .setCallback(umShareListener)
                                .share();
                    }
                });

                break;
            case R.id.btn_weixin_circle:
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ShareAction shareweinCircle = new ShareAction(mActivity);

                        shareweinCircle
                                .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                                .withText(context)
                                .withTitle(content)
                                .withTargetUrl(newUrl)
                                .setCallback(umShareListener)
                                .share();
                    }
                });

                break;
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHARE_FAIL:
                    ToastUtils.showToast(mActivity, "分享失败");
                    break;
                case SHARE_CANCLE:
                    ToastUtils.showToast(mActivity, "分享取消");
                    break;
            }
        }
    };
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            //System.out.println(platform + "分享成功");

        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            handler.sendEmptyMessage(SHARE_FAIL);

        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            handler.sendEmptyMessage(SHARE_CANCLE);

        }
    };

    @Override
    public void onBackPressed() {
        sendBroadcast(new Intent(CellListActivity.CELLLIST_FINISH));
        finish();
    }



    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }
}
