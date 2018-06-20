package com.example.miic.common;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.widget.ImageView;

import com.alibaba.mobileim.YWAPI;
import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.aop.AdviceBinder;
import com.alibaba.mobileim.aop.PointCutEnum;
import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.alibaba.wxlib.util.SysUtil;
import com.example.miic.R;
import com.example.miic.oa.common.IM.ChattingOperationCustomSample;
import com.example.miic.oa.common.IM.ConversationListUICustomSample;
import com.lzy.ninegrid.NineGridView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Iterator;
import java.util.List;



/**
 * 便于随时随地使用context
 * MyApplation.getContext()便可以得到全局的context
 * 但是不到迫不得已不要使用，因为会引起内存泄露
 */
public class MyApplication extends Application {
    private static Context context;
    //阿里百川
    public static String APP_KEY = "24814440";
    public final String App_Secret = "a2ba2d99bf2da37cb663617d771cfc86";
    //获取SDK对象
    public static  YWIMKit mIMKit ;
    //项目的包名
    public final static String PKG_NAME = "com.example.ccy.oa";
    private static final String TAG = "Init";
    public static SharedPreferences sp;
    public static SharedPreferences sps;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        oneInit();
        context=getApplicationContext();
        sp = getSharedPreferences("cookies_prefs", Context.MODE_PRIVATE);
        sps = getSharedPreferences("data", MODE_PRIVATE);
        NineGridView.setImageLoader(new PicassoImageLoader());
        //在应用启动时初始化AlibabaSDK
        //initCloudChannel(this);
        //阿里百川
        //必须首先执行这部分代码, 如果在":TCMSSevice"进程中，无需进行云旺（OpenIM）和app业务的初始化，以节省内存;
//        SysUtil.setApplication(this);
//        if(SysUtil.isTCMSServiceProcess(this)){
//            return;
//        }
        //todo Application.onCreate中，首先执行这部分代码，以下代码固定在此处，不要改动，这里return是为了退出Application.onCreate！！！
        if(mustRunFirstInsideApplicationOnCreate()){
            //todo 如果在":TCMSSevice"进程中，无需进行openIM和app业务的初始化，以节省内存
            return;
        }
        //第一个参数是Application Context
        //这里的APP_KEY即应用创建时申请的APP_KEY，同时初始化必须是在主进程中
        if(SysUtil.isMainProcess()){
            YWAPI.init(this, APP_KEY);
            SharedPreferences sf=getSharedPreferences("data",MODE_PRIVATE);
            String userID=sf.getString("userID","");
            if(userID!=""){
                mIMKit= YWAPI.getIMKitInstance(userID, APP_KEY);
                //从IMKit对象中获取IMCore
//            YWIMCore imCore = mIMKit.getIMCore();
            }
        }
        AdviceBinder.bindAdvice(PointCutEnum.CHATTING_FRAGMENT_POINTCUT,ChattingOperationCustomSample.class);
        AdviceBinder.bindAdvice(PointCutEnum.CONVERSATION_FRAGMENT_UI_POINTCUT, ConversationListUICustomSample.class);
//        AdviceBinder.bindAdvice(PointCutEnum.CHATTING_FRAGMENT_UI_POINTCUT, ChattingUICustomSample.class);
    }


    /**
     * 单次初始化
     */
    public void oneInit() {
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        Log.i("processAppName---", processAppName);
        //默认的app会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就return掉
        if (processAppName == null ||!processAppName.equalsIgnoreCase(PKG_NAME)) {
            Log.i("service process!","jinru");
            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }
    }

    private boolean mustRunFirstInsideApplicationOnCreate() {
        //必须的初始化
        SysUtil.setApplication(this);
        return SysUtil.isTCMSServiceProcess(context);
    }

    public static Context getContext() {
        return context;
    }

    public static String getAPPKEY() {
        return APP_KEY;
    }
    public static YWIMKit getYWIMKit() {
        return mIMKit;
    }
    public static void setYWIMKit(YWIMKit IMKit) {
        mIMKit = IMKit;
    }

    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return processName;
    }
    /**
     * 初始化云推送通道
     * @param applicationContext
     */
    private void initCloudChannel(Context applicationContext) {
        PushServiceFactory.init(applicationContext);
        final CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                Log.i(TAG, "init cloudchannel success");
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                Log.e(TAG, "init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });

        // 初始化小米通道，自动判断是否支持小米系统推送，如不支持会跳过注册
//        MiPushRegister.register(applicationContext, "小米AppID", "小米AppKey");
        // 初始化华为通道，自动判断是否支持华为系统推送，如不支持会跳过注册
//        HuaWeiRegister.register(applicationContext);
    }

    /** Picasso 加载 */
    private class PicassoImageLoader implements NineGridView.ImageLoader {

        @Override
        public void onDisplayImage(Context context, ImageView imageView, String url) {
            int height = 300;
            int width = 300;
            if(imageView.getHeight()==0){
                height = 800;
                width = 800;
            }
            Picasso.with(context).load(url)//
                    .placeholder(R.drawable.ic_default_image)//
                    .error(R.drawable.ic_default_image)//
                    .resize(width,height)
                    .into(imageView);
        }

        @Override
        public Bitmap getCacheImage(String url) {
            return null;
        }
    }
    public static String getCookieStr(){
        String userID = sp.getString("MiicID","");
        String userCode = sp.getString("MiicUserName","");
        String expires =sp.getString("expires","");
        String path = sp.getString("path","");
        String oa = sp.getString("oa.miic.com.cn","");
        return oa;
    }
    public static String getUserID(){
        return sps.getString("userID","");
    }
    public static String getUserName(){
        return sps.getString("userName","");
    }
    public static String getDeptName(){
        return sps.getString("deptName","");
    }
    public static String getDeptID(){
        return sps.getString("deptID","");
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }
}
