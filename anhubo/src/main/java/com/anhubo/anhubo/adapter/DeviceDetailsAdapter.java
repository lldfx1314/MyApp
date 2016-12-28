package com.anhubo.anhubo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.anhubo.anhubo.R;

import java.util.List;

/**
 * Created by Administrator on 2016/9/18.
 */
public class DeviceDetailsAdapter extends BaseAdapter {

    public Context mContext;
    public List mList;

    public DeviceDetailsAdapter(Context context, List<String> list) {
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
        ViewHolder viewHolder = null;
        // 检查项
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_listview, null);
            viewHolder.tv_check_des = (TextView) convertView.findViewById(R.id.tv_check_des);
            viewHolder.iv_check_device = (ImageView) convertView.findViewById(R.id.iv_check_device);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        // 获取数据
        String str = (String) mList.get(position);
        // 填充数据
        viewHolder.tv_check_des.setText(str);

        return convertView;
    }


    static class ViewHolder {
        TextView tv_check_des;
        ImageView iv_check_device;
    }
}
