package com.anhubo.anhubo.ui.activity.unitDetial;

import android.view.View;
import android.widget.Button;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.CellDetailAdapter;
import com.anhubo.anhubo.adapter.CellListAdapter;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.CellDeiailBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.view.RefreshListview;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by LUOLI on 2016/12/29.
 */
public class Cell_Detail_Activity extends BaseActivity {
    @InjectView(R.id.company_detail_listview)
    RefreshListview listview;
    @InjectView(R.id.exit_unit)
    Button exitUnit;
    private String unitId;
    private String uid;
    private int pager = 0;
    private int page;
    private List<CellDeiailBean.Data.Businesses> businesses;
    private boolean isLoadMore = false;
    private CellDetailAdapter adapter;

    @Override
    protected void initConfig() {
        super.initConfig();
        unitId = getIntent().getStringExtra(Keys.UNITID);
        uid = SpUtils.getStringParam(mActivity, Keys.UID);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_company_detail;
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        setTopBarDesc("单元详情");
    }

    @Override
    protected void initViews() {
// 监听listview的滑动监听
        listview.setOnRefreshingListener(new MyOnRefreshingListener());
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
        //　获取数据
        getData();
    }

    /**
     * 获取数据
     */
    private void getData() {
        String url = Urls.Url_Unit_Detail;
        Map<String, String> parmas = new HashMap<>();
        parmas.put("unit_id", unitId);
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
        }

        @Override
        public void onResponse(String response) {
            System.out.println("CellListActivity界面+" + response);
            setData(response);
        }
    }

    private void setData(String response) {
        CellDeiailBean bean = new Gson().fromJson(response, CellDeiailBean.class);
        if (bean != null) {
            int code = bean.code;
            String msg = bean.msg;
            CellDeiailBean.Data data = bean.data;
            page = data.page;
            businesses = data.businesses;
        }
        if (businesses == null) {
            listview.loadMoreFinished();
            ToastUtils.showToast(mActivity, "已经是最后一条了");
        }
        if (!isLoadMore) {
            adapter = new CellDetailAdapter(mActivity, businesses);
            listview.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @OnClick(R.id.exit_unit)
    public void onClick(View v) {

    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }
}
