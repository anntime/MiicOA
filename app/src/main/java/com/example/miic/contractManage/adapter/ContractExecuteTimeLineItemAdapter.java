package com.example.miic.contractManage.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.miic.R;
import com.example.miic.base.timeline.TimelineView;
import com.example.miic.contractManage.item.ContractExecuteTimeLineItem;
import com.example.miic.contractManage.item.ContractTimeLineViewHolder;
import com.example.miic.contractManage.utils.VectorDrawableUtils;

import java.util.List;

/**
 * Created by admin on 2018/9/12.
 */

public class ContractExecuteTimeLineItemAdapter extends RecyclerView.Adapter<ContractTimeLineViewHolder> {
    private List<ContractExecuteTimeLineItem> mFeedList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public ContractExecuteTimeLineItemAdapter(List<ContractExecuteTimeLineItem> mFeedList, Context mContext) {
        this.mFeedList = mFeedList;
        this.mContext = mContext;
    }
    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position,getItemCount());
    }

    @Override
    public ContractTimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        mLayoutInflater = LayoutInflater.from(mContext);
        View view;
        view = mLayoutInflater.inflate(R.layout.timeline_item, parent, false);
        return new ContractTimeLineViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(ContractTimeLineViewHolder holder, int position) {
        ContractExecuteTimeLineItem timeLineModel = mFeedList.get(position);

//        if(position==0) {
//            holder.timeLine.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_inactive, android.R.color.darker_gray));
//        } else{
            holder.timeLine.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_active, R.color.colorPrimary));
//        }


        if(!timeLineModel.getApproveTime().isEmpty()) {
            holder.contractOperator.setVisibility(View.VISIBLE);
            holder.contractOperator.setText(timeLineModel.getApproveTime());
        }
        else
        holder.contractOperator.setVisibility(View.GONE);

        holder.contractTime.setText(timeLineModel.getPayWay());
        holder.contractState.setText(timeLineModel.getAmount());
    }

    @Override
    public int getItemCount() {
        return (mFeedList!=null? mFeedList.size():0);
    }

}
