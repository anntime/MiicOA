package com.example.miic.oa.work.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.common.MyApplication;
import com.example.miic.oa.work.item.WorkPageIcon;
import com.example.miic.qjManage.activity.QjManageActivity;
import com.example.miic.sealManagement.activity.SealManageActivity;

import java.util.List;

/**
 * Created by XuKe on 2018/2/9.
 */

public class WorkPageIconAdapter extends BaseAdapter {
    private Context context;
    private List<WorkPageIcon> list;

    public WorkPageIconAdapter(Context context, List<WorkPageIcon> list){
        this.context = context;
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();//注意此处
    }

    @Override
    public WorkPageIcon getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if(convertView==null){
            view=LayoutInflater.from(context).inflate(R.layout.grid_item_for_work_page,null);
            holder=new ViewHolder(view);
            view.setTag(holder);
        }else{
            view=convertView;
            holder= (ViewHolder) view.getTag();
        }
        holder.mImageView.setBackgroundResource(list.get(position).getIconSrc());
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String clickID = list.get(position).getIconID();
                if(clickID.equals("sealManage")){
                    Intent intent=new Intent(context,SealManageActivity.class);
                    context.startActivity(intent);
                    //Toast.makeText(MyApplication.getContext(),"待开发中……",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MyApplication.getContext(),"待开发中……",Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.mTextView.setText(list.get(position).getIconName());
        return view;
    }
    class ViewHolder{
        ImageView mImageView;
        TextView mTextView;
        public ViewHolder(View view){
            mImageView=(ImageView)view.findViewById(R.id.work_icon);
            mTextView=(TextView)view.findViewById(R.id.work_icon_name);
        }
    }

}