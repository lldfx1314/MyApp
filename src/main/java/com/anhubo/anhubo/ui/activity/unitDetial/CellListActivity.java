package com.anhubo.anhubo.ui.activity.unitDetial;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.CellListAdapter;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.CellListBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.view.RefreshListview;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by LUOLI on 2016/12/29.
 */
public class CellListActivity extends BaseActivity implements CellListAdapter.OnBtnClickListener {
    @InjectView(R.id.unit_list_listview)
    RefreshListview listview;
    @InjectView(R.id.create_unit)
    Button createUnit;
    private String planId;
    private int pager = 0;
    private int page;
    private List<CellListBean.Data.Units> units;
    private CellListAdapter adapter;
    private boolean isLoadMore = false;
    private String uid;
    private String unitId;
    private Dialog showDialog;

    @Override
    protected void initConfig() {
        super.initConfig();
        planId = getIntent().getStringExtra(Keys.PLANID);
        uid = SpUtils.getStringParam(mActivity, Keys.UID);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_unit_list;
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        setTopBarDesc("单元列表");
    }

    @Override
    protected void initViews() {
        // 监听listview的滑动监听
        listview.setOnRefreshingListener(new MyOnRefreshingListener());


    }

    /**
     * listView的条目点击事件
     */
    @Override
    public void onbtnClick(View v) {
        int position = (int) v.getTag();
        unitId = units.get(position).unit_id;
        // 跳转到加入列表的界面
        enterUnitActivity();
    }

    class MyOnRefreshingListener implements RefreshListview.OnRefreshingListener {

        @Override
        public void onLoadMore() {


            // 判断是否有更多数据，moreurl是否为空
            if (pager <= page) {
                // 加载更多业务
                isLoadMore = true;
                getData();
            } else {
                // 恢复Listview的加载更多状态
                listview.loadMoreFinished();
                ToastUtils.showToast(mActivity, "没有更多数据了");
            }
        }
    }

    @Override
    protected void onLoadDatas() {
        // 获取数据
        showDialog = loadProgressDialog.show(mActivity, "正在加载...");
        getData();
    }

    /**
     * 获取数据
     */
    private void getData() {
        String url = Urls.Url_Unit_List;
        Map<String, String> parmas = new HashMap<>();
        parmas.put("plan_id", planId);
        parmas.put("page", String.valueOf(pager++));

        OkHttpUtils.post()
                .url(url)
                .params(parmas)
                .build()
                .execute(new MyStringCallback());
    }

    class MyStringCallback extends StringCallback {

        @Override
        public void onError(Call call, Exception e) {
            System.out.println("CellListActivity界面+获取数据失败+" + e.getMessage());
            showDialog.dismiss();
        }

        @Override
        public void onResponse(String response) {
            System.out.println("CellListActivity界面+" + response);
            showDialog.dismiss();
            setData(response);
        }
    }

    private void setData(String response) {
        CellListBean bean = new Gson().fromJson(response, CellListBean.class);
        if (bean != null) {
            int code = bean.code;
            String msg = bean.msg;
            CellListBean.Data data = bean.data;
            page = data.page;
            units = data.units;
        }

        if (!isLoadMore) {
            adapter = new CellListAdapter(mActivity, units, CellListActivity.this);
            listview.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        if (units == null && adapter.getCount() != 0) {
            listview.loadMoreFinished();
            ToastUtils.showToast(mActivity, "已经是最后一条了");
        }

    }

    @OnClick(R.id.create_unit)
    public void onClick(View v) {
        // 创建单元
        enterUnitActivity();
    }

    /**
     * 加入和创建单元
     */
    private void enterUnitActivity() {
        Intent intent = new Intent();
        intent.setClass(mActivity, CellActivity.class);
        intent.putExtra(Keys.PLANID, planId);
        if (!TextUtils.isEmpty(unitId)) {
            intent.putExtra(Keys.UNITID, unitId);
        }
        startActivity(intent);
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }


}
