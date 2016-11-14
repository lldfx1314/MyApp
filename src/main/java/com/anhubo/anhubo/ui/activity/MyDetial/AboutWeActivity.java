package com.anhubo.anhubo.ui.activity.MyDetial;

import android.view.View;
import android.webkit.WebView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.protocol.Urls;

import butterknife.InjectView;

/**
 * Created by LUOLI on 2016/10/27.
 */
public class AboutWeActivity extends BaseActivity {
    @InjectView(R.id.wv_aboutwe)
    WebView wvAboutwe;
    private String url;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_we;
    }

    @Override
    protected void initViews() {
// 设置状态栏显示的提示内容
        setTopBarDesc("关于我们");
    }

    @Override
    protected void initEvents() {
        super.initEvents();
        url = Urls.Url_MyAboutWe;
        wvAboutwe.loadUrl(url);
    }

    @Override
    protected void onLoadDatas() {

    }

    @Override
    public void onClick(View v) {

    }

}
