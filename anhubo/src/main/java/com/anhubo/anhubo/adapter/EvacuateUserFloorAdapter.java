package com.anhubo.anhubo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.anhubo.anhubo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LUOLI on 2016/11/1.
 */
public class EvacuateUserFloorAdapter extends BaseAdapter {
    private List<String> mList;
    private Context mContext;
    private ViewHolder hold;
    public EvacuateUserFloorAdapter(Context context, ArrayList<String> list) {
        this.mList = list;
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
            convertView = View.inflate(mContext, R.layout.item_evacuate_userfloor, null);
            hold = new ViewHolder(convertView);
            convertView.setTag(hold);
        } else {
            hold = (ViewHolder) convertView.getTag();
        }
        hold.txt.setText(mList.get(position));

        return convertView;

    }

    static class ViewHolder {
        TextView txt;
        public ViewHolder(View view) {
            txt = (TextView) view.findViewById(R.id.tv_evacuate);
        }
    }
}