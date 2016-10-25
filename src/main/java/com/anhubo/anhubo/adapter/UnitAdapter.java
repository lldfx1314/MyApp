package com.anhubo.anhubo.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.anhubo.anhubo.ui.impl.UnitFragment;

/**
 * Created by LUOLI on 2016/10/24.
 */
public class UnitAdapter extends BaseAdapter{
    private final UnitFragment context;

    public UnitAdapter(UnitFragment context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
