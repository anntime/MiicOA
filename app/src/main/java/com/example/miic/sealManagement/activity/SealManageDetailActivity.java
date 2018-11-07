package com.example.miic.sealManagement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.base.http.PostRequest;
import com.example.miic.common.MyApplication;
import com.example.miic.sealManagement.adapter.TimeLineItemAdapter;
import com.example.miic.sealManagement.common.SealCommon;
import com.example.miic.sealManagement.item.ApproveStatus;
import com.example.miic.sealManagement.item.SealApproveStatus;
import com.example.miic.sealManagement.item.TimeLineItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.miic.oa.common.Setting.stampToDate;

public class SealManageDetailActivity extends AppCompatActivity {
    private LinearLayout backBtn;
    private String ClickID;

    private RecyclerView mRecyclerView;
    private TimeLineItemAdapter mTimeLineAdapter;
    private List<TimeLineItem> mDataList = new ArrayList<>();

    private TextView sealUseType;
    private TextView depatName;
    private TextView sealTime;
    private TextView sealReason;
    private TextView sealOrgnization;
    private TextView sealType;
    private TextView sealOperator;
    private TextView sealNum;
    private TextView sealAssessor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seal_manage_detail);
        sealUseType = (TextView)findViewById(R.id.seal_use_type);
        depatName = (TextView)findViewById(R.id.dept_name);
        sealTime = (TextView)findViewById(R.id.time);
        sealReason = (TextView)findViewById(R.id.reason);
        sealOrgnization = (TextView)findViewById(R.id.organization);
        sealType = (TextView)findViewById(R.id.seal_type);
        sealOperator = (TextView)findViewById(R.id.operator);
        sealNum = (TextView)findViewById(R.id.seal_num);
        sealAssessor = (TextView)findViewById(R.id.assessor);

        backBtn = (LinearLayout)findViewById(R.id.search_back);
        //获取上一页面传递的参数
        Intent intent = getIntent();
        String clickID = intent.getStringExtra("sealApplyID");
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
        getSealApplyDetail();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);

        mTimeLineAdapter = new TimeLineItemAdapter(mDataList,SealManageDetailActivity.this);
        mRecyclerView.setAdapter(mTimeLineAdapter);

    }
    private void getSealApplyDetail(){
        JSONObject requestJson = new JSONObject();
        try{
            requestJson.put("id",ClickID);
        }catch (JSONException ex){
            Log.i("getOperator","json对象构造错误");
        }
        RequestBody userView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.GetMySealDetailInfo(userView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    System.out.println("body:" + response.body());
                    if(response.body()!=null ){
                        System.out.println("body:" + response.body());
                        SealCommon api = new SealCommon();
                        JSONObject jsonObject = new JSONObject(new JSONObject(response.body()).getString("d")).getJSONObject("SealInfo");
                        Log.i("请求信息格式",jsonObject.toString());
                        sealUseType.setText(api.getSealUseTypeText(jsonObject.getString("SealApplication")));
                        depatName.setText(jsonObject.getString("DeptName"));
                        Log.i("getSealApplyDetail",jsonObject.getString("UsingTime"));
                        Log.i("getSealApplyDetail",stampToDate(jsonObject.getString("UsingTime")));
                        if(!jsonObject.isNull("UsingTime")&&jsonObject.getString("UsingTime")!=null&&jsonObject.getString("UsingTime")!=""){
                            sealTime.setText(stampToDate(jsonObject.getString("UsingTime")).toString());
                        }
                        sealReason.setText(jsonObject.getString("Content"));
                        sealOrgnization.setText(jsonObject.getString("ToDept"));
                        sealType.setText(api.getSealTypeText(jsonObject.getString("SealType")));
                        sealOperator.setText(jsonObject.getString("CreaterName"));
                        sealNum.setText(jsonObject.getString("SealNum"));
                        //getChiefInfo();

                        JSONArray approveList = new JSONObject(new JSONObject(response.body()).getString("d")).getJSONArray("ApproveList");
                        for (int i=0;i<approveList.length();i++){
                            JSONObject approveObj = approveList.getJSONObject(i);
                            String remark;
                            String approvalTime;
                            if(!approveObj.isNull("ApprovalRemark")){
                               remark = approveObj.getString("ApprovalRemark");
                            }else {
                               remark = "";
                            }
                            if(!approveObj.isNull("ApprovalTime")){
                                approvalTime = approveObj.getString("ApprovalTime");
                            }else {
                                approvalTime = "";
                            }
                            String approvalName = approveObj.getString("ApprovalName");
                            String approvalStatus =approveObj.getString("ApprovalStatus");
                            mDataList.add(new TimeLineItem(approvalName, approvalTime,SealApproveStatus.parse(approvalStatus)));
                            Log.i("detail",mDataList.toString());
                        }
                        sealAssessor.setText(approveList.getJSONObject(0).getString("ApprovalName"));
                        mTimeLineAdapter.notifyDataSetChanged();

                    }else{
                        Toast.makeText(SealManageDetailActivity.this,"获取用户信息失败！",Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException ex){
                    Log.e("InfoChildFragment",ex.getMessage());
                    Toast.makeText(SealManageDetailActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(SealManageDetailActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
//    //获取审核人员信息
//    private void getChiefInfo(){
//        JSONObject requestJson = new JSONObject();
//        String deptID = MyApplication.getDeptID();
//        try{
//            JSONObject val = new JSONObject();
//            val.put("ProjID",ProjID);
//            val.put("DeptID",deptID);
//            requestJson.put("wfProjDeptView",val);
//        }catch (JSONException ex){
//            Log.i("MyPageFrgment","json对象构造错误");
//        }
//        RequestBody userView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
//        Log.i("请求信息格式",requestJson.toString());
//        Call<String> call = PostRequest.Instance.request.ChooseProjKV (userView);
//        Callback<String> callback = new Callback<String>() {
//            //请求成功时回调
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//                if(response.body()!=null){
//                    try {
//                        System.out.println("body:" + response.body());
//                        JSONObject jsonObject = new JSONObject(response.body());
//                        JSONArray jsonTemp = new JSONArray(jsonObject.getString("d"));
//                        sealAssessor.setText(jsonTemp.getJSONObject(0).getString("MainChiefName"));
//                    }catch (JSONException ex){
//                        Log.e("InfoChildFragment",ex.getMessage());
//                        Toast.makeText(SealManageDetailActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
//                    }
//                }else{
//                    Toast.makeText(SealManageDetailActivity.this,"获取用户信息失败！",Toast.LENGTH_SHORT).show();
//                }
//            }
//            //请求失败时回调
//            @Override
//            public void onFailure(Call<String> call, Throwable throwable) {
//                System.out.println("请求失败" + call.request());
//                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
//                Toast.makeText(SealManageDetailActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
//            }
//        };
//        PostRequest.Instance.CommonAsynPost(call, callback);
//    }
    //获取审核人员信息
//    private void getChiefInfo(){
//        JSONObject requestJson = new JSONObject();
//        String deptID = DeptID;
//        try{
//            requestJson.put("deptID",deptID);
//        }catch (JSONException ex){
//            Log.i("getChiefInfo","json对象构造错误");
//        }
//        RequestBody view = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
//        Log.i("请求信息格式",requestJson.toString());
//        Call<String> call = PostRequest.Instance.request.ChooseProjKV (view);
//        Callback<String> callback = new Callback<String>() {
//            //请求成功时回调
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//                if(response.body()!=null){
//                    try {
//                        System.out.println("body:" + response.body());
//                        JSONObject jsonObject = new JSONObject(response.body());
//                        JSONObject jsonTemp = jsonObject.getJSONObject("d");
//                        JSONObject json = new JSONObject();
//                        String projID = jsonTemp.getString("Name");
//                        try{
//                            json.put("projID",projID);
//                            json.put("level",1);
//                        }catch (JSONException ex){
//                            Log.i("getChiefInfo","json对象构造错误");
//                        }
//                        RequestBody Proview = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());
//                        Call<String> call2 = PostRequest.Instance.request.GetNextApprovalList (Proview);
//                        Callback<String> callback2 = new Callback<String>() {
//                            //请求成功时回调
//                            @Override
//                            public void onResponse(Call<String> call, Response<String> response) {
//                                if (response.body() != null) {
//                                    try {
//                                        System.out.println("" +
//                                                "" + response.body());
//                                        JSONObject jsonObject = new JSONObject(response.body());
//                                        JSONArray jsonTemp = new JSONArray(jsonObject.getString("d"));
//                                        ApprovalList = jsonTemp;
//                                        if (jsonTemp.getJSONObject(jsonTemp.length()-1).getJSONArray("MainChiefList").length()==1){
//                                            JSONArray mainChief = jsonTemp.getJSONObject(jsonTemp.length()-1).getJSONArray("MainChiefList");
//                                            assessorTv.setText(mainChief.getJSONObject(0).getString("MainChiefName"));
//                                            MainChiefName= mainChief.getJSONObject(0).getString("MainChiefName");
//                                            MainChiefID = mainChief.getJSONObject(0).getString("MainChiefID");
//                                        }else{
//                                            if(jsonTemp.getJSONObject(jsonTemp.length()-1).getJSONArray("MainChiefList").length()>1){
//                                                //有多个审批人的时候
//                                                assessorSp.setVisibility(View.VISIBLE);
//                                                assessorTv.setVisibility(View.GONE);
//
//                                                JSONArray temp = jsonTemp.getJSONObject(jsonTemp.length()-1).getJSONArray("MainChiefList");
//                                                initAssessorSp(temp);
//                                                ListAssessor.add("请选择");
//                                                for(int i=0;i<temp.length();i++){
//                                                    ListAssessor.add(temp.getJSONObject(i).getString("MainChiefName"));
//                                                }
//
//                                            }else{
//                                                JSONArray mainChief = jsonTemp.getJSONObject(jsonTemp.length()-1).getJSONArray("MainChiefList");
//                                                assessorTv.setText(mainChief.getJSONObject(0).getString("MainChiefName"));
//                                                MainChiefName= mainChief.getJSONObject(0).getString("MainChiefName");
//                                                MainChiefID = mainChief.getJSONObject(0).getString("MainChiefID");
//                                            }
//                                        }
//                                    } catch (JSONException ex) {
//                                        Log.i("getChiefInfo", "json对象构造错误");
//                                    }
//                                }
//                            }
//
//                            public void onFailure(Call<String> call, Throwable throwable) {
//                                System.out.println("请求失败" + call.request());
//                                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
//                                Toast.makeText(SealApplyActivity.this, "网络错误，请稍后再试！", Toast.LENGTH_SHORT).show();
//                            }
//                        };
//                        PostRequest.Instance.CommonAsynPost(call2, callback2);
//                    }catch (JSONException ex){
//                        Log.e("InfoChildFragment",ex.getMessage());
//                        Toast.makeText(SealApplyActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
//                    }
//                }else{
//                    Toast.makeText(SealApplyActivity.this,"获取用户信息失败！",Toast.LENGTH_SHORT).show();
//                }
//            }
//            //请求失败时回调
//            @Override
//            public void onFailure(Call<String> call, Throwable throwable) {
//                System.out.println("请求失败" + call.request());
//                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
//                Toast.makeText(SealApplyActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
//            }
//        };
//        PostRequest.Instance.CommonAsynPost(call, callback);
//    }


}
