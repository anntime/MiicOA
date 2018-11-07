package com.example.miic.base.http;

import com.example.miic.common.MyApplication;
import com.example.miic.base.cookieSetting.AddCookiesInterceptor;
import com.example.miic.base.cookieSetting.SaveCookiesInterceptor;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by XuKe on 2018/1/16.
 */

public class PostRequest{
    //设置请求的URL
    private  String OA_URI="http://oa.miic.com.cn/";//"http://oa.miic.com.cn/";
    private  String SHARE_URI="http://share.miic.com.cn/";//"http://oa.miic.com.cn/";
    private String MANAGE_URI="http://sns.miic.com.cn";
    private String QJ_URI="http://qj.miic.com.cn";
    //设置拦截器
    private OkHttpClient httpClient = new OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .connectTimeout(60000, TimeUnit.MILLISECONDS)
			 .readTimeout(60000, TimeUnit.MILLISECONDS)
			 .writeTimeout(60000, TimeUnit.MILLISECONDS)
            //添加应用拦截器
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    //获取request
                    Request request = chain.request();
                    //获取request的创建者builder
                    Request.Builder builder = request.newBuilder();
                    //从request中获取headers，通过给定的键url_name
                    List<String> headerValues = request.headers("url_name");
                    if (headerValues != null && headerValues.size() > 0) {
                        //如果有这个header，先将配置的header删除，因此header仅用作app和okhttp之间使用
                        builder.removeHeader("url_name");
                        //匹配获得新的BaseUrl
                        String headerValue = headerValues.get(0);
                        HttpUrl newBaseUrl = null;
                        if ("oa".equals(headerValue)) {
                            newBaseUrl = HttpUrl.parse(OA_URI);
                        } else if ("share".equals(headerValue)) {
                            newBaseUrl = HttpUrl.parse(SHARE_URI);
                        } else if("manage".equals(headerValue)){
                            newBaseUrl = HttpUrl.parse(MANAGE_URI);
                        } else if("qj".equals(headerValue)){
                            newBaseUrl = HttpUrl.parse(QJ_URI);
                        }else{
                            newBaseUrl = HttpUrl.parse(OA_URI);
                        }
                        //从request中获取原有的HttpUrl实例oldHttpUrl
                        HttpUrl oldHttpUrl = request.url();
                        //重建新的HttpUrl，修改需要修改的url部分
                        HttpUrl newFullUrl = oldHttpUrl.newBuilder()
                                .scheme(newBaseUrl.scheme())
                                .host(newBaseUrl.host())
                                .port(newBaseUrl.port())
                                .build();
                        //重建这个request，通过builder.url(newFullUrl).build()；
                        //然后返回一个response至此结束修改
                        return
                                chain.proceed(builder.url(newFullUrl).build());
                    } else{
                        return chain.proceed(request);
                    }
                }
            })
            .addNetworkInterceptor(new AddCookiesInterceptor(MyApplication.getContext()))
            .addNetworkInterceptor(new SaveCookiesInterceptor(MyApplication.getContext()))
            .build();
    //步骤4:创建Retrofit对象
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(OA_URI) // 设置 网络请求 Url
            .client(httpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build();
    // 步骤5:创建 网络请求接口 的实例
    public PostRequestInterface request = retrofit.create(PostRequestInterface.class);

    public  void CommonAsynPost(Call<String> call, Callback<String> callback){

        if(call!=null&& callback!=null){
            call.enqueue(callback);
        }
    }

    public static PostRequest Instance = new PostRequest();
}
