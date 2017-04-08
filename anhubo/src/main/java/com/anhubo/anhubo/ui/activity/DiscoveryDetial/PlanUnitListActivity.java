package com.anhubo.anhubo.ui.activity.DiscoveryDetial;

import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.AdapterPlanUnitList;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.UnitListBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.ui.activity.unitDetial.Cell_Detail_Activity;
import com.anhubo.anhubo.utils.JsonUtil;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.view.AlertDialog;
import com.anhubo.anhubo.view.RefreshListview;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;

/**
 * Created by LUOLI on 2017/3/27.
 */
public class PlanUnitListActivity extends BaseActivity {

    private static final String TAG = "PlanUnitListActivity";
    @InjectView(R.id.lv_plan_unit_list)
    RefreshListview lvPlanUnitList;
    private String uid;
    private String planId;
    private String type;
    private int pager = 0;
    private ArrayList<UnitListBean.Data.Units> unitList;
    private AdapterPlanUnitList adapterPlanUnitList;
    private int page;
    private Dialog dialog;

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        setTopBarDesc("单元列表");
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_plan_unit_list;
    }

    @Override
    protected void initViews() {
    }

    @Override
    protected void initEvents() {
        super.initEvents();
        getLocalData();
        if (!TextUtils.isEmpty(type)) {
            dialog = loadProgressDialog.show(mActivity, "正在加载...");
            if (TextUtils.equals(type, 1 + "")) {
                getData(1 + "");
            } else if (TextUtils.equals(type, 2 + "")) {
                getData(2 + "");
            }
        }
        setAdapters();
    }

    private void setAdapters() {
        unitList = new ArrayList<>();
        adapterPlanUnitList = new AdapterPlanUnitList(mActivity, unitList);
        lvPlanUnitList.setAdapter(adapterPlanUnitList);
        lvPlanUnitList.setOnRefreshingListener(new MyOnRefreshingListener());
        // 条目的点击事件
        setOnItemUnitListListener();
    }

    private boolean isLoadMore;//记录是否加载更多

    class MyOnRefreshingListener implements RefreshListview.OnRefreshingListener {

        @Override
        public void onLoadMore() {

            // 判断是否有更多数据，moreurl是否为空
            if (pager <= page) {
                // 加载更多业务

                isLoadMore = true;
                if (!TextUtils.isEmpty(type)) {
                    if (TextUtils.equals(type, 1 + "")) {
                        getData(1 + "");
                    } else if (TextUtils.equals(type, 2 + "")) {
                        getData(2 + "");
                    }
                }
            } else {
                lvPlanUnitList.setLoadMoretv("没有更多内容了");
            }
        }
    }

    /**
     * 条目的点击事件
     */
    private void setOnItemUnitListListener() {
        if (lvPlanUnitList != null) {

            lvPlanUnitList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String unitId = unitList.get(position).unit_id;
                    //点击进入单元详情界面
                    enterCellDetialActivity(unitId);
                }
            });
        }
    }

    @Override
    protected void onLoadDatas() {
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 进入单元详情界面
     */
    private void enterCellDetialActivity(String unitId) {
        Intent intent = new Intent();
        intent.setClass(mActivity, Cell_Detail_Activity.class);
        intent.putExtra(Keys.UNITID, unitId);
        intent.putExtra(Keys.PLANID, planId);
        startActivity(intent);
    }

    private void getData(String type) {

        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
//        LogUtils.eNormal(TAG, "uid+" + uid);
        params.put("plan_id", planId);
//        LogUtils.eNormal(TAG, "plan_id+" + planId);
        params.put("page", String.valueOf(pager++));
        LogUtils.eNormal(TAG, "pager+" + pager);
        params.put("type", type);
//        LogUtils.eNormal(TAG, "type+" + type);
        OkHttpUtils.post()
                .url(Urls.Url_PlanUnitList)
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
                                        if (isLoadMore) {
                                            lvPlanUnitList.loadMoreFinished();
                                        }
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
        UnitListBean bean = JsonUtil.json2Bean(response, UnitListBean.class);
        if (bean != null) {
            int code = bean.code;
            String msg = bean.msg;
            UnitListBean.Data data = bean.data;
            page = data.page;

            LogUtils.eNormal(TAG, "page+" + page);
            isLoadMore = false;
            // 恢复加载更多状态
            lvPlanUnitList.loadMoreFinished();
            if (code == 0) {
                List<UnitListBean.Data.Units> units = data.units;
                if (units != null) {
                    unitList.addAll(units);
                    adapterPlanUnitList.notifyDataSetChanged();
                }
            } else {
                ToastUtils.showToast(mActivity, msg);
            }
        } else {

            isLoadMore = false;
            // 恢复加载更多状态
            lvPlanUnitList.loadMoreFinished();
        }
    }

    private void getLocalData() {
        uid = SpUtils.getStringParam(mActivity, Keys.UID);
        planId = getIntent().getStringExtra(Keys.PLANID);
        type = getIntent().getStringExtra(Keys.TYPE_FULL);
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }

}
