package com.example.miic.contractManage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.miic.R;
import com.example.miic.contractManage.item.ContractGridView;

import java.util.List;

/**
 * Created by admin on 2018/9/4.
 */

public class ContractGridViewAdapter extends BaseAdapter{
        private Context context;
        private List<ContractGridView> list;

    public ContractGridViewAdapter(Context context, List<ContractGridView> list){
        this.context = context;
        this.list = list;
    }
        @Override
        public int getCount() {
        return list.size();//注意此处
    }

        @Override
        public ContractGridView getItem(int position) {
        return list.get(position);
    }

        @Override
        public long getItemId(int position) {
        return position;
    }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
            ContractGridViewAdapter.ViewHolder holder;
        if(convertView==null){
            view= LayoutInflater.from(context).inflate(R.layout.contract_grid_view_item,null);
            holder=new ContractGridViewAdapter.ViewHolder(view);
            view.setTag(holder);
        }else{
            view=convertView;
            holder= (ContractGridViewAdapter.ViewHolder) view.getTag();
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

        private ContractGridViewAdapter.onItemMyClickListener onMyClickListener;

        public void setOnItemMyClickListener(ContractGridViewAdapter.onItemMyClickListener onMyClickListener) {
            this.onMyClickListener = onMyClickListener;
        }

    }
