package com.anhubo.anhubo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anhubo.anhubo.R;
import com.anhubo.anhubo.adapter.viewholder.RunCertificateIcon_Holder;
import com.anhubo.anhubo.bean.RunCertificateBean;
import com.anhubo.anhubo.ui.activity.unitDetial.RunCertificateActivity;
import com.anhubo.anhubo.utils.LogUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

/**
 * Created by LUOLI on 2017/1/17.
 */
public class RunCertificateIconAdapter extends RecyclerView.Adapter<RunCertificateIcon_Holder> {
    private static final String TAG = "RunCertificateIconAdapter";
    private Context mContext;
    private ArrayList<RunCertificateBean.Data.Icon> mList;

    public RunCertificateIconAdapter(Context context, ArrayList<RunCertificateBean.Data.Icon> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public RunCertificateIcon_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.item_runcertificate_icon, null);
        RunCertificateIcon_Holder holder = new RunCertificateIcon_Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RunCertificateIcon_Holder holder, int position) {
        RunCertificateBean.Data.Icon icon = mList.get(position);
        String url = icon.pic;
        if (!TextUtils.isEmpty(url)) {
            setHeaderIcon(holder.imageView, url);
        } else {
            holder.imageView.setImageResource(R.drawable.newicon);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * 设置头像的方法
     */
    private void setHeaderIcon(final CircleImageView ivIcon, String imgurl) {

        Glide
                .with(mContext)
                .load(imgurl)
                .centerCrop().crossFade()
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
                .into(ivIcon);

    }

}
