package com.example.miic.share.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.miic.R;
import com.example.miic.share.item.MomentCommentItem;
import com.example.miic.share.item.ShareMomentsItem;
import com.squareup.picasso.Picasso;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.List;

/**
 * Created by XuKe on 2018/4/26.
 */

public class MomentCommentItemAdapter extends BaseAdapter {
    private Context context;
    private List<MomentCommentItem> dataList;

    public MomentCommentItemAdapter(Context context, List<MomentCommentItem> dataList) {
        this.context = context;
        this.dataList = dataList;
    }
    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public MomentCommentItem getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        final ViewHolder holder;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.moments_comment, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        final MomentCommentItem item = dataList.get(position);
        String imgPath = item.getFromCommenterUrl();
        Picasso.with(context).load(imgPath).into(holder.fromCommenterUrl);
        holder.fromCommenterName.setText(item.getFromCommenterName());
        if(!item.getAuthorID().equals(item.getToCommenterID())){
            holder.toCommenterName.setText(" 回复 "+item.getToCommenterName());
        }
        holder.commentContent.setHtml(item.getCommentContent(), new HtmlHttpImageGetter(holder.commentContent, "http://share.miic.com.cn"));
        holder.commentTime.setText(item.getCommentTime());
        return view;
    }
    class ViewHolder {
        ImageView fromCommenterUrl;
        TextView fromCommenterName;
        TextView toCommenterName;
        TextView commentTime;
        HtmlTextView commentContent;

        public ViewHolder(View view) {
            fromCommenterUrl = (ImageView) view.findViewById(R.id.from_commenter_url);
            fromCommenterName = (TextView) view.findViewById(R.id.from_commenter_name);
            toCommenterName = (TextView) view.findViewById(R.id.to_commenter_name);
            commentContent = (HtmlTextView) view.findViewById(R.id.comment_content);
            commentTime = (TextView) view.findViewById(R.id.comment_time);
        }
    }
}
