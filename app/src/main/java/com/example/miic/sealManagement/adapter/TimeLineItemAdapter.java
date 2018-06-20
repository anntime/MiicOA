package com.example.miic.sealManagement.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.miic.R;
import com.example.miic.base.timeline.TimelineView;
import com.example.miic.sealManagement.item.ApproveStatus;
import com.example.miic.sealManagement.item.TimeLineItem;
import com.example.miic.sealManagement.item.TimeLineViewHolder;
import com.example.miic.sealManagement.utils.DateTimeUtils;
import com.example.miic.sealManagement.utils.VectorDrawableUtils;

import java.util.List;

import static com.example.miic.oa.common.Setting.stampToDate;

/**
 * Created by XuKe on 2018/4/9.
 */

public class TimeLineItemAdapter extends RecyclerView.Adapter<TimeLineViewHolder> {
    private List<TimeLineItem> mFeedList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public TimeLineItemAdapter(List<TimeLineItem> mFeedList, Context mContext) {
        this.mFeedList = mFeedList;
        this.mContext = mContext;
    }
    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position,getItemCount());
    }

    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        mLayoutInflater = LayoutInflater.from(mContext);
        View view;
        view = mLayoutInflater.inflate(R.layout.timeline_item, parent, false);
        return new TimeLineViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(TimeLineViewHolder holder, int position) {
        TimeLineItem timeLineModel = mFeedList.get(position);

//        if(timeLineModel.getApproveState() == ApproveStatus.FINISH) {
//            holder.timeLine.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_inactive, android.R.color.darker_gray));
//        } else if(timeLineModel.getApproveState() == ApproveStatus.APPROVEING) {
//            holder.timeLine.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_active, R.color.colorPrimary));
//        } else {
//            holder.timeLine.setMarker(ContextCompat.getDrawable(mContext, R.drawable.ic_marker), ContextCompat.getColor(mContext, android.R.color.darker_gray));
//        }
        if (position==0){
            holder.timeLine.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_active, R.color.colorPrimary));

        }else{
            holder.timeLine.setMarker(ContextCompat.getDrawable(mContext, R.drawable.ic_marker), ContextCompat.getColor(mContext, android.R.color.darker_gray));
        }

//        holder.timeLine.setStartLine(ContextCompat.getColor(mContext, android.R.color.darker_gray), 0);
//        holder.timeLine.setEndLine(ContextCompat.getColor(mContext, android.R.color.darker_gray), 0);

        if(!timeLineModel.getApproveTime().isEmpty()) {
            holder.sealTime.setVisibility(View.VISIBLE);

            holder.sealTime.setText(stampToDate(timeLineModel.getApproveTime()));
//            holder.sealTime.setText("2018-03-22 09:20:12");
        }
        else
            holder.sealTime.setVisibility(View.GONE);

//        holder.sealState.setText(timeLineModel.getApproveState().getName());
        holder.sealState.setText(timeLineModel.getRemark());
        holder.sealOperator.setText(timeLineModel.getApproveName());
    }

    @Override
    public int getItemCount() {
        return (mFeedList!=null? mFeedList.size():0);
    }


}
