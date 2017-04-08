package com.anhubo.anhubo.ui.activity.DiscoveryDetial;

import android.app.Dialog;
import android.content.Intent;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.PlanManageBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.DisplayUtil;
import com.anhubo.anhubo.utils.JsonUtil;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.utils.Utils;
import com.anhubo.anhubo.view.AlertDialog;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by LUOLI on 2017/3/22.
 */
public class PlanManageActivity extends BaseActivity {
    private static final String TAG = "PlanManageActivity";
    @InjectView(R.id.tv_plan_member)
    TextView tvPlanMember;
    @InjectView(R.id.ll_plan_manage)
    LinearLayout llPlanManage;
    @InjectView(R.id.ll_plan_member)
    LinearLayout llPlanMember;
    @InjectView(R.id.tv_plan_redWarning)
    TextView tvPlanRedWarning;
    @InjectView(R.id.tv_plan_yellowWarning)
    TextView tvPlanYellowWarning;
    @InjectView(R.id.tv_plan_greenWarning)
    TextView tvPlanGreenWarning;
    @InjectView(R.id.tv_plan_riskIndex)
    TextView tvPlanRiskIndex;
    @InjectView(R.id.ll_plan_riskIndex)
    LinearLayout llPlanRiskIndex;
    @InjectView(R.id.tv_plan_helpEvent)
    TextView tvPlanHelpEvent;
    @InjectView(R.id.ll_plan_helpEvent)
    LinearLayout llPlanHelpEvent;
    @InjectView(R.id.tv_plan_quitApply)
    TextView tvPlanQuitApply;
    @InjectView(R.id.ll_plan_quitApply)
    LinearLayout llPlanQuitApply;
    @InjectView(R.id.tv_plan_helpSumMoney)
    TextView tvPlanHelpSumMoney;
    @InjectView(R.id.ll_plan_helpSumMoney)
    LinearLayout llPlanHelpSumMoney;
    @InjectView(R.id.tv_plan_ServiceMoney)
    TextView tvPlanServiceMoney;
    @InjectView(R.id.ll_plan_ServiceMoney)
    LinearLayout llPlanServiceMoney;
    private String uid;
    private String planId;
    private SpannableString ss;

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        setTopBarDesc("计划管理");
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_plan_manage;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initEvents() {
        super.initEvents();
        getLocalData();
        getData();
    }


    @Override
    protected void onLoadDatas() {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }


    @OnClick({R.id.ll_plan_member, R.id.ll_plan_riskIndex, R.id.ll_plan_helpEvent, R.id.ll_plan_quitApply, R.id.ll_plan_helpSumMoney, R.id.ll_plan_ServiceMoney})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_plan_member:
                Intent intent = new Intent(mActivity, PlanMemberActivity.class);
                intent.putExtra(Keys.PLANID, planId);
                startActivity(intent);
                break;
            case R.id.ll_plan_riskIndex:
//                ToastUtils.showToast(mActivity, "这是风险指数");
                break;
            case R.id.ll_plan_helpEvent:
//                ToastUtils.showToast(mActivity, "这是互助事件");
                break;
            case R.id.ll_plan_quitApply:
//                ToastUtils.showToast(mActivity, "这是退出申请");
                break;
            case R.id.ll_plan_helpSumMoney:
                Intent intentHelpSumMoney = new Intent(mActivity, PlanHelpSumMoneyActivity.class);
                intentHelpSumMoney.putExtra(Keys.PLANID, planId);
                startActivity(intentHelpSumMoney);
                break;
            case R.id.ll_plan_ServiceMoney:
//                ToastUtils.showToast(mActivity, "这是计划服务费");
                break;
        }
    }

    /**
     * 获取网络数据
     */
    private void getData() {
        final Dialog dialog = loadProgressDialog.show(mActivity, "正在加载...");
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
//        LogUtils.eNormal(TAG, "uid+" + uid);
        params.put("plan_id", planId);
//        LogUtils.eNormal(TAG, "plan_id+" + planId);
        OkHttpUtils.post()
                .url(Urls.Url_PlanManage)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        dialog.dismiss();
                        LogUtils.e(TAG, "getData:", e);
                        new AlertDialog(mActivity).builder().setTitle("提示").setMsg("网络错误").setPositiveButton("返回", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
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
        PlanManageBean bean = JsonUtil.json2Bean(response, PlanManageBean.class);
        if (bean != null) {
            int code = bean.code;
            String msg = bean.msg;
            PlanManageBean.Data data = bean.data;
            if (code == 0) {
                llPlanManage.setVisibility(View.VISIBLE);
                String actNum = data.act_num;// 加入单位数
                int redPercent = data.red_percent;
                int yelPercent = data.yel_percent;
                int greenPercent = data.green_percent;
                String warming = data.warming;// 风险指数
                String payedNum = data.payed_num;// 互助事件
                String applyNum = data.apply_num;// 申请退出数
                String sumMoney = data.sum_money;// 互助金总额
                int serviceMoney = data.service_money;//计划服务费
                // 设置控件显示内容
                tvPlanMember.setText(actNum);
                tvPlanRedWarning.setText(redPercent+"%");
                tvPlanYellowWarning.setText(yelPercent+"%");
                tvPlanGreenWarning.setText(greenPercent+"%");
                tvPlanRiskIndex.setText(warming);
                tvPlanHelpEvent.setText(payedNum);
                tvPlanQuitApply.setText(applyNum);
//                tvPlanHelpSumMoney.setText(sumMoney);
                setShowDetial(sumMoney,tvPlanHelpSumMoney);
//                tvPlanServiceMoney.setText(serviceMoney+"");
                setShowDetial(serviceMoney+"",tvPlanServiceMoney);

            } else {
                new AlertDialog(mActivity).builder()
                        .setTitle("提示")
                        .setMsg(msg)
                        .setCancelable(false)
                        .setPositiveButton("确认", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        }).show();
            }
        }
    }

    /**
     * 获取本地数据
     */
    private void getLocalData() {
        uid = SpUtils.getStringParam(mActivity, Keys.UID);
        planId = getIntent().getStringExtra(Keys.PLANID);
    }

    private void setShowDetial( String string, TextView textView) {
        if (!TextUtils.isEmpty(string)) {

            Double heighSharing = Double.parseDouble(string) / 10000;

            if (heighSharing < 1) {
                if (string.endsWith(".0")) {
                    string = string.replace(".0", "");
                }
                textView.setText(string);
            } else if (heighSharing >= 1) {
                String str = String.valueOf(heighSharing);
                if (str.length() >= 3) {
                    String substring = str.substring(0, 3);
                    //　做判断，防止显示类似＂50.万＂这样的情况
                    if (substring.endsWith(".")) {
                        setWan(substring.substring(0, 2) + "万");
                    } else if (substring.endsWith(".0")) {
                        setWan(substring.substring(0, 1) + "万");
                    }
                } else {
                    setWan(str + "万");

                }
                textView.setHorizontallyScrolling(true);
                textView.setText(ss);
            }
        }
    }

    /**
     * 设置万字大小
     */
    private void setWan(String string) {

        ss = new SpannableString(string);
        MyURLSpan myURLSpan = new MyURLSpan(string);
        ss.setSpan(myURLSpan, string.length() - 1, string.length(), SpannableString.SPAN_EXCLUSIVE_INCLUSIVE);
    }

    class MyURLSpan extends URLSpan {


        public MyURLSpan(String url) {
            super(url);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setTextSize(DisplayUtil.sp2px(mActivity, 13));
        }
    }

}
