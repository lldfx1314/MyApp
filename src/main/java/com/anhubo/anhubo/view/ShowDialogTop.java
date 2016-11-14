package com.anhubo.anhubo.view;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;

import com.anhubo.anhubo.R;

public class ShowDialogTop {

	private Activity activity;
	private View view,cancelview;
	public ShowDialogTop(Activity activity, View view, View cancelview){
		this.activity = activity;
		this.view = view;
		this.cancelview = cancelview;
	}
	public Dialog show(){

		final Dialog dialog = new Dialog(activity, R.style.transparentFrameWindowStyle);
		dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		Window window = dialog.getWindow();
		// 设置显示动画
		window.setWindowAnimations(R.style.animstyle);
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.x = 0;
		// 设置dialog显示在上面
		wl.y = -activity.getWindowManager().getDefaultDisplay().getHeight();
		// 以下这两句是为了保证按钮可以水平满屏
		wl.width = LayoutParams.MATCH_PARENT;
		wl.height = LayoutParams.MATCH_PARENT;

		// 设置显示位置
		dialog.onWindowAttributesChanged(wl);
		// 设置点击外围解散
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

		cancelview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		return dialog;
	}
	

}
