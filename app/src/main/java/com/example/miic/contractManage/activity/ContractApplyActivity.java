package com.example.miic.contractManage.activity;

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

public class ContractApplyActivity extends AppCompatActivity {
    private LinearLayout backBtn;

    private Spinner ContractUseTypeSp;
    private List<String> ListContractUseType;
    private ArrayAdapter<String> ContractUseTypeAdapter;

    private Spinner ContractTypeSp;
    private List<String> ListContractType;
    private ArrayAdapter<String> ContractTypeAdapter;

    private TimePickerView pvTime;
    private TextView btn_Time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_apply);
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
        initContractType();
        //用印时间
        initContractUseTime();
        //印章类型
        initContractUserType();

    }
    //初始化用印类型下拉列表
    private void initContractUserType(){
        ContractUseTypeSp = (Spinner)findViewById(R.id.contract_use_type);
        /*设置数据源*/
        ListContractUseType=new ArrayList<String>();
        ListContractUseType.add("请选择");
        ListContractUseType.add("即时用印");
        ListContractUseType.add("携印外出");
        /*新建适配器*/
        ContractUseTypeAdapter=new ArrayAdapter<String>(this,R.layout.spinner_display_style,ListContractUseType);
        /*adapter设置一个下拉列表样式，参数为系统子布局*/
        ContractUseTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_style);

        /*spDown加载适配器*/
        ContractUseTypeSp.setAdapter(ContractUseTypeAdapter);

        /*soDown的监听器*/
        ContractUseTypeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String contractType=ContractUseTypeAdapter.getItem(i);   //获取选中的那一项
                Log.i("您选择的印章类型是",contractType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //设置默认值;
        ContractUseTypeSp.setSelection(0);

    }
    //初始化印章类型下拉列表
    private void initContractType(){
        ContractTypeSp = (Spinner)findViewById(R.id.contract_type);
        /*设置数据源*/
        ListContractType=new ArrayList<String>();
        ListContractType.add("请选择");
        ListContractType.add("中心印章2");
        ListContractType.add("中心印章3");
        /*新建适配器*/
        ContractTypeAdapter=new ArrayAdapter<String>(this,R.layout.spinner_display_style,ListContractType);
        /*adapter设置一个下拉列表样式，参数为系统子布局*/
        ContractTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_style);

        /*spDown加载适配器*/
        ContractTypeSp.setAdapter(ContractTypeAdapter);

        /*soDown的监听器*/
        ContractTypeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String contractType=ContractTypeAdapter.getItem(i);   //获取选中的那一项
                Log.i("您选择的印章类型是",contractType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //设置默认值;
        ContractTypeSp.setSelection(0);

    }

    //用印时间
    private void initContractUseTime(){
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
                Toast.makeText(ContractApplyActivity.this, getTime(date).split(" ")[0], Toast.LENGTH_SHORT).show();
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
