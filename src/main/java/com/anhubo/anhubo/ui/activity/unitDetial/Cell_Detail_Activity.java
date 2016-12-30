package com.anhubo.anhubo.ui.activity.unitDetial;

import android.view.View;
import android.widget.Button;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.view.RefreshListview;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by LUOLI on 2016/12/29.
 */
public class Cell_Detail_Activity extends BaseActivity {
    @InjectView(R.id.company_detail_listview)
    RefreshListview listview;
    @InjectView(R.id.exit_unit)
    Button exitUnit;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_company_detail;
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        setTopBarDesc("单元详情");
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



    @OnClick(R.id.exit_unit)
    public void onClick(View v) {
    }
}
