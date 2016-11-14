package com.anhubo.anhubo.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * 这是图片压缩的工具类
 * Created by Administrator on 2016/10/18.
 */
public class ImageTools {
    public static Bitmap zoomBitmap(Bitmap photo, int i, int j) {
        // 获取这个图片的宽和高
        float width = photo.getWidth();
        float height = photo.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) i) / width;
        float scaleHeight = ((float) j) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(photo, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }
    public static Bitmap bigBitmap(Bitmap photo, int i, int j) {
        // 获取这个图片的宽和高
        float width = photo.getWidth();
        float height = photo.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) i) * width;
        float scaleHeight = ((float) j) * height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(photo, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }
}
