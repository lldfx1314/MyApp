package com.anhubo.anhubo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.anhubo.anhubo.R;

import java.util.ArrayList;

/**
 * Created by LUOLI on 2016/10/20.
 */
public class UnitMsgCenterAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> mListMsg;
    private ArrayList<String> mListTItle;
    private ArrayList<String> mListTime;
    ViewHolder hold;

    public UnitMsgCenterAdapter(Context context, ArrayList<String> listMsg, ArrayList<String> listTItle, ArrayList<String> listTime) {
        this.mContext = context;
        this.mListMsg = listMsg;
        this.mListTItle = listTItle;
        this.mListTime = listTime;
    }

    @Override
    public int getCount() {
        return mListMsg == null ? 0 : mListMsg.size();
    }

    @Override
    public Object getItem(int position) {
        return mListMsg.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_unit_msgcenter, null);

            hold = new ViewHolder(convertView);
            convertView.setTag(hold);
        } else {
            hold = (ViewHolder) convertView.getTag();
        }

        hold.textTile.setText(mListTItle.get(position)+"title");
        hold.textTime.setText(mListTime.get(position));
        hold.textMsg.setText(mListMsg.get(position));
        return convertView;

    }

    static class ViewHolder {
        TextView textTile;
        TextView textTime;
        TextView textMsg;
        public ViewHolder(View view) {
            textTile = (TextView) view.findViewById(R.id.tv_title);
            textTime = (TextView) view.findViewById(R.id.tv_time);
            textMsg = (TextView) view.findViewById(R.id.tv_msg);
        }
    }
}
