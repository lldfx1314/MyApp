package com.anhubo.anhubo.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 会根据宽，等比例缩放高的ImageView
 * @author dzl
 *
 */
public class RatioImageView extends ImageView {

	public RatioImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		MeasureSpec.UNSPECIFIED 未指定，可以滚动的容器会给它的子View的模式就是未指定
//		MeasureSpec.AT_MOST 最多,布局里面的wrap_content
//		MeasureSpec.EXACTLY 精确,布局里面的match_parent和填具体数值
//		int mode = MeasureSpec.getMode(widthMeasureSpec);	// 获取测量规格中的模式
//		int size = MeasureSpec.getSize(widthMeasureSpec);	// 获取测量规格中的大小
//		int measureSpec = MeasureSpec.makeMeasureSpec(200, MeasureSpec.EXACTLY);	// 创建一个测量规格
		
//		MeasureSpecUtil.printMeasureSpec(widthMeasureSpec, heightMeasureSpec);
		
		Drawable drawable = getDrawable();	// 获取到src属性指定的图片
		if (drawable != null) {
			int pictureRealWidth = drawable.getMinimumWidth();	// 获取图片真实的宽
			int pictureRealHeight = drawable.getMinimumHeight();// 获取图片真实的高
			// 最终要算什么值，则这个这值做为被除数
			float scale = (float) pictureRealHeight / pictureRealWidth;
			
			// 取出ImageView的宽
			int imageViewWidth = MeasureSpec.getSize(widthMeasureSpec);
			int imageViewHeight = (int) (imageViewWidth * scale);	// 按比例计算ImageView的高
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(imageViewHeight, MeasureSpec.EXACTLY);
		}
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	


}
