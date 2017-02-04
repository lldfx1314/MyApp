package com.anhubo.anhubo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by LUOLI on 2016/12/6.
 */
public class GuidePagerAdapter  extends FragmentStatePagerAdapter {
    ArrayList<Fragment> list;

    public GuidePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public GuidePagerAdapter(FragmentManager fm,ArrayList<Fragment> list) {
        super(fm);
        this.list=list;
    }


    @Override
    public Fragment getItem(int arg0) {
        return list.get(arg0);
    }

    @Override
    public int getCount() {
        return list.size();
    }
    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }
}
