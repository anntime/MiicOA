package com.example.miic.contractManage.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Environment;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.base.http.PostRequest;
import com.example.miic.base.progressbar.DownLoadProgressBar;
import com.example.miic.common.MyApplication;
import com.example.miic.contractManage.adapter.ContractApprovalAccFileAdapter;
import com.example.miic.contractManage.adapter.ContractTimeLineItemAdapter;
import com.example.miic.contractManage.common.ContractCommon;
import com.example.miic.contractManage.item.ContractApprovalAccFileItem;
import com.example.miic.contractManage.item.ContractApproveStatus;
import com.example.miic.contractManage.item.ContractTimeLineItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.os.Handler;
import com.example.miic.oa.common.OpenFileUtil;
import com.example.miic.oa.common.Setting;
import com.example.miic.oa.common.fileDownload.FileUtils;

import static com.example.miic.oa.common.Setting.requestPermission;
import static com.example.miic.oa.common.Setting.stampToDate;
import static com.example.miic.oa.common.LvHeightUtil.setListViewHeightBasedOnChildren;
import static com.example.miic.oa.common.Setting.stampToTime;

public class ContractApprovalActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ContractTimeLineItemAdapter mTimeLineAdapter;
    private List<ContractTimeLineItem> mDataList = new ArrayList<>();

    private List<ContractApprovalAccFileItem> contractAccList;
    private ContractApprovalAccFileAdapter contractFileAccAdapter;

    private TextView contractIDTv;
    private TextView contractNameTv;
    private TextView contractNumTv;
    private TextView contractUndertakeDeptTv;
    private TextView contractUndertakeNameTv;
    private TextView contractSignerTv;
    private TextView contractSignTimeTv;
    private TextView contractSubjectAmountTv;
    private TextView contractScottareTypeTv;

    private TextView contractOtherCompanyTv;
    private TextView contractLinkManTv;
    private TextView contractLinkTelTv;
    private TextView contractTimeTv;
    private TextView contractIndentificationItemTv;
    private TextView contractSealTypeTv;
    private TextView contractPrintNumTv;
    private TextView contractPrintRemarkTv;
    private TextView contractTypeTv;
    private TextView contractDrafttypeTv;
    private TextView contractFileTv;
    private ListView contractAccFileLv;
    private TextView contractAccFileTv;
    private TextView contractApproveChiefTv;
    private TextView contractApproveStatusTv;

    private LinearLayout moreBtn;
    private LinearLayout lessBtn;
    private LinearLayout moreContractInfo;

    private EditText remarkEt;
    private Button withdrawBtn;
    private Button passBtn;
    private Button disagreeBtn;
    private Button noPassBtn;

    private View auditorPopWindowView;
    private Spinner assessorSp;
    private Button submitBtn;
    private Button cancelBtn;
    private List<String> ListAssessor;
    private ArrayAdapter<String>  AssessorAdapter;

    private View searchResultView;
    private View searchProgressView;


    private String ApproveRemark;
    private String ClickID;
    private Activity mActivity ;
    private JSONObject SubProjKV =new JSONObject();
    private String ApproveID;
    private String ApproveMainChiefID;
    private String ApproveMainChiefName;
    private JSONArray ApprovalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_approval);
        mActivity = this;
        //获取上一页面传递的参数
        Intent intent = getIntent();
        ClickID= intent.getStringExtra("clickID");

        setHeader();
        initView();


        GetDetailInfo();
        GetContractApproveDetailInfo();

    }
    private void setHeader() {
        TextView titleTv = (TextView) findViewById(R.id.page_title);
        LinearLayout backLin = (LinearLayout)findViewById(R.id.back);
        LinearLayout rightLin = (LinearLayout)findViewById(R.id.menu);
        titleTv.setText("合同审批");
        rightLin.setVisibility(View.INVISIBLE);

        backLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(1);
                finish();
            }
        });
    }

    private void initView(){
        contractIDTv = (TextView)findViewById(R.id.contract_id);
        contractNameTv = (TextView)findViewById(R.id.contract_name);
        contractNumTv = (TextView)findViewById(R.id.contract_num);
        contractUndertakeDeptTv = (TextView)findViewById(R.id.contract_undertake_dept);
        contractUndertakeNameTv = (TextView)findViewById(R.id.contract_undertake_name);
        contractSignerTv = (TextView)findViewById(R.id.contract_signer);
        contractSignTimeTv = (TextView)findViewById(R.id.sign_time);
        contractSubjectAmountTv = (TextView)findViewById(R.id.subject_amount);
        contractScottareTypeTv = (TextView)findViewById(R.id.scottare_type);
        contractOtherCompanyTv = (TextView)findViewById(R.id.other_company);
        contractLinkManTv = (TextView)findViewById(R.id.link_man);
        contractLinkTelTv = (TextView)findViewById(R.id.link_tel);
        contractTimeTv = (TextView)findViewById(R.id.contract_time);
        contractIndentificationItemTv = (TextView)findViewById(R.id.contract_identification_item);
        contractSealTypeTv = (TextView)findViewById(R.id.contract_seal_type);
        contractPrintNumTv = (TextView)findViewById(R.id.contract_print_num);
        contractPrintRemarkTv = (TextView)findViewById(R.id.contract_print_remark);
        contractTypeTv = (TextView)findViewById(R.id.contract_type);
        contractDrafttypeTv = (TextView)findViewById(R.id.draft_type);
        contractFileTv = (TextView)findViewById(R.id.contract_file);
        contractAccFileLv = (ListView)findViewById(R.id.contract_acc_file);
        contractAccFileTv = (TextView)findViewById(R.id.contract_acc_file_tv);
        contractApproveChiefTv = (TextView)findViewById(R.id.approve_main_chief);
        contractApproveStatusTv = (TextView)findViewById(R.id.contract_approve_status);

        moreBtn = (LinearLayout)findViewById(R.id.more_btn);
        lessBtn = (LinearLayout)findViewById(R.id.less_btn);
        moreContractInfo = (LinearLayout)findViewById(R.id.more_contract_info);
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreBtn.setVisibility(View.GONE);
                lessBtn.setVisibility(View.VISIBLE);
                moreContractInfo.setVisibility(View.VISIBLE);
            }
        });
        lessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lessBtn.setVisibility(View.GONE);
                moreContractInfo.setVisibility(View.GONE);
                moreBtn.setVisibility(View.VISIBLE);
            }
        });

        remarkEt = (EditText)findViewById(R.id.remark_keyword);
        withdrawBtn = (Button)findViewById(R.id.withdraw_btn);
        passBtn = (Button)findViewById(R.id.pass_btn);
        disagreeBtn = (Button)findViewById(R.id.disagree_btn);
        noPassBtn = (Button)findViewById(R.id.no_pass_btn);

        ApproveRemark = remarkEt.getText().toString();


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setVisibility(View.VISIBLE);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);
        mTimeLineAdapter = new ContractTimeLineItemAdapter(mDataList,ContractApprovalActivity.this);
        mRecyclerView.setAdapter(mTimeLineAdapter);

        //弹出框
        auditorPopWindowView = LayoutInflater.from(this).inflate(R.layout.contract_choose_auditor_pop_window,null);//PopupWindow对象
        assessorSp= (Spinner) auditorPopWindowView.findViewById(R.id.sp_assessor);
        submitBtn = (Button) auditorPopWindowView.findViewById(R.id.save_btn);
        cancelBtn = (Button) auditorPopWindowView.findViewById(R.id.cancel_btn);

        searchResultView = findViewById(R.id.search_result);
        searchProgressView = findViewById(R.id.search_progress);
        showProgress(true);
    }
    //获取合同详细信息
    public void GetDetailInfo(){
        JSONObject requestJson = new JSONObject();
        try{
            requestJson.put("id",ClickID);
        }catch (JSONException ex){
            Log.i("GetDetailInfo","json对象构造错误");
        }
        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Call<String> call = PostRequest.Instance.request.GetDetailInfo(keywordView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                showProgress(false);
                if(response.body()!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        String jsonStrTemp = jsonObject.getString("d");
                        if(!jsonStrTemp.equals("[]")){
                            ContractCommon contract = new ContractCommon();
                            JSONArray arrayTemp = new JSONArray(jsonStrTemp);
                            JSONObject objectTemp = arrayTemp.getJSONObject(0);
                            contractIDTv.setText(objectTemp.getString("ContractNo"));
                            contractNameTv.setText(objectTemp.getString("ContractName"));
                            contractNumTv.setText(objectTemp.getString("ContractNum"));
                            contractUndertakeDeptTv.setText(objectTemp.getString("DeptName"));
                            contractUndertakeNameTv.setText(objectTemp.getString("CreaterName"));
                            contractSignerTv.setText(objectTemp.getString("SignatoryName"));
                            //时间样式
                            if(objectTemp.getString("SignatoryTime").contains("Date")){
                                contractSignTimeTv.setText(stampToDate(objectTemp.getString("SignatoryTime")));
                            }else{
                                contractSignTimeTv.setText(objectTemp.getString("SignatoryTime").split(" ")[0]);
                            }
                            //金额处理
                            long amount = objectTemp.getLong("SubjectAmount");
                            DecimalFormat df = new DecimalFormat("#,###.00");
                            String m = df.format(amount);
                            contractSubjectAmountTv.setText("￥"+m);
                            contractScottareTypeTv.setText(contract.getScottareTypeStr(objectTemp.getString("ScottareType")));
                            contractOtherCompanyTv.setText(objectTemp.getString("OtherCompany"));
                            contractLinkManTv.setText(objectTemp.getString("LinkMan"));
                            contractLinkTelTv.setText(objectTemp.getString("LinkTel"));
                            //时间样式
                            String contractStart ;
                            String contractEnd ;
                            if(objectTemp.getString("ContractBeginTime").contains("Date")){
                                contractStart = stampToDate(objectTemp.getString("ContractBeginTime"));
                            }else{
                                contractStart = objectTemp.getString("ContractBeginTime").split(" ")[0];

                            }
                            if(objectTemp.getString("IsLong").equals("2")){
                                if(objectTemp.getString("ContractEndTime").contains("Date")){
                                    contractEnd = stampToDate(objectTemp.getString("ContractEndTime"));
                                }else{
                                    contractEnd = objectTemp.getString("ContractEndTime").split(" ")[0];
                                }
                            }else{
                                //长期合同
                                contractEnd = "长期";
                            }
                            contractTimeTv.setText(contractStart+" ~ "+contractEnd);

                            Log.i("IdentificationItem",objectTemp.getString("IdentificationItem"));
                            Log.i("PrintType",objectTemp.getString("PrintType"));
                            contractIndentificationItemTv.setText(contract.getIdentificationItemStr(objectTemp.getString("IdentificationItem")));
                            contractSealTypeTv.setText(contract.getIdentificationItemStr(objectTemp.getString("IdentificationItem")+""+contract.getPrintTypeStr(objectTemp.getString("PrintType"))));
                            contractPrintNumTv.setText(objectTemp.getString("PrintCount")+"份");
                            if (objectTemp.getString("PrintRemark") != "") {
                                contractPrintRemarkTv.setText(objectTemp.getString("PrintRemark"));
                            } else {
                                contractPrintRemarkTv.setText("暂无");
                            }

                            contractTypeTv.setText(contract.getContractTypeOneStr(objectTemp.getString("ContractType1"))+"-"+contract.getContractTypeTwoStr(objectTemp.getString("ContractType2")));
                            contractDrafttypeTv.setText(contract.getContractDraftTypeStr(objectTemp.getString("DraftType")));

                            final String contractUrl = new Setting().getService() + objectTemp.getString("ContractUrl");
                            Log.i("文件地址：",contractUrl);
                            String[] arr = contractUrl.split("/");
                            final String contractName = arr[arr.length-1];
                            contractFileTv.setText(contractName);
                            contractFileTv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //下载文件(单个文件下载)
                                    Setting.downLoadSingle(contractUrl, contractName,mActivity);
                                }
                            });
                            //判断合同附件
                            JSONArray accList = objectTemp.getJSONArray("AccList");
                            if (accList != null&&accList.length()!=0) {
                                if(!accList.getJSONObject(0).isNull("ID") ){
                                    //有可能是文件，有可能是图片
                                    //只能是文件，显示link_TextView
                                    contractAccList = new ArrayList<>();
                                    for (int i = 0; i < accList.length(); i++) {
                                        String title = accList.getJSONObject(i).getString("FileName");
                                        String link = new Setting().getService() + accList.getJSONObject(i).getString("FilePath");
                                        contractAccList.add(new ContractApprovalAccFileItem(title, link,0));
                                    }
                                    //附件
                                    contractFileAccAdapter = new ContractApprovalAccFileAdapter(ContractApprovalActivity.this, contractAccList);
                                    contractAccFileLv.setAdapter(contractFileAccAdapter);
                                    setListViewHeightBasedOnChildren(contractAccFileLv);
                                    contractAccFileLv.setOnItemClickListener(listItemClickListener);
                                    contractFileAccAdapter.notifyDataSetChanged();
                                    contractAccFileTv.setVisibility(View.GONE);
                                }else{
                                    contractAccFileLv.setVisibility(View.GONE);
                                    contractAccFileTv.setText("暂无合同附件");
                                }
                            }
                            contractApproveStatusTv.setText(contract.getContractApproveStatusStr(objectTemp.getString("Status")));

                            //获取审核人
                            //contractApproveChiefTv.setText(objectTemp.getString("ContractNo"));
                        }
                    }catch (JSONException ex){
                        Log.e("GetDetailInfo",ex.getMessage());
                        Toast.makeText(ContractApprovalActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }

            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ContractApprovalActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //获取 合同审批详情
    public void GetContractApproveDetailInfo(){
        JSONObject requestJson = new JSONObject();
        try{
            requestJson.put("contractID",ClickID);
        }catch (JSONException ex){
            Log.i("ContractSearch","json对象构造错误");
        }
        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Call<String> call = PostRequest.Instance.request.GetContractApproveDetailInfo(keywordView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                showProgress(false);
                if(response.body()!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        String jsonStrTemp = jsonObject.getString("d");
                        if(!jsonStrTemp.equals("")) {
                            JSONObject jsonTemp = new JSONObject(jsonStrTemp);
                            SubProjKV.put("SubProjID",jsonTemp.getString("SubProjID"));
                            SubProjKV.put("SubProjName",jsonTemp.getString("SubProjName"));
                            JSONArray approveList = jsonTemp.getJSONArray("ApproveList");
                            if(approveList !=null&& approveList.length() > 0
                                    && !(approveList.getJSONObject(0).getString("ApprovalID").equals(new MyApplication().getUserID()))
                                    && approveList.getJSONObject(0).getString("ApprovalStatus").equals(ContractApproveStatus.Create.getIndex()+"")){
                                //contractApproveChiefTv.setText(approveList.getJSONObject(0).getString("ApprovalName"));
                                //ApprovalID = approveList.getJSONObject(0).getString("ApprovalID");
                                //判断是否可以撤回
                                JSONObject withdrawer = new JSONObject();
                                withdrawer.put("ID",approveList.getJSONObject(0).getString("ID"));
                                withdrawer.put("ApplicationID",ClickID);
                                CanWithdraw(withdrawer);
                            }else{
                               withdrawBtn.setVisibility(View.GONE);
                                //当前审批，获取下一个审批人
                                getChiefInfo();
                            }
                            //审批详情显示
                            if(approveList!=null&&approveList.length()>0){
                                for (int index = 0;index<approveList.length();index++){
                                    JSONObject item = approveList.getJSONObject(index);
                                    String approveName = item.getString("ApprovalName");
                                    String approveTime;
                                    String temp = item.isNull("ApprovalTime")?"":item.getString("ApprovalTime");
                                    if(temp.contains("Date")){
                                        approveTime = stampToTime(temp);
                                    }else{
                                        approveTime = temp;
                                    }
                                    String approvalStatus =item.getString("ApprovalStatus");
                                    if (index==0){
                                         if (item.getString("ApprovalID").equals(new MyApplication().getUserID())
                                                && item.getString("ApprovalStatus").equals(ContractApproveStatus.Create.getIndex()+"")) {
                                             //审批按钮显示并绑定事件
                                             ApproveID = item.getString("ID");
                                            passBtn.setVisibility(View.VISIBLE);
                                            disagreeBtn.setVisibility(View.VISIBLE);
                                            noPassBtn.setVisibility(View.VISIBLE);
                                            //通过
                                            passBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    ContractApproveEvent(ContractApproveStatus.Agree);
                                                }
                                            });
                                            //不通过
                                            noPassBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    ContractApproveEvent(ContractApproveStatus.Disagree);
                                                }
                                            });
                                            //退回
                                            disagreeBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    ContractApproveEvent(ContractApproveStatus.Retreat);
                                                }
                                            });
                                        } else {
                                            //隐藏审批相关按钮
                                            remarkEt.setVisibility(View.GONE);
                                            passBtn.setVisibility(View.GONE);
                                            disagreeBtn.setVisibility(View.GONE);
                                            noPassBtn.setVisibility(View.GONE);
                                        }
                                        mDataList.add(new ContractTimeLineItem(approveName, approveTime, ContractApproveStatus.parse(approvalStatus)));
                                    }else{
                                        mDataList.add(new ContractTimeLineItem(approveName, approveTime, ContractApproveStatus.parse(approvalStatus)));
                                    }
                                }
                                mTimeLineAdapter.notifyDataSetChanged();
                            }

                        }
                    }catch (JSONException ex){
                        Log.e("ContractSearch",ex.getMessage());
                        Toast.makeText(ContractApprovalActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ContractApprovalActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //审批事件
    private void ContractApproveEvent(ContractApproveStatus operStatus ){
        JSONObject requestJson = new JSONObject();
        JSONObject projKV = new JSONObject();
        JSONArray person = GetApprovePersonList(operStatus);
        try{
            projKV.put("Name",SubProjKV.getString("SubProjID"));
            projKV.put("Value",SubProjKV.getString("SubProjName"));

            requestJson.put("projKV",projKV);
            requestJson.put("person",person);
        }catch (JSONException ex){
            Log.i("ContractSearch","json对象构造错误");
        }
        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Call<String> call = PostRequest.Instance.request.ApproveContract(keywordView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        JSONObject data = jsonObject.isNull("d")?null:jsonObject.getJSONObject("d");
                        if(data!=null){
                            Boolean res = data.getBoolean("Result");
                            if (res==true){
                                Toast.makeText(ContractApprovalActivity.this,"合同审批成功~",Toast.LENGTH_LONG).show();
                                finish();
                            }else {
                                Toast.makeText(ContractApprovalActivity.this,"合同审批失败~",Toast.LENGTH_LONG).show();
                            }
                        }

                    }catch (JSONException ex){
                        Log.e("ContractSearch",ex.getMessage());
                        Toast.makeText(ContractApprovalActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ContractApprovalActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //获取审核人员列表（审核时用）
    private JSONArray GetApprovePersonList(ContractApproveStatus operStatus){
        String operStatusSetting =  operStatus.getIndex()+"";
        JSONArray person = new JSONArray();
        try{
            if(ApprovalList.length() > 1){
                if(operStatus == ContractApproveStatus.Agree){
                    for (int index=0;index<ApprovalList.length()-1;index++){
                        JSONObject obj = new JSONObject();
                        obj.put("ID",ApproveID);
                        obj.put("ApplicationID",ClickID);
                        obj.put("UserID",MyApplication.getUserID());
                        obj.put("UserName",MyApplication.getUserName());
                        obj.put("ApproveRemark",remarkEt.getText().toString());
                        obj.put("OperStatusSetting",operStatusSetting);
                        person.put(obj);
                    }
                }
                if (ApprovalList.getJSONObject(ApprovalList.length()-1).getJSONArray("MainChiefList").length()>1){
                    //下一步审核人是个下拉列表，说是要在点击审核的时候弹出框让用户选择
                    ChoosePerson();
                    JSONObject obj = new JSONObject();
                    obj.put("ID",ApproveID);
                    obj.put("ApplicationID",ClickID);
                    obj.put("UserID",ApproveMainChiefID);
                    obj.put("UserName",ApproveMainChiefName);
                    obj.put("ApproveRemark",remarkEt.getText().toString());
                    obj.put("OperStatusSetting",operStatusSetting);
                    person.put(obj);
                }else{
                    JSONObject obj = new JSONObject();
                    obj.put("ID",ApproveID);
                    obj.put("ApplicationID",ClickID);
                    obj.put("UserID",ApproveMainChiefID);
                    obj.put("UserName",ApproveMainChiefName);
                    obj.put("ApproveRemark",remarkEt.getText().toString());
                    obj.put("OperStatusSetting",operStatusSetting);
                    person.put(obj);
                }
            }else if(ApprovalList.length() == 1){
                if(ApprovalList.getJSONObject(0).getJSONArray("MainChiefList").length()>1){
                    //下一步审核人是个下拉列表，说是要在点击审核的时候弹出框让用户选择
                    ChoosePerson();
                    JSONObject obj = new JSONObject();
                    obj.put("ID",ApproveID);
                    obj.put("ApplicationID",ClickID);
                    obj.put("UserID",ApproveMainChiefID);
                    obj.put("UserName",ApproveMainChiefName);
                    obj.put("ApproveRemark",remarkEt.getText().toString());
                    obj.put("OperStatusSetting",operStatusSetting);
                    person.put(obj);

                }else{
                    JSONObject obj = new JSONObject();
                    obj.put("ID",ApproveID);
                    obj.put("ApplicationID",ClickID);
                    obj.put("UserID",ApproveMainChiefID);
                    obj.put("UserName",ApproveMainChiefName);
                    obj.put("ApproveRemark",remarkEt.getText().toString());
                    obj.put("OperStatusSetting",operStatusSetting);
                    person.put(obj);
                }
            }else{
                JSONObject obj = new JSONObject();
                obj.put("ID",ApproveID);
                obj.put("ApplicationID",ClickID);
                obj.put("UserID","");
                obj.put("UserName","");
                obj.put("ApproveRemark",remarkEt.getText().toString());
                obj.put("OperStatusSetting",operStatusSetting);
                person.put(obj);
            }
        }catch (JSONException ex){
            Log.i("ContractSearch","json对象构造错误:"+ex.getMessage());
        }
        return person;
    }
    //获取审核人员信息
    private void getChiefInfo(){
        JSONObject requestJson = new JSONObject();
        try{
            requestJson.put("applicationID",ClickID);
        }catch (JSONException ex){
            Log.i("getChiefInfo","json对象构造错误");
        }
        RequestBody view = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.GetCurrentSetpNum (view);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try {
                        System.out.println("body:" + response.body());
                        JSONObject jsonObject = new JSONObject(response.body());
                        JSONArray arrTemp = jsonObject.getJSONArray("d");
                        int current_num = 0;
                        for(int index=0;index<arrTemp.length();index++){
                            JSONObject item = arrTemp.getJSONObject(index);
                            if(item.getString("Key").equals("Count")){
                                current_num = item.getInt("Value");
                                break;
                            }
                        }
                        for(int index=0;index<arrTemp.length();index++){
                            JSONObject item = arrTemp.getJSONObject(index);
                            if(item.getString("Key").equals("IsEnd")){
                                if(item.getBoolean("Value")==false){
                                    GetNextApprovalList(current_num);
                                    break;
                                }
                            }
                        }
                    }catch (JSONException ex){
                        Log.e("InfoChildFragment",ex.getMessage());
                        Toast.makeText(ContractApprovalActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(ContractApprovalActivity.this,"获取用户信息失败！",Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ContractApprovalActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    private void GetNextApprovalList(int level){
        JSONObject json = new JSONObject();
        try{
            json.put("projID",SubProjKV.getString("SubProjID"));
            json.put("deptID","");
            json.put("level",level);
        }catch (JSONException ex){
            Log.i("getChiefInfo","json对象构造错误");
        }
        RequestBody Proview = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());
        Call<String> call2 = PostRequest.Instance.request.GetNextApprovalList (Proview);
        Callback<String> callback2 = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.body() != null) {
                    try {
                        System.out.println("" +
                                "" + response.body());
                        JSONObject jsonObject = new JSONObject(response.body());
                        final  JSONArray jsonTemp = new JSONArray(jsonObject.getString("d"));
                        ApprovalList = jsonTemp;
                        if (jsonTemp.getJSONObject(jsonTemp.length()-1).getJSONArray("MainChiefList").length()==1){
                            JSONArray mainChief = jsonTemp.getJSONObject(jsonTemp.length()-1).getJSONArray("MainChiefList");
                            contractApproveChiefTv.setText(mainChief.getJSONObject(0).getString("MainChiefName"));
                            ApproveMainChiefName= mainChief.getJSONObject(0).getString("MainChiefName");
                            ApproveMainChiefID = mainChief.getJSONObject(0).getString("MainChiefID");
                        }else{
                            if(jsonTemp.getJSONObject(jsonTemp.length()-1).getJSONArray("MainChiefList").length()>1){
                                //有多个审批人的时候
                                JSONArray temp = jsonTemp.getJSONObject(jsonTemp.length()-1).getJSONArray("MainChiefList");

                                initAssessorSp(temp);
                                ListAssessor.add("请选择");
                                for(int i=0;i<temp.length();i++){
                                    ListAssessor.add(temp.getJSONObject(i).getString("MainChiefName"));
                                }
                            }else{
                                JSONArray mainChief = jsonTemp.getJSONObject(jsonTemp.length()-1).getJSONArray("MainChiefList");
                                contractApproveChiefTv.setText(mainChief.getJSONObject(0).getString("MainChiefName"));
                                ApproveMainChiefName= mainChief.getJSONObject(0).getString("MainChiefName");
                                ApproveMainChiefID = mainChief.getJSONObject(0).getString("MainChiefID");
                            }
                        }
                    } catch (JSONException ex) {
                        Log.i("getChiefInfo", "json对象构造错误");
                    }
                }
            }

            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ContractApprovalActivity.this, "网络错误，请稍后再试！", Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call2, callback2);
    }
    private void CanWithdraw(final JSONObject withdrawer){
        final JSONObject requestJson = new JSONObject();
        try {
            JSONObject val1 = new JSONObject();
            val1.put("Name",SubProjKV.getString("SubProjID"));
            val1.put("Value",SubProjKV.getString("SubProjName"));
            requestJson.put("projKV",val1);
            requestJson.put("withdrawer",withdrawer);
        }catch (JSONException ex){
            Log.e("ContractSearch",ex.getMessage());
            Toast.makeText(ContractApprovalActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
        }
        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Call<String> call = PostRequest.Instance.request.CanWithdraw(keywordView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try{
                        JSONObject jsonObject = new JSONObject(response.body());
                        Boolean status = jsonObject.getBoolean("d");
                        if (status==true){
                            withdrawBtn.setVisibility(View.VISIBLE);
                            withdrawBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    withdrawEvent(requestJson);
                                }
                            });
                        }else{
                            withdrawBtn.setVisibility(View.GONE);
                        }
                    }catch (JSONException ex){
                        Log.e("ContractSearch",ex.getMessage());
                        Toast.makeText(ContractApprovalActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ContractApprovalActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    private void withdrawEvent(final JSONObject requestJson){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(ContractApprovalActivity.this);
        normalDialog.setTitle("提醒");
        normalDialog.setMessage("您确定要撤回合同审批单么？");
        // 显示
        final AlertDialog dia = normalDialog.show();
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        contractWithdraw(requestJson);
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dia.dismiss();

                    }
                });
    }
    private void contractWithdraw(final JSONObject requestJson){
        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Call<String> call = PostRequest.Instance.request.Withdraw(keywordView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try{
                        JSONObject jsonObject = new JSONObject(response.body());
                        Boolean status = jsonObject.getBoolean("d");
                        if (status==true){
                            Toast.makeText(ContractApprovalActivity.this,"您已撤回申请单成功~",Toast.LENGTH_LONG).show();
                            finish();
                        }else{
                            Toast.makeText(ContractApprovalActivity.this,"撤回申请单失败~",Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException ex){
                        Log.e("ContractSearch",ex.getMessage());
                        Toast.makeText(ContractApprovalActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ContractApprovalActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //文件点击事件
    private AdapterView.OnItemClickListener listItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            if(position<contractAccList.size()){
                //通过view获取其内部的组件，进而进行操作
                ContractApprovalAccFileItem fileAcc = contractAccList.get(position);
                String title = fileAcc.getTitle();
                String link = fileAcc.getLink();
                int progress = fileAcc.getProgress();
                //下载文件
                downLoad(link, title,position);
            }
        }
    };
    private Handler mHandler  = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            publishProgress(msg.arg1,msg.arg2);
        }
    };
    public void publishProgress(final int progress,  final int positionInAdapter) {
        contractFileAccAdapter.getItem(positionInAdapter).setProgress(progress);

        if(positionInAdapter >= contractAccFileLv.getFirstVisiblePosition() &&
                positionInAdapter <= contractAccFileLv.getLastVisiblePosition()) {
            int positionInListView = positionInAdapter - contractAccFileLv.getFirstVisiblePosition();
            DownLoadProgressBar item = (DownLoadProgressBar) contractAccFileLv.getChildAt(positionInListView)
                    .findViewById(R.id.pb_mp);

            item.setProgress(progress);
        }
    }
    /**
     * 从服务器下载文件
     * @param path 下载文件的地址
     * @param FileName 文件名字
     */
    public void downLoad(final String path, final String FileName,final int listPosition) {
        Log.i("下载函数：：","下载？");
        //更新一下列表中表示下载的boolean变量，表示该条数据正在下载
        contractFileAccAdapter.getItem(listPosition).setIsdownload(true);
        if(listPosition >= contractAccFileLv.getFirstVisiblePosition() &&
                listPosition <= contractAccFileLv.getLastVisiblePosition()) {
            int positionInListView = listPosition - contractAccFileLv.getFirstVisiblePosition();
            final DownLoadProgressBar item  = (DownLoadProgressBar) contractAccFileLv.getChildAt(positionInListView)
                    .findViewById(R.id.pb_mp);
            item.setVisibility(View.VISIBLE);
            requestPermission(ContractApprovalActivity.this);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(path);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setReadTimeout(5000);
                        con.setConnectTimeout(5000);
                        con.setRequestProperty("Charset", "UTF-8");
                        con.setRequestMethod("GET");
                        if (con.getResponseCode() == 200) {
                            InputStream is = con.getInputStream();//获取输入流
                            //获取文件总长度
                            int contentLength = con.getContentLength();
                            Log.i("文件长度：",contentLength+"");
                            item.setMaxProgress(contentLength);
                            FileOutputStream fileOutputStream = null;//文件输出流
                            if (is != null) {
                                //记录下载进度
                                int downloadSize = 0;
                                final String filePath = Environment.getExternalStorageDirectory().toString() + "/OA_file";
                                FileUtils fileUtils = new FileUtils(filePath);
                                /* 取得扩展名 */
                                String[] arr = path.split("\\.");
                                Log.i("下载地址：",path );
                                String end="pdf";
                                if(arr.length!=0){
                                    end =  arr[arr.length-1];
                                }
                                fileOutputStream = new FileOutputStream(fileUtils.createFile(FileName+"."+end));//指定文件保存路径，代码看下一步
                                Log.i("下载后缀：",end );
                                byte[] buf = new byte[1024];
                                int ch;
                                while ((ch = is.read(buf)) != -1) {
                                    fileOutputStream.write(buf, 0, ch);//将获取到的流写入文件中
                                    downloadSize += ch;
                                    //更新进度条
                                    Message message = mHandler.obtainMessage();
                                    Log.i("下载数量：",downloadSize+"");
                                    message.arg1 = downloadSize;
                                    message.arg2 = listPosition;
                                    mHandler.sendMessage(message);
                                    if(downloadSize==contentLength){
                                        OpenFileUtil.openFile(filePath+"/"+FileName+"."+end);
                                    }

                                }
                            }
                            if (fileOutputStream != null) {
                                fileOutputStream.flush();
                                fileOutputStream.close();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Toast.makeText(ContractApprovalActivity.this,"文件下载至："+Environment.getExternalStorageDirectory().toString() + "/OA_file",Toast.LENGTH_SHORT).show();
        }
    }
    public void ChoosePerson() {
        Log.i("审核人员选择事件：","点击显示弹出框");

        final PopupWindow popupWindow=new PopupWindow(auditorPopWindowView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);

        //设置PopupWindow布局
        //popupWindow.setContentView(view1);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        // 设置PopupWindow是否能响应外部点击事件
        popupWindow.setOutsideTouchable(true);
        // 设置PopupWindow是否能响应点击事件
        popupWindow.setTouchable(true);
        //返回键点击，弹出
        popupWindow.setFocusable(true);
        popupWindow.update();
        //设置动画
        popupWindow.setAnimationStyle(R.style.shipping_popup_style);
        //在父布局的弹入/出位置
        View rootView =LayoutInflater.from(this).inflate(R.layout.activity_contract_approval, null);
        popupWindow.showAtLocation(rootView, Gravity.CENTER,0,0);
        //实例化控件
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //选好审核人员，确定提交；在下拉列表的时间中，把选择的审核人赋值给ApproveMainChiefName了，所以，点击确定之后读取这个值就好
                if (ApproveMainChiefName.equals("请选择")){
                    Toast.makeText(ContractApprovalActivity.this,"请选择下一步审核人员！",Toast.LENGTH_LONG).show();
                }else{
                    popupWindow.dismiss();
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消，没有选择审核人员，应该是审核事件被终止
                popupWindow.dismiss();
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
    }
    //初始化审核人下拉列表
    private void initAssessorSp(final JSONArray temp){
        /*设置数据源*/
        ListAssessor=new ArrayList<String>();
        /*新建适配器*/
        AssessorAdapter=new ArrayAdapter<String>(this,R.layout.spinner_display_style,ListAssessor);
        /*adapter设置一个下拉列表样式，参数为系统子布局*/
        AssessorAdapter.setDropDownViewResource(R.layout.spinner_dropdown_style);

        /*spDown加载适配器*/
        assessorSp.setAdapter(AssessorAdapter);

        /*soDown的监听器*/
        assessorSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String assessor =AssessorAdapter.getItem(i);   //获取选中的那一项
                ApproveMainChiefName = assessor;
                try {
                    ApproveMainChiefID = temp.getJSONObject(i-1).getString("MainChiefID");
                }catch (JSONException ex){
                    Log.e("InfoChildFragment",ex.getMessage());
                    Toast.makeText(ContractApprovalActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                }

                Log.i("您选择的印章类型是",assessor);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //设置默认值;
        assessorSp.setSelection(0);
    }
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            searchResultView.setVisibility(show ? View.GONE : View.VISIBLE);
            searchResultView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    searchResultView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            searchProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            searchProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    searchProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            searchProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            searchResultView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}

