package com.anhubo.anhubo.ui.activity.MyDetial;

import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.Alter_UnitBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.ui.activity.unitDetial.BuildingActivity;
import com.anhubo.anhubo.ui.activity.unitDetial.BusinessActivity;
import com.anhubo.anhubo.utils.JsonUtil;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.Utils;
import com.anhubo.anhubo.view.AlertDialog;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by LUOLI on 2016/12/6.
 */
public class AlterUnitActivity extends BaseActivity {
    private static final int REQUESTCODE1 = 1;
    private static final int REQUESTCODE2 = 2;
    private static final String TAG = "AlterUnitActivity";
    @InjectView(R.id.tv_alter_unit)
    TextView tvMyUnit;
    @InjectView(R.id.ll_alter_unit)
    LinearLayout llAlterUnit;
    @InjectView(R.id.tv_alter_building)
    TextView tvMyBuilding;
    @InjectView(R.id.ll_alter_building)
    LinearLayout llAlterBuilding;
    @InjectView(R.id.btn_alter_building_unit)
    Button btnAlterBuildingUnit;
    private String businessName;
    private String buildingName;
    private String building;
    private String unit;
    private Dialog showDialog;
    private String buildPoi;
    private String businessPoi;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_alter_unit;
    }

    @Override
    protected void initViews() {
        setTopBarDesc("修改单位");
    }

    @Override
    protected void initEvents() {
        super.initEvents();
        // 初始化显示单位和建筑
        buildingName = SpUtils.getStringParam(mActivity, Keys.BUILDINGNAME);
        if (!TextUtils.isEmpty(buildingName)) {
            tvMyBuilding.setText(buildingName);
        }

        businessName = SpUtils.getStringParam(mActivity, Keys.BUSINESSNAME);
        if (!TextUtils.isEmpty(businessName)) {
            tvMyUnit.setText(businessName);
        }
    }

    @Override
    protected void onLoadDatas() {
        btnAlterBuildingUnit.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case REQUESTCODE1:
                    if (resultCode == 1) {
                        // 建筑
                        String stringExtra = data.getStringExtra(Keys.STR);
                        buildPoi = data.getStringExtra(Keys.BUILD_POI);

                        if (!TextUtils.isEmpty(stringExtra)) {
                            tvMyBuilding.setText(stringExtra);
                        }
                    }
                    break;
                case REQUESTCODE2:
                    if (resultCode == 2) {
                        // 单位
                        String stringExtra = data.getStringExtra(Keys.STR);
                        businessPoi = data.getStringExtra(Keys.BUSINESS_POI);
                        if (!TextUtils.isEmpty(stringExtra)) {
                            tvMyUnit.setText(stringExtra);
                        }
                    }
                    break;
            }
        }
    }

    @OnClick({R.id.ll_alter_unit, R.id.ll_alter_building, R.id.btn_alter_building_unit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_alter_building:
                // 建筑的点击事件，地图poi
                Intent intent = new Intent(mActivity, BuildingActivity.class);
                startActivityForResult(intent, REQUESTCODE1);
                break;
            case R.id.ll_alter_unit:
                // 单位的点击事件，地图poi
                Intent intent2 = new Intent(mActivity, BusinessActivity.class);
                startActivityForResult(intent2, REQUESTCODE2);
                break;
            case R.id.btn_alter_building_unit:
                submitAlter();
                break;
        }
    }

    /**
     * 提交修改
     */
    private void submitAlter() {
        getText();

        if (TextUtils.isEmpty(building)) {
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("请选择建筑")
                    .setCancelable(false).show();
            return;
        }

        String uid = SpUtils.getStringParam(mActivity, Keys.UID);
        showDialog = loadProgressDialog.show(mActivity, "正在提交...");
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("building_name", building);
        params.put("business_name", unit);
        params.put("version", versionName);
        if (TextUtils.isEmpty(buildPoi)) {
            params.put("building_poi_id", "");
        }else{
            params.put("building_poi_id", buildPoi);
        }
        if (TextUtils.isEmpty(businessPoi)) {
            params.put("business_poi_id", "");
        }else{
            params.put("business_poi_id", businessPoi);
        }

        String url = Urls.Url_AlterUnit;

        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback());
    }

    private void getText() {
        building = tvMyBuilding.getText().toString().trim();
        unit = tvMyUnit.getText().toString().trim();
    }


    class MyStringCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {
            showDialog.dismiss();
            LogUtils.e(TAG,":submitAlter",e);
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("网络有问题，请检查")
                    .setCancelable(true).show();
        }

        @Override
        public void onResponse(String response) {
            LogUtils.eNormal(TAG+":submitAlter",response);
            showDialog.dismiss();
            final Alter_UnitBean bean = JsonUtil.json2Bean(response, Alter_UnitBean.class);
            if (bean != null) {
                int code = bean.code;
                String msg = bean.msg;
                Alter_UnitBean.Data data = bean.data;
                String businessId = data.business_id;
                final String businessName = data.business_name;
                String buildingId = data.building_id;
                String buildingName = data.building_name;

                // 保存修改后的内容
                SpUtils.putParam(mActivity, Keys.BUSINESSID, businessId);
                SpUtils.putParam(mActivity, Keys.BULIDINGID, buildingId);
                SpUtils.putParam(mActivity, Keys.BUILDINGNAME, buildingName);
                SpUtils.putParam(mActivity, Keys.BUSINESSNAME, businessName);
                if (code == 0) {
                    new AlertDialog(mActivity).builder()
                            .setTitle("提示")
                            .setMsg("修改成功")
                            .setCancelable(false)
                            .setPositiveButton("确认", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // 关闭该页面
                                    Intent intent = new Intent();
                                    intent.putExtra(Keys.BUSINESSNAME, businessName);
                                    setResult(1, intent);
                                    finish();
                                }
                            }).show();
                }

            }

        }
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }

}
