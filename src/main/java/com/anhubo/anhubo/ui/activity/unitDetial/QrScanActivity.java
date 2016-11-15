package com.anhubo.anhubo.ui.activity.unitDetial;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
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
import com.anhubo.anhubo.view.AlertDialog;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;
import okhttp3.Call;

public class QrScanActivity extends BaseActivity implements QRCodeView.Delegate {
    private static final String TAG = QrScanActivity.class.getSimpleName();
    private static final int CAMERAPERMISSION = 0;
    @InjectView(R.id.zxingview)
    ZXingView mQRCodeView;
    @InjectView(R.id.proBar)
    ProgressBar proBar;
    @InjectView(R.id.btn_nfcScan)
    Button btnNfcScan;
    @InjectView(R.id.btn_completeCheck)
    Button btnCompleteCheck;
    @InjectView(R.id.btn_light)
    Button btnLight;
    @InjectView(R.id.tv_bigQrNumber)
    TextView tvBigQrNumber;
    @InjectView(R.id.tv_smallQrNumber)
    TextView tvSmallQrNumber;
    @InjectView(R.id.rl_qrNumber)
    RelativeLayout rlQrNumber;
    private String ndIntent;
    private String checkIntent;
    private String exerciseIntent;
    private boolean isEnter = false;
    private String cardNumber;
    private String deviceId;
    /*用来记录是否获取到信息的变量*/
    private boolean isGetCkeckInfo = false;
    private int deviceCheckedNum;
    private String devicesNum;
    private static final int REQUESTSCAN = 1;
    private AlertDialog mDialog;

    @Override
    protected void initConfig() {
        super.initConfig();
        // 获取打开此Activity的意图对象携带的数据
        ndIntent = getIntent().getStringExtra(Keys.NEWDEVICE);
        checkIntent = getIntent().getStringExtra(Keys.CHECK);
        exerciseIntent = getIntent().getStringExtra(Keys.EXERCISE);
    }


    @Override
    protected int getContentViewId() {
        return R.layout.activity_qr_scan;
    }

    @Override
    protected void initViews() {
        // 设置扫描二维码的代理
        mQRCodeView.setDelegate(this);
        // 设置标题栏提示内容
        setTopBarDesc("检查");

        // 设置完成检查按钮显示的文字
        if (!TextUtils.isEmpty(ndIntent)) {
            setTopBarDesc("新增");
            btnCompleteCheck.setText("完成新增");
            proBar.setVisibility(View.GONE);
            rlQrNumber.setVisibility(View.GONE);
        } else if (!TextUtils.isEmpty(checkIntent)) {
            setTopBarDesc("检查");
            tvTopBarRight.setVisibility(View.VISIBLE);
            btnCompleteCheck.setText("完成检查");
        } else if (!TextUtils.isEmpty(exerciseIntent)) {
            setTopBarDesc("演练");
            btnCompleteCheck.setText("完成演练");
            proBar.setVisibility(View.GONE);
            rlQrNumber.setVisibility(View.GONE);

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
            proBar.setMax(Integer.parseInt(devicesNum));
            proBar.setProgress(Integer.parseInt(deviceCheckedNum));
            tvBigQrNumber.setText(deviceCheckedNum + "");
            tvSmallQrNumber.setText(devicesNum);
        }
    }

    @Override
    protected void onLoadDatas() {

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
                            proBar.setMax(Integer.parseInt(devicesNum));
                            proBar.setProgress(deviceCheckedNum);
                            tvBigQrNumber.setText(deviceCheckedNum + "");
                            tvSmallQrNumber.setText(devicesNum);
                        }
                    } else {
                        ToastUtils.showToast(mActivity, "bean为空");
                    }
                    break;
                case CAMERAPERMISSION:
                    mDialog.dismiss();
                    break;
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
        mQRCodeView.startSpotAndShowRect();
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        super.onDestroy();
    }

    /**
     * 震动
     */
    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        cardNumber = result;
        Log.i(TAG, "result:" + cardNumber);
        // 调用震动的方法
        vibrate();
        mQRCodeView.startSpot();
        // 拿到数据后做相应的操作
        processData(cardNumber);

    }

    /**
     * 拿到数据后做相应的操作
     */
    private void processData(String cardNumber) {
        // 刚进来这个方法就把isEnter直反
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
     * 这是检查的网络请求获取数据的方法，使用Post
     */
    private void getData() {
        progressBar.setVisibility(View.VISIBLE);
        String url = Urls.Url_Check;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("device_id", cardNumber);
        params.put("version", "1.1.0");
        OkHttpUtils.post()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new MyStringCallback());

    }


    class MyStringCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e) {

            System.out.println("QrScanActivity+++===没拿到数据" + e.getMessage());
        }

        @Override
        public void onResponse(String response) {
            //System.out.println("查询"+response);
            ScanBean bean = new Gson().fromJson(response, ScanBean.class);
            if (bean != null) {
                parseMessage(bean);
            } else {
                System.out.println("QrScanActivity+++===没获取bean对象");
            }
        }
    }


    /**
     * 拿到数据后判断设备ID是否绑定而跳转到相应界面
     */
    private void parseMessage(ScanBean scanBean) {

        progressBar.setVisibility(View.GONE);
        int isExist = scanBean.data.device_exist;//设备号是否在后台存在
        //设备ID
        deviceId = scanBean.data.device_id;
        if (isExist == 0) {
            // 跳转到新增页面
            if (TextUtils.equals(deviceId, cardNumber)) {
                Intent intent = new Intent(QrScanActivity.this, Add_Device_Activity.class);
                intent.putExtra(Keys.CARDNUMBER, cardNumber);
                startActivity(intent);

            } else {
                System.out.println("NFC_ScanActivity界面+++===deviceId和cardNumber不一样，这种情况应该不会发生");
            }

        } else if (isExist == 1) {
            // 跳转到修改页面
            Intent intent = new Intent(QrScanActivity.this, Check_Device_Activity.class);
            intent.putExtra(Keys.DEVICEINFO, scanBean);
            intent.putExtra(Keys.SCAN_TYPE, "qrscan");
            startActivityForResult(intent, REQUESTSCAN);
        } else {
            ToastUtils.showToast(mActivity, scanBean.msg);
        }
    }

    /**
     * 这是新增设备的方法
     */
    private void addNewDevice() {
        // 拿到扫描到的eDeviceId走添加设备借接口
        // 跳转到新增页面
        Intent intent = new Intent(QrScanActivity.this, Add_Device_Activity.class);
        intent.putExtra(Keys.CARDNUMBER, cardNumber);
        startActivity(intent);
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        //ToastUtils.showToast(mActivity,"请到设置里面打开权限");
        dialog();
        Log.e(TAG, "打开相机出错");
    }

    /*
     * Dialog对话框提示用户去设置界面打开权限
     */
    protected void dialog() {


        mDialog = new AlertDialog(mActivity);
        mDialog
                .builder()
                .setTitle("提示")
                .setMsg("前往系统设置的应用列表里打开安互保的相机权限？")
                .setCancelable(false)
                .setPositiveButton("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 打开系统设置界面
                        Intent localIntent = new Intent();
                        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (Build.VERSION.SDK_INT >= 9) {
                            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                        } else if (Build.VERSION.SDK_INT <= 8) {
                            localIntent.setAction(Intent.ACTION_VIEW);
                            localIntent.setClassName("com.android.settings","com.android.settings.InstalledAppDetails");
                            localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
                        }
                        startActivityForResult(localIntent,CAMERAPERMISSION);
                        /*Intent intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
                        startActivityForResult(intent, CAMERAPERMISSION);*/
                        mDialog.dismiss();
                    }
                }).setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        }).show();


    }

    private boolean isLight = false;

    @OnClick({R.id.btn_nfcScan, R.id.btn_completeCheck, R.id.btn_light})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_nfcScan:
                // 跳转到NFC扫描界面
                jumpNfcScan();
                break;
            case R.id.btn_completeCheck://完成检查
                finish();
                break;
            case R.id.tvTopBarRight:
                // 右上角列表
                startActivity(new Intent(mActivity, DeviceList.class));
                break;
            case R.id.btn_light:
                if (!isLight) {
                    mQRCodeView.openFlashlight();
                    btnLight.setBackgroundResource(R.drawable.light_close);
                } else {
                    mQRCodeView.closeFlashlight();
                    btnLight.setBackgroundResource(R.drawable.light_open);
                }
                isLight = !isLight;
                break;
        }
    }

    /**
     * 跳转到NFC扫描界面
     */
    private void jumpNfcScan() {
        if (!TextUtils.isEmpty(ndIntent)) {
            // 新增
            Intent intent = new Intent(mActivity, NfcScanActivity.class);
            intent.putExtra(Keys.NEWDEVICE, "newDevice");
            startActivity(intent);

        } else if (!TextUtils.isEmpty(checkIntent)) {
            // 检查
            Intent intentCheck = new Intent(mActivity, NfcScanActivity.class);
            intentCheck.putExtra(Keys.CHECK, "Check");
            startActivity(intentCheck);

        } else if (!TextUtils.isEmpty(exerciseIntent)) {
            // 演练
            Intent intentExercise = new Intent(mActivity, NfcScanActivity.class);
            intentExercise.putExtra(Keys.EXERCISE, "Exercise");
            startActivity(intentExercise);
        } else {

        }


    }


}