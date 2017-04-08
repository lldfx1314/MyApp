package com.anhubo.anhubo.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.widget.EditText;
import android.widget.TextView;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 这个类存放各个方法的类
 * Created by Administrator on 2016/9/28.
 */
public class Utils {


    private SpannableString ss;



    /**
     * 判断手机号码是否合法
     */
    public static boolean judgePhoneNumber(String str) {
//        String regular_phone_number = "^[1]([3][0-9]{1}|45|47|50|51|52|53|55|56|57|58|59|70|71|76|77|78|80|81|82|83|84|85|86|87|88|89)[0-9]{8}$";
        String regular_phone_number = "^1[34578]\\d{9}$";

        //Pattern p = Pattern.compile("^(13[0-9]|14[57]|15[0-35-9]|17[6-8]|18[0-9])[0-9]{8}$");
        Pattern p = Pattern.compile(regular_phone_number);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 验证注册密码是否正确
     */
    public static final boolean isRightPwd(String pwd) {
        Pattern p = Pattern.compile("^(?![^a-zA-Z]+$)(?!\\D+$)[0-9a-zA-Z]{8,16}$");
        Matcher m = p.matcher(pwd);
        return m.matches();

    }
    /**
     * 验证登录密码是否正确
     */
    public static final boolean isRightLoginPwd(String pwd) {
        Pattern p = Pattern.compile("^\\w{8}(,\\w{8})*$");
        Matcher m = p.matcher(pwd);
        return m.matches();

    }

    /**
     * 身份证是否正确
     */
    public static final boolean isRightIdcard(String pwd) {
        String regular_Idcard = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$|^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$";
        Pattern p = Pattern.compile(regular_Idcard);
        Matcher m = p.matcher(pwd);
        return m.matches();

    }

    /**
     * 更改获取验证码的TextView显示的内容
     */
    public static void setSecurityTextView(final TextView textView) {
        CountDownTimer countDownTimer = new CountDownTimer(60 * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                // mTextField.setText("seconds remaining: " + TimeUtils.formatDuration((int)millisUntilFinished));
                textView.setText(millisUntilFinished / 1000 + "秒");
                textView.setTextColor(Color.parseColor("#7393f4"));
                textView.setEnabled(false);
            }

            public void onFinish() {
                textView.setText("重新获取");
                textView.setEnabled(true);
            }
        };
        countDownTimer.start();
    }

    /**
     * 设置下划线
     * 利用可变参数
     */
    public static void setUnderline(TextView... tv) {
        for (int i = 0; i < tv.length; i++) {
            tv[i].getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
            tv[i].getPaint().setAntiAlias(true);//抗锯齿
        }
    }


    /**
     * 获取版本信息
     */
    public static String getAppInfo(Context context) {
        try {
            String pkName = context.getPackageName();
            String versionName = context.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
            int versionCode = context.getPackageManager()
                    .getPackageInfo(pkName, 0).versionCode;
            return pkName + "#" + versionName + "#" + versionCode;
        } catch (Exception e) {
        }
        return null;
    }

    public static String getDeviceId(Context context) {
        String deviceId = "";

        if (deviceId == null || "".equals(deviceId)) {
            try {
                deviceId = getLocalMac(context).replace(":", "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (deviceId == null || "".equals(deviceId)) {
            try {
                deviceId = getAndroidId(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (deviceId == null || "".equals(deviceId)) {

            if (deviceId == null || "".equals(deviceId)) {
                UUID uuid = UUID.randomUUID();
                deviceId = uuid.toString().replace("-", "");
            }
        }
        return deviceId;
    }
    // IMEI码
    private static String getIMIEStatus(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        return deviceId;
    }

    // Mac地址
    private static String getLocalMac(Context context) {
        WifiManager wifi = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    // Android Id
    private static String getAndroidId(Context context) {
        String androidId = Settings.Secure.getString(
                context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }

    /**把光标移动到指定输入框*/
    public static void setEditTextSelection(EditText et) {
        // 把焦点移动到验证码的输入框
        et.requestFocus();
        // 设置光标
        et.setSelection(0);
    }


}
