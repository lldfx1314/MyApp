package com.anhubo.anhubo.ui.activity.unitDetial;

import android.view.View;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.view.RefreshListview;

import butterknife.InjectView;

/**
 * Created by LUOLI on 2016/12/29.
 */
public class CellListActivity extends BaseActivity {
    @InjectView(R.id.unit_list_listview)
    RefreshListview listview;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_unit_list;
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        setTopBarDesc("单元列表");
    }

    @Override
    protected void initViews() {

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
}
