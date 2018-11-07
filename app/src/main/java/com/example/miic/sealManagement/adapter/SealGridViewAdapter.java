package com.example.miic.sealManagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.miic.R;
import com.example.miic.sealManagement.item.SealGridView;

import java.util.List;

/**
 * Created by admin on 2018/9/4.
 */

public class SealGridViewAdapter extends BaseAdapter{
        private Context context;
        private List<SealGridView> list;

    public SealGridViewAdapter(Context context, List<SealGridView> list){
        this.context = context;
        this.list = list;
    }
        @Override
        public int getCount() {
        return list.size();//注意此处
    }

        @Override
        public SealGridView getItem(int position) {
        return list.get(position);
    }

        @Override
        public long getItemId(int position) {
        return position;
    }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
            SealGridViewAdapter.ViewHolder holder;
        if(convertView==null){
            view= LayoutInflater.from(context).inflate(R.layout.seal_grid_view_item,null);
            holder=new SealGridViewAdapter.ViewHolder(view);
            view.setTag(holder);
        }else{
            view=convertView;
            holder= (SealGridViewAdapter.ViewHolder) view.getTag();
        }
        holder.mImageView.setBackgroundResource(list.get(position).getColumnIconSrc());
        holder.mTextView.setText(list.get(position).getColumnIconName());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMyClickListener.onMyClick(0, position);
            }
        });

        return view;
    }
        class ViewHolder{
            ImageView mImageView;
            TextView mTextView;
            public ViewHolder(View view){
                mImageView=(ImageView)view.findViewById(R.id.column_icon);
                mTextView=(TextView)view.findViewById(R.id.column_icon_name);
            }
        }
        public interface onItemMyClickListener {
            void onMyClick(int type, int listID);
        }

        private SealGridViewAdapter.onItemMyClickListener onMyClickListener;

        public void setOnItemMyClickListener(SealGridViewAdapter.onItemMyClickListener onMyClickListener) {
            this.onMyClickListener = onMyClickListener;
        }

    }
