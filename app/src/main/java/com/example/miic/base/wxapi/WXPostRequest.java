package com.example.miic.base.wxapi;

import com.example.miic.common.MyApplication;
import com.example.miic.base.cookieSetting.AddCookiesInterceptor;
import com.example.miic.base.cookieSetting.SaveCookiesInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by XuKe on 2018/1/16.
 */

public class WXPostRequest {
    //设置请求的URL
    private  String URI="https://api.weixin.qq.com/sns/";//"http://oa.miic.com.cn/";
    //设置拦截器
    private OkHttpClient httpClient = new OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .connectTimeout(60000, TimeUnit.MILLISECONDS)
            .addNetworkInterceptor(new AddCookiesInterceptor(MyApplication.getContext()))
            .addNetworkInterceptor(new SaveCookiesInterceptor(MyApplication.getContext()))
            .build();
    //步骤4:创建Retrofit对象
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(URI) // 设置 网络请求 Url
            .client(httpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build();
    // 步骤5:创建 网络请求接口 的实例
    public WXPostRequestInterface request = retrofit.create(WXPostRequestInterface.class);

    public  void CommonAsynPost(Call<String> call, Callback<String> callback){

        if(call!=null&& callback!=null){
            call.enqueue(callback);
        }
    }

    public static WXPostRequest Instance = new WXPostRequest();
}
