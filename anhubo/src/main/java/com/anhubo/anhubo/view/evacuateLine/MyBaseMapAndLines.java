package com.anhubo.anhubo.view.evacuateLine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * 自定义 地图 地图
 * Created by Administrator on 2016/6/30.
 */
public class MyBaseMapAndLines extends ImageView {

    // 线 坐标
    public ArrayList<MapLineCoord> mapLineCoords;

    public MyBaseMapAndLines(Context context) {
        super(context);
        mapLineCoords = new ArrayList<>();
    }

    public MyBaseMapAndLines(Context context, AttributeSet attrs) {
        super(context, attrs);
        mapLineCoords = new ArrayList<>();
    }

    public ArrayList<MapLineCoord> getMapLineCoords() {
        return mapLineCoords;
    }

    public int getLineSize() {
        return mapLineCoords.size();
    }

    public void clearLines() {
        mapLineCoords.clear();
    }

    public void addLines(ArrayList<MapLineCoord> mapLineCoords) {
        this.mapLineCoords.addAll(mapLineCoords);
    }

    public MapLineCoord getLine(int index) {
        return mapLineCoords.get(index);
    }

    public void addLine(MapLineCoord mapLineCoord) {
        mapLineCoords.add(mapLineCoord);
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mapLineCoords == null || mapLineCoords.size() <= 0)
            return;
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth((float) 5.0);
        canvas.drawCircle(mapLineCoords.get(0).getViewX(), mapLineCoords.get(0).getViewY(), 10, paint);
        canvas.drawCircle(mapLineCoords.get(mapLineCoords.size() - 1).getViewX(),
                mapLineCoords.get(mapLineCoords.size() - 1).getViewY(), 10, paint);
        // 划线
        for (int i = 1; i < mapLineCoords.size(); i++) {
            canvas.drawLine(mapLineCoords.get(i - 1).getViewX(),
                    mapLineCoords.get(i - 1).getViewY(),
                    mapLineCoords.get(i).getViewX(), mapLineCoords.get(i).getViewY(), paint);
        }
    }

    /**
     * 地图 线 拐点 坐标
     */
    public class MapLineCoord {
        private float firstX;
        private float firstY;

        private float viewX;
        private float viewY;

        public MapLineCoord() {
        }

        public MapLineCoord(float firstX, float firstY, float viewX, float viewY) {
            this.firstX = firstX;
            this.firstY = firstY;
            this.viewX = viewX;
            this.viewY = viewY;
        }

        public float getFirstX() {
            return firstX;
        }

        public void setFirstX(float firstX) {
            this.firstX = firstX;
        }

        public float getFirstY() {
            return firstY;
        }

        public void setFirstY(float firstY) {
            this.firstY = firstY;
        }

        public float getViewX() {
            return viewX;
        }

        public void setViewX(float viewX) {
            this.viewX = viewX;
        }

        public float getViewY() {
            return viewY;
        }

        public void setViewY(float viewY) {
            this.viewY = viewY;
        }

        @Override
        public String toString() {
            return "MapLineCoord{" +
                    "firstX=" + firstX +
                    ", firstY=" + firstY +
                    ", viewX=" + viewX +
                    ", viewY=" + viewY +
                    '}';
        }
    }
}
