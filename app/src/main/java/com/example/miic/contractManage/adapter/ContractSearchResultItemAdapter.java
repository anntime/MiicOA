package com.example.miic.contractManage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.miic.R;
import com.example.miic.contractManage.common.ContractCommon;
import com.example.miic.contractManage.item.ContractSearchResultItem;
import com.example.miic.contractManage.item.ContractSearchResultItem;
import com.example.miic.sealManagement.adapter.SealSearchResultItemAdapter;

import java.util.List;

import static com.example.miic.oa.common.Setting.stampToDate;
import static com.example.miic.oa.common.Setting.strToDate;

/**
 * Created by XuKe on 2018/4/8.
 */

public class ContractSearchResultItemAdapter extends BaseAdapter {

    private Context context;
    private List<ContractSearchResultItem> list;


    public ContractSearchResultItemAdapter(Context context, List<ContractSearchResultItem> objects){
        this.context = context;
        this.list = objects;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ContractSearchResultItem getItem(int position) {
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
            view= LayoutInflater.from(context).inflate(R.layout.contract_search_result_item,null);
            holder=new ViewHolder(view);
            view.setTag(holder);
        }else{
            view=convertView;
            holder= (ViewHolder) view.getTag();
        }
        ContractSearchResultItem contract = getItem(position);
        holder.contractTitle.setText(contract.getContractTitle());
        holder.contractTime.setText(strToDate(contract.getContractTime()));
        holder.contractState.setText(new ContractCommon().getContractStatusStr(contract.getContractStatus()));
        //判断状态，设置审批状态字体的颜色！！！！！！！！！！！！！！！！！！！！！！！！！！
        holder.contractState.setTextColor(new ContractCommon().getContractStatusColorStr(contract.getContractStatus()));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMyClickListener.onMyClick(0, position);
            }
        });
        return view;
    }
    /**
     * 添加列表项
     * @param item
     */
    public void addItem(ContractSearchResultItem item) {
        list.add(item);
    }
    class ViewHolder{
        TextView contractTitle;
        TextView contractTime;
        TextView contractState;
        public ViewHolder(View view){
            contractTitle = (TextView) view.findViewById(R.id.contract_title);
            contractTime = (TextView) view.findViewById(R.id.contract_time);
            contractState = (TextView) view.findViewById(R.id.contract_state);
        }
    }
    public interface onItemMyClickListener {
        void onMyClick(int type, int listID);
    }

    private ContractSearchResultItemAdapter.onItemMyClickListener onMyClickListener;

    public void setOnItemMyClickListener(ContractSearchResultItemAdapter.onItemMyClickListener onMyClickListener) {
        this.onMyClickListener = onMyClickListener;
    }



}
