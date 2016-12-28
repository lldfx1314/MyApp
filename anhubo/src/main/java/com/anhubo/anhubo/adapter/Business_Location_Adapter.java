package com.anhubo.anhubo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.ui.activity.unitDetial.BusinessActivity;

import java.util.List;

/**
 * Created by LUOLI on 2016/11/21.
 */
public class Business_Location_Adapter extends BaseAdapter {
    private Context mContext;
    private List<String> mList;
    private ViewHolder holder;

    public Business_Location_Adapter(Context context, List<String> business) {
        this.mContext = context;
        this.mList = business;
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
            convertView = View.inflate(mContext, R.layout.item_location, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String string = mList.get(position);
        holder.tvLocation.setText(string);

        return convertView;
    }

    class ViewHolder {
        TextView tvLocation;

        public ViewHolder(View view) {
            tvLocation = (TextView) view.findViewById(R.id.tv_location);
        }
    }
}
