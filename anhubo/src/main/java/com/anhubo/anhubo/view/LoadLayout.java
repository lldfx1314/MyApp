package com.anhubo.anhubo.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.anhubo.anhubo.R;

/**
 * Created by LUOLI on 2016/11/16.
 */
public class LoadLayout {
    private Display display;
    private Context context;
    private Dialog dialog;
    private RelativeLayout rlProgress;
    private RelativeLayout rlLoading;

    public LoadLayout(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public LoadLayout builder() {
        /// 获取Dialog布局
        /// 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(
                R.layout.loading, null);
        rlProgress = (RelativeLayout) view.findViewById(R.id.rl_progress);
        rlLoading = (RelativeLayout) view.findViewById(R.id.rl_loading);
        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);

        // 调整dialog背景大小
        rlLoading.setLayoutParams(new FrameLayout.LayoutParams((int) (display
                .getWidth() * 0.85), LinearLayout.LayoutParams.WRAP_CONTENT));
        return this;
    }

    public void show() {
        dialog.show();
    }
    public void dismiss() {
        dialog.dismiss();
    }

}
