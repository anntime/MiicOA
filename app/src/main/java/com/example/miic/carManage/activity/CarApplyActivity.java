package com.example.miic.carManage.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class CarApplyActivity extends AppCompatActivity {
    private LinearLayout backBtn;

    private Spinner CarUseTypeSp;
    private List<String> ListCarUseType;
    private ArrayAdapter<String> CarUseTypeAdapter;

    private Spinner CarTypeSp;
    private List<String> ListCarType;
    private ArrayAdapter<String> CarTypeAdapter;

    private TimePickerView pvTime;
    private TextView btn_Time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_apply);
        backBtn = (LinearLayout)findViewById(R.id.search_back);
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
        //用印类型
        initCarType();
        //用印时间
        initCarUseTime();
        //印章类型
        initCarUserType();

    }
    //初始化用印类型下拉列表
    private void initCarUserType(){
        CarUseTypeSp = (Spinner)findViewById(R.id.car_use_type);
        /*设置数据源*/
        ListCarUseType=new ArrayList<String>();
        ListCarUseType.add("请选择");
        ListCarUseType.add("即时用印");
        ListCarUseType.add("携印外出");
        /*新建适配器*/
        CarUseTypeAdapter=new ArrayAdapter<String>(this,R.layout.spinner_display_style,ListCarUseType);
        /*adapter设置一个下拉列表样式，参数为系统子布局*/
        CarUseTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_style);

        /*spDown加载适配器*/
        CarUseTypeSp.setAdapter(CarUseTypeAdapter);

        /*soDown的监听器*/
        CarUseTypeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String carType=CarUseTypeAdapter.getItem(i);   //获取选中的那一项
                Log.i("您选择的印章类型是",carType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //设置默认值;
        CarUseTypeSp.setSelection(0);

    }
    //初始化印章类型下拉列表
    private void initCarType(){
        CarTypeSp = (Spinner)findViewById(R.id.car_type);
        /*设置数据源*/
        ListCarType=new ArrayList<String>();
        ListCarType.add("请选择");
        ListCarType.add("中心印章2");
        ListCarType.add("中心印章3");
        /*新建适配器*/
        CarTypeAdapter=new ArrayAdapter<String>(this,R.layout.spinner_display_style,ListCarType);
        /*adapter设置一个下拉列表样式，参数为系统子布局*/
        CarTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_style);

        /*spDown加载适配器*/
        CarTypeSp.setAdapter(CarTypeAdapter);

        /*soDown的监听器*/
        CarTypeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String carType=CarTypeAdapter.getItem(i);   //获取选中的那一项
                Log.i("您选择的印章类型是",carType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //设置默认值;
        CarTypeSp.setSelection(0);

    }

    //用印时间
    private void initCarUseTime(){
        initTimePicker();
        btn_Time = (TextView) findViewById(R.id.btn_Time);
        Calendar cal = Calendar.getInstance();
        String date = cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DAY_OF_MONTH);
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
                Toast.makeText(CarApplyActivity.this, getTime(date).split(" ")[0], Toast.LENGTH_SHORT).show();
                btn_Time.setText(getTime(date).split(" ")[0]);
                Log.i("pvTime", "onTimeSelect");


            }
        })
                .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                    @Override
                    public void onTimeSelectChanged(Date date) {
//                        btn_Time.setText(getTime(date).split(" ")[0]);
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
