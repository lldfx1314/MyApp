package com.anhubo.anhubo.ui.activity.unitDetial;

import android.app.Dialog;
import android.view.View;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.UnitMsgCenterAdapter;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.UnitMsgCenterBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.JsonUtil;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.view.AlertDialog;
import com.anhubo.anhubo.view.RefreshListview;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.InjectView;

/**
 * Created by LUOLI on 2016/10/20.
 */
public class UnitMsgCenterActivity extends BaseActivity {


    private static final String TAG = "UnitMsgCenterActivity";
    @InjectView(R.id.lv_unit_msgCenter)
    RefreshListview lvUnitMsgCenter;
    private String uid;
    private int pager;
    private List<UnitMsgCenterBean.Data.Msg_list> msgList;
    private UnitMsgCenterBean.Data.Msg_list newMsg;
    private ArrayList<String> listMsg;
    private ArrayList<String> listTItle;
    private ArrayList<String> listTime;
    private View footer;
    private int page;
    private UnitMsgCenterAdapter msgAdapter;
    private Dialog showDialog;

    @Override
    protected void initConfig() {
        super.initConfig();
        // 获取business_id
        uid = SpUtils.getStringParam(mActivity, Keys.UID).trim();
        // 定义一个数，记录页数
        pager = 0;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.act_unit_msgcenter;
    }

    @Override
    protected void initViews() {
        setTopBarDesc("消息中心");
        // 创建一个集合用来存放time
        listTime = new ArrayList<>();
        // 创建一个集合用来存放userName
        listMsg = new ArrayList<>();
        // 创建一个集合用来存放typeId
        listTItle = new ArrayList<>();

        lvUnitMsgCenter.setOnRefreshingListener(new MyOnRefreshingListener());
    }

    private boolean isLoadMore;//记录是否加载更多

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

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
                lvUnitMsgCenter.setLoadMoretv("没有更多内容了");
            }
        }
    }


    @Override
    protected void onLoadDatas() {
        // 刚进来先获取网络数据
        showDialog = loadProgressDialog.show(mActivity, "正在加载...");
        getData();
    }

    private void getData() {

        String url = Urls.Url_UnitMsgCenter;
        HashMap<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("page", pager + "");
        pager++;
        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback());

    }

    @Override
    public void onClick(View v) {

    }
    class MyStringCallback extends StringCallback {
        @Override
        public void onError(Request request, Exception e) {
            showDialog.dismiss();
            LogUtils.e(TAG,":getData",e);
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("网络有问题，请检查")
                    .setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(isLoadMore){
                                lvUnitMsgCenter.loadMoreFinished();
                            }
                        }
                    })
                    .setCancelable(false).show();
        }

        @Override
        public void onResponse(String response) {
            LogUtils.eNormal(TAG+":getData",response);
            UnitMsgCenterBean bean = JsonUtil.json2Bean(response, UnitMsgCenterBean.class);
            if (bean != null) {
                showDialog.dismiss();
                processData(bean);
                isLoadMore = false;
                // 恢复加载更多状态
                lvUnitMsgCenter.loadMoreFinished();
            } else {
                isLoadMore = false;
                // 恢复加载更多状态
                lvUnitMsgCenter.loadMoreFinished();
            }
        }
    }


    private void processData(UnitMsgCenterBean bean) {

        page = bean.data.page;
        msgList = bean.data.msg_list;
        // 遍历集合
        if (msgList != null) {
            // 获取到数据后隐藏脚布局
            for (int i = 0; i < msgList.size(); i++) {
                // 拿到一条信息记录
                newMsg = msgList.get(i);
                // 获取时间
                String time = newMsg.time;
                listTime.add(time);
                // 获取msg
                String msg = newMsg.msg;
                listMsg.add(msg);
                // 获取title
                String title = newMsg.title;
                listTItle.add(title);
            }
        }
        if (!isLoadMore) {
            // 给listView设置适配器
            msgAdapter = new UnitMsgCenterAdapter(mActivity, listMsg, listTItle, listTime);
            lvUnitMsgCenter.setAdapter(msgAdapter);
        } else {
            // 是加载更多
            msgAdapter.notifyDataSetChanged();
        }

    }

}
