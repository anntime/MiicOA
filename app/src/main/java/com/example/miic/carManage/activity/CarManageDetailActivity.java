package com.example.miic.carManage.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.base.http.PostRequest;
import com.example.miic.carManage.adapter.CarTimeLineItemAdapter;
import com.example.miic.carManage.common.CarCommon;
import com.example.miic.carManage.item.CarApproveStatus;
import com.example.miic.carManage.item.CarTimeLineItem;
import com.example.miic.common.MyApplication;
import com.example.miic.sealManagement.adapter.TimeLineItemAdapter;
import com.example.miic.sealManagement.item.ApproveStatus;
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

public class CarManageDetailActivity extends AppCompatActivity {
    private LinearLayout backBtn;
    private Button returnBtn;
    private String ClickID;
    private String ProjID = "ebedc18c-f1ef-11e7-aeab-00155d07b40a";

    private RecyclerView mRecyclerView;
    private CarTimeLineItemAdapter mTimeLineAdapter;
    private List<CarTimeLineItem> mDataList = new ArrayList<>();

    private TextView carNum;
    private TextView createrName;
    private TextView travelWay;
    private TextView isWorkTime;
    private TextView beginTime;
    private TextView endTime;
    private TextView returnTime;
    private TextView useKm;
    private TextView checkKm;
    private TextView carAssessor;
    private String MainChiefName;
    private String MainChiefID;
    private TextView assessorTv;
    private Spinner assessorSp;
    private List<String> ListAssessor;
    private ArrayAdapter<String>  AssessorAdapter;
    private JSONArray ApprovalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_manage_detail);
        carNum = (TextView)findViewById(R.id.car_num);
        createrName = (TextView)findViewById(R.id.creater_name);
        travelWay = (TextView)findViewById(R.id.travel_way);
        isWorkTime = (TextView)findViewById(R.id.is_worktime);
        beginTime = (TextView)findViewById(R.id.begin_time);
        endTime = (TextView)findViewById(R.id.end_time);
        returnTime = (TextView)findViewById(R.id.return_time);
        useKm = (TextView)findViewById(R.id.use_km);
        checkKm = (TextView)findViewById(R.id.check_km);
        carAssessor = (TextView)findViewById(R.id.assessor);

        backBtn = (LinearLayout)findViewById(R.id.search_back);
        returnBtn = (Button)findViewById(R.id.submit_btn);
        //获取上一页面传递的参数
        Intent intent = getIntent();
        String clickID = intent.getStringExtra("carApplyID");
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
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //获取用车申请信息
        getCarApplyDetail();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);
        mTimeLineAdapter = new CarTimeLineItemAdapter(mDataList,CarManageDetailActivity.this);
        mRecyclerView.setAdapter(mTimeLineAdapter);

    }

    private void getCarApplyDetail(){
        JSONObject requestJson = new JSONObject();
        try{
            requestJson.put("id",ClickID);
        }catch (JSONException ex){
            Log.i("getDetail","json对象构造错误");
        }
        RequestBody userView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.GetCarDetailInfo(userView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    //System.out.println("body:" + response.body());
                    if(response.body()!=null ){
                       // System.out.println("body:" + response.body());
                        CarCommon api = new CarCommon();
                        //两层Json数组处理
                        //JSONArray jsonArray = new JSONArray(new JSONObject((response.body())).getString("d"));
                        //JSONArray accList = jsonArray.getJSONObject(0).getJSONArray("AccsList");
                        //String AccsID = accList.getJSONObject(0).getString("AccsID");
                        //Log.i("用车附件：",AccsID);

                        JSONObject jsonObject = new JSONObject(new JSONObject(response.body()).getString("d")).getJSONObject("CarInfo");
                        Log.i("请求信息格式",jsonObject.toString());
                        carNum.setText(jsonObject.getString("CarNumName"));
                        createrName.setText(jsonObject.getString("CreaterName"));
                        travelWay.setText(jsonObject.getString("TravelWay"));
                        //isWorkTime.setText(jsonObject.getString("IsWorkingTime"));
                        isWorkTime.setText(api.getIsWorkTimeText(jsonObject.getString("IsWorkingTime")));
                        Log.i("getCarApplyDetail",jsonObject.getString("BeginTime"));
                        Log.i("getCarApplyDetail",stampToDate(jsonObject.getString("BeginTime")));
                        if(!jsonObject.isNull("BeginTime")&&jsonObject.getString("BeginTime")!=null&&jsonObject.getString("BeginTime")!=""){
                            beginTime.setText(stampToDate(jsonObject.getString("BeginTime")).toString());
                        }
                        if(!jsonObject.isNull("EndTime")&&jsonObject.getString("EndTime")!=null&&jsonObject.getString("EndTime")!=""){
                            endTime.setText(stampToDate(jsonObject.getString("EndTime")).toString());
                        }
                        if(!jsonObject.isNull("ReturnTime")&&jsonObject.getString("ReturnTime")!=null&&jsonObject.getString("ReturnTime")!=""){
                            returnTime.setText(stampToDate(jsonObject.getString("ReturnTime")).toString());
                        }
                        if(!jsonObject.isNull("UsedKmNum")&&jsonObject.getString("UsedKmNum")!=null&&jsonObject.getString("UsedKmNum")!=""){
                            useKm.setText(jsonObject.getString("UsedKmNum"));
                        }
                        if(!jsonObject.isNull("CheckKmNum")&&jsonObject.getString("CheckKmNum")!=null&&jsonObject.getString("CheckKmNum")!="") {
                            checkKm.setText(jsonObject.getString("CheckKmNum"));
                        }
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
                            mDataList.add(new CarTimeLineItem(approvalName, approvalTime,CarApproveStatus.parse(approvalStatus)));
                            Log.i("detail",mDataList.toString());
                        }
                        mTimeLineAdapter.notifyDataSetChanged();

                    }else{
                        Toast.makeText(CarManageDetailActivity.this,"获取用户信息失败！",Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException ex){
                    Log.e("InfoChildFragment",ex.getMessage());
                    Toast.makeText(CarManageDetailActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(CarManageDetailActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }

    //获取审核人员信息
    private void getChiefInfo(){
        JSONObject requestJson = new JSONObject();
        String deptID = MyApplication.getDeptID();
        try{
            JSONObject val = new JSONObject();
            val.put("ProjID",ProjID);
            val.put("deptID",deptID);
            requestJson.put("wfProjDeptView",val);
        }catch (JSONException ex){
            Log.i("MyPageFrgment","json对象构造错误");
        }
        RequestBody userView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.CarChooseProjKV (userView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try {
                        System.out.println("body:" + response.body());
                        JSONObject jsonObject = new JSONObject(response.body());
                        JSONObject jsonTemp = jsonObject.getJSONObject("d");
                        JSONObject json = new JSONObject();
                        String projID = jsonTemp.getString("Name");
                        try{
                            json.put("projID",projID);
                            json.put("level",1);
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
                                        JSONArray jsonTemp = new JSONArray(jsonObject.getString("d"));
                                        ApprovalList = jsonTemp;
                                        if (jsonTemp.getJSONObject(jsonTemp.length()-1).getJSONArray("MainChiefList").length()==1){
                                            JSONArray mainChief = jsonTemp.getJSONObject(jsonTemp.length()-1).getJSONArray("MainChiefList");
                                            assessorTv.setText(mainChief.getJSONObject(0).getString("MainChiefName"));
                                            MainChiefName= mainChief.getJSONObject(0).getString("MainChiefName");
                                            MainChiefID = mainChief.getJSONObject(0).getString("MainChiefID");
                                        }else{
                                            if(jsonTemp.getJSONObject(jsonTemp.length()-1).getJSONArray("MainChiefList").length()>1){
                                                //有多个审批人的时候
                                                assessorSp.setVisibility(View.VISIBLE);
                                                assessorTv.setVisibility(View.GONE);

                                                JSONArray temp = jsonTemp.getJSONObject(jsonTemp.length()-1).getJSONArray("MainChiefList");
                                                initAssessorSp(temp);
                                                ListAssessor.add("请选择");
                                                for(int i=0;i<temp.length();i++){
                                                    ListAssessor.add(temp.getJSONObject(i).getString("MainChiefName"));
                                                }

                                            }else{
                                                JSONArray mainChief = jsonTemp.getJSONObject(jsonTemp.length()-1).getJSONArray("MainChiefList");
                                                assessorTv.setText(mainChief.getJSONObject(0).getString("MainChiefName"));
                                                MainChiefName= mainChief.getJSONObject(0).getString("MainChiefName");
                                                MainChiefID = mainChief.getJSONObject(0).getString("MainChiefID");
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
                                Toast.makeText(CarManageDetailActivity.this, "网络错误，请稍后再试！", Toast.LENGTH_SHORT).show();
                            }
                        };
                        PostRequest.Instance.CommonAsynPost(call2, callback2);
                    }catch (JSONException ex){
                        Log.e("InfoChildFragment",ex.getMessage());
                        Toast.makeText(CarManageDetailActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(CarManageDetailActivity.this,"获取用户信息失败！",Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(CarManageDetailActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }

    //初始化审批人下拉列表
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
                MainChiefName = assessor;
                try {
                    MainChiefID = temp.getJSONObject(i-1).getString(MainChiefName);
                }catch (JSONException ex){
                    Log.e("InfoChildFragment",ex.getMessage());
                    Toast.makeText(CarManageDetailActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
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

}
