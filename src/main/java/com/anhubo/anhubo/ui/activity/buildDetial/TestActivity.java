package com.anhubo.anhubo.ui.activity.buildDetial;

import android.text.TextUtils;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.TestAdapter;
import com.anhubo.anhubo.adapter.TestDetialBean;
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
    private ArrayList<ArrayList<String>> listChild;
    private TestAdapter adapter;
    private HashMap<Integer, String> hmTestId;
    private int m = 0;
    private ArrayList<Integer> completeList;
    /*用来记录对应的position位是否被点击*/
    private Map<Integer, Boolean> map = new HashMap<>();
    /* 用来记录存在问题的选项，0表示没问题，1表示有问题*/
    private int isProblem = 0;

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
        hmTestId = new HashMap<>();

        listChild = new ArrayList<>();
        // 获取测试项
        getData();
        // 初始化completeList
        completeList = new ArrayList<>();
        // 创建一个数组，五个数全是0
        int[] arr = new int[]{0, 0, 0, 0, 0};
        for (int i = 0; i < arr.length; i++) {

            completeList.add(arr[i]);
        }
        // 为map集合设置默认的值，每个item默认没被点击过
        map.put(0, false);
        map.put(1, false);
        map.put(2, false);
        map.put(3, false);
        map.put(4, false);
    }


    @Override
    protected void initEvents() {
        super.initEvents();
        tvTopBarRight.setOnClickListener(this);


    }


    @Override
    protected void onLoadDatas() {
        setListener();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvTopBarRight:
                ToastUtils.showToast(mActivity, "哈哈，就不提交");
                break;
            case R.id.rl_all_test:

                break;
        }
    }

    /**
     * 点击事件
     */
    private void setListener() {
        /**组头的点击事件*/
        elListview.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            // 定义一个变量记录已经被点开的是哪一个组
            int selectposition = -1;

            @Override
            public boolean onGroupClick(final ExpandableListView parent, View v, final int groupPosition, long id) {


                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isOpen = false;
                        // 判断是否展开
                        if (parent.isGroupExpanded(groupPosition)) {
                            // 已经展开去关闭
                            isOpen = false;
                            parent.collapseGroup(groupPosition);
                        } else {
                            isOpen = true;
                            // 已经关闭去 展开
                            parent.expandGroup(groupPosition);
                        }
                        /**获取测试项详情*/
                        if (isOpen) {
                            getDetialData(groupPosition);

                        }
                    }
                });

                return false;
            }
        });
        /**孩子的点击事件*/
        elListview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, final int childPosition, long id) {
                final ImageView ivChild = (ImageView) v.findViewById(R.id.iv_child);
                // 获取当前position对应位置的点击记录
                Boolean isClick = map.get(childPosition);
                if (!isClick) {
                    // 表示这个问题存在
                    ivChild.setImageResource(R.drawable.fuxuan_input01);
                    // 存在问题就设置为1;
                    isProblem = 1;
                } else {
                    // 这个问题不存在
                    ivChild.setImageResource(R.drawable.fuxuan_input02);
                    isProblem = 0;
                }

                // 将对应position位置改为对应的boolean值和值
                map.put(childPosition, !isClick);
                completeList.set(childPosition, isProblem);
                /*v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });*/
                return false;
            }
        });

    }

    /**
     * 获取测试项详情
     */
    private void getDetialData(int groupPosition) {
        progressBar.setVisibility(View.VISIBLE);
        String url = Urls.Url_Build_TestDetial;
        Map<String, String> params = new HashMap<>();
        params.put("device_id", cardnumber);
        params.put("test_id", hmTestId.get(groupPosition));
        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback1());
    }

    /**
     * 获取测试项
     */
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
                if (code == 0 && requires != null) {
                    if (!requires.isEmpty()) {
                        for (int i = 0; i < requires.size(); i++) {
                            TestItemBean.Data.Require require = requires.get(i);
                            requireTag = require.require_tag;
                            testId = require.test_id;
                            hmTestId.put(m++, testId);
                            listRequireTag.add(requireTag);
                        }
                    }
                }
                // 如果requireTag不为空，显示ExpandableListView
                adapter = new TestAdapter(mActivity, listRequireTag, listChild);
                elListview.setAdapter(adapter);

            }
        }
    }

    /**
     * 测试项的详情
     */
    class MyStringCallback1 extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {
            System.out.println("TestActivity+++获取测试项详情===界面失败" + e.getMessage());

            progressBar.setVisibility(View.GONE);
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("网络有问题，请检查")
                    .setCancelable(false).show();
        }

        @Override
        public void onResponse(String response) {
            System.out.println("测试详情+++===" + response);
            TestDetialBean bean = new Gson().fromJson(response, TestDetialBean.class);
            if (bean != null) {
                progressBar.setVisibility(View.GONE);
                int code = bean.code;
                String requireDesc = bean.data.require_desc;
                if (code == 0 && !TextUtils.isEmpty(requireDesc)) {
                    String[] split = requireDesc.split("\\|");
                    ArrayList<String> childitem = new ArrayList<String>();

                    for (int i = 0; i < split.length; i++) {
                        childitem.add(split[i]);
                        System.out.println(split[i]);
                    }
                    listChild.add(childitem);
                    // 如果requireTag不为空，显示ExpandableListView
                    adapter = new TestAdapter(mActivity, listRequireTag, listChild);
                    elListview.setAdapter(adapter);
                }
            }
        }
    }


}
