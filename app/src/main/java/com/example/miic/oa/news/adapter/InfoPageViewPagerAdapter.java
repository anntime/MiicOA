package com.example.miic.oa.news.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.miic.base.http.PostRequest;
import com.example.miic.common.MyApplication;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by XuKe on 2018/1/29.
 */

public class InfoPageViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;
    private FragmentManager fragmentManager;
    private List<String> tags;

    public InfoPageViewPagerAdapter(FragmentManager fm, List<Fragment> fragments,List<String> tags) {
        super(fm);
        this.fragments = fragments;
        this.tags = tags;
        this.fragmentManager = fm;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    /**
     * 获取columnID，进行http请求获取新闻列表
     */
    public void newsSearchByColumn(final String columnID){
        JSONObject requestJson = new JSONObject();
        try{
            JSONObject val = new JSONObject();
            val.put("ColumnID",columnID);
            requestJson.put("keywordView",val);
            JSONObject val1 = new JSONObject();
            val1.put("pageStart","1");
            val1.put("pageEnd","10");
            requestJson.put("page",val1);
        }catch (JSONException ex){
            Log.i("InfoPageFragment","json对象构造错误");
            Toast.makeText(MyApplication.getContext(),ex.getMessage(),Toast.LENGTH_SHORT).show();
        }
        RequestBody newsInfoView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.NewsSearchByColumn(newsInfoView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                //初始化新闻列表（传递参数）
                EventBus.getDefault().post(response.body()+"<TAG><TAG>"+columnID);
            }

            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(MyApplication.getContext(),throwable.getMessage() + "------" + throwable.getCause(),Toast.LENGTH_SHORT).show();
             }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
