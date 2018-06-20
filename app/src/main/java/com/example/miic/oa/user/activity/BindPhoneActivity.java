package com.example.miic.oa.user.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.base.http.PostRequest;
import com.example.miic.oa.common.countDownTimer.CountDownTimerUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.miic.oa.common.Setting.isCellphone;

public class BindPhoneActivity extends AppCompatActivity {
    private LinearLayout backBtn;
    private EditText phoneNumView;
    private EditText identifyingCodeView;
    private Button identifyingPhoneView;
    private TextView sendidentifyingCodeView;
    private long millisUntilFinished=90000;

    private LinearLayout firstLayout;
    private LinearLayout thridLayout;
    private LinearLayout isBindLayout;
    private String phoneNum;
    private TextView BindTextView;
    private Button unBind;

    private String UserInfo;
    private SharedPreferences sf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone);

        firstLayout = (LinearLayout)findViewById(R.id.firstStepLinearLayout);
        thridLayout = (LinearLayout)findViewById(R.id.thirdStepLinearLayout);
        isBindLayout = (LinearLayout)findViewById(R.id.isBind);
        BindTextView = (TextView)findViewById(R.id.bind_text);
        unBind = (Button)findViewById(R.id.unBind);

        //返回按钮
        backBtn = (LinearLayout)findViewById(R.id.back);
        backBtn.setOnClickListener(backClickListener);
        //验证手机
        phoneNumView = (EditText)findViewById(R.id.phoneNum);
        identifyingCodeView = (EditText)findViewById(R.id.identifyingCode);
        identifyingPhoneView = (Button)findViewById(R.id.identifyingPhone);
        sendidentifyingCodeView = (TextView)findViewById(R.id.sendIdentifyingCode);

       phoneNum = phoneNumView.getText().toString().trim();
        //发送验证码
        sendidentifyingCodeView.setOnClickListener(sendIdentifyingCodeListener);
        sf = BindPhoneActivity.this.getSharedPreferences("data",BindPhoneActivity.MODE_PRIVATE);

        init();

        unBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateBind("2");
                isBindLayout.setVisibility(View.GONE);
                firstLayout.setVisibility(View.GONE);
                thridLayout.setVisibility(View.VISIBLE);

            }
        });
    }
    private void init(){
        JSONObject requestJson = new JSONObject();
        String userID=sf.getString("userID","");
        try{
            requestJson.put("userID",userID);
        }catch (JSONException ex){
            Log.i("BindPhoneActivity","json对象构造错误");
        }
        RequestBody userView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.GetUserInfo (userView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try {
                        System.out.println("body:" + response.body());
                        JSONObject jsonObject = new JSONObject(response.body());
                        JSONObject userInfo = jsonObject.getJSONObject("d");
                        Log.i("BindPhone",userInfo.toString()+"");
                        int isBind = userInfo.getInt("MobileBind");
                        String mobile = userInfo.getString("Mobile");
                        Log.i("BindPhone",isBind+"");
                        Log.i("BindPhone",mobile+"");
                        if(isBind==1){
                            isBindLayout.setVisibility(View.VISIBLE);
                            BindTextView.setText("您的手机号："+mobile.substring(0,3)+"****"+mobile.substring(7,11)+" 若已丢失或已停用，请立即更换，避免账户被盗!");
                        }else{
                            isBindLayout.setVisibility(View.GONE);
                            firstLayout.setVisibility(View.VISIBLE);
                            phoneNumView.setText(mobile);
                            phoneNumView.setFocusableInTouchMode(false);
                            phoneNumView.setFocusable(false);
                        }
                    }catch (JSONException ex){
                        Log.e("InfoChildFragment",ex.getMessage());
                        Toast.makeText(BindPhoneActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(BindPhoneActivity.this,"获取用户信息失败！",Toast.LENGTH_SHORT).show();
                }

            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(BindPhoneActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);

    }


    /**
     * 发送验证码按钮的监听事件
     */
    private View.OnClickListener sendIdentifyingCodeListener =new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i("BindPhoneActivity","发送手机验证码");
            //发送手机验证码
            String phoneNum = phoneNumView.getText().toString().trim();
            if(phoneNum.trim()!=""&& isCellphone(phoneNum) ){
                //发送手机验证码
                SendIdentifyToMobile(phoneNum);
                //设置验证码有效期倒计时
                CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(sendidentifyingCodeView, millisUntilFinished, 1000);
                mCountDownTimerUtils.start();
                //设置验证手机按钮的背景色并添加监听事件。
                //验证手机(即下一页)
                identifyingPhoneView.setOnClickListener(bindPhoneListener);
                identifyingPhoneView.setBackgroundResource(R.drawable.btn_shape_background);  //还原背景色
            }else{
                Toast.makeText(BindPhoneActivity.this,"请输入手机号！",Toast.LENGTH_SHORT).show();
            }

        }
    };
    /**
     * 验证手机按钮监听事件
     */
    private View.OnClickListener bindPhoneListener = new View.OnClickListener(){
        public void onClick(View view){
            String identifyingCode = identifyingCodeView.getText().toString().trim();
            String phoneNum = phoneNumView.getText().toString().trim();
            Log.i("BindPhoneActivity","验证手机按钮触发");
            Log.i("BindPhoneActivity",phoneNum);
            if (phoneNum.isEmpty()){
                phoneNumView.setError(getString(R.string.error_phone_num));
                phoneNumView.requestFocus();
            }else if(!isCellphone(phoneNum) ){
                phoneNumView.setError(getString(R.string.error_phone_num));
                phoneNumView.requestFocus();
            }else if(identifyingCode.isEmpty()){
                identifyingCodeView.setError(getString(R.string.error_identifying_code));
                identifyingCodeView.requestFocus();
            }else{
                JSONObject requestJson = new JSONObject();
                try{
                    requestJson.put("code",identifyingCode);
                    requestJson.put("mobile",phoneNum);
                }catch (JSONException ex){
                    Log.i("BindPhoneActivity","json对象构造错误");
                }
                //绑定手机
                BindPhoneAction(requestJson);
            }
        }
    };

    /**
     * 绑定手机
     */
    public void BindPhoneAction(JSONObject requestJson){
        RequestBody MobileInfoView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.CheckIdentifyCodeForBind(MobileInfoView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                // 请求处理,输出结果
                System.out.println("请求是：" + call.request());
                System.out.println("header是：" + response.headers());
                System.out.println("body是：" + response.body());
                if(response.body()!=null){
                    try {
                        //将字符串转换成jsonObject对象
                        JSONObject result =new JSONObject(new JSONObject(response.body()).getString("d")) ;
                        //获取对应的值
                        boolean results = result.getBoolean("result");
                        String errorMessage = result.getString("message");
                        if(results==false){
                            Log.e("BindPhoneActivity",errorMessage);
                            Toast.makeText(BindPhoneActivity.this,errorMessage,Toast.LENGTH_SHORT).show();

                        }else{
                            UpdateBind("1");
                        }
                    }catch (JSONException ex){
                        Log.e("BindPhoneActivity",ex.getMessage());
                        Toast.makeText(BindPhoneActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(BindPhoneActivity.this,"绑定手机失败，请稍后再试！",Toast.LENGTH_SHORT).show();
                }

            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(BindPhoneActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
                //回到登录页面
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    /**
     * 更新手机绑定状态
     */
    public void UpdateBind(String mobileBind){
//        {userInfo:{"ID":"c98a5e43-7d06-1d60-b3a7-aada86ac317a","MobileBind":"1"}}
        JSONObject requestJson = new JSONObject();
        String userID=sf.getString("userID","");
        try{
            JSONObject val = new JSONObject();
            val.put("ID",userID);
            val.put("MobileBind",mobileBind);
            requestJson.put("userInfo",val);
        }catch (JSONException ex){
            Log.i("BindPhoneActivity","json对象构造错误");
        }
        RequestBody userView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());

        Call<String> call = PostRequest.Instance.request.UpdateBind(userView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                // 请求处理,输出结果
                System.out.println("请求是：" + call.request());
                System.out.println("header是：" + response.headers());
                System.out.println("body是：" + response.body());
                if(response.body()!=null){
                    //{"d":{"__type":"Miic.BaseStruct.MiicBooleanWithMessage","result":true,"ErrorMessage":""}}
                    try {
                        //将字符串转换成jsonObject对象
                        boolean result = new JSONObject(response.body()).getBoolean("d");
                        if(result==true){
                            //绑定成功
                            firstLayout.setVisibility(View.GONE);
                            thridLayout.setVisibility(View.VISIBLE);
                            Toast.makeText(BindPhoneActivity.this,"操作成功！",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(BindPhoneActivity.this,"绑定失败！",Toast.LENGTH_SHORT).show();
                        }

                    }catch (JSONException ex){
                        Log.e("faindPasswordActivity",ex.getMessage());
                        Toast.makeText(BindPhoneActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(BindPhoneActivity.this,"验证码发送失败！请稍后再试!",Toast.LENGTH_SHORT).show();
                }

            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(BindPhoneActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }

    /**
     * 发送手机验证码（http请求）
     */
    public void SendIdentifyToMobile(String mobile){
        RequestBody MobileResetUserInfoView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), "{'mobile':'"+mobile+"'}");

        Call<String> call = PostRequest.Instance.request.SendIdentifyToMobileForBind(MobileResetUserInfoView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                // 请求处理,输出结果
                System.out.println("请求是：" + call.request());
                System.out.println("header是：" + response.headers());
                System.out.println("body是：" + response.body());
                if(response.body()!=null){
                    //{"d":{"__type":"Miic.BaseStruct.MiicBooleanWithMessage","result":true,"ErrorMessage":""}}
                    try {
                        //将字符串转换成jsonObject对象
                        JSONObject result = new JSONObject(response.body()).getJSONObject("d");
                        //获取对应的值
                        boolean loginResult = result.getBoolean("result");
                        String errorMessage = result.getString("ErrorMessage");
                        if(loginResult==false){
                            Log.e("faindPasswordActivity",errorMessage);
                            Toast.makeText(BindPhoneActivity.this,errorMessage,Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(BindPhoneActivity.this,"验证码已发送",Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException ex){
                        Log.e("faindPasswordActivity",ex.getMessage());
                        Toast.makeText(BindPhoneActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(BindPhoneActivity.this,"验证码发送失败！请稍后再试!",Toast.LENGTH_SHORT).show();
                }

            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(BindPhoneActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
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
