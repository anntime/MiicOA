package com.example.miic.meetingRoomManage.activity;

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
import com.example.miic.base.multiLineRadioGroup.MultiLineRadioGroup;
import com.example.miic.common.MyApplication;
import com.example.miic.contractManage.activity.AddExecuteActivity;
import com.example.miic.contractManage.activity.ContractManageActivity;
import com.example.miic.meetingRoomManage.item.ApplyListItem;
import com.example.miic.meetingRoomManage.item.RoomStatus;
import com.example.miic.meetingRoomManage.item.SearchResultItem;
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

import static com.example.miic.oa.common.Setting.stampToTimes;

public class MeetingRoomApplyActivity extends AppCompatActivity {
    private Spinner MeetingRoomSp;
    private List<String> ListMeetingRoom;
    private List<String> ListMeetingRoomName;
    private ArrayAdapter<String> MeetingRoomAdapter;
    private String SelectMeetingRoom;

    private TimePickerView DateTpv;
    private TextView DateTv;
    private String SelectUseDate;
    private TimePickerView StartTimeTpv;
    private TextView StartTimeTv;
    private String SelectStartTime;
    private TimePickerView EndTimeTpv;
    private TextView EndTimeTv;
    private String SelectEndTime;
    private MultiLineRadioGroup UseTimeMr;
    private EditText TopicEt;
    private TextView DeptNameTv;
    private Spinner DeptNameSp;
    private List<String> ListDeptName;
    private ArrayAdapter<String> DeptNameAdapter;
    private String DeptName;
    private String DeptID;
    private JSONArray DeptList;
    private TextView UserNameTv;
    private Button SubmitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_room_apply);
        setHeader();
        initView();
        GetRoomList();
        getMyDeptInfoList();
    }

    private void setHeader() {
        TextView titleTv = (TextView) findViewById(R.id.page_title);
        LinearLayout backLin = (LinearLayout) findViewById(R.id.back);
        LinearLayout rightLin = (LinearLayout) findViewById(R.id.menu);
        titleTv.setText("会议室预约");
        rightLin.setVisibility(View.INVISIBLE);
        backLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initView() {
        MeetingRoomSp = (Spinner) findViewById(R.id.meeting_room);
        UseTimeMr = (MultiLineRadioGroup) findViewById(R.id.use_time);
        TopicEt = (EditText) findViewById(R.id.topic);
        DeptNameTv = (TextView) findViewById(R.id.dept_name);
        DeptNameSp = (Spinner)findViewById(R.id.sp_dept_name);
        DeptNameSp.setVisibility(View.GONE);
        UserNameTv = (TextView) findViewById(R.id.user_name);
        SubmitBtn = (Button) findViewById(R.id.submit_button);
        initMeetingRoom();
        initUseDate();
        initStartTime();
        initEndTime();
        initDeptNameSpinner();
//        initUseTime();
        DeptNameTv.setText(MyApplication.getDeptName());
        UserNameTv.setText(MyApplication.getUserName());
        SubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubmitMeetingRoomApply();
            }
        });
    }
    private void initMeetingRoom(){
        /*设置数据源*/
        ListMeetingRoom=new ArrayList<String>();
        ListMeetingRoom.add("请选择");

        /*新建适配器*/
        MeetingRoomAdapter=new ArrayAdapter<String>(this,R.layout.spinner_display_style,ListMeetingRoom);
        /*adapter设置一个下拉列表样式，参数为系统子布局*/
        MeetingRoomAdapter.setDropDownViewResource(R.layout.spinner_dropdown_style);

        /*spDown加载适配器*/
        MeetingRoomSp.setAdapter(MeetingRoomAdapter);

        /*soDown的监听器*/
        MeetingRoomSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String meetingRoom=MeetingRoomAdapter.getItem(i);   //获取选中的那一项
                SelectMeetingRoom = meetingRoom;
                GetMeetingTimeByDate();
                Log.i("选择的会议室：",meetingRoom);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //设置默认值;
        MeetingRoomSp.setSelection(0);
    }
    private void initUseDate(){
        DateTv = (TextView) findViewById(R.id.date);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2030,1,1);
        DateTpv = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                DateTv.setText(getTime(date).split(" ")[0]);
                SelectUseDate = getTime(date).split(" ")[0];
                Log.i("pvTime", "onTimeSelect");
                GetMeetingTimeByDate();
            }
        }).setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
            @Override
            public void onTimeSelectChanged(Date date) {
                Log.i("pvTime", "onTimeSelectChanged");
            }
        })
                .setType(new boolean[]{true, true, true, false, false, false})
                .setRangDate(Calendar.getInstance(),endDate)//起始终止年月日设定
                .build();
        Calendar cal = Calendar.getInstance();
        String date = cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DAY_OF_MONTH);
        SelectUseDate =date;
        DateTv.setText(date);
        DateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateTpv.show(view);//弹出时间选择器，传递参数过去，回调的时候则可以绑定此view
            }
        });
    }
    private void initStartTime(){
        StartTimeTv = (TextView) findViewById(R.id.start_time);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2030,1,1);
        StartTimeTpv = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                StartTimeTv.setText(getTime(date).split(" ")[1]);
                SelectStartTime = getTime(date).split(" ")[1];
                Log.i("pvTime", "onTimeSelect");
            }
        }).setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
            @Override
            public void onTimeSelectChanged(Date date) {
                StartTimeTv.setText(getTime(date).split(" ")[0]);
                Log.i("pvTime", "onTimeSelectChanged");
            }
        })
                .setType(new boolean[]{false, false, false,true, true, false})
                .build();
        Calendar cal = Calendar.getInstance();
        String date = (cal.get(Calendar.HOUR_OF_DAY)<10?("0"+cal.get(Calendar.HOUR_OF_DAY)):cal.get(Calendar.HOUR_OF_DAY))+":"
                +(cal.get(Calendar.MINUTE)<10?("0"+cal.get(Calendar.MINUTE)):cal.get(Calendar.MINUTE));
        SelectStartTime =date;
        StartTimeTv.setText(date);
        StartTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartTimeTpv.show(view);//弹出时间选择器，传递参数过去，回调的时候则可以绑定此view
            }
        });
    }
    private void initEndTime(){
        EndTimeTv = (TextView) findViewById(R.id.end_time);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2030,1,1);
        EndTimeTpv = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                EndTimeTv.setText(getTime(date).split(" ")[1]);
                SelectEndTime = getTime(date).split(" ")[1];
                Log.i("pvTime", getTime(date));
                GetMeetingTimeByDate();
            }
        }).setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
            @Override
            public void onTimeSelectChanged(Date date) {
                EndTimeTv.setText(getTime(date).split(" ")[1]);
                Log.i("pvTime", "onTimeSelectChanged");
            }
        })
                .setType(new boolean[]{false, false, false,true, true, false})
                .build();
        Calendar cal = Calendar.getInstance();
        String date = (cal.get(Calendar.HOUR_OF_DAY)<10?("0"+cal.get(Calendar.HOUR_OF_DAY)):cal.get(Calendar.HOUR_OF_DAY))+":"
                +(cal.get(Calendar.MINUTE)<10?("0"+cal.get(Calendar.MINUTE)):cal.get(Calendar.MINUTE));
        SelectEndTime =date;
        EndTimeTv.setText(date);
        EndTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EndTimeTpv.show(view);//弹出时间选择器，传递参数过去，回调的时候则可以绑定此view
            }
        });
    }
    private void initUseTime(){
        UseTimeMr.append("08:00 ~ 10:30");
        UseTimeMr.append("10:30 ~ 11:00");
        UseTimeMr.append("13:30 ~ 14:00");
        UseTimeMr.append("08:00 ~ 10:30");
        UseTimeMr.append("10:30 ~ 11:00");
        UseTimeMr.append("13:30 ~ 14:00");
    }
    private String getTime(Date date) {//可根据需要自行截取数据显示
        Log.d("getTime()", "choice date millis: " + date.getTime());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    }
    private void SubmitMeetingRoomApply(){
        //会议室，日期，开始，结束，主题，预订部门，预订人
        JSONObject search = new JSONObject();
        try{
            JSONObject val = new JSONObject();
            val.put("ID", RandomCode.getCode());
            for(int i=0;i<ListMeetingRoom.size();i++){
                if(ListMeetingRoom.get(i).equals(SelectMeetingRoom)){
                    val.put("RoomID",ListMeetingRoomName.get(i-1));
                }
            }
            val.put("RoomName", SelectMeetingRoom);
            val.put("MeetingTheme",TopicEt.getText().toString());
            val.put("ReverseBeginTime", SelectUseDate+" "+SelectStartTime);
            val.put("ReverseEndTime", SelectUseDate+" "+SelectEndTime);
            val.put("ReverseDate", SelectUseDate);
//            val.put("ReverseTimeLength", "");
            val.put("DeptID", DeptID);
            val.put("DeptName", DeptName);
            search.put("meetingInfo",val);

        }catch (JSONException ex){
            Log.i("SubmitMeetingRoomApply",ex.getMessage());
        }
        RequestBody searchView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),search.toString());
        Call<String> call = PostRequest.Instance.request.SubmitMeetingRoomApply(searchView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        Log.i("会议室预约列表：",jsonObject.toString());
                        Boolean result = jsonObject.getBoolean("d");
                        if(result==true){
                            Toast.makeText(MeetingRoomApplyActivity.this, "会议室预约成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MeetingRoomApplyActivity.this, MeetingRoomManageActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(MeetingRoomApplyActivity.this, "会议室预约失败，请稍后再试~", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MeetingRoomApplyActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);

    }
    //日期选择结束后查询时段占用情况
    private void GetMeetingTimeByDate(){
        //读取选择的会议室与日期,二者不可为空
        if(SelectMeetingRoom.equals("")||SelectMeetingRoom.equals("请选择")){
            Toast.makeText(this, "请选择会议室~", Toast.LENGTH_SHORT).show();
            return;
        }
        if (SelectUseDate.equals("")){
            Toast.makeText(this, "请选择预定日期~", Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject search = new JSONObject();
        try{
            search.put("date",SelectUseDate);
            for(int index=0;index<ListMeetingRoom.size();index++){
                String item = ListMeetingRoom.get(index);
                if(item.equals(SelectMeetingRoom)){
                    search.put("roomID",ListMeetingRoomName.get(index-1));
                }
            }
        }catch (JSONException ex){
            Log.i("GetMeetingTimeByDate",ex.getMessage());
        }
        RequestBody searchView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),search.toString());
        Call<String> call = PostRequest.Instance.request.GetMeetingTimeByDate(searchView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        Log.i("会议室预约列表：",jsonObject.toString());
                        String dataStr = jsonObject.getString("d");
                        for (int i=0;i<UseTimeMr.getChildCount();i++){
                            UseTimeMr.remove(i);
                        }

                        if(!dataStr.equals("[]")){
                            JSONArray dataArr = new JSONArray(dataStr);
                            for (int index = 0;index<dataArr.length();index++){
                                JSONObject item = dataArr.getJSONObject(index);
                                String startTime = stampToTimes(item.getString("ReverseBeginTime"));
                                String endTime = stampToTimes(item.getString("ReverseEndTime"));
                                UseTimeMr.append(startTime+"~"+endTime);
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
                Toast.makeText(MeetingRoomApplyActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);

    }
    //获取会议室列表
    private void GetRoomList(){
        RequestBody searchView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),"{roomStatus:"+ RoomStatus.CanUse.getIndex()+"}");
        Call<String> call = PostRequest.Instance.request.GetRoomList(searchView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        Log.i("会议室预约列表：",jsonObject.toString());
                        JSONArray dataArr = jsonObject.getJSONArray("d");
                        ListMeetingRoomName = new  ArrayList<String>();
                        if(dataArr!=null){
                            for (int index = 0;index<dataArr.length();index++){
                                JSONObject item = dataArr.getJSONObject(index);
                                ListMeetingRoom.add(item.getString("Value"));
                                ListMeetingRoomName.add(item.getString("Name"));
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
                Toast.makeText(MeetingRoomApplyActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //初始化部门列表
    private void initDeptNameSpinner(){
        /*设置数据源*/
        ListDeptName=new ArrayList<String>();
        ListDeptName.add("其他部门");
        /*新建适配器*/
        DeptNameAdapter=new ArrayAdapter<String>(this,R.layout.spinner_display_style,ListDeptName);
        /*adapter设置一个下拉列表样式，参数为系统子布局*/
        DeptNameAdapter.setDropDownViewResource(R.layout.spinner_dropdown_style);

        /*spDown加载适配器*/
        DeptNameSp.setAdapter(DeptNameAdapter);

        /*soDown的监听器*/
        DeptNameSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String deptName=DeptNameAdapter.getItem(i);   //获取选中的那一项
                try{
                    for (int index=0;index<DeptList.length();index++){
                        JSONObject jsonObject = DeptList.getJSONObject(index);
                        if(deptName.equals(jsonObject.getString("DeptName"))){
                            DeptName = jsonObject.getString("DeptName");
                            DeptID = jsonObject.getString("DeptID");
                        }
                    }
                }catch (JSONException ex){
                    Toast.makeText(MeetingRoomApplyActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //设置默认值;
        DeptNameSp.setSelection(0);
    }
    //获取预订部门
    public void getMyDeptInfoList(){
        JSONObject obj = new JSONObject();
        try {
            obj.put("isApprove", true);
            obj.put("withMiic", true);
        }catch (JSONException ex){
            Log.i("getMyDeptInfoList",ex.getMessage());
        }
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
                                DeptNameTv.setText(jsonObjTem.getString("DeptName"));
                                DeptID = jsonObjTem.getString("DeptID");
                                DeptName = jsonObjTem.getString("DeptName");
                                DeptNameSp.setVisibility(View.GONE);
                            }else if(jsonArr.length() > 1){
                                DeptNameTv.setVisibility(View.GONE);
                                DeptNameSp.setVisibility(View.VISIBLE);
                                Log.i("GetMyDeptResponse",jsonArr.toString());
                                DeptList = jsonArr;
                                for(int i=0;i<jsonArr.length();i++){
                                    JSONObject jsonObjTem =  jsonArr.getJSONObject(i) ;
                                    ListDeptName.add(jsonObjTem.getString("DeptName"));
                                }
                            }else{
                                DeptNameTv.setText("其他部门");
                                DeptNameSp.setVisibility(View.GONE);
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
                Toast.makeText(MeetingRoomApplyActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call2, callback2);
    }
}
