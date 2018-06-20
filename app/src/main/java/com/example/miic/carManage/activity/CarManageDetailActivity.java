package com.example.miic.carManage.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.miic.R;
import com.example.miic.carManage.adapter.CarTimeLineItemAdapter;
import com.example.miic.carManage.item.CarApproveStatus;
import com.example.miic.carManage.item.CarTimeLineItem;

import java.util.ArrayList;
import java.util.List;

public class CarManageDetailActivity extends AppCompatActivity {
    private LinearLayout backBtn;
    private String ClickID;

    private RecyclerView mRecyclerView;
    private CarTimeLineItemAdapter mTimeLineAdapter;
    private List<CarTimeLineItem> mDataList = new ArrayList<>();

    private TextView carUseType;
    private TextView depatName;
    private TextView carTime;
    private TextView carReason;
    private TextView carOrgnization;
    private TextView carType;
    private TextView carOperator;
    private TextView carNum;
    private TextView carAssessor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_manage_detail);
        carUseType = (TextView)findViewById(R.id.car_use_type);
        depatName = (TextView)findViewById(R.id.dept_name);
        carTime = (TextView)findViewById(R.id.car_time);
        carReason = (TextView)findViewById(R.id.reason);
        carOrgnization = (TextView)findViewById(R.id.organization);
        carType = (TextView)findViewById(R.id.car_type);
        carOperator = (TextView)findViewById(R.id.operator);
        carNum = (TextView)findViewById(R.id.car_num);
        carAssessor = (TextView)findViewById(R.id.assessor);

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
//        getCarApplyDetail();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);
        setDataListItems();
        mTimeLineAdapter = new CarTimeLineItemAdapter(mDataList,CarManageDetailActivity.this);
        mRecyclerView.setAdapter(mTimeLineAdapter);

    }

    private void setDataListItems(){
        mDataList.add(new CarTimeLineItem("王宇静", "", CarApproveStatus.ACTIVE));
        mDataList.add(new CarTimeLineItem("王骥", "2017-02-10 15:00", CarApproveStatus.COMPLETED));
        mDataList.add(new CarTimeLineItem("宋家东", "2017-02-10 14:30", CarApproveStatus.COMPLETED));
        mDataList.add(new CarTimeLineItem("马技超", "2017-02-10 14:00", CarApproveStatus.COMPLETED));
    }

}
