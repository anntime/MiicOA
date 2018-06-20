package com.example.miic.carManage.item;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.miic.R;
import com.example.miic.base.timeline.TimelineView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HP-HP on 05-12-2015.
 */
public class CarTimeLineViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.time_marker)
    public TimelineView timeLine;
    @BindView(R.id.operator)
    public TextView sealOperator;
    @BindView(R.id.approve_state)
    public TextView sealState;
    @BindView(R.id.time)
    public TextView sealTime;

    public CarTimeLineViewHolder(View itemView, int viewType) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        timeLine.initLine(viewType);
    }

}
