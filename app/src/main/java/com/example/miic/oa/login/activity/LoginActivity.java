package com.example.miic.oa.login.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.mobileim.IYWLoginService;
import com.alibaba.mobileim.YWAPI;
import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.YWLoginParam;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.example.miic.BuildConfig;
import com.example.miic.R;
import com.example.miic.base.http.PostRequest;
import com.example.miic.common.MyApplication;
import com.example.miic.oa.basic.activity.MainActivity;
import com.squareup.picasso.Picasso;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify;
import com.wei.android.lib.fingerprintidentify.base.BaseFingerprint;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.miic.common.MyApplication.APP_KEY;
import static com.example.miic.oa.common.Setting._AutoLoginStateCallback;

/**
 * 通过用户名与密码登录的登录界面
 */
public class LoginActivity extends AppCompatActivity {
    // UI references.
    private EditText mUserNameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private LinearLayout mforgetPwdView;
    private ImageView weChatBtn;
    private SharedPreferences sf;

    //指纹初始化
    private FingerprintIdentify mFingerprintIdentify;
    private static final int MAX_AVAILABLE_TIMES = 3;
    private LinearLayout fingerPng;
    //版本相关
    private String VersionID = "5abcc4d6ca87a86ab700fb0a";
    private String VersionToken = "59d330e18e8928dbf56dbb3922e1e8e6";

    public static final String APP_ID = "wx798XXXXXXXXX";//(这个APP_ID为在微信开放平台创建应用后获得的ID)
    private IWXAPI api;//要导入微信相关包

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sf = getSharedPreferences("data", MODE_PRIVATE);
        // 设置登录界面
        mUserNameView = (EditText) findViewById(R.id.userName);
        mPasswordView = (EditText) findViewById(R.id.password);
        mforgetPwdView = (LinearLayout) findViewById(R.id.forgetPwd);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        fingerPng = (LinearLayout) findViewById(R.id.finger);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                showProgress(true);
                attemptLogin();
            }
        });
        mforgetPwdView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, FindPwdActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        //versionUP();

        //微信授权登录
//        api=WXAPIFactory.createWXAPI(this, APP_ID,true);
//        api.registerApp(APP_ID);
//        weChatBtn = (ImageView)findViewById(R.id.wechat_login);
//        weChatBtn.setOnClickListener(add OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SendAuth.Req req=add SendAuth.Req();
//                req.scope="snsapi_userinfo";
//                req.state="wechat_sdk_demo_test";
//                api.sendReq(req);
//            }
//        });
    }

    //阿里百川登录
    private void baiChuanLogin(final String userid, final String password) {
        YWIMKit mIMKit = YWAPI.getIMKitInstance(userid, APP_KEY);
        MyApplication.setYWIMKit(mIMKit);
        //开始登录
        IYWLoginService loginService = MyApplication.getYWIMKit().getLoginService();
        YWLoginParam loginParam = YWLoginParam.createLoginParam(userid, password);
        loginService.login(loginParam, new IWxCallback() {

            @Override
            public void onSuccess(Object... arg0) {
                Log.i("即时通讯", "阿里百川登录成功");
                _AutoLoginStateCallback();

            }

            @Override
            public void onProgress(int arg0) {
                // TODO Auto-generated method stub
                Log.i("即时通讯", "阿里百川onProgress");
            }

            @Override
            public void onError(int errCode, String description) {
                //如果登录失败，errCode为错误码,description是错误的具体描述信息
                Log.i("即时通讯", "阿里百川登录失败" + description + "---" + errCode);
                baiChuanLogin(userid, password);
            }
        });
    }
    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mUserNameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String userName = mUserNameView.getText().toString();
        String password = mPasswordView.getText().toString();
        Log.i("loginActivity", userName);
        Log.i("loginActivity", password);
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(userName)) {
            mUserNameView.setError(getString(R.string.error_field_required));
            focusView = mUserNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            UserLogin(userName, password);
        }
    }
    /**
     * 判断用户密码是否够长
     */
    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 1;
    }
    /**
     * 显示进度界面，隐藏登录界面
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    /**
     * 登录
     */
    public void UserLogin(final String userName, final String password) {
        RequestBody loginView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), "{loginView:{UserCode:'" + userName + "',Password:'" + password + "'}}");
        Call<String> call = PostRequest.Instance.request.AppLogin(loginView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.body() != null) {
                    //判断结果，登录成功----》隐藏进度条，结束activity，跳转到mainActivity
                    //登录失败----》隐藏进度条，用户名获取焦点。
                    try {
                        //将字符串转换成jsonObject对象
                        JSONObject result = new JSONObject(response.body()).getJSONObject("d");
                        //获取对应的值
                        boolean loginResult = result.getBoolean("Result");
                        if (loginResult == false) {
                            //登录失败，检查是哪里的错误
                            boolean checkUserCode = result.getBoolean("CheckUserCode");
                            boolean chekPassword = result.getBoolean("CheckPassword");
                            int valid = result.getInt("Valid");
                            if (checkUserCode == false) {
                                Log.i("LoginActivity", "用户名错误，请重新输入");
                                showProgress(false);
                                mUserNameView.setError(getString(R.string.error_field_required));
                                mUserNameView.requestFocus();
                            } else if (chekPassword == false) {
                                Log.i("LoginActivity", "密码错误，请重新输入");
                                showProgress(false);
                                mPasswordView.setError(getString(R.string.error_incorrect_password));
                                mPasswordView.requestFocus();
                                ;
                            } else if (valid != 2) {
                                Log.i("LoginActivity", "用户失效");
                                showProgress(false);
                                mUserNameView.setError("用户失效，请换一个用户登录");
                                mUserNameView.requestFocus();
                            }
                        } else {
                            //登录成功，将用户名与是否登陆的标志位存在缓存中
                            baiChuanLogin(result.getString("UserID"), password);

                            SharedPreferences.Editor editor = sf.edit();
                            editor.putBoolean("isLogin", true);
                            editor.putString("userCode", userName);
                            editor.putString("userID", result.getString("UserID"));
                            editor.commit();
                            System.out.println("isLogin:" + sf.getBoolean("isLogin", true));
                            System.out.println("userCode:" + sf.getString("userCode", userName));
                            Log.i("LoginActivity", "已经登录!");
                            getUserInfo();
                        }
                    } catch (JSONException e) {
                        Log.e("LoginActivity", e.getMessage());
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "当前网络不可用，请稍后再试！", Toast.LENGTH_SHORT).show();
                }
            }

            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
//                登录失败----》隐藏进度条，用户名获取焦点。
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                showProgress(false);
                Toast.makeText(LoginActivity.this, "用户登录失败，请稍后再试！", Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    private void getUserInfo(){
        JSONObject requestJson = new JSONObject();
        String userID=sf.getString("userID","");
        try{
            requestJson.put("userID",userID);
        }catch (JSONException ex){
            Log.i("MyPageFrgment","json对象构造错误");
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
                        JSONObject jsonTemp = jsonObject.getJSONObject("d");
                        String userName = jsonTemp.getString("UserName");
                        String userUrl = jsonTemp.getString("UserUrl");
                        String orgName = jsonTemp.getString("OrgName");
                        String deptName = jsonTemp.getString("DeptName");
                        String deptID = jsonTemp.getString("DeptID");
                        String userPosition = jsonTemp.getString("Position");
                        String userRemark = jsonTemp.getString("Remark");
                        String userKey = jsonTemp.getString("UserKey");
                        String userCode = jsonTemp.getString("UserCode");
                        SharedPreferences.Editor editor = sf.edit();
                        editor.putString("userName",userName);
                        editor.putString("deptName",deptName);
                        editor.putString("deptID",deptID);
                        editor.commit();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }catch (JSONException ex){
                        Log.e("InfoChildFragment",ex.getMessage());
                        Toast.makeText(LoginActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(LoginActivity.this,"获取用户信息失败！",Toast.LENGTH_SHORT).show();
                }

            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(LoginActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
}

