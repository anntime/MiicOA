package com.example.miic.carManage.activity;

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
import com.example.miic.carManage.common.CarCommon;
import com.example.miic.common.MyApplication;

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

public class CarApplyActivity extends AppCompatActivity {
    private LinearLayout backBtn;

    private Spinner CarNumSp;
    private List<String> ListCarNum;
    private List<String> ListAssessor;
    private ArrayAdapter<String>  AssessorAdapter;
    private ArrayAdapter<String> CarNumAdapter;

    private Spinner IsWorkTimeSp;
    private List<String> ListIsWorkTime;
    private ArrayAdapter<String> IsWorkTimeAdapter;

    private TextView deptTv;
    private Spinner deptNameSp;
    private List<String> ListDeptName;
    private ArrayAdapter<String> DeptNameAdapter;
    private JSONArray DeptList;
    private TimePickerView beginTimePv;
    private TimePickerView endTimePv;
    private TextView beginTime;
    private TextView endTime;

    private TextView createrNameTv;

    private TextView assessorTv;
    private Spinner assessorSp;

    private EditText travelWay;

    private Button submitBtn;

    private String ChooseCarNumID = "";
    private String ChooseCarNum = "";
    private String CreaterName = "";
    private String CreaterId = "";
    private String TravelWay = "";
    private String ChooseIsWorkTime = "";
    private String ChooseBeginTime = "";
    private String ChooseEndTime = "";
    private String ChooseAssessor = "";
    private String DeptID;
    private String DeptName;
    private String carApplyID;
    private String MainChiefName;
    private String MainChiefID;
    private JSONArray ApprovalList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_apply);
        backBtn = (LinearLayout)findViewById(R.id.search_back);
        DeptID = MyApplication.getDeptID();
        DeptName = MyApplication.getDeptName();
        init();
    }
    private void init(){
        deptTv = (TextView)findViewById(R.id.dept_name);
        deptNameSp = (Spinner)findViewById(R.id.dept_name_sp);
        createrNameTv = (TextView)findViewById(R.id.createrName);
        assessorTv = (TextView)findViewById(R.id.assessor);
        assessorSp = (Spinner)findViewById(R.id.sp_assessor);
        assessorSp.setVisibility(View.GONE);
        travelWay = (EditText)findViewById(R.id.travelWay);
        submitBtn = (Button)findViewById(R.id.submit_btn);

        DeptName = deptTv.getText().toString();
        TravelWay = travelWay.getText().toString();
        CreaterId = MyApplication.getUserID();
        CreaterName = MyApplication.getUserName();
        initDeptNameSpinner();

        //返回按钮事件
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //车牌号
        initCarNum();
        //工作时间
        initIsWorkTime();
        //开始时间
        initBeginTime();
        //结束时间
        initEndTime();
        Intent intent = getIntent();
        carApplyID = intent.getStringExtra("carApplyID");
        if(carApplyID!=null){
            getCarApply(carApplyID);
        }else{
            getCreaterNameInfo(MyApplication.getDeptID());
        }
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitCarApplication();
            }
        });

    }
    //初始化部门列表
    private void initDeptNameSpinner(){
        /*设置数据源*/
        ListDeptName=new ArrayList<String>();
        ListDeptName.add("请选择");

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
                DeptName = DeptNameAdapter.getItem(i);   //获取选中的那一项
                try{
                    for (int index=0;index<DeptList.length();index++){
                        JSONObject jsonObject = DeptList.getJSONObject(index);
                        if(DeptName.equals(jsonObject.getString("DeptName"))){
                            DeptID = jsonObject.getString("DeptID");
                        }
                    }
                }catch (JSONException ex){
                    Toast.makeText(CarApplyActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

                Log.i("您选择的部门是",DeptName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //设置默认值;
        deptNameSp.setSelection(0);
    }
    //初始化车牌号下拉列表
    private void initCarNum(){
        CarNumSp = (Spinner)findViewById(R.id.car_num);
        /*设置数据源*/
        ListCarNum=new ArrayList<String>();
        ListCarNum.add("请选择");
        ListCarNum.add("京J55283");
        ListCarNum.add("京N70BB6");
        ListCarNum.add("京N360F8");
        ListCarNum.add("京HK8995");
        /*新建适配器*/
        CarNumAdapter=new ArrayAdapter<String>(this,R.layout.spinner_display_style,ListCarNum);
        /*adapter设置一个下拉列表样式，参数为系统子布局*/
        CarNumAdapter.setDropDownViewResource(R.layout.spinner_dropdown_style);

        /*spDown加载适配器*/
        CarNumSp.setAdapter(CarNumAdapter);

        /*soDown的监听器*/
        CarNumSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String carNum=CarNumAdapter.getItem(i);   //获取选中的那一项
                ChooseCarNumID = new CarCommon().getCarNum(carNum);
                ChooseCarNum = carNum;
                Log.i("您选择的车牌号是",carNum);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //设置默认值;
        CarNumSp.setSelection(0);

    }
    //初始化是否工作时间
    private void initIsWorkTime(){
        IsWorkTimeSp = (Spinner)findViewById(R.id.isWorkingTime);
        /*设置数据源*/
        ListIsWorkTime=new ArrayList<String>();
        ListIsWorkTime.add("是");
        ListIsWorkTime.add("否");
        /*新建适配器*/
        IsWorkTimeAdapter=new ArrayAdapter<String>(this,R.layout.spinner_display_style,ListIsWorkTime);
        /*adapter设置一个下拉列表样式，参数为系统子布局*/
        IsWorkTimeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_style);

        /*spDown加载适配器*/
        IsWorkTimeSp.setAdapter(IsWorkTimeAdapter);

        /*soDown的监听器*/
        IsWorkTimeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String isWorkTime=IsWorkTimeAdapter.getItem(i);   //获取选中的那一项
                ChooseIsWorkTime = new CarCommon().getIsWorkTime(isWorkTime);
                Log.i("您选择的是否工作时间是",isWorkTime);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //设置默认值;
        IsWorkTimeSp.setSelection(0);

    }

    //开始时间
    private void initBeginTime(){
        initTimePickerBegin();
        beginTime = (TextView) findViewById(R.id.beginTime);
        Calendar cal = Calendar.getInstance();
        String date = cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DAY_OF_MONTH);
        ChooseBeginTime =date;
        beginTime.setText(date);
        beginTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beginTimePv.show(view);//弹出时间选择器，传递参数过去，回调的时候则可以绑定此view
            }
        });
    }
    //结束时间
    private void initEndTime(){
        initTimePickerEnd();
        endTime = (TextView) findViewById(R.id.endTime);
        Calendar cal = Calendar.getInstance();
        String date = cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DAY_OF_MONTH);
        ChooseEndTime =date;
        endTime.setText(date);
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTimePv.show(view);//弹出时间选择器，传递参数过去，回调的时候则可以绑定此view
            }
        });
    }
    private void initTimePickerBegin() {
        Calendar endDate = Calendar.getInstance();
        endDate.set(2030,1,1);
        beginTimePv = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                Toast.makeText(CarApplyActivity.this, getTime(date).split(" ")[0], Toast.LENGTH_SHORT).show();
                beginTime.setText(getTime(date).split(" ")[0]);
                ChooseBeginTime = getTime(date).split(" ")[0];
                Log.i("beginTimePv", "onTimeSelect");


            }
        }).setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
            @Override
            public void onTimeSelectChanged(Date date) {
//                        beginTime.setText(getTime(date).split(" ")[0]);
                Log.i("beginTimePv", "onTimeSelectChanged");
            }
        })

                .setType(new boolean[]{true, true, true, false, false, false})
                .setRangDate(Calendar.getInstance(),endDate)//起始终止年月日设定
                .build();
    }

    private void initTimePickerEnd() {
        Calendar endDate = Calendar.getInstance();
        endDate.set(2030,1,1);
        endTimePv = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                Toast.makeText(CarApplyActivity.this, getTime(date).split(" ")[0], Toast.LENGTH_SHORT).show();
                endTime.setText(getTime(date).split(" ")[0]);
                ChooseEndTime = getTime(date).split(" ")[0];
                Log.i("endTimePv", "onTimeSelect");


            }
        }).setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
            @Override
            public void onTimeSelectChanged(Date date) {
//                        endTime.setText(getTime(date).split(" ")[0]);
                Log.i("endTimePv", "onTimeSelectChanged");
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
    //获取用车申请部门
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
                        Log.i("GetLoginInfo",new CarCommon().getYesOrNo("No"));

                        String manage = jsonObj.getString("Manage")+"";
                        Log.i("GetLoginInfo",manage);
                        Log.i("GetLoginInfo",(manage.equals(new CarCommon().getYesOrNo("No"))+"1"));
                        if(manage.equals(new CarCommon().getYesOrNo("No"))){
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
                                                        ListDeptName.add(jsonObjTem.getString("DeptName"));
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
                                    Toast.makeText(CarApplyActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(CarApplyActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);

    }

    //获取用印经办人信息
    private  void getCreaterNameInfo(String parameter){

        JSONObject requestJson = new JSONObject();
        try{
            requestJson.put("id",parameter);
        }catch (JSONException ex){
            Log.i("getCreaterName","json对象构造错误");
        }
        RequestBody userView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        //GetSealOperatorInfo()方法获取经办人信息
        Call<String> call = PostRequest.Instance.request.GetSealOperatorInfo(userView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    if(response.body()!=null&&!(new JSONObject(response.body()).isNull("d"))){
                        System.out.println("body:" + response.body());
                        JSONObject jsonObject = new JSONObject(response.body()).getJSONObject("d");
                        createrNameTv.setText(MyApplication.getUserName());
                        //获取部门信息
                        getMyDeptInfoList();
                        getChiefInfo();
                    }else{
                        Toast.makeText(CarApplyActivity.this,"获取用户信息失败！",Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException ex){
                    Log.e("InfoChildFragment",ex.getMessage());
                    Toast.makeText(CarApplyActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }
            //请求失败时回调
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(CarApplyActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);

    }

    //获取用车申请信息
    private void getCarApply(String parameter){

        JSONObject requestJson = new JSONObject();
        try{
            requestJson.put("id",parameter);
        }catch (JSONException ex){
            Log.i("getApplyID","json对象构造错误");
        }
        RequestBody userView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.GetCarApply(userView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    if(response.body()!=null&&!(new JSONObject(response.body()).isNull("d"))){
                        System.out.println("body:" + response.body());
                        JSONObject jsonObject = new JSONObject(response.body()).getJSONObject("d");
                        CarNumSp.setSelection(parseInt(jsonObject.getString("CarNumName")));
                        createrNameTv.setText(MyApplication.getUserName());
                        travelWay.setText(jsonObject.getString("TravelWay"));
                        IsWorkTimeSp.setSelection(parseInt(jsonObject.getString("IsWorkTime")));
                        beginTime.setText(stampToDate(jsonObject.getString("BeginTime")));
                        endTime.setText(stampToDate(jsonObject.getString("EndTime")));
                        getChiefInfo();
                    }else{
                        Toast.makeText(CarApplyActivity.this,"获取用户信息失败！",Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException ex){
                    Log.e("InfoChildFragment",ex.getMessage());
                    Toast.makeText(CarApplyActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                }



            }
            //请求失败时回调
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(CarApplyActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
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
        Call<String> call = PostRequest.Instance.request.CarChooseProjKV (view);
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
                                Toast.makeText(CarApplyActivity.this, "网络错误，请稍后再试！", Toast.LENGTH_SHORT).show();
                            }
                        };
                        PostRequest.Instance.CommonAsynPost(call2, callback2);
                    }catch (JSONException ex){
                        Log.e("InfoChildFragment",ex.getMessage());
                        Toast.makeText(CarApplyActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(CarApplyActivity.this,"获取用户信息失败！",Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(CarApplyActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }

    //提交用车申请
    private void submitCarApplication(){
        RequestBody view = getCarApplicationContent();
        if(view!=null) {
            Call<String> call = PostRequest.Instance.request.SubmitCarApplication(view);
            Callback<String> callback = new Callback<String>() {
                //请求成功时回调
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.body() != null) {
                        try {
                            System.out.println("body:" + response.body());
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.getBoolean("d") == true) {
                                Toast.makeText(CarApplyActivity.this, "用车申请成功！", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(CarApplyActivity.this, "用车申请失败！", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException ex) {
                            Log.e("InfoChildFragment", ex.getMessage());
                            Toast.makeText(CarApplyActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(CarApplyActivity.this, "获取用户信息失败！", Toast.LENGTH_SHORT).show();
                    }

                }

                //请求失败时回调
                public void onFailure(Call<String> call, Throwable throwable) {
                    System.out.println("请求失败" + call.request());
                    System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                    Toast.makeText(CarApplyActivity.this, "网络错误，请稍后再试！", Toast.LENGTH_SHORT).show();
                }
            };
            PostRequest.Instance.CommonAsynPost(call, callback);
        }

    }

    //获取用车申请填写信息
    private RequestBody getCarApplicationContent(){
        DeptName = deptTv.getText().toString();
        if(!(DeptName!=null&&DeptName.length() != 0)){
            Toast.makeText(CarApplyActivity.this,"部门名称不能为空",Toast.LENGTH_LONG).show();
            return null;
        }

        if(!(ChooseCarNumID!=null&&ChooseCarNumID.length() != 0)){
            Toast.makeText(CarApplyActivity.this,"请选择车辆",Toast.LENGTH_LONG).show();
            return null;
        }

        TravelWay = travelWay.getText().toString();
        if(!(TravelWay!=null&&TravelWay.length() != 0)){
            Toast.makeText(CarApplyActivity.this,"行车路线不能为空",Toast.LENGTH_LONG).show();
            return null;
        }

        if(!(MainChiefID!=null&&MainChiefID.length() != 0)){
            Toast.makeText(CarApplyActivity.this,"审批人不能为空",Toast.LENGTH_LONG).show();
            return null;
        }
        if(!(MainChiefName!=null&&MainChiefName.length() != 0)){
            Toast.makeText(CarApplyActivity.this,"审批人不能为空",Toast.LENGTH_LONG).show();
            return null;
        }
        JSONObject requestJson = new JSONObject();
        try{
            JSONObject val1  = new JSONObject();
            if(carApplyID!=null){
                val1.put("ID", carApplyID);
            }else {
                val1.put("ID", RandomCode.getCode());
            }

            val1.put("CarNumName", ChooseCarNum);
            val1.put("CarNumID", ChooseCarNumID);
            val1.put("CreaterID", CreaterId);
            val1.put("CreaterName", CreaterName);
            val1.put("IsWorkingTime", ChooseIsWorkTime);
            val1.put("TravelWay", TravelWay);
            val1.put("BeginTime", ChooseBeginTime);
            val1.put("EndTime", ChooseEndTime);
            val1.put("DeptID",DeptID);
            val1.put("DeptName",DeptName);
            requestJson.put("carInfo",val1);
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
                    MainChiefID = temp.getJSONObject(i-1).getString("MainChiefID");
                }catch (JSONException ex){
                    Log.e("InfoChildFragment",ex.getMessage());
                    Toast.makeText(CarApplyActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
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
