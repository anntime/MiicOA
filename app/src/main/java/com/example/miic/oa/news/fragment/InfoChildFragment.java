package com.example.miic.oa.news.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.base.http.PostRequest;
import com.example.miic.base.loadmore.LoadMoreListView;
import com.example.miic.base.loadmore.OnLoadMoreScrollListener;
import com.example.miic.common.MyApplication;
import com.example.miic.oa.common.Setting;
import com.example.miic.oa.news.activity.NewsDetailActivity;
import com.example.miic.oa.news.adapter.InfoPageNewsAdapter;
import com.example.miic.oa.news.item.InfoPageNews;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by XuKe on 2018/1/29.
 */

public class InfoChildFragment extends Fragment{

    private static Context mContext;
    private View rootView;
    private List<InfoPageNews> newsList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private LoadMoreListView newsListView;
    private InfoPageNewsAdapter newsAdapter;
    private TextView NewsTips;
    private LinearLayout process;
    //总共页数
    private int mPageCount = 0;
    //每页信息数量
    private int mPageSize = 10;
    //当前页
    private int mCurrentPage=0;
    //新闻栏目ID
    private String ColumnID;
    private Boolean isFull = true;

    @SuppressLint("ValidFragment")
    public InfoChildFragment(String columnID) {
        this.mContext = MyApplication.getContext();
        this.ColumnID = ColumnID;
    }
    public InfoChildFragment( ) {
        this.mContext = MyApplication.getContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (null != rootView) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (null != parent) {
                parent.removeView(rootView);
            }
        } else {
            rootView = inflater.inflate(R.layout.info_child_fragment, container, false);
            // 控件初始化
            NewsTips = rootView.findViewById(R.id.news_tips);
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
            //新闻列表填充数据
            newsListView = (LoadMoreListView) rootView.findViewById(R.id.news_container);
            //设置上拉加载，下拉刷新
            swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refreshview);

            newsAdapter =new InfoPageNewsAdapter(mContext,newsList);
            newsListView.setAdapter(newsAdapter);
        }

        return rootView;
    }
    //EventBus接受数据的函数
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String data) {
        Log.i("传递参数",data);
        String[] datas = data.split("<TAG><TAG>");
        String responce = datas[0];
        String columnID = datas[1];
        if(datas[2].equals("1")){
            isFull = false;
        }
        ColumnID = columnID;
        newsList.clear();
        getNewsCountByColumnID(responce,columnID);
//        EventBus.getDefault().unregister(this);
    }
    /**
     *下拉刷新事件定义
     */
    private void reFresh() {
        newsList.clear();
        newsListView.setEnd(false);
        mCurrentPage = 0;
        getNewsByColumnID(ColumnID);
        swipeRefreshLayout.setRefreshing(false);
    }
    /**
     * 上滑加载事件定义
     */
    private void loadMore() {
        if (mPageCount > 1&&mCurrentPage<mPageCount) {
            getNewsByColumnID(ColumnID);
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    newsListView.setEnd(true);
                }
            });
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                newsAdapter.notifyDataSetChanged();
            }
        });
    }
    /**
     * 新闻点击事件
     */
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            if(position<newsList.size()){
                //通过view获取其内部的组件，进而进行操作
                String clickTitle = (String) ((TextView)view.findViewById(R.id.news_title)).getText();
                String clickNewsID="";
                String publishType="";
                for(int i=0;i<newsList.size();i++){
                    InfoPageNews news = newsList.get(i);
                    if(news.getTitle()==clickTitle){
                        clickNewsID = news.getNewsID();
                        publishType = news.getPublishType();
                    }
                }
                //加判断，如果是连接，就直接用浏览器打开链接；如果是附件新闻，就直接下载文件
                if(publishType.equals("1")||publishType.equals("3")){
                    getDetailInfomation(clickNewsID);
                }else{
                    //获得点击的新闻的id，然后把这个id传到新的activity中。
                    Intent intent=new Intent(mContext,NewsDetailActivity.class);
                    intent.putExtra("clickNewsID",clickNewsID);
                    startActivityForResult(intent,1);
                    //getActivity().finish();
                }
            }

        }
    };
    //返回这个活动时，刷新页面
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                break;
            default:
                reFresh();
                break;
        }
    }
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
    public void onCreate(){
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }
    public void onResume(){
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    //上滑加载新闻
    public void getNewsByColumnID(String columnID){
        JSONObject requestJson = new JSONObject();
        //private JSONObject page = {"pageStart":mCurrentPage * mPageSize + 1,"pageEnd": (mCurrentPage + 1) *mPageSize};
        try{
            JSONObject val = new JSONObject();
            val.put("ColumnID",columnID);
            val.put("isFull",isFull);
            requestJson.put("keywordView",val);
            JSONObject val1 = new JSONObject();

            val1.put("pageStart",mCurrentPage * mPageSize + 1);
            val1.put("pageEnd", (mCurrentPage + 1) *mPageSize);
            requestJson.put("page",val1);
            Log.i("mCurrentPage!!!!!!!!",mCurrentPage+"");
            //当前页
            mCurrentPage=mCurrentPage+1;
        }catch (JSONException ex){
            Log.i("InfoChildFragment","json对象构造错误");
        }
        newsSearch(requestJson,false);
    }
    //获取新闻数量
    public void getNewsCountByColumnID(final String Responce,String columnID){
        process = (LinearLayout)rootView.findViewById(R.id.progress);
        process.setVisibility(View.VISIBLE);
        JSONObject requestJson = new JSONObject();
        try{
            JSONObject val = new JSONObject();
            val.put("ColumnID",columnID);
            val.put("isFull",isFull);
            requestJson.put("keywordView",val);
        }catch (JSONException ex){
            Log.i("InfoChildFragment","json对象构造错误");
        }
        RequestBody newsInfoView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.GetNewsSearchCountByColumn (newsInfoView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println("count是：" + response.body());
                if(response.body()!=null){
                    try{
                        JSONObject jsonObject = new JSONObject(response.body());
                        int newsCount = Integer.parseInt(jsonObject.getString("d"));
                        //判断新闻数量不等于0
                        if(newsCount!=0){
                            //总共页数
                            mPageCount = (int)Math.ceil(newsCount/20)+1;
                            //每页信息数量mPageSize = 20;
                            //当前页
                            mCurrentPage=1;
                        }
                        dealNewsResponce(Responce,true);
                        process.setVisibility(View.GONE);
                    }catch (JSONException ex){
                        Log.i("InfoChildFragment","json对象构造错误");
                    }
                }else{
                    Toast.makeText(mContext,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(mContext,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //处理新闻返回的字符串
    public void dealNewsResponce(String responce,boolean isFirstLoad){
        if(isFirstLoad==true){
            newsList.clear();
        }
        try {
            JSONObject jsonObject = new JSONObject(responce);
            String jsonStrTemp = jsonObject.getString("d");
            if(!jsonStrTemp.equals("[]")){
                JSONArray arrayTemp = new JSONArray(jsonStrTemp);
                for (int i=0;i<arrayTemp.length();i++){
                    JSONObject objectTemp = arrayTemp.getJSONObject(i);
                    newsList.add(new InfoPageNews(objectTemp));
                }
                if(newsListView.getFooterViewsCount() ==0){
                    newsListView.setFooterView(View.inflate(getActivity(), R.layout.item_listview_footer_loadmore, null), new OnLoadMoreScrollListener.OnLoadMoreStateListener() {
                        @Override
                        public void onNormal(View footView) {
                            footView.findViewById(R.id.footer_pb_loading).setVisibility(View.GONE);
                            footView.findViewById(R.id.footer_tv_msg).setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoading(View footView) {
                            footView.findViewById(R.id.footer_pb_loading).setVisibility(View.VISIBLE);
                            footView.findViewById(R.id.footer_tv_msg).setVisibility(View.VISIBLE);
                            ((TextView) footView.findViewById(R.id.footer_tv_msg)).setText("正在加载...");

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    loadMore();
                                }
                            }).start();
                        }
                        @Override
                        public void onEnd(final View footView) {
                            footView.findViewById(R.id.footer_pb_loading).setVisibility(View.GONE);
                            ((TextView) footView.findViewById(R.id.footer_tv_msg)).setText("没有更多的资讯了");
                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    //do something
                                    footView.findViewById(R.id.footer_tv_msg).setVisibility(View.GONE);
                                }
                            }, 3000);    //延时3s执行
                        }
                    });
                    //新闻绑定点击事件
                    newsListView.setOnItemClickListener(itemClickListener);
                }
                swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        reFresh();
                        newsAdapter.notifyDataSetChanged();
                    }
                });
                newsListView.requestFocus();
                NewsTips.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
            }else{
                NewsTips.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setVisibility(View.GONE);
            }
            newsAdapter.notifyDataSetChanged();
        }catch (JSONException ex){
            Log.e("InfoChildFragment",ex.getMessage());
            Toast.makeText(mContext,ex.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 进行http请求获取新闻列表
     */
    public void newsSearch(JSONObject requestJson, final boolean isFirstLoad){
        RequestBody newsInfoView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.NewsSearchByColumn(newsInfoView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                // 请求处理,输出结果
                // System.out.println("请求是：" + call.request());
                // System.out.println("header是：" + response.headers());
                System.out.println("body是：" + response.body());//.replaceAll("\\\\\"","\""));
                //初始化新闻列表（传递参数）
                dealNewsResponce(response.body(),isFirstLoad);
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(mContext,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }


    //浏览新闻
    public static void browseNews(String ClickID){
        JSONObject requestJson = new JSONObject();
        try{
            requestJson.put("newsID",ClickID);
        }catch (JSONException ex){
            Log.i("NewsDetailActivity","json对象构造错误");
        }
        RequestBody newsInfoView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.BrowseNews (newsInfoView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println("body:" + response.body());
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //进行http请求，获取新闻内容
    public void getDetailInfomation( final String clickNewsID){
        //浏览新闻
        browseNews(clickNewsID);
        JSONObject requestJson = new JSONObject();
        try{
            requestJson.put("id",clickNewsID);
        }catch (JSONException ex){
            Log.i("InfoChildFragment","json对象构造错误");
        }
        RequestBody newsInfoView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.GetDetailInformation (newsInfoView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println("body:" + response.body());
                //String res = response.body().replaceAll("//","<TAG>");
                if(response.body()!=null){
                    try{
                        JSONObject jsonObject =new JSONObject(new JSONObject(response.body()).getString("d"));
                        JSONObject newsInfo =jsonObject.getJSONObject("NewsInfo");
                        if(newsInfo!=null){
                            /**
                             * 新闻类型有四种：
                             * 1，文件，应该可以点击一个连接或者按钮进行下载
                             * 2，是个连接，这个用题目显示是个连接，点击使用手机自带的浏览器打开
                             * 3，图片，直接展示，最好是可以左右滑动的，如果不好做就做成图片罗列展示是个listView就可以吧。
                             * 4，文字，最简单的
                             * 此处只处理两种！！！！！
                             */
                            Setting setting= new Setting();
                            String publishType = newsInfo.getString("PublishType");
                            JSONArray accList = jsonObject.isNull("AccList") ? null : jsonObject.getJSONArray("AccList");
                             if(publishType.equals("1")){
                                //链接
                                if(newsInfo.getString("Url")!=null){
                                    Uri uri = Uri.parse(newsInfo.getString("Url").toString());
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent);
                                }
                            }else if(publishType.equals("3")){
                                //文件
                                //有附件
                                if (accList != null) {
                                    //获取附件，只会是一个
                                    String title = accList.getJSONObject(0).getString("FileName");
                                    String link = setting.getService() + accList.getJSONObject(0).getString("FilePath");
                                    //下载文件（不用显示进度条）
                                    Setting.downLoadSingle(link, title,getActivity());
                                }
                            }
                        }
                    }catch (JSONException ex){
                        Log.i("NewsDetailActivity","json对象构造错误");
                        Toast.makeText(mContext,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(mContext,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(mContext,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
}
