package com.anhubo.anhubo.ui.activity.unitDetial;

import android.view.View;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.UnitMenuAdapter;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.StudyBean;
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
    private List<StudyBean.Data.Records.Record_list> recordList;
    private StudyBean.Data.Records.Record_list recordDetails;
    private ArrayList<String> listUserName;
    private ArrayList<String> listTypeId;
    private ArrayList<String> listTime;
    private int page;
    private boolean isLoadMore = false;
    private UnitMenuAdapter menuAdapter;

    @Override
    protected void initConfig() {
        super.initConfig();
        // 获取business_id
        businessId = SpUtils.getStringParam(mActivity, Keys.BUSINESSID).trim();
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

        // 创建一个集合用来存放time
        listTime = new ArrayList<>();

        listUserName = new ArrayList<>();
        // 创建一个集合用来存放typeId
        listTypeId = new ArrayList<>();

        // 监听listview的滑动监听
        lvStudy.setOnRefreshingListener(new MyOnRefreshingListener());
    }

    @Override
    protected void onLoadDatas() {
        /**获取网络数据*/
        getData();
    }


    /**
     * 获取网络数据
     */
    private void getData() {
        String url = Urls.Url_studyRecord;
        HashMap<String, String> params = new HashMap<>();
        params.put("business_id", businessId);
        params.put("page", pager + "");
        pager++;
        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback());

    }

    class MyOnRefreshingListener implements RefreshListview.OnRefreshingListener {

        @Override
        public void onLoadMore() {


            // 判断是否有更多数据，moreurl是否为空
            if(pager<=page){
                // 加载更多业务
                isLoadMore = true;
                getData();
            }else{
                // 恢复Listview的加载更多状态
                lvStudy.loadMoreFinished();
                ToastUtils.showToast(mActivity,"没有更多数据了");
            }
        }
    }

    class MyStringCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {
            System.out.println("UnitMenuActivity+++===没拿到数据" + e.getMessage());
        }

        @Override
        public void onResponse(String response) {
            StudyBean bean = new Gson().fromJson(response, StudyBean.class);
            if (bean != null) {
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

            listTime.add(time);
            // 拿到详细记录的集合
            recordList = record.record_list;
            //  遍历集合
            for (int j = 0; j < recordList.size(); j++) {
                // 获取到每条详细记录
                recordDetails = recordList.get(j);
                String typeId = recordDetails.type_id;
                String userName = recordDetails.user_name;
                listTypeId.add(typeId);
                listUserName.add(userName);
            }
            if(!isLoadMore){
                // 给listView设置适配器
                menuAdapter = new UnitMenuAdapter(mActivity, listTime, listUserName, listTypeId,pager);
                lvStudy.setAdapter(menuAdapter);
            }else{
                menuAdapter.notifyDataSetChanged();
            }


        }
    }


    @Override
    public void onClick(View v) {

    }
}
