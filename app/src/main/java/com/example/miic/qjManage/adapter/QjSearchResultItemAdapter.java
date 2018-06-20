package com.example.miic.qjManage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.miic.R;
import com.example.miic.qjManage.item.QjSearchResultItem;

import java.util.List;

/**
 * Created by XuKe on 2018/4/8.
 */

public class QjSearchResultItemAdapter extends BaseAdapter {

    private Context context;
    private List<QjSearchResultItem> list;


    public QjSearchResultItemAdapter(Context context, List<QjSearchResultItem> objects){
        this.context = context;
        this.list = objects;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public QjSearchResultItem getItem(int position) {
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
            view= LayoutInflater.from(context).inflate(R.layout.qj_search_result_item,null);
            holder=new ViewHolder(view);
            view.setTag(holder);
        }else{
            view=convertView;
            holder= (ViewHolder) view.getTag();
        }
        QjSearchResultItem seal = getItem(position);
        holder.sealTitle.setText(seal.getQjApplyTitle());
        holder.sealTime.setText(seal.getApplyTime());
        holder.sealState.setText(seal.getApproveState());
        //判断状态，设置审批状态字体的颜色！！！！！！！！！！！！！！！！！！！！！！！！！！
        return view;
    }
    /**
     * 添加列表项
     * @param item
     */
    public void addItem(QjSearchResultItem item) {
        list.add(item);
    }
    class ViewHolder{
        TextView sealTitle;
        TextView sealTime;
        TextView sealState;
        public ViewHolder(View view){
            sealTitle = (TextView) view.findViewById(R.id.seal_title);
            sealTime = (TextView) view.findViewById(R.id.seal_time);
            sealState = (TextView) view.findViewById(R.id.seal_state);
        }
    }
}
