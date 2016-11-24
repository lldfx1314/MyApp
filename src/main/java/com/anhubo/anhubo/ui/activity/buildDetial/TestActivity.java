package com.anhubo.anhubo.ui.activity.buildDetial;

import android.view.View;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.TestAdapter;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.TestItemBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.view.AlertDialog;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import okhttp3.Call;

/**
 * Created by LUOLI on 2016/11/24.
 */
public class TestActivity extends BaseActivity {

    @InjectView(R.id.rl_all_test)
    RelativeLayout rlAllTest;
    @InjectView(R.id.el_listview)
    ExpandableListView elListview;
    private String cardnumber;
    private String testId;
    private String requireTag;
    private ArrayList<String> listRequireTag;
    private ArrayList<String> listTestId;

    @Override
    protected void initConfig() {
        super.initConfig();
        cardnumber = getIntent().getStringExtra(Keys.CARDNUMBER);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_test;
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        setTopBarDesc("问题描述");
        tvTopBarRight.setVisibility(View.VISIBLE);
        setTopBarRight("提交");
    }

    @Override
    protected void initViews() {
        listRequireTag = new ArrayList<>();
        listTestId = new ArrayList<>();
        // 获取测试项
        getData();
    }

    private void getData() {
        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        params.put("device_id", cardnumber);
        String url = Urls.Url_Build_Test;

        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback());
    }

    @Override
    protected void initEvents() {
        super.initEvents();
        tvTopBarRight.setOnClickListener(this);

    }

    @Override
    protected void onLoadDatas() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvTopBarRight:
                ToastUtils.showToast(mActivity, "哈哈，就不提交");
                Map<String, String> params = new HashMap<>();
                params.put("device_id", cardnumber);
                params.put("test_id", "24");
                //String url = Urls.Url_Build_Test;
                String url = Urls.Url_Build_TestDetial;

                OkHttpUtils.post()//
                        .url(url)//
                        .params(params)//
                        .build()//
                        .execute(new MyStringCallback());
                break;
            case R.id.rl_all_test:

                break;
        }
    }


    /**
     * 获取测试项
     */
    class MyStringCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {

            System.out.println("TestActivity+++获取测试项===界面失败" + e.getMessage());

            progressBar.setVisibility(View.GONE);
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("网络有问题，请检查")
                    .setCancelable(false).show();
        }

        @Override
        public void onResponse(String response) {
            System.out.println("获取测试项+++===" + response);
            TestItemBean bean = new Gson().fromJson(response, TestItemBean.class);
            if (bean != null) {
                progressBar.setVisibility(View.GONE);
                int code = bean.code;
                String msg = bean.msg;
                List<TestItemBean.Data.Require> requires = bean.data.require;
                if(code == 0 && requires!=null){
                    if(!requires.isEmpty()){
                        for (int i = 0; i < requires.size(); i++) {
                            TestItemBean.Data.Require require = requires.get(i);
                            requireTag = require.require_tag;
                            testId = require.test_id;
                            listTestId.add(testId);
                            listRequireTag.add(requireTag);
                        }
                    }
                }
                // 如果requireTag不为空，显示ExpandableListView
                TestAdapter adapter = new TestAdapter(mActivity, listTestId, listRequireTag);
                elListview.setAdapter(adapter);

            }
        }
    }
}
