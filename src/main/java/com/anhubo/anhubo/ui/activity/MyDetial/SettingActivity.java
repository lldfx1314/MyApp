package com.anhubo.anhubo.ui.activity.MyDetial;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by LUOLI on 2016/10/27.
 */
public class SettingActivity extends BaseActivity {
    @InjectView(R.id.ll_about)
    LinearLayout llAbout;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initViews() {
        setTopBarDesc("设置");

    }

    @Override
    protected void onLoadDatas() {

    }

    @OnClick(R.id.ll_about)
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_about:
                startActivity(new Intent(mActivity, AboutWeActivity.class));
                break;
            default:
                break;
        }
    }
}
