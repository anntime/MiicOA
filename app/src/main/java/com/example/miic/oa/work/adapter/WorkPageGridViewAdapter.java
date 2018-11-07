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
import com.example.miic.carManage.activity.CarIndexActivity;
import com.example.miic.carManage.activity.CarManageActivity;
import com.example.miic.common.MyApplication;
import com.example.miic.contractManage.activity.ContractIndexActivity;
import com.example.miic.contractManage.activity.ContractManageActivity;
import com.example.miic.meetingManage.activity.MeetingManageActivity;
//import com.example.miic.meetingRoomManage.activity.MeetingRoomManageActivity;
import com.example.miic.meetingRoomManage.activity.MeetingRoomManageActivity;
import com.example.miic.oa.work.item.WorkPageGridView;
import com.example.miic.sealManagement.activity.SealIndexActivity;
import com.example.miic.sealManagement.activity.SealManageActivity;
import com.example.miic.qjManage.activity.QjManageActivity;
import com.example.miic.share.activity.ShareActivity;

import java.util.List;

/**
 * Created by XuKe on 2018/2/9.
 */

public class WorkPageGridViewAdapter extends BaseAdapter {
    private Context context;
    private List<WorkPageGridView> list;

    public WorkPageGridViewAdapter(Context context, List<WorkPageGridView> list){
        this.context = context;
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();//注意此处
    }

    @Override
    public WorkPageGridView getItem(int position) {
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
            view=LayoutInflater.from(context).inflate(R.layout.grid_item_for_column_page,null);
            holder=new ViewHolder(view);
            view.setTag(holder);
        }else{
            view=convertView;
            holder= (ViewHolder) view.getTag();
        }
        holder.mImageView.setBackgroundResource(list.get(position).getColumnIconSrc());
        holder.mTextView.setText(list.get(position).getColumnIconName());

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String clickID = list.get(position).getColumnIconID();
                if(clickID.equals("sealManage")){
                    Intent intent=new Intent(context,SealIndexActivity.class);
                    context.startActivity(intent);
                }else if(clickID.equals("contractManage")){
                    Intent intent=new Intent(context,ContractIndexActivity.class);
                    context.startActivity(intent);
                }else if(clickID.equals("meetingRoomManage")){
                    Intent intent=new Intent(context,MeetingRoomManageActivity.class);
                    context.startActivity(intent);
                } else if(clickID.equals("carManage")){
                    Intent intent=new Intent(context,CarIndexActivity.class);
                    context.startActivity(intent);
                } else if(clickID.equals("qjManage")){
                    Intent intent=new Intent(context,QjManageActivity.class);
                    context.startActivity(intent);
                }else if(clickID.equals("share")){
                    Intent intent=new Intent(context,ShareActivity.class);
                    context.startActivity(intent);
                }else{
                    Toast.makeText(MyApplication.getContext(),"待开发中……",Toast.LENGTH_SHORT).show();
                }
//                String clickImageID = list.get(position).getColumnIconID();
//                Log.i("点击图标的ID",clickImageID);
//
//                if(clickImageID=="IM"){
////                    final String target = "c98a5e43-7d06-1d60-b3a7-aada86ac317a";//"im1jeg08c98a5e43-7d06-1d60-b3a7-aada86ac317a"; //消息接收者ID
////                    final String appkey = MyApplication.getAPPKEY(); //消息接收者appKey
////                    Intent intent = MyApplication.getYWIMKit().getChattingActivityIntent(target, appkey);
////                    context.startActivity(intent);
//                    Intent intent=add Intent(context, MainActivity.class);
//                    context.startActivity(intent);
//                }else if(clickImageID=="address"){
//                    Intent intent=add Intent(context,ContactsActivity.class);
//                    context.startActivity(intent);
//                }else{
//                    Intent intent=add Intent(context,WorkChildActivity.class);
//                    intent.putExtra("clickIconID",clickImageID);
//                    context.startActivity(intent);
//                }
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

}