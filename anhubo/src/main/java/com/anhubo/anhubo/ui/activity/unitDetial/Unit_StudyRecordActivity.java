package com.anhubo.anhubo.ui.activity.unitDetial;

import android.app.Dialog;
import android.view.View;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.UnitMenuAdapter;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.StudyBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.JsonUtil;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.utils.Utils;
import com.anhubo.anhubo.view.AlertDialog;
import com.anhubo.anhubo.view.RefreshListview;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.InjectView;
import okhttp3.Call;

/**
 * Created by Administrator on 2016/10/11.
 */
public class Unit_StudyRecordActivity extends BaseActivity {

    private static final String TAG = "Unit_StudyRecordActivity";
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
    private Dialog dialog;

    @Override
    protected void initConfig() {
        super.initConfig();
        // 获取business_id
        businessId = SpUtils.getStringParam(mActivity, Keys.BUSINESSID);
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
                lvStudy.setLoadMoretv("没有更多内容了");
            }
        }
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }

    @Override
    protected void onLoadDatas() {
        /**获取网络数据*/
        dialog = loadProgressDialog.show(mActivity, "正在加载...");
        getData();
    }


    /**
     * 获取网络数据
     */
    private void getData() {
        String[] split = Utils.getAppInfo(mActivity).split("#");
        versionName = split[1];
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
            dialog.dismiss();
            LogUtils.e(TAG,":getData",e);
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("网络有问题，请检查")
                    .setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(isLoadMore){
                                lvStudy.loadMoreFinished();
                            }
                        }
                    })
                    .setCancelable(false).show();
        }

        @Override
        public void onResponse(String response) {
            LogUtils.eNormal(TAG+":getData",response);
            dialog.dismiss();
            StudyBean bean = JsonUtil.json2Bean(response, StudyBean.class);
            if (bean != null) {
                processData(bean);
                isLoadMore = false;
                // 恢复加载更多状态
                lvStudy.loadMoreFinished();
            } else {
                isLoadMore = false;
                // 恢复加载更多状态
                lvStudy.loadMoreFinished();
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
