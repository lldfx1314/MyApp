package com.anhubo.anhubo.ui.activity.unitDetial;

<<<<<<< HEAD
import android.app.Dialog;
=======
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
import android.view.View;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.UnitMenuAdapter;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.StudyBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.utils.Utils;
import com.anhubo.anhubo.view.AlertDialog;
import com.anhubo.anhubo.view.RefreshListview;
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
 * Created by Administrator on 2016/10/11.
 */
public class UnitMenuActivity extends BaseActivity {

    @InjectView(R.id.lv_study)
    RefreshListview lvStudy;
    private String businessId;
    private int pager;
    private List<StudyBean.Data.Records> records;
    private StudyBean.Data.Records record;
    private int page;
    private boolean isLoadMore = false;
    private UnitMenuAdapter menuAdapter;
    private String versionName;
    private ArrayList<Object> datas;
<<<<<<< HEAD
    private Dialog dialog;
=======
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce

    @Override
    protected void initConfig() {
        super.initConfig();
        // 获取business_id
<<<<<<< HEAD
        businessId = SpUtils.getStringParam(mActivity, Keys.BUSINESSID);
=======
        businessId = SpUtils.getStringParam(mActivity, Keys.BUSINESSID).trim();
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
        // 定义一个数，记录页数
        pager = 0;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.act_study_record;
    }

    @Override
    protected void initViews() {
        setTopBarDesc("执行记录");
    }

    @Override
    protected void initEvents() {
        super.initEvents();
        datas = new ArrayList<>();
        // 监听listview的滑动监听
        lvStudy.setOnRefreshingListener(new MyOnRefreshingListener());
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
                lvStudy.loadMoreFinished();
                ToastUtils.showToast(mActivity, "没有更多数据了");
            }
        }
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }

    @Override
    protected void onLoadDatas() {
        /**获取网络数据*/
<<<<<<< HEAD
        dialog = loadProgressDialog.show(mActivity, "正在加载...");
=======
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
        getData();
    }


    /**
     * 获取网络数据
     */
    private void getData() {
<<<<<<< HEAD
        String[] split = Utils.getAppInfo(mActivity).split("#");
        versionName = split[1];
=======

        String[] split = Utils.getAppInfo(mActivity).split("#");
        versionName = split[1];
        progressBar.setVisibility(View.VISIBLE);
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
        String url = Urls.Url_studyRecord;
        HashMap<String, String> params = new HashMap<>();
        params.put("business_id", businessId);
        params.put("version", versionName);
        params.put("page", String.valueOf(pager++));

        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback());

    }


    class MyStringCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {
            System.out.println("UnitMenuActivity+++===没拿到数据" + e.getMessage());
<<<<<<< HEAD
            dialog.dismiss();
=======
            progressBar.setVisibility(View.GONE);
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("网络有问题，请检查")
                    .setCancelable(false).show();
        }

        @Override
        public void onResponse(String response) {
//            System.out.println("执行记录++" + response);
<<<<<<< HEAD
            dialog.dismiss();
            StudyBean bean = new Gson().fromJson(response, StudyBean.class);
            if (bean != null) {
=======

            StudyBean bean = new Gson().fromJson(response, StudyBean.class);
            if (bean != null) {
                progressBar.setVisibility(View.GONE);
>>>>>>> 3e8e17c0bcfaefbf5a3deb90a517d6c61d5401ce
                processData(bean);
                isLoadMore = false;
                // 恢复加载更多状态
                lvStudy.loadMoreFinished();
            } else {
                isLoadMore = false;
                // 恢复加载更多状态
                lvStudy.loadMoreFinished();
                // 没拿到bean对象
                System.out.println("UnitMenuActivity+++===没拿到bean对象");
            }
        }
    }


    private void processData(StudyBean bean) {
        // 拿到解析的数据
        page = bean.data.page;
        records = bean.data.records;
        // 遍历集合
        for (int i = 0; i < records.size(); i++) {
            // 拿到一条信息记录
            record = records.get(i);
            String time = record.time;
            datas.add(time);
            datas.addAll(record.record_list);
        }

        if (!isLoadMore) {
            // 给listView设置适配器
            menuAdapter = new UnitMenuAdapter(mActivity, datas);
            lvStudy.setAdapter(menuAdapter);
        } else {
            menuAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {

    }
}
