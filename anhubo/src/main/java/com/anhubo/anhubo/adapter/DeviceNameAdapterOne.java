package com.anhubo.anhubo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anhubo.anhubo.R;

import java.util.List;

/**
 * Created by Administrator on 2016/9/24.
 */
public class DeviceNameAdapterOne extends BaseAdapter {
    private List<String> mList1;
    private Context mContext;
    private int mPosition = 0;
    ViewHolder hold;

    public DeviceNameAdapterOne(Context context, List<String> list1) {
        this.mContext = context;
        this.mList1 = list1;
    }

    @Override
    public int getCount() {
        return mList1 == null ? 0 : mList1.size();
    }

    @Override
    public Object getItem(int position) {
        return mList1.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_listview1, null);
            hold = new ViewHolder(convertView);
            convertView.setTag(hold);
        } else {
            hold = (ViewHolder) convertView.getTag();
        }
        hold.txt.setText(mList1.get(position));
        hold.txt.setTextColor(Color.parseColor("#99000000"));
        hold.viewItem.setVisibility(View.GONE);
        hold.txt.setTag(mList1.get(position));
        if (mPosition == position) {
            String tag = (String) hold.txt.getTag();
            if(TextUtils.equals(tag,hold.txt.getText().toString())){
                hold.txt.setTextColor(Color.parseColor("#5e84ff"));
            }

            hold.viewItem.setVisibility(View.VISIBLE);
            hold.viewItem.setBackgroundResource(R.drawable.view_shap);
        }
        return convertView;

    }
    public void setSelectItem(int position) {
        this.mPosition = position;
    }

    public int getSelectItem() {
        return mPosition;
    }



    static class ViewHolder {
        LinearLayout layout;
        TextView txt;
        View viewItem;
        public ViewHolder(View view) {
            txt = (TextView) view.findViewById(R.id.mainitem_txt);
            layout = (LinearLayout) view.findViewById(R.id.mainitem_layout);
            viewItem = view.findViewById(R.id.mainitem_view);
        }
    }
}
