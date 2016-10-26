package com.anhubo.anhubo.ui.activity.unitDetial;

import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.MsgPerfectAdapterOne;
import com.anhubo.anhubo.adapter.MsgPerfectAdapterSecond;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.MsgPerfectBean;
import com.anhubo.anhubo.bean.MsgPerfectMemberBean;
import com.anhubo.anhubo.bean.MsgPerfectUseProBean;
import com.anhubo.anhubo.bean.MsgPerfect_UsePro_Bean;
import com.anhubo.anhubo.protocol.RequestResultListener;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.DatePackerUtil;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.NetUtil;
import com.anhubo.anhubo.utils.PopOneHelper;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.view.ShowDialogTop;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by Administrator on 2016/10/17.
 */
public class MsgPerfectActivity extends BaseActivity {
    private static final int MSGPERFECT01 = 1;
    private static final int MSGPERFECT02 = 2;
    private static final int MSGPERFECT03 = 3;
    private static final int MSGPERFECT04 = 4;
    @InjectView(R.id.ivTopBarLeft)
    ImageView ivTopBarLeft;
    @InjectView(R.id.ivTopBarleft_unit_menu)
    ImageView ivTopBarleftUnitMenu;
    @InjectView(R.id.ivTopBarleft_build_pen)
    ImageView ivTopBarleftBuildPen;
    @InjectView(R.id.tvAddress)
    TextView tvAddress;
    @InjectView(R.id.ivTopBarRight_unit_msg)
    ImageView ivTopBarRightUnitMsg;
    @InjectView(R.id.ll_Top)
    LinearLayout llTop;
    @InjectView(R.id.iv_msg_per01)
    ImageView ivMsgPer01;
    @InjectView(R.id.ll_msg_per01)
    LinearLayout llMsgPer01;
    @InjectView(R.id.iv_msg_per02)
    ImageView ivMsgPer02;
    @InjectView(R.id.ll_msg_per02)
    LinearLayout llMsgPer02;
    @InjectView(R.id.iv_msg_per03)
    ImageView ivMsgPer03;
    @InjectView(R.id.ll_msg_per03)
    LinearLayout llMsgPer03;
    @InjectView(R.id.iv_msg_per04)
    ImageView ivMsgPer04;
    @InjectView(R.id.ll_msg_per04)
    LinearLayout llMsgPer04;
    @InjectView(R.id.tv_msg_per05)
    TextView tvMsgPer05;
    @InjectView(R.id.ll_msg_per05)
    LinearLayout llMsgPer05;
    @InjectView(R.id.tv_msg_per06)
    TextView tvMsgPer06;
    @InjectView(R.id.ll_msg_per06)
    LinearLayout llMsgPer06;
    private int employNum;
    private int threeQ;
    private int lower;
    private int notice;
    private int rent;
    private String property1;
    private PopOneHelper popOneHelper;
    private String businessId;
    private Dialog dialog;
    private ListView lvMsgPerfectLeft;
    private ListView lvMsgPerfectRight;

    public static List<String> mainList = new ArrayList<>();
    private List<MsgPerfect_UsePro_Bean.Data.Properties> list;
    private List<MsgPerfect_UsePro_Bean.Data.Properties.Property1_arr> property1ArrList;

    private List<String> list2TypeName = new ArrayList<>();
    private String[][] data;
    private ListView mainlist;
    private ListView morelist;
    private MsgPerfectAdapterSecond adapterSecond;
    private String strSecondList;

    @Override
    protected void initConfig() {
        super.initConfig();
        // 获取business_id
        businessId = SpUtils.getStringParam(mActivity, Keys.BUSINESSID).trim();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_msgperfect;
    }

    @Override
    protected void initViews() {
        setTopBarDesc("完善信息");

        popOneHelper = new PopOneHelper(this);
        popOneHelper.setListItem(DatePackerUtil.getList());
        popOneHelper.setOnClickOkListener(new PopOneHelper.OnClickOkListener() {
            @Override
            public void onClickOk(String str) {
                tvMsgPer05.setText(str);
                // 走网络，提交员工数
                String url = Urls.Url_MsgPerfect_Member;
                HashMap<String, String> params = new HashMap<>();
                params.put("business_id", businessId);
                params.put("employ_num", str);
                MyRequestResultListener1 requestResultListener = new MyRequestResultListener1();
                NetUtil.requestData(url, params, MsgPerfectMemberBean.class, requestResultListener, 0);
            }
        });
    }

    @Override
    protected void initEvents() {
        super.initEvents();
        // 刚进来走网络获取数据
        String url = Urls.Url_MsgPerfect;
        HashMap<String, String> params = new HashMap<>();
        String businessId = SpUtils.getStringParam(mActivity, Keys.BUSINESSID);
        params.put("business_id", businessId);
        MyRequestResultListener requestResultListener = new MyRequestResultListener();
        NetUtil.requestData(url, params, MsgPerfectBean.class, requestResultListener, 0);
    }


    @Override
    protected void onLoadDatas() {

    }

    /**
     * 员工数的网络强求
     */
    class MyRequestResultListener1 implements RequestResultListener<MsgPerfectMemberBean> {
        @Override
        public void onRequestFinish(MsgPerfectMemberBean bean, int what) {
            if (bean != null) {
                int code = bean.code;
                String msg = bean.msg;
                ToastUtils.showLongToast(mActivity, msg);

            } else {
                System.out.println("MsgPerfectActivity上传员工数量+++===界面没拿到bean对象");
            }
        }

        @Override
        public void showJsonStr(String str, int what) {
            //System.out.println("MsgPerfectActivity上传员工数量+++===" + str);
        }
    }

    /**
     * 首次加载网络
     */
    class MyRequestResultListener implements RequestResultListener<MsgPerfectBean> {
        @Override
        public void onRequestFinish(MsgPerfectBean bean, int what) {
            if (bean != null) {

                // 营业执照
                threeQ = bean.data.three_q;
                //法人
                lower = bean.data.lower;
                //消防审批
                notice = bean.data.notice;
                //租房合同
                rent = bean.data.rent;
                // 员工数
                employNum = bean.data.employ_num;//员工数
                // 场所性质
                property1 = bean.data.property1;
                isShowView();
            } else {
                // 没拿到bean对象
                System.out.println("MsgPerfectActivity界面+++===没拿到bean对象");
            }
        }

        @Override
        public void showJsonStr(String str, int what) {
            //System.out.println("MsgPerfectActivity+++===" + str);
        }
    }

    private void isShowView() {

        if (threeQ == 1) {
            ivMsgPer01.setVisibility(View.VISIBLE);
        } else {
            ivMsgPer01.setVisibility(View.GONE);
        }
        if (lower == 1) {
            ivMsgPer02.setVisibility(View.VISIBLE);
        } else {
            ivMsgPer02.setVisibility(View.GONE);
        }
        if (notice == 1) {
            ivMsgPer03.setVisibility(View.VISIBLE);
        } else {
            ivMsgPer03.setVisibility(View.GONE);
        }
        if (rent == 1) {
            ivMsgPer04.setVisibility(View.VISIBLE);
        } else {
            ivMsgPer04.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(employNum+"")) {
            tvMsgPer05.setVisibility(View.VISIBLE);
            tvMsgPer05.setText(employNum+"");
        } else {
            tvMsgPer05.setText(0);
        }

        if (!TextUtils.isEmpty(property1)) {
            tvMsgPer06.setVisibility(View.VISIBLE);
            tvMsgPer06.setText(property1);
        } else {
            tvMsgPer06.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (null != intent) {
            switch (requestCode) {
                case MSGPERFECT01:
                    if (resultCode == 1) {
                        boolean result1_boolean = intent.getBooleanExtra(Keys.ISCLICK1, false);
                        if (result1_boolean) {
                            ivMsgPer01.setVisibility(View.VISIBLE);
                        } else {
                            ivMsgPer01.setVisibility(View.GONE);

                        }
                    }

                    break;
                case MSGPERFECT02:
                    if (resultCode == 2) {
                        boolean result2_boolean = intent.getBooleanExtra(Keys.ISCLICK2, false);
                        if (result2_boolean) {
                            ivMsgPer02.setVisibility(View.VISIBLE);
                        } else {
                            ivMsgPer02.setVisibility(View.GONE);

                        }
                    }

                    break;
                case MSGPERFECT03:
                    if (resultCode == 3) {
                        boolean result3_boolean = intent.getBooleanExtra(Keys.ISCLICK3, false);
                        if (result3_boolean) {
                            ivMsgPer03.setVisibility(View.VISIBLE);
                        } else {
                            ivMsgPer03.setVisibility(View.GONE);

                        }
                    }

                    break;
                case MSGPERFECT04:
                    if (resultCode == 4) {
                        boolean result4_boolean = intent.getBooleanExtra(Keys.ISCLICK4, false);
                        if (result4_boolean) {
                            ivMsgPer04.setVisibility(View.VISIBLE);
                        } else {
                            ivMsgPer04.setVisibility(View.GONE);

                        }
                    }

                    break;
            }

        }

    }

    @OnClick({R.id.ll_msg_per01, R.id.ll_msg_per02, R.id.ll_msg_per03, R.id.ll_msg_per04, R.id.ll_msg_per05, R.id.ll_msg_per06})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_msg_per01:
                startActivityForResult(new Intent(mActivity, UploadingActivity1.class), MSGPERFECT01);
                break;
            case R.id.ll_msg_per02:
                startActivityForResult(new Intent(mActivity, UploadingActivity2.class), MSGPERFECT02);
                break;
            case R.id.ll_msg_per03:
                startActivityForResult(new Intent(mActivity, UploadingActivity3.class), MSGPERFECT03);
                break;
            case R.id.ll_msg_per04:
                startActivityForResult(new Intent(mActivity, UploadingActivity4.class), MSGPERFECT04);
                break;
            case R.id.ll_msg_per05:
                popOneHelper.show(llMsgPer05);
                break;
            case R.id.ll_msg_per06:
                // 顶部弹出对话框
                showDialog();


                break;
        }
    }

    /**
     * 场所使用性质弹窗
     */
    private void showDialog() {
        View view = View.inflate(mActivity, R.layout.top_dialog, null);
        View btnTopDialog = view.findViewById(R.id.btn_top_dialog);
        ShowDialogTop dialogTop = new ShowDialogTop(mActivity, view, btnTopDialog);
        dialog = dialogTop.show();

        mainlist = (ListView) view.findViewById(R.id.lv_MsgPerfect_01);
        morelist = (ListView) view.findViewById(R.id.lv_MsgPerfect_02);
        // 定义方法，获取数据
        getData();

    }

    private void getData() {
        String url = Urls.Url_GetUsePro;
        MyRequestResultListener2 requestResultListener = new MyRequestResultListener2();
        NetUtil.requestData(url, null, MsgPerfect_UsePro_Bean.class, requestResultListener, 0);
    }

    class MyRequestResultListener2 implements RequestResultListener<MsgPerfect_UsePro_Bean> {
        @Override
        public void onRequestFinish(MsgPerfect_UsePro_Bean bean, int what) {
            if (bean != null) {
                showData(bean);
                // 设置第一个适配器
                setAdapterOne();

            } else {
                System.out.println("DeviceName_Activity+++===没拿到bean对象");
            }

        }

        @Override
        public void showJsonStr(String str, int what) {
            //System.out.println("MsgPerfectActivity+++===" + str);
        }
    }

    private void setAdapterOne() {
        // 拿到数据后调用构造把数据传过去
        final MsgPerfectAdapterOne adapterOne = new MsgPerfectAdapterOne(mActivity, mainList);
        mainlist.setAdapter(adapterOne);
        adapterOne.setSelectItem(0);
        mainlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                initAdapter(data[position]);
                adapterOne.setSelectItem(position);
                adapterOne.notifyDataSetChanged();
            }
        });
        // 设置第二个适配器
        setAdapterSecond();
    }

    private void setAdapterSecond() {
        mainlist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        // 一定要设置这个属性，否则ListView不会刷新
        initAdapter(data[0]);

        morelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                adapterSecond.setSelectItem(position);

                TextView tv = (TextView) view.findViewById(R.id.moreitem_txt);
                // 获取到从第二例数组里面的字符串
                strSecondList = tv.getText().toString().trim();
                // 拿着数据去走网络传到服务器   这里用OKhttp
                Map<String, String> params = new HashMap<>();
                params.put("business_id", businessId);
                params.put("property1", strSecondList);

                String url = Urls.Url_UpLoading06;
                OkHttpUtils.post()//
                        .url(url)
                        .params(params)//
                        .build()//
                        .execute(new MyStringCallback());
                adapterSecond.notifyDataSetChanged();

            }
        });
    }
    /**场所使用性质*/
    class MyStringCallback extends StringCallback{
        @Override
        public void onError(Call call, Exception e) {
            ToastUtils.showToast(mActivity,"网络有问题，请检查");

            dialog.dismiss();
        }

        @Override
        public void onResponse(String response) {
            MsgPerfectUseProBean bean = new Gson().fromJson(response, MsgPerfectUseProBean.class);
            int code = bean.code;
            if(code != 0){
                ToastUtils.showToast(mActivity,"上传失败");
                tvMsgPer06.setVisibility(View.VISIBLE);
                // 把字符串设置给场所使用性质的控件
                tvMsgPer06.setText(strSecondList);
                dialog.dismiss();
            }else{
                ToastUtils.showToast(mActivity,"上传成功");
                tvMsgPer06.setVisibility(View.VISIBLE);
                // 把字符串设置给场所使用性质的控件
                tvMsgPer06.setText(strSecondList);
                dialog.dismiss();
            }

        }
    }

    private void initAdapter(String[] array) {
        adapterSecond = new MsgPerfectAdapterSecond(mActivity, array);
        morelist.setAdapter(adapterSecond);
        adapterSecond.notifyDataSetChanged();
    }


    private void showData(MsgPerfect_UsePro_Bean bean) {
        // 先获取第一列数据
        list = bean.data.properties;
        // 每次遍历都清空一下集合
        mainList.clear();
        // 遍历list
        for (int i = 0; i < list.size(); i++) {
            MsgPerfect_UsePro_Bean.Data.Properties properties = list.get(i);
            String property2 = properties.property2;
            // 获取到第一列数据的集合
            mainList.add(property2);

            // 获取到第二列数据
            property1ArrList = properties.property1_arr;
            for (int m = 0; m < property1ArrList.size(); m++) {
                MsgPerfect_UsePro_Bean.Data.Properties.Property1_arr property1Arr = property1ArrList.get(m);
                // 拿到第二列的每一组的每个数据
                String property11 = property1Arr.property1;
                //添加进每组
                list2TypeName.add(property11);
            }
            // 每次添加完后为了便于区分,添加一个*来用于分割集合
            list2TypeName.add("#");
        }

        //list2TypeName集合添加完之后用#切割得到新数组
        String[] arr = list2TypeName.toString().split("#");
        // 创建一个二维数组
        data = new String[arr.length][];
        for (int i = 0; i < arr.length; i++) {
            String s = arr[i];
            String string = s.substring(1, s.length()).trim();
            String[] newArr = string.split(",");
            data[i] = newArr;
        }
    }
}