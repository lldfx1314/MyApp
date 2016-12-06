package com.anhubo.anhubo.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.view.ShowBottonDialog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 这个类存放各个方法的类
 * Created by Administrator on 2016/9/28.
 */
public class Utils {

    private static Dialog dialog;
    private static Button btnTakephoto;
    private static Button btnPhoto;

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
     * 验证密码是否正确
     */
    public static final boolean isRightPwd(String pwd) {
        Pattern p = Pattern.compile("^(?![^a-zA-Z]+$)(?!\\D+$)[0-9a-zA-Z]{8,16}$");
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

    /** 把dp单位的值转换为px单位的值 */
   /* public static int dp2px(int dp) {
        // 获取手机的屏幕密度，不同手机的屏幕密度可能不一样
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5);	// 加0.5是为了把结果四舍五入
    }*/

}
