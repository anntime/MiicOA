package com.example.miic.base.wxapi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by XuKe on 2018/1/16.
 */

public interface WXPostRequestInterface {


    //采用@Post表示Post方法进行请求（传入部分url地址）
    // 采用@FormUrlEncoded注解的原因:API规定采用请求格式x-www-form-urlencoded,即表单形式
    // 需要配合@Field使用

    //通过得到的CODE获取access_token
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @GET("oauth2/access_token")
    Call<String> GetAccessToken(@Query("appid") String appid ,@Query("secret") String secret,@Query("code") String code ,@Query("grant_type") String grant_type);

    //通过得到的CODE获取access_token
    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @GET("userinfo")
    Call<String> GetUserinfo(@Query("access_token") String access_token ,@Query("openid") String openid);
}
