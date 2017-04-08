package com.anhubo.anhubo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.bean.PlanBean;
import com.anhubo.anhubo.utils.DisplayUtil;
import com.anhubo.anhubo.utils.LogUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/22.
 */
public class AdapterJoinPlan extends RecyclerView.Adapter<AdapterJoinPlan.MyViewHolder> implements View.OnClickListener {
    private Context mContext;
    private ArrayList<PlanBean.Data.Cert> mList;
    private SpannableString ss;

    public AdapterJoinPlan(Context context, ArrayList<PlanBean.Data.Cert> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_join_plan, parent, false));
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // 获取数据
        PlanBean.Data.Cert cert = mList.get(position);
        double planEnsure = cert.plan_ensure;// 最高互助额
        String planId = cert.plan_id;
        double planMoney = cert.plan_money;
        String planName = cert.plan_name;
        String status = cert.status;

        // 添加旋转动画
        Animation rotateAnimation = AnimationUtils.loadAnimation(mContext, R.anim.rotation_anim);
        LinearInterpolator lir = new LinearInterpolator();
        rotateAnimation.setInterpolator(lir);
//        rotateAnimation.setRepeatCount(Animation.INFINITE);
        holder.ivPlanBg.setAnimation(rotateAnimation);

        if (!TextUtils.equals(status, "运行期")) {
            holder.ivPlanBg.setBackgroundResource(R.drawable.plan_norun);
            holder.ivPlanBg1.setBackgroundResource(R.drawable.plan_norun1);
            holder.tvPlanRunStatus.setTextColor(Color.parseColor("#ff9b4b"));
        } else {
            holder.ivPlanBg.setBackgroundResource(R.drawable.plan_run);
            holder.ivPlanBg1.setBackgroundResource(R.drawable.plan_run1);
            holder.tvPlanRunStatus.setTextColor(Color.parseColor("#4abbe9"));
        }
        holder.tvPlanRunStatus.setText(status);
        holder.tvJoinPlanName.setText(planName);
        setShowDetial(planEnsure + "", holder.tvHighHelpMoney);
//        holder.tvHighHelpMoney.setText(planEnsure + "");
        setShowDetial(planMoney + "", holder.tvHighShareMoney);
//        holder.tvHighShareMoney.setText(planMoney + "");
        holder.llJoinPlan.setOnClickListener(this);
        holder.llJoinPlan.setTag(position);
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
        LinearLayout llJoinPlan;
        ImageView ivPlanBg;
        ImageView ivPlanBg1;
        TextView tvPlanRunStatus;
        TextView tvJoinPlanName;
        TextView tvHighHelpMoney;
        TextView tvHighShareMoney;

        MyViewHolder(View view) {
            super(view);
            llJoinPlan = (LinearLayout) view.findViewById(R.id.item_ll_joinPlan);
            ivPlanBg = (ImageView) view.findViewById(R.id.item_iv_plan_bg);
            ivPlanBg1 = (ImageView) view.findViewById(R.id.item_iv_plan_bg1);
            tvPlanRunStatus = (TextView) view.findViewById(R.id.item_tv_plan_run_status);
            tvJoinPlanName = (TextView) view.findViewById(R.id.item_tv_join_plan_name);
            tvHighHelpMoney = (TextView) view.findViewById(R.id.item_tv_high_helpMoney);
            tvHighShareMoney = (TextView) view.findViewById(R.id.item_tv_high_shareMoney);
        }
    }
}
