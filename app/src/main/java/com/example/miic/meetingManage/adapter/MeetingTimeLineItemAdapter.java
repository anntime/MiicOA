package com.example.miic.meetingManage.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.miic.R;
import com.example.miic.base.timeline.TimelineView;
import com.example.miic.meetingManage.item.MeetingApproveStatus;
import com.example.miic.meetingManage.item.MeetingTimeLineItem;
import com.example.miic.meetingManage.item.MeetingTimeLineViewHolder;
import com.example.miic.meetingManage.utils.DateTimeUtils;
import com.example.miic.meetingManage.utils.VectorDrawableUtils;

import java.util.List;

/**
 * Created by XuKe on 2018/4/9.
 */

public class MeetingTimeLineItemAdapter extends RecyclerView.Adapter<MeetingTimeLineViewHolder> {
    private List<MeetingTimeLineItem> mFeedList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public MeetingTimeLineItemAdapter(List<MeetingTimeLineItem> mFeedList, Context mContext) {
        this.mFeedList = mFeedList;
        this.mContext = mContext;
    }
    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position,getItemCount());
    }

    @Override
    public MeetingTimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        mLayoutInflater = LayoutInflater.from(mContext);
        View view;
        view = mLayoutInflater.inflate(R.layout.timeline_item, parent, false);
        return new MeetingTimeLineViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(MeetingTimeLineViewHolder holder, int position) {
        MeetingTimeLineItem timeLineModel = mFeedList.get(position);

        if(timeLineModel.getApproveState() == MeetingApproveStatus.INACTIVE) {
            holder.timeLine.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_inactive, android.R.color.darker_gray));
        } else if(timeLineModel.getApproveState() == MeetingApproveStatus.ACTIVE) {
            holder.timeLine.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_active, R.color.colorPrimary));
        } else {
            holder.timeLine.setMarker(ContextCompat.getDrawable(mContext, R.drawable.ic_marker), ContextCompat.getColor(mContext, android.R.color.darker_gray));
        }

//        holder.timeLine.setStartLine(ContextCompat.getColor(mContext, android.R.color.darker_gray), 0);
//        holder.timeLine.setEndLine(ContextCompat.getColor(mContext, android.R.color.darker_gray), 0);

        if(!timeLineModel.getApproveTime().isEmpty()) {
            holder.meetingTime.setVisibility(View.VISIBLE);
            //holder.meetingTime.setText(DateTimeUtils.parseDateTime(timeLineModel.getApproveTime(), "yyyy-MM-dd HH:mm", "hh:mm a, dd-MMM-yyyy"));
            holder.meetingTime.setText("2018-03-22 09:20:12");
        }
        else
            holder.meetingTime.setVisibility(View.GONE);

        holder.meetingState.setText(timeLineModel.getApproveState().getName());
        holder.meetingOperator.setText(timeLineModel.getApproveName());
    }

    @Override
    public int getItemCount() {
        return (mFeedList!=null? mFeedList.size():0);
    }


}
