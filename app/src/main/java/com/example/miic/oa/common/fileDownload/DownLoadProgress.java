package com.example.miic.oa.common;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import com.example.miic.R;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.ContentValues.TAG;

/**
 * Created by XuKe on 2018/2/27.
 */

public class DownLoadProgress extends AsyncTask<String,Integer,Integer> {

    private Context context;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;

    public DownLoadProgress(Context context){
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
        String channelId = null;
        builder = new NotificationCompat.Builder(context,channelId);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentInfo("下载中...")
                .setContentTitle("正在下载");

    }

    @Override
    //String...str,表示可变字符串数组，String[] str
    public Integer doInBackground(String... params) {
        Log.e(TAG, "doInBackground: "+params[0] );
        InputStream is = null;
        OutputStream os = null;
        HttpURLConnection connection = null;
        int total_length = 0;
        try {
            URL url1 = new URL(params[0]);
            connection = (HttpURLConnection) url1.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(50000);
            connection.connect();

            if(connection.getResponseCode() == 200){
                is = connection.getInputStream();
                os = new FileOutputStream("/sdcard/OA_file");
                byte [] buf = new byte[1024];
                int len;
                int pro1=0;
                int pro2=0;
                // 获取文件流大小，用于更新进度
                long file_length = connection.getContentLength();
                while((len = is.read(buf))!=-1){
                    total_length += len;
                    if(file_length>0) {
                        pro1 = (int) ((total_length / (float) file_length) * 100);//传递进度（注意顺序）
                    }
                    if(pro1!=pro2) {
                        // 调用update函数，更新进度
                        publishProgress(pro2=pro1);
                    }
                    os.write(buf, 0, len);
                }
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return total_length;
    }

    @Override
    protected void onCancelled(Integer integer) {
        super.onCancelled(integer);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        builder.setProgress(100,values[0],false);
        notificationManager.notify(0x3,builder.build());
        //下载进度提示
        builder.setContentText("下载"+values[0]+"%");
        if(values[0]==100) {    //下载完成后点击安装
            Intent it = new Intent(Intent.ACTION_VIEW);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            it.setDataAndType(Uri.parse("file:///sdcard/zongzhi.apk"), "application/vnd.android.package-archive");
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, it, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentTitle("下载完成")
                    .setContentText("点击安装")
                    .setContentInfo("下载完成")
                    .setContentIntent(pendingIntent);
            notificationManager.notify(0x3, builder.build());
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        if(integer == 100) {
            Toast.makeText(context, "下载完成", Toast.LENGTH_SHORT).show();
        }
    }
}
