package com.example.miic.contractManage.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectChangeListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.miic.R;
import com.example.miic.base.RandomCode;
import com.example.miic.base.http.PostRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPlanActivity extends AppCompatActivity {
    private EditText PlanAmountEt;
    private EditText RemarkEt;
    private Button SubmitBtn;

    private TimePickerView pvTime;
    private TextView btn_Time;


    private String ClickID;
    private JSONArray Planlist;
    private String ChooseTime;
    private float SubjectAmount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan);
        //获取上一页面传递的参数
        Intent intent = getIntent();
        ClickID= intent.getStringExtra("clickID");
        try{
            Planlist = new JSONArray(intent.getStringExtra("Planlist"));
        }catch (JSONException ex){
            Toast.makeText(AddPlanActivity.this,"json对象构建错误",Toast.LENGTH_LONG).show();
        }
        SubjectAmount = intent.getLongExtra("subjectAmount",0);
        setHeader();
        initView();
    }
    private void setHeader() {
        TextView titleTv = (TextView) findViewById(R.id.page_title);
        LinearLayout backLin = (LinearLayout)findViewById(R.id.back);
        LinearLayout rightLin = (LinearLayout)findViewById(R.id.menu);
        titleTv.setText("添加合同执行记录");
        rightLin.setVisibility(View.INVISIBLE);
        backLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView(){
        PlanAmountEt = (EditText)findViewById(R.id.plan_amount);
        RemarkEt = (EditText)findViewById(R.id.plan_remark);
        SubmitBtn = (Button)findViewById(R.id.submit_btn);
        SubmitBtn.setOnClickListener(ContractPlanSubmit);
        initPlanTime();
    }
    private View.OnClickListener ContractPlanSubmit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            ContractPlanSubmit();
        }
    };
    private void ContractPlanSubmit(){
        float totleAmount = 0;
        JSONObject requestJson = new JSONObject();
        try{
            for (int index = 0;index<Planlist.length();index++){
                totleAmount+= Planlist.getJSONObject(index).getLong("PlanAmount");
            }
            JSONObject val = new JSONObject();
            val.put("ID", RandomCode.getCode());
            val.put("ContractID", ClickID);
            val.put("PlanDate", ChooseTime);
            float planAmount = Long.parseLong(PlanAmountEt.getText().toString().trim());
            if(planAmount <= (SubjectAmount-totleAmount)){
                val.put("PlanAmount", planAmount);
            }else{
                Toast.makeText(AddPlanActivity.this,"计划金额超出上限",Toast.LENGTH_LONG).show();
                return;
            }

            val.put("Remark", RemarkEt.getText().toString());
            requestJson.put("contractPlanInfo",val);
        }catch (JSONException ex){
            Toast.makeText(AddPlanActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Call<String> call = PostRequest.Instance.request.ContractPlanSubmit(keywordView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        Boolean result = jsonObject.getBoolean("d");
                        if(result==true){
                            Toast.makeText(AddPlanActivity.this, "添加合同执行信息成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Toast.makeText(AddPlanActivity.this, "添加合同执行信息失败", Toast.LENGTH_SHORT).show();
                        }

                    }catch (JSONException ex){
                        Log.e("GetDetailInfo",ex.getMessage());
                        Toast.makeText(AddPlanActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }

            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(AddPlanActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //用印时间
    private void initPlanTime(){
        initTimePicker();
        btn_Time = (TextView) findViewById(R.id.plan_Time);
        Calendar cal = Calendar.getInstance();
        String date = cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DAY_OF_MONTH);
        ChooseTime =date;
        btn_Time.setText(date);
        btn_Time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pvTime.show(view);//弹出时间选择器，传递参数过去，回调的时候则可以绑定此view
            }
        });
    }
    private void initTimePicker() {
        Calendar endDate = Calendar.getInstance();
        endDate.set(2030,1,1);
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                Toast.makeText(AddPlanActivity.this, getTime(date).split(" ")[0], Toast.LENGTH_SHORT).show();
                btn_Time.setText(getTime(date).split(" ")[0]);
                ChooseTime = getTime(date).split(" ")[0];
                Log.i("pvTime", "onTimeSelect");


            }
        }).setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
            @Override
            public void onTimeSelectChanged(Date date) {
                //btn_Time.setText(getTime(date).split(" ")[0]);
                Log.i("pvTime", "onTimeSelectChanged");
            }
        })
                .setType(new boolean[]{true, true, true, false, false, false})
                .setRangDate(Calendar.getInstance(),endDate)//起始终止年月日设定
                .build();

    }
    private String getTime(Date date) {//可根据需要自行截取数据显示
        Log.d("getTime()", "choice date millis: " + date.getTime());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }
}
