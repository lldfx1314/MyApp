package com.anhubo.anhubo.ui.activity.unitDetial;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.bean.CheckComplete_Bean;
import com.anhubo.anhubo.bean.ScanBean;
import com.anhubo.anhubo.protocol.Urls;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.SpUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by Administrator on 2016/9/14.
 */
public class NfcScanActivity extends BaseActivity {

    private static final int REQUESTCODE = 1;// 添加完成
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
    private static String SCANINENT;// 定义一个字符串，用来执行具体那个的网络请求
    private String deviceId;// 网络获取到的deviceId
    private boolean isEnter = false;
    /*用来记录是否获取到信息的变量*/
    private boolean isGetCkeckInfo = false;
    private int deviceCheckedNum;
    private String devicesNum;
    private static final int REQUESTSCAN = 1;

    @Override
    protected void initConfig() {
        super.initConfig();

        // 获取打开此Activity的意图对象携带的数据
        ndIntent = getIntent().getStringExtra(Keys.NEWDEVICE);
        checkIntent = getIntent().getStringExtra(Keys.CHECK);
        exerciseIntent = getIntent().getStringExtra(Keys.EXERCISE);

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
        }
        tvTopBarRight.setOnClickListener(this);
    }

    @Override
    protected void initEvents() {
        super.initEvents();
        String deviceCheckedNum = SpUtils.getStringParam(mActivity, Keys.DEVICECHECKEDNUM);
        String devicesNum = SpUtils.getStringParam(mActivity, Keys.DEVICESNUM);
        if (!TextUtils.isEmpty(deviceCheckedNum) && !TextUtils.isEmpty(deviceCheckedNum)) {
            //设置进度条的初始化
            pronfcBar.setMax(Integer.parseInt(devicesNum));
            pronfcBar.setProgress(Integer.parseInt(deviceCheckedNum));
            tvBignfcNumber.setText(deviceCheckedNum + "");
            tvSmallnfcNumber.setText(devicesNum + "");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (intent != null) {

            switch (requestCode) {
                case REQUESTSCAN:
                    if (resultCode == 1) {

                        CheckComplete_Bean bean = (CheckComplete_Bean) intent.getSerializableExtra(Keys.CHECKCOMPLETE_BEAN);
                        // 拿到序列化对象就可以获取里面数据进行展示了
                        if (bean != null) {
                            // 获取到数据
                            deviceCheckedNum = bean.data.device_checked_num;
                            devicesNum = bean.data.devices_num;
                            // 把这俩数据保存起来
                            SpUtils.putParam(mActivity, Keys.DEVICECHECKEDNUM, deviceCheckedNum + "");
                            SpUtils.putParam(mActivity, Keys.DEVICESNUM, devicesNum);


                            //动态的设置进度条
                            pronfcBar.setMax(Integer.parseInt(devicesNum));
                            pronfcBar.setProgress(deviceCheckedNum);
                            tvBignfcNumber.setText(deviceCheckedNum + "");
                            tvSmallnfcNumber.setText(devicesNum + "");
                        }
                    } else {
                        ToastUtils.showToast(mActivity, "bean为空");
                    }
                    break;
            }
        }
    }


    @Override
    protected void onLoadDatas() {

    }

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

    /**
     * 拿到数据后做相应的操作
     */
    private void processData(String cardNumber) {
        isEnter = false;
        if (!TextUtils.isEmpty(cardNumber)) {
            if (!TextUtils.isEmpty(ndIntent) && !isEnter) {
                isEnter = true;
                //新增
                addNewDevice();


            } else if (!TextUtils.isEmpty(checkIntent) && !isEnter) {
                // 检查
                isEnter = true;

                // 定义一个请求网络的方法
                getData();

            } else if (!TextUtils.isEmpty(exerciseIntent) && !isEnter) {
                // 演练,获取DeviceId的最后一位数进行跳转到演练界面
                isEnter = true;
                String lastNumber = cardNumber.substring(cardNumber.length() - 1, cardNumber.length());
                if (Integer.parseInt(lastNumber) == 1) {
                    Toast.makeText(mActivity, "演练结束", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mActivity, "您已到达" + lastNumber + "层", Toast.LENGTH_LONG).show();
                }

                /***********演练**************************/
            } else {
                //isEnter = false;
            }

        }
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
        progressBar.setVisibility(View.VISIBLE);
        String url = Urls.Url_Check;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("device_id", cardNumber);

        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback());


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
        }
    }

    class MyStringCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {

            System.out.println("NfcScanActivity+++===没拿到数据" + e.getMessage());
        }

        @Override
        public void onResponse(String response) {
            ScanBean bean = new Gson().fromJson(response, ScanBean.class);
            if (bean != null) {

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
        progressBar.setVisibility(View.GONE);

        //设备ID
        deviceId = scanBean.data.device_id;
        //System.out.println("Nfc_D======"+deviceId);
        if (isExist == 0) {
            // 跳转到新增页面
            Intent intent = new Intent(NfcScanActivity.this, Add_Device_Activity.class);
            intent.putExtra(Keys.CARDNUMBER, deviceId);
            startActivity(intent);


        } else if (isExist == 1) {
            // 跳转到修改页面
            Intent intent = new Intent(NfcScanActivity.this, Check_Device_Activity.class);
            intent.putExtra(Keys.DEVICEINFO, scanBean);
            intent.putExtra(Keys.SCAN_TYPE, "nfcscan");
            startActivityForResult(intent, REQUESTSCAN);

        } else {
            ToastUtils.showToast(mActivity, scanBean.msg);
        }
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
            ToastUtils.showToast(mActivity, "您的手机不支持NFC");
            return;
        } else {
            if ((!nfcAdapter.isEnabled())) {
                ToastUtils.showToast(mActivity, "NFC尚未被开启，请在设置里面先打开NFC");
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
            //ToastUtils.showToast(getApplicationContext(), "是NDEF类型的");
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

            ToastUtils.showToast(getApplicationContext(), "是Tech类型的");

        } else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {

        } else {
            ToastUtils.showToast(getApplicationContext(), "未知类型");
        }

    }

    private void processNDEFMsg(NdefMessage[] messages) {
        if (messages.length == 0 || messages == null) {

            ToastUtils.showToast(getApplicationContext(), "Tag内容为空");
            return;
        }
        // 如果messages集合不为空，则遍历集合
        for (int i = 0; i < messages.length; i++) {
            //获取每条信息记录的长度
            int length = messages[i].getRecords().length;
            //System.out.println("拿到的信息记录是===" + length + "条");//这里用公交卡获取到的长度为1，说明每条信息里面只有一条记录
            //ToastUtils.showToast(getApplicationContext(), "信息 的长度"+length);
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
            String card = new String(record.getPayload());
            //System.out.println("切割之前的号======" + cardNumber);
            cardNumber = card.replace(card.substring(0, 7), "anhubo");
            // 拿到数据后做相应的操作
            if (!TextUtils.isEmpty(cardNumber)) {
                processData(cardNumber);
            }
        }
    }
}
