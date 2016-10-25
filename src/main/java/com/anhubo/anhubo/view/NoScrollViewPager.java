package com.anhubo.anhubo.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NoScrollViewPager extends ViewPager {
	/**
	 * 在布局里面使用时调用
	 * @param context
	 * @param attrs
	 */
	public NoScrollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// 屏蔽父类的方法
		return false;
	}
	/**
	 * 复写父类的拦截方法
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// 干掉ViewPager默认的拦截事件
		return false;//自己不拦截孩子的事件
	}
	
}
