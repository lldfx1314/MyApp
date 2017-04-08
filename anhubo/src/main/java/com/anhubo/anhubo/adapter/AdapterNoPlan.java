package com.anhubo.anhubo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.bean.PlanBean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/22.
 */
public class AdapterNoPlan extends RecyclerView.Adapter<AdapterNoPlan.MyViewHolder> implements View.OnClickListener {
    private Context mContext;
    private ArrayList<PlanBean.Data.PlanList> mList;
    public AdapterNoPlan(Context context, ArrayList<PlanBean.Data.PlanList> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_no_join_plan, parent, false));
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PlanBean.Data.PlanList plan = mList.get(position);
        // 获取数据
        String joinNum = plan.join_num;
        String payedNum = plan.payed_num;
        String planEnsure = plan.plan_ensure;
        String planId = plan.plan_id;
        String planMoney = plan.plan_money;
        String planName = plan.plan_name;
        String status = plan.status;
        // 填充数据
        holder.tvName.setText(planName);
        holder.llNoPlan.setTag(position);
        holder.llNoPlan.setOnClickListener(this);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View v) {
        if(itemClickListener!=null){
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
    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        LinearLayout llNoPlan;

        MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tv_no_join_plan_name);
            llNoPlan = (LinearLayout) view.findViewById(R.id.ll_no_join_plan);
        }
    }
}
