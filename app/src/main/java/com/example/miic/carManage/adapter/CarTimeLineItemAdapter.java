package com.example.miic.carManage.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.miic.R;
import com.example.miic.base.timeline.TimelineView;
import com.example.miic.carManage.item.CarApproveStatus;
import com.example.miic.carManage.item.CarTimeLineItem;
import com.example.miic.carManage.item.CarTimeLineViewHolder;
import com.example.miic.carManage.utils.VectorDrawableUtils;

import java.util.List;

/**
 * Created by XuKe on 2018/4/9.
 */

public class CarTimeLineItemAdapter extends RecyclerView.Adapter<CarTimeLineViewHolder> {
    private List<CarTimeLineItem> mFeedList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public CarTimeLineItemAdapter(List<CarTimeLineItem> mFeedList, Context mContext) {
        this.mFeedList = mFeedList;
        this.mContext = mContext;
    }
    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position,getItemCount());
    }

    @Override
    public CarTimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        mLayoutInflater = LayoutInflater.from(mContext);
        View view;
        view = mLayoutInflater.inflate(R.layout.timeline_item, parent, false);
        return new CarTimeLineViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(CarTimeLineViewHolder holder, int position) {
        CarTimeLineItem timeLineModel = mFeedList.get(position);

        if(timeLineModel.getApproveState() == CarApproveStatus.Create) {
            holder.timeLine.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_inactive, android.R.color.darker_gray));
        } else {
            holder.timeLine.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_active, R.color.colorPrimary));
        }

//        holder.timeLine.setStartLine(ContextCompat.getColor(mContext, android.R.color.darker_gray), 0);
//        holder.timeLine.setEndLine(ContextCompat.getColor(mContext, android.R.color.darker_gray), 0);

        if(!timeLineModel.getApproveTime().isEmpty()) {
            holder.sealTime.setVisibility(View.VISIBLE);
            //holder.sealTime.setText(DateTimeUtils.parseDateTime(timeLineModel.getApproveTime(), "yyyy-MM-dd HH:mm", "hh:mm a, dd-MMM-yyyy"));
            holder.sealTime.setText("2018-03-22 09:20:12");
        }
        else
            holder.sealTime.setVisibility(View.GONE);

        holder.sealState.setText(timeLineModel.getApproveState().getName());
        holder.sealOperator.setText(timeLineModel.getApproveName());
    }

    @Override
    public int getItemCount() {
        return (mFeedList!=null? mFeedList.size():0);
    }


}
