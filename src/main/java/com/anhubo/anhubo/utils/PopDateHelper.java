package com.anhubo.anhubo.utils;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.pickerview.LoopListener;
import com.anhubo.anhubo.pickerview.LoopView;

import java.util.List;


/**
 * Created by baiyuliang on 2015-11-24.
 */
public class PopDateHelper {

    private Context context;
    private PopupWindow pop;
    private View view;
    private OnClickOkListener onClickOkListener;

    private List<String> listDate, listTime;
    private List<String> listYear,listMonth;
    private String year, month;

    public PopDateHelper(Context context) {
        this.context = context;
        view = View.inflate(context,R.layout.picker_date, null);
        pop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        initPop();
        initData();
        initView();
    }


    private void initPop() {
        pop.setAnimationStyle(android.R.style.Animation_InputMethod);
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }


    /**
     * 初始化数据
     */
    private void initData() {
        listDate = DatePackerUtil.getYearList();
        listYear = DatePackerUtil.getYear();
        listTime = DatePackerUtil.getMonthList();
        listMonth = DatePackerUtil.getMonth();
    }

    private void initView() {
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        Button btnOk = (Button) view.findViewById(R.id.btnOK);
        final LoopView loopView1 = (LoopView) view.findViewById(R.id.loopView1);
        final LoopView loopView2 = (LoopView) view.findViewById(R.id.loopView2);
        final LoopView loopViewYear = (LoopView) view.findViewById(R.id.loopViewYear);
        final LoopView loopViewMonth = (LoopView) view.findViewById(R.id.loopViewMonth);
        loopView1.setIsViewYear(false);//不显示年
        loopView1.setList(listDate);
        loopView1.setNotLoop();
        loopView1.setCurrentItem(1);

        loopViewYear.setList(listYear);
        loopViewYear.setNotLoop();
        loopViewYear.setCurrentItem(0);

        loopViewMonth.setList(listMonth);
        loopViewMonth.setNotLoop();
        loopViewMonth.setCurrentItem(0);


        loopView2.setList(listTime);
        loopView2.setNotLoop();
        loopView2.setCurrentItem(0);

        loopView1.setListener(new LoopListener() {
            @Override
            public void onItemSelect(int item) {
                year =listDate.get(item);
            }
        });
        loopView2.setListener(new LoopListener() {
            @Override
            public void onItemSelect(int item) {
                month = listTime.get(item);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onClickOkListener.onClickOk(year , month);
                    }
                }, 500);
            }
        });
    }

    /**
     * 显示
     *
     * @param view
     */
    public void show(View view) {
        pop.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 隐藏监听
     *
     * @param onDismissListener
     */
    public void setOnDismissListener(PopupWindow.OnDismissListener onDismissListener) {
        pop.setOnDismissListener(onDismissListener);
    }

    public void setOnClickOkListener(OnClickOkListener onClickOkListener) {
        this.onClickOkListener = onClickOkListener;
    }

    public interface OnClickOkListener {
        public void onClickOk(String date, String time);
    }

}
