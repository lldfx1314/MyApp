package com.anhubo.anhubo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by LUOLI on 2017/3/6.
 */
public class MyPhotoView extends PhotoView {
    public MyPhotoView(Context context) {
        this(context,null);
    }

    public MyPhotoView(Context context, AttributeSet attr) {
        this(context,attr,0);
    }

    public MyPhotoView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        super.setOnLongClickListener(l);
        if(onLongPressClickListener!=null){
//            onLongPressClickListener.LongPressClick();
        }

    }

    public void setOnLongPressClickListener(OnLongPressClickListener onLongPressClickListener) {
        this.onLongPressClickListener = onLongPressClickListener;
    }

    public OnLongPressClickListener getOnLongPressClickListener() {
        return onLongPressClickListener;
    }

    private OnLongPressClickListener onLongPressClickListener;
    public interface OnLongPressClickListener{
        void LongPressClick(float X,float Y);
    }
}
