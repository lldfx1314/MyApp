package com.anhubo.anhubo.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.anhubo.anhubo.ui.impl.BuildFragment;

/**
 * Created by LUOLI on 2016/10/25.
 */
public class BuildAdapter extends BaseAdapter {

    private BuildFragment context;

    public BuildAdapter(BuildFragment context) {
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
