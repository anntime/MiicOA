package com.example.miic.oa.search.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.base.http.PostRequest;
import com.example.miic.base.loadmore.LoadMoreListView;
import com.example.miic.base.loadmore.OnLoadMoreScrollListener;
import com.example.miic.oa.common.Setting;
import com.example.miic.oa.news.activity.NewsDetailActivity;
import com.example.miic.oa.search.adapter.SearchPageNewsAdapter;
import com.example.miic.oa.search.item.SearchPageNews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchActivity extends AppCompatActivity {

    private LinearLayout backBtn;
    private LoadMoreListView searchNewsListView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<SearchPageNews> searchNewsList;
    private SearchPageNewsAdapter searchNewsAdapter;
    private EditText keywordEditText;
    private String searchKeyword;
    private SharedPreferences sf;
    //总共页数
    private int mPageCount = 0;
    //每页信息数量
    private int mPageSize = 10;
    //当前页
    private int mCurrentPage=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        sf = SearchActivity.this.getSharedPreferences("data",SearchActivity.MODE_PRIVATE);
        backBtn = (LinearLayout) findViewById(R.id.search_back);
        backBtn.setOnClickListener(backClickListener);
        searchNewsListView = (LoadMoreListView)findViewById(R.id.news_comments_container);
        //设置上拉加载，下拉刷新
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refreshview);

        keywordEditText = (EditText)findViewById(R.id.search_keywork);
        Drawable drawable=getResources().getDrawable(R.drawable.find);
        drawable.setBounds(0,0,35,35);
        keywordEditText.setCompoundDrawables(drawable,null,null,null);
        searchKeyword = keywordEditText.getText().toString();
        //搜索
        keywordEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == keyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    //隐藏键盘
                    ((InputMethodManager) SearchActivity.this.getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(SearchActivity.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    // 提交
                    getNewsCountByColumnID(true);
                    keywordEditText.clearFocus();
                }
                return false;
            }
        });
        //新闻评论列表填充数据
        searchNewsList = new ArrayList<>();
        searchNewsAdapter = new SearchPageNewsAdapter(SearchActivity.this,searchNewsList);
        searchNewsListView.setAdapter(searchNewsAdapter);
//        String searchNewsStr=sf.getString("searchNewsList","");
//        if(!searchNewsStr.equals("")) {
//            try{
//                JSONArray arrayTemp = add JSONArray(searchNewsStr);
//                for (int i=0;i<arrayTemp.length();i++){
//                    JSONObject objectTemp = arrayTemp.getJSONObject(i);
//                    searchNewsList.add(add SearchPageNews(objectTemp) );
//                }
//                mPageCount = sf.getInt("mPageCount",0);
//                mCurrentPage = sf.getInt("mCurrentPage",0);
//            }catch (JSONException ex){
//                Log.i("SearchActivity","json对象构造错误");
//            }
//
//        } else {
//            getNewsCountByColumnID(true);
//        }
    }
    //返回按钮绑定事件
    private View.OnClickListener backClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
//            moveTaskToBack(true);
        }
    };

    //进行http请求，获取查询结果
    private void getSearchNews(boolean isFirstLoad){
        if(isFirstLoad==true){
            searchNewsList.clear();
        }
        JSONObject requestJson = new JSONObject();
        try{
            JSONObject val1 = new JSONObject();
            val1.put("pageStart",mCurrentPage * mPageSize + 1);
            val1.put("pageEnd", (mCurrentPage + 1) *mPageSize);
            requestJson.put("page",val1);
            JSONObject val2 = new JSONObject();
            val2.put("keyword",searchKeyword);
            requestJson.put("keywordView",val2);
            //当前页
            mCurrentPage=mCurrentPage+1;
        }catch (JSONException ex){
            Log.i("SearchActivity","json对象构造错误");
        }
        RequestBody searchView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.GetSearchResultList (searchView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try{
                    if(response.body()!=null){
                        String jsonStrTemp =(new JSONObject(response.body())).getString("d");
                        System.out.println("body:" + jsonStrTemp);
                        JSONArray arrayTemp = new JSONArray(jsonStrTemp);
                        for (int i=0;i<arrayTemp.length();i++){
                            JSONObject objectTemp = arrayTemp.getJSONObject(i);
                            searchNewsList.add(new SearchPageNews(objectTemp) );
                        }
//                        String searchNewsStr=sf.getString("searchNewsList","");
//                        if(searchNewsStr.equals("")) {
//                            SharedPreferences.Editor editor=sf.edit();
//                            editor.putString("searchNewsList",arrayTemp.toString());
//                            editor.putInt("mPageCount",mPageCount);
//                            editor.putInt("mCurrentPage",mCurrentPage);
//                            System.out.println("SharedPreferences:" + mPageCount);
//                            System.out.println("SharedPreferences:" + mCurrentPage);
//                            editor.commit();
//                        }

                        if(searchNewsListView.getFooterViewsCount() ==0){
                            searchNewsListView.setFooterView(View.inflate(SearchActivity.this, R.layout.item_listview_footer_loadmore, null), new OnLoadMoreScrollListener.OnLoadMoreStateListener() {
                                @Override
                                public void onNormal(View footView) {
                                    footView.findViewById(R.id.footer_pb_loading).setVisibility(View.GONE);
                                    footView.findViewById(R.id.footer_tv_msg).setVisibility(View.GONE);
//                                    if(mPageCount==1){
//                                        Log.i("mPageCount",mPageCount+"!!!");
//                                        footView.findViewById(R.id.footer_pb_loading).setVisibility(View.GONE);
//                                        ((TextView) footView.findViewById(R.id.footer_tv_msg)).setText("没有更多的新闻了");
//                                    }else{
//                                        footView.findViewById(R.id.footer_pb_loading).setVisibility(View.GONE);
//                                        ((TextView) footView.findViewById(R.id.footer_tv_msg)).setText("加载更多新闻");
//                                    }
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
                                    ((TextView) footView.findViewById(R.id.footer_tv_msg)).setText("没有更多的评论了");
                                    new Handler().postDelayed(new Runnable() {

                                        @Override
                                        public void run() {
                                            //do something
                                            footView.findViewById(R.id.footer_tv_msg).setVisibility(View.GONE);
                                        }
                                    }, 3000);    //延时3s执行

                                }
                            });
                            //新闻评论绑定点击事件
                            searchNewsListView.setOnItemClickListener(itemClickListener);
                        }
                        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                            @Override
                            public void onRefresh() {
                                reFresh();
                                searchNewsAdapter.notifyDataSetChanged();
                            }
                        });
                    }else{
                        Toast.makeText(SearchActivity.this,"暂无搜索推荐！",Toast.LENGTH_SHORT).show();
                    }
                    searchNewsAdapter.notifyDataSetChanged();

                }catch (JSONException ex){
                    Log.i("InfoChildFragment","json对象构造错误");
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(SearchActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    /**
     * 新闻点击事件
     */
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            if(position<searchNewsList.size()){
                //通过view获取其内部的组件，进而进行操作
                String clickTitle = (String) ((TextView)view.findViewById(R.id.news_title)).getText();
                String clickNewsID="";
                String publishType="";
                for(int i=0;i<searchNewsList.size();i++){
                    SearchPageNews news = searchNewsList.get(i);
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
                    Intent intent=new Intent(SearchActivity.this,NewsDetailActivity.class);
                    intent.putExtra("clickNewsID",clickNewsID);
                    startActivityForResult(intent,1);
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
    /**
     *下拉刷新事件定义
     */
    private void reFresh() {
        searchNewsList.clear();
        searchNewsListView.setEnd(false);
        mCurrentPage = 0;
        getSearchNews(false);
        swipeRefreshLayout.setRefreshing(false);
    }
    /**
     * 上滑加载事件定义
     */
    private void loadMore() {
        if (mPageCount > 1 && mCurrentPage<mPageCount) {
            getSearchNews(false);
        } else {
            SearchActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    searchNewsListView.setEnd(true);
                }
            });
        }
        SearchActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                searchNewsAdapter.notifyDataSetChanged();
            }
        });
    }


    //获取新闻数量
    public void getNewsCountByColumnID(final boolean isFirstLoad){
        JSONObject requestJson = new JSONObject();
        try{
            JSONObject val = new JSONObject();
            val.put("Keyword",searchKeyword);
            requestJson.put("keywordView",val);
        }catch (JSONException ex){
            Log.i("InfoChildFragment","json对象构造错误");
        }
        RequestBody newsInfoView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.GetSearchResultCount (newsInfoView);
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
                            getSearchNews(isFirstLoad);
                            //当前页
                            //mCurrentPage=mCurrentPage+1;
                        }
                    }catch (JSONException ex){
                        Log.i("SearchActivity","json对象构造错误");
                    }
                }else{
                    Toast.makeText(SearchActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(SearchActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
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
                                    Setting.downLoadSingle(link, title,SearchActivity.this);
                                }
                            }
                        }
                    }catch (JSONException ex){
                        Log.i("NewsDetailActivity","json对象构造错误");
                        Toast.makeText(SearchActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(SearchActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(SearchActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
