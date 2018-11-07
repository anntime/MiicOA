package com.example.miic.carManage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.miic.R;
import com.example.miic.carManage.item.CarSearchResultItem;
import com.example.miic.carManage.item.CarSearchResultItem;

import java.util.List;

/**
 * Created by XuKe on 2018/4/8.
 */

public class CarSearchResultItemAdapter extends BaseAdapter {

    private Context context;
    private List<CarSearchResultItem> list;


    public CarSearchResultItemAdapter(Context context, List<CarSearchResultItem> objects){
        this.context = context;
        this.list = objects;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CarSearchResultItem getItem(int position) {
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
            view= LayoutInflater.from(context).inflate(R.layout.car_search_result_item,null);
            holder=new ViewHolder(view);
            view.setTag(holder);
        }else{
            view=convertView;
            holder= (ViewHolder) view.getTag();
        }
        CarSearchResultItem car = getItem(position);
        holder.carTitle.setText(car.getCarTravelWay());
        holder.carTime.setText(car.getApplyTime());
        holder.carState.setText(car.getApproveState());
        //判断状态，设置审批状态字体的颜色！！！！！！！！！！！！！！！！！！！！！！！！！！
        holder.carState.setText(getApproveStateStr(car.getApproveState()));
        holder.carState.setTextColor(getApproveStateColorStr(car.getApproveState()));
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
    public void addItem(CarSearchResultItem item) {
        list.add(item);
    }
    class ViewHolder{
        TextView carTitle;
        TextView carTime;
        TextView carState;
        public ViewHolder(View view){
            carTitle = (TextView) view.findViewById(R.id.car_title);
            carTime = (TextView) view.findViewById(R.id.car_time);
            carState = (TextView) view.findViewById(R.id.car_state);
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
                result = "待还车";
                break;
            case "7":
                result = "待核查";
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
