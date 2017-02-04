package com.anhubo.anhubo.view;

import android.app.Activity;
import android.app.Dialog;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;

import com.anhubo.anhubo.R;
/**
 * Created by LUOLI on 2016/10/16.
 */
public class ShowCheckDeviceDialog {

    private Activity activity;
    private View view;

    public ShowCheckDeviceDialog(Activity activity, View view) {
        this.activity = activity;
        this.view = view;
    }

    public Dialog show() {

        final Dialog dialog = new Dialog(activity, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        Display defaultDisplay = window.getWindowManager().getDefaultDisplay();
        // 获取屏幕的宽度和高度
        int screenWidth = defaultDisplay.getWidth();
        int screenHeight = defaultDisplay.getHeight();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        // 设置显示位置
        window.setGravity(Gravity.LEFT | Gravity.BOTTOM);
		// 以下这两句是为了保证按钮可以水平满屏
		wl.width = (int) (screenWidth * 0.8);    //宽度设置为屏幕的0.8
		wl.height = LayoutParams.WRAP_CONTENT;
        // 计算对话框距离屏幕左边的距离
        int x = screenWidth / 2 - (int) (screenWidth * 0.8) / 2;
        wl.x = x;
        wl.y = 100;
		// 设置显示位置
		dialog.onWindowAttributesChanged(wl);
		// 设置点击外围解散
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
        boolean showing = dialog.isShowing();
        if(showing&&listenerDialog!=null){
            listenerDialog.popup();
        }


        return dialog;
    }

    public void setListenerDialog(ClickListenerDialog listenerDialog) {
        this.listenerDialog = listenerDialog;
    }

    private ClickListenerDialog listenerDialog;




    public interface ClickListenerDialog{
        // 显示
        void popup();
    }
}
