package com.example.miic.oa.common;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.storage.StorageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.mobileim.YWChannel;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.login.IYWConnectionListener;
import com.alibaba.mobileim.login.YWLoginCode;
import com.alibaba.mobileim.login.YWLoginState;
import com.example.miic.BuildConfig;
import com.example.miic.R;
import com.example.miic.common.MyApplication;
import com.example.miic.oa.common.fileDownload.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by XuKe on 2018/2/27.
 */

public class Setting {
    private String Service="http://oa.miic.com.cn/";
    private static IYWConnectionListener mConnectionListener;
    public void setService(String service){
        this.Service = service;
    }
    public String getService(){
        return Service;
    }
    public int getGif(String src){
        int i = R.drawable.gif_01;
        switch (src){
            case "01":
                i= R.drawable.gif_01;
                break;
            case "02" :
                i= R.drawable.gif_02;break;
            case "03" :
                i= R.drawable.gif_03;break;
            case "04" :
                i= R.drawable.gif_04;break;
            case "05" :
                i= R.drawable.gif_05;break;
            case "06" :
                i= R.drawable.gif_06;break;
            case "07" :
                i= R.drawable.gif_07;break;
            case "08" :
                i= R.drawable.gif_08;break;
            case "09" :
                i= R.drawable.gif_09;break;
            case "10" :
                i= R.drawable.gif_10;break;
            case "11" :
                i= R.drawable.gif_11;break;
            case "12" :
                i= R.drawable.gif_12;break;
            case "13" :
                i= R.drawable.gif_13;break;
            case "14" :
                i= R.drawable.gif_14;break;
            case "15" :
                i= R.drawable.gif_15;break;
            case "16" :
                i= R.drawable.gif_16;break;
            case "17" :
                i= R.drawable.gif_17;break;
            case "18" :
                i= R.drawable.gif_18;break;
            case "19" :
                i= R.drawable.gif_19;break;
            case "20" :
                i= R.drawable.gif_20;break;
            case "21" :
                i= R.drawable.gif_21;break;
            case "22" :
                i= R.drawable.gif_22;break;
            case "23" :
                i= R.drawable.gif_23;break;
            case "24" :
                i= R.drawable.gif_24;break;
            case "25" :
                i= R.drawable.gif_25;break;
            case "26" :
                i= R.drawable.gif_26;break;
            case "27" :
                i= R.drawable.gif_27;break;
            case "28" :
                i= R.drawable.gif_28;break;
            case "29" :
                i= R.drawable.gif_29;break;
            case "30" :
                i= R.drawable.gif_30;break;
            case "31" :
                i= R.drawable.gif_31;break;
            case "32" :
                i= R.drawable.gif_32;break;
            case "33" :
                i= R.drawable.gif_33;break;
            case "34" :
                i= R.drawable.gif_34;break;
            case "35" :
                i= R.drawable.gif_35;break;
            case "36" :
                i= R.drawable.gif_36;break;
            case "37" :
                i= R.drawable.gif_37;break;
            case "38" :
                i= R.drawable.gif_38;break;
            case "39" :
                i= R.drawable.gif_39;break;
            case "40" :
                i= R.drawable.gif_40;break;
            case "41" :
                i= R.drawable.gif_41;break;
            case "42" :
                i= R.drawable.gif_42;break;
            case "43" :
                i= R.drawable.gif_43;break;
            case "44" :
                i= R.drawable.gif_44;break;
            case "45" :
                i= R.drawable.gif_45;break;
            case "46" :
                i= R.drawable.gif_46;break;
            case "47" :
                i= R.drawable.gif_47;break;
            case "48" :
                i= R.drawable.gif_48;break;
            case "49" :
                i= R.drawable.gif_49;break;
            case "50" :
                i= R.drawable.gif_50;break;
            case "51" :
                i= R.drawable.gif_51;break;
            case "52" :
                i= R.drawable.gif_52;break;
            case "53" :
                i= R.drawable.gif_53;break;
            case "54" :
                i= R.drawable.gif_54;break;
            case "55" :
                i= R.drawable.gif_55;break;
            case "56" :
                i= R.drawable.gif_56;break;
            case "57" :
                i= R.drawable.gif_57;break;
            case "58" :
                i= R.drawable.gif_58;break;
            case "59" :
                i= R.drawable.gif_59;break;
            case "60" :
                i= R.drawable.gif_60;break;
            case "61" :
                i= R.drawable.gif_61;break;
            case "62" :
                i= R.drawable.gif_62;break;
            case "63" :
                i= R.drawable.gif_63;break;
            case "64" :
                i= R.drawable.gif_64;break;
            case "65" :
                i= R.drawable.gif_65;break;
            case "66" :
                i= R.drawable.gif_66;break;
            case "67" :
                i= R.drawable.gif_67;break;
            case "68" :
                i= R.drawable.gif_68;break;
            case "69" :
                i= R.drawable.gif_69;break;
            case "70" :
                i= R.drawable.gif_70;break;
            case "71" :
                i= R.drawable.gif_71;break;
            case "72" :
                i= R.drawable.gif_72;break;
            case "73" :
                i= R.drawable.gif_73;break;
            case "74" :
                i= R.drawable.gif_74;break;
            case "75" :
                i= R.drawable.gif_75;break;
            default:
                i=R.drawable.gif_01;

        }
        return i;
    }
    /**
     * 下载单独文件
     * @param path 下载文件的地址
     * @param FileName 文件名字
     */
    public static void downLoadSingle(final String path, final String FileName,Activity activity) {
        requestPermission(activity);
        /* 取得扩展名 */
        final String[] arr = path.split("\\.");
        Log.i("文件长地址：",path+"");

                new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Looper.prepare();
                    URL url = new URL(path); 
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setReadTimeout(5000);
                    con.setConnectTimeout(5000);
                    con.setRequestProperty("Charset", "UTF-8");
                    con.setRequestMethod("GET");
                    final String filePath = Environment.getExternalStorageDirectory().toString()+"/OA_file";//MyApplication.getContext().getFilesDir().getAbsolutePath()+"/OA_file";//strAllPath[strAllPath.length-1] + "/OA_file";
                    if (con.getResponseCode() == 200) {
                        InputStream is = con.getInputStream();//获取输入流
                        //获取文件总长度
                        int contentLength = con.getContentLength();
                        Log.i("文件长度：",contentLength+"");
                        FileOutputStream fileOutputStream = null;//文件输出流
                        if (is != null) {
                            //记录下载进度
                            int downloadSize = 0;
                            //判断是否包含文件或者文件夹
                            FileUtils fileUtils = new FileUtils(filePath);
                            String end="pdf";
                            if(arr.length!=0){
                                end =  arr[arr.length-1];
                            }
                            fileOutputStream = new FileOutputStream(fileUtils.createFile(FileName+"."+end),true);//指定文件保存路径，代码看下一步
                            byte[] buf = new byte[1024];
                            int ch;
                            while ((ch = is.read(buf)) != -1) {
                                fileOutputStream.write(buf, 0, ch);//将获取到的流写入文件中
                                downloadSize += ch;
                                if(downloadSize==contentLength){
                                    Intent intent;
                                    //Android 7.0的安全策略变了，解决方法是用provider来实现打开文件
                                    //判断是否是AndroidN以及更高的版本，Build.VERSION_CODES.N是Android 7.0
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        File txtFile = new File(filePath+"/"+FileName+"."+end);
                                        Uri contentUri = FileProvider.getUriForFile(MyApplication.getContext(), BuildConfig.APPLICATION_ID+".provider", txtFile);
                                        Log.i("文件地址：",contentUri.toString());
                                        intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                                        grantUriPermission(MyApplication.getContext(), contentUri, intent);
                                    } else {
                                        //7.0以下的可以打开文件了
                                        intent = OpenFileUtil.openFile(filePath+"/"+FileName+"."+end);

                                    }
                                    MyApplication.getContext().startActivity(intent);
                                }
                            }

                        }
                        if (fileOutputStream != null) {
                            fileOutputStream.flush();
                            fileOutputStream.close();

                        }
                    }else {
                        Toast.makeText(MyApplication.getContext(),con.getResponseCode()+"文件找不到或者有问题！不能下载！",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("下载：",e.getMessage());
                    Toast.makeText(MyApplication.getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }
        }).start();
        Toast.makeText(MyApplication.getContext(),"文件下载至："+Environment.getExternalStorageDirectory().toString()+"/OA_file/",Toast.LENGTH_SHORT).show();

    }

    /**
     * 手机号码校验
     */
    public static boolean isCellphone(String str) {
        Pattern pattern = Pattern.compile("^((13[0-9])|(14[5,7,9])|(15[^4])|(18[0-9])|(17[0,1,3,5,6,7,8]))\\d{8}$");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
    //step1：   写个方法 _AutoLoginStateCallback()   。这个是用来自动登录的方法
    public static void _AutoLoginStateCallback() {
        YWChannel.setAutoLoginCallBack(new IWxCallback() {
            @Override
            public void onSuccess(Object... result) {
                sendAutoLoginState(YWLoginState.success);
            }
            @Override
            public void onError(int code, String info) {
                sendAutoLoginState(YWLoginState.fail);
            }
            @Override
            public void onProgress(int progress) {
                sendAutoLoginState(YWLoginState.logining);
            }
        });
    }
    //step2：      //将自动登录的状态广播出去
    private static void sendAutoLoginState(YWLoginState loginState) {
        Intent intent = new Intent("com.openim.autoLoginStateActionn");
        intent.putExtra("state", loginState.getValue());
        LocalBroadcastManager.getInstance(YWChannel.getApplication()).sendBroadcast(intent);
    }
    //step3：  在使用自动登录的时，调用一下_AutoLoginStateCallback() 这个方法即可实现自动登录。
    //设置连接状态的监听
    public static void addConnectionListener() {
        mConnectionListener = new IYWConnectionListener() {
            @Override
            public void onDisconnect(int code, String info) {
                if(code != YWLoginCode.LOGON_OK){
                    _AutoLoginStateCallback();
                }
            }

            @Override
            public void onReConnecting() {

            }

            @Override
            public void onReConnected() {

            }
        };
        MyApplication.getYWIMKit().getIMCore().addConnectionListener(mConnectionListener);
    }

    //设备API大于6.0时，主动申请权限（读取文件的权限）
    public static  void requestPermission(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 0);

            }
        }
    }
    //添加权限
    private static void grantUriPermission (Context context, Uri fileUri, Intent intent) {
        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

    /**
     * 得到所有的存储路径（内部存储+外部存储）
     *
     * @param context
     * @return
     */
    public static String[] getAllSdPaths(Context context) {
        Method mMethodGetPaths = null;
        String[] paths = null;
        //通过调用类的实例mStorageManager的getClass()获取StorageManager类对应的Class对象
        //getMethod("getVolumePaths")返回StorageManager类对应的Class对象的getVolumePaths方法，这里不带参数
        StorageManager mStorageManager = (StorageManager)context
                .getSystemService(context.STORAGE_SERVICE);//storage
        try {
            mMethodGetPaths = mStorageManager.getClass().getMethod("getVolumePaths");
            paths = (String[]) mMethodGetPaths.invoke(mStorageManager);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //paths[0];// 手机
        //paths[1];// sd卡
        return paths;
    }
    /*
* 将时间戳转换为时间
*/
    public static String stampToDate(String s){
        s = s.substring(6,s.length()-2);
        final Date date = new Date(Long.parseLong(s));
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return  format.format(date);
    }
    public static String stampToTime(String s){
        s = s.substring(6,s.length()-2);
        final Date date = new Date(Long.parseLong(s));
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return  format.format(date);
    }
}
