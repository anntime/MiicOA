package com.example.miic.share.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.base.http.PostRequest;
import com.example.miic.base.http.SocialPostRequest;
import com.example.miic.base.loadmore.LoadMoreListView;
import com.example.miic.base.loadmore.OnLoadMoreScrollListener;
import com.example.miic.common.MyApplication;
import com.example.miic.oa.common.FormatCurrentData;
import com.example.miic.oa.common.Setting;
import com.example.miic.oa.news.activity.NewsCommentsActivity;
import com.example.miic.oa.news.activity.NewsDetailActivity;
import com.example.miic.oa.news.item.InfoPageNewsComment;
import com.example.miic.share.adapter.ShareMomentsCommentItemAdapter;
import com.example.miic.share.adapter.ShareMomentsItemAdapter;
import com.example.miic.share.item.MomentCommentItem;
import com.example.miic.share.item.ShareMomentsCommentItem;
import com.example.miic.share.item.ShareMomentsItem;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Header;

import static com.example.miic.oa.common.LvHeightUtil.setListViewHeightBasedOnChildren;
import static com.example.miic.oa.common.Setting.stampToDate;


public class ShareActivity extends AppCompatActivity {

    //总共页数
    private int mPageCount = 0;
    //每页信息数量
    private int mPageSize = 20;
    //当前页
    private int mCurrentPage=0;
    private List<ShareMomentsItem> shareMomentsItemList = new ArrayList<>();
    private ShareMomentsItemAdapter shareMomentsItemAdapter;
    private LoadMoreListView momentsListView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView snsUserBgIv;
    private ImageView snsUserIconIv;
    private TextView snsUserNameTv;
    private SharedPreferences spSns;
    private Boolean isLike;
    private ShareMomentsItem currentMomentItem;
    //listView添加了header数量
    private int headerCount = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        spSns = getSharedPreferences("data", MODE_PRIVATE);
        setHeader();
        initView();
        getUserDetailInfo();
        //列表填充数据
        shareMomentsItemAdapter =new ShareMomentsItemAdapter(ShareActivity.this,shareMomentsItemList);
        momentsListView.setAdapter(shareMomentsItemAdapter);
        setListViewHeightBasedOnChildren(momentsListView);
        getMomentsCount();
        //adapter item点击点赞、评论事件：position是数据项的位置=itemIdex-headerCount
        shareMomentsItemAdapter.setOnItemMyClickListener(new ShareMomentsItemAdapter.onItemMyClickListener() {
            public void onMyClick(int type, int position,int i) {
                ShareMomentsItem momentsEntity = shareMomentsItemList.get(position);
                if (type == 0) {
                    //点赞点击
                    praiseMoments(position);
                } else if (type == 1) {
                    //评论点击,显示评论编辑框
                    showEditor(position);
                } else if (type==2){
                    //详情页
                    if(momentsEntity.getMomentTitle().equals("")){
                        Toast.makeText(ShareActivity.this,"短篇文章没有详情页！",Toast.LENGTH_SHORT).show();
                    }else {
                        currentMomentItem = momentsEntity;
                        Log.i("长篇文章：",currentMomentItem.getMomentTitle());
                        EventBus.getDefault().postSticky( currentMomentItem);
                        Intent intent=new Intent(ShareActivity.this,MomentsDetailActivity.class);
                        startActivityForResult(intent,1);
                    }
                }else if (type==3){
                    //点击自己的评论
                    onMyItemClick(position,i);
                }else if(type==4){
                    //点击别人的评论
                    replyComment(position,i);
                }
            }
        });

    }
    private void setHeader() {
        TextView titleTv = (TextView) findViewById(R.id.page_title);
        LinearLayout backLin = (LinearLayout)findViewById(R.id.back);
        LinearLayout rightLin = (LinearLayout)findViewById(R.id.menu);
        ImageView menuImage = (ImageView)findViewById(R.id.menu_image);
        View titleLine = (View)findViewById(R.id.title_line);
        titleTv.setText(R.string.logo_str);
        titleLine.setBackgroundColor(getResources().getColor(R.color.activitycolor));
        //back event
        backLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        menuImage.setImageResource(R.drawable.edit);
        //send friends messgae
        rightLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShareActivity.this, MomentsEditActivity.class);
                startActivityForResult(intent,3);
            }
        });
    }
    private void initView() {
        momentsListView = (LoadMoreListView) findViewById(R.id.moments_container);
        //设置上拉加载，下拉刷新
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refreshview);
        View headerView = LayoutInflater.from(ShareActivity.this).inflate(R.layout.friends_circle_header, null);
        momentsListView.addHeaderView(headerView);
        snsUserBgIv = (ImageView) headerView.findViewById(R.id.sns_user_bg_iv);
        snsUserIconIv = (ImageView) headerView.findViewById(R.id.sns_user_icon_iv);
        snsUserNameTv = (TextView) headerView.findViewById(R.id.sns_user_name_tv);
        snsUserIconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ShareActivity.this,ShareFriendsActivity.class);
                startActivity(intent);
            }
        });
    }
    private void getUserDetailInfo(){
        JSONObject requestJson = new JSONObject();
        try{
            requestJson.put("userID",spSns.getString("userID",""));
        }catch (JSONException ex){
            Log.i("ShareActivity","json对象构造错误");
        }
        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        String cookieStr = MyApplication.getCookieStr();
        Call<String> call = PostRequest.Instance.request.GetUserDetailInformation (cookieStr,keywordView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try{
                    Log.i("ShareActivity",response.body());
                    String jsonObject1 = new JSONObject(response.body()).getString("d");
                    Log.i("ShareActivity",jsonObject1.toString());
                    if(! new JSONObject(response.body()).isNull("d")){
                        JSONObject jsonObject =new JSONObject ((new JSONObject(response.body())).getString("d"));
                        Log.i("ShareActivity",jsonObject.toString());
                        Picasso.with(ShareActivity.this).load(jsonObject.getJSONObject("SocialUserInfo").getString("MicroUserUrl")).into(snsUserIconIv);
                        snsUserNameTv.setText(jsonObject.getJSONObject("UserInfo").getString("UserName"));
                        Log.i("ShareActivity",jsonObject.getJSONObject("SocialUserInfo").getString("MicroUserUrl"));
                        Log.i("ShareActivity",jsonObject.getJSONObject("UserInfo").getString("UserName"));
                    }
                }catch (JSONException ex){
                    Log.i("ShareActivity","json对象构造错误");
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ShareActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    private void getMomentsCount(){
        JSONObject requestJson = new JSONObject();
        try{
            JSONObject val1 = new JSONObject();
            val1.put("Year","");
            val1.put("Month","");
            requestJson.put("dateView",val1);
        }catch (JSONException ex){
            Log.i("InfoChildFragment","json对象构造错误");
        }
        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        String cookieStr = MyApplication.getCookieStr();
        Call<String> call = PostRequest.Instance.request.GetSearchMyMomentsWithCommentCount (cookieStr,keywordView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println("count是：" + response.body());
                if(response.body()!=null){
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
                            getMoments(true);
                        }else{
                            swipeRefreshLayout.setVisibility(View.GONE);
                        }

                    }catch (JSONException ex){
                        Log.i("ShareActivity","json对象构造错误");
                    }
                }

            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ShareActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    private void getMoments(boolean isFirstLoad){
        if(isFirstLoad==true){
            shareMomentsItemList.clear();
        }
        JSONObject requestJson = new JSONObject();
        try{
            JSONObject val1 = new JSONObject();
            val1.put("pageStart",mCurrentPage * mPageSize + 1);
            val1.put("pageEnd", (mCurrentPage + 1) *mPageSize);
            requestJson.put("page",val1);
            JSONObject val2= new JSONObject();
            Calendar cal = Calendar.getInstance();
            val2.put("Year",cal.get(Calendar.YEAR)+"");
//            val2.put("Year","");
            val2.put("Month","");
            requestJson.put("dateView",val2);
            mCurrentPage=mCurrentPage+1;
        }catch (JSONException ex){
            Log.i("NewsDetailActivity","json对象构造错误");
        }
        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        String cookieStr = MyApplication.getCookieStr();
        Call<String> call = PostRequest.Instance.request.SearchMyMomentsWithComment (cookieStr,keywordView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try{
                    System.out.println("body:" + response.body());
                    if(response.body()!=null){
                        String jsonStrTemp =(new JSONObject(response.body())).getString("d");
                        System.out.println("body:" + jsonStrTemp);
                        if(jsonStrTemp!=null||!jsonStrTemp.equals("[]")){
                            JSONArray arrayTemp = new JSONArray(jsonStrTemp);
                            for (int i=0;i<arrayTemp.length();i++){
                                JSONObject objectTemp = arrayTemp.getJSONObject(i);
                                shareMomentsItemList.add(new ShareMomentsItem(objectTemp));
                            }
                            if(momentsListView.getFooterViewsCount() ==0){
                                momentsListView.setFooterView(View.inflate(ShareActivity.this, R.layout.item_listview_footer_loadmore, null),
                                        new OnLoadMoreScrollListener.OnLoadMoreStateListener() {
                                            @Override
                                            public void onNormal(View footView) {
                                                if(mPageCount==1){
                                                    footView.findViewById(R.id.footer_pb_loading).setVisibility(View.GONE);
                                                    ((TextView) footView.findViewById(R.id.footer_tv_msg)).setText("没有更多的消息了");
                                                }else{
                                                    footView.findViewById(R.id.footer_pb_loading).setVisibility(View.GONE);
                                                    ((TextView) footView.findViewById(R.id.footer_tv_msg)).setText("加载更多消息");
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
                            }
                            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                @Override
                                public void onRefresh() {
                                    reFresh();
                                    shareMomentsItemAdapter.notifyDataSetChanged();
                                }
                            });
                            momentsListView.requestFocus();
                            swipeRefreshLayout.setVisibility(View.VISIBLE);
                        }else{
                            swipeRefreshLayout.setVisibility(View.GONE);
                            Toast.makeText(ShareActivity.this,"暂无数据！请稍后再试！",Toast.LENGTH_SHORT).show();
                        }
                        shareMomentsItemAdapter.notifyDataSetChanged();
                    }else{
                       Toast.makeText(ShareActivity.this,"暂无数据，请稍后再试！",Toast.LENGTH_SHORT).show();
                    }

                }catch (JSONException ex){
                    Log.i("ShareActivity","json对象构造错误");
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ShareActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }

    //返回这个活动时，刷新页面
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                break;
            default:
                //updateCommentAndPraise();
                reFresh();
                break;
        }
    }
    /**
     *下拉刷新事件定义
     */
    private void reFresh() {
        shareMomentsItemList.clear();
        momentsListView.setEnd(false);
        mCurrentPage = 0;
        getMomentsCount();
        swipeRefreshLayout.setRefreshing(false);
    }
    /**
     * 上滑加载事件定义
     */
    private void loadMore() {
        Log.i("pageCount",mPageCount+"");
        Log.i("currentPage",mCurrentPage+"");
        if (mPageCount > 1 && mCurrentPage<mPageCount) {
            getMoments(false);
        } else {
            ShareActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    momentsListView.setEnd(true);
                }
            });
        }
        ShareActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                shareMomentsItemAdapter.notifyDataSetChanged();
            }
        });
    }
    //点赞操作
    public void praiseMoments(final int position){
        JSONObject requestJson = new JSONObject();
        final ShareMomentsItem item = shareMomentsItemList.get(position);
        isLike = item.getIsLike();
        try{
            requestJson.put("publishID",item.getMomentID());
        }catch (JSONException ex){
            Log.i("ShareActivity","json对象构造错误");
        }
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.PraiseMoments(MyApplication.getCookieStr(),requestBody);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println("body:" + response.body());
                if(response.body()!=null){
                    try{
                        //将字符串转换成jsonObject对象
                        JSONObject result = new JSONObject(response.body()).getJSONObject("d");
                        //获取对应的值
                        boolean praiseResult = result.getBoolean("result");
                        if(praiseResult==true){
                            //点赞成功
                            Log.i("ShareActivity","点赞成功");
                            int count = Integer.parseInt(item.getLikeNum());
                            if (isLike==true) {
                                //取消赞
                                count--;
                                isLike =!isLike;
                            } else {
                                //点赞
                                count++;
                                isLike =!isLike;
                            }
                            //更新
                            item.setIsLike(isLike);
                            item.setLikeNum(count+"");
                            //刷新
                            shareMomentsItemAdapter.updateView(momentsListView, position, headerCount);
                        }else{
                            //点赞失败
                            Log.i("ShareActivity","点赞失败");
                        }
                    }catch (JSONException ex){
                        Log.i("ShareActivity","json对象构造错误");
                    }
                }else{
                    Toast.makeText(ShareActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ShareActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //评论操作
    public void showEditor(final int position){
        View view1 = LayoutInflater.from(this).inflate(R.layout.comment_edit_pop_window,null);//PopupWindow对象
        final PopupWindow popupWindow=new PopupWindow(view1, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
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
        final InputMethodManager imm = (InputMethodManager) getSystemService(NewsCommentsActivity.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view1,InputMethodManager.SHOW_FORCED);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(ShareActivity.this.getCurrentFocus()
                        .getWindowToken(), InputMethodManager.SHOW_FORCED);
        //实例化控件
        final EditText replyCommentEdit= (EditText) view1.findViewById(R.id.comment_reply);
        replyCommentEdit.setHint("请输入…");
        replyCommentEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == keyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    //隐藏键盘
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(ShareActivity.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    // 提交评论
                    String content = replyCommentEdit.getText().toString();
                    commentMoments(content,position,popupWindow);
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
    private void commentMoments(String content, final int position, final PopupWindow popupWindow){
        JSONObject requestJson = new JSONObject();
        final ShareMomentsItem momentsItem = shareMomentsItemList.get(position);
        //        {commentView:{"PublishID":"65875148-6931-927c-e2d3-3f601aaaea35","Content":"？",
        // "ToCommenterID":"c98a5e43-7d06-1d60-b3a7-aada86ac317a","ToCommenterName":"常成月"}}
        try{
            JSONObject val1 = new JSONObject();
            val1.put("PublishID",momentsItem.getMomentID());
            val1.put("Content",content);
            val1.put("ToCommenterID",momentsItem.getCreaterID());
            val1.put("ToCommenterName",momentsItem.getCreaterName());
            requestJson.put("commentView",val1);
        }catch (JSONException ex){
            Log.i("ShareActivity","json对象构造错误");
        }
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.CommentMoments(MyApplication.getCookieStr(),requestBody);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try{
                        //将字符串转换成jsonObject对象
                        JSONObject result = new JSONObject(response.body()).getJSONObject("d");
                        //获取对应的值
                        boolean praiseResult = result.getBoolean("result");
                        if(praiseResult==true){
                            //评论成功
                            Log.i("ShareActivity","评论成功");
                            Toast.makeText(ShareActivity.this,"评论成功",Toast.LENGTH_SHORT).show();
                            int count = Integer.parseInt(momentsItem.getCommentNum());
                            count++;
                            //更新
                            momentsItem.setCommentNum(count+"");
                            Log.i("评论数量：",count+"");
                            popupWindow.dismiss();
                            //更新评论
                            //刷新
                            shareMomentsItemAdapter.updateView(momentsListView, position, headerCount);
                            getCommentCount(momentsItem,position);
                        }else{
                            //点赞失败
                            Log.i("MomentsDetailActivity","评论失败");
                        }
                    }catch (JSONException ex){
                        Log.i("MomentsDetailActivity","json对象构造错误");
                    }
                }else{
                    Toast.makeText(ShareActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ShareActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    //点击自己的评论
    //第一个参数是朋友圈列表的位置,第二个参数是评论列表的位置
    private void onMyItemClick(final int i,final int position){
        View view1 = LayoutInflater.from(this).inflate(R.layout.comment_pop_window,null);//PopupWindow对象
        final PopupWindow popupWindow=new PopupWindow(view1, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);

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
        View rootView =LayoutInflater.from(this).inflate(R.layout.activity_share, null);
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
                List<ShareMomentsCommentItem> ItemList= shareMomentsItemList.get(i).getShareMomentsCommentItemList();
                if (position<ItemList.size()) {
                    //删除该条评论
                    ShareMomentsCommentItem comment = ItemList.get(position);
                    String commentID = comment.getCommentID();
                    removeComment(commentID,i,position,popupWindow);
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
    //第2个参数是朋友圈列表的位置,第3个参数是评论列表的位置
    private void removeComment(final String commentID,final int i, final int position,final PopupWindow window){
        JSONObject requestJson = new JSONObject();
        try{
            requestJson.put("commentID",commentID);
        }catch (JSONException ex){
            Log.i("MomentsDetailActivity","json对象构造错误");
        }
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.RemoveMomentsComment (MyApplication.getCookieStr(),requestBody);
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
                            ShareMomentsItem moment = shareMomentsItemList.get(i);
                            //移除评论，关闭pop，刷新页面
                            moment.getShareMomentsCommentItemList().remove(position);
                            //关闭PopupWindow
                            window.dismiss();
                            // 刷新布局
                            moment.getShareMomentsCommentItemAdapter().notifyDataSetChanged();
                        }else{
                            //删除评论失败
                            Log.i("NewsCommentsActivity","删除评论失败");
                            Toast.makeText(MyApplication.getContext(),"删除评论失败！请稍后再试。",Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException ex){
                        Log.i("MomentsDetailActivity","json对象构造错误");
                    }
                }else{
                    Toast.makeText(MyApplication.getContext(),"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(MyApplication.getContext(),"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //回复别人的评论 ;第一个参数是朋友圈列表的位置,第二个参数是评论列表的位置
    private void replyComment(final int momentID,final int commentID){
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
        View rootView =LayoutInflater.from(this).inflate(R.layout.activity_share, null);
        popupWindow.showAtLocation(rootView, Gravity.CENTER,0,0);
        InputMethodManager imm = (InputMethodManager) getSystemService(NewsCommentsActivity.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view1,InputMethodManager.SHOW_FORCED);
        //实例化控件
        final EditText replyCommentEdit= (EditText) view1.findViewById(R.id.comment_reply);
        replyCommentEdit.setHint("回复"+shareMomentsItemList.get(momentID).getShareMomentsCommentItemList().get(commentID).getFromCommenterName());

        replyCommentEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == keyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    //隐藏键盘
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(ShareActivity.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    // 提交评论
                    String content = replyCommentEdit.getText().toString();
                    replyCommentMoments(content,momentID,commentID,popupWindow);
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
    public void replyCommentMoments(String content,final int momentID,final int commentID, final PopupWindow window){
        JSONObject requestJson = new JSONObject();
        final ShareMomentsItem shareMomentsItem = shareMomentsItemList.get(momentID);
        ShareMomentsCommentItem commentItem = shareMomentsItem.getShareMomentsCommentItemList().get(commentID);
        try{
            JSONObject commentView  = new JSONObject();
            commentView.put("PublishID",shareMomentsItem.getMomentID());
            commentView.put("Content",content);
            commentView.put("ToCommenterID",commentItem.getFromCommenterID());
            commentView.put("ToCommenterName",commentItem.getFromCommenterName());
            requestJson.put("commentView",commentView);
        }catch (JSONException ex){
            Log.i("ShareActivity","json对象构造错误");
        }
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.CommentMoments (MyApplication.getCookieStr(),requestBody);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println("body:" + response.body());
                if(response.body()!=null){
                    try{
                        //将字符串转换成jsonObject对象
                        JSONObject result = new JSONObject(response.body()).getJSONObject("d");
                        //获取对应的值
                        boolean praiseResult = result.getBoolean("result");
                        final String ID = result.getString("PrimaryID");
                        if(praiseResult==true){
                            //评论成功
                            Log.i("ShareActivity","回复评论成功");
                            Toast.makeText(ShareActivity.this,"回复评论成功",Toast.LENGTH_SHORT).show();
                            int count = Integer.parseInt(shareMomentsItem.getCommentNum());
                            count++;
                            shareMomentsItemList.get(momentID).setCommentNum(count+"");
                            shareMomentsItemAdapter.notifyDataSetChanged();
                            //关闭PopupWindow
                            window.dismiss();
                            //刷新
                            shareMomentsItemAdapter.updateView(momentsListView, momentID, headerCount);
                            //更新评论
                            getCommentCount(shareMomentsItem,momentID);
                        }else{
                            //点赞失败
                            Log.i("ShareActivity","回复评论失败");
                        }
                    }catch (JSONException ex){
                        Log.i("NewsCommentsActivity","json对象构造错误");
                    }
                }else{
                    Toast.makeText(ShareActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ShareActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //根据朋友圈id获取评论数量
    private void getCommentCount(final ShareMomentsItem shareMomentsItem,final int momentID){
        JSONObject requestJson = new JSONObject();
//        {commentSearchView:{"PublishID":"1f3b2aab-278d-21b0-dc27-53ee46b45b63","WithAddress":1},page:{"pageStart":1,"pageEnd":5}}
        try{
            JSONObject val1 = new JSONObject();
            val1.put("PublishID",shareMomentsItem.getMomentID());
            val1.put("WithAddress",1);
            requestJson.put("commentSearchView",val1);
        }catch (JSONException ex){
            Log.i("ShareActivity","json对象构造错误");
        }
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.GetCommentInfoCount(MyApplication.getCookieStr(),requestBody);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try{
                        List<ShareMomentsCommentItem> commentItemList = shareMomentsItem.getShareMomentsCommentItemList();
                        //将字符串转换成jsonObject对象
                        JSONObject result = new JSONObject(response.body());
                        if(result!=null){
                            int count = result.getInt("d");
                            if(count>0){
                                getCommentInfos(shareMomentsItem,momentID,count);
                            }
                        }
                    }catch (JSONException ex){
                        Log.i("ShareActivity","json对象构造错误");
                    }
                }else{
                    Toast.makeText(ShareActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ShareActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //根据朋友圈ID获取评论
    private void getCommentInfos(final ShareMomentsItem shareMomentsItem,final int momentID,final int count){
        JSONObject requestJson = new JSONObject();
//        {commentSearchView:{"PublishID":"1f3b2aab-278d-21b0-dc27-53ee46b45b63","WithAddress":1},page:{"pageStart":1,"pageEnd":5}}
        try{
            JSONObject val1 = new JSONObject();
            val1.put("PublishID",shareMomentsItem.getMomentID());
            val1.put("WithAddress",1);
            requestJson.put("commentSearchView",val1);
            JSONObject val2 = new JSONObject();
            val2.put("pageStart",1);
            val2.put("pageEnd",count);
            requestJson.put("page",val2);
        }catch (JSONException ex){
            Log.i("ShareActivity","json对象构造错误");
        }
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.GetCommentInfos(MyApplication.getCookieStr(),requestBody);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try{
                        List<ShareMomentsCommentItem> commentItemList = shareMomentsItem.getShareMomentsCommentItemList();
                        Log.i("请求信息格式2",response.body());
                        Log.i("请求信息格式2",new JSONObject(response.body()).getString("d"));
                        //将字符串转换成jsonObject对象
                        String str = new JSONObject(response.body()).getString("d");
                        JSONArray result = new JSONArray(str);
                        if(result!=null){
                            commentItemList.clear();
                            for (int i=0;i<result.length();i++){
                                commentItemList.add(new ShareMomentsCommentItem(result.getJSONObject(i),shareMomentsItem.getCreaterID()));
                            }
                            //刷新
                            Log.i("commentItemList",commentItemList.toString());
                            shareMomentsItem.setShareMomentsCommentItemList(commentItemList);
                            shareMomentsItemAdapter.updateView(momentsListView, momentID, headerCount);
                            shareMomentsItem.getShareMomentsCommentItemAdapter().notifyDataSetChanged();
                            setListViewHeightBasedOnChildren(momentsListView);
                        }else{
                            //点赞失败
                            Log.i("ShareActivity","评论失败");
                        }
                    }catch (JSONException ex){
                        Log.i("ShareActivity","json对象构造错误");
                    }
                }else{
                    Toast.makeText(ShareActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ShareActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
}
