package com.anhubo.anhubo.ui.impl;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.AdapterDotLine;
import com.anhubo.anhubo.adapter.AdapterJoinPlan;
import com.anhubo.anhubo.adapter.AdapterNoPlan;
import com.anhubo.anhubo.adapter.AdapterPlanManage;
import com.anhubo.anhubo.base.BaseFragment;
import com.anhubo.anhubo.bean.PlanBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.ui.activity.DiscoveryDetial.FeedActivity;
import com.anhubo.anhubo.ui.activity.DiscoveryDetial.JoinOtherPlanActivity;
import com.anhubo.anhubo.ui.activity.DiscoveryDetial.NoticeActivity;
import com.anhubo.anhubo.ui.activity.DiscoveryDetial.PlanManageActivity;
import com.anhubo.anhubo.ui.activity.DiscoveryDetial.UseGuideActivity;
import com.anhubo.anhubo.ui.activity.unitDetial.QrScanActivity;
import com.anhubo.anhubo.ui.activity.unitDetial.RunCertificateActivity;
import com.anhubo.anhubo.utils.JsonUtil;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.view.AlertDialog;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/8.
 */
public class FindFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{


    private static final String TAG = "FindFragment";
    private LinearLayout llplan;
    private RelativeLayout rlTest;

    private RelativeLayout rlNoPlan;
    private RelativeLayout rlJoinPlan;
    private RelativeLayout rlPlanManage;
    private RelativeLayout rlPlanList;
    private RecyclerView rvNoPlan;
    private RecyclerView rvJoinPlan;
    private RecyclerView rvPlanManage;
    private TextView tvJoinOtherPlan;
    private String uid;
    private String businessId;
    private AdapterNoPlan adapterNoPlan;
    private AdapterJoinPlan adapterJoinPlan;
    private AdapterPlanManage adapterPlanManage;
    private ArrayList<PlanBean.Data.PlanList> noPlanList;
    private ArrayList<PlanBean.Data.Cert> joinPlanPlanList;
    private ArrayList<PlanBean.Data.Manage> planManageList;
    private SwipeRefreshLayout swipe;
    private double dialog_i = 0;


    @Override
    public void initTitleBar() {
        //设置菜单键隐藏
        iv_basepager_left.setVisibility(View.GONE);
        tv_basepager_title.setText("计划");
        ivTopBarRightUnitMsg.setVisibility(View.VISIBLE);
        ivTopBarRightUnitMsg.setImageResource(R.drawable.notice_plan);
    }

    @Override
    public Object getContentView() {
        return R.layout.fragment_find;
    }

    @Override
    public void initView() {
        // 发现时的控件
        llplan = findView(R.id.ll_plan);
        rlTest = findView(R.id.rl_test);
        // 计划时的控件
        swipe = findView(R.id.swipe_refresh_find);// 无计划
        rlNoPlan = findView(R.id.rl_no_plan);// 无计划
        rlJoinPlan = findView(R.id.rl_join_plan);// 加入计划
        rlPlanManage = findView(R.id.rl_plan_manage);// 计划管理者
        rlPlanList = findView(R.id.rl_plan_list);// 计划列表
        rvNoPlan = findView(R.id.rv_no_plan);// 无计划
        rvJoinPlan = findView(R.id.rv_join_plan);// 加入计划
        rvPlanManage = findView(R.id.rv_plan_manage);// 计划管理者
        tvJoinOtherPlan = findView(R.id.tv_join_other_plan);// 计划列表  加其它计划入
        swipe.setColorSchemeResources(
                R.color.colorAccent,
                R.color.colorPrimary,
                R.color.colorPrimaryDark
        );
    }

    @Override
    public void initListener() {
        // 设置监听
        rlTest.setOnClickListener(this);
        tvJoinOtherPlan.setOnClickListener(this);// 加入其它计划监听
        //监听SwipeRefreshLayout.OnRefreshListener
        swipe.setOnRefreshListener(this);
    }


    @Override
    public void initData() {
        getLocalData();
        // 为三个RecyclerView设置适配器
        setAdapters();
        //　设置网络请求获取数据以显示内容
        getData();
    }


    @Override
    public void processClick(View view) {
        switch (view.getId()) {
            case R.id.rl_test:
                Intent intent = new Intent(mActivity, QrScanActivity.class);
                intent.putExtra(Keys.TEST, "test");
                startActivity(intent);
                break;
            // 以上是发现的点击事件
            case R.id.ivTopBarRight_unit_msg:
                startActivity(new Intent(mActivity, NoticeActivity.class));
                break;

            case R.id.tv_join_other_plan:
                //  加入其他计划
                startActivity(new Intent(mActivity, JoinOtherPlanActivity.class));
                break;
        }
    }

    /**
     * 为三个RecyclerView设置适配器
     */
    private void setAdapters() {
        noPlanList = new ArrayList<>();
        joinPlanPlanList = new ArrayList<>();
        planManageList = new ArrayList<>();
        //　没有加入计划
        rvNoPlan.setLayoutManager(new LinearLayoutManager(mActivity));
        adapterNoPlan = new AdapterNoPlan(mActivity, noPlanList);
        rvNoPlan.setAdapter(adapterNoPlan);
        //　计划凭证
        rvJoinPlan.setLayoutManager(new LinearLayoutManager(mActivity));
        adapterJoinPlan = new AdapterJoinPlan(mActivity, joinPlanPlanList);
        rvJoinPlan.setAdapter(adapterJoinPlan);
        //　计划管理
        rvPlanManage.setLayoutManager(new LinearLayoutManager(mActivity));
        adapterPlanManage = new AdapterPlanManage(mActivity, planManageList);
        rvPlanManage.setAdapter(adapterPlanManage);
        // 没加入计划的条目点击事件
        setOnItemNoPlanListener();
        // 加入计划的条目点击事件
        setOnItemPlanListener();
        // 计划管理的条目点击事件
        setOnItemManagePlanListener();
    }

    /**
     * 计划管理的条目点击事件
     */
    private void setOnItemManagePlanListener() {
        if (adapterPlanManage != null) {

            adapterPlanManage.setItemClickListener(new AdapterPlanManage.NoPlanItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
//                    ToastUtils.showToast(mActivity, "计划管理id：" + planManageList.get(position).plan_id);
                    Intent intent = new Intent(mActivity, PlanManageActivity.class);
                    intent.putExtra(Keys.PLANID, planManageList.get(position).plan_id);
                    startActivity(intent);
                }
            });
        }
    }

    /**
     * 加入计划的条目点击事件
     */
    private void setOnItemPlanListener() {
        if (adapterJoinPlan != null) {

            adapterJoinPlan.setItemClickListener(new AdapterJoinPlan.NoPlanItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
//                    ToastUtils.showToast(mActivity, "计划id：" + joinPlanPlanList.get(position).plan_id);
                    String planId = joinPlanPlanList.get(position).plan_id;
                    Intent intent = new Intent();
                    intent.setClass(mActivity, RunCertificateActivity.class);
                    intent.putExtra(Keys.PLANID, planId);
                    startActivity(intent);
                }
            });
        }
    }

    /**
     * 没加入计划的条目点击事件
     */
    private void setOnItemNoPlanListener() {
        if (adapterNoPlan != null) {

            adapterNoPlan.setItemClickListener(new AdapterNoPlan.NoPlanItemClickListener() {
                @Override
                public void onItemClick(View view, final int position) {
                    new AlertDialog(mActivity)
                            .builder()
                            .setTitle("提示")
                            .setMsg("请关注微信公众号：安互保 加入计划")
                            .setCancelable(false)
                            .setPositiveButton("确认", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ToastUtils.showToast(mActivity, "计划名称：" + noPlanList.get(position).plan_name);
                                }
                            })
                            .setNegativeButton("取消", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            })
                            .show();
                }
            });
        }
    }

    /**
     * 第一次网络请求，获取数据
     */
    private void getData() {
        final Dialog dialog = loadProgressDialog.show(mActivity, "正在加载...");
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
//        LogUtils.eNormal(TAG, "uid+" + uid);
        params.put("business_id", businessId);
//        LogUtils.eNormal(TAG, "businessId+" + businessId);
        OkHttpUtils.post()
                .url(Urls.Url_PlanModule)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        dialog.dismiss();
                        LogUtils.e(TAG, "getData:", e);
                        if (dialog_i == 0) {
                            showdialog("您的网络异常，请检查");
                            dialog_i++;
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        LogUtils.eNormal(TAG, "getData:" + response);
                        if (!TextUtils.isEmpty(response)) {
                            dealWith(response);
                        }
                    }
                });
    }

    private void dealWith(String response) {
        PlanBean bean = JsonUtil.json2Bean(response, PlanBean.class);
        int code1 = bean.code;
        if (bean != null) {
            int code = bean.code;
            String msg = bean.msg;
            if (code == 0) {
                llplan.setVisibility(View.VISIBLE);
                PlanBean.Data data = bean.data;
                int status = data.status;
                if (status == 0) {
                    // 没有加入计划
                    rlNoPlan.setVisibility(View.VISIBLE);
                    rlJoinPlan.setVisibility(View.GONE);
                    rlPlanManage.setVisibility(View.GONE);
                    rlPlanList.setVisibility(View.GONE);
                } else if (status == 1) {
                    // 加入计划 但不是管理员
                    rlNoPlan.setVisibility(View.GONE);
                    rlJoinPlan.setVisibility(View.VISIBLE);
                    rlPlanManage.setVisibility(View.GONE);
                    rlPlanList.setVisibility(View.VISIBLE);
                } else if (status == 2) {
                    // 是计划管理者
                    rlNoPlan.setVisibility(View.GONE);
                    rlJoinPlan.setVisibility(View.VISIBLE);
                    rlPlanManage.setVisibility(View.VISIBLE);
                    rlPlanList.setVisibility(View.VISIBLE);
                }
                // 未加入计划
                List<PlanBean.Data.PlanList> planList = data.list;
                noPlanList.clear();
                noPlanList.addAll(planList);
                adapterNoPlan.notifyDataSetChanged();
                // 加入计划
                List<PlanBean.Data.Cert> certs = data.cert;
                joinPlanPlanList.clear();
                joinPlanPlanList.addAll(certs);
                adapterJoinPlan.notifyDataSetChanged();
                // 计划管理
                List<PlanBean.Data.Manage> manages = data.manage;
                planManageList.clear();
                planManageList.addAll(manages);
                adapterPlanManage.notifyDataSetChanged();

            } else {
                ToastUtils.showToast(mActivity, msg);
            }
        }
    }

    /**
     * 获取本地数据
     */
    private void getLocalData() {
        uid = SpUtils.getStringParam(mActivity, Keys.UID);
        businessId = SpUtils.getStringParam(mActivity, Keys.BUSINESSID);
    }

    /**
     * 提示用户的弹窗
     */
    private void showdialog(String string) {
        new AlertDialog(mActivity).builder()
                .setTitle("提示")
                .setMsg(string)
                .setCancelable(false).show();
    }
    /**SwipeRefreshLayout 下拉刷新*/
    @Override
    public void onRefresh() {
        getLocalData();
        getData();
        swipe.setRefreshing(false);
    }
}
