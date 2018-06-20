package com.example.miic.share.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.miic.R;
import com.example.miic.share.item.AuthenticationMessageItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.miic.oa.common.Setting.stampToTime;

/**
 * Created by XuKe on 2018/5/9.
 */

public class FriendApplyAdapter extends BaseAdapter {
    private Context context;
    private List<AuthenticationMessageItem> dataList;

    public FriendApplyAdapter(Context context, List<AuthenticationMessageItem> dataList) {
        this.context = context;
        this.dataList = dataList;
    }
    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public AuthenticationMessageItem getItem(int position) {
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
            view = LayoutInflater.from(context).inflate(R.layout.authentication_message_item, viewGroup,false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            view = convertView;
            holder =  (ViewHolder) view.getTag();
        }
        AuthenticationMessageItem item = dataList.get(position);
        holder.nameTv.setText(item.getUserName().toString());
        holder.conTv.setText(item.getApplyMessage().toString());
        holder.timeTv.setText(stampToTime(item.getApplyTime()).toString());
        String imgPath = item.getUserUrl();
        Picasso.with(context).load(imgPath).into(holder.imgIv);
        //接受
        holder.imageAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMyClickListener.onMyClick(0, position);
            }
        });
        //拒绝
        holder.imageRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMyClickListener.onMyClick(1, position);
            }
        });

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

    class ViewHolder {
        ImageView imgIv;
        TextView nameTv;
        TextView timeTv;
        TextView conTv;
        ImageView imageAccept;
        ImageView imageRefuse;
        public ViewHolder(View view) {
            imgIv = (ImageView) view.findViewById(R.id.iv_avatar);
            nameTv = (TextView) view.findViewById(R.id.tv_name);
            timeTv = (TextView) view.findViewById(R.id.tv_time);
            conTv = (TextView) view.findViewById(R.id.tv_message);
            imageAccept = (ImageView) view.findViewById(R.id.accept);
            imageRefuse = (ImageView) view.findViewById(R.id.refuse);
        }
    }
}
