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
 * Created by LUOLI on 2016/11/1.
 */
public class CertificationActivity extends BaseActivity {

    @InjectView(R.id.ll_idcard)
    LinearLayout llIdcard;
    @InjectView(R.id.ll_engineer)
    LinearLayout llEngineer;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_certification;
    }

    @Override
    protected void initViews() {
        setTopBarDesc("实名认证");
    }

    @Override
    protected void onLoadDatas() {

    }

    @OnClick({R.id.ll_idcard, R.id.ll_engineer})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_idcard:
                /**身份证认证*/
                idCard();
                break;
            case R.id.ll_engineer:
                /**消防工程师认证*/
                engineer();
                break;
        }
    }

    /**
     * 身份证认证
     */
    private void idCard() {
        Intent intent = new Intent(mActivity, IdCardActivity.class);
        startActivity(intent);
    }

    /**
     * 消防工程师认证
     */
    private void engineer() {
        Intent intent = new Intent(mActivity, EngineerActivity.class);
        startActivity(intent);
    }


}
