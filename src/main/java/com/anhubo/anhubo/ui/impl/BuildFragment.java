package com.anhubo.anhubo.ui.impl;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.BuildAdapter;
import com.anhubo.anhubo.base.BaseFragment;
import com.anhubo.anhubo.bean.BuildScoreBean;
import com.anhubo.anhubo.bean.BuildThreeBean;
import com.anhubo.anhubo.bean.Build_Help_Plan_Bean;
import com.anhubo.anhubo.bean.MyPolygonBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.ui.activity.buildDetial.Build_CltMsgActivity;
import com.anhubo.anhubo.ui.activity.unitDetial.FeedbackActivity;
import com.anhubo.anhubo.utils.DisplayUtil;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.utils.Utils;
import com.anhubo.anhubo.view.AlertDialog;
import com.anhubo.anhubo.view.MyPolygonView;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/10/8.
 */
public class BuildFragment extends BaseFragment {


    private ListView lvBuild;
    private RelativeLayout rlBuild01;
    private RelativeLayout rlBuild02;
    private TextView tvBuildFragMsg;
    private TextView tvBuildFragTest;
    private MyPolygonView myPolygonView;
    private String bulidingid;
    private TextView tvBuildScore;
    private TextView tvBuildTime;
    private TextView red;
    private TextView green;
    private TextView yellow;
    private String score1;
    private String score2;
    private String score3;
    private String score4;
    private String score5;
    private String score6;
    private String sumScore;
    private String datatime;
    private int redNumber;
    private int greenNumber;
    private int yellowNumber;
    private ArrayList<String> list;
    private int[] arrScores;
    private TextView tvBuildLowNum;
    private TextView tvBuildMiddleNum;
    private TextView tvBuildHeighNum;
    private TextView bottom;
    private BuildAdapter adapter;

    @Override
    public void initTitleBar() {
        //设置返回键隐藏
        iv_basepager_left.setVisibility(View.GONE);
        //设置铅笔键显示
        ivTopBarleftBuildPen.setVisibility(View.VISIBLE);
        String buildingName = SpUtils.getStringParam(mActivity, Keys.BUILDINGNAME);
        tv_basepager_title.setText(buildingName);
        // 设置title的背景颜色
        llTop.setBackgroundResource(R.color.unit_top);

    }

    @Override
    public Object getContentView() {
        return R.layout.fragment_build;
    }

    @Override
    public void initView() {
        // 统计图的外围布局
        rlBuild01 = findView(R.id.rl_build_01);
        rlBuild02 = findView(R.id.rl_build_02);

        // 统计图下面的按钮tv_build_frag_02_msg
        tvBuildFragMsg = findView(R.id.tv_build_frag_02_msg);
        tvBuildFragTest = findView(R.id.tv_build_frag_02_test);

        // 多边形
        myPolygonView = findView(R.id.polygon_build);

        // 设置下划线 setUnderline里面的参数是可变参数
        Utils.setUnderline(tvBuildFragMsg, tvBuildFragTest);

        // 建筑安全指数
        tvBuildScore = findView(R.id.tv_build_score);
        tvBuildTime = findView(R.id.tv_build_time);

        // 设置时间显示的粗体
        TextPaint paint = tvBuildTime.getPaint();
        paint.setFakeBoldText(true);

        //ListView
        lvBuild = findView(R.id.lv_build);
        // 填充头布局
        View view = View.inflate(mActivity, R.layout.header_build, null);
        // 颜色对应的值
        tvBuildLowNum = (TextView) view.findViewById(R.id.tv_build_lowNum);
        tvBuildMiddleNum = (TextView) view.findViewById(R.id.tv_build_middleNum);
        tvBuildHeighNum = (TextView) view.findViewById(R.id.tv_build_heighNum);
        //三个颜色
        red = (TextView) view.findViewById(R.id.red);
        green = (TextView) view.findViewById(R.id.green);
        yellow = (TextView) view.findViewById(R.id.yellow);
        // 底色
        bottom = (TextView) view.findViewById(R.id.bottom);

        //ListView添加脚布局
        lvBuild.addHeaderView(view, null, true);
        // 去掉头布局的分割线
        lvBuild.setHeaderDividersEnabled(false);


    }


    @Override
    public void initData() {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            bulidingid = SpUtils.getStringParam(mActivity, Keys.BULIDINGID);
            // 获取建筑安全指数
            getBuildData();
            // 获取三色预警比例
            getThreeWarning();
            // 获取户主计划列表信息
            getHelpPlan();
        }
    }


    /**
     * 设置监听
     */
    @Override
    public void initListener() {
        // 统计图
        rlBuild01.setOnClickListener(this);
        rlBuild02.setOnClickListener(this);
        // 统计图下面的TextView
        tvBuildFragMsg.setOnClickListener(this);
        tvBuildFragTest.setOnClickListener(this);
    }

    /**
     * ****************************************************
     * 获取互助计划列表信息
     */
    private void getHelpPlan() {
        HashMap<String, String> params = new HashMap<>();
        params.put("building_id", bulidingid);
        String url = Urls.Url_Build_Help_Plan;

        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback2());
    }

    /**
     * 互助计划
     */
    class MyStringCallback2 extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {
            System.out.println("BuildFragment互助计划+++===获取数据失败" + e.getMessage());

        }

        @Override
        public void onResponse(String response) {
            //System.out.println("BuildFragment互助计划+++===" + response);
            Build_Help_Plan_Bean bean = new Gson().fromJson(response, Build_Help_Plan_Bean.class);
            if (bean != null) {
                int code = bean.code;
                String msg = bean.msg;
                Build_Help_Plan_Bean.Data data = bean.data;
                List<Build_Help_Plan_Bean.Data.Plans> plans = data.plans;
                if (code == 0 && !plans.isEmpty()) {
                    adapter = new BuildAdapter(mActivity, plans);
                } else {
                    adapter = new BuildAdapter(mActivity);
                }
                lvBuild.setAdapter(adapter);
            }

        }
    }

    /**
     * ****************************************************
     * 获取三色预警比例
     */
    private void getThreeWarning() {
        HashMap<String, String> params = new HashMap<>();
        params.put("building_id", bulidingid);
        String url = Urls.Url_Build_ThreeColour;

        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback1());
    }


    /**
     * 三色预警
     */
    class MyStringCallback1 extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {
            System.out.println("BuildFragment三色预警+++===获取数据失败" + e.getMessage());

        }

        @Override
        public void onResponse(String response) {

            //System.out.println("BuildFragment三色预警+++===" + response);
            if (!TextUtils.isEmpty(response)) {
                BuildThreeBean threeBean = new Gson().fromJson(response, BuildThreeBean.class);
                //获取数据
                redNumber = threeBean.data.red;
                yellowNumber = threeBean.data.yellow;
                greenNumber = threeBean.data.green;
                // 设置分数显示
                tvBuildLowNum.setText(redNumber + "");
                tvBuildMiddleNum.setText(yellowNumber + "");
                tvBuildHeighNum.setText(greenNumber + "");
                //设置权重
                bottom.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0));
                red.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, redNumber));
                yellow.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, yellowNumber));
                green.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, greenNumber));
                //设置圆角
                setCircularRadius();

            }

        }
    }


    /**
     * ****************************************************************
     * 获取建筑安全指数
     */
    private void getBuildData() {

        Map<String, String> params = new HashMap<>();
        params.put("building_id", bulidingid);

        String url = Urls.Url_Build_score;
        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback());

    }

    /**
     * 建筑安全指数
     */
    class MyStringCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {

            System.out.println("BuildFragment+++建筑安全指数===获取到数据失败" + e.getMessage());
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("网络有问题，请检查")
                    .setCancelable(true).show();
        }

        @Override
        public void onResponse(String response) {
            //            System.out.println("BuildFragment建筑安全指数+++===" + response);
            list = new ArrayList<>();// 创建一个数组
            arrScores = new int[6];

            if (!TextUtils.isEmpty(response)) {
                BuildScoreBean scoreBean = new Gson().fromJson(response, BuildScoreBean.class);

                score1 = scoreBean.data.sub_score1;
                score2 = scoreBean.data.sub_score2;
                score3 = scoreBean.data.sub_score3;
                score4 = scoreBean.data.sub_score4;
                score5 = scoreBean.data.sub_score5;
                score6 = scoreBean.data.sub_score6;
                sumScore = scoreBean.data.sum_score;
                datatime = scoreBean.data.datatime;
                // 设置显示评估分数
                tvBuildScore.setText(sumScore);
                // 设置显示评估时间
                tvBuildTime.setText("评估时间 " + datatime);
                list.add(0, score4);
                list.add(1, score5);
                list.add(2, score6);
                list.add(3, score1);
                list.add(4, score2);
                list.add(5, score3);

                // 遍历集合，把元素添加到数组里面
                for (int i = 0; i < list.size(); i++) {
                    arrScores[i] = Integer.parseInt(list.get(i)) * 6 / 100;
                }
                if (arrScores.length == 6) {
                    myPolygonView.setDataModel(getPolygonData());
                }

            }

        }
    }


    @Override
    public void processClick(View v) {
        switch (v.getId()) {
            case R.id.ivTopBarleft_build_pen:// 编辑
                startActivity(new Intent(mActivity, FeedbackActivity.class));
                break;
            case R.id.tv_build_frag_02_msg:// 完善基础信息
                startActivity(new Intent(mActivity, Build_CltMsgActivity.class));
                break;
            case R.id.tv_build_frag_02_test:// 测试
                ToastUtils.showLongToast(mActivity, "测试");
                break;
            case R.id.rl_build_01:
                rlBuild01.setVisibility(View.GONE);
                rlBuild02.setVisibility(View.VISIBLE);
                tvBuildFragMsg.setVisibility(View.VISIBLE);
                // tvBuildFragTest.setVisibility(View.VISIBLE);
                break;
            case R.id.rl_build_02:
                rlBuild01.setVisibility(View.VISIBLE);
                rlBuild02.setVisibility(View.GONE);
                tvBuildFragMsg.setVisibility(View.GONE);
                tvBuildFragTest.setVisibility(View.GONE);
                break;
            default:
                break;

        }
    }


    /**
     * 获取多边形的实体类数据
     */
    private MyPolygonBean getPolygonData() {
        MyPolygonBean myPolygonBean = new MyPolygonBean();
        String[] arrText = new String[]{"救援情况", "周边道路", "建筑情况", "三色预警", "设施设备", "毗邻建筑"};
        myPolygonBean.setText(arrText);
        myPolygonBean.setArea(arrScores);
        return myPolygonBean;
    }

    /**
     * 设置圆角
     */
    private void setCircularRadius() {
        Resources resources = DisplayUtil.getResources();
        // 设置红色背景圆角
        if (redNumber == 100) {
            Drawable drawable = resources.getDrawable(R.drawable.tv_shap_red);
            red.setBackgroundDrawable(drawable);
        } else if (redNumber == 0) {
            // 红色为0
            if (yellowNumber == 0) {
                // 黄色为0
                Drawable drawable = resources.getDrawable(R.drawable.tv_shap_green);
                green.setBackgroundDrawable(drawable);
            } else {

                Drawable drawable = resources.getDrawable(R.drawable.tv_shap_yellow1);
                yellow.setBackgroundDrawable(drawable);
                if (greenNumber != 0) {
                    Drawable drawable2 = resources.getDrawable(R.drawable.tv_shap_green2);
                    green.setBackgroundDrawable(drawable2);
                } else {

                    Drawable drawable1 = resources.getDrawable(R.drawable.tv_shap_yellow);
                    yellow.setBackgroundDrawable(drawable1);
                }
            }

        } else {
            // 红色不为0
            Drawable drawable = resources.getDrawable(R.drawable.tv_shap_red1);
            red.setBackgroundDrawable(drawable);

            if (yellowNumber == 0) {
                Drawable drawable1 = resources.getDrawable(R.drawable.tv_shap_green2);
                green.setBackgroundDrawable(drawable1);
            } else {
                // 黄色也不为0
                if (greenNumber != 0) {
                    // 绿色也不为0
                    // 此时设置黄色没有圆角
                    Drawable drawabley = resources.getDrawable(R.color.dot_yellow);
                    yellow.setBackgroundDrawable(drawabley);
                    //右边圆角为绿色
                    Drawable drawable1 = resources.getDrawable(R.drawable.tv_shap_green2);
                    green.setBackgroundDrawable(drawable1);
                } else {
                    // 绿色为0
                    Drawable drawable2 = resources.getDrawable(R.drawable.tv_shap_yellow2);
                    yellow.setBackgroundDrawable(drawable2);
                }

            }
        }
    }
}

