package com.anhubo.anhubo.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * 有新的Toast请求时，会马上显示新的Toast内容
 */
public class ToastUtils {
    private static Toast toast;
    /**短时吐司*/
    public static void showToast(Context context,String text){
        if (toast==null){
            toast=Toast.makeText(context,"",Toast.LENGTH_SHORT);
        }
        toast.setText(text);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }
    /**长时吐司*/
    public static void showLongToast(Context context,String text){
        if (toast==null){
            toast=Toast.makeText(context,"",Toast.LENGTH_LONG);
        }
        toast.setText(text);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }
}
