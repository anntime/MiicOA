package com.example.miic.sealManagement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.example.miic.common.MyApplication;
import com.example.miic.sealManagement.common.SealCommon;

import org.json.JSONArray;
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

import static com.example.miic.oa.common.Setting.stampToDate;
import static java.lang.Integer.parseInt;

public class SealApplyActivity extends AppCompatActivity {
    private LinearLayout backBtn;

    private Spinner SealUseTypeSp;
    private List<String> ListSealUseType;
    private List<String> ListAssessor;
    private ArrayAdapter<String>  AssessorAdapter;
    private ArrayAdapter<String> SealUseTypeAdapter;

    private Spinner SealTypeSp;
    private List<String> ListSealType;
    private ArrayAdapter<String> SealTypeAdapter;

    private TimePickerView pvTime;
    private TextView btn_Time;

    private TextView deptTv;
    private Spinner deptNameSp;
    private List<String> ListDeptName;
    private ArrayAdapter<String> DeptNameAdapter;
    private TextView operatorTv;
    private TextView assessorTv;
    private Spinner assessorSp;
    private String ProjID = "";

    private EditText reasonTv;
    private EditText organizationTv;
    private EditText sealNumTv;

    private Button submitBtn;
    private Button saveBtn;

    private String ChooseSealType = "";
    private String ChooseSealUseType = "";
    private String ChooseTime = "";
    private String ChooseAssessor = "";
    private String DeptID;
    private String DeptName;
    private JSONArray DeptList;
    private String Reason;
    private String ToDept;
    private String SealNum;
    private String sealApplyID;
    private String MainChiefName;
    private String MainChiefID;
    private JSONArray ApprovalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seal_apply);
        backBtn = (LinearLayout)findViewById(R.id.search_back);
        DeptID = MyApplication.getDeptID();
        init();
    }
    private void init(){
        deptTv = (TextView)findViewById(R.id.dept_name);
        deptNameSp = (Spinner)findViewById(R.id.dept_name_sp);
        operatorTv = (TextView)findViewById(R.id.operator);
        assessorTv = (TextView)findViewById(R.id.assessor);
        assessorSp = (Spinner)findViewById(R.id.sp_assessor);
        assessorSp.setVisibility(View.GONE);
        reasonTv = (EditText)findViewById(R.id.reason);
        organizationTv = (EditText)findViewById(R.id.organization);
        sealNumTv = (EditText)findViewById(R.id.seal_num);
        submitBtn = (Button)findViewById(R.id.submit_btn);
        saveBtn = (Button)findViewById(R.id.save_btn);

        DeptName = deptTv.getText().toString();
        Reason = reasonTv.getText().toString();
        ToDept = organizationTv.getText().toString();
        SealNum = sealNumTv.getText().toString();
        initDeptNameSpinner();
        //返回按钮事件
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //用印类型
        initSealType();
        //用印时间
        initSealUseTime();
        //印章类型
        initSealUserType();
        Intent intent = getIntent();
        sealApplyID = intent.getStringExtra("sealApplyID");
        if(sealApplyID!=null){
            getSealApply(sealApplyID);
        }else{
            getSealOperatorInfo(MyApplication.getDeptID());
        }
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitSealApplication();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSealApplication();
            }
        });
    }
    //初始化部门列表
    private void initDeptNameSpinner(){
        /*设置数据源*/
        ListDeptName=new ArrayList<String>();
        ListDeptName.add("请选择");
        getMyDeptInfoList();
        /*新建适配器*/
        DeptNameAdapter=new ArrayAdapter<String>(this,R.layout.spinner_display_style,ListDeptName);
        /*adapter设置一个下拉列表样式，参数为系统子布局*/
        DeptNameAdapter.setDropDownViewResource(R.layout.spinner_dropdown_style);

        /*spDown加载适配器*/
        deptNameSp.setAdapter(DeptNameAdapter);

        /*soDown的监听器*/
        deptNameSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String sealType=DeptNameAdapter.getItem(i);   //获取选中的那一项
                DeptName = sealType;
                try{
                    for (int index=0;index<DeptList.length();index++){
                        JSONObject jsonObject = DeptList.getJSONObject(index);
                        if(DeptName.equals(jsonObject.getString("DeptName"))){
                             DeptID = jsonObject.getString("DeptID");
                        }
                    }
                }catch (JSONException ex){
                    Toast.makeText(SealApplyActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

                Log.i("您选择的印章类型是",sealType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //设置默认值;
        deptNameSp.setSelection(0);
    }
    //初始化用印类型下拉列表
    private void initSealUserType(){
        SealUseTypeSp = (Spinner)findViewById(R.id.seal_use_type);
        /*设置数据源*/
        ListSealUseType=new ArrayList<String>();
        ListSealUseType.add("请选择");
        ListSealUseType.add("即时用印");
        ListSealUseType.add("携印外出");
        /*新建适配器*/
        SealUseTypeAdapter=new ArrayAdapter<String>(this,R.layout.spinner_display_style,ListSealUseType);
        /*adapter设置一个下拉列表样式，参数为系统子布局*/
        SealUseTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_style);

        /*spDown加载适配器*/
        SealUseTypeSp.setAdapter(SealUseTypeAdapter);

        /*soDown的监听器*/
        SealUseTypeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String sealType=SealUseTypeAdapter.getItem(i);   //获取选中的那一项
                ChooseSealUseType = new SealCommon().getSealUseType(sealType);
                Log.i("您选择的印章类型是",sealType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //设置默认值;
        SealUseTypeSp.setSelection(0);

    }
    //初始化印章类型下拉列表
    private void initSealType(){
        SealTypeSp = (Spinner)findViewById(R.id.seal_type);
        /*设置数据源*/
        ListSealType=new ArrayList<String>();
        ListSealType.add("请选择");
        getSealTypeInfos();
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
                Log.i("您选择的印章类型是",sealType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //设置默认值;
        SealTypeSp.setSelection(0);

    }

    //用印时间
    private void initSealUseTime(){
        initTimePicker();
        btn_Time = (TextView) findViewById(R.id.btn_Time);
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
                Toast.makeText(SealApplyActivity.this, getTime(date).split(" ")[0], Toast.LENGTH_SHORT).show();
                btn_Time.setText(getTime(date).split(" ")[0]);
                ChooseTime = getTime(date).split(" ")[0];
                Log.i("pvTime", "onTimeSelect");


            }
        }).setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
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
    //获取印章类型
    public void getSealTypeInfos(){
        Call<String> call = PostRequest.Instance.request.GetSealTypeInfos( );
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        JSONArray jsonArr = jsonObject.getJSONArray("d");
                        for(int i=0;i<jsonArr.length();i++){
                            JSONObject jsonObjTem =  jsonArr.getJSONObject(i) ;
                            ListSealType.add(jsonObjTem.getString("Value"));
                        }
                    }
                    catch (JSONException ex){
                        Log.i("getSealTypeInfos","json对象构造错误");
                    }
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(SealApplyActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //获取用印申请部门
    public void getMyDeptInfoList(){
        String str = "{userID:'" + MyApplication.getUserID() + "'}";
        RequestBody userView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),str);
        Call<String> call = PostRequest.Instance.request.GetLoginInfo(userView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        Log.i("GetLoginInfoResponse",jsonObject.toString());
                        JSONObject jsonObj= new JSONObject(jsonObject.getString("d"));
                        Log.i("GetLoginInfo",jsonObj.toString());
                        Log.i("GetLoginInfo",new SealCommon().getYesOrNo("No"));

                        String manage = jsonObj.getString("Manage")+"";
                        Log.i("GetLoginInfo",manage);
                        Log.i("GetLoginInfo",(manage.equals(new SealCommon().getYesOrNo("No"))+"1"));
                        if(manage.equals(new SealCommon().getYesOrNo("No"))){
                            //不是
                            deptTv.setText(jsonObj.getString("OrgName"));
                            DeptID = jsonObj.getString("OrgID");
                        }else{
                            JSONObject obj = new JSONObject();
                            obj.put("isApprove",true);
                            obj.put("withMiic",true);
                            RequestBody view = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),obj.toString());

                            Call<String> call2 = PostRequest.Instance.request.GetMyDeptInfoList(view);
                            Callback<String> callback2 = new Callback<String>() {
                                //请求成功时回调
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    if(response.body()!=null){
                                        try {
                                            JSONObject jsonObject = new JSONObject(response.body());
                                            Log.i("GetMyDeptResponse",jsonObject.toString());
                                            JSONArray jsonArr =new JSONArray(jsonObject.getString("d"));
                                            if (jsonArr != null) {
                                                if (jsonArr.length() == 1) {
                                                    JSONObject jsonObjTem =  jsonArr.getJSONObject(0) ;
                                                    deptTv.setText(jsonObjTem.getString("DeptName"));
                                                    DeptID = jsonObjTem.getString("DeptID");
                                                    deptNameSp.setVisibility(View.GONE);
                                                }else if(jsonArr.length() > 1){
                                                    deptTv.setVisibility(View.GONE);
                                                    deptNameSp.setVisibility(View.VISIBLE);
                                                    Log.i("GetMyDeptResponse",jsonArr.toString());
                                                    DeptList = jsonArr;
                                                    for(int i=0;i<jsonArr.length();i++){
                                                        JSONObject jsonObjTem =  jsonArr.getJSONObject(i) ;
                                                        if(!ListDeptName.contains(jsonObjTem.getString("DeptName"))){
                                                            ListDeptName.add(jsonObjTem.getString("DeptName"));
                                                        }

                                                    }
                                                }else{
                                                    deptTv.setText("其他部门");
                                                    deptNameSp.setVisibility(View.GONE);
                                                }
                                            }
                                        }
                                        catch (JSONException ex){
                                            Log.i("getMyDeptInfoList","json对象构造错误");
                                        }
                                    }
                                }
                                //请求失败时回调
                                @Override
                                public void onFailure(Call<String> call, Throwable throwable) {
                                    System.out.println("请求失败" + call.request());
                                    System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                                    Toast.makeText(SealApplyActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
                                }
                            };
                            PostRequest.Instance.CommonAsynPost(call2, callback2);
                        }
                    }
                    catch (JSONException ex){
                        Log.i("getMyDeptInfoList","json对象构造错误");
                    }
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(SealApplyActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);

    }

    //获取用印经办人信息
    private  void getSealOperatorInfo(String parameter){

        JSONObject requestJson = new JSONObject();
        try{
            requestJson.put("id",parameter);
        }catch (JSONException ex){
            Log.i("getOperator","json对象构造错误");
        }
        RequestBody userView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.GetSealOperatorInfo(userView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    if(response.body()!=null&&!(new JSONObject(response.body()).isNull("d"))){
                        System.out.println("body:" + response.body());
                        JSONObject jsonObject = new JSONObject(response.body()).getJSONObject("d");
                        operatorTv.setText(MyApplication.getUserName());
                        //获取部门信息
                        getMyDeptInfoList();
                        getChiefInfo();
                    }else{
                        Toast.makeText(SealApplyActivity.this,"获取用户信息失败！",Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException ex){
                    Log.e("InfoChildFragment",ex.getMessage());
                    Toast.makeText(SealApplyActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                }



            }
            //请求失败时回调
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(SealApplyActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);

    }
    //获取用印申请信息
    private void getSealApply(String parameter){

        JSONObject requestJson = new JSONObject();
        try{
            requestJson.put("id",parameter);
        }catch (JSONException ex){
            Log.i("getOperator","json对象构造错误");
        }
        RequestBody userView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.GetSealApply(userView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    if(response.body()!=null&&!(new JSONObject(response.body()).isNull("d"))){
                        System.out.println("body:" + response.body());
                        JSONObject jsonObject = new JSONObject(response.body()).getJSONObject("d");
                        operatorTv.setText(MyApplication.getUserName());
                        deptTv.setText(jsonObject.getString("DeptName"));
                        reasonTv.setText(jsonObject.getString("Content"));
                        organizationTv.setText(jsonObject.getString("ToDept"));
                        sealNumTv.setText(jsonObject.getString("SealNum"));
                        btn_Time.setText(stampToDate(jsonObject.getString("UsingTime")));
                        SealUseTypeSp.setSelection(parseInt(jsonObject.getString("SealApplication")));
                        SealTypeSp.setSelection(parseInt(jsonObject.getString("SealType")));
                        getChiefInfo();
                    }else{
                        Toast.makeText(SealApplyActivity.this,"获取用户信息失败！",Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException ex){
                    Log.e("InfoChildFragment",ex.getMessage());
                    Toast.makeText(SealApplyActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                }



            }
            //请求失败时回调
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(SealApplyActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);

    }
    //获取审核人员信息
    private void getChiefInfo(){
        JSONObject requestJson = new JSONObject();
        String deptID = DeptID;
        try{
            requestJson.put("deptID",deptID);
        }catch (JSONException ex){
            Log.i("getChiefInfo","json对象构造错误");
        }
        RequestBody view = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.ChooseProjKV (view);
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
                            json.put("deptID",DeptID);
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
                                Toast.makeText(SealApplyActivity.this, "网络错误，请稍后再试！", Toast.LENGTH_SHORT).show();
                            }
                        };
                        PostRequest.Instance.CommonAsynPost(call2, callback2);
                    }catch (JSONException ex){
                        Log.e("InfoChildFragment",ex.getMessage());
                        Toast.makeText(SealApplyActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(SealApplyActivity.this,"获取用户信息失败！",Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(SealApplyActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //提交用印申请
    private void submitSealApplication(){
        RequestBody view = getSealApplicationContent();
        if(view!=null){
            Call<String> call = PostRequest.Instance.request.SubmitSealApplication (view);
            Callback<String> callback = new Callback<String>() {
                //请求成功时回调
                public void onResponse(Call<String> call, Response<String> response) {
                    if(response.body()!=null){
                        try {
                            System.out.println("body:" + response.body());
                            JSONObject jsonObject = new JSONObject(response.body());
                            if(jsonObject.getBoolean("d")==true){
                                Toast.makeText(SealApplyActivity.this,"用印申请成功！",Toast.LENGTH_SHORT).show();
                                finish();
                            }else {
                                Toast.makeText(SealApplyActivity.this,"用印申请失败！",Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException ex){
                            Log.e("InfoChildFragment",ex.getMessage());
                            Toast.makeText(SealApplyActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(SealApplyActivity.this,"获取用户信息失败！",Toast.LENGTH_SHORT).show();
                    }

                }
                //请求失败时回调
                public void onFailure(Call<String> call, Throwable throwable) {
                    System.out.println("请求失败" + call.request());
                    System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                    Toast.makeText(SealApplyActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
                }
            };
            PostRequest.Instance.CommonAsynPost(call, callback);
        }


    }
    //保存用印申请
    private void saveSealApplication(){
        JSONObject requestJson = new JSONObject();
        DeptName = deptTv.getText().toString();
        if(!(DeptName!=null&&DeptName.length() != 0)){
            Toast.makeText(SealApplyActivity.this,"部门名称不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        Reason = reasonTv.getText().toString();
        if(!(Reason!=null&&Reason.length() != 0)){
            Toast.makeText(SealApplyActivity.this,"用印理由不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        ToDept = organizationTv.getText().toString();
        if(!(ToDept!=null&&ToDept.length() != 0)){
            Toast.makeText(SealApplyActivity.this,"发往部门不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        SealNum = sealNumTv.getText().toString();
        if(!(SealNum!=null&&SealNum.length() != 0)){
            Toast.makeText(SealApplyActivity.this,"用印数量不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        if(ChooseSealType==""||ChooseSealType=="0"){
            Toast.makeText(SealApplyActivity.this,"请选择印章类型",Toast.LENGTH_LONG).show();
            return ;
        }
        if(ChooseSealUseType==""||ChooseSealUseType=="0"){
            Toast.makeText(SealApplyActivity.this,"请选择用印类型",Toast.LENGTH_LONG).show();
            return ;
        }
        try{
            JSONObject val1  = new JSONObject();
            if(sealApplyID!=null){
                val1.put("ID", sealApplyID);
            }else {
                val1.put("ID", RandomCode.getCode());
            }
            val1.put("SealApplication", ChooseSealUseType);
            val1.put("UsingTime", ChooseTime);
            val1.put("Content", Reason);
            val1.put("ToDept", ToDept);
            val1.put("SealType", ChooseSealType);
            val1.put("SealNum", SealNum);
            val1.put("DeptID", DeptID);
            val1.put("DeptName", DeptName);
            requestJson.put("sealInfo",val1);
        }catch (JSONException ex){
            Log.i("getOperator","json对象构造错误");
        }
        RequestBody view = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Call<String> call = PostRequest.Instance.request.SaveSealApplication (view);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try {
                        System.out.println("body:" + response.body());
                        JSONObject jsonObject = new JSONObject(response.body());
                        if(jsonObject.getBoolean("d")==true){
                            Toast.makeText(SealApplyActivity.this,"用印申请临时保存成功！",Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Toast.makeText(SealApplyActivity.this,"用印申请临时保存失败！",Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException ex){
                        Log.e("InfoChildFragment",ex.getMessage());
                        Toast.makeText(SealApplyActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(SealApplyActivity.this,"用印申请临时保存失败！",Toast.LENGTH_SHORT).show();
                }

            }
            //请求失败时回调
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("保存请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(SealApplyActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //获取用印申请填写信息
    private RequestBody getSealApplicationContent(){
         DeptName = deptTv.getText().toString();
        if(!(DeptName!=null&&DeptName.length() != 0)){
            Toast.makeText(SealApplyActivity.this,"部门名称不能为空",Toast.LENGTH_LONG).show();
            return null;
        }
        Reason = reasonTv.getText().toString();
        if(!(Reason!=null&&Reason.length() != 0)){
            Toast.makeText(SealApplyActivity.this,"用印理由不能为空",Toast.LENGTH_LONG).show();
            return null;
        }
        ToDept = organizationTv.getText().toString();
        if(!(ToDept!=null&&ToDept.length() != 0)){
            Toast.makeText(SealApplyActivity.this,"发往部门不能为空",Toast.LENGTH_LONG).show();
            return null;
        }
        SealNum = sealNumTv.getText().toString();
        if(!(SealNum!=null&&SealNum.length() != 0)){
            Toast.makeText(SealApplyActivity.this,"用印数量不能为空",Toast.LENGTH_LONG).show();
            return null;
        }
        if(!(MainChiefID!=null&&MainChiefID.length() != 0)){
            Toast.makeText(SealApplyActivity.this,"审批人不能为空",Toast.LENGTH_LONG).show();
            return null;
        }
        if(!(MainChiefName!=null&&MainChiefName.length() != 0)){
            Toast.makeText(SealApplyActivity.this,"审批人不能为空",Toast.LENGTH_LONG).show();
            return null;
        }
        if(ChooseSealType==""||ChooseSealType=="0"){
            Toast.makeText(SealApplyActivity.this,"请选择印章类型",Toast.LENGTH_LONG).show();
            return null;
        }
        if(ChooseSealUseType==""||ChooseSealUseType=="0"){
            Toast.makeText(SealApplyActivity.this,"请选择用印类型",Toast.LENGTH_LONG).show();
            return null;
        }
        JSONObject requestJson = new JSONObject();
        try{
            JSONObject val1  = new JSONObject();
            if(sealApplyID!=null){
                val1.put("ID", sealApplyID);
            }else {
                val1.put("ID", RandomCode.getCode());
            }
            val1.put("SealApplication", ChooseSealUseType);
            val1.put("UsingTime", ChooseTime);
            val1.put("Content", Reason);
            val1.put("ToDept", ToDept);
            val1.put("SealType", ChooseSealType);
            val1.put("SealNum", SealNum);
            val1.put("DeptID", DeptID);
            val1.put("DeptName", DeptName);
            requestJson.put("sealInfo",val1);
            JSONArray user = initSubmitPerson();
            Log.i("审批人信息",user.toString());
            requestJson.put("user",user);


        }catch (JSONException ex){
            Log.i("getOperator","json对象构造错误");
        }
        RequestBody view = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        return view;
    }
    //生成submit的user对象
    private  JSONArray initSubmitPerson(){
        JSONArray user  = new JSONArray();
        try{
            if (ApprovalList.length()>1){
                for(int i=0;i<ApprovalList.length()-1;i++){
                    JSONObject val  = new JSONObject();
                    val.put("UserID", new MyApplication().getUserID());
                    val.put("UserName", new MyApplication().getUserName());
                    user.put(val);
                }
                if (ApprovalList.getJSONObject(ApprovalList.length()-1).getJSONArray("MainChiefList").length()>1){
                    JSONObject val  = new JSONObject();
                    val.put("UserID", MainChiefID);
                    val.put("UserName", MainChiefName);
                    user.put(val);
                }else{
                    JSONObject val  = new JSONObject();
                    val.put("UserID", MainChiefID);
                    val.put("UserName", MainChiefName);
                    user.put(val);
                }
            }else {
                if (ApprovalList.getJSONObject(0).getJSONArray("MainChiefList").length()>1){
                    JSONObject val  = new JSONObject();
                    val.put("UserID", MainChiefID);
                    val.put("UserName", MainChiefName);
                    user.put(val);
                }else{
                    JSONObject val  = new JSONObject();
                    val.put("UserID", MainChiefID);
                    val.put("UserName", MainChiefName);
                    user.put(val);
                }
            }
        }catch (JSONException ex){
            Log.i("initSubmitPerson","json对象构造错误");
        }
        return user;
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
                MainChiefName = assessor;
                try {
                    MainChiefID = temp.getJSONObject(i-1).getString("MainChiefID");
                }catch (JSONException ex){
                    Log.e("InfoChildFragment",ex.getMessage());
                    Toast.makeText(SealApplyActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
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


