package com.example.miic.oa.login.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
import com.example.miic.oa.basic.activity.GuideActivity;
import com.example.miic.oa.basic.activity.MainActivity;
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify;
import com.wei.android.lib.fingerprintidentify.base.BaseFingerprint;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.miic.common.MyApplication.APP_KEY;
import static com.example.miic.oa.common.Setting._AutoLoginStateCallback;


/**
 * Created by ccy on 2018/1/15.
 * 项目起始页，显示5秒就关闭。
 */

public class SplashActivity extends AppCompatActivity {
    private static final int TIME = 1000;
    private static final int GO_MAin = 100;
    private static final int GO_GUIDE = 101;
    private static final int GO_LOGIN = 102;
    Handler mhandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_MAin:
                    goMain();
                    break;
                case GO_GUIDE:
                    //goGuide();
                    break;
                case GO_LOGIN:
                    goLogin();
                    break;
            }
        }
    };

    //指纹初始化
    private FingerprintIdentify mFingerprintIdentify;

    private static final int MAX_AVAILABLE_TIMES = 3;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        versionUP();
//        mFingerprintIdentify = add FingerprintIdentify(this, add BaseFingerprint.FingerprintIdentifyExceptionListener() {
//            @Override
//            public void onCatchException(Throwable exception) {
//                Log.i("SplashActity:","\nException：" + exception.getLocalizedMessage());
//            }
//        });
//        if (!mFingerprintIdentify.isFingerprintEnable()&&!mFingerprintIdentify.isHardwareEnable()&&!mFingerprintIdentify.isRegisteredFingerprint()) {
//            //不支持指纹，直接进入init
//            init();
//        }else{
//            start();
//        }
    }

    private void init() {
        //判断是否是第一次进入
        SharedPreferences sf = getSharedPreferences("data", MODE_PRIVATE);
        boolean isFirstIn = sf.getBoolean("isFirstIn", true);
        /*
        * 在这里判断用户是否能已经登录，跟是否是第一次进入一起判断。
        * */
        boolean isLogin = sf.getBoolean("isLogin", false);
        String userCode = sf.getString("userCode", "");
        System.out.println("是否已经登录：" + isLogin);
        System.out.println("用户名：" + userCode);
        SharedPreferences.Editor editor = sf.edit();
        if (isFirstIn) {
            //若为true，则为第一次进入
            editor.putBoolean("isFirstIn", false);
            //将欢迎页停留5秒，并且将message设置为跳转到引导页SplashActivity，
            // 跳转在goGuide中实现
            mhandler.sendEmptyMessageDelayed(GO_GUIDE, TIME);
        } else {
            if (isLogin == true || userCode != "") {
                //将欢迎页停留5秒，并且将message设置文跳转到MainActivity，
                // 跳转功能在goMain中实现
                mhandler.sendEmptyMessageDelayed(GO_MAin, TIME);
            } else {
                //跳转到登录页面
                mhandler.sendEmptyMessageDelayed(GO_LOGIN, TIME);
            }
        }
        editor.commit();
    }

    //版本升级
    private void versionUP() {
        Call<String> call = PostRequest.Instance.request.GetVersionNum();
        Callback<String> callback = new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.body() != null) {
                    try {
                        final JSONObject result = new JSONObject(response.body());
                        String version = result.getString("version");
                        final String updateUrl = result.getString("update_url").toString();
                        Log.i("版本信息", version);
                        Log.i("版本信息", (version.equals("1")) + "");
                        //if(BuildConfig.VERSION_CODE+"" !=version){
                        if (version.equals(BuildConfig.VERSION_CODE + "")) {
                            //版本不同，更新
                            /* @setIcon 设置对话框图标
                             * @setTitle 设置对话框标题
                             * @setMessage 设置对话框消息提示
                             * setXXX方法返回Dialog对象，因此可以链式设置属性
                             */
                            final AlertDialog.Builder normalDialog =
                                    new AlertDialog.Builder(SplashActivity.this);
                            normalDialog.setTitle("提示更新");
                            normalDialog.setMessage(BuildConfig.VERSION_NAME + "已经正式发布！");
                            normalDialog.setPositiveButton("更新",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //...To-do
                                            Uri uri = Uri.parse(updateUrl);
                                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                            startActivity(intent);
                                        }
                                    });
                            normalDialog.setNegativeButton("取消",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //...To-do
                                            //fingerUnlock();
                                            judgeLogin();

                                        }
                                    });
                            // 显示
                            normalDialog.show();
                        }
                    } catch (JSONException ex) {
                        Toast.makeText(SplashActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                    }

                } else {
                    //fingerUnlock();
                    judgeLogin();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                judgeLogin();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //指纹解锁
//    private void fingerUnlock() {
//        fingerPng.setVisibility(View.VISIBLE);
//        mFingerprintIdentify = add FingerprintIdentify(this, add BaseFingerprint.FingerprintIdentifyExceptionListener() {
//            @Override
//            public void onCatchException(Throwable exception) {
//                Log.i("LoginActivity:", "\nException：" + exception.getLocalizedMessage());
//            }
//        });
//        if (!mFingerprintIdentify.isFingerprintEnable()
//                && !mFingerprintIdentify.isHardwareEnable()
//                && !mFingerprintIdentify.isRegisteredFingerprint()) {
//            //不支持指纹，直接进入
//            fingerPng.setVisibility(View.GONE);
//            judgeLogin();
//        } else {
//            final int time = MAX_AVAILABLE_TIMES;
//            //指纹解锁
//            mFingerprintIdentify.startIdentify(MAX_AVAILABLE_TIMES, add BaseFingerprint.FingerprintIdentifyListener() {
//                @Override
//                public void onSucceed() {
//                    Log.i("LoginActivity", "\n" + getString(R.string.succeed));
//                    Toast.makeText(LoginActivity.this, getString(R.string.succeed), Toast.LENGTH_LONG);
//                    fingerPng.setVisibility(View.GONE);
//                    Intent intent = add Intent(LoginActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//
//                @Override
//                public void onNotMatch(int availableTimes) {
//                    Log.i("LoginActivity", "\n" + getString(R.string.not_match, availableTimes));
//                    if (time > 0) {
//                        Toast.makeText(LoginActivity.this, getString(R.string.not_match, availableTimes), Toast.LENGTH_LONG);
//                    } else {
//                        fingerPng.setVisibility(View.GONE);
//                        Toast.makeText(LoginActivity.this, "验证密码错误！请输入密码登录！", Toast.LENGTH_LONG);
//                    }
//                }
//
//                @Override
//                public void onFailed() {
//                    Log.i("LoginActivity", "\n" + getString(R.string.failed));
//                    Toast.makeText(LoginActivity.this, getString(R.string.failed), Toast.LENGTH_LONG);
//                    fingerPng.setVisibility(View.GONE);
//                }
//            });
//        }
//    }

    private void judgeLogin() {
        //判断是否是第一次进入
        SharedPreferences sf = getSharedPreferences("data", MODE_PRIVATE);
        /*
        * 在这里判断用户是否能已经登录，跟是否是第一次进入一起判断。
        * */
        boolean isLogin = sf.getBoolean("isLogin", false);
        String userCode = sf.getString("userCode", "");
        System.out.println("是否已经登录：" + isLogin);
        System.out.println("用户名：" + userCode);
        SharedPreferences.Editor editor = sf.edit();

        if (isLogin == true || userCode != "") {
            goMain();
        }else{
            goLogin();
        }
        editor.commit();
    }

    private void goMain() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void goGuide() {
        Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
        startActivity(intent);
        finish();
    }

    private void goLogin() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    //开始指纹验证
    public void start() {
        mFingerprintIdentify.startIdentify(MAX_AVAILABLE_TIMES, new BaseFingerprint.FingerprintIdentifyListener() {
            @Override
            public void onSucceed() {
                Log.i("SplashActivity", "\n" + getString(R.string.succeed));
            }

            @Override
            public void onNotMatch(int availableTimes) {
                Log.i("SplashActivity", "\n" + getString(R.string.not_match, availableTimes));
            }

            @Override
            public void onFailed() {
                Log.i("SplashActivity", "\n" + getString(R.string.failed));
            }
        });
    }
}
