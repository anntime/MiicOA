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
    public View getView(int position, View convertView, ViewGroup parent){
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
        holder.carTitle.setText(car.getCarApplyTitle());
        holder.carTime.setText(car.getApplyTime());
        holder.carState.setText(car.getApproveState());
        //判断状态，设置审批状态字体的颜色！！！！！！！！！！！！！！！！！！！！！！！！！！
        return view;
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
}
