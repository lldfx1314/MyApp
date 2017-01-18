package com.anhubo.anhubo.protocol;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.anhubo.anhubo.ui.activity.unitDetial.RunCertificateActivity;

/**
 * Created by LUOLI on 2017/1/18.
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {
        private int space;
        public DividerItemDecoration(int space) {
            this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        if(parent.getChildPosition(view) >= 5)
            outRect.top = space;
    }

}
