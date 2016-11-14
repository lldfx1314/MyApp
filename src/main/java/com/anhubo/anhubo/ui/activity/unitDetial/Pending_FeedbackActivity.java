package com.anhubo.anhubo.ui.activity.unitDetial;

import android.view.View;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.Pending_FeedbackBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.Keys;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

/**
 * Created by LUOLI on 2016/11/14.
 */
public class Pending_FeedbackActivity extends BaseActivity{

    private String isId;

    @Override
    protected void initConfig() {
        super.initConfig();
        isId = getIntent().getStringExtra(Keys.IsId);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_pending_feedback;
    }

    @Override
    protected void initViews() {
        setTopBarDesc("问题描述");
    }

    @Override
    protected void initEvents() {
        super.initEvents();
        // 这里是完成的点击事件
        String url = Urls.Url_Check_Pending_FeedBack;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("is_id",isId); //这是uid,登录后改成真正的用户

        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback());
    }

    class MyStringCallback extends StringCallback{
        @Override
        public void onError(Call call, Exception e) {

            System.out.println("Pending_FeedbackActivity+++===没拿到数据" + e.getMessage());
        }

        @Override
        public void onResponse(String response) {
            System.out.println("Pending_FeedbackActivity"+response);
            Pending_FeedbackBean bean = new Gson().fromJson(response, Pending_FeedbackBean.class);
            if(bean!=null){
                String msg = bean.msg;
                int code = bean.code;
                String isContent = bean.data.is_content;
                String isTime = bean.data.is_time;
                List<String> isPic = bean.data.is_pic;
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
