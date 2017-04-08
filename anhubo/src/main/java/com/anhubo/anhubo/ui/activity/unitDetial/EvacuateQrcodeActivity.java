package com.anhubo.anhubo.ui.activity.unitDetial;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.base.BaseActivity;
import com.anhubo.anhubo.utils.Keys;
import com.anhubo.anhubo.utils.LogUtils;
import com.anhubo.anhubo.utils.ToastUtils;
import com.anhubo.anhubo.view.AlertDialog;

import butterknife.InjectView;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

/**
 * Created by LUOLI on 2017/3/7.
 */
public class EvacuateQrcodeActivity extends BaseActivity implements QRCodeView.Delegate {
    private static final String TAG = "EvacuateQrcodeActivity";
    public static final String CARDNUMBER = "cardNumber";
    @InjectView(R.id.zxingview_evacuate_qrcode)
    ZXingView mQRCodeView;
    @InjectView(R.id.btn_light_evacuate_qrcode)
    Button btnLightEvacuateQrcode;
    private String cardNumber;
    private boolean isPermission;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_evacuate_qrcode;
    }

    @Override
    protected void initViews() {
        setTopBarDesc("疏散");
// 设置扫描二维码的代理
        mQRCodeView.setDelegate(this);
    }

    @Override
    protected void onLoadDatas() {

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
//            ToastUtils.showToast(mActivity,cardNumber);
            Intent intent = new Intent();
            intent.putExtra(CARDNUMBER,cardNumber);
            setResult(1,intent);
            finish();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_light_bind_qrcode:
                // 开关灯
                if (!isLight) {
                    mQRCodeView.openFlashlight();
                    btnLightEvacuateQrcode.setBackgroundResource(R.drawable.light_close);
                } else {
                    mQRCodeView.closeFlashlight();
                    btnLightEvacuateQrcode.setBackgroundResource(R.drawable.light_open);
                }
                isLight = !isLight;
                break;
        }
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {

    }
}
