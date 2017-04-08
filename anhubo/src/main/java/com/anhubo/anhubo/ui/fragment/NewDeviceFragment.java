package com.anhubo.anhubo.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseFragment;
import com.anhubo.anhubo.ui.activity.unitDetial.Add_Device_Activity;
import com.anhubo.anhubo.ui.activity.unitDetial.NfcScanActivity;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.view.AlertDialog;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

/**
 * Created by LUOLI on 2017/3/1.
 */
public class NewDeviceFragment extends BaseFragment implements QRCodeView.Delegate {
    private static final String TAG = "NewDeviceFragment";
    ZXingView mQRCodeView;
    Button btnNfcScan;
    Button btnCompleteCheck;
    Button btnLight;
    private String cardNumber;
    private boolean isPermission;

    @Override
    public void initTitleBar() {

    }

    @Override
    public Object getContentView() {
        return R.layout.newdevice_fragment;
    }

    @Override
    public void initView() {
        llTop.setVisibility(View.GONE);
        mQRCodeView = findView(R.id.zxingview_bind_qrcode);
        btnNfcScan = findView(R.id.btn_nfcScan_bind_qrcode);
        btnCompleteCheck = findView(R.id.btn_completeCheck_bind_qrcode);
        btnLight = findView(R.id.btn_light_bind_qrcode);
        // 设置扫描二维码的代理
        mQRCodeView.setDelegate(this);

        btnCompleteCheck.setText("完成新增");
    }

    @Override
    public void initListener() {
        btnNfcScan.setOnClickListener(this);
        btnLight.setOnClickListener(this);
        btnCompleteCheck.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void processClick(View view) {

    }

//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        super.onHiddenChanged(hidden);
//        if(hidden){
//            mQRCodeView.stopCamera();
//            mQRCodeView.onDestroy();
//            LogUtils.i(TAG,"关权限");
//        }else{
//            mQRCodeView = findView(R.id.zxingview_bind_qrcode);
//            mQRCodeView.startCamera();
//            mQRCodeView.startSpotAndShowRect();
//            LogUtils.i(TAG,"开权限");
//        }
//    }

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
            // 拿到数据后做相应的操作
            processData(cardNumber);
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
     * 拿到数据后做相应的操作
     */
    private void processData(String cardNumber) {
        if (!TextUtils.isEmpty(cardNumber)) {
            /***********新增**************************/
            addNewDevice();
//            ToastUtils.showToast(mActivity,"这是新增设备");

        }
    }

    /**
     * 这是新增设备的方法
     */
    private void addNewDevice() {
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

    /**
     * 点击事件
     */
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_nfcScan_bind_qrcode:
                // 跳转到NFC扫描界面
                jumpNfcScan();
                break;
            case R.id.btn_completeCheck_bind_qrcode://完成检查
//                mActivity.finish();
                break;
            case R.id.btn_light_bind_qrcode:
                // 开关灯
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
        // 新增
        Intent intent = new Intent(mActivity, NfcScanActivity.class);
        intent.putExtra(Keys.NEWDEVICE, "newDevice");
        startActivity(intent);
    }
}
