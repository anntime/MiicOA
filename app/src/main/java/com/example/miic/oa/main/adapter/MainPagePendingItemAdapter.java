package com.example.miic.oa.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.miic.R;
import com.example.miic.oa.main.item.MainPagePendingItem;

import java.util.List;

/**
 * Created by XuKe on 2018/1/31.
 * 新闻Adapter 实现listview的数据填充
 */

public class MainPagePendingItemAdapter extends BaseAdapter {
        private  Context context;
        private List<MainPagePendingItem> list;
        public MainPagePendingItemAdapter(Context context, List<MainPagePendingItem> objects){
                this.context = context;
                this.list = objects;
        }
        @Override
        public int getCount() {
                return list.size();//注意此处
        }

        @Override
        public MainPagePendingItem getItem(int position) {
                return list.get(position);
        }

        @Override
        public long getItemId(int position) {
                return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
                MainPagePendingItem pendingItem = getItem(position);
                View view;
                ViewHolder holder;
                if(convertView==null){
                        view=LayoutInflater.from(context).inflate(R.layout.pending_item_for_main_fragment,null);
                        holder=new ViewHolder(view);
                        view.setTag(holder);
                }else{
                        view=convertView;
                        holder= (ViewHolder) view.getTag();
                }
                holder.newsTitle.setText(pendingItem.getTitle());
                holder.newsPublishTime.setText(pendingItem.getPublishTime());
                holder.icon.setImageResource(pendingItem.getIconID());
                return view;
        }
        class ViewHolder{
                private TextView newsTitle;
                private TextView newsPublishTime;
                private ImageView icon;

                public ViewHolder( View view){
                        newsTitle = (TextView) view.findViewById(R.id.news_title);
                        newsPublishTime = (TextView) view.findViewById(R.id.news_publish_time);
                        icon = (ImageView)view.findViewById(R.id.icon);
                }
        }
}