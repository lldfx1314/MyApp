package com.anhubo.anhubo.ui.activity.DiscoveryDetial;

import android.app.Dialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.AdapterNoPlan;
import com.anhubo.anhubo.adapter.AdapterPlanList;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.JoinOtherPlanBean;
import com.anhubo.anhubo.protocol.Urls;
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

import butterknife.InjectView;

/**
 * Created by LUOLI on 2017/3/24.
 */
public class JoinOtherPlanActivity extends BaseActivity {
    private static final String TAG = "JoinOtherPlanActivity";
    @InjectView(R.id.rv_other_plan)
    RecyclerView rvOtherPlan;
    private ArrayList<JoinOtherPlanBean.Data.PlanList> planList;
    private AdapterNoPlan adapterNoPlan;
    private String businessId;
    private AdapterPlanList adapterPlanList;

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        setTopBarDesc("计划列表");
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_other_plan;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initEvents() {
        super.initEvents();
        getLocalData();
        setAdapters();
        getData();
    }


    @Override
    protected void onLoadDatas() {

    }

    @Override
    public void onClick(View v) {

    }

    private void getData() {
        final Dialog dialog = loadProgressDialog.show(mActivity, "正在加载...");
        Map<String, String> params = new HashMap<>();
        params.put("business_id", businessId);
        LogUtils.eNormal(TAG, "businessId+" + businessId);
        OkHttpUtils.post()
                .url(Urls.Url_JoinPlan)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        dialog.dismiss();
                        LogUtils.e(TAG, "getData:", e);
                        new AlertDialog(mActivity).builder().setTitle("提示")
                                .setMsg("网络错误")
                                .setCancelable(false)
                                .setPositiveButton("返回", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        finish();
                                    }
                                }).show();
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
        JoinOtherPlanBean bean = JsonUtil.json2Bean(response, JoinOtherPlanBean.class);
        if (bean != null) {
            int code = bean.code;
            String msg = bean.msg;
            JoinOtherPlanBean.Data data = bean.data;
            if (code == 0) {
                List<JoinOtherPlanBean.Data.PlanList> list = data.list;
                if(list!=null){
                    planList.clear();
                    planList.addAll(list);
                    adapterPlanList.notifyDataSetChanged();
                }else{
                    new AlertDialog(mActivity).builder().setTitle("提示")
                            .setMsg("没有更多的计划")
                            .setCancelable(false)
                            .setPositiveButton("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                }
                            }).show();
                }

            } else {
                ToastUtils.showToast(mActivity, msg);
            }
        }
    }

    private void setAdapters() {
        planList = new ArrayList<>();

        rvOtherPlan.setLayoutManager(new LinearLayoutManager(mActivity));
        adapterPlanList = new AdapterPlanList(mActivity, planList);
        rvOtherPlan.setAdapter(adapterPlanList);

        // 没加入计划的条目点击事件
        setOnItemNoPlanListener();

    }

    private void setOnItemNoPlanListener() {
        if (adapterPlanList != null) {

            adapterPlanList.setItemClickListener(new AdapterPlanList.NoPlanItemClickListener() {
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
//                                    ToastUtils.showToast(mActivity, "计划名称：" + noPlanList.get(position).plan_name);
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

    private void getLocalData() {
        businessId = SpUtils.getStringParam(mActivity, Keys.BUSINESSID);
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }
}
