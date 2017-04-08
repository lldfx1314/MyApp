package com.anhubo.anhubo.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;

import java.util.ArrayList;

/**
 * Created by LUOLI on 2017/3/7.
 */
public class EvacuateAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> mList;

    private ViewHolder holder;

    public EvacuateAdapter(Context context, ArrayList<String> list) {
        this.mContext = context;
        this.mList = list;
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
            convertView = View.inflate(mContext, R.layout.item_dotline, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String s = mList.get(position);
        holder.tvFloor.setText(s);
        return convertView;
    }

    static class ViewHolder {
        TextView tvFloor;

        public ViewHolder(View view) {
            tvFloor = (TextView) view.findViewById(R.id.item_tv_Floor);
        }
    }
}
