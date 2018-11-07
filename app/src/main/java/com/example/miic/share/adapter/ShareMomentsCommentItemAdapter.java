package com.example.miic.share.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.example.miic.R;
import com.example.miic.share.item.ShareMomentsCommentItem;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.io.Serializable;
import java.util.List;

/**
 * Created by XuKe on 2018/4/27.
 */

public class ShareMomentsCommentItemAdapter  extends BaseAdapter implements Serializable {
    private Context context;
    private List<ShareMomentsCommentItem> dataList;

    public ShareMomentsCommentItemAdapter(Context context, List<ShareMomentsCommentItem> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public ShareMomentsCommentItem getItem(int position) {
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
            view = LayoutInflater.from(context).inflate(R.layout.share_moments_comment_item,parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        final ShareMomentsCommentItem entity = dataList.get(position);
        String fromCommenterName = entity.getFromCommenterName();
        String toCommenterName =entity.getToCommenterName();
        String commentContent = entity.getCommentContent();
        if(entity.getAuthorID().equals(entity.getToCommenterID())){
            holder.comments.setText(fromCommenterName+"："+commentContent);
        }else{
            holder.comments.setText(fromCommenterName+" 回复 "+toCommenterName+"："+commentContent);
        }

        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMyClickListener.onMyClick(0, position, dataList);
            }
        });
        return view;
    }
    //评论点击事件
    public interface onItemMyClickListener {
        void onMyClick(int type, int position,List<ShareMomentsCommentItem> dataList);
    }

    private onItemMyClickListener onMyClickListener;

    public void setOnItemMyClickListener(onItemMyClickListener onMyClickListener) {
        this.onMyClickListener = onMyClickListener;
    }


    class ViewHolder {
        HtmlTextView comments;

        public ViewHolder(View view) {
            comments = (HtmlTextView) view.findViewById(R.id.comments);

        }
    }
}
