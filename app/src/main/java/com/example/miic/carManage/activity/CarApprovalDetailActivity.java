package com.example.miic.carManage.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.PopupWindow;
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

public class CarApprovalDetailActivity extends AppCompatActivity {
    private LinearLayout backBtn;
    private EditText remarkEt;
    private Button passBtn;
    private Button disagreeBtn;
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
    private TextView carApproveChiefTv;
    private View auditorPopWindowView;
    private Button submitBtn;
    private Button cancelBtn;
    private String ApproveRemark;

    private JSONObject SubProjKV =new JSONObject();
    private String ApproveID;
    private String ApproveMainChiefID;
    private String ApproveMainChiefName;
    private JSONArray ApprovalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_approval_detail);
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
        remarkEt = (EditText)findViewById(R.id.remark_keyword);
        passBtn = (Button)findViewById(R.id.pass_btn);
        disagreeBtn = (Button)findViewById(R.id.disagree_btn);

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

        //获取用车申请信息
        getCarApplyDetail();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);
        mTimeLineAdapter = new CarTimeLineItemAdapter(mDataList,CarApprovalDetailActivity.this);
        mRecyclerView.setAdapter(mTimeLineAdapter);

        ApproveRemark = remarkEt.getText().toString();

        //弹出框
        auditorPopWindowView = LayoutInflater.from(this).inflate(R.layout.car_choose_auditor_pop_window,null);//PopupWindow对象
        assessorSp= (Spinner) auditorPopWindowView.findViewById(R.id.sp_assessor);
        submitBtn = (Button) auditorPopWindowView.findViewById(R.id.submit_btn);
        cancelBtn = (Button) auditorPopWindowView.findViewById(R.id.cancel_btn);

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

                        JSONObject jsonObject1 = new JSONObject(response.body());
                        String jsonStrTemp = jsonObject1.getString("d");
                        JSONObject jsonTemp = new JSONObject(jsonStrTemp);
                        SubProjKV.put("SubProjID",jsonTemp.getString("SubProjID"));
                        SubProjKV.put("SubProjName",jsonTemp.getString("SubProjName"));

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
                        getChiefInfo();

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
                            if (i==0){
                                if (approveObj.getString("ApprovalID").equals(new MyApplication().getUserID())
                                        && approveObj.getString("ApprovalStatus").equals(CarApproveStatus.Create.getIndex()+"")) {
                                    //审批按钮显示并绑定事件
                                    ApproveID = approveObj.getString("ID");
                                    passBtn.setVisibility(View.VISIBLE);
                                    disagreeBtn.setVisibility(View.VISIBLE);
                                    //通过
                                    passBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            CarApproveEvent(CarApproveStatus.Agree);
                                        }
                                    });
                                    //退回
                                    disagreeBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            CarApproveEvent(CarApproveStatus.Retreat);
                                        }
                                    });
                                } else {
                                    //隐藏审批相关按钮
                                    remarkEt.setVisibility(View.GONE);
                                    passBtn.setVisibility(View.GONE);
                                    disagreeBtn.setVisibility(View.GONE);
                                }
                                mDataList.add(new CarTimeLineItem(approvalName, approvalTime, CarApproveStatus.parse(approvalStatus)));
                            }else
                                mDataList.add(new CarTimeLineItem(approvalName, approvalTime, CarApproveStatus.parse(approvalStatus)));
                            //mDataList.add(new TimeLineItem(approvalName, approvalTime,status ,remark));
                            Log.i("detail",mDataList.toString());
                        }
                        mTimeLineAdapter.notifyDataSetChanged();

                    }else{
                        Toast.makeText(CarApprovalDetailActivity.this,"获取用户信息失败！",Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException ex){
                    Log.e("InfoChildFragment",ex.getMessage());
                    Toast.makeText(CarApprovalDetailActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(CarApprovalDetailActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }

    //审批事件
    private void CarApproveEvent(CarApproveStatus operStatus ){
        JSONObject requestJson = new JSONObject();
        JSONObject projKV = new JSONObject();
        JSONArray person = GetApprovePersonList(operStatus);

        try{
            projKV.put("Name",SubProjKV.getString("SubProjID"));
            projKV.put("Value",SubProjKV.getString("SubProjName"));

            requestJson.put("projKV",projKV);
            requestJson.put("person",person);
        }catch (JSONException ex){
            Log.i("CarSearch","json对象构造错误");
        }
        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Call<String> call = PostRequest.Instance.request.ApproveCar(keywordView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            //@Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        JSONObject data = jsonObject.isNull("d")?null:jsonObject.getJSONObject("d");
                        if(data!=null){
                            Boolean res = data.getBoolean("Result");
                            if (res==true){
                                Toast.makeText(CarApprovalDetailActivity.this,"用车审批成功~",Toast.LENGTH_LONG).show();
                                finish();
                            }else {
                                Toast.makeText(CarApprovalDetailActivity.this,"用车审批失败~",Toast.LENGTH_LONG).show();
                            }
                        }

                    }catch (JSONException ex){
                        Log.e("CarSearch",ex.getMessage());
                        Toast.makeText(CarApprovalDetailActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
            //请求失败时回调
           // @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(CarApprovalDetailActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }

    //获取审核人员列表（审核时用）
    private JSONArray GetApprovePersonList(CarApproveStatus operStatus){
        String operStatusSetting =  operStatus.getIndex()+"";
        JSONArray person = new JSONArray();
        try{
            if(ApprovalList.length() > 1){
                if(operStatus == CarApproveStatus.Agree){
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
            Log.i("CarSearch","json对象构造错误:"+ex.getMessage());
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
                        Toast.makeText(CarApprovalDetailActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(CarApprovalDetailActivity.this,"获取用户信息失败！",Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(CarApprovalDetailActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }

    private void GetNextApprovalList(int level){
        JSONObject json = new JSONObject();
        try{
            json.put("projID",SubProjKV.getString("SubProjID"));
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
                            carApproveChiefTv.setText(mainChief.getJSONObject(0).getString("MainChiefName"));
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
                                carApproveChiefTv.setText(mainChief.getJSONObject(0).getString("MainChiefName"));
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
                Toast.makeText(CarApprovalDetailActivity.this, "网络错误，请稍后再试！", Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call2, callback2);
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
        View rootView = LayoutInflater.from(this).inflate(R.layout.activity_car_approval_detail, null);
        popupWindow.showAtLocation(rootView, Gravity.CENTER,0,0);
        //实例化控件
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //选好审核人员，确定提交；在下拉列表的时间中，把选择的审核人赋值给ApproveMainChiefName了，所以，点击确定之后读取这个值就好
                if (ApproveMainChiefName.equals("请选择")){
                    Toast.makeText(CarApprovalDetailActivity.this,"请选择下一步审核人员！",Toast.LENGTH_LONG).show();
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
                    Toast.makeText(CarApprovalDetailActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
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
