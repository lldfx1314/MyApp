package com.anhubo.anhubo.adapter;

import android.content.Context;
import android.content.pm.LabeledIntent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.bean.PlanBean;
import com.anhubo.anhubo.utils.DisplayUtil;
import com.anhubo.anhubo.view.MyRoundProcess;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/22.
 */
public class AdapterPlanManage extends RecyclerView.Adapter<AdapterPlanManage.MyViewHolder> implements View.OnClickListener {
    private Context mContext;
    private ArrayList<PlanBean.Data.Manage> mList;
    private SpannableString ss;

    public AdapterPlanManage(Context context, ArrayList<PlanBean.Data.Manage> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_plan_manage, parent, false));
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        PlanBean.Data.Manage manage = mList.get(position);
        String actNum = manage.act_num;// 加入人数
        String payedNum = manage.payed_num;//互助事件数
        String planId = manage.plan_id;
        String planName = manage.plan_name;
        String planNum = manage.plan_num;//计划总人数
        String sumMoney = manage.sum_money;//保证金总额
        // 填充数据
        holder.tvPlanName.setText(planName);
        holder.tvjoinNum.setText(actNum);
        holder.tvSumNum.setText("/ " + planNum);
        setShowDetial(sumMoney, holder.tvMarginMoney);
//        holder.tvMarginMoney.setText(sumMoney);
        holder.tvHelpEventNum.setText(payedNum + "个");

        holder.llPalnManage.setTag(position);
        holder.llPalnManage.setOnClickListener(this);
        // 计算比例
        int proportion = Integer.parseInt(actNum) / Integer.parseInt(planNum);
        holder.tvProportion.setText(proportion + "%");
        holder.roundProcess.setMaxProgress((float) Double.parseDouble(planNum));
        holder.roundProcess.setProgress((float) Double.parseDouble(actNum));
        holder.roundProcess.runAnimate((float) Double.parseDouble(actNum));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View v) {
        if (itemClickListener != null) {
            itemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }

    public NoPlanItemClickListener itemClickListener;

    public void setItemClickListener(NoPlanItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface NoPlanItemClickListener {

        /**
         * Item里选择点名称的点击事件
         */
        void onItemClick(View view, int position);
    }

    private void setShowDetial(String string, TextView textView) {
        if (!TextUtils.isEmpty(string)) {

            Double heighSharing = Double.parseDouble(string) / 10000;

            if (heighSharing < 1) {
                if (string.endsWith(".0")) {
                    string = string.replace(".0", "");
                }
                textView.setText(string);
            } else if (heighSharing >= 1) {
                String str = String.valueOf(heighSharing);
                if (str.length() >= 3) {
                    String substring = str.substring(0, 3);
                    //　做判断，防止显示类似＂50.万＂这样的情况
                    if (substring.endsWith(".")) {
                        setWan(substring.substring(0, 2) + "万");
                    } else if (substring.endsWith(".0")) {
                        setWan(substring.substring(0, 1) + "万");
                    }
                } else {
                    setWan(str + "万");

                }
                textView.setHorizontallyScrolling(true);
                textView.setText(ss);
            }
        }
    }

    /**
     * 设置万字大小
     */
    private void setWan(String string) {

        ss = new SpannableString(string);
        MyURLSpan myURLSpan = new MyURLSpan(string);
        ss.setSpan(myURLSpan, string.length() - 1, string.length(), SpannableString.SPAN_EXCLUSIVE_INCLUSIVE);
    }

    class MyURLSpan extends URLSpan {


        public MyURLSpan(String url) {
            super(url);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setTextSize(DisplayUtil.sp2px(mContext, 24));
        }
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvProportion;// 比例
        LinearLayout llPalnManage;
        TextView tvPlanName;// 计划名称
        TextView tvjoinNum;// 加入人数
        TextView tvSumNum;// 总人数
        TextView tvMarginMoney;// 保证金总额
        TextView tvHelpEventNum;// 互助事件数
        MyRoundProcess roundProcess;// 进度条

        MyViewHolder(View view) {
            super(view);
            tvProportion = (TextView) view.findViewById(R.id.item_tv_plan_manage_Proportion);
            llPalnManage = (LinearLayout) view.findViewById(R.id.item_ll_paln_manage);
            tvPlanName = (TextView) view.findViewById(R.id.item_tv_plan_manage_name);
            tvjoinNum = (TextView) view.findViewById(R.id.item_tv_plan_manage_joinNum);
            tvSumNum = (TextView) view.findViewById(R.id.item_tv_plan_manage_sumNum);
            tvMarginMoney = (TextView) view.findViewById(R.id.item_tv_plan_manage_MarginMoney);
            tvHelpEventNum = (TextView) view.findViewById(R.id.item_tv_plan_manage_HelpEventNum);
            roundProcess = (MyRoundProcess) view.findViewById(R.id.roundProcess);
        }
    }
}
