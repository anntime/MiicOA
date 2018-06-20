package com.example.miic.share.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.miic.R;
import com.example.miic.share.item.FriendContactItem;
import com.example.miic.share.item.SearchUserItem;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by admin on 2018/5/11.
 */

public class SearchUserAdapter extends BaseAdapter {
    private List<SearchUserItem> dataList;
    private Context context;

    public SearchUserAdapter(Context context,List<SearchUserItem> contacts) {
        this.dataList = contacts;
        this.context = context;
    }
    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public SearchUserItem getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        View view;
        final ViewHolder holder;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.share_contacts_item, viewGroup,false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            view = convertView;
            holder =  (ViewHolder) view.getTag();
        }
        SearchUserItem item = dataList.get(position);
        holder.tvName.setText(item.getName());
        String imgPath = item.getUrl();
        Picasso.with(context).load(imgPath).into(holder.ivAvatar);
        holder.tvIndex.setVisibility(View.GONE);
        //添加好友
        if(item.getIsMyFriend()==false){
            holder.addBtn.setVisibility(View.VISIBLE);
            holder.addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onMyClickListener.onMyClick(0, position);
                }
            });
        }else{
            holder.addBtn.setVisibility(View.VISIBLE);
            holder.addBtn.setText("已互为好友");
        }

        return view;
    }

    public interface onItemMyClickListener {
        /**
         *监听接口：点赞、评论点击事件
         * @param type:事件类型
         * @param listID：列表的位置id
         */
        void onMyClick(int type, int listID);
    }

    private onItemMyClickListener onMyClickListener;

    public void setOnItemMyClickListener(onItemMyClickListener onMyClickListener) {
        this.onMyClickListener = onMyClickListener;
    }


    class ViewHolder{
        public TextView tvIndex;
        public ImageView ivAvatar;
        public TextView tvName;
        public TextView addBtn;

        public ViewHolder(View itemView) {
            tvIndex = (TextView) itemView.findViewById(R.id.tv_index);
            ivAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            addBtn = (TextView)itemView.findViewById(R.id.add_btn);
        }
    }
}
