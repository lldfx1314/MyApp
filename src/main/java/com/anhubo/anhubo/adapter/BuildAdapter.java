package com.anhubo.anhubo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.bean.Build_Help_Plan_Bean;
import com.anhubo.anhubo.utils.DisplayUtil;

import java.util.List;

/**
 * Created by LUOLI on 2016/10/25.
 */
public class BuildAdapter extends BaseAdapter {

    private Context mContext;
    private List<Build_Help_Plan_Bean.Data.Plans> mList;
    private ViewHolder holder;
    private SpannableString ss;

    public BuildAdapter(Context context, List<Build_Help_Plan_Bean.Data.Plans> plans) {
        this.mContext = context;
        this.mList = plans;
    }

    public BuildAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_build_help_plan, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Build_Help_Plan_Bean.Data.Plans plan = mList.get(position);

        String massName = plan.mass_name;
        String massId = plan.mass_id;
        String planEnsure = plan.plan_ensure;
        String planId = plan.plan_id;
        String planJoin = plan.plan_join;
        String planPerson = plan.plan_person;
        String planRange = plan.plan_range;
        String planName = plan.plan_name;
        //设置数据
        holder.tvPlanName.setText(planName);
        holder.tvMassName.setText("By "+massName);
        holder.tvPlanEnsure.setText("参与会员单位最高获得"+planEnsure+"保障");
        holder.tvPlanRange.setText("保障范围: "+planRange);
        // 根据参与人数与计划人数相比判断显示进度条还是已完成计划人数
        if(Integer.parseInt(planJoin)<Integer.parseInt(planPerson)){
            // 显示进度条
            holder.rlProBar.setVisibility(View.VISIBLE);
            holder.proBarPlan.setMax(Integer.parseInt(planPerson));
            holder.proBarPlan.setProgress(Integer.parseInt(planJoin));
            holder.tvProBarPlanJoin.setText(planJoin);
            holder.tvProBarPlanperson.setText("/"+planPerson);

        }else{
            // 隐藏进度条，显示已完成的计划人数
            holder.tvPlanPerson.setVisibility(View.VISIBLE);
            String string = "已加入"+planPerson+"人";
            setCharacterSize(string);
            holder.tvPlanPerson.setText(ss);
        }
        return convertView;
    }
    /**设置文字大小、颜色*/
    private void setCharacterSize(String string){

        ss = new SpannableString(string);
        MyURLSpan myURLSpan = new MyURLSpan(string);
        ss.setSpan(myURLSpan, 3, string.length()-1, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE);
    }
    class MyURLSpan extends URLSpan {


        public MyURLSpan(String url) {
            super(url);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setTextSize(DisplayUtil.sp2px(mContext,15));
            ds.setColor(Color.parseColor("#3B69FF"));//设置文字的颜色
        }
    }
    static class ViewHolder {
        TextView tvPlanName;
        TextView tvMassName;
        TextView tvPlanEnsure;
        TextView tvPlanRange;
        RelativeLayout rlProBar;
        ProgressBar proBarPlan;
        TextView tvProBarPlanJoin;
        TextView tvProBarPlanperson;
        TextView tvPlanPerson;

        public ViewHolder(View view) {
            tvPlanName = (TextView) view.findViewById(R.id.tv_plan_name);
            tvMassName = (TextView) view.findViewById(R.id.tv_mass_name);
            tvPlanEnsure = (TextView) view.findViewById(R.id.tv_plan_ensure);
            tvPlanRange = (TextView) view.findViewById(R.id.tv_plan_range);
            rlProBar = (RelativeLayout) view.findViewById(R.id.rl_proBar);
            proBarPlan = (ProgressBar) view.findViewById(R.id.proBar_plan);
            tvProBarPlanJoin = (TextView) view.findViewById(R.id.tv_proBar_plan_join);
            tvProBarPlanperson = (TextView) view.findViewById(R.id.tv_proBar_plan_persion);
            tvPlanPerson = (TextView) view.findViewById(R.id.tv_plan_person);
        }
    }
}
