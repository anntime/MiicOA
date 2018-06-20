package com.example.miic.oa.search.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.miic.R;
import com.example.miic.oa.search.item.SearchPageNews;

import java.util.List;

/**
 * Created by XuKe on 2018/1/31.
 * 新闻Adapter 实现listview的数据填充
 */

public class SearchPageNewsAdapter extends BaseAdapter {
        private  Context context;
        private List<SearchPageNews> list;
        public SearchPageNewsAdapter(Context context,List<SearchPageNews> objects){
                this.context = context;
                this.list = objects;
        }
        @Override
        public int getCount() {
                return list.size();//注意此处
        }

        @Override
        public SearchPageNews getItem(int position) {
                return list.get(position);
        }

        @Override
        public long getItemId(int position) {
                return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
                SearchPageNews searchNews = getItem(position);
                View view;
                ViewHolder holder;
                if(convertView==null){
                        view=LayoutInflater.from(context).inflate(R.layout.search_news_item,null);
                        holder=new ViewHolder(view);
                        view.setTag(holder);
                }else{
                        view=convertView;
                        holder= (ViewHolder) view.getTag();
                }
                SearchPageNews news = getItem(position);
                holder.newsTitle.setText(news.getTitle());
                holder.newsPublishTime.setText(news.getPublishTime());
                holder.newsPraiseCount.setText(news.getPraiseCount());
                if(news.getCanComment().equals("1")){
                        //可以评论
                        holder.newsCanComment.setVisibility(View.VISIBLE);
                        holder.newsCommentCount.setText(news.getCommentCount());
                }else{
                        holder.newsCanComment.setVisibility(View.GONE);
                }
                holder.newsBrowseCount.setText(news.getBrowseCount());
                return view;
        }
        class ViewHolder{
                TextView newsTitle;
                TextView newsPublishTime;
                TextView newsPraiseCount;
                TextView newsCommentCount;
                TextView newsBrowseCount;
                LinearLayout newsCanComment;
                public ViewHolder(View view){
                        newsTitle = (TextView) view.findViewById(R.id.news_title);
                        newsPublishTime = (TextView) view.findViewById(R.id.news_publish_time);
                        newsPraiseCount = (TextView) view.findViewById(R.id.heart_count);
                        newsCommentCount = (TextView) view.findViewById(R.id.comment_count);
                        newsBrowseCount = (TextView) view.findViewById(R.id.read_count);
                        newsCanComment = (LinearLayout)view.findViewById(R.id.can_comment);
                }
        }
}