package com.example.miic.meetingManage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.miic.R;
import com.example.miic.meetingManage.item.MeetingSearchResultItem;
import com.example.miic.meetingManage.item.MeetingSearchResultItem;

import java.util.List;

/**
 * Created by XuKe on 2018/4/8.
 */

public class MeetingSearchResultItemAdapter extends BaseAdapter {

    private Context context;
    private List<MeetingSearchResultItem> list;


    public MeetingSearchResultItemAdapter(Context context, List<MeetingSearchResultItem> objects){
        this.context = context;
        this.list = objects;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public MeetingSearchResultItem getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view;
        ViewHolder holder;
        if(convertView==null){
            view= LayoutInflater.from(context).inflate(R.layout.meeting_search_result_item,null);
            holder=new ViewHolder(view);
            view.setTag(holder);
        }else{
            view=convertView;
            holder= (ViewHolder) view.getTag();
        }
        MeetingSearchResultItem meeting = getItem(position);
        holder.meetingTitle.setText(meeting.getMeetingApplyTitle());
        holder.meetingTime.setText(meeting.getApplyTime());
        holder.meetingState.setText(meeting.getApproveState());
        //判断状态，设置审批状态字体的颜色！！！！！！！！！！！！！！！！！！！！！！！！！！
        return view;
    }
    /**
     * 添加列表项
     * @param item
     */
    public void addItem(MeetingSearchResultItem item) {
        list.add(item);
    }
    class ViewHolder{
        TextView meetingTitle;
        TextView meetingTime;
        TextView meetingState;
        public ViewHolder(View view){
            meetingTitle = (TextView) view.findViewById(R.id.meeting_title);
            meetingTime = (TextView) view.findViewById(R.id.meeting_time);
            meetingState = (TextView) view.findViewById(R.id.meeting_state);
        }
    }
}
