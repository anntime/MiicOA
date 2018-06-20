package com.example.miic.contractManage.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.miic.R;
import com.example.miic.contractManage.adapter.ContractTimeLineItemAdapter;
import com.example.miic.contractManage.item.ContractApproveStatus;
import com.example.miic.contractManage.item.ContractTimeLineItem;

import java.util.ArrayList;
import java.util.List;

public class ContractManageDetailActivity extends AppCompatActivity {
    private LinearLayout backBtn;
    private String ClickID;

    private RecyclerView mRecyclerView;
    private ContractTimeLineItemAdapter mTimeLineAdapter;
    private List<ContractTimeLineItem> mDataList = new ArrayList<>();

    private TextView contractUseType;
    private TextView depatName;
    private TextView contractTime;
    private TextView contractReason;
    private TextView contractOrgnization;
    private TextView contractType;
    private TextView contractOperator;
    private TextView contractNum;
    private TextView contractAssessor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_manage_detail);
        contractUseType = (TextView)findViewById(R.id.contract_use_type);
        depatName = (TextView)findViewById(R.id.dept_name);
        contractTime = (TextView)findViewById(R.id.contract_time);
        contractReason = (TextView)findViewById(R.id.reason);
        contractOrgnization = (TextView)findViewById(R.id.organization);
        contractType = (TextView)findViewById(R.id.contract_type);
        contractOperator = (TextView)findViewById(R.id.operator);
        contractNum = (TextView)findViewById(R.id.contract_num);
        contractAssessor = (TextView)findViewById(R.id.assessor);

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
//        getContractApplyDetail();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);
        setDataListItems();
        mTimeLineAdapter = new ContractTimeLineItemAdapter(mDataList,ContractManageDetailActivity.this);
        mRecyclerView.setAdapter(mTimeLineAdapter);

    }

    private void setDataListItems(){
        mDataList.add(new ContractTimeLineItem("王宇静", "", ContractApproveStatus.ACTIVE));
        mDataList.add(new ContractTimeLineItem("王骥", "2017-02-10 15:00", ContractApproveStatus.COMPLETED));
        mDataList.add(new ContractTimeLineItem("宋家东", "2017-02-10 14:30", ContractApproveStatus.COMPLETED));
        mDataList.add(new ContractTimeLineItem("马技超", "2017-02-10 14:00", ContractApproveStatus.COMPLETED));
    }

}
