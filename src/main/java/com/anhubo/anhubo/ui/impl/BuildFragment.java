package com.anhubo.anhubo.ui.impl;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.BuildAdapter;
import com.anhubo.anhubo.adapter.UnitAdapter;
import com.anhubo.anhubo.base.BaseFragment;
import com.anhubo.anhubo.bean.MyPolygonBean;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.utils.Utils;
import com.anhubo.anhubo.view.MyPolygonView;

import butterknife.ButterKnife;
import butterknife.InjectView;

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

    @Override
    public void initTitleBar() {
        //设置返回键隐藏
        iv_basepager_left.setVisibility(View.GONE);
        //设置铅笔键显示
        ivTopBarleftBuildPen.setVisibility(View.VISIBLE);

        tv_basepager_title.setText("建筑");
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
        // 配置数据
        myPolygonView.setDataModel(getPolygonData());

        // 设置下划线 setUnderline里面的参数是可变参数
        Utils.setUnderline(tvBuildFragMsg, tvBuildFragTest);

        //ListView
        lvBuild = findView(R.id.lv_build);
        // 填充头布局
        View view = View.inflate(mActivity,R.layout.header_build,null);
        RelativeLayout rlBuild01 = (RelativeLayout) view.findViewById(R.id.rl_build_01);
        RelativeLayout rlBuild02 = (RelativeLayout) view.findViewById(R.id.rl_build_02);
        RelativeLayout rlBuild03 = (RelativeLayout) view.findViewById(R.id.rl_build_03);
        lvBuild.addHeaderView(view);
        BuildAdapter adapter = new BuildAdapter(this);
        lvBuild.setAdapter(adapter);

    }

    private MyPolygonBean getPolygonData() {
        MyPolygonBean myPolygonBean = new MyPolygonBean();
        String[] arrText = new String[]{"救援情况", "周边道路", "建筑情况", "三色预警", "设施设备", "毗邻建筑"};
        myPolygonBean.setText(arrText);
        int[] arrArea = new int[]{1, 1, 5, 2, 6, 3};
        myPolygonBean.setArea(arrArea);
        return myPolygonBean;
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

    @Override
    public void initData() {

    }

    @Override
    public void processClick(View v) {
        switch (v.getId()) {
            case R.id.ivTopBarleft_build_pen:
                ToastUtils.showLongToast(mActivity, "编辑");
                break;
            case R.id.tv_build_frag_02_msg:
                ToastUtils.showLongToast(mActivity, "完善基础信息");
                break;
            case R.id.tv_build_frag_02_test:
                ToastUtils.showLongToast(mActivity, "测试");
                break;
            case R.id.rl_build_01:
                rlBuild01.setVisibility(View.GONE);
                rlBuild02.setVisibility(View.VISIBLE);
                tvBuildFragMsg.setVisibility(View.VISIBLE);
                tvBuildFragTest.setVisibility(View.VISIBLE);
                break;
            case R.id.rl_build_02:
                rlBuild02.setVisibility(View.GONE);
                rlBuild01.setVisibility(View.VISIBLE);
                tvBuildFragMsg.setVisibility(View.GONE);
                tvBuildFragTest.setVisibility(View.GONE);
                break;
            default:
                break;

        }
    }
}

