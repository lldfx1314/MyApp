package com.anhubo.anhubo.view;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.anhubo.anhubo.bean.Panel;
import com.anhubo.anhubo.bean.SesameItemModel;
import com.anhubo.anhubo.bean.SesameModel;
import com.anhubo.anhubo.utils.DisplayUtil;

import java.util.ArrayList;

/**
 * Created by LUOLI on 2016/10/12.
 * 弧形进度条
 */
public class SesameCreditPanel extends View {
    /**
     * 仪表盘
     */
    private Paint panelPaint;
    /**
     * 仪表盘文本
     */
    private Paint panelTextPaint;

    /**
     * 进度条的画笔
     */
    private Paint progressPaint;


    /**
     * 进度条的画笔上的小点
     */
    private Paint progressDotPaint;
    //进度条范围（矩形）
    private RectF progressRectF;
    private int viewHeight;
    private int viewWidth;
    /**
     * 进度条的半径
     */
    private int progressRaduis;
    /**
     * 进度条的宽度
     */
    private int progressStroke = DisplayUtil.dp2px(getContext(),1);

    private int mTikeCount = 31;
    private int mItemcount = 6;
    /**
     * 进度条底色的起始位置
     */
    private int startAngle = 126;
    /**
     * 进度条底色的结束位置
     */
    private int sweepAngle = 288;
    // 圆心
    private PointF centerPoint = new PointF();
    private SesameModel dataModel;
    private float progressSweepAngle = 1;
    //旋转的角度(0.34误差校准)
    private float rAngle = sweepAngle / mTikeCount + 0.34f;
    // 进度的总和
    private float progressTotalSweepAngle;
    // 动画
    private ValueAnimator progressAnimator;
    // 积分
    private String sesameJiFen;
    private int startGradientColor = Color.parseColor("#f24a29");
    private int endGradientColor = Color.parseColor("#1170c1");

    public SesameCreditPanel(Context context) {
        this(context, null);
    }

    public SesameCreditPanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SesameCreditPanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        /**进度条底色的画笔*/
        panelPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        panelPaint.setColor(Color.argb(125, 255, 255, 255));
        panelPaint.setStyle(Paint.Style.STROKE);

        /**进度条的画笔*/
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setColor(Color.WHITE);
        progressPaint.setStrokeWidth(progressStroke);


        /**进度条x小点的画笔*/
        progressDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        progressDotPaint.setColor(Color.WHITE);

        /**仪表盘文本画笔*/
        panelTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        panelTextPaint.setStyle(Paint.Style.STROKE);

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (dataModel != null && dataModel.getSesameItemModels() != null && dataModel.getSesameItemModels().size() != 0) {
            viewWidth = w;
            viewHeight = h;
            progressRaduis = (w / 2) * 9 / 12;
            // 设置中心点
            centerPoint.set(viewWidth / 2, viewHeight / 2 * 25 / 24);
            sesameJiFen = String.valueOf(dataModel.getTotalMin());
            //System.out.println("dataModel.getTotalMin() = " + dataModel.getTotalMin() + ", String.valueOf(dataModel.getTotalMin()) = " + String.valueOf(dataModel.getTotalMin()));
            //sesameJiFen = String.valueOf(dataModel.getUserTotal());
            /**计算信用分所占的角度*/
            progressTotalSweepAngle = computeProgressAngle();
            Panel startPanel = new Panel();
            startPanel.setStartSweepAngle(1);
            startPanel.setStartSweepValue(dataModel.getTotalMin());

            Panel endPanel = new Panel();
            /**设置结束时的角度*/
            endPanel.setEndSweepAngle(progressTotalSweepAngle);
            // 设置动态的显示信用分
            endPanel.setEndSweepValue(dataModel.getUserTotal());

            progressAnimator = ValueAnimator.ofObject(new creditEvaluator(), startPanel, endPanel);
            progressAnimator.setDuration(5000);
            progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Panel panel = (Panel) animation.getAnimatedValue();
                    //更新进度值
                    progressSweepAngle = panel.getSesameSweepAngle();

                    //数字积分动态显示
                    sesameJiFen = String.valueOf(panel.getSesameSweepValue());
                    //System.out.println("panel.getSesameSweepValue() = " + panel.getSesameSweepValue() + ", String.valueOf(panel.getSesameSweepValue()); = " + String.valueOf(panel.getSesameSweepValue()));
                    invalidateView();
                }
            });

            postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressAnimator.start();
                }
            }, 1000);
        }

    }




    private class creditEvaluator implements TypeEvaluator {
        @Override
        public Object evaluate(float fraction, Object startValue, Object endValue) {
            Panel resultPanel = new Panel();
            Panel startPanel = (Panel) startValue;
            Panel endPanel = (Panel) endValue;
            //开始扫描角度,从1度开始扫描
            float startSweepAngle = startPanel.getStartSweepAngle();
            //结束扫描的角度,为计算出来的用户信用分在仪表盘上扫描过的角度
            float endSweepAngle = endPanel.getEndSweepAngle();
            //            计算出来进度条变化时变化的角度
            float sesameSweepAngle = startSweepAngle + fraction * (endSweepAngle - startSweepAngle);
            //设置计算出来进度条变化时变化的角度
            resultPanel.setSesameSweepAngle(sesameSweepAngle);
            //开始扫描的值,为起始刻度350
            float startSweepValue = startPanel.getStartSweepValue();
            //结束扫描的值,为用户的信用分
            float endSweepValue = endPanel.getEndSweepValue();
            //计算出进度条在变化的时候信用分的值
            float sesameSweepValue = startSweepValue + fraction * (endSweepValue - startSweepValue);
            resultPanel.setSesameSweepValue((int) sesameSweepValue);
            return resultPanel;

        }
    }

    /**
     * 计算用户信用分所占角度
     */
    private float computeProgressAngle() {
        ArrayList<SesameItemModel> list = dataModel.getSesameItemModels();
        int userTotal = dataModel.getUserTotal();
        float progressAngle = 0;
        for (int i = 0; i < list.size(); i++) {
            if (userTotal > list.get(i).getMax()) {
                progressAngle += mItemcount * rAngle;
                continue;
            }
            int blance = userTotal - list.get(i).getMin();
            float areaItem = (list.get(i).getMax() - list.get(i).getMin()) / mItemcount;
            progressAngle += (blance / areaItem) * rAngle;
            if (blance % areaItem != 0) {
                blance -= (blance / areaItem) * areaItem;
                float percent = (blance / areaItem);
                progressAngle += (int) (percent * rAngle);
            }
            break;
        }
        return progressAngle;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (dataModel != null && dataModel.getSesameItemModels() != null && dataModel.getSesameItemModels().size() != 0) {
            //绘制背景的变化
            //drawBackground(canvas);
            // 仪表盘
            drawPanel(canvas);

        }
    }



    /**
     * 绘制仪表盘
     * .........................
     */
    private void drawPanel(Canvas canvas) {
        panelPaint.setStrokeWidth(progressStroke);
        progressRectF = new RectF(centerPoint.x - progressRaduis, centerPoint.y - progressRaduis, centerPoint.x + progressRaduis, centerPoint.y + progressRaduis);
        // 仪表盘上面的进度条
        canvas.drawArc(progressRectF, startAngle, sweepAngle, false, panelPaint);
        //仪表盘上面的小进度（画笔颜色为白色，宽度是4，在画笔里面设置）
        canvas.drawArc(progressRectF, startAngle, progressSweepAngle, false, progressPaint);
        // 仪表盘上面的小进度的小圆点（画笔颜色为白色）
        canvas.drawCircle((float) (centerPoint.x + progressRaduis

                        * Math.cos((startAngle + progressSweepAngle) * 3.14 / 180)),

                (float) (centerPoint.y + progressRaduis

                        * Math.sin((startAngle + progressSweepAngle) * 3.14 / 180)), DisplayUtil.dp2px(getContext(),2), progressDotPaint);

        canvas.save();
        //旋转-110度,即坐标系270度位置  即垂直方向,便于计算
        canvas.rotate(-progressSweepAngle, centerPoint.x, centerPoint.y);
        canvas.restore();
        //绘制仪表盘文本
        drawPanelText(canvas);
    }



    /**
     * 绘制仪表盘文本
     */
    private void drawPanelText(Canvas canvas) {
        float drawTextY, textSpace = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics());
        Rect rect = new Rect();
        /**顶部Beta---->>安全级别*/
        String text = dataModel.getFirstText();
        panelTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()));
        panelTextPaint.setColor(Color.parseColor("#e5e5e5"));
        panelTextPaint.setFakeBoldText(false);
        panelTextPaint.getTextBounds(text, 0, text.length(), rect);
        drawTextY = centerPoint.y - progressRectF.height() / 2 * 0.45f;
        canvas.drawText(text, centerPoint.x - rect.width() / 2, drawTextY, panelTextPaint);

        // 积分
        text = sesameJiFen;
        panelTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 55, getResources().getDisplayMetrics()));
        panelTextPaint.setColor(Color.WHITE);
        panelTextPaint.setFakeBoldText(true);
        //System.out.println("panelTextPaint = " + panelTextPaint );
        //System.out.println("text = " + text );
        panelTextPaint.getTextBounds(text, 0, text.length(), rect);
        //drawTextY = drawTextY + rect.height() + textSpace;
        drawTextY = centerPoint.y + rect.height() / 2;
        canvas.drawText(text, centerPoint.x - rect.width() / 2, drawTextY, panelTextPaint);


        // 评估时间
        text = dataModel.getFourText();
        panelTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
        panelTextPaint.setColor(Color.WHITE);
        panelTextPaint.setFakeBoldText(false);
        panelTextPaint.getTextBounds(text, 0, text.length(), rect);
        drawTextY = centerPoint.y + progressRectF.height() / 2 * 0.55f;

        canvas.drawText(text, centerPoint.x - rect.width() / 2, drawTextY, panelTextPaint);
    }


    private void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }



    public void setDataModel(SesameModel datadataModel) {
        this.dataModel = datadataModel;
        //System.out.println("UserTotal() = " + dataModel.getUserTotal());
        invalidateView();
    }

}
