package com.anhubo.anhubo.ui.activity.unitDetial;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by LUOLI on 2016/12/29.
 */
public class RunCertificateActivity extends BaseActivity {
    @InjectView(R.id.run_tv_company)
    TextView runTvCompany;
    @InjectView(R.id.run_tv_interaction_m)
    TextView runTvInteractionM;
    @InjectView(R.id.run_tv_heigh_help_m)
    TextView runTvHeighHelpM;
    @InjectView(R.id.run_tv_heigh_share_m)
    TextView runTvHeighShareM;
    @InjectView(R.id.run_tv_red)
    TextView runTvRed;
    @InjectView(R.id.run_tv_yellow)
    TextView runTvYellow;
    @InjectView(R.id.run_tv_green)
    TextView runTvGreen;
    @InjectView(R.id.run_tv_unit)
    TextView runTvUnit;
    @InjectView(R.id.run_tv_unit_name)
    TextView runTvUnitName;
    @InjectView(R.id.run_btn_join_unit)
    Button runBtnJoinUnit;
    @InjectView(R.id.run_tv_unit_member)
    TextView runTvUnitMember;
    @InjectView(R.id.run_tv_plan_time)
    TextView runTvPlanTime;
    @InjectView(R.id.run_tv_plan_name)
    TextView runTvPlanName;
    @InjectView(R.id.run_tv_plan_company)
    TextView runTvPlanCompany;
    @InjectView(R.id.run_tv_plan_run_status)
    TextView runTvPlanRunStatus;
    @InjectView(R.id.run_tv_plan_help_company)
    TextView runTvPlanHelpCompany;

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

    }

    @Override
    protected void onLoadDatas() {

    }


    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }


    @OnClick({R.id.run_btn_join_unit, R.id.run_tv_unit_member, R.id.run_tv_plan_run_status})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.run_btn_join_unit:
                break;
            case R.id.run_tv_unit_member:
                break;
            case R.id.run_tv_plan_run_status:
                break;
        }
    }
}
