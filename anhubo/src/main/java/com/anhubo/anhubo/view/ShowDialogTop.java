package com.anhubo.anhubo.view;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.utils.DisplayUtil;

/**
 * Created by LUOLI on 2016/10/13.
 */
public class ShowDialogTop {

	private Activity activity;
	private View view;
	public ShowDialogTop(Activity activity, View view){
		this.activity = activity;
		this.view = view;
	}
	public Dialog show(){

		final Dialog dialog = new Dialog(activity, R.style.bottom_dialog_perfect_mag_Style);
		dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		Window window = dialog.getWindow();
		// 设置显示动画
		window.setWindowAnimations(R.style.main_menu_animstyle);
		WindowManager.LayoutParams wl = window.getAttributes();
		window .setGravity(Gravity.LEFT | Gravity.TOP);
		wl.x = 0;
		// 设置dialog显示在上面
//		wl.y = -activity.getWindowManager().getDefaultDisplay().getHeight();
		wl.y = 0;
		// 以下这两句是为了保证按钮可以水平满屏
		wl.width = LayoutParams.MATCH_PARENT;
		wl.height = LayoutParams.WRAP_CONTENT;

		// 设置显示位置
		dialog.onWindowAttributesChanged(wl);
//		window .setAttributes(wl);
		dialog.show();
		// 设置点击外围解散
//		dialog.setCanceledOnTouchOutside(false);
		return dialog;
	}
	

}
