package com.anhubo.anhubo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.bean.PlanMemberBean;

import java.util.ArrayList;

/**
 * Created by LUOLI on 2017/3/27.
 */
public class AdapterPlanJoinRecord extends RecyclerView.Adapter<AdapterPlanJoinRecord.MyViewHolder>{
    private Context mContext;
    private ArrayList<PlanMemberBean.Data.Business_names> mList;
    public AdapterPlanJoinRecord(Context context, ArrayList<PlanMemberBean.Data.Business_names> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_plan_joinrecord, parent, false));
        return viewHolder;
    }

    @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
        PlanMemberBean.Data.Business_names businessNames = mList.get(position);
        String businessName = businessNames.business_name;
        holder.tvPalnName.setText(businessName);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvPalnName;
        public MyViewHolder(View view) {
            super(view);
            tvPalnName = (TextView) view.findViewById(R.id.tv_item_plan_joinrecord_name);
        }
    }
}
