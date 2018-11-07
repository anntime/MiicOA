package com.example.miic.oa.work.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.carManage.activity.CarManageActivity;
import com.example.miic.common.MyApplication;
import com.example.miic.contractManage.activity.ContractIndexActivity;
import com.example.miic.contractManage.activity.ContractManageActivity;
import com.example.miic.meetingRoomManage.activity.MeetingRoomManageActivity;
import com.example.miic.oa.work.item.WorkPageItem;
import com.example.miic.sealManagement.activity.SealManageActivity;

import java.util.List;

/**
 * Created by XuKe on 2018/2/9.
 */

public class WorkPageItemAdapter extends BaseAdapter {
    private Context context;
    private List<WorkPageItem> list;

    public WorkPageItemAdapter(Context context, List<WorkPageItem> list){
        this.context = context;
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();//注意此处
    }

    @Override
    public WorkPageItem getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        final WorkPageItem columnItem = getItem(position);
        if(convertView==null){
            view=LayoutInflater.from(context).inflate(R.layout.column_child_item,null);
            holder=new ViewHolder(view);
            view.setTag(holder);
        }else{
            view=convertView;
            holder= (ViewHolder) view.getTag();
        }
        if(columnItem.getColumnTitle()!=""){
            holder.mTextView.setText(columnItem.getColumnTitle());
        }
        WorkPageGridViewAdapter columnGridViewAdapter = new WorkPageGridViewAdapter(context,columnItem.getColumnGridView());
        holder.mGridView.setAdapter(columnGridViewAdapter);
        holder.mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //获得点击的图标的ID，然后将id传递到下一页，
                String clickIconID = columnItem.getColumnGridView().get(position).getColumnIconID();
                Intent intent;
                switch (clickIconID){
                    case "sealManage":
                        intent=new Intent(context,SealManageActivity.class);
                        context.startActivity(intent);
                        break;
                    case "contractManage":
                        intent=new Intent(context, ContractIndexActivity.class);
                        context.startActivity(intent);
                        break;
                    case "carManage":
                        intent=new Intent(context,CarManageActivity.class);
                        context.startActivity(intent);
                        break;
                    case "meetingRoomManage":
                        intent=new Intent(context,MeetingRoomManageActivity.class);
                        context.startActivity(intent);
                        break;
                    default:
                        Toast.makeText(context,"该功能尚未开放~",Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });

        return view;
    }
    class ViewHolder{
        TextView mTextView;
        GridView mGridView;
        public ViewHolder(View view){
            mTextView=(TextView)view.findViewById(R.id.cloumn_title);
            mGridView = (GridView)view.findViewById(R.id.cloumn_gridview);
        }
    }

}