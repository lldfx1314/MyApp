package com.anhubo.anhubo.ui.impl;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.UnitAdapter;
import com.anhubo.anhubo.base.BaseFragment;
import com.anhubo.anhubo.bean.MyPolygonBean;
import com.anhubo.anhubo.bean.SesameItemModel;
import com.anhubo.anhubo.bean.SesameModel;
import com.anhubo.anhubo.bean.UnitBean;
import com.anhubo.anhubo.bean.Unit_PlanBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.ui.activity.HomeActivity;
import com.anhubo.anhubo.ui.activity.unitDetial.AreaBindingActivity;
import com.anhubo.anhubo.ui.activity.unitDetial.BindQrcodeActivity;
import com.anhubo.anhubo.ui.activity.unitDetial.EmployeeListActivity;
import com.anhubo.anhubo.ui.activity.unitDetial.EvacuateActivity;
import com.anhubo.anhubo.ui.activity.unitDetial.MsgPerfectActivity;
import com.anhubo.anhubo.ui.activity.unitDetial.QrScanActivity;
import com.anhubo.anhubo.ui.activity.unitDetial.RunCertificateActivity;
import com.anhubo.anhubo.ui.activity.unitDetial.Unit2Study;
import com.anhubo.anhubo.ui.activity.unitDetial.UnitMsgCenterActivity;
import com.anhubo.anhubo.ui.activity.unitDetial.Unit_StudyRecordActivity;
import com.anhubo.anhubo.utils.JsonUtil;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.Utils;
import com.anhubo.anhubo.view.AlertDialog;
import com.anhubo.anhubo.view.MyPolygonView;
import com.anhubo.anhubo.view.SesameCreditPanel;
import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/10/8.
 */
public class UnitFragment extends BaseFragment {
    private static final int STUDY = 1;
    private static final int CHECK = 2;
    private static final int DRILL = 3;
    private static final String TAG = "UnitFragment";
    private ListView lvUnit;
    private RelativeLayout rlUnit01;
    private RelativeLayout rlUnit02;
    private RelativeLayout rlUnit;
    private TextView tvUnitFrag;
    private TextView tvUnitFragMsg;
    private TextView tvUnitFragInvite;
    private TextView tvUnitFragAdd;
    private Button btnStudy;
    private Button btnCheck;
    private Button btnDrill;
    private View dotStudy;
    private View dotCheck;
    //private View dotDrill;
    private FrameLayout sesameCreditPanelLL;
    private MyPolygonView myPolygonView;
    private String datatime;
    private String grade;
    private int sumScore;
    private String subScore1;
    private String subScore2;
    private String subScore3;
    private String subScore4;
    private String subScore5;
    private String subScore6;
    private ArrayList<String> list;
    private int[] arrScores;
    private UnitBean.Data data;
    private SesameCreditPanel scp;
    private TextView tvNoPlan1;
    private TextView tvNoPlan2;
    private int code;
    private List<Unit_PlanBean.Data.Certs> certs;
    private String uid;
    private UnitAdapter adapter;
    private boolean isShowDot_study = false;
    private boolean isShowDot_check = false;
    private boolean isShowDot_drill = false;
    private static int dialog_i = 0;

    private AlertDialog builder;

    /**
     * 接收消息让小圆点隐藏
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case STUDY:
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dotStudy.setVisibility(View.GONE);
                            String oldTime = getSystemTime();
                            SpUtils.putParam(mActivity, Keys.STUDY_TIME, oldTime);
                        }
                    }, 300);

                    break;
                case CHECK:
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dotCheck.setVisibility(View.GONE);
                            String oldTime = getSystemTime();
                            SpUtils.putParam(mActivity, Keys.CHECK_TIME, oldTime);
                        }
                    }, 300);
                    break;
                case DRILL:
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            dotDrill.setVisibility(View.GONE);
//                            String oldTime = getSystemTime();
//                            SpUtils.putParam(mActivity, Keys.DRILL_TIME, oldTime);
                        }
                    }, 300);
                    break;
            }
        }
    };

    private boolean isLoading = false;// 页面已经加载过
    private String businessId;
    private String businessName;

    @Override
    public void initData() {
        // 进入界面的网络请求
        getDataInternet();
        //　ListView的条目点击事件
        lvUnit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<Unit_PlanBean.Data.Certs> certs = UnitFragment.this.certs;
                Unit_PlanBean.Data.Certs cert = certs.get(position - 1);
                String planId = cert.plan_id;
                Intent intent = new Intent();
                intent.setClass(mActivity, RunCertificateActivity.class);
                intent.putExtra(Keys.PLANID, planId);
                startActivity(intent);
            }
        });

    }

    @Override
    public void initTitleBar() {
        //设置返回键隐藏
        iv_basepager_left.setVisibility(View.GONE);
        //设置菜单键和信息键显示
        ivTopBarleftUnitMenu.setVisibility(View.VISIBLE);
        ivTopBarRightUnitMsg.setVisibility(View.VISIBLE);
        String businessName = SpUtils.getStringParam(mActivity, Keys.BUSINESSNAME);
        tv_basepager_title.setText(businessName);
        // 设置title的背景颜色
        llTop.setBackgroundResource(R.color.unit_top);
    }

    @Override
    public Object getContentView() {
        return R.layout.fragment_unit;
    }

    @Override
    public void initView() {
        builder = new AlertDialog(mActivity).builder();

        // 统计图
        rlUnit01 = findView(R.id.rl_unit_01);
        rlUnit02 = findView(R.id.rl_unit_02);
        rlUnit = findView(R.id.rl_unit);
        // 统计图下面的按钮
        tvUnitFrag = findView(R.id.tv_unit_frag_01);
        tvUnitFragMsg = findView(R.id.tv_unit_frag_02_msg);
        tvUnitFragInvite = findView(R.id.tv_unit_frag_02_invite);
        tvUnitFragAdd = findView(R.id.tv_unit_frag_02_add);

        // 圆形进度条
        sesameCreditPanelLL = findView(R.id.panel);
        // 多边形
        myPolygonView = findView(R.id.polygon);

        // 设置下划线 setUnderline里面的参数是可变参数
        Utils.setUnderline(tvUnitFrag, tvUnitFragMsg, tvUnitFragInvite, tvUnitFragAdd);
        // listView
        lvUnit = findView(R.id.lv_unit);
        // 头布局
        View view = View.inflate(mActivity, R.layout.header_unit, null);

        // 安全提升级别的button
        btnStudy = (Button) view.findViewById(R.id.btn_study);
        btnCheck = (Button) view.findViewById(R.id.btn_check);
        btnDrill = (Button) view.findViewById(R.id.btn_drill);
        // 红色的小圆点
        dotStudy = view.findViewById(R.id.dot_study);
        dotCheck = view.findViewById(R.id.dot_check);
        //dotDrill = view.findViewById(R.id.dot_drill);
        // 提示没有任何保障计划
        tvNoPlan1 = (TextView) view.findViewById(R.id.tv_no_plan1);
        tvNoPlan2 = (TextView) view.findViewById(R.id.tv_no_plan2);


        // 添加头布局
        lvUnit.addHeaderView(view, null, true);
        // 去掉头布局的分割线
        lvUnit.setHeaderDividersEnabled(false);

    }

    /**
     * 设置监听
     */
    @Override
    public void initListener() {
        // 统计图
        rlUnit01.setOnClickListener(this);
        rlUnit02.setOnClickListener(this);
        // 统计图下面的TextView
        tvUnitFragMsg.setOnClickListener(this);
        tvUnitFragInvite.setOnClickListener(this);
        tvUnitFragAdd.setOnClickListener(this);
        // 安全提升级别的button
        btnStudy.setOnClickListener(this);
        btnCheck.setOnClickListener(this);
        btnDrill.setOnClickListener(this);
        // 监听，从遮罩层回来后，重新加载数据
        HomeActivity activity = (HomeActivity) getActivity();
        activity.setUpdateFragmentUIFromActivity(new UIChangeListener());
    }

    @Override
    public void onResume() {
        super.onResume();
        //界面一加载设置小红点显示
        setDotVisible();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            // 重新刷一遍界面
            // 界面每次可见都设置一下单位，邀请同事以后单位可能会改变
            businessName = SpUtils.getStringParam(getActivity(), Keys.BUSINESSNAME, null);

            if (tv_basepager_title != null && !TextUtils.isEmpty(businessName)) {

                tv_basepager_title.setText(businessName);
            }

            isLoading = true;
            businessId = SpUtils.getStringParam(mActivity, Keys.BUSINESSID);
            getData(businessId);
            getPlanData();
        }
    }

    /**
     * 设置小圆点显示
     */
    private void setDotVisible() {
        String newTime = getSystemTime();
        String studyTime = SpUtils.getStringParam(mActivity, Keys.STUDY_TIME);
        String checkTime = SpUtils.getStringParam(mActivity, Keys.CHECK_TIME);
        //String drillTime = SpUtils.getStringParam(mActivity, Keys.DRILL_TIME);
        if (!TextUtils.isEmpty(newTime)) {
            Date newDate = getDate(newTime);
            // 学习
            if (!TextUtils.isEmpty(studyTime)) {
                Date studyDate = getDate(studyTime);
                if (newDate != null && studyDate != null) {
                    long time = (newDate.getTime() - studyDate.getTime()) / 1000 / 60 / 60 / 24;
                    if (time >= 10) {
                        isShowDot_study = true;
                    }
                }
            } else {
                isShowDot_study = true;
            }
            // 检查
            if (!TextUtils.isEmpty(checkTime)) {
                Date checkDate = getDate(checkTime);
                if (newDate != null && checkDate != null) {
                    long time = (newDate.getTime() - checkDate.getTime()) / 1000 / 60 / 60 / 24;
                    if (time >= 10) {
                        isShowDot_check = true;
                    }
                }
            } else {
                isShowDot_check = true;
            }
            // 演练
//            if (!TextUtils.isEmpty(drillTime)) {
//                Date drillDate = getDate(drillTime);
//                if (newDate != null && drillDate != null) {
//                    long time = (newDate.getTime() - drillDate.getTime()) / 1000 /*/ 60 / 60 / 24*/;
//                    if (time >= 10) {
//                        isShowDot_drill = true;
//                    }
//                }
//            }else{
//                isShowDot_drill = true;
//            }
        }

        if (isShowDot_study) {
            dotStudy.setVisibility(View.VISIBLE);
        }
        if (isShowDot_check) {
            dotCheck.setVisibility(View.VISIBLE);
        }
//        if (isShowDot_drill) {
//            dotDrill.setVisibility(View.VISIBLE);
//        }
    }

    // 界面更新的监听
    private class UIChangeListener implements HomeActivity.UpdateFragmentUIFromActivity {

        @Override
        public void UIChange() {
            isLoading = true;
            businessName = SpUtils.getStringParam(getActivity(), Keys.BUSINESSNAME, null);
            businessId = SpUtils.getStringParam(mActivity, Keys.BUSINESSID);
            tv_basepager_title.setText(businessName);
            getData(businessId);
            getPlanData();
        }

        @Override
        public void changeUnit(String string, String str) {
            //　接受同事邀请后刷新单位按钮的显示
//                LogUtils.eNormal(TAG,":界面修改单位+"+string);
            tv_basepager_title.setText(string);
            // 重新刷一遍界面
            isLoading = true;
            getData(str);
            getPlanData();
        }
    }

    private void getDataInternet() {
        isLoading = true;
        // 界面每次可见都设置一下单位，邀请同事以后单位可能会改变
        businessName = SpUtils.getStringParam(getActivity(), Keys.BUSINESSNAME, null);

        if (tv_basepager_title != null && !TextUtils.isEmpty(businessName)) {

            tv_basepager_title.setText(businessName);
        }
        businessId = SpUtils.getStringParam(mActivity, Keys.BUSINESSID);
        if (TextUtils.isEmpty(businessId)) {
            // 当businessId为空时，弧形控件显示默认数据，等待HomeActivity显示遮罩层，去跳转界面选择单位
            if (isLoading) {
                // 动态的添加自定义控件，设置数据
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SimpleDateFormat df = new SimpleDateFormat("MM-dd");//设置日期格式
                        String time = df.format(new Date());
                        if (sesameCreditPanelLL != null) {
                            sesameCreditPanelLL.removeView(scp);
                        }
                        // 初始化加载弧型进度条
                        scp = new SesameCreditPanel(getActivity());
                        scp.setDataModel(getData(31, "低", time));
                        scp.setEnabled(true);
                        scp.setClickable(true);
                        sesameCreditPanelLL.addView(scp);
                        isLoading = false;
                    }
                }, 500);
            }
        } else {
            // 获取圆弧数据
            getData(businessId);
        }
        uid = SpUtils.getStringParam(getActivity(), Keys.UID);
        //动态凭证获取数据
        getPlanData();
    }

    /**
     * 动态凭证获取数据
     */
    private void getPlanData() {
        final Dialog dialog = loadProgressDialog.show(mActivity, "正在加载...");
        //　互保计划　请求网络

        String url = Urls.URL_UNIT_RUN_CERTIFICATE;
        HashMap<String, String> params = new HashMap<>();

        params.put("uid", uid);

        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        dialog.dismiss();
                        LogUtils.e(TAG, ":getPlanData", e);
                        tvNoPlan1.setVisibility(View.VISIBLE);
                        String response = SpUtils.getStringParam(mActivity, "PlanData");
                        setPlanData(response);

                    }

                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        LogUtils.eNormal(TAG + ":getPlanData", response);
                        SpUtils.putParam(mActivity, "PlanData", response);
                        //  互保计划
                        setPlanData(response);

                    }
                });
    }

    /**
     * 互保计划
     */
    private void setPlanData(String response) {
        certs = new ArrayList<>();

        Unit_PlanBean bean = JsonUtil.json2Bean(response, Unit_PlanBean.class);
        if (bean != null) {
            code = bean.code;
            String msg = bean.msg;
            certs = bean.data.certs;
        }
        // 没有任何保障时显示提示信息，并且不显示ListView的分割线
        if (code == 0 && certs != null) {

            if (certs.size() == 0) {
                tvNoPlan2.setVisibility(View.GONE);
                tvNoPlan1.setVisibility(View.VISIBLE);
                lvUnit.setDividerHeight(0);
            } else {
                tvNoPlan1.setVisibility(View.GONE);
                tvNoPlan2.setVisibility(View.VISIBLE);
                tvNoPlan2.setText("动态互保凭证");
            }

        } else {
            tvNoPlan2.setVisibility(View.GONE);
            tvNoPlan1.setVisibility(View.VISIBLE);
            lvUnit.setDividerHeight(0);
        }
        adapter = new UnitAdapter(mActivity, certs);
        lvUnit.setAdapter(adapter);

    }

    /**
     * 获取圆弧数据
     */
    public void getData(String businessId) {
        // 每次请求网络之前（控件是在网络获取成功后动态添加上去的）先把上次的控件对象移除，否则会重复
        if (sesameCreditPanelLL != null) {
            sesameCreditPanelLL.removeView(scp);
        }

        // 创建一个集合，用于记录多边形的数据
        list = new ArrayList<>();
        // 创建一个数组,用于存储多边形的数据
        arrScores = new int[6];

        /**只有当该Fragment被用户可见的时候,才加载网络数据*/
        // 获取网络数据
        final Dialog dialog = loadProgressDialog.show(mActivity, "正在加载...");
        String url = Urls.Url_Unit;
        HashMap<String, String> params = new HashMap<>();
        params.put("business_id", businessId);
        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        dialog.dismiss();
                        LogUtils.e(TAG, "+:getData", e);
                        // 获取数据时报后显示多边形（主要显示各维度问题，分数默认显示0）
                        if (arrScores.length == 6 && myPolygonView != null) {
                            myPolygonView.setDataModel(getPolygonData());
                        }
                        // 网络不好，加载缓存数据
                        String response = SpUtils.getStringParam(mActivity, "getdata");
                        setData(response);
                        if (dialog_i == 0) {
                            showdialog("您的网络异常，请检查");
                            dialog_i++;
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        LogUtils.eNormal(TAG, ":getData" + response);
                        // 保存一下数据，以方便在网络不好的时候进行显示
                        SpUtils.putParam(mActivity, "getdata", response);
                        setData(response);
                    }
                });
    }

    /**
     * 拿到圆弧数据数据后显示
     */
    private void setData(String string) {
        UnitBean bean = JsonUtil.json2Bean(string, UnitBean.class);
        list.clear();
        if (bean != null) {
            data = bean.data;
            // 获取到数据
            datatime = data.datatime;
            grade = data.grade;
            sumScore = Integer.parseInt(data.sum_score.replace(".00", ""));
            if (isLoading) {
                // 动态的添加自定义控件，设置数据
                scp = new SesameCreditPanel(mActivity);
                scp.setDataModel(getData(sumScore, grade, datatime));
                scp.setEnabled(true);
                scp.setClickable(true);
                sesameCreditPanelLL.addView(scp);
                isLoading = false;
            }

            // 获取多边形数据
            subScore1 = data.sub_score1;
            subScore2 = data.sub_score2;
            subScore3 = data.sub_score3;
            subScore4 = data.sub_score4;
            subScore5 = data.sub_score5;
            subScore6 = data.sub_score6;
            list.add(0, subScore4);
            list.add(1, subScore5);
            list.add(2, subScore6);
            list.add(3, subScore1);
            list.add(4, subScore2);
            list.add(5, subScore3);
            // 遍历集合，把元素添加到数组里面
            for (int i = 0; i < list.size(); i++) {
                arrScores[i] = Integer.parseInt(list.get(i)) * 6 / 100;
            }
            if (arrScores.length == 6) {
                myPolygonView.setDataModel(getPolygonData());
            }
        }
    }

    /**
     * 提示用户的弹窗
     */
    private void showdialog(String string) {
        builder
                .setTitle("提示")
                .setMsg(string)
                .setCancelable(true).show();
    }

    /**
     * 获取系统当前时间
     */
    private String getSystemTime() {
        Date date = new Date(System.currentTimeMillis());//获取当前时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
        String str = formatter.format(date);
        return str;
    }

    /**
     * 获取Date格式的时间
     */
    private Date getDate(String time) {
        //2,定义日期格式化对象
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
        //3,将日期字符串转换成日期对象
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


    @Override
    public void processClick(View v) {

        switch (v.getId()) {

            case R.id.ivTopBarleft_unit_menu:// 执行记录
                startActivity(new Intent(mActivity, Unit_StudyRecordActivity.class));
                break;

            case R.id.ivTopBarRight_unit_msg://消息中心

                startActivity(new Intent(mActivity, UnitMsgCenterActivity.class));
                break;

            case R.id.tv_unit_frag_02_msg: //信息完善
                startActivity(new Intent(mActivity, MsgPerfectActivity.class));
                break;
            case R.id.tv_unit_frag_02_invite://员工列表
                startActivity(new Intent(mActivity, EmployeeListActivity.class));
                break;
            case R.id.tv_unit_frag_02_add:  //新增设备  改成绑定二维码了
                Intent intent = new Intent(mActivity, BindQrcodeActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_study:// 学习
                /****************************************************/
                isShowDot_study = false;
                if (!isShowDot_study) {
                    handler.sendEmptyMessage(STUDY);
                }
                startActivity(new Intent(mActivity, Unit2Study.class));
                break;
            case R.id.btn_check://检查
                /****************************************************/
                isShowDot_check = false;
                if (!isShowDot_check) {
                    handler.sendEmptyMessage(CHECK);
                }
                Intent intentCheck = new Intent(mActivity, QrScanActivity.class);
                intentCheck.putExtra(Keys.CHECK, "Check");
                startActivity(intentCheck);
                break;
            case R.id.btn_drill: // 疏散
                /****************************************************/
                isShowDot_drill = false;
                if (!isShowDot_drill) {
                    handler.sendEmptyMessage(DRILL);
                }
//                Intent intentExercise = new Intent(mActivity, QrScanActivity.class);
//                intentExercise.putExtra(Keys.EXERCISE, "Exercise");
//                startActivity(intentExercise);
                Intent intentAreaBinding = new Intent(mActivity, EvacuateActivity.class);
                startActivity(intentAreaBinding);
//                new AlertDialog(mActivity).builder()
//                        .setTitle("提示")
//                        .setMsg("此功能暂未开放")
//                        .setCancelable(false).show();
                break;
            case R.id.rl_unit_01:
                rlUnit.setVisibility(View.GONE);
                rlUnit02.setVisibility(View.VISIBLE);
//                tvUnitFragMsg.setVisibility(View.VISIBLE);
//                tvUnitFragInvite.setVisibility(View.VISIBLE);
//                tvUnitFragAdd.setVisibility(View.VISIBLE);
                break;
            case R.id.rl_unit_02:
                rlUnit.setVisibility(View.VISIBLE);
                rlUnit02.setVisibility(View.GONE);
//                tvUnitFragMsg.setVisibility(View.GONE);
//                tvUnitFragInvite.setVisibility(View.GONE);
//                tvUnitFragAdd.setVisibility(View.GONE);
                break;
            default:
                break;
        }

    }


    /**
     * 获取多边形的数据
     */
    private MyPolygonBean getPolygonData() {
        MyPolygonBean myPolygonBean = new MyPolygonBean();
        String[] arrText = new String[]{"参与人数", "行政审批", "设施设备", "知识水平", "管理行为", "疏散逃生"};
        myPolygonBean.setText(arrText);
        myPolygonBean.setArea(arrScores);
        return myPolygonBean;
    }

    /**
     * 获取圆形统计图的数据
     */
    private SesameModel getData(int userTotal, String level, String time) {

        SesameModel model = new SesameModel();
        model.setUserTotal(userTotal);
        model.setFirstText("安全级别 " + level);
        model.setFourText("评估时间:" + time);


        /**上面这些。。。。。。。。。。。。。。。。。。。。。。。*/


        // 区间最小值
        model.setTotalMin(0);
        // 区间最大值
        model.setTotalMax(100);
        ArrayList<SesameItemModel> sesameItemModels = new ArrayList<SesameItemModel>();
        // 添加级别
        SesameItemModel ItemModel0 = new SesameItemModel();
        //ItemModel0.setArea("较差");
        ItemModel0.setMin(0);
        ItemModel0.setMax(20);
        sesameItemModels.add(ItemModel0);

        SesameItemModel ItemModel60 = new SesameItemModel();
        //ItemModel60.setArea("中等");
        ItemModel60.setMin(20);
        ItemModel60.setMax(40);
        sesameItemModels.add(ItemModel60);

        SesameItemModel ItemModel70 = new SesameItemModel();
        //ItemModel70.setArea("良好");
        ItemModel70.setMin(40);
        ItemModel70.setMax(60);
        sesameItemModels.add(ItemModel70);

        SesameItemModel ItemModel80 = new SesameItemModel();
        //ItemModel80.setArea("较好");
        ItemModel80.setMin(60);
        ItemModel80.setMax(80);
        sesameItemModels.add(ItemModel80);

        SesameItemModel ItemModel90 = new SesameItemModel();
        //ItemModel90.setArea("优秀");
        ItemModel90.setMin(80);
        ItemModel90.setMax(100);
        sesameItemModels.add(ItemModel90);

        model.setSesameItemModels(sesameItemModels);
        return model;
    }

}
