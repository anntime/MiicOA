package com.example.miic.contractManage.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.miic.R;
import com.example.miic.base.timeline.TimelineView;
import com.example.miic.contractManage.item.ContractApproveStatus;
import com.example.miic.contractManage.item.ContractTimeLineItem;
import com.example.miic.contractManage.item.ContractTimeLineViewHolder;
import com.example.miic.contractManage.utils.DateTimeUtils;
import com.example.miic.contractManage.utils.VectorDrawableUtils;

import java.util.List;

/**
 * Created by XuKe on 2018/4/9.
 */

public class ContractTimeLineItemAdapter extends RecyclerView.Adapter<ContractTimeLineViewHolder> {
    private List<ContractTimeLineItem> mFeedList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public ContractTimeLineItemAdapter(List<ContractTimeLineItem> mFeedList, Context mContext) {
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
        ContractTimeLineItem timeLineModel = mFeedList.get(position);

        if(timeLineModel.getApproveState() == ContractApproveStatus.Create) {
            holder.timeLine.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_inactive, android.R.color.darker_gray));
        } else{
            holder.timeLine.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_active, R.color.colorPrimary));
        }
//        else (timeLineModel.getApproveState() == ContractApproveStatus.Create)  {
//            holder.timeLine.setMarker(ContextCompat.getDrawable(mContext, R.drawable.ic_marker), ContextCompat.getColor(mContext, android.R.color.darker_gray));
//        }

//        holder.timeLine.setStartLine(ContextCompat.getColor(mContext, android.R.color.darker_gray), 0);
//        holder.timeLine.setEndLine(ContextCompat.getColor(mContext, android.R.color.darker_gray), 0);

        if(!timeLineModel.getApproveTime().isEmpty()) {
            holder.contractTime.setVisibility(View.VISIBLE);
            //holder.contractTime.setText(DateTimeUtils.parseDateTime(timeLineModel.getApproveTime(), "yyyy-MM-dd HH:mm", "hh:mm a, dd-MMM-yyyy"));
            holder.contractTime.setText("2018-03-22 09:20:12");
        }
        else
            holder.contractTime.setVisibility(View.GONE);

        holder.contractState.setText(timeLineModel.getApproveState().getName());
        holder.contractOperator.setText(timeLineModel.getApproveName());
    }

    @Override
    public int getItemCount() {
        return (mFeedList!=null? mFeedList.size():0);
    }


}
