package com.example.miic.oa.news.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.miic.R;
import com.example.miic.oa.common.gifView.GifView;
import com.example.miic.oa.news.item.InfoPageNewsDetailGifImage;

import java.util.List;

/**
 * Created by XuKe on 2018/2/27.
 */

public class InfoPageNewsDetailGifImageAdapter extends BaseAdapter {
    private Context context;
    private List<InfoPageNewsDetailGifImage> list;
    public InfoPageNewsDetailGifImageAdapter(Context context, List<InfoPageNewsDetailGifImage> objects){
        this.context = context;
        this.list = objects;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public InfoPageNewsDetailGifImage getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        InfoPageNewsDetailGifImage newsDetailGifImage = getItem(position);
        View view;
        ViewHolder holder;
        if(convertView==null){
            view = LayoutInflater.from(context).inflate(R.layout.news_detail_gif_image_item,null);
            holder=new ViewHolder(view);
            view.setTag(holder);
        }else{
            view=convertView;
            holder= (ViewHolder) view.getTag();
        }

        holder.src.readGifFormNet(newsDetailGifImage.getLink());
        return view;
    }
    class ViewHolder{
        private GifView src;
        public ViewHolder( View view){
            src = (GifView) view.findViewById(R.id.gifImage);
        }

    }
}
