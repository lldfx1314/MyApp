package com.anhubo.anhubo.utils;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.pickerview.LoopListener;
import com.anhubo.anhubo.pickerview.LoopView;

import java.util.Calendar;
import java.util.List;


/**
 * Created by baiyuliang on 2015-11-24.
 */
public class PopBirthHelper {

    private Context context;
    private PopupWindow pop;
    private View view;
    private OnClickOkListener onClickOkListener;

    private List<String> listYear, listMonth, listDay;
    private String year, month, day;

    public PopBirthHelper(Context context) {
        this.context = context;
        view = View.inflate(context,R.layout.picker_birth, null);
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
        listYear = DatePackerUtil.getBirthYearList();
        listMonth = DatePackerUtil.getBirthMonthList();
        listDay = DatePackerUtil.getBirthDay31List();
    }

    private void initView() {
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        Button btnOk = (Button) view.findViewById(R.id.btnOK);
        final LoopView loopView1 = (LoopView) view.findViewById(R.id.loopView1);
        final LoopView loopView2 = (LoopView) view.findViewById(R.id.loopView2);
        final LoopView loopView3 = (LoopView) view.findViewById(R.id.loopView3);
        loopView1.setList(listYear);
        loopView1.setNotLoop();

        Calendar c = Calendar.getInstance();
        loopView1.setCurrentItem(99 - (c.get(Calendar.YEAR) - 2016));//定位到2016年
        loopView2.setList(listMonth);
       // loopView2.setNotLoop();
        loopView2.setCurrentItem(0);
        loopView3.setList(listDay);
        //loopView3.setNotLoop();
        loopView3.setCurrentItem(0);


        loopView1.setListener(new LoopListener() {
            @Override
            public void onItemSelect(int item) {
                String select_item = listYear.get(item);
                if (TextUtils.isEmpty(year)) {
                    year = "2016";
                } else {
                    year = select_item.replace("年", "");
                }
                if (!TextUtils.isEmpty(month)&&month.equals("2")) {
                    if (DatePackerUtil.isRunYear(year)&&listDay.size()!=29) {
                        listDay=DatePackerUtil.getBirthDay29List();
                        loopView3.setList(listDay);
                        loopView3.setCurrentItem(0);
                    }else if (!DatePackerUtil.isRunYear(year)&&listDay.size()!=28) {
                        listDay=DatePackerUtil.getBirthDay28List();
                        loopView3.setList(listDay);
                        loopView3.setCurrentItem(0);
                    }
                }
            }
        });
        loopView2.setListener(new LoopListener() {
            @Override
            public void onItemSelect(int item) {
                String select_item = listMonth.get(item);
                if (TextUtils.isEmpty(month)) {
                    month = "1";
                } else {
                    month = select_item.replace("月", "");
                }
                if (month.equals("2")) {
                    if (!TextUtils.isEmpty(year)&&DatePackerUtil.isRunYear(year)&&listDay.size()!=29) {
                        listDay=DatePackerUtil.getBirthDay29List();
                        loopView3.setList(listDay);
                        loopView3.setCurrentItem(0);
                    }else if (!TextUtils.isEmpty(year)&&!DatePackerUtil.isRunYear(year)&&listDay.size()!=28) {
                        listDay=DatePackerUtil.getBirthDay28List();
                        loopView3.setList(listDay);
                        loopView3.setCurrentItem(0);
                    }
                }else if((month.equals("1")||month.equals("3")||month.equals("5")||month.equals("7")||month.equals("8")||month.equals("10")||month.equals("12"))&&listDay.size()!=31){
                    listDay=DatePackerUtil.getBirthDay31List();
                    loopView3.setList(listDay);
                    loopView3.setCurrentItem(0);
                }else if((month.equals("4")||month.equals("6")||month.equals("9")||month.equals("11"))&&listDay.size()!=30){
                    listDay=DatePackerUtil.getBirthDay30List();
                    loopView3.setList(listDay);
                    loopView3.setCurrentItem(0);
                }

            }
        });
        loopView3.setListener(new LoopListener() {
            @Override
            public void onItemSelect(int item) {
                String select_item = listDay.get(item);
                if (TextUtils.isEmpty(day)) {
                    day = "1";
                } else {
                    day = select_item.replace("日", "");
                }
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
                        onClickOkListener.onClickOk(year + "-" + month + "-" + day);
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
        public void onClickOk(String birthday);
    }

}
