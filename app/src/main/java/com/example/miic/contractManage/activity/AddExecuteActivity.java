package com.example.miic.contractManage.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectChangeListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.miic.R;
import com.example.miic.base.RandomCode;
import com.example.miic.base.http.PostRequest;
import com.example.miic.sealManagement.common.SealCommon;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddExecuteActivity extends AppCompatActivity {

    private EditText ExecuteAmountEt;
    private EditText RemarkEt;
    private Button SubmitBtn;

    private TimePickerView pvTime;
    private TextView btn_Time;


    private Spinner PayWaySp;
    private List<String> ListPayWay;
    private ArrayAdapter<String> PayWayAdapter;

    private String ClickID;
    private String ChoosePayWay;
    private String ChooseTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_execute);
        //获取上一页面传递的参数
        Intent intent = getIntent();
        ClickID= intent.getStringExtra("clickID");
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
        ExecuteAmountEt = (EditText)findViewById(R.id.execute_amount);
        RemarkEt = (EditText)findViewById(R.id.remark);
        SubmitBtn = (Button)findViewById(R.id.submit_execute_btn);
        SubmitBtn.setOnClickListener(ContractExecuteSubmitListener);
        initPayWay();
        initExecuteTime();
    }
    private View.OnClickListener ContractExecuteSubmitListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            ContractExecuteSubmit();
        }
    };
    private void ContractExecuteSubmit(){
        JSONObject requestJson = new JSONObject();
        try{
            JSONObject val = new JSONObject();
            val.put("ID", RandomCode.getCode());
            val.put("ContractID", ClickID);
            val.put("ExecuteDate", ChooseTime);
            val.put("ExecuteAmount", ExecuteAmountEt.getText().toString());
            val.put("PayMode", ChoosePayWay );
            val.put("Remark", RemarkEt.getText().toString());
            requestJson.put("contractExecuteInfo",val);
        }catch (JSONException ex){
            Toast.makeText(AddExecuteActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Call<String> call = PostRequest.Instance.request.ContractExecuteSubmit(keywordView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        Boolean result = jsonObject.getBoolean("d");
                        if(result==true){
                            Toast.makeText(AddExecuteActivity.this, "添加合同执行信息成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Toast.makeText(AddExecuteActivity.this, "添加合同执行信息失败", Toast.LENGTH_SHORT).show();
                        }

                    }catch (JSONException ex){
                        Log.e("GetDetailInfo",ex.getMessage());
                        Toast.makeText(AddExecuteActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }

            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(AddExecuteActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //初始化印章类型下拉列表
    private void initPayWay(){
        PayWaySp = (Spinner)findViewById(R.id.pay_way);
        /*设置数据源*/
        ListPayWay=new ArrayList<String>();
        ListPayWay.add("现金");
        ListPayWay.add("转账支票");
        ListPayWay.add("汇款");
        /*新建适配器*/
        PayWayAdapter=new ArrayAdapter<String>(this,R.layout.spinner_display_style,ListPayWay);
        /*adapter设置一个下拉列表样式，参数为系统子布局*/
        PayWayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_style);

        /*spDown加载适配器*/
        PayWaySp.setAdapter(PayWayAdapter);

        /*soDown的监听器*/
        PayWaySp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String sealType=PayWayAdapter.getItem(i);   //获取选中的那一项
                ChoosePayWay = new SealCommon().getSealType(sealType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //设置默认值;
        PayWaySp.setSelection(0);
    }
    //执行时间
    private void initExecuteTime(){
        initTimePicker();
        btn_Time = (TextView) findViewById(R.id.execute_Time);
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
                Toast.makeText(AddExecuteActivity.this, getTime(date).split(" ")[0], Toast.LENGTH_SHORT).show();
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
