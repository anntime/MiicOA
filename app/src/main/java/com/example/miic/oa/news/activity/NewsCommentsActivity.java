package com.example.miic.oa.news.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.base.http.PostRequest;
import com.example.miic.base.loadmore.LoadMoreListView;
import com.example.miic.base.loadmore.OnLoadMoreScrollListener;
import com.example.miic.oa.common.FormatCurrentData;
import com.example.miic.oa.common.Setting;
import com.example.miic.oa.news.adapter.InfoPageNewsCommentAdapter;
import com.example.miic.oa.news.item.InfoPageNewsComment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NewsCommentsActivity extends AppCompatActivity{


    private LinearLayout NewsCommentBackBtn;
    //总共页数
    private int mPageCount = 0;
    //每页信息数量
    private int mPageSize = 20;
    //当前页
    private int mCurrentPage=0;
    private String NewsID;
    private List<InfoPageNewsComment> newsCommentList = new ArrayList<>();
    private InfoPageNewsCommentAdapter newsCommentAdapter;
    private LoadMoreListView newsCommentListView;
    private SwipeRefreshLayout swipeRefreshLayout;
    //暂无评论
    private TextView commentTip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_comments);
        //页面初始化
        NewsCommentBackBtn = (LinearLayout)findViewById(R.id.news_comment_back);
        NewsCommentBackBtn.setOnClickListener(backClickListener);
        //获取上一页面传递的参数
        Intent intent = getIntent();
        String clickNewsID = intent.getStringExtra("NewsID");
        NewsID = clickNewsID;
        newsCommentListView = (LoadMoreListView) findViewById(R.id.news_comments_container);
        //设置上拉加载，下拉刷新
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refreshview);
        getNewsCommentsCount();

        commentTip = (TextView)findViewById(R.id.news_comment_tips);
        //新闻评论列表填充数据
        newsCommentAdapter =new InfoPageNewsCommentAdapter(NewsCommentsActivity.this,R.layout.news_comment,newsCommentList);
        newsCommentListView.setAdapter(newsCommentAdapter);
    }

    //进行http请求，获取评论列表
    private void getNewsComments(boolean isFirstLoad){
        if(isFirstLoad==true){
            newsCommentList.clear();
        }
        JSONObject requestJson = new JSONObject();
        try{
            JSONObject val1 = new JSONObject();
            val1.put("pageStart",mCurrentPage * mPageSize + 1);
            val1.put("pageEnd", (mCurrentPage + 1) *mPageSize);
            requestJson.put("page",val1);
            requestJson.put("newsID",NewsID);
            mCurrentPage=mCurrentPage+1;
        }catch (JSONException ex){
            Log.i("NewsDetailActivity","json对象构造错误");
        }
        RequestBody newsInfoView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.GetCommentList (newsInfoView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try{
                    String jsonStrTemp =(new JSONObject(response.body())).getString("d");
                    System.out.println("body:" + jsonStrTemp);
                    if(jsonStrTemp!=null||jsonStrTemp!="[]"){
                        Setting setting = new Setting();
                        JSONArray arrayTemp = new JSONArray(jsonStrTemp);
                        for (int i=0;i<arrayTemp.length();i++){
                            JSONObject objectTemp = arrayTemp.getJSONObject(i);
                            String toCommenterName = objectTemp.getString("ToCommenterName");
                            JSONObject newsCommentJson = new JSONObject();
                            try{
                                newsCommentJson.put("ID",objectTemp.getString("ID"));
                                newsCommentJson.put("NewsID",objectTemp.getString("NewsID"));
                                newsCommentJson.put("FromCommenterUrl",objectTemp.getString("FromCommenterUrl"));
                                newsCommentJson.put("FromCommenterName",objectTemp.getString("FromCommenterName"));
                                newsCommentJson.put("FromCommenterID",objectTemp.getString("FromCommenterID"));
                                newsCommentJson.put("CommentTime",FormatCurrentData.getTimeRange(objectTemp.getString("CommentTime")));

                                String content = objectTemp.getString("Content");
                                Pattern p=Pattern.compile("/images/arclist/");//找的
                                Matcher m=p.matcher(content);//主字符串
                                while(m.find()){
                                    String[] contentArr = content.split(p.toString());
                                    System.out.println(contentArr);//abc
                                    List<Integer> tempInt = new ArrayList<>();
                                    List<String> tempStr = new ArrayList<>();
                                    String temp ;
                                    Setting set = new Setting();
                                    for(int g=0;g<contentArr.length;g++){
                                        Pattern p1=Pattern.compile(".gif");//找的
                                        Matcher m1=p1.matcher(contentArr[g]);//主字符串
                                        while(m1.find()){
                                            temp  = contentArr[g].split(p1.toString())[0];
                                            tempInt.add(set.getGif(temp));
                                            tempStr.add(temp);
                                        }
                                    }
                                    for (int t=0;t<tempInt.size();t++){
                                        content = content.replaceAll("../../images/arclist/"+tempStr.get(t)+".gif",tempInt.get(t).toString());
                                    }
                                }
                                newsCommentJson.put("Content", content);
                                Log.i("评论内容：",newsCommentJson.getString("Content"));
                                newsCommentJson.put("ToCommenterName",toCommenterName);
                                newsCommentJson.put("ToCommenterID",objectTemp.getString("ToCommenterID"));
                                Log.i("NewsCommentActivity",newsCommentJson.toString());
                            }catch (JSONException ex){
                                Log.i("NewsCommentActivity","json对象构造错误");
                            }
                            if(!toCommenterName.equals("")&&toCommenterName!=null){
                                toCommenterName = " 回复@"+toCommenterName;
                                newsCommentJson.put("ToCommenterName",toCommenterName);
                                newsCommentList.add(new InfoPageNewsComment(newsCommentJson));
                            }else{
                                newsCommentList.add(new InfoPageNewsComment(newsCommentJson));
                            }
                        }
                        if(newsCommentListView.getFooterViewsCount() ==0){
                            newsCommentListView.setFooterView(View.inflate(NewsCommentsActivity.this, R.layout.item_listview_footer_loadmore, null), new OnLoadMoreScrollListener.OnLoadMoreStateListener() {
                                @Override
                                public void onNormal(View footView) {
                                    if(mPageCount==1){
                                        Log.i("mPageCount",mPageCount+"!!!");
                                        footView.findViewById(R.id.footer_pb_loading).setVisibility(View.GONE);
                                        ((TextView) footView.findViewById(R.id.footer_tv_msg)).setText("没有更多的评论了");
                                    }else{
                                        footView.findViewById(R.id.footer_pb_loading).setVisibility(View.GONE);
                                        ((TextView) footView.findViewById(R.id.footer_tv_msg)).setText("加载更多评论");
                                    }
                                }
                                @Override
                                public void onLoading(View footView) {
                                    footView.findViewById(R.id.footer_pb_loading).setVisibility(View.VISIBLE);
                                    ((TextView) footView.findViewById(R.id.footer_tv_msg)).setText("正在加载...");
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            loadMore();
                                        }
                                    }).start();
                                }
                                @Override
                                public void onEnd(View footView) {
                                    footView.findViewById(R.id.footer_pb_loading).setVisibility(View.GONE);
                                    ((TextView) footView.findViewById(R.id.footer_tv_msg)).setText("没有更多的评论了");
                                }
                            });
                            //新闻评论绑定点击事件
                            newsCommentListView.setOnItemClickListener(itemClickListener);
                        }
                        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                            @Override
                            public void onRefresh() {
                                reFresh();
                                newsCommentAdapter.notifyDataSetChanged();
                            }
                        });
                        newsCommentListView.requestFocus();
                        swipeRefreshLayout.setVisibility(View.VISIBLE);
                    }else{
                        commentTip.setVisibility(View.VISIBLE);
                        swipeRefreshLayout.setVisibility(View.GONE);
                        Toast.makeText(NewsCommentsActivity.this,"暂无评论！请浏览其他栏目。",Toast.LENGTH_SHORT).show();
                    }
                    newsCommentAdapter.notifyDataSetChanged();
                }catch (JSONException ex){
                    Log.i("InfoChildFragment","json对象构造错误");
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(NewsCommentsActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //进行http请求，获取评论数量
    private void  getNewsCommentsCount(){
        JSONObject requestJson = new JSONObject();
        try{
            requestJson.put("newsID",NewsID);
        }catch (JSONException ex){
            Log.i("InfoChildFragment","json对象构造错误");
        }
        RequestBody newsInfoView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.GetCommentCount (newsInfoView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println("count是：" + response.body());
                try{
                    JSONObject jsonObject = new JSONObject(response.body());
                    int newsCount = Integer.parseInt(jsonObject.getString("d"));
                    //判断新闻评论数量不等于0
                    if(newsCount!=0){
                        //总共页数
                        mPageCount = (int)Math.ceil(newsCount/20)+1;
                        Log.i("pageCount",mPageCount+"");
                        //每页信息数量mPageSize = 20;
                        //当前页
                        mCurrentPage=0;
                        getNewsComments(true);
                    }else{
                        commentTip.setVisibility(View.VISIBLE);
                        swipeRefreshLayout.setVisibility(View.GONE);
                    }

                }catch (JSONException ex){
                    Log.i("NewsDetailActivity","json对象构造错误");
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(NewsCommentsActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //返回按钮绑定事件
    private View.OnClickListener backClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };
    /**
     * 评论点击事件
     */
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            if(position<newsCommentList.size()){
                //通过view获取其内部的组件，进而进行操作
                InfoPageNewsComment newsComment = newsCommentList.get(position);
                String commenter = newsComment.getFromCommenterName();
                SharedPreferences sf=getSharedPreferences("data",MODE_PRIVATE);
                String userCode=sf.getString("userCode","");
                if(commenter.equals(userCode)){
                    //是自己发表的评论，可以删除（下面弹出框：删除与取消）
                    onMyItemClick(position);
                }else{
                    //不是自己发表的评论，可以评论，跟软键盘一起出来一个编辑框
                    replyComment(position);
                }
            }

        }
    };
    public void onMyItemClick(final int i) {
        Log.i("新闻评论点击事件：","hanshu ");
        View view1 = LayoutInflater.from(this).inflate(R.layout.comment_pop_window,null);//PopupWindow对象
        final PopupWindow popupWindow=new PopupWindow(view1,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);

        //设置PopupWindow布局
        //popupWindow.setContentView(view1);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        // 设置PopupWindow是否能响应外部点击事件
        popupWindow.setOutsideTouchable(true);
        // 设置PopupWindow是否能响应点击事件
        popupWindow.setTouchable(true);
        //返回键点击，弹出
        popupWindow.setFocusable(true);
        popupWindow.update();
        //设置动画
        popupWindow.setAnimationStyle(R.style.shipping_popup_style);
        //在父布局的弹入/出位置
        View rootView =LayoutInflater.from(this).inflate(R.layout.activity_news_comments, null);
        popupWindow.showAtLocation(rootView, Gravity.CENTER,0,0);

        //实例化控件
        Button bt_cancel= (Button) view1.findViewById(R.id.comment_cancel);
        Button bt_delete= (Button) view1.findViewById(R.id.comment_delete);

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                if (i<newsCommentList.size()) {
                    //删除该条评论
                    InfoPageNewsComment newsComment = newsCommentList.get(i);
                    String commentID = newsComment.getCommentID();
                    removeComment(commentID,i,popupWindow);
                }
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
    }
    //删除评论
    private void removeComment(final String commentID,final int position, final PopupWindow window){
        JSONObject requestJson = new JSONObject();
        try{
            requestJson.put("id",commentID);
        }catch (JSONException ex){
            Log.i("NewsCommentActivity","json对象构造错误");
        }
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.RemoveComment (requestBody);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println("body:" + response.body());
                if(response.body()!=null){
                    try{
                        Boolean jsonStr =new JSONObject(response.body()).getBoolean("d");
                        if(jsonStr==true){
                            //删除评论成功
                            Log.i("NewsCommentsActivity","删除评论成功");
                            //移除评论，关闭pop，刷新页面
                            newsCommentList.remove(position);
                            //关闭PopupWindow
                            window.dismiss();
                            // 刷新布局
                            newsCommentAdapter.notifyDataSetChanged();
                        }else{
                            //删除评论失败
                            Log.i("NewsCommentsActivity","删除评论失败");
                            Toast.makeText(NewsCommentsActivity.this,"删除评论失败！请稍后再试。",Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException ex){
                        Log.i("NewsCommentsActivity","json对象构造错误");
                    }
                }else{
                    Toast.makeText(NewsCommentsActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(NewsCommentsActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }


    public void replyComment(final int i) {
        Log.i("新闻评论点击事件：","hanshu ");
        View view1 = LayoutInflater.from(this).inflate(R.layout.comment_edit_pop_window,null);//PopupWindow对象
        final PopupWindow popupWindow=new PopupWindow(view1,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);

        //设置PopupWindow布局
        //popupWindow.setContentView(view1);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        // 设置PopupWindow是否能响应外部点击事件
        popupWindow.setOutsideTouchable(true);
        // 设置PopupWindow是否能响应点击事件
        popupWindow.setTouchable(true);
        //返回键点击，弹出
        popupWindow.setFocusable(true);
        popupWindow.update();
        //设置动画
        popupWindow.setAnimationStyle(R.style.shipping_popup_style);
        //在父布局的弹入/出位置
        View rootView =LayoutInflater.from(this).inflate(R.layout.activity_news_comments, null);
        popupWindow.showAtLocation(rootView, Gravity.CENTER,0,0);
        InputMethodManager imm = (InputMethodManager) getSystemService(NewsCommentsActivity.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view1,InputMethodManager.SHOW_FORCED);
        //实例化控件
        final EditText replyCommentEdit= (EditText) view1.findViewById(R.id.comment_reply);
        replyCommentEdit.setHint("回复@"+newsCommentList.get(i).getFromCommenterName());

        replyCommentEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == keyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    //隐藏键盘
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(NewsCommentsActivity.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    // 提交评论
                    String content = replyCommentEdit.getText().toString();
                    replyCommentNews(content,i,popupWindow);
                }
                return false;
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
    }
    //新闻回复评论
    public void replyCommentNews(String content,int position, final PopupWindow window){
        JSONObject requestJson = new JSONObject();
        InfoPageNewsComment newsComment = newsCommentList.get(position);
        try{
            JSONObject toCommentView  = new JSONObject();
            toCommentView.put("NewsID",newsComment.getNewsID());
            toCommentView.put("Content",content);
            toCommentView.put("ToCommenterID",newsComment.getFromCommenterID());
            toCommentView.put("ToCommenterName",newsComment.getFromCommenterName());
            toCommentView.put("ResCommentID",newsComment.getCommentID());
            toCommentView.put("ResCommentContent",newsComment.getCommentContent());
            requestJson.put("toCommentView",toCommentView);
        }catch (JSONException ex){
            Log.i("NewsDetailActivity","json对象构造错误");
        }
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.ReplyComment (requestBody);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println("body:" + response.body());
                if(response.body()!=null){
                    try{
                        Boolean jsonStr =new JSONObject(response.body()).getBoolean("d");
                        if(jsonStr==true){
                            //回复评论成功
                            Log.i("NewsCommentsActivity","回复评论成功");
                            Toast.makeText(NewsCommentsActivity.this,"回复评论成功！",Toast.LENGTH_SHORT).show();
                            //刷新页面
                            newsCommentList.clear();
                            //关闭PopupWindow
                            window.dismiss();
                            // 刷新布局
                            getNewsComments(false);
                            newsCommentAdapter.notifyDataSetChanged();
                        }else{
                            //回复评论失败
                            Log.i("NewsCommentsActivity","回复评论失败");
                            Toast.makeText(NewsCommentsActivity.this,"评论失败！请稍后再试。",Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException ex){
                        Log.i("NewsCommentsActivity","json对象构造错误");
                    }
                }else{
                    Toast.makeText(NewsCommentsActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(NewsCommentsActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    /**
     *下拉刷新事件定义
     */
    private void reFresh() {
        newsCommentList.clear();
        newsCommentListView.setEnd(false);
        mCurrentPage = 0;
        getNewsCommentsCount();
        swipeRefreshLayout.setRefreshing(false);
    }
    /**
     * 上滑加载事件定义
     */
    private void loadMore() {
        Log.i("pageCount",mPageCount+"");
        Log.i("currentPage",mCurrentPage+"");
        if (mPageCount > 1 && mCurrentPage<mPageCount) {
            getNewsComments(false);
        } else {
            NewsCommentsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    newsCommentListView.setEnd(true);
                }
            });
        }
        NewsCommentsActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                newsCommentAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        newsCommentListView.requestFocusFromTouch();
    }
}
