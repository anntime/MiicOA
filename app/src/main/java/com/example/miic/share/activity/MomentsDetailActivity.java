package com.example.miic.share.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.base.http.PostRequest;
import com.example.miic.common.MyApplication;
import com.example.miic.oa.news.activity.NewsCommentsActivity;
import com.example.miic.oa.news.activity.NewsDetailActivity;
import com.example.miic.share.adapter.MomentCommentItemAdapter;
import com.example.miic.share.adapter.ShareMomentsItemAdapter;
import com.example.miic.share.item.MomentCommentItem;
import com.example.miic.share.item.ShareMomentsCommentItem;
import com.example.miic.share.item.ShareMomentsItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.miic.oa.common.LvHeightUtil.setListViewHeightBasedOnChildren;

public class MomentsDetailActivity extends AppCompatActivity {

    private ShareMomentsItem momentsItem;
    /*短篇
    private TextView shortTitle;
    private TextView shortMomentsTime;
    private ImageView createrUrl;
    private TextView createrName;
    private TextView shortDetailTitle;
    private TextView shortDetailTime;
    */
    /*长篇*/
    private TextView LongTitle;
    private TextView LongTime;
    private HtmlTextView LongContent;
    private TextView DetailCommentCount;
    private TextView DetailHeartCount;
    private ImageView DetailGiveComment;
    private ImageView DetailGiveHeart;
    private EditText MomentComment;
    /*评论展示*/
    private ListView commentsListView;
    private LinearLayout commentsContainer;
    private List<MomentCommentItem> commentItemList = new ArrayList<>();
    private MomentCommentItemAdapter commentItemAdapter;
    private String authorID;
    /*还有附件的展示。。*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moments_detail);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        setHeader();
    }
    //EventBus接受数据的函数
    @Subscribe(threadMode = ThreadMode.MAIN, sticky=true)
    public void receiveMessage(ShareMomentsItem data) {
        Log.i("传递参数",data.getMomentTitle());
        momentsItem = data;
        authorID = momentsItem.getCreaterID();
        initView();


    }
    private void setHeader() {
        TextView titleTv = (TextView) findViewById(R.id.page_title);
        LinearLayout backLin = (LinearLayout)findViewById(R.id.back);
        LinearLayout rightLin = (LinearLayout)findViewById(R.id.menu);
        titleTv.setText("员工圈详情");
        //back event
        backLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //send friends messgae
        rightLin.setVisibility(View.INVISIBLE);
    }
    private void initView(){
        //列表填充数据
        commentItemAdapter =new MomentCommentItemAdapter(MomentsDetailActivity.this,commentItemList);
        LongTitle = (TextView)findViewById(R.id.news_detail_title);
        LongTime = (TextView)findViewById(R.id.long_time);
        LongContent = (HtmlTextView)findViewById(R.id.news_detail_content);
        DetailCommentCount = (TextView)findViewById(R.id.news_detail_comment_count);
        DetailHeartCount = (TextView)findViewById(R.id.news_detail_heart_count);
        DetailGiveComment = (ImageView)findViewById(R.id.give_comment);
        DetailGiveHeart = (ImageView)findViewById(R.id.give_heart);
        MomentComment = (EditText)findViewById(R.id.news_detail_input_comment);
        Drawable drawable=getResources().getDrawable(R.drawable.edit);
        drawable.setBounds(0,0,30,30);
        MomentComment.setCompoundDrawables(drawable,null,null,null);
        MomentComment.clearFocus();
        commentsListView = (ListView)findViewById(R.id.comment_list_view);
        commentsContainer = (LinearLayout)findViewById(R.id.comment_container);
        //获取上一页面传递的参数
        Log.i("LongTitle",momentsItem.getMomentTitle());
        LongTitle.setText(momentsItem.getMomentTitle());
        LongTime.setText(momentsItem.getMomentTime());
        String html = momentsItem.getDetailContent();
        LongContent.setHtml(html, new HtmlHttpImageGetter(LongContent, "http://share.miic.com.cn"));
        Log.i("朋友圈详情",momentsItem.getDetailContent());
        DetailCommentCount.setText(momentsItem.getCommentNum());
        DetailHeartCount.setText(momentsItem.getLikeNum());
        if(momentsItem.getIsLike()){
            DetailGiveHeart.setImageResource(R.drawable.red_heart);
        }else {
            DetailGiveHeart.setImageResource(R.drawable.heart);
        }
        initComments(momentsItem.getCommentList());
        DetailGiveComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //暂无
            }
        });
        DetailGiveHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点赞
                priseMoments();
            }
        });
        //评论事件
        MomentComment.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == keyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    //隐藏键盘
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(MomentsDetailActivity.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    // 提交评论
                    String content = MomentComment.getText().toString();
                    commentMoments(content);
                }
                return false;
            }
        });
    }
    private void priseMoments(){
        String publishID = momentsItem.getMomentID();
        JSONObject requestJson = new JSONObject();
        try{
            requestJson.put("publishID",publishID);
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
                            Log.i("MomentsDetailActivity","点赞成功");
                            int count = Integer.parseInt(momentsItem.getLikeNum());
                            Boolean isLike = momentsItem.getIsLike();
                            if(isLike){
                                //取消赞
                                count--;
                                DetailGiveHeart.setImageResource(R.drawable.heart);

                            }else {
                                //取消赞
                                count++;
                                DetailGiveHeart.setImageResource(R.drawable.red_heart);
                            }
                            DetailHeartCount.setText(count+"");
                        }else{
                            //点赞失败
                            Log.i("MomentsDetailActivity","点赞失败");
                        }
                    }catch (JSONException ex){
                        Log.i("MomentsDetailActivity","json对象构造错误");
                    }
                }else{
                    Toast.makeText(MomentsDetailActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(MomentsDetailActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    private void commentMoments(String content){
        JSONObject requestJson = new JSONObject();
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
                        final String ID = result.getString("PrimaryID");
                        if(praiseResult==true){
                            //评论成功
                            Log.i("ShareActivity","评论成功");
                            Toast.makeText(MomentsDetailActivity.this,"评论成功",Toast.LENGTH_SHORT).show();
                            int count = Integer.parseInt(momentsItem.getCommentNum());
                            count++;
                            DetailCommentCount.setText(count+"");
                            MomentComment.setText("");
                            MomentComment.clearFocus();
                            //更新评论
                            getCommentCount();
                        }else{
                            //点赞失败
                            Log.i("MomentsDetailActivity","评论失败");
                        }
                    }catch (JSONException ex){
                        Log.i("MomentsDetailActivity","json对象构造错误");
                    }
                }else{
                    Toast.makeText(MomentsDetailActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(MomentsDetailActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    private void initComments(final String comments){
        try{
            JSONArray tempArr = new JSONArray(comments);
            List<MomentCommentItem> tempList = new ArrayList<>();
            for(int i=0;i<tempArr.length();i++){
                if(!tempArr.getJSONObject(i).isNull("ID")){
                    Log.i("评论：",tempArr.getJSONObject(i).toString());
                    tempList.add(new MomentCommentItem(tempArr.getJSONObject(i),authorID));
                }
            }
            commentItemList.addAll(tempList);
            if(commentItemList.size()>0){
                commentsContainer.setVisibility(View.VISIBLE);
            }else{
                commentsContainer.setVisibility(View.GONE);
            }
            commentsListView.setAdapter(commentItemAdapter);
            setListViewHeightBasedOnChildren(commentsListView);
            commentItemAdapter.notifyDataSetChanged();
            commentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //通过view获取其内部的组件，进而进行操作
                    MomentCommentItem comment = commentItemList.get(i);
                    String commenterID = comment.getFromCommenterID();
                    SharedPreferences sf=getSharedPreferences("data",MODE_PRIVATE);
                    String userID=sf.getString("userID","");
                    Log.i("评论人：",commenterID);
                    Log.i("登录用户：",userID);
                    if(commenterID.equals(userID)){
                        //是自己发表的评论，可以删除（下面弹出框：删除与取消）
                        onMyItemClick(i);
                    }else{
                        //不是自己发表的评论，可以回复评论，(貌似没有回复评论事件！)

                        replyComment(i);
                    }
                }
            });
        }catch (JSONException ex){
            Toast.makeText(MomentsDetailActivity.this,"json解析错误",Toast.LENGTH_SHORT).show();
        }
    }
    private void onMyItemClick(final int position){
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
        View rootView =LayoutInflater.from(this).inflate(R.layout.activity_moments_detail, null);
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
                if (position<commentItemList.size()) {
                    //删除该条评论
                    MomentCommentItem comment = commentItemList.get(position);
                    String commentID = comment.getCommentID();
                    removeComment(commentID,position,popupWindow);
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
                            //移除评论，关闭pop，刷新页面
                            commentItemList.remove(position);
                            //关闭PopupWindow
                            window.dismiss();
                            // 刷新布局
                            commentItemAdapter.notifyDataSetChanged();
                        }else{
                            //删除评论失败
                            Log.i("NewsCommentsActivity","删除评论失败");
                            Toast.makeText(MomentsDetailActivity.this,"删除评论失败！请稍后再试。",Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException ex){
                        Log.i("MomentsDetailActivity","json对象构造错误");
                    }
                }else{
                    Toast.makeText(MomentsDetailActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(MomentsDetailActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //回复评论
    private void replyComment(final int i){
        String toCommenterName = commentItemList.get(i).getToCommenterName();
        MomentComment.setFocusable(true);
        MomentComment.setFocusableInTouchMode(true);
        MomentComment.requestFocus();
        MomentComment.setHint("回复"+toCommenterName+":");
        InputMethodManager imm = (InputMethodManager) getSystemService(MomentsDetailActivity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
    //根据朋友圈id获取评论数量
    private void getCommentCount(){
        JSONObject requestJson = new JSONObject();
//        {commentSearchView:{"PublishID":"1f3b2aab-278d-21b0-dc27-53ee46b45b63","WithAddress":1},page:{"pageStart":1,"pageEnd":5}}
        try{
            JSONObject val1 = new JSONObject();
            val1.put("PublishID",momentsItem.getMomentID());
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
                        List<ShareMomentsCommentItem> commentItemList = momentsItem.getShareMomentsCommentItemList();
                        //将字符串转换成jsonObject对象
                        JSONObject result = new JSONObject(response.body());
                        if(result!=null){
                            int count = result.getInt("d");
                            if(count>0){
                                getCommentInfos(count);
                            }
                        }
                    }catch (JSONException ex){
                        Log.i("ShareActivity","json对象构造错误");
                    }
                }else{
                    Toast.makeText(MomentsDetailActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(MomentsDetailActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //根据朋友圈ID获取评论
    private void getCommentInfos(final int count){
        JSONObject requestJson = new JSONObject();
//        {commentSearchView:{"PublishID":"1f3b2aab-278d-21b0-dc27-53ee46b45b63","WithAddress":1},page:{"pageStart":1,"pageEnd":5}}
        try{
            JSONObject val1 = new JSONObject();
            val1.put("PublishID",momentsItem.getMomentID());
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
                        //将字符串转换成jsonObject对象
                        String str = new JSONObject(response.body()).getString("d");
                        JSONArray result = new JSONArray(str);
//                        JSONArray result = add JSONObject(response.body()).getJSONArray("d");
                        if(result!=null){
                            commentItemList.clear();
                            for (int i=0;i<result.length();i++){
                                commentItemList.add(new MomentCommentItem(result.getJSONObject(i),authorID));
                            }
                            commentItemAdapter.notifyDataSetChanged();

                        }else{
                            //点赞失败
                            Log.i("MomentsDetailActivity","评论失败");
                        }
                    }catch (JSONException ex){
                        Log.i("MomentsDetailActivity","json对象构造错误");
                    }
                }else{
                    Toast.makeText(MomentsDetailActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(MomentsDetailActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);//反注册EventBus
    }
}
