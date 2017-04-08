package com.anhubo.anhubo.ui.activity.unitDetial;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.RunCertificateIconAdapter;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.RunCertificateBean;
import com.anhubo.anhubo.entity.RxBus;
import com.anhubo.anhubo.entity.event.Rxbus_JoinUnit;
import com.anhubo.anhubo.protocol.DividerItemDecoration;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.ui.activity.DiscoveryDetial.NoticeActivity;
import com.anhubo.anhubo.utils.DisplayUtil;
import com.anhubo.anhubo.utils.JsonUtil;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.view.AlertDialog;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by LUOLI on 2016/12/29.
 */
public class RunCertificateActivity extends BaseActivity {
    private static final String TAG = "RunCertificateActivity";
    @InjectView(R.id.run_ll_sum)
    LinearLayout runllSum;
    @InjectView(R.id.run_tv_company)
    TextView runTvCompany;
    @InjectView(R.id.run_tv_interaction_m)
    TextView runTvInteractionM;
    @InjectView(R.id.run_tv_heigh_help_m)
    TextView runTvHeighHelpM;
    @InjectView(R.id.run_tv_heigh_share_m)
    TextView runTvHeighShareM;
    @InjectView(R.id.run_iv_red)
    ImageView runivRed;
    @InjectView(R.id.run_iv_yellow)
    ImageView runivYellow;
    @InjectView(R.id.run_iv_green)
    ImageView runivGreen;
    @InjectView(R.id.run_tv_unit)
    TextView runTvUnit;
    @InjectView(R.id.run_tv_unit_name)
    TextView runTvUnitName;
    @InjectView(R.id.run_ll_join_unit)
    LinearLayout runllJoinUnit;
    @InjectView(R.id.run_btn_join_unit)
    Button runBtnJoinUnit;
    @InjectView(R.id.run_tv_unit_member)
    TextView runTvUnitMember;
    @InjectView(R.id.run_rl_unit_member)
    RelativeLayout runrlUnitMember;
    @InjectView(R.id.run_tv_plan_time)
    TextView runTvPlanTime;
    @InjectView(R.id.run_tv_plan_name)
    TextView runTvPlanName;
    @InjectView(R.id.run_tv_plan_company)
    TextView runTvPlanCompany;
    @InjectView(R.id.run_btn_help_situation)
    Button runBtnhelpSituation;
    @InjectView(R.id.run_tv_plan_help_company)
    TextView runTvPlanHelpCompany;
    @InjectView(R.id.run_tv_unit_details)
    TextView runTvunitDetails;
    private String planId;
    private String uid;
    private String unitId;
    private Dialog showDialog;
    private RecyclerView recylerview;
    private RunCertificateIconAdapter adapter;
    private ArrayList<RunCertificateBean.Data.Icon> list;
    private int today;
    private Subscription rxSubscription;
    private boolean isHaveData;


    @Override
    protected void initConfig() {
        super.initConfig();
        planId = getIntent().getStringExtra(Keys.PLANID);
        uid = SpUtils.getStringParam(mActivity, Keys.UID);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_run_certificate;
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        setTopBarDesc("动态凭证");
    }

    @Override
    protected void initViews() {
        recylerview = (RecyclerView) findViewById(R.id.run_recyclerview);
        list = new ArrayList<>();
        adapter = new RunCertificateIconAdapter(this, list);
        recylerview.setAdapter(adapter);//设置适配器
        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 5);
        recylerview.setLayoutManager(layoutManager);
        // 添加分割线
        recylerview.addItemDecoration(new DividerItemDecoration(DisplayUtil.dp2px(mActivity, 11)));

    }

    @Override
    protected void onLoadDatas() {
        // RxBus
        rxBusOnClickListener();
    }

    // 订阅修改信息的事件
    private void rxBusOnClickListener() {
        // rxSubscription是一个Subscription的全局变量，这段代码可以在onCreate/onStart等生命周期内
        rxSubscription = RxBus.getDefault().toObservable(Rxbus_JoinUnit.class)
                .subscribe(new Action1<Rxbus_JoinUnit>() {
                               @Override
                               public void call(Rxbus_JoinUnit joinUnit) {
                                   getData();
                               }
                           },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                // TODO: 处理异常
                            }
                        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 解除订阅
        if (!rxSubscription.isUnsubscribed()) {
            rxSubscription.unsubscribe();
        }
    }

    @Override
    protected void initEvents() {
        super.initEvents();
        // 获取数据
        getData();
    }


    /**
     * 获取数据
     */
    private void getData() {
        showDialog = loadProgressDialog.show(mActivity, "正在上传...");

        String url = Urls.URL_RUN_CERTIFICATE;
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("plan_id", planId);
        params.put("version", versionName);
        LogUtils.eNormal(TAG, ":动态凭证:" + versionName);
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new MyStringCallback());
    }

    Handler handler = new Handler();

    class MyStringCallback extends StringCallback {

        @Override
        public void onError(Request request, Exception e) {

            LogUtils.e(TAG, ":getData", e);
            showDialog.dismiss();
            isHaveData = false;
            AlertDialog builder = new AlertDialog(mActivity).builder();
            builder
                    .setTitle("提示")
                    .setMsg("网络错误")
                    .setPositiveButton("返回", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 500);
                        }
                    })
                    .setCancelable(false).show();
        }

        @Override
        public void onResponse(String response) {
            LogUtils.eNormal(TAG + ":getData", response);
            isHaveData = true;

            showDialog.dismiss();
            setPlanData(response);
        }
    }

    private void setPlanData(String response) {
        RunCertificateBean bean = JsonUtil.json2Bean(response, RunCertificateBean.class);
        if (bean != null) {
            int code = bean.code;
            String msg = bean.msg;
            RunCertificateBean.Data data = bean.data;
            if (code == 0) {
                runllSum.setVisibility(View.VISIBLE);
                //　会员
                String sumMoney = data.sum_money;// 交互金余额
                double newPlanEnsure = data.plan_ensure;// 最高互助额
                double newPlanMoney = data.plan_money;// 最高分摊额
                int twoDaysAgo = data.score0;
                int last = data.score1;
                today = data.score2;
                // 单元
                String unitName = data.unit_name;// 所属单元
                String unitBusinessNum = data.unit_business_num;// 单元会员数
                int existFlag = data.exist_flag;// 是否加入单元
                unitId = data.unit_id;
                // 计划
                String status = data.status;// 计划状态
                String planName = data.plan_name;// 计划名称
                String businessNum = data.business_num;// 参与企业数
                int payedNum = data.payed_num;// 运行情况
                int payNum = data.pay_num;// 可互助企业数
                // 头像
                List<RunCertificateBean.Data.Icon> icons = data.unit_pics;
                list.clear();
                if (icons != null && icons.size() >= 0) {

                    list.addAll(icons);
                }
                adapter.notifyDataSetChanged();

                // 会员

                setShowMember(runTvInteractionM, sumMoney);
                setShowMember(runTvHeighHelpM, newPlanEnsure + "");
                setShowMember(runTvHeighShareM, newPlanMoney + "");

                // 三色进度条
                // 一
                if (twoDaysAgo == 0) {
                    runivRed.setBackgroundResource(R.drawable.run_gray);
                } else if (twoDaysAgo <= 60) {
                    runivRed.setBackgroundResource(R.drawable.red01);
                } else if (twoDaysAgo <= 80) {
                    runivRed.setBackgroundResource(R.drawable.yellow01);
                } else if (twoDaysAgo <= 100) {
                    runivRed.setBackgroundResource(R.drawable.green01);
                }
                // 二
                if (last == 0) {
                    runivYellow.setBackgroundResource(R.drawable.run_gray);
                } else if (last <= 60) {
                    runivYellow.setBackgroundResource(R.drawable.red01);
                } else if (last <= 80) {
                    runivYellow.setBackgroundResource(R.drawable.yellow01);
                } else if (last <= 100) {
                    runivYellow.setBackgroundResource(R.drawable.green01);
                }


                // 单元
                if (existFlag == 1) {
                    // 已加入单元
                    runTvunitDetails.setVisibility(View.VISIBLE);// 查看详情显示
                    runTvUnitName.setVisibility(View.VISIBLE);// 单位名称显示
                    runTvUnitMember.setVisibility(View.VISIBLE);// 已加入数量显示
                    runrlUnitMember.setVisibility(View.VISIBLE);//头像显示
                    runllJoinUnit.setVisibility(View.GONE);// 加入单元隐藏
                    runTvUnitName.setText(unitName);

                    if (TextUtils.equals(unitBusinessNum, 10 + "")) {
//                    runTvUnitMember.setText("满员");
                        runTvUnitMember.setVisibility(View.GONE);// 已加入数量隐藏
                    } else {
                        runTvUnitMember.setText(unitBusinessNum + "/10");
                    }
                } else if (existFlag == 0) {
                    // 尚未加入单元
                    runTvunitDetails.setVisibility(View.GONE);// 查看详情隐藏
                    runTvUnitName.setVisibility(View.GONE);// 单位名称隐藏
                    runrlUnitMember.setVisibility(View.GONE);//头像隐藏
                    runllJoinUnit.setVisibility(View.VISIBLE);// 加入单元显示
                }


                //计划
                runTvPlanTime.setText(status);
                runTvPlanName.setText(planName);
                runTvPlanCompany.setText(businessNum);
//                runBtnhelpSituation.setText(payedNum + "起互助");
                runTvPlanHelpCompany.setText(payNum + "");
            }


        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (isHaveData) {
            // 展示当前旬
            if (today == 0) {
                runivGreen.setBackgroundResource(R.drawable.run_gray);
            } else if (today <= 60) {
                runivGreen.setBackgroundResource(R.drawable.red_animlist);
            } else if (today <= 80) {
                runivGreen.setBackgroundResource(R.drawable.yellow_animlist);
            } else if (today <= 100) {
                runivGreen.setBackgroundResource(R.drawable.green_animlist);
            }

            AnimationDrawable animationDrawable = (AnimationDrawable) runivGreen.getBackground();
            animationDrawable.start();
        }


    }

    /**
     * 设置会员显示内容
     */
    private void setShowMember(TextView textView, String string) {
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
                        textView.setText(string.substring(0, 2) + "万");
                    } else if (substring.endsWith(".0")) {
                        textView.setText(substring.substring(0, 1) + "万");
                    }

                } else {
                    textView.setText(str + "万");

                }
            }
        }
    }


    @OnClick({R.id.run_btn_join_unit, R.id.run_tv_unit_details, R.id.run_btn_help_situation})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.run_btn_join_unit:
                // 加入单元,点击进入单元列表界面
                Intent intent = new Intent();
                intent.setClass(mActivity, CellListActivity.class);
                intent.putExtra(Keys.PLANID, planId);
                startActivity(intent);
                break;
            case R.id.run_tv_unit_details:
                // 单元会员数,点击进入单元详情界面
                Intent intent1 = new Intent();
                intent1.setClass(mActivity, Cell_Detail_Activity.class);
                intent1.putExtra(Keys.UNITID, unitId);
                intent1.putExtra(Keys.PLANID, planId);
                startActivity(intent1);
                break;
            case R.id.run_btn_help_situation:
                // 运行情况
                startActivity(new Intent(mActivity, NoticeActivity.class));
                break;
        }
    }


    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }
}
