package com.example.miic.sealManagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.miic.R;
import com.example.miic.sealManagement.item.SealSearchResultItem;
import com.example.miic.sealManagement.item.SealSearchResultItem;

import java.util.List;

import static com.example.miic.oa.common.Setting.stampToDate;

/**
 * Created by XuKe on 2018/4/8.
 */

public class SealSearchResultItemAdapter extends BaseAdapter {

    private Context context;
    private List<SealSearchResultItem> list;


    public SealSearchResultItemAdapter(Context context, List<SealSearchResultItem> objects){
        this.context = context;
        this.list = objects;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public SealSearchResultItem getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View view;
        ViewHolder holder;
        if(convertView==null){
            view= LayoutInflater.from(context).inflate(R.layout.seal_search_result_item,null);
            holder=new ViewHolder(view);
            view.setTag(holder);
        }else{
            view=convertView;
            holder= (ViewHolder) view.getTag();
        }
        SealSearchResultItem seal = getItem(position);
        holder.sealTitle.setText(seal.getSealApplyTitle());
        holder.sealTime.setText(stampToDate(seal.getApplyTime()));
        holder.sealState.setText(getApproveStateStr(seal.getApproveState()));
        holder.sealState.setTextColor(getApproveStateColorStr(seal.getApproveState()));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMyClickListener.onMyClick(0, position);
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
    /**
     * 添加列表项
     * @param item
     */
    public void addItem(SealSearchResultItem item) {
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
    private String getApproveStateStr(String state){
        String result="";
        switch (state) {
            case "0":
                result = "全部";
                break;
            case "1":
                result = "待提交";
                break;
            case "2":
                result = "审批中";
                break;
            case "3":
                result = "同意(已完成)";
                break;
            case "4":
                result = "同意(已归档)";
                break;
            case "5":
                result = "不同意(已归档)";
                break;
            case "7":
                result = "监印中";
                break;
            case "8":
                result = "已完成";
                break;
            default:
                result = "状态不明";
                break;
        }
        return result;
    }
    private int getApproveStateColorStr(String state){
         int result= 0xff666666;
        switch (state) {
            case "0":
                result = 0xffBA57ED;
                break;
            case "1":
                result = 0xff62B264;
                break;
            case "2":
                result = 0xffFFCC33;
                break;
            case "3":
                result = 0xffFF33CC;
                break;
            case "4":
                result = 0xff63b8ff;
                break;
            case "5":
                result = 0xffFFCC33;
                break;
            case "7":
                result = 0xffFF33CC;
                break;
            case "8":
                result = 0xff63b8ff;
                break;
            default:
                break;
        }
        return result;
    }

}

