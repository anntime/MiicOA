package com.example.miic.qjManage.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.miic.R;


public class QjManageDetailActivity extends AppCompatActivity {
    private LinearLayout linBackBtn;
    private String ClickID;

    private TextView tvQjType;
    private TextView tvQjTime;
    private TextView tvQjdays;
    private TextView tvQjReason;
    private TextView tvQjOrgnization;
    private TextView tvQjmobile;
    private TextView tvQjOperator;
    private TextView tvApplyTime;
    private TextView tvApplyState;

    private TextView tvPeople;
    private TextView tvResult;
    private TextView tvOpinion;
    private TextView tvTime;
    private Button btnSubmit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qj_manage_detail);
        //获取上一页面传递的参数
        Intent intent = getIntent();
        String clickID = intent.getStringExtra("clickID");
        ClickID = clickID;

        init();
    }


    private void init(){
        linBackBtn = (LinearLayout)findViewById(R.id.search_back);
        //返回按钮事件
        linBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvQjType = (TextView)findViewById(R.id.qj_type);
        tvQjTime = (TextView)findViewById(R.id.qj_time);
        tvQjdays = (TextView)findViewById(R.id.qj_day);
        tvQjReason = (TextView)findViewById(R.id.qj_reason);
        tvQjOrgnization = (TextView)findViewById(R.id.apply_dept);
        tvQjmobile = (TextView)findViewById(R.id.mobile);
        tvQjOperator = (TextView)findViewById(R.id.operator);
        tvApplyTime = (TextView)findViewById(R.id.apply_time);
        tvApplyState = (TextView)findViewById(R.id.apply_state);

        //获取印章申请信息
        //getSealApplyDetail();



    }



}
