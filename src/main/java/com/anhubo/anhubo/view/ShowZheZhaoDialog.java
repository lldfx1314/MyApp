package com.anhubo.anhubo.view;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.anhubo.anhubo.R;

/**
 * Created by LUOLI on 2016/10/25.
 */
public class ShowZheZhaoDialog {

	private Activity activity;
	private View view;
	private Dialog dialog;

	public ShowZheZhaoDialog(Activity activity, View view){
		this.activity = activity;
		this.view = view;
	}
	public Dialog show(){
		dialog = new Dialog(activity);
//		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
		dialog.setContentView(view);
		Window window = dialog.getWindow();
		WindowManager.LayoutParams wl = window.getAttributes();
		window.setGravity(Gravity.LEFT | Gravity.TOP);
        Display d = activity.getWindowManager().getDefaultDisplay(); // 获取屏幕宽
		int screenWidth = d.getWidth();
		wl.width = (int) (screenWidth * 0.3);
		wl.height = LayoutParams.WRAP_CONTENT;
		wl.x = screenWidth / 2 - (int) (screenWidth * 0.3) / 2;
		wl.y = 10;
//		wl.alpha = 5f;


		// 设置显示位置
		dialog.onWindowAttributesChanged(wl);
		dialog.setCancelable(false);// 不可以用“返回键”取消
		// 设置点击外围解散
//		dialog.setCanceledOnTouchOutside(false);
		dialog.show();


		return dialog;
	}

	public void dismiss(){
		dialog.dismiss();
	}

}
