package com.example.miic.oa.login.activity;

import android.content.Intent;
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

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.miic.oa.common.Setting.isCellphone;

public class FindPwdActivity extends AppCompatActivity{
    //第一步
    private EditText phoneNumView;
    private EditText identifyingCodeView;
    private Button identifyingPhoneView;
    private TextView sendidentifyingCodeView;
    private LinearLayout firstLayout;
    private long millisUntilFinished=90000;
    //第二步
    private EditText newPasswordView;
    private EditText secondPasswordView;
    private Button confirmPasswordView;
    private LinearLayout secondLayout;

    //第三步
    private Button goToMainPageView;
    private LinearLayout thridLayout;

    private LinearLayout backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pwd);
        //设置找回密码界面
        phoneNumView = (EditText)findViewById(R.id.phoneNum);
        identifyingCodeView = (EditText)findViewById(R.id.identifyingCode);
        identifyingPhoneView = (Button)findViewById(R.id.identifyingPhone);
        sendidentifyingCodeView = (TextView)findViewById(R.id.sendIdentifyingCode);
        newPasswordView = (EditText)findViewById(R.id.input_new_password);
        secondPasswordView = (EditText)findViewById(R.id.input_new_password_again);
        confirmPasswordView = (Button)findViewById(R.id.confirmPassword);
        goToMainPageView = (Button)findViewById(R.id.interMainPage);
        backBtn = (LinearLayout)findViewById(R.id.back);
        backBtn.setOnClickListener(backClickListener);
        //三个界面
        firstLayout = (LinearLayout)findViewById(R.id.firstStepLinearLayout);
        secondLayout = (LinearLayout)findViewById(R.id.secondStepLinearLayout);
        thridLayout = (LinearLayout)findViewById(R.id.thirdStepLinearLayout);
        /**
         * 第一步
         */
         String phoneNum = phoneNumView.getText().toString().trim();
        //发送验证码
        sendidentifyingCodeView.setOnClickListener(sendIdentifyingCodeListener);

        /**
         * 第二步
         */
        //判断两输入的密码是否一致。
        secondPasswordView.setOnFocusChangeListener(passwordLoseFocusListener);
        //提交手机，验证码，密码
        confirmPasswordView.setOnClickListener(confirmPasswordListener);
        //跳转到主页
        goToMainPageView.setOnClickListener(goToMainPageListener);
    }
    /**
     * 跳转到主页按钮监听事件
     */
    private View.OnClickListener goToMainPageListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };
    /**
     * 提交密码按钮监听事件
     */
    private View.OnClickListener confirmPasswordListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String newPassword = newPasswordView.getText().toString().trim();
            String secondPassword = secondPasswordView.getText().toString().trim();
            String identifyingCode = identifyingCodeView.getText().toString().trim();
            String phoneNum = phoneNumView.getText().toString().trim();
            //判断两次输入的密码是否一致
            if(!newPassword.equals(secondPassword)){
                secondPasswordView.setError(getString(R.string.error_password));
                secondPasswordView.requestFocus();
            }else if(newPassword.isEmpty()){
                //密码字段为空
                newPasswordView.setError(getString(R.string.error_field_required));
                newPasswordView.requestFocus();
            }else if(secondPassword.isEmpty()){
                //确认密码字段为空
                secondPasswordView.setError(getString(R.string.error_field_required));
                secondPasswordView.requestFocus();
            }
            else{
                JSONObject requestJson = new JSONObject();
                try{
                    JSONObject val = new JSONObject();
                    val.put("IdentifyingCode",identifyingCode);
                    val.put("NewPassword",newPassword);
                    val.put("Mobile",phoneNum);
                    requestJson.put("resetUserInfo",val);

                }catch (JSONException ex){
                    Log.i("findPassword","json对象构造错误");
                }
                //提交修改密码申请
                resetMobilePassword(requestJson);
            }
        }
    };
    /**
     * 发送手机验证码（http请求）
     */
    public void SendIdentifyToMobile(String mobile){
        RequestBody MobileResetUserInfoView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), "{'mobile':'"+mobile+"'}");

        Call<String> call = PostRequest.Instance.request.SendIdentifyToMobile(MobileResetUserInfoView);
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
                        JSONObject result = new JSONObject(response.body()).getJSONObject("d");
                        //获取对应的值
                        boolean loginResult = result.getBoolean("result");
                        String errorMessage = result.getString("ErrorMessage");
                        if(loginResult==false){
                            Log.e("faindPasswordActivity",errorMessage);
                            Toast.makeText(FindPwdActivity.this,errorMessage,Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(FindPwdActivity.this,"验证码已发送",Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException ex){
                        Log.e("faindPasswordActivity",ex.getMessage());
                        Toast.makeText(FindPwdActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(FindPwdActivity.this,"验证码发送失败！请稍后再试!",Toast.LENGTH_SHORT).show();
                }

            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(FindPwdActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }

    /**
     * 修改密码申请（http请求）
     */
    public void resetMobilePassword(JSONObject requestJson){
        RequestBody MobileResetUserInfoView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.ResetMobilePassword(MobileResetUserInfoView);
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
                        JSONObject result = new JSONObject(response.body()).getJSONObject("d");
                        //获取对应的值
                        boolean results = result.getBoolean("result");
                        String errorMessage = result.getString("ErrorMessage");
                        if(results==false){
                            Log.e("faindPasswordActivity",errorMessage);
                            Toast.makeText(FindPwdActivity.this,errorMessage,Toast.LENGTH_SHORT).show();
                            if(errorMessage.equals("验证码过期")){
                                //修改失败，（1）验证码过期，跳转回前一页，验证码输入框清空，两个密码框清空
                                firstLayout.setVisibility(View.VISIBLE);
                                secondLayout.setVisibility(View.GONE);
                                identifyingCodeView.setText("");
                                newPasswordView.setText("");
                                secondPasswordView.setText("");
                            }else if(errorMessage.equals("数据更新错误")){
                                //（2）数据更新错误，密码修改失败，请稍后再试，回到登录页面
                                Toast.makeText(FindPwdActivity.this,"密码修改失败，请稍后再试！",Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(FindPwdActivity.this,LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(FindPwdActivity.this,errorMessage,Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            //修改成功，跳转到下一页
                            firstLayout.setVisibility(View.GONE);
                            secondLayout.setVisibility(View.GONE);
                            thridLayout.setVisibility(View.VISIBLE);
                            backBtn.setVisibility(View.GONE);
                        }
                    }catch (JSONException ex){
                        Log.e("faindPasswordActivity",ex.getMessage());
                        Toast.makeText(FindPwdActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(FindPwdActivity.this,"修改密码失败，请稍后再试！",Toast.LENGTH_SHORT).show();
                }

            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                 Toast.makeText(FindPwdActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
                //回到登录页面
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    /**
     * 输入框失去焦点的监听事件//判断两次输入的密码是否一致
     */
    private View.OnFocusChangeListener passwordLoseFocusListener=new View.OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            // TODO Auto-generated method stub
            if(hasFocus){
                //获得焦点处理
                secondPasswordView.selectAll();
            }
            else {
                String newPassword = newPasswordView.getText().toString().trim();
                String secondPassword = secondPasswordView.getText().toString().trim();
                //失去焦点处理
                if(newPassword!=secondPassword){
                    secondPasswordView.setError(getString(R.string.error_password));
                    phoneNumView.requestFocus();
                }
            }
        }
    };
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
                SendIdentifyToMobile(phoneNum);
                //设置验证码有效期倒计时
                CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(sendidentifyingCodeView, millisUntilFinished, 1000);
                mCountDownTimerUtils.start();
                //设置验证手机按钮的背景色并添加监听事件。
                //验证手机(即下一页)
                identifyingPhoneView.setOnClickListener(goToSecondStepListener);
                identifyingPhoneView.setBackgroundResource(R.drawable.btn_shape_background);  //还原背景色
            }else{
                Toast.makeText(FindPwdActivity.this,"请输入手机号！",Toast.LENGTH_SHORT).show();
            }

        }
    };
    /**
     * 验证手机按钮监听事件
     */
    private View.OnClickListener goToSecondStepListener = new View.OnClickListener(){
        public void onClick(View view){
            String identifyingCode = identifyingCodeView.getText().toString().trim();
            String phoneNum = phoneNumView.getText().toString().trim();
            Log.i("findpassword","验证手机按钮触发");
            Log.i("findpassword",phoneNum);
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
                firstLayout.setVisibility(View.GONE);
                secondLayout.setVisibility(View.VISIBLE);
            }
        }
    };
    //返回按钮绑定事件
    private View.OnClickListener backClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };

}
