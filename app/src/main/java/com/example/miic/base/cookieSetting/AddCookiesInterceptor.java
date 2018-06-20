package com.example.miic.base.cookieSetting;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by CCY on 2018/1/11.
 * 定义请求拦截器，如果该请求存在cookie，则为其添加到Header的Cookie中
 */

public class AddCookiesInterceptor implements Interceptor  {
    private static final String COOKIE_PREF = "cookies_prefs";
    private Context mContext;

    public AddCookiesInterceptor(Context context) {
        mContext = context;
    }

    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        String cookie = getCookie(request.url().toString(), request.url().host());
        //cookie = "SNS_FriendsUserThemeID=7E9E7B47-F184-465F-83F5-85FB69F8C330; SNS_UserThemeID=575CDF5E-0F38-4BFE-9731-4676CA4CA0CC; SNS_SocialCode=admin; SNS_ID=admin; SNS_One=78cb5f9530dcd3f5361133f4b2bb319a; SNS_UserType=0; SNS_UserUrl=http://sns.mictalk.cn/file/User/fed6a077-9c2b-4dcb-8388-d54246ca0f59.jpg; SNS_UserName=admin";
        if (!TextUtils.isEmpty(cookie)) {
            builder.addHeader("Cookie", cookie);
        }

        return chain.proceed(builder.build());
    }

    private String getCookie(String url, String domain) {
        SharedPreferences sp = mContext.getSharedPreferences(COOKIE_PREF, Context.MODE_PRIVATE);
        if (!TextUtils.isEmpty(url)&&sp.contains(url)&&!TextUtils.isEmpty(sp.getString(url,""))) {
            return sp.getString(url, "");
        }
        if (!TextUtils.isEmpty(domain)&&sp.contains(domain) && !TextUtils.isEmpty(sp.getString(domain, ""))) {
            return sp.getString(domain, "");
        }

        return null;
    }



}
