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


import com.example.miic.R;
import com.example.miic.base.http.PostRequest;
import com.example.miic.contractManage.item.ContractStatus;
import com.example.miic.sealManagement.common.SealCommon;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContractPrintActivity extends AppCompatActivity {
    //合同名称、用章类型、监印份数、监印备注
    private TextView ContractNameTv;
    private EditText RemarkEt;
    private Button SubmitBtn;

    private Spinner SealTypeSp;
    private List<String> ListSealType;
    private ArrayAdapter<String> SealTypeAdapter;

    private Spinner SealCountSp;
    private List<String> ListSealCount;
    private ArrayAdapter<String> SealCountAdapter;

    private String ClickID;
    private String ContractName;
    private String ChooseSealCount;
    private String ChooseSealType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_print);
        //获取上一页面传递的参数
        Intent intent = getIntent();
        ClickID= intent.getStringExtra("clickID");
        ContractName = intent.getStringExtra("contractName");
        setHeader();
        initView();
    }
    private void setHeader() {
        TextView titleTv = (TextView) findViewById(R.id.page_title);
        LinearLayout backLin = (LinearLayout)findViewById(R.id.back);
        LinearLayout rightLin = (LinearLayout)findViewById(R.id.menu);
        titleTv.setText("合同监印");
        rightLin.setVisibility(View.INVISIBLE);
        backLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initView(){
        ContractNameTv = (TextView) findViewById(R.id.contract_name);
        ContractNameTv.setText(ContractName);
        RemarkEt = (EditText)findViewById(R.id.print_remark);
        SubmitBtn = (Button)findViewById(R.id.submit_btn);
        SubmitBtn.setOnClickListener(ContractPrintApprovedListener);
        initSealType();
        initSealCount();
    }
    private View.OnClickListener ContractPrintApprovedListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            ContractPrintApproved();
        }
    };
    private void ContractPrintApproved(){
        JSONObject requestJson = new JSONObject();
        try{
            JSONObject val = new JSONObject();
            val.put("ID", ClickID);
            val.put("PrintType", ChooseSealType);
            val.put("PrintCount", ChooseSealCount);
            val.put("PrintRemark", RemarkEt.getText().toString() );
            val.put("Status", ContractStatus.File.getIndex());
            requestJson.put("contractInfo",val);
        }catch (JSONException ex){
            Toast.makeText(ContractPrintActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Call<String> call = PostRequest.Instance.request.ContractPrintApproved(keywordView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        Boolean result = jsonObject.getBoolean("d");
                        if(result==true){
                            Toast.makeText(ContractPrintActivity.this, "添加合同执行信息成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Toast.makeText(ContractPrintActivity.this, "添加合同执行信息失败", Toast.LENGTH_SHORT).show();
                        }

                    }catch (JSONException ex){
                        Log.e("GetDetailInfo",ex.getMessage());
                        Toast.makeText(ContractPrintActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }

            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ContractPrintActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //初始化印章类型下拉列表
    private void initSealType(){
        SealTypeSp = (Spinner)findViewById(R.id.seal_type);
        /*设置数据源*/
        ListSealType=new ArrayList<String>();
        ListSealType.add("中心公章");
        ListSealType.add("电子信息系统推广办公室章");
        ListSealType.add("兆软公章");
        ListSealType.add("保卫章");
        ListSealType.add("房产章");
        /*新建适配器*/
        SealTypeAdapter=new ArrayAdapter<String>(this,R.layout.spinner_display_style,ListSealType);
        /*adapter设置一个下拉列表样式，参数为系统子布局*/
        SealTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_style);
        /*spDown加载适配器*/
        SealTypeSp.setAdapter(SealTypeAdapter);
        /*soDown的监听器*/
        SealTypeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String sealType=SealTypeAdapter.getItem(i);   //获取选中的那一项
                ChooseSealType = new SealCommon().getSealType(sealType);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        //设置默认值;
        SealTypeSp.setSelection(0);
    }
    //初始化监印份数下拉列表
    private void initSealCount(){
        SealCountSp = (Spinner)findViewById(R.id.seal_count);
        /*设置数据源*/
        ListSealCount=new ArrayList<String>();
        ListSealCount.add("1份");
        ListSealCount.add("2份");
        ListSealCount.add("3份");
        ListSealCount.add("4份");
        ListSealCount.add("5份");
        ListSealCount.add("6份");
        ListSealCount.add("7份");
        ListSealCount.add("8份");
        ListSealCount.add("9份");
        ListSealCount.add("10份");
        ListSealCount.add("11份");
        ListSealCount.add("12份");
        /*新建适配器*/
        SealCountAdapter=new ArrayAdapter<String>(this,R.layout.spinner_display_style,ListSealCount);
        /*adapter设置一个下拉列表样式，参数为系统子布局*/
        SealCountAdapter.setDropDownViewResource(R.layout.spinner_dropdown_style);

        /*spDown加载适配器*/
        SealCountSp.setAdapter(SealCountAdapter);

        /*soDown的监听器*/
        SealCountSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String sealType=SealCountAdapter.getItem(i);   //获取选中的那一项
                ChooseSealCount = new SealCommon().getSealType(sealType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //设置默认值;
        SealCountSp.setSelection(0);
    }

}
