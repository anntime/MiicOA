package com.example.miic.carManage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.miic.R;
import com.example.miic.carManage.item.CarGridView;

import java.util.List;

/**
 * Created by admin on 2018/9/4.
 */

public class CarGridViewAdapter extends BaseAdapter{
        private Context context;
        private List<CarGridView> list;

    public CarGridViewAdapter(Context context, List<CarGridView> list){
        this.context = context;
        this.list = list;
    }
        @Override
        public int getCount() {
        return list.size();//注意此处
    }

        @Override
        public CarGridView getItem(int position) {
        return list.get(position);
    }

        @Override
        public long getItemId(int position) {
        return position;
    }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
            CarGridViewAdapter.ViewHolder holder;
        if(convertView==null){
            view= LayoutInflater.from(context).inflate(R.layout.car_grid_view_item,null);
            holder=new CarGridViewAdapter.ViewHolder(view);
            view.setTag(holder);
        }else{
            view=convertView;
            holder= (CarGridViewAdapter.ViewHolder) view.getTag();
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

        private CarGridViewAdapter.onItemMyClickListener onMyClickListener;

        public void setOnItemMyClickListener(CarGridViewAdapter.onItemMyClickListener onMyClickListener) {
            this.onMyClickListener = onMyClickListener;
        }

    }
