package com.anhubo.anhubo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.interfaces.InterClick;

import java.util.ArrayList;

/**
 * Created by LUOLI on 2016/10/24.
 */
public class DeviceListAdapter extends BaseAdapter implements View.OnClickListener {
    private Context mContext;
    private ArrayList<String> deviceIds;
    private ArrayList<String> deviceNames;
    private ArrayList<String> deviceJudges;
    private InterClick mCallback;
    ViewHolder hold;

    public DeviceListAdapter(Context context, ArrayList<String> deviceIds,ArrayList<String> deviceNames, ArrayList<String> deviceJudges,InterClick callback) {
        this.mContext = context;
        this.deviceIds = deviceIds;
        this.deviceNames = deviceNames;
        this.deviceJudges = deviceJudges;
        this.mCallback = callback;
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
        hold.ivJudge.setTag(position);
        int mPosition = (int) hold.ivJudge.getTag();
        String judge = deviceJudges.get(position);
        if (TextUtils.equals(1 + "", judge)&&mPosition == position) {
            hold.ivJudge.setVisibility(View.VISIBLE);
//            hold.ivJudge.setBackgroundResource(R.drawable.fuxuan_input01);
        }else{
            hold.ivJudge.setVisibility(View.GONE);
        }

        String name = deviceNames.get(position);
        hold.txt.setText(name);

        hold.btnDelete.setOnClickListener(this);
        hold.btnDelete.setTag(position);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_devices_del:
                if (mCallback != null) {
                    mCallback.onBtnClick(v);
                }
                break;
            default:
                break;
        }
    }

    static class ViewHolder {
        TextView txt;
        ImageView ivJudge;
        Button btnDelete;

        public ViewHolder(View view) {
            txt = (TextView) view.findViewById(R.id.tv_devices);
            ivJudge = (ImageView) view.findViewById(R.id.iv_devices_judege);
            btnDelete = (Button) view.findViewById(R.id.btn_devices_del);
        }
    }
    /**条目内部的button点击事件*/
//    public interface InterClick {
//        void onBtnClick(View v);
//    }
}
