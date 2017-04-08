package com.anhubo.anhubo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.ui.activity.HomeActivity;
import com.anhubo.anhubo.utils.DisplayUtil;
import com.anhubo.anhubo.utils.LogUtils;

import java.util.ArrayList;

/**
 * Created by LUOLI on 2017/2/23.
 */
public class AdapterDotLine extends RecyclerView.Adapter<AdapterDotLine.MyViewHolder> implements View.OnClickListener {
    private static final String TAG = "AdapterDotLine";
    private ArrayList<String> mList;
    private Context mContext;
    private int selectedPosition;


    public AdapterDotLine(Context context, ArrayList<String> dotArray) {
        this.mList = dotArray;
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_dotline, parent, false));
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        String string = mList.get(position);
        holder.tvFloor.setText(string);
        holder.tvFloor.setTag(position);
        holder.tvFloor.setOnClickListener(this);
        // 显示选中条目的效果
        if (selectedPosition == position) {
            holder.tvFloor.setBackgroundResource(R.drawable.slider);
        } else {
            holder.tvFloor.setBackgroundColor(DisplayUtil.getColor(R.color.backgroud_white));
        }

    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_tv_Floor:
                if (itemClickListener != null) {
                    LogUtils.eNormal(TAG, "点击了+" + v.getTag());
                    itemClickListener.onItemClick(v, (Integer) v.getTag());
                }

                break;
        }

    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }


    public interface ItemClickListener {

        /**
         * Item里选择点名称的点击事件
         */
        void onItemClick(View view, int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvFloor;

        MyViewHolder(View view) {
            super(view);
            tvFloor = (TextView) view.findViewById(R.id.item_tv_Floor);
        }
    }
}
