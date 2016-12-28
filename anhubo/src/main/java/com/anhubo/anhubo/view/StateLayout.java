package com.anhubo.anhubo.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.anhubo.anhubo.R;

/**
 * Created by LUOLI on 2016/11/02.
 * 状态布局，封装了4种状态：正在加载、加载失败、加载为空、正常界面
 * 使用{@link #newInstance(Context, Object)} 来创建StateLayout的实现
 */

public class StateLayout extends FrameLayout {

	private View loadingView;
	private View failView;
	private View emptyView;
	private View contentView;

	public StateLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	/**
	 * 创建一个StateLayout实现
	 * @param layoutIdOrView 可以传一个布局id，也可以传一个View
	 */
	public static StateLayout newInstance(Context context, Object layoutIdOrView) {
		StateLayout stateLayout = (StateLayout) View.inflate(context, R.layout.state_layout, null);
		// StateLayout inflate之后就有3个状态的View了，还需要第四种状态
		stateLayout.setContentView(layoutIdOrView);
		return stateLayout;
	}
	
	/**
	 * 设置正常界面的View
	 * @param layoutIdOrView 可以传一个布局id，也可以传一个View
	 */
	public void setContentView(Object layoutIdOrView) {
		if (layoutIdOrView instanceof Integer) {	// 如果是一个布局id
			int layoutId = (Integer) layoutIdOrView;
			contentView = View.inflate(getContext(), layoutId, null);
		} else {	// layoutIdOrView就是一个View
			contentView = (View) layoutIdOrView;
		}
		super.addView(contentView);	// 把正常界面的View设置到状态布局中
		contentView.setVisibility(View.GONE);	//  默认显示LoadingView
	}
	
	
	/**
	 * 把xml中的View填充成Java对象的View
	 */
	@SuppressLint("MissingSuperCall")
	@Override
	protected void onFinishInflate() {
		loadingView = findViewById(R.id.loadingView);
		failView = findViewById(R.id.failView);
		emptyView = findViewById(R.id.emptyView);
		showLoadingView();
	}
	
	/** 显示正在加载的View */
	public void showLoadingView() {
		showView(loadingView);
	}
	
	/** 显示失败的View */
	public void showFailView() {
		showView(failView);
	}
	
	/** 显示加载为空的View */
	public void showEmptyView() {
		showView(emptyView);
	}
	
	/** 显示正常界面的View */
	public void showContentView() {
		showView(contentView);
	}

	/** 
	 * 显示指定的View，并且隐藏其它的View
	 * @param view 指定要显示的View
	 */
	private void showView(View view) {
		// getChildCount() 获取子孩子的数量
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);	// 获取子孩子
			child.setVisibility(view == child ? View.VISIBLE : View.GONE);
		}
	}
	
}
