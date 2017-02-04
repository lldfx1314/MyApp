package com.anhubo.anhubo.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.anhubo.anhubo.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by LUOLI on 2017/1/17.
 */
public class RunCertificateIcon_Holder extends RecyclerView.ViewHolder {


    public CircleImageView imageView;

    public RunCertificateIcon_Holder(View itemView) {
        super(itemView);

        imageView = (CircleImageView)itemView.findViewById(R.id.iv_run_icon);
    }
}
