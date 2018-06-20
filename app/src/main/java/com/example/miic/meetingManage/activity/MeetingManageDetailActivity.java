package com.example.miic.meetingManage.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.miic.R;
import com.example.miic.meetingManage.adapter.MeetingTimeLineItemAdapter;
import com.example.miic.meetingManage.item.MeetingApproveStatus;
import com.example.miic.meetingManage.item.MeetingTimeLineItem;

import java.util.ArrayList;
import java.util.List;

public class MeetingManageDetailActivity extends AppCompatActivity {
    private LinearLayout backBtn;
    private String ClickID;

    private RecyclerView mRecyclerView;
    private MeetingTimeLineItemAdapter mTimeLineAdapter;
    private List<MeetingTimeLineItem> mDataList = new ArrayList<>();

    private TextView meetingUseType;
    private TextView depatName;
    private TextView meetingTime;
    private TextView meetingReason;
    private TextView meetingOrgnization;
    private TextView meetingType;
    private TextView meetingOperator;
    private TextView meetingNum;
    private TextView meetingAssessor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_manage_detail);
        meetingUseType = (TextView)findViewById(R.id.meeting_use_type);
        depatName = (TextView)findViewById(R.id.dept_name);
        meetingTime = (TextView)findViewById(R.id.meeting_time);
        meetingReason = (TextView)findViewById(R.id.reason);
        meetingOrgnization = (TextView)findViewById(R.id.organization);
        meetingType = (TextView)findViewById(R.id.meeting_type);
        meetingOperator = (TextView)findViewById(R.id.operator);
        meetingNum = (TextView)findViewById(R.id.meeting_num);
        meetingAssessor = (TextView)findViewById(R.id.assessor);

        backBtn = (LinearLayout)findViewById(R.id.search_back);
        //获取上一页面传递的参数
        Intent intent = getIntent();
        String clickID = intent.getStringExtra("clickID");
        ClickID = clickID;

        init();
    }


    private void init(){
        //返回按钮事件
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //获取印章申请信息
//        getMeetingApplyDetail();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);
        setDataListItems();
        mTimeLineAdapter = new MeetingTimeLineItemAdapter(mDataList,MeetingManageDetailActivity.this);
        mRecyclerView.setAdapter(mTimeLineAdapter);

    }

    private void setDataListItems(){
        mDataList.add(new MeetingTimeLineItem("王宇静", "", MeetingApproveStatus.ACTIVE));
        mDataList.add(new MeetingTimeLineItem("王骥", "2017-02-10 15:00", MeetingApproveStatus.COMPLETED));
        mDataList.add(new MeetingTimeLineItem("宋家东", "2017-02-10 14:30", MeetingApproveStatus.COMPLETED));
        mDataList.add(new MeetingTimeLineItem("马技超", "2017-02-10 14:00", MeetingApproveStatus.COMPLETED));
    }

}
