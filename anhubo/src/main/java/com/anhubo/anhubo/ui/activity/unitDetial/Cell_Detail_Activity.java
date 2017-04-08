package com.anhubo.anhubo.ui.activity.unitDetial;

import android.app.Dialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.CellDetailAdapter;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.CellDeiailBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.view.ShowBottonDialog;
import com.google.gson.Gson;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.okhttp.Request;

/**
 * Created by LUOLI on 2016/12/29.
 */
public class Cell_Detail_Activity extends BaseActivity {
    @InjectView(R.id.company_detail_listview)
    ListView listview;
    private String unitId;
    private String uid;
    private int pager = 0;
    private int page;
    private List<CellDeiailBean.Data.Businesses> businesses;
    private CellDetailAdapter adapter;
    private Dialog showDialog;
    private Dialog dialog;
    private Button btnWeixin;
    private Button btnweixinCircle;
    private String planId;

    @Override
    protected void initConfig() {
        super.initConfig();
        unitId = getIntent().getStringExtra(Keys.UNITID);
        planId = getIntent().getStringExtra(Keys.PLANID);
        uid = SpUtils.getStringParam(mActivity, Keys.UID);
    }

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
        ivTopBarRightUnitShare.setVisibility(View.VISIBLE);
        ivTopBarRightUnitShare.setOnClickListener(this);
    }


    @Override
    protected void onLoadDatas() {
        //　获取数据
        showDialog = loadProgressDialog.show(mActivity, "正在加载...");
        getData();
    }

    /**
     * 获取数据
     */
    private void getData() {
        String url = Urls.Url_Unit_Detail;
        Map<String, String> parmas = new HashMap<>();
        parmas.put("unit_id", unitId);
        parmas.put("page", String.valueOf(pager++));

        OkHttpUtils.post()
                .url(url)
                .params(parmas)
                .build()
                .execute(new MyStringCallback());
    }

    class MyStringCallback extends StringCallback {

        @Override
        public void onError(Request request, Exception e) {

            System.out.println("CellListActivity界面+获取数据失败+" + e.getMessage());
            showDialog.dismiss();
        }

        @Override
        public void onResponse(String response) {
            System.out.println("CellListActivity界面+" + response);
            showDialog.dismiss();
            setData(response);
        }
    }

    private void setData(String response) {
        CellDeiailBean bean = new Gson().fromJson(response, CellDeiailBean.class);
        if (bean != null) {
            int code = bean.code;
            String msg = bean.msg;
            CellDeiailBean.Data data = bean.data;
            page = data.page;
            businesses = data.businesses;
        }

        adapter = new CellDetailAdapter(mActivity, businesses);
        listview.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        ShareAction shareAction = new ShareAction(mActivity);
        String newUrl = Urls.Url_Cell_WeiXin + "?plan_id=" + planId;
        String title = "好友邀请你一起加入\"计划名称\"下\"单元名称\"单元,获取安全保障";
        String context = "加入互助计划,时刻享用安全保障,前行路上更安心";
        switch (v.getId()){
            case R.id.ivTopBarRight_unit_share:
                showDialog();
                break;
            case R.id.btn_weixin:

                shareAction
                        .setPlatform(SHARE_MEDIA.WEIXIN)
                        .withText(context)
                        .withTitle(title)
                        .withTargetUrl(newUrl)
                        .setCallback(umShareListener)
                        .share();
                break;
            case R.id.btn_weixin_circle:
                shareAction
                        .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                        .withText(context)
                        .withTitle(title)
                        .withTargetUrl(newUrl)
                        .setCallback(umShareListener)
                        .share();
                break;
        }
    }

    /**
     * 弹出对话框
     */
    private void showDialog() {
        // 创建一个对象
        View view = View.inflate(mActivity, R.layout.dialog_share, null);
        View btnCancel = view.findViewById(R.id.btn_share_Dialog_cancel);//取消按钮
        //显示对话框
        ShowBottonDialog showBottonDialog = new ShowBottonDialog(mActivity, view, btnCancel);
        dialog = showBottonDialog.show();
        //拍照按钮
        btnWeixin = (Button) view.findViewById(R.id.btn_weixin);
        //相册获取
        btnweixinCircle = (Button) view.findViewById(R.id.btn_weixin_circle);
        // 设置监听
        btnWeixin.setOnClickListener(this);
        btnweixinCircle.setOnClickListener(this);
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            //System.out.println(platform + "分享成功");
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            ToastUtils.showToast(mActivity, "分享失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            ToastUtils.showToast(mActivity, "分享取消");

        }
    };

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }
}
