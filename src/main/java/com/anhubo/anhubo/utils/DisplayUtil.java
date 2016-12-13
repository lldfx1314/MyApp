package com.anhubo.anhubo.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.anhubo.anhubo.MyApp;

import java.util.Random;

/**
 * dp(dip): device independent pixels(设备独立像素). 不同设备有不同的显示效果,这个和设备硬件有关，一般我们为了支持WVGA、HVGA和QVGA 推荐使用这个，不依赖像素。
 * dp也就是dip，这个和sp基本类似。如果设置表示长度、高度等属性时可以使用dp 或sp。但如果设置字体，需要使用sp。dp是与密度无关，sp除了与密度无关外，还与scale无关。如果屏幕密度为160，这时dp和sp和px是一 样的。1dp=1sp=1px，但如果使用px作单位，如果屏幕大小不变（假设还是3.2寸），而屏幕密度变成了320。那么原来TextView的宽度
 * 设成160px，在密度为320的3.2寸屏幕里看要比在密度为160的3.2寸屏幕上看短了一半。但如果设置成160dp或160sp的话。系统会自动 将width属性值设置成320px的。也就是160 * 320 / 160。其中320 / 160可称为密度比例因子。也就是说，如果使用dp和sp，系统会根据屏幕密度的变化自动进行转换。
 * px: pixels(像素). 不同设备显示效果相同，一般我们HVGA代表320x480像素，这个用的比较多。
 * pt: point，是一个标准的长度单位，1pt＝1/72英寸，用于印刷业，非常简单易用；
 * sp: scaled pixels(放大像素). 主要用于字体显示best for textsize。
 */

/**
 * dp、sp 转换为 px 的工具类
 */
public class DisplayUtil {
    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue （DisplayMetrics类中属性density）
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue （DisplayMetrics类中属性density）
     * @return
     */
    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取Resources对象
     */
    public static Resources getResources() {
        return MyApp.getContext().getResources();
    }

    /**
     * 根据id获取vulues/strings.xml中的字符串
     */
    public static String getString(int id) {
        return getResources().getString(id);
    }

    /**
     * 根据资源id获取res下的图片资源
     */
    public static Drawable getDrawable(int id) {
        return getResources().getDrawable(id);
    }

    /**
     * 根据资源id获取res/values/colors.xml文件中的Color
     */
    public static int getColor(int id) {
        return getResources().getColor(id);
    }


    /** 创建一个随机颜色 */
    public static int createRandomColor() {
        Random random = new Random();
        int red = 50 + random.nextInt(151);		// 50 ~ 200
        int green = 50 + random.nextInt(151);	// 50 ~ 200
        int blue = 50 + random.nextInt(151);	// 50 ~ 200
        int color = Color.rgb(red, green, blue);
        return color;
    }

    /**
     * 创建一个TextView
     * @return
     */
    public static TextView createTextView(Context context) {
        final TextView textView = new TextView(context);
        textView.setTextColor(Color.parseColor("#5e84ff"));
        textView.setTextSize(12);
        textView.setPadding(6, 6, 6, 6);
        textView.setGravity(Gravity.CENTER);

        textView.setBackgroundDrawable(createDrawableShape());
        textView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //ToastUtils.showToast(v.getContext(), (String) textView.getText());
            }
        });
        return textView;
    }

    /**
     * 创建一个自定义背景的Drawable
     * @return
     */
    public static Drawable createDrawableShape() {
        StateListDrawable stateListDrawable = new StateListDrawable();	// 创建一个选择器对象
        // 创建一个按下状态和按下状态对应的图片
//        int[] pressState = {android.R.attr.state_pressed, android.R.attr.state_enabled};
//        Drawable pressDrawable = createShape();

        // 创建一个正常状态和正常状态对应的图片
        int[] normalState = {};
        Drawable normalDrawable = createShape();

//        stateListDrawable.addState(pressState, pressDrawable);	// 按下状态显示按下的Drawable
        stateListDrawable.addState(normalState, normalDrawable);// 正常状态显示正常的Drawable
        return stateListDrawable;
    }

    /**
     * 创建一个的图形
     * @return
     */
    public static Drawable createShape() {
        GradientDrawable gradientDrawable = new GradientDrawable();	 // 创建一个图形Drawable
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);		 // 设置图形为矩形
        gradientDrawable.setCornerRadius(6);	// 设置矩形的圆角
        gradientDrawable.setStroke(dp2px(MyApp.getContext(),1),Color.parseColor("#5e84ff"));  //设置边框颜色
        gradientDrawable.setColor(Color.argb(100,230,230,230));	    // 设置矩形的颜色
        return gradientDrawable;
    }
}
