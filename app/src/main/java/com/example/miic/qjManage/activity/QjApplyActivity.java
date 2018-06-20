package com.example.miic.qjManage.activity;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class QjApplyActivity extends AppCompatActivity {
    //返回按钮
    private LinearLayout linBackBtn;
    //历史按钮
    private LinearLayout linHistoryBtn;
    //请假事由
    private EditText etQjReason;
    //请假类型
    private Spinner spQjType;
    private List<String> ListQjType;
    private ArrayAdapter<String> QjTypeAdapter;
    //请假时间
    private TimePickerView pvStartTime;
    private TextView tvStartTime;
    private TimePickerView pvEndTime;
    private TextView tvEndTime;
    //请假天数
    private TextView tvQjDays;
    //剩余年假
    private TextView tvLeftDays;
    //联系方式
    private EditText etMobile;
    //审核人
    private TextView tvAssessor;
    //提交按钮
    private Button btnSubmit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qj_apply);
        init();
    }
    private void init(){
        //返回按钮事件
        linBackBtn = (LinearLayout)findViewById(R.id.search_back);
        linBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        linHistoryBtn = (LinearLayout)findViewById(R.id.history_button);
        linHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 //????
            }
        });
        etQjReason = (EditText)findViewById(R.id.qj_reason);
        spQjType = (Spinner)findViewById(R.id.qj_type);
        //请假类别
        initQjType();
        tvStartTime = (TextView) findViewById(R.id.start_Time);
        tvEndTime = (TextView)findViewById(R.id.end_Time);
        //请假时间
        initQjTime();
        tvQjDays = (TextView)findViewById(R.id.qj_day);
        tvLeftDays = (TextView)findViewById(R.id.left_day);
        etMobile = (EditText) findViewById(R.id.mobile);
        tvAssessor = (TextView)findViewById(R.id.assessor);
        btnSubmit = (Button)findViewById(R.id.submit_btn);
    }

    //初始化印章类型下拉列表
    private void initQjType(){

        /*设置数据源*/
        ListQjType=new ArrayList<String>();
        ListQjType.add("请选择");
        ListQjType.add("病假");
        ListQjType.add("事假");
        ListQjType.add("其他");
        /*新建适配器*/
        QjTypeAdapter=new ArrayAdapter<String>(this,R.layout.spinner_display_style,ListQjType);
        /*adapter设置一个下拉列表样式，参数为系统子布局*/
        QjTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_style);

        /*spDown加载适配器*/
        spQjType.setAdapter(QjTypeAdapter);

        /*soDown的监听器*/
        spQjType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String qjType=QjTypeAdapter.getItem(i);   //获取选中的那一项
                Log.i("您选择的请假类型是",qjType);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //设置默认值;
        spQjType.setSelection(0);
    }

    //用印时间
    private void initQjTime(){
        initStartTimePicker();
        Calendar cal = Calendar.getInstance();
        String date = cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DAY_OF_MONTH);
        tvStartTime.setText(date);
        tvStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pvStartTime.show(view);//弹出时间选择器，传递参数过去，回调的时候则可以绑定此view
            }
        });
        initEndTimePicker();
        Calendar cal2 = Calendar.getInstance();
        String date2 = cal2.get(Calendar.YEAR)+"-"+(cal2.get(Calendar.MONTH)+1)+"-"+cal2.get(Calendar.DAY_OF_MONTH);
        tvEndTime.setText(date2);
        tvEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pvEndTime.show(view);//弹出时间选择器，传递参数过去，回调的时候则可以绑定此view
            }
        });
    }
    private void initStartTimePicker() {
        Calendar endDate = Calendar.getInstance();
        endDate.set(2030,1,1);
        pvStartTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                Toast.makeText(QjApplyActivity.this, getTime(date).split(" ")[0], Toast.LENGTH_SHORT).show();
                tvStartTime.setText(getTime(date).split(" ")[0]);
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
    private void initEndTimePicker() {
        Calendar endDate = Calendar.getInstance();
        endDate.set(2030,1,1);
        pvEndTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                Toast.makeText(QjApplyActivity.this, getTime(date).split(" ")[0], Toast.LENGTH_SHORT).show();
                tvEndTime.setText(getTime(date).split(" ")[0]);
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
