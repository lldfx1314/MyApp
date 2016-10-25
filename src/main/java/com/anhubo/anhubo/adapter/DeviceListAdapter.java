package com.anhubo.anhubo.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.anhubo.anhubo.R;

import java.util.ArrayList;

/**
 * Created by LUOLI on 2016/10/24.
 */
public class DeviceListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> deviceIds;
    private ArrayList<String> deviceNames;
    ViewHolder hold;

    public DeviceListAdapter(Context context, ArrayList<String> deviceIds, ArrayList<String> deviceNames) {
        this.mContext = context;
        this.deviceIds = deviceIds;
        this.deviceNames = deviceNames;
    }

    @Override
    public int getCount() {
        return deviceIds == null ? 0 : deviceIds.size();
    }

    @Override
    public Object getItem(int position) {
        return deviceIds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_devices, null);
            hold = new ViewHolder(convertView);
            convertView.setTag(hold);
        } else {
            hold = (ViewHolder) convertView.getTag();
        }
        String id = deviceIds.get(position);
        String name = deviceNames.get(position);
        hold.txt.setText(name);
        return convertView;
    }
    static class ViewHolder {
        TextView txt;
        public ViewHolder(View view) {
            txt = (TextView) view.findViewById(R.id.tv_devices);
        }
    }
}
