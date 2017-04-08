package com.anhubo.anhubo.ui.activity.unitDetial;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.ui.fragment.AreaBindFragment;
import com.anhubo.anhubo.ui.fragment.NewDeviceFragment;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.view.AlertDialog;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

/**
 * Created by LUOLI on 2017/3/1.
 */
public class BindQrcodeActivity extends BaseActivity implements QRCodeView.Delegate {
    private static final String TAG = "BindQrcodeActivity";
    @InjectView(R.id.btn_light_bind_qrcode)
    Button btnLightBindQrcode;
    @InjectView(R.id.btn_nfcScan_nfc)
    Button btnNfcScanNfc;
    @InjectView(R.id.btn_completeCheck)
    Button btnCompleteCheck;
    @InjectView(R.id.btn_bindqrcode)
    Button btnNfcScanAreabind;
    @InjectView(R.id.zxingview_bind_qrcode)
    ZXingView mQRCodeView;
    private String cardNumber;
    private boolean isPermission;

    @Override
    protected void initConfig() {
        super.initConfig();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_bind_qrcode;
    }

    @Override
    protected void initViews() {
        rlIndicator.setVisibility(View.VISIBLE);
        viewIndicatorAddDevice.setVisibility(View.VISIBLE);
        // 隐藏标题栏
        tvToptitle.setVisibility(View.GONE);
        // 设置扫描二维码的代理
        mQRCodeView.setDelegate(this);
    }

    @Override
    protected void onLoadDatas() {
        tvIndicatorAdddevice.setOnClickListener(this);
        tvIndicatorArea.setOnClickListener(this);
        btnLightBindQrcode.setOnClickListener(this);
        btnNfcScanNfc.setOnClickListener(this);
        btnCompleteCheck.setOnClickListener(this);
        btnNfcScanAreabind.setOnClickListener(this);
    }

    @Override
    protected void initEvents() {
        super.initEvents();
        // 初始化添加新增设备
//        addFragment1();
    }

    @Override
    public void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
        mQRCodeView.startSpotAndShowRect();
        LogUtils.i(TAG, "第一次开权限");
    }


    @Override
    public void onStop() {
        super.onStop();
        mQRCodeView.stopCamera();
        LogUtils.i(TAG, "关闭权限");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mQRCodeView.onDestroy();
    }

    /**
     * 震动
     */
    private void vibrate() {
        Vibrator vibrator = (Vibrator) mActivity.getSystemService(mActivity.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        boolean isanhuboCard = result.startsWith("anhubo", 0);
        boolean isAHBCard = result.startsWith("AHB", 0);
        if (isanhuboCard || isAHBCard) {
            cardNumber = result;
            Log.i(TAG, "result:" + cardNumber);
            // 调用震动的方法
            vibrate();
            mQRCodeView.stopSpot();
            // 拿到数据后做相应的操作
            if (count == 1) {
                addNewDevice(cardNumber);

            } else if (count == 2) {
                jumpAreaBinding(cardNumber);
            }
        } else {
            AlertDialog builder = new AlertDialog(mActivity).builder();
            builder
                    .setTitle("提示")
                    .setMsg("请使用安互保专用码")
                    .setPositiveButton("确认", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mQRCodeView.startSpot();
                        }
                    })
                    .setCancelable(false).show();

        }


    }


    /**
     * 这是新增设备的方法
     */
    private void addNewDevice(String cardNumber) {
        // 跳转到新增页面
        Intent intent = new Intent(mActivity, Add_Device_Activity.class);
        intent.putExtra(Keys.CARDNUMBER, cardNumber);
        startActivity(intent);
    }

    /**
     * 打开相机失败的回调
     */
    @Override
    public void onScanQRCodeOpenCameraError() {
        isPermission = !isPermission;
        if (isPermission) {
            // Dialog对话框提示用户去设置界面打开权限
            dialog();
        }
        //Log.e(TAG, "打开相机出错");
    }

    /**
     * Dialog对话框提示用户去设置界面打开权限
     */
    protected void dialog() {

        new AlertDialog(mActivity).builder()
                .setTitle("提示")
                .setMsg("前往系统设置的应用列表里打开安互保的相机权限？")
                .setCancelable(false)
                .setPositiveButton("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 打开系统设置界面
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (Build.VERSION.SDK_INT >= 9) {
                            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                            intent.setData(Uri.fromParts("package", mActivity.getPackageName(), null));
                        } else if (Build.VERSION.SDK_INT <= 8) {
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
                            intent.putExtra("com.android.settings.ApplicationPkgName", mActivity.getPackageName());
                        }
                        startActivity(intent);

                    }
                }).setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }).show();
    }

    // 记录灯是否打开
    private boolean isLight = false;
    private int count = 1;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_Indicator_adddevice:
//                addFragment1();
                count = 1;
                viewIndicatorAddDevice.setVisibility(View.VISIBLE);
                viewIndicatorArea.setVisibility(View.GONE);
                btnCompleteCheck.setVisibility(View.VISIBLE);
                btnNfcScanNfc.setVisibility(View.VISIBLE);
//                btnNfcScanAreabind.setVisibility(View.GONE);
                break;
            case R.id.tv_Indicator_area:
//                addFragment2();
                count = 2;
                viewIndicatorAddDevice.setVisibility(View.GONE);
                viewIndicatorArea.setVisibility(View.VISIBLE);
                btnCompleteCheck.setVisibility(View.GONE);
                btnNfcScanNfc.setVisibility(View.GONE);
//                btnNfcScanAreabind.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_nfcScan_nfc:
                // 跳转到nfc界面
                jumpNfcScan();
                break;
            case R.id.btn_bindqrcode:
                // 跳转到区域绑定界面
                jumpAreaBinding("");
                break;
            case R.id.btn_completeCheck://完成检查
                finish();
                break;
            case R.id.btn_light_bind_qrcode:
                // 开关灯
                if (!isLight) {
                    mQRCodeView.openFlashlight();
                    btnLightBindQrcode.setBackgroundResource(R.drawable.light_close);
                } else {
                    mQRCodeView.closeFlashlight();
                    btnLightBindQrcode.setBackgroundResource(R.drawable.light_open);
                }
                isLight = !isLight;
                break;
        }
    }

    /**
     * 跳转到NFC扫描界面
     */
    private void jumpAreaBinding(String cardNumber) {
        // 新增
        Intent intent = new Intent(mActivity, AreaBindingActivity.class);
        intent.putExtra(Keys.CARDNUMBER, cardNumber);
        startActivity(intent);
    }

    /**
     * 跳转到NFC扫描界面
     */
    private void jumpNfcScan() {
        // 新增
        Intent intent = new Intent(mActivity, NfcScanActivity.class);
        intent.putExtra(Keys.NEWDEVICE, "newDevice");
        startActivity(intent);
    }

    private void addFragment2() {
        if (isAddFragment("fragment1")) {
            LogUtils.i(TAG, "fragment1移除");
            removeFragment("fragment1");
        }
        AreaBindFragment fragment2 = new AreaBindFragment();
        LogUtils.i(TAG, "fragment2添加");
        addFragment(fragment2, "fragment2");
        viewIndicatorAddDevice.setVisibility(View.GONE);
        viewIndicatorArea.setVisibility(View.VISIBLE);
    }

    private void addFragment1() {
        if (isAddFragment("fragment2")) {
            LogUtils.i(TAG, "fragment2移除");
            removeFragment("fragment2");
        }
        NewDeviceFragment fragment1 = new NewDeviceFragment();
        LogUtils.i(TAG, "fragment1添加");
        addFragment(fragment1, "fragment1");

        viewIndicatorAddDevice.setVisibility(View.VISIBLE);
        viewIndicatorArea.setVisibility(View.GONE);
    }

    private void addFragment(Fragment fragment, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.fragment_container, fragment, tag);
        transaction.commit();
    }

    private void removeFragment(String tag) {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag(tag);
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.remove(fragment);
        transaction.commit();
    }

    private boolean isAddFragment(String tag) {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag(tag);
        if (fragment != null) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }
}
