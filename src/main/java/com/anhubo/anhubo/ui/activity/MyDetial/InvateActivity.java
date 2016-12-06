package com.anhubo.anhubo.ui.activity.MyDetial;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.MyshareBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.google.gson.Gson;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.InjectView;
import okhttp3.Call;

/**
 * Created by LUOLI on 2016/10/27.
 */
public class InvateActivity extends BaseActivity {
    @InjectView(R.id.wv_invate)
    WebView wvInvate;

    private String url;
    private String newUrl_invate;
    private String uid;
    private String url_weixin;
    private String content;
    private String title;
    private String imgUrl;

    @Override
    protected void initConfig() {
        super.initConfig();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_invate;
    }

    @Override
    protected void initViews() {
        // 设置状态栏显示的提示内容
        setTopBarDesc("邀请");
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initEvents() {
        super.initEvents();

        uid = SpUtils.getStringParam(mActivity, Keys.UID);
        url = Urls.Url_MyInvare;
        newUrl_invate = url + "?uid=" + uid;

        url_weixin = Urls.Url_MyInvare_WeiXin;

        WebSettings settings = wvInvate.getSettings();
        settings.setJavaScriptEnabled(true);

        wvInvate.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
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
        wvInvate.addJavascriptInterface(new MyJavaScriptInterface(), "ADAPP");
        wvInvate.loadUrl(newUrl_invate);
    }

    Handler handler = new Handler();

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }


    class MyJavaScriptInterface {


        public MyJavaScriptInterface() {
        }

        /**
         * 粘贴邀请码
         */
        @JavascriptInterface
        public void clickqrcode(String qrcode) {

            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            // 将内容set到剪贴板
            clipboardManager.setPrimaryClip(ClipData.newPlainText(null, qrcode));
            ToastUtils.showToast(mActivity, "复制成功");
            if (clipboardManager.hasPrimaryClip()) {
                // 获取内容
                CharSequence text = clipboardManager.getPrimaryClip().getItemAt(0).getText();
                //System.out.println("+++---==="+text);
            }
        }

        /**
         * 分享朋友圈
         */
        @JavascriptInterface
        public void weixinfriendsshare() {
            handler.post(new Runnable() {
                @Override
                public void run() {

                    String url = Urls.Url_MyShare;

                    OkHttpUtils.post()//
                            .url(url)//
                            .build()//
                            .execute(new MyStringCallback1());
                }
            });

        }

        /**
         * 分享微信
         */
        @JavascriptInterface
        public void weixinshare() {


            String url = Urls.Url_MyShare;

            OkHttpUtils.post()//
                    .url(url)//
                    .build()//
                    .execute(new MyStringCallback());
        }

    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            //System.out.println(platform + "分享成功");

        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            ToastUtils.showToast(mActivity, "分享失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            ToastUtils.showToast(mActivity, "分享取消");

        }
    };

    /**
     * 微信朋友圈网络请求
     */
    class MyStringCallback1 extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {

            System.out.println("InvateActivity界面获取信息失败");
        }

        @Override
        public void onResponse(String response) {
            //System.out.println("11111朋友圈111111" + response);
            MyshareBean bean = new Gson().fromJson(response, MyshareBean.class);

            if (bean != null) {
                int code = bean.code;
                if (code == 0) {
                    content = bean.data.content;
                    title = bean.data.title;
                    imgUrl = bean.data.img_url;
                    /**注意：注意：注意：content一定要有值，没值的话调不动分享界面*/
                    final String content1 = TextUtils.isEmpty(content) ? url_weixin : content;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            String newUrl = url_weixin + "?type=" + 0 + "&uid=" + uid;
                            ShareAction shareAction = new ShareAction(mActivity);
                            //System.out.println("11111朋友圈111111");

                            shareAction
                                    .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                                    .withText(content1)
                                    .withTitle(title)
                                    .withTargetUrl(newUrl)
                                    .setCallback(umShareListener)
                                    .share();
                        }
                    });
                }
            }
        }
    }


    /**
     * 微信网络请求
     */
    class MyStringCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {

            System.out.println("InvateActivity界面获取信息失败");
        }

        @Override
        public void onResponse(String response) {
            //System.out.println("11111微信111111" + response);
            MyshareBean bean = new Gson().fromJson(response, MyshareBean.class);
            if (bean != null) {
                int code = bean.code;
                if (code == 0) {
                    content = bean.data.content;
                    title = bean.data.title;
                    imgUrl = bean.data.img_url;
                    /**注意：注意：注意：content一定要有值，没值的话调不动分享界面*/
                    final String content1 = TextUtils.isEmpty(content) ? url_weixin : content;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String newUrl = url_weixin + "?type=" + 1 + "&uid=" + uid;
                            //System.out.println("11111微信111111");
                            ShareAction shareAction = new ShareAction(mActivity);
                            shareAction
                                    .setPlatform(SHARE_MEDIA.WEIXIN)
                                    .withText(content1)
                                    .withTitle(title)
                                    .withTargetUrl(newUrl)
                                    .setCallback(umShareListener)
                                    .share();

                        }
                    });
                }
            }
        }
    }


    @Override
    protected void onLoadDatas() {

    }

    @Override
    public void onClick(View v) {

    }
}
