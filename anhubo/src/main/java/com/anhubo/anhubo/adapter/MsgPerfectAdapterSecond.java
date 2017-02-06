package com.anhubo.anhubo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.anhubo.anhubo.R;

/**
 * Created by Administrator on 2016/9/24.
 */
public class MsgPerfectAdapterSecond extends BaseAdapter {
    private String[]  mArr;
    private Context mContext;
    private int mPosition = 0;
    ViewHolder hold;


    public MsgPerfectAdapterSecond(Context context, String[] arr) {
        this.mContext = context;
        this.mArr = arr;
    }


    @Override
    public int getCount() {
        return mArr == null ? 0 : mArr.length;
    }

    @Override
    public Object getItem(int position) {
        return mArr[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_listview2, null);
            hold = new ViewHolder(convertView);
            convertView.setTag(hold);
        } else {
            hold = (ViewHolder) convertView.getTag();
        }
        hold.txt.setText(mArr[position]);
        hold.txt.setTextColor(0xFF666666);
        if (position == mPosition) {
//            hold.txt.setTextColor(0xFFFF8C00);
        }
        return convertView;
    }

    public void setSelectItem(int position) {
        this.mPosition = position;
    }




    static class ViewHolder {
        TextView txt;

        public ViewHolder(View view) {
            txt = (TextView) view.findViewById(R.id.moreitem_txt);
        }
    }

}
