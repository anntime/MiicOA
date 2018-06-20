package com.example.miic.oa.news.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.miic.R;
import com.example.miic.oa.news.item.InfoPageNewsDetailImageAcc;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by XuKe on 2018/2/27.
 */

public class InfoPageNewsDetailImageAccAdapter extends BaseAdapter {
    private Context context;
    private List<InfoPageNewsDetailImageAcc> list;
    public InfoPageNewsDetailImageAccAdapter(Context context, List<InfoPageNewsDetailImageAcc> objects){
        this.context = context;
        this.list = objects;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public InfoPageNewsDetailImageAcc getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        InfoPageNewsDetailImageAcc newsDetailImageAcc = getItem(position);
        View view;
        ViewHolder holder;
        if(convertView==null){
            view = LayoutInflater.from(context).inflate(R.layout.news_detail_image_item,null);
            holder=new ViewHolder(view);
            view.setTag(holder);
        }else{
            view=convertView;
            holder= (ViewHolder) view.getTag();
        }
        Picasso.with(context).load(newsDetailImageAcc.getLink()).into(holder.src);

        return view;
    }
    class ViewHolder{
        private ImageView src;
        public ViewHolder( View view){
            src = (ImageView) view.findViewById(R.id.news_detail_acc_image);
        }
    }
}
