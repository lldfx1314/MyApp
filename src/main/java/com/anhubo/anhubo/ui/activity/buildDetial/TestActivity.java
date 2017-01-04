package com.anhubo.anhubo.ui.activity.buildDetial;

import android.app.Dialog;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.TestAdapter;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.TestItemBean;
import com.anhubo.anhubo.bean.TestSubmitBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.view.AlertDialog;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
    @InjectView(R.id.iv_all_test)
    ImageView ivAllTest;
    private String cardnumber;
    private String testId;
    private String requireTag;
    private ArrayList<String> listRequireTag;
    private ArrayList<ArrayList<String>> listChild;
    private TestAdapter adapter;
    private HashMap<String, ArrayList<Integer>> hmTestId;

    private HashMap<Integer, ArrayList<Integer>> hmItem;
    private String testDesc;
    private Map<Integer, Map<Integer, Boolean>> listMap = new HashMap<>();// 用来记录所有组头对应的子item的状态
    private Map<Integer, Boolean> map = new HashMap<>();//用来记录每组子item对应的position位是否被点击
    private int isProblem = 0;                          //用来记录存在问题的选项，0表示没问题，1表示有问题
    private HashMap<Integer, Integer> hm;               // 用于记录每组 子item的数量，以便于创建对应于组头的集合，方便记录被选中的状态
    private ArrayList<Integer> completeList;            // 根据hm创建的对应于组头的子item集合
    private HashMap<Integer, String> hmBindTestId;      //把组头和testId绑定
    private Dialog showDialog1;
    private Dialog showDialog;

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
        listChild = new ArrayList<>();
        hmTestId = new HashMap<>();
        hmBindTestId = new HashMap<>();
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
                //　走提交的接口
                submit();
                break;
            case R.id.rl_all_test:
                // 当点击全部按钮的时候让其他全部都呈现未选中状态
                ivAllTest.setBackgroundResource(R.drawable.fuxuan_input01);


                for (int i = 0; i < hmItem.keySet().size(); i++) {

                    // 让所有的组头合起来,合起来的目的就是不显示已经展开子item的选中效果，当再次打开时呈现的是未选中效果
                    elListview.collapseGroup(i);

                    ArrayList<Integer> list = hmItem.get(i);
                    for (int m = 0; m < list.size(); m++) {
                        // 让全部item都呈现未选中状态
                        list.set(m, 0);
                    }
                    hmItem.put(i, list);
                    hmTestId.put(hmBindTestId.get(i), list);
                }
                adapter.setList(hmItem);
                break;
        }
    }

    private void submit() {
        String uid = SpUtils.getStringParam(mActivity, Keys.UID);
        ArrayList<String> requireIds = new ArrayList<>();
        Iterator<Map.Entry<String, ArrayList<Integer>>> iterator = hmTestId.entrySet().iterator();
        while (iterator.hasNext()) {
            //获取每一个Entry对象
            Map.Entry<String, ArrayList<Integer>> next = iterator.next();
            String key = next.getKey();                    //根据键值对对象获取键
            //根据键值对对象获取值
            ArrayList<Integer> list = next.getValue();
            String string = key + ":" + list.toString();
            requireIds.add(string);
        }

        showDialog1 = loadProgressDialog.show(mActivity, "正在提交...");
        Map<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(cardnumber)) {
            params.put("device_id", cardnumber);
        }
        params.put("uid", uid);
        if (!requireIds.isEmpty()) {
            params.put("require_ids", requireIds.toString());
        }
        String url = Urls.Url_Test_Submit;
        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback1());


    }

    private Handler handler = new Handler();

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }

    class MyStringCallback1 extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {
            showDialog1.dismiss();
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("网络有问题，请检查")
                    .setCancelable(true).show();
            System.out.println("TestActivity+++提交测试===界面失败" + e.getMessage());
        }

        @Override
        public void onResponse(String response) {
            //System.out.println(response);
            showDialog1.dismiss();
            TestSubmitBean bean = new Gson().fromJson(response, TestSubmitBean.class);
            if (bean != null) {
                int code = bean.code;
                if (code == 0) {
                    new AlertDialog(mActivity).builder()
                            .setTitle("提示")
                            .setMsg("提交成功")
                            .setPositiveButton("确认", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            finish();
                                        }
                                    }, 300);

                                }
                            }).setCancelable(false).show();

                }
            }
        }
    }

    /**
     * 点击事件
     */
    private void setListener() {

        /**全部按钮的点击监听*/
        rlAllTest.setOnClickListener(this);

        /**组头的点击事件*/

        elListview.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            int mOpenGroup = -1;

            @Override
            public boolean onGroupClick(final ExpandableListView parent, View v, final int groupPosition, long id) {

                if (elListview.isGroupExpanded(groupPosition)) {
                    // 已经展开去关闭
                    elListview.collapseGroup(groupPosition);
                } else {
                    // 已经关闭去 展开
                    elListview.collapseGroup(mOpenGroup);// 把之前展开的关闭掉
                    elListview.expandGroup(groupPosition, true);// 展开一个组
                    elListview.setSelection(groupPosition);// 把当前的展开的组置顶
                    mOpenGroup = groupPosition;// 记住每次展开的position
                }
                return true;
            }
        });

        /**孩子的点击事件*/
        elListview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                final ImageView ivChild = (ImageView) v.findViewById(R.id.iv_child);
                // 当任何一个子item被点击选中的时候全部无异常的控件选中状态应该取消掉
                ivAllTest.setBackgroundResource(R.drawable.fuxuan_input02);

                // 获取当前position对应位置的点击记录
                map = listMap.get(groupPosition);
                final Boolean isClick = map.get(childPosition);
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
                listMap.put(groupPosition, map);

                ArrayList<Integer> list = hmItem.get(groupPosition);
                list.set(childPosition, isProblem);
                hmItem.put(groupPosition, list);
                adapter.setList(hmItem);

                hmTestId.put(hmBindTestId.get(groupPosition), list);

                return false;
            }
        });

    }

    /**
     * 获取测试项
     */
    private void getData() {
        showDialog = loadProgressDialog.show(mActivity, "长在获取...");
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

            showDialog.dismiss();
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("网络有问题，请检查")
                    .setCancelable(false).show();
        }

        @Override
        public void onResponse(String response) {
            //System.out.println("获取测试项+++===" + response);
            showDialog.dismiss();
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
                            /********************************************************************/
                            requireTag = require.require_tag;
                            listRequireTag.add(requireTag);
                            /********************************************************************/
                            testId = require.test_id;
                            hmBindTestId.put(i, testId);

                            /********************************************************************/
                            testDesc = require.require_desc;
                            String[] split = testDesc.split("\\|");
                            ArrayList<String> childitem = new ArrayList<String>();

                            for (int m = 0; m < split.length; m++) {
                                childitem.add(split[m]);
                            }
                            listChild.add(childitem);
                        }
                        /********************************************************************/
                        // 创建一个hashmap，用于记录每组子item数量
                        hm = new HashMap<>();
                        //遍历listChild，listChild里面的元素是每组的元素的小集合
                        for (int j = 0; j < listChild.size(); j++) {
                            ArrayList<String> list = listChild.get(j);
                            int size = list.size();
                            // 记录每组子item数量
                            hm.put(j, size);

                            for (int m = 0; m < size; m++) {
                                map.put(m, false);
                            }
                            listMap.put(j, map);
                        }
                        /********************************************************************/
                        // 创建集合，保存每组被选中的状态
                        for (int i = 0; i < hm.keySet().size(); i++) {


                            // 得到每组子item的数
                            Integer items = hm.get(i);

                            // 创建对应的集合,集合长度为items，里面的每个元素初始值都为0
                            completeList = new ArrayList<>();
                            for (int m = 0; m < items; m++) {
                                completeList.add(m, 0);
                            }
                            /**用于提交的hm*/
                            hmTestId.put(hmBindTestId.get(i), completeList);

                            /**用于记录状态的hm*/ // 把该集合添加进HashMap里面
                            hmItem.put(i, completeList);
                        }
                    }
                    /********************************************************************/
                    adapter = new TestAdapter(mActivity, listRequireTag, listChild);
                    elListview.setAdapter(adapter);
                }
            }
        }
    }
}
