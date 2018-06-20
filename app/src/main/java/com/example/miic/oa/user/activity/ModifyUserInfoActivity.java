package com.example.miic.oa.user.activity;

import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.miic.oa.common.Setting.isCellphone;

public class ModifyUserInfoActivity extends AppCompatActivity {

    private LinearLayout backBtn;
    private TextView UserNameEt;
    private TextView SexSp;
    private List<String> ListSex;
    private ArrayAdapter<String> SexAdapter;
    private TextView NationSp;
    private List<String> ListNation;
    private ArrayAdapter<String> NationAdapter;
    private TextView PortNameTv;
    private TextView PositionTv;
    private TextView DeptNameTv;
    private EditText RemarkEt;
    private EditText QQEt;
    private EditText WechatEt;
    private EditText MobileEt;
    private Button SubmitBtn;

    private String UserInfo;

    private SharedPreferences sf;
    private int Sex;
    private int Nation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user_info);
        backBtn = (LinearLayout)findViewById(R.id.back);
        backBtn.setOnClickListener(backClickListener);

        UserNameEt = (TextView)findViewById(R.id.user_name);
        SexSp = (TextView)findViewById(R.id.sex);
        NationSp = (TextView)findViewById(R.id.nation);
        PortNameTv = (TextView)findViewById(R.id.port_name);
        PositionTv = (TextView)findViewById(R.id.position);
        DeptNameTv = (TextView)findViewById(R.id.dept_name);
        RemarkEt = (EditText)findViewById(R.id.remark);
        QQEt = (EditText)findViewById(R.id.qq);
        WechatEt = (EditText)findViewById(R.id.wechat);
        MobileEt = (EditText)findViewById(R.id.mobile);
        SubmitBtn = (Button)findViewById(R.id.submit_btn);

        sf = getSharedPreferences("data",MODE_PRIVATE);

        //获取上一页面传递的参数
        Intent intent = getIntent();
        UserInfo = intent.getStringExtra("UserInfo");

        initView();
    }

    //初始化View
    private void initView(){
        getUserInfo();
        SubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitUserInfo();
            }
        });
    }

    //获取用户信息
    private void getUserInfo(){
        try{
            JSONObject jsonObject = new JSONObject(UserInfo);
            JSONObject jsonTemp = jsonObject.getJSONObject("d");
            String userName = jsonTemp.getString("UserName");
            String orgName = jsonTemp.getString("OrgName");
            String deptName = jsonTemp.getString("DeptName");
            String userPosition = jsonTemp.getString("Position");
            String userRemark = jsonTemp.getString("Remark");
            String userQQ = jsonTemp.getString("QQ");
            String userWechat = jsonTemp.getString("Wechat");
            String userMobile = jsonTemp.getString("Mobile");
            String userKey = jsonTemp.getString("UserKey");
            int nation = jsonTemp.getInt("Nation");
            int sex = jsonTemp.getInt("Sex");
            String portName = jsonTemp.getString("PortName");
            if(userName!=""){
                UserNameEt.setText(userName);
            }
            if(sex==0){
                SexSp.setText("男");
            }else if(sex==1){
                SexSp.setText("女");
            }
            initNation(nation);

            if (portName!=""){
                PortNameTv.setText(portName);
            }
            if (userPosition!=""){
                PositionTv.setText(userPosition);
            }
            if (deptName!=""){
                DeptNameTv.setText(deptName);
            }
            if(userRemark!=""){
                RemarkEt.setText(userRemark);
            }
            if(userMobile!=""){
                MobileEt.setText(userMobile);
            }
            if(userWechat!=""){
                WechatEt.setText(userWechat);
            }
            if(userQQ!=""){
                QQEt.setText(userQQ);
            }
        }catch (JSONException ex){
            Log.e("InfoChildFragment",ex.getMessage());
            Toast.makeText(ModifyUserInfoActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    //提交用户信息
    private void submitUserInfo(){
//        String userName = UserNameEt.getText().toString();
//        if(userName.trim()==""){
//            UserNameEt.setError("姓名不得为空");
//            return;
//        }
        String remark = RemarkEt.getText().toString();

        String qq = QQEt.getText().toString();
        String mobile = MobileEt.getText().toString();
        if(!isCellphone(mobile)){
            MobileEt.setError("手机号不正确！");
            return;
        }
        String wechat = WechatEt.getText().toString();

        JSONObject requestJson = new JSONObject();
        String userID=sf.getString("userID","");
        try{
            JSONObject val = new JSONObject();
            val.put("ID",userID);
//            val.put("UserName",userName);
//            val.put("Sex",Sex);
//            val.put("Nation",Nation+1);
            val.put("Remark",remark);
            val.put("QQ",qq);
            val.put("Mobile",mobile);
            val.put("Wechat",wechat);
            requestJson.put("userInfo",val);
        }catch (JSONException ex){
            Log.i("MyPageFrgment","json对象构造错误");
        }
        RequestBody userView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.SubmitUserInfos (userView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try {
                        System.out.println("body:" + response.body());
                        JSONObject jsonObject = new JSONObject(response.body());
                        Boolean isTrue = jsonObject.getBoolean("d");
                        if(isTrue==true){
                            Toast.makeText(ModifyUserInfoActivity.this,"用户信息保存成功",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(ModifyUserInfoActivity.this,"用户信息保存失败！请稍后再试！",Toast.LENGTH_SHORT).show();
                        }

                    }catch (JSONException ex){
                        Log.e("InfoChildFragment",ex.getMessage());
                        Toast.makeText(ModifyUserInfoActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ModifyUserInfoActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);

    }


    //初始化民族下拉列表
    private void initNation(final int nation){
        /*设置数据源*/
        ListNation=new ArrayList<String>();

        final List<String> ListNation = new ArrayList<String>();
        RequestBody searchView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), "");
        Call<String> call = PostRequest.Instance.request.GetNationInfos (searchView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try{
                    if(response.body()!=null){
                        //Log.i("Setting",response.body());
                        String jsonStrTemp =(new JSONObject(response.body())).getString("d");
                        JSONArray arrayTemp = new JSONArray(jsonStrTemp);
                        //for (int i=0;i<arrayTemp.length();i++){
                            JSONObject objectTemp = arrayTemp.getJSONObject(nation-1);
                            //ListNation.add(objectTemp.getString("Value"));
                            NationSp.setText(objectTemp.getString("Value"));
                        //}
                    }else{
                        Toast.makeText(ModifyUserInfoActivity.this,"获取民族失败",Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException ex){
                    Toast.makeText(ModifyUserInfoActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                Toast.makeText(ModifyUserInfoActivity.this,throwable.getMessage() + "------" + throwable.getCause(),Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //返回按钮绑定事件
    private View.OnClickListener backClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };
}
