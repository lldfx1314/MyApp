package com.anhubo.anhubo.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.bean.CellDeiailBean;
import com.anhubo.anhubo.bean.CellListBean;
import com.anhubo.anhubo.ui.activity.unitDetial.Cell_Detail_Activity;

import java.util.List;

/**
 * Created by LUOLI on 2017/1/3.
 */
public class CellDetailAdapter extends BaseAdapter {
    private Context mContext;
    private List<CellDeiailBean.Data.Businesses> businesses;
    private ViewHolder holder;

    public CellDetailAdapter(Context context, List<CellDeiailBean.Data.Businesses> businesses) {
        this.mContext = context;
        this.businesses = businesses;
    }

    @Override
    public int getCount() {
        return businesses == null ? 0 : businesses.size();
    }

    @Override
    public Object getItem(int position) {
        return businesses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_cell_detail, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CellDeiailBean.Data.Businesses businesses = this.businesses.get(position);
        holder.textView.setText(businesses.business_name);
        String color = businesses.color;
        switch (color) {
            case "红":
                holder.imageView.setImageResource(R.drawable.dot_red_shape);
                break;
            case "黄":
                holder.imageView.setImageResource(R.drawable.dot_yellow_shape);
                break;
            case "绿":
                holder.imageView.setImageResource(R.drawable.dot_green_shape);
                break;
        }
        return convertView;
    }


    static class ViewHolder {

        TextView textView;
        ImageView imageView;

        public ViewHolder(View view) {
            textView = (TextView) view.findViewById(R.id.item_tv_unit_detail);
            imageView = (ImageView) view.findViewById(R.id.item_iv_unit_detail);
        }
    }
}