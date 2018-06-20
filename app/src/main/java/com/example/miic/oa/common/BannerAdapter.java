package com.example.miic.oa.common;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.miic.R;

import cn.bingoogolapple.bgabanner.BGABanner;

/**
 * Created by XuKe on 2018/3/26.
 */

public class BannerAdapter implements BGABanner.Adapter<ImageView, String>  {
    @Override
    public void fillBannerItem(BGABanner banner, ImageView itemView, String model, int position) {
        Glide.with(itemView.getContext())
                .load(model)
                .apply(new RequestOptions().placeholder(R.drawable.holder).error(R.drawable.holder).dontAnimate().centerCrop())
                .into(itemView);
    }

}
