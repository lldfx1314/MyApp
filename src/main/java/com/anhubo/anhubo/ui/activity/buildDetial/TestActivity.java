package com.anhubo.anhubo.ui.activity.buildDetial;

import android.text.TextUtils;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
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
    private ArrayList<ArrayList<String>> listChild;
    private TestAdapter adapter;
    private HashMap<Integer, String> hmTestId;
    private int m = 0;


    private HashMap<Integer,ArrayList<Integer>> hmItem;
    private Map<Integer, Map<Integer, Boolean>> listMap = new HashMap<>();
    /*用来记录对应的position位是否被点击*/
    private Map<Integer, Boolean> map = new HashMap<>();
    /* 用来记录存在问题的选项，0表示没问题，1表示有问题*/
    private int isProblem = 0;
    private String testDesc;
    private HashMap<Integer, Integer> hm;
    private ArrayList<Integer> completeList;
    private int groupItem = 0;
    private int childItem = 0;

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
        // 初始化completeList
        completeList = new ArrayList<>();
        hmItem = new HashMap<>();
        // 获取测试项
        getData();


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

        /*elListview.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(final ExpandableListView parent, View v, final int groupPosition, long id) {
                System.out.println("组头" + groupPosition);

                return false;
            }
        });*/

        /**孩子的点击事件*/
        elListview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                final ImageView ivChild = (ImageView) v.findViewById(R.id.iv_child);

                System.out.println("点击了22222groupPosition+++==="+groupPosition);
                System.out.println("点击了22222childPosition+++==="+childPosition);
                groupItem = groupPosition;
                childItem = childPosition;

                // 获取当前position对应位置的点击记录
                map = listMap.get(groupPosition);
                final Boolean isClick = map.get(childPosition);
//                if(groupItem == groupPosition && childItem == childPosition){
                adapter.setOnChildClickListener(new TestAdapter.OnChildClickListener() {
                    @Override
                    public void childClick() {
                        if (!isClick) {
                            // 表示这个问题存在
                            //ivChild.setTag(groupPosition,childPosition);
                            ivChild.setImageResource(R.drawable.fuxuan_input01);
                            // 存在问题就设置为1;
                            isProblem = 1;
                        } else {
                            // 这个问题不存在
                            //ivChild.setTag(groupPosition,childPosition);
                            ivChild.setImageResource(R.drawable.fuxuan_input02);
                            isProblem = 0;
                        }
                    }
                });

//                }
                // 将对应position位置改为对应的boolean值和值
                map.put(childPosition, !isClick);
                listMap.put(groupPosition,map);
                //completeList.set(childPosition, isProblem);
                //hmItem.put(groupPosition,completeList);
                return true;
            }
        });

    }

    /**
     * 获取测试项
     */
    private void getData() {
        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(cardnumber)) {
            params.put("device_id", cardnumber);
        }
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
            //System.out.println("获取测试项+++===" + response);
            progressBar.setVisibility(View.GONE);
            TestItemBean bean = new Gson().fromJson(response, TestItemBean.class);
            if (bean != null) {
                int code = bean.code;
                String msg = bean.msg;
                String deviceName = bean.data.device_name;
                List<TestItemBean.Data.Require> requires = bean.data.require;
                if (code == 0 && requires != null) {
                    if (requires.size() > 0) {
                        for (int i = 0; i < requires.size(); i++) {
                            TestItemBean.Data.Require require = requires.get(i);
                            requireTag = require.require_tag;
                            listRequireTag.add(requireTag);

                            testDesc = require.require_desc;
                            String[] split = testDesc.split("\\|");
                            ArrayList<String> childitem = new ArrayList<String>();

                            for (int m = 0; m < split.length; m++) {
                                childitem.add(split[m]);
                            }
                            listChild.add(childitem);
                        }
                        // 创建一个hashmap，用于记录每组子item数量
                        hm = new HashMap<>();
                        //遍历listChild，listChild里面的元素是每组的元素的小集合
                        for (int j = 0; j < listChild.size(); j++) {
                            ArrayList<String> list = listChild.get(j);
                            int size = list.size();
                            // 记录每组子item数量
                            hm.put(j,size);
                            //System.out.println("添加+++==="+j+"+++"+"size+++=="+size);
                            for (int m = 0; m < size; m++) {
                                map.put(m, false);
                            }
                            listMap.put(j, map);
                        }
                        // 创建集合，保存每组被选中的状态
                        for (int i = 0; i < hm.keySet().size(); i++) {


                            // 得到每组子item的数
                            Integer items = hm.get(i);
                            System.out.println("items长度+++==="+items);

                            // 创建对应的集合,集合长度为items里面的每个元素初始值都为0
                            ArrayList<Integer> arrayList = new ArrayList<>();
                            for (int m = 0; m < items; m++) {
                                arrayList.add(m,0);
                            }
                            //System.out.println("arrayList长度+++==="+arrayList.size());
                            //System.out.println("arrayList+++==="+arrayList.toString());
                            // 把该集合添加进HashMap里面
                            hmItem.put(i,arrayList);
                        }
                    }
                    // 如果requireTag不为空，显示ExpandableListView
                    adapter = new TestAdapter(mActivity, listRequireTag, listChild);
                    elListview.setAdapter(adapter);
                }
            }
        }
    }
}
