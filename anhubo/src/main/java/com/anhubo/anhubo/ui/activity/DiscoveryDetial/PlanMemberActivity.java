package com.anhubo.anhubo.ui.activity.DiscoveryDetial;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.AdapterPlanJoinRecord;
import com.anhubo.anhubo.adapter.AdapterPlanNumberFull;
import com.anhubo.anhubo.adapter.AdapterPlanNumberUnFull;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.PlanMemberBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.ui.activity.unitDetial.Cell_Detail_Activity;
import com.anhubo.anhubo.utils.JsonUtil;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.view.AlertDialog;
import com.anhubo.anhubo.view.ShowBottonDialog;
import com.squareup.okhttp.Request;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/3/22.
 */
public class PlanMemberActivity extends BaseActivity {
    private static final String TAG = "PlanMemberActivity";
    @InjectView(R.id.ll_plan_member)
    LinearLayout llPlanMember;
    @InjectView(R.id.tv_plan_Nofullmore)
    TextView tvPlanNofullmore;
    @InjectView(R.id.tv_plan_fullmore)
    TextView tvPlanFullmore;
    @InjectView(R.id.rv_plan_member_unfull)
    RecyclerView rvPlanMemberUnfull;
    @InjectView(R.id.rv_plan_member_full)
    RecyclerView rvPlanMemberFull;
    @InjectView(R.id.rv_plan_join_Record)
    RecyclerView rvPlanJoinRecord;
    @InjectView(R.id.tv_plan_joinRecordNum)
    TextView tvPlanJoinRecordNum;
    @InjectView(R.id.tv_actualJoinPeople)
    TextView tvActualJoinPeople;
    @InjectView(R.id.tv_planJoinPeople)
    TextView tvPlanJoinPeople;
    private String planId;
    private String uid;
    private ArrayList<PlanMemberBean.Data.Business_names> joinRecordList;
    private AdapterPlanJoinRecord adapterPlanJoinRecord;
    private ArrayList<PlanMemberBean.Data.Unfilled_units> unfullList;
    private AdapterPlanNumberUnFull adapterPlanNumberUnFull;
    private AdapterPlanNumberFull adapterPlanNumberFull;
    private ArrayList<PlanMemberBean.Data.Filled_units> fullList;
    private Dialog dialog;
    private Button btnWeixin;
    private Button btnweixinCircle;

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        setTopBarDesc("成员情况");
    }

    @Override
    protected void initConfig() {
        super.initConfig();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_plan_member;
    }

    @Override
    protected void initViews() {

        ivTopBarRightUnitShare.setVisibility(View.VISIBLE);
        ivTopBarRightUnitShare.setImageResource(R.drawable.paln_member_add_white);
        ivTopBarRightUnitShare.setOnClickListener(this);
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

        ShareAction shareAction = new ShareAction(mActivity);
        String newUrl = Urls.Url_Cell_WeiXin + "?plan_id=" + planId;
        String title = "好友邀请你一起加入\"计划名称\"下\"单元名称\"单元,获取安全保障";
        String context = "加入互助计划,时刻享用安全保障,前行路上更安心";
        switch (v.getId()) {
            case R.id.ivTopBarRight_unit_share:
                showDialog();
                break;
            case R.id.btn_weixin:

                shareAction
                        .setPlatform(SHARE_MEDIA.WEIXIN)
                        .withText(context)
                        .withTitle(title)
                        .withTargetUrl(newUrl)
                        .setCallback(umShareListener)
                        .share();
                break;
            case R.id.btn_weixin_circle:
                shareAction
                        .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                        .withText(context)
                        .withTitle(title)
                        .withTargetUrl(newUrl)
                        .setCallback(umShareListener)
                        .share();
                break;
        }
    }

    @OnClick({R.id.tv_plan_Nofullmore, R.id.tv_plan_fullmore})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_plan_Nofullmore:
                Intent intent = new Intent();
                intent.setClass(mActivity, PlanUnitListActivity.class);
                intent.putExtra(Keys.PLANID, planId);
                intent.putExtra(Keys.TYPE_FULL, 1 + "");
                startActivity(intent);
                break;
            case R.id.tv_plan_fullmore:
                Intent intent1 = new Intent();
                intent1.setClass(mActivity, PlanUnitListActivity.class);
                intent1.putExtra(Keys.PLANID, planId);
                intent1.putExtra(Keys.TYPE_FULL, 2 + "");
                startActivity(intent1);
                break;
        }
    }

    /**
     * 弹出对话框
     */
    private void showDialog() {
        // 创建一个对象
        View view = View.inflate(mActivity, R.layout.dialog_share, null);
        View btnCancel = view.findViewById(R.id.btn_share_Dialog_cancel);//取消按钮
        //显示对话框
        ShowBottonDialog showBottonDialog = new ShowBottonDialog(mActivity, view, btnCancel);
        dialog = showBottonDialog.show();
        //拍照按钮
        btnWeixin = (Button) view.findViewById(R.id.btn_weixin);
        //相册获取
        btnweixinCircle = (Button) view.findViewById(R.id.btn_weixin_circle);
        // 设置监听
        btnWeixin.setOnClickListener(this);
        btnweixinCircle.setOnClickListener(this);
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            //System.out.println(platform + "分享成功");
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            ToastUtils.showToast(mActivity, "分享失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            ToastUtils.showToast(mActivity, "分享取消");

        }
    };

    /**
     * 设置设配器
     */
    private void setAdapters() {
        unfullList = new ArrayList<>();
        fullList = new ArrayList<>();
        joinRecordList = new ArrayList<>();
        // 未满员
        rvPlanMemberUnfull.setLayoutManager(new LinearLayoutManager(mActivity));
        adapterPlanNumberUnFull = new AdapterPlanNumberUnFull(mActivity, unfullList);
        rvPlanMemberUnfull.setAdapter(adapterPlanNumberUnFull);
        // 满员
        rvPlanMemberFull.setLayoutManager(new LinearLayoutManager(mActivity));
        adapterPlanNumberFull = new AdapterPlanNumberFull(mActivity, fullList);
        rvPlanMemberFull.setAdapter(adapterPlanNumberFull);
        //加入记录
        rvPlanJoinRecord.setLayoutManager(new LinearLayoutManager(mActivity));
        adapterPlanJoinRecord = new AdapterPlanJoinRecord(mActivity, joinRecordList);
        rvPlanJoinRecord.setAdapter(adapterPlanJoinRecord);

        // 未满员条目点击事件
        setOnItemUnFullListener();
        // 满员条目点击事件
        setOnItemFullListener();
    }

    /**
     * 未满员
     */
    private void setOnItemFullListener() {
        if (adapterPlanNumberFull != null) {

            adapterPlanNumberFull.setItemClickListener(new AdapterPlanNumberFull.NoPlanItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    String unitId = unfullList.get(position).unit_id;
                    //点击进入单元详情界面
                    enterCellDetialActivity(unitId);
                }
            });
        }
    }

    /**
     * 未满员
     */
    private void setOnItemUnFullListener() {
        if (adapterPlanNumberUnFull != null) {

            adapterPlanNumberUnFull.setItemClickListener(new AdapterPlanNumberUnFull.NoPlanItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    String unitId = unfullList.get(position).unit_id;
                    //点击进入单元详情界面
                    enterCellDetialActivity(unitId);
                }
            });
        }
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

    /**
     * 获取数据
     */
    private void getData() {
        final Dialog dialog = loadProgressDialog.show(mActivity, "正在加载...");
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
//        LogUtils.eNormal(TAG, "uid+" + uid);
        params.put("plan_id", planId);
//        LogUtils.eNormal(TAG, "plan_id+" + planId);
        OkHttpUtils.post()
                .url(Urls.Url_PlanMember)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        dialog.dismiss();
                        LogUtils.e(TAG, "getData:", e);
                        new AlertDialog(mActivity).builder().setTitle("提示").setMsg("网络错误").setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                finish();
                            }
                        }).setCancelable(false).show();
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
        PlanMemberBean bean = JsonUtil.json2Bean(response, PlanMemberBean.class);
        if (bean != null) {
            int code = bean.code;
            String msg = bean.msg;
            PlanMemberBean.Data data = bean.data;
            if (code == 0) {
                llPlanMember.setVisibility(View.VISIBLE);
                String actualNum = data.actual_num;
                String planNum = data.plan_num;
                tvActualJoinPeople.setText(actualNum);
                tvPlanJoinPeople.setText("/" + planNum);
                // 今天刚加入数量
                String todayNum = data.today_num;
                tvPlanJoinRecordNum.setText("新加入" + todayNum + "家");
                // 为满员
                List<PlanMemberBean.Data.Unfilled_units> unfilledUnits = data.unfilled_units;
                unfullList.clear();
                unfullList.addAll(unfilledUnits);
                adapterPlanNumberUnFull.notifyDataSetChanged();

                // 满员
                List<PlanMemberBean.Data.Filled_units> filledUnits = data.filled_units;
                fullList.clear();
                fullList.addAll(filledUnits);
                adapterPlanNumberFull.notifyDataSetChanged();
                // 加入记录
                List<PlanMemberBean.Data.Business_names> businessNames = data.business_names;
                joinRecordList.clear();
                joinRecordList.addAll(businessNames);
                adapterPlanJoinRecord.notifyDataSetChanged();
            } else {
                ToastUtils.showToast(mActivity, msg);
            }
        }
    }

    private void getLocalData() {
        uid = SpUtils.getStringParam(mActivity, Keys.UID);
        planId = getIntent().getStringExtra(Keys.PLANID);
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }

}
