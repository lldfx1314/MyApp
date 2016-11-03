package com.anhubo.anhubo.ui.activity.buildDetial;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.ui.activity.Login_Register.AnhubaoDeal;
import com.anhubo.anhubo.utils.DisplayUtil;
import com.anhubo.anhubo.utils.Keys;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by LUOLI on 2016/10/26.
 */
public class Build_CltMsgActivity extends BaseActivity {
    @InjectView(R.id.tv_build_msg)
    TextView tvBuildMsg;

    @Override
    protected int getContentViewId() {
        return R.layout.act_build_cltmsg;
    }

    @Override
    protected void initViews() {
        setTopBarDesc("完善基础信息");
    }

    @Override
    protected void initEvents() {
        super.initEvents();
        // 利用富文本是其中的网址变色
        String msg = "完善建筑信息，请到www.anhubo.com，登录\r\n个人中心进行完善";
        SpannableString ss = new SpannableString(msg);
        String url = Urls.Url_Deal;
        MyURLSpan myURLSpan = new MyURLSpan(url);
        ss.setSpan(myURLSpan, 9, 23, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE);
        tvBuildMsg.setText(ss);

        //tvBuildMsg.setMovementMethod(LinkMovementMethod.getInstance());//设置可以点击超链接
    }

    class MyURLSpan extends URLSpan {

        public MyURLSpan(String url) {
            super(url);
        }

        @Override
        public void onClick(View widget) {

        }

        @Override
        public void updateDrawState(TextPaint ds) {
            //super.updateDrawState(ds);
            ds.setColor(Color.parseColor("#3178C8"));//设置文字的颜色
            ds.setUnderlineText(false);//设置是否显示下划线
            ds.setTextSize(DisplayUtil.sp2px(mActivity,20));
        }
    }

    @Override
    protected void onLoadDatas() {

    }

    @Override
    public void onClick(View v) {

    }

}
