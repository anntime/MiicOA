package com.example.miic.oa.work.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.miic.R;
import com.example.miic.oa.work.item.WorkPageChildNews;

import java.util.List;

/**
 * Created by XuKe on 2018/1/31.
 * 新闻Adapter 实现listview的数据填充
 */

public class WorkPageChildNewsAdapter extends BaseAdapter {
        private  Context context;
        private List<WorkPageChildNews> list;
        public WorkPageChildNewsAdapter(Context context, List<WorkPageChildNews> objects){
                this.context = context;
                this.list = objects;
        }
        @Override
        public int getCount() {
                return list.size();//注意此处
        }

        @Override
        public WorkPageChildNews getItem(int position) {
                return list.get(position);
        }

        @Override
        public long getItemId(int position) {
                return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
                WorkPageChildNews workPageChildNews = getItem(position);
                View view;
                ViewHolder holder;
                if(convertView==null){
                        view=LayoutInflater.from(context).inflate(R.layout.work_child_news_item,null);
                        holder=new ViewHolder(view);
                        view.setTag(holder);
                }else{
                        view=convertView;
                        holder= (ViewHolder) view.getTag();
                }
                holder.newsTitle.setText(workPageChildNews.getTitle());
                holder.newsPublishTime.setText(workPageChildNews.getPublishTime());
                holder.newsPraiseCount.setText(workPageChildNews.getPraiseCount());
                holder.newsCommentCount.setText(workPageChildNews.getCommentCount());
                holder.newsBrowseCount.setText(workPageChildNews.getBrowseCount());
                return view;
        }
        class ViewHolder{
                private TextView newsTitle;
                private TextView newsPublishTime;
                private TextView newsPraiseCount;
                private TextView newsCommentCount;
                private TextView newsBrowseCount;

                public ViewHolder( View view){
                        newsTitle = (TextView) view.findViewById(R.id.news_title);
                        newsPublishTime = (TextView) view.findViewById(R.id.news_publish_time);
                        newsPraiseCount = (TextView) view.findViewById(R.id.heart_count);
                        newsCommentCount = (TextView) view.findViewById(R.id.comment_count);
                        newsBrowseCount = (TextView) view.findViewById(R.id.read_count);
                }
        }
}