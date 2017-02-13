package com.anhubo.anhubo.ui.activity.unitDetial;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.DeviceDetailsAdapter;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.CheckComplete_Bean;
import com.anhubo.anhubo.bean.ScanBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.ui.activity.buildDetial.TestActivity;
import com.anhubo.anhubo.utils.JsonUtil;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.utils.PopBirthHelper;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.utils.Utils;
import com.anhubo.anhubo.view.AlertDialog;
import com.anhubo.anhubo.view.ShowCheckDeviceDialog;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by Administrator on 2016/9/14.
 */
public class NfcScanActivity extends BaseActivity {

    private static final int REQUIRECODE = 1;
    private static final String TAG = "NfcScanActivity";
    @InjectView(R.id.btn_completeNfc)
    Button btnCompleteNfc;
    @InjectView(R.id.tv_bignfcNumber)
    TextView tvBignfcNumber;
    @InjectView(R.id.tv_smallnfcNumber)
    TextView tvSmallnfcNumber;
    @InjectView(R.id.pronfcBar)
    ProgressBar pronfcBar;
    @InjectView(R.id.rl_nfcnumber)
    RelativeLayout rlNfcnumber;

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private String cardNumber;
    private String ndIntent;// 新增设备
    private String checkIntent;// 检查设备
    private String exerciseIntent;// 演练
    private String testIntent;// 演练
    private static String SCANINENT;// 定义一个字符串，用来执行具体那个的网络请求
    private String deviceId;// 网络获取到的deviceId
    private boolean isEnter = false;
    private int deviceCheckedNum;
    private String devicesNum;
    private Dialog dialog;
    private ListView ck_ListView;
    private TextView deviceName;
    private Button btnCheckMore;
    private Button btnCheckComplete;
    private TextView checkTime;
    private RelativeLayout checkFeedback;
    private TextView tvIssueDes;
    private DeviceDetailsAdapter adapter;
    private PopBirthHelper popBirthHelper;
    private String startTime;
    private String device_type_name;
    private int dateFlag;
    private List<String> require_list;
    private String isId;
    private String isContent;
    private boolean isGetDeviceInfo = false;
    private String uid;
    private String businessid;
    private ArrayList<Integer> completeList;
    /* 用来记录存在问题的选项，0表示没问题，1表示有问题*/
    private int isProblem = 0;
    /*用来记录对应的position位是否被点击*/
    private Map<Integer, Boolean> map = new HashMap<>();
    private View viewFeed;
    private View viewTime;
    private ArrayList<String> listResult;
    private Dialog showDialog;
    private Dialog showDialog1;

    @Override
    protected void initConfig() {
        super.initConfig();

        // 获取打开此Activity的意图对象携带的数据
        ndIntent = getIntent().getStringExtra(Keys.NEWDEVICE);
        checkIntent = getIntent().getStringExtra(Keys.CHECK);
        exerciseIntent = getIntent().getStringExtra(Keys.EXERCISE);
        testIntent = getIntent().getStringExtra(Keys.TEST);

        // 获取uid
        uid = SpUtils.getStringParam(mActivity, Keys.UID);
        businessid = SpUtils.getStringParam(mActivity, Keys.BUSINESSID);


    }

    /**
     * 添加布局
     */
    @Override
    protected int getContentViewId() {
        return R.layout.activity_nfc_scan;
    }

    @Override
    protected void initViews() {
        // 找控件
        ButterKnife.inject(this);
        // 检查设备是否支持NFC
        checkNFC();
        //
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        // 设置状态栏显示的提示内容
        setTopBarDesc("扫描NFC");
        // 设置完成检查按钮显示的文字
        if (!TextUtils.isEmpty(ndIntent)) {
            setTopBarDesc("新增");
            btnCompleteNfc.setText("完成新增");
            pronfcBar.setVisibility(View.GONE);
            rlNfcnumber.setVisibility(View.GONE);
        } else if (!TextUtils.isEmpty(checkIntent)) {
            setTopBarDesc("检查");
            tvTopBarRight.setVisibility(View.VISIBLE);
            btnCompleteNfc.setText("完成检查");
        } else if (!TextUtils.isEmpty(exerciseIntent)) {
            setTopBarDesc("演练");
            btnCompleteNfc.setText("完成演练");
            pronfcBar.setVisibility(View.GONE);
            rlNfcnumber.setVisibility(View.GONE);
        } else if (!TextUtils.isEmpty(testIntent)) {
            setTopBarDesc("测试");
            btnCompleteNfc.setText("完成测试");
            pronfcBar.setVisibility(View.GONE);
            rlNfcnumber.setVisibility(View.GONE);
        }
        tvTopBarRight.setOnClickListener(this);
        // 出厂时间的弹窗
        alterTime();
    }


    @Override
    protected void initEvents() {
        super.initEvents();
        listResult = new ArrayList<String>();
        getNum();
    }

    /**
     * 获取进度条的信息
     */
    private void getNum() {
        String url = Urls.Url_Get_Num;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("uid", uid); //这是uid,登录后改成真正的用户
        params.put("business_id", businessid);//这是business_id,登录后改成真正的business_id

        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback2());
    }


    class MyStringCallback2 extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {
            LogUtils.e(TAG, ":进度条getNum", e);
        }

        @Override
        public void onResponse(String response) {
            LogUtils.eNormal(TAG + ":进度条getNum", response);
            CheckComplete_Bean bean = JsonUtil.json2Bean(response, CheckComplete_Bean.class);
            if (bean != null) {

                // 获取到数据
                deviceCheckedNum = bean.data.device_checked_num;
                devicesNum = bean.data.devices_num;

                //动态的设置进度条
                setProgressBar();

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (intent != null) {

            switch (requestCode) {

                case REQUIRECODE:
                    // 待反馈界面
                    if (checkFeedback != null && resultCode == 1) {

                        checkFeedback.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    }


    @Override
    protected void onLoadDatas() {

    }


    /**
     * 拿到数据后做相应的操作
     */
    private void processData(String cardNumber) {
        isEnter = false;
        if (!TextUtils.isEmpty(cardNumber)) {
            if (!TextUtils.isEmpty(ndIntent) && !isEnter) {
                /***********新增**************************/
                isEnter = true;
                //新增
                addNewDevice();


            } else if (!TextUtils.isEmpty(checkIntent) && !isEnter) {
                /***********检查**************************/
                isEnter = true;

                // 定义一个请求网络的方法
                getData();

            } else if (!TextUtils.isEmpty(exerciseIntent) && !isEnter) {
                /***********演练**************************/
                // 演练,获取DeviceId的最后一位数进行跳转到演练界面
                isEnter = true;
                String lastNumber = cardNumber.substring(cardNumber.length() - 1, cardNumber.length());
                if (Integer.parseInt(lastNumber) <= 1) {
                    new AlertDialog(mActivity).builder()
                            .setTitle("提示")
                            .setMsg("演练结束")
                            .setCancelable(true).show();
                } else {
                    new AlertDialog(mActivity).builder()
                            .setTitle("提示")
                            .setMsg("您已到达" + lastNumber + "层")
                            .setCancelable(true).show();
                }


            } else if (!TextUtils.isEmpty(testIntent) && !isEnter) {
                /***********测试**************************/
                isEnter = true;
                // 测试的方法
                enterTestActivity();
            }

        }
    }


    /**
     * 进入测试页
     */
    private void enterTestActivity() {
        // 跳转到测试页面
        Intent intent = new Intent(mActivity, TestActivity.class);
        intent.putExtra(Keys.CARDNUMBER, cardNumber);
        startActivity(intent);
    }

    /**
     * 这是新增设备的方法
     */
    private void addNewDevice() {
        // 拿到扫描到的eDeviceId走添加设备借接口
        // 跳转到新增页面
        Intent intent = new Intent(NfcScanActivity.this, Add_Device_Activity.class);
        intent.putExtra(Keys.CARDNUMBER, cardNumber);
        startActivity(intent);
    }


    /**
     * 这是检查的网络请求获取数据的方法，使用Post
     */
    private void getData() {
        showDialog = loadProgressDialog.show(mActivity, "请稍后...");
        String url = Urls.Url_Check;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("device_id", cardNumber);
        params.put("version", versionName);
        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback());


    }


    /**
     * 检查的网络请求获取数据的方法
     */
    class MyStringCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {
            showDialog.dismiss();
            LogUtils.e(TAG, ":getData", e);
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("网络有问题，请检查")
                    .setCancelable(true).show();
        }

        @Override
        public void onResponse(String response) {
            showDialog.dismiss();
            LogUtils.eNormal(TAG + ":getData", response);
            ScanBean bean = JsonUtil.json2Bean(response, ScanBean.class);
            if (bean != null) {
                // 获取到数据置为true
                isGetDeviceInfo = true;
                parseMessage(bean);
            } else {
                System.out.println("NfcScanActivity+++===没获取bean对象");
            }
        }
    }


    /**
     * 拿到数据后判断设备ID是否绑定而跳转到相应界面
     */
    private void parseMessage(ScanBean scanBean) {
        int isExist = scanBean.data.device_exist;//设备号是否在后台存在


        //设备ID
        deviceId = scanBean.data.device_id;
        // 获取数据
        showInfo(scanBean);
        if (isExist == 0) {
            // 跳转到新增页面
            Intent intent = new Intent(NfcScanActivity.this, Add_Device_Activity.class);
            intent.putExtra(Keys.CARDNUMBER, deviceId);
            startActivity(intent);


        } else if (isExist == 1) {

            // 弹出对话框
            showCheckDeviceDialog();

        } else {
            ToastUtils.showToast(mActivity, scanBean.msg);
        }
    }

    /**
     * 拿到信息获取设备的相关信息
     */
    private void showInfo(ScanBean scanBean) {
        // 获取到设备名
        device_type_name = scanBean.data.device_type_name;
        // 获取到设备Id
        deviceId = scanBean.data.device_id;
        // 获取到时间标记
        dateFlag = scanBean.data.require_date_flag;
        // 拿到问题集合
        require_list = scanBean.data.require_list;
        // 拿到问题id
        isId = scanBean.data.is_id;
        // 拿到问题内容
        isContent = scanBean.data.is_content;
    }

    private void showCheckDeviceDialog() {
        // 创建一个对象
        View view = View.inflate(mActivity, R.layout.activity_check_device, null);
        //显示对话框
        ShowCheckDeviceDialog checkDeviceDialog = new ShowCheckDeviceDialog(mActivity, view);
        checkDeviceDialog.setListenerDialog(new ShowCheckDeviceDialog.ClickListenerDialog() {

            @Override
            public void popup() {

                // 初始化completeList
                completeList = new ArrayList<>();
                // 创建一个数组，五个数全是0
                int[] arr = new int[]{0, 0, 0, 0, 0};
                for (int i = 0; i < arr.length; i++) {

                    completeList.add(arr[i]);
                }
                // 为map集合设置默认的值，每个item默认没被点击过
                map.put(0, false);
                map.put(1, false);
                map.put(2, false);
                map.put(3, false);
                map.put(4, false);

                // 每次弹出对话框把listResult集合里的元素全部清空
                if (listResult != null) {
                    listResult.clear();
                }

            }

        });
        dialog = checkDeviceDialog.show();
        // 找控件
        ck_ListView = (ListView) view.findViewById(R.id.check_listView);
        deviceName = (TextView) view.findViewById(R.id.tv_check_device);
        btnCheckMore = (Button) view.findViewById(R.id.check_more);// 更多
        btnCheckComplete = (Button) view.findViewById(R.id.check_complete);//检查完成
        // 出厂日期
        checkTime = (TextView) view.findViewById(R.id.tv_check_time);
        // 出厂日期分割线
        viewTime = view.findViewById(R.id.view_time);
        //待处理反馈
        checkFeedback = (RelativeLayout) view.findViewById(R.id.rl_check_feedback);
        //待处理反馈分割线
        viewFeed = view.findViewById(R.id.view_feed);
        // 问题描述
        tvIssueDes = (TextView) view.findViewById(R.id.tv_issue_des);

        // 获取适配器对象
        adapter = new DeviceDetailsAdapter(this, require_list);
        // 处理出厂日期、待处理反馈
        checkFeedback.setOnClickListener((View.OnClickListener) mActivity);

        checkTime.setOnClickListener((View.OnClickListener) mActivity);
        // 事件处理
        event();
    }

    /**
     * 事件处理
     */
    private void event() {


        // 时间
        if (dateFlag == 1) {
            checkTime.setVisibility(View.VISIBLE);
            viewTime.setVisibility(View.VISIBLE);
        }
        // 问题描述
        if (!TextUtils.isEmpty(isContent)) {
            viewFeed.setVisibility(View.VISIBLE);
            checkFeedback.setVisibility(View.VISIBLE);
            tvIssueDes.setText(isContent);
        }


        // 显示 设备名称
        if (isGetDeviceInfo) {
            deviceName.setText(device_type_name);
        } else {
            deviceName.setText("没有数据");
            isGetDeviceInfo = false;
        }
        // 给listView设置适配器对象
        ck_ListView.setAdapter(adapter);
        // 设置listView的监听
        ck_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView imageView = (ImageView) view.findViewById(R.id.iv_check_device);
                // 获取当前position对应位置的点击记录
                Boolean isClick = map.get(position);
                if (!isClick) {
                    // 表示这个问题存在
                    imageView.setImageResource(R.drawable.fuxuan_input01);
                    // 存在问题就设置为1;
                    isProblem = 1;
                } else {
                    // 这个问题不存在
                    imageView.setImageResource(R.drawable.fuxuan_input02);
                    isProblem = 0;
                }

                // 将对应position位置改为对应的boolean值和值
                map.put(position, !isClick);
                completeList.set(position, isProblem);

            }

        });

        // 设置按钮的点击事件
        btnCheckMore.setOnClickListener(this);
        btnCheckComplete.setOnClickListener(this);

    }

    private void alterTime() {
        popBirthHelper = new PopBirthHelper(mActivity);
        popBirthHelper.setOnClickOkListener(new PopBirthHelper.OnClickOkListener() {
            @Override
            public void onClickOk(String time) {

                if (!TextUtils.isEmpty(time)) {
                    startTime = time;

                    // 拿到年龄,上传到网络
                } else {
                    ToastUtils.showToast(mActivity, "您所选日期大于当前时间");
                }

            }


        });
    }

    /**
     * 检查完成的点击事件，请求网络
     */
    private void checkComplete() {

        // 这里是完成的点击事件
        showDialog1 = loadProgressDialog.show(mActivity, "正在提交...");
        String url = Urls.Url_Check_Complete;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("uid", uid); //这是uid,登录后改成真正的用户
        params.put("device_id", deviceId);
        params.put("device_result", completeList.toString());//选择后的集合
        params.put("business_id", businessid);//这是business_id,登录后改成真正的business_id
        if (!TextUtils.isEmpty(startTime)) {
            params.put("start_time", startTime);
        }

        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback1());
    }


    class MyStringCallback1 extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {
            showDialog1.dismiss();

            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("网络有问题，请检查")
                    .setCancelable(true).show();

            System.out.println("NfcScanActivity+++===没拿到数据" + e.getMessage());
        }

        @Override
        public void onResponse(String response) {
            showDialog1.dismiss();
            CheckComplete_Bean bean = new Gson().fromJson(response, CheckComplete_Bean.class);
            if (bean != null) {
                dialog.dismiss();
                // 获取到数据
                deviceCheckedNum = bean.data.device_checked_num;
                devicesNum = bean.data.devices_num;

                //动态的设置进度条
                setProgressBar();

                boolean isZero = false;
                for (int i = 0; i < completeList.size(); i++) {
                    Integer integer = completeList.get(i);
                    if (integer == 1) {
                        isZero = true;
                    }
                }
                if (isZero) {
                    // 有问题，跳转到反馈界面
                    Intent intent = new Intent(mActivity, FeedbackActivity.class);
                    intent.putExtra(Keys.DeviceId, deviceId);
                    /******************把问题传递过去********************/
                    for (int i = 0; i < completeList.size(); i++) {
                        Integer integer = completeList.get(i);
                        if (integer == 1) {
                            String s = require_list.get(i);
                            listResult.add(s);
                        }
                    }
                    intent.putExtra(Keys.REQUIRE_LIST, listResult);
                    startActivity(intent);
                } else {
                    // 无问题，提示检查完成
                    ToastUtils.showToast(mActivity, "检查完成");

                }

            }
        }
    }

    /**
     * 动态的设置进度条
     */
    private void setProgressBar() {


        int maxNum = Integer.parseInt(devicesNum);
        // 做一个处理，防止服务器返回当前进度大于总进度
        if (deviceCheckedNum > maxNum) {
            pronfcBar.setProgress(maxNum);
            tvBignfcNumber.setText(devicesNum);
        } else {
            pronfcBar.setProgress(deviceCheckedNum);
            tvBignfcNumber.setText(deviceCheckedNum + "");
        }

        pronfcBar.setMax(Integer.parseInt(devicesNum));
        tvSmallnfcNumber.setText(devicesNum + "");
    }

    /**
     * 点击事件的处理
     */
    @OnClick(R.id.btn_completeNfc)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_completeNfc:
                finish();
                break;
            case R.id.tvTopBarRight:
                // 右上角列表
                startActivity(new Intent(mActivity, DeviceList.class));
                break;
            case R.id.check_more:
                // 更多的点击事件
                Intent intent = new Intent(mActivity, FeedbackActivity.class);
                intent.putExtra(Keys.DeviceId, deviceId);
                startActivity(intent);
                break;
            case R.id.check_complete:
                // 检查完成的点击事件
                checkComplete();

                break;
            case R.id.tv_check_time:
                // 时间
                popBirthHelper.show(checkTime);
                break;
            case R.id.rl_check_feedback:
                // 待处理反馈
                Intent intent1 = new Intent(mActivity, Pending_FeedbackActivity.class);
                intent1.putExtra(Keys.IsId, isId);
                startActivityForResult(intent1, REQUIRECODE);
                break;
        }
    }
/*********************************************nfc扫描*******************************************/

    /**
     * 获取焦点的方法
     */
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // 获取焦点时开启前台分配
        enableForegroundDispatch();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableForegroundDispatch();
    }


    /**
     * 当获取到Tag信息的时候走这个方法 处理获取到的Tag信息
     */
    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        //拿到Tag信息进行处理
        if (intent != null) {
            processIntent(intent);
        } else {
            finish();
        }
    }

    /**
     * 设置开启前台进程的方法
     */
    private void enableForegroundDispatch() {
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    private void disableForegroundDispatch() {
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    /**
     * 检查设备是否支持NFC以及NFC是否打开
     */
    private void checkNFC() {
        // 获取nfc的适配器

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            new AlertDialog(mActivity).builder()
                    .setTitle("提示")
                    .setMsg("您的手机不支持NFC")
                    .setCancelable(true).show();
            return;
        } else {
            if ((!nfcAdapter.isEnabled())) {
                //NFC未开启，请去设置里面先打开NFC
                dialog();
                return;
            }
        }
    }

    /**
     * 拿到Tag之后判断是哪个类型然后做相应的处理
     */

    private void processIntent(Intent intent) {
        // 获取到意图对象里面的数据
        String action = intent.getAction();
        // 分别判断是哪个类型的Tag类型
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            //ToastUtils.showToast(mActivity, "是NDEF类型的");
            NdefMessage[] messages = null;
            // 拿到Tag里面的信息
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            // 判断数组
            if (rawMsgs != null) {
                messages = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    messages[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // 拿到记录
                byte[] empty = new byte[]{};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,
                        empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
                messages = new NdefMessage[]{msg};
            }
            // 定义一个方法处理拿到的数据
            processNDEFMsg(messages);
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            ToastUtils.showToast(mActivity, "是Tech类型的");
        } else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            ToastUtils.showToast(mActivity, "是TAG类型的");
        } else {
            ToastUtils.showToast(mActivity, "未知类型");
        }

    }

    private void processNDEFMsg(NdefMessage[] messages) {
        if (messages.length == 0 || messages == null) {

            ToastUtils.showToast(getApplicationContext(), "Tag内容为空");
            return;
        }
        // 如果messages集合不为空，则遍历集合
        for (int i = 0; i < messages.length; i++) {
            // 拿到每条信息里面的记录
            NdefRecord[] records = messages[i].getRecords();
            for (int j = 0; j < records.length; j++) {
                for (NdefRecord record : records) {
                    // 拿到记录并解析
                    parseRTDUriRecord(record);
                }
            }
        }
    }

    /**
     * 拿到记录解析并显示
     */
    private void parseRTDUriRecord(NdefRecord record) {
        if (record != null) {
            try {
                byte[] payload = record.getPayload();
                //下面开始NDEF文本数据第一个字节，状态字节
                //判断文本是基于UTF-8还是UTF-16的，取第一个字节"位与"上16进制的80，16进制的80也就是最高位是1，
                //其他位都是0，所以进行"位与"运算后就会保留最高位
                String textEncoding = ((payload[0] & 0x80) == 0) ? "UTF-8" : "UTF-16";
                //3f最高两位是0，第六位是1，所以进行"位与"运算后获得第六位
                int languageCodeLength = payload[0] & 0x3f;
                //下面开始NDEF文本数据第二个字节，语言编码
                //获得语言编码
                String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
                //下面开始NDEF文本数据后面的字节，解析出文本
                cardNumber = new String(payload, languageCodeLength + 1,
                        payload.length - languageCodeLength - 1, textEncoding);
                if (!TextUtils.isEmpty(cardNumber)) {
                    boolean isanhuboCard = cardNumber.startsWith("anhubo", 0);
                    boolean isAHBCard = cardNumber.startsWith("AHB", 0);
                    if (isanhuboCard || isAHBCard) {
                        processData(cardNumber);
                    } else {
                        AlertDialog builder = new AlertDialog(mActivity).builder();
                        builder
                                .setTitle("提示")
                                .setMsg("请扫描安互保专用卡片")
                                .setCancelable(true).show();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Dialog对话框提示用户去设置界面打开NFC权限
     */
    protected void dialog() {

        new AlertDialog(mActivity).builder()
                .setTitle("提示")
                .setMsg("前往系统设置打开NFC权限？")
                .setCancelable(false)
                .setPositiveButton("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 打开系统设置界面
                        Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                        startActivity(intent);
                        startActivity(intent);

                    }
                }).setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }).show();
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }
}
