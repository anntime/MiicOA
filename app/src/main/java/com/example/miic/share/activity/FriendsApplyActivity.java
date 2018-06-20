package com.example.miic.share.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.base.http.PostRequest;
import com.example.miic.base.loadmore.OnLoadMoreScrollListener;
import com.example.miic.common.MyApplication;
import com.example.miic.share.adapter.FriendApplyAdapter;
import com.example.miic.share.item.AuthenticationMessageItem;
import com.example.miic.share.item.ShareMomentsItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendsApplyActivity extends AppCompatActivity {

    private TextView messageTip;
    private ListView applyListView;
    private List<AuthenticationMessageItem> applyList = new ArrayList<>();
    private FriendApplyAdapter applyAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_apply);
        setHeader();
        initView();
    }

    private void setHeader() {
        TextView titleTv = (TextView) findViewById(R.id.page_title);
        LinearLayout backLin = (LinearLayout)findViewById(R.id.back);
        LinearLayout rightLin = (LinearLayout)findViewById(R.id.menu);
        ImageView menuImage = (ImageView)findViewById(R.id.menu_image);
        titleTv.setText("验证消息");
        //back event
        backLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        menuImage.setVisibility(View.GONE);
    }
    private void initView(){
        messageTip = (TextView)findViewById(R.id.message_tip);
        applyListView = (ListView)findViewById(R.id.apply_list);
        applyAdapter = new FriendApplyAdapter(FriendsApplyActivity.this,applyList);
        applyListView.setAdapter(applyAdapter);
        getMyValidationMessageList();
        applyAdapter.setOnItemMyClickListener(new FriendApplyAdapter.onItemMyClickListener(){
            public void onMyClick(int type, int position) {
                AuthenticationMessageItem entity = applyList.get(position);
                if (type == 0) {
                    //同意好友申请
                    acceptApply(position);
                } else if (type == 1) {
                    //拒绝好友申请
                    refuseApply(position);
                }
            }
        });
    }
    private void getMyValidationMessageList(){
        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), "");
        String cookieStr = MyApplication.getCookieStr();
        Call<String> call = PostRequest.Instance.request.GetMyValidationMessageList (cookieStr,keywordView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try{
                    if(response.body()!=null){
                        String jsonStrTemp =(new JSONObject(response.body())).getString("d");
                        System.out.println("body:" + jsonStrTemp);
                        if(jsonStrTemp!=null||jsonStrTemp!="[]"){
                            JSONArray arrayTemp = new JSONArray(jsonStrTemp);
                            for (int i=0;i<arrayTemp.length();i++){
                                JSONObject objectTemp = arrayTemp.getJSONObject(i);

                                applyList.add(new AuthenticationMessageItem(objectTemp));
                            }

                            applyListView.setVisibility(View.VISIBLE);
                            messageTip.setVisibility(View.GONE);
                            applyListView.requestFocus();
                        }else{
                            applyListView.setVisibility(View.GONE);
                            messageTip.setVisibility(View.VISIBLE);
                            Toast.makeText(FriendsApplyActivity.this,"暂无数据！请稍后再试！",Toast.LENGTH_SHORT).show();
                        }
                        applyAdapter.notifyDataSetChanged();
                    }else{
                        Toast.makeText(FriendsApplyActivity.this,"解析错误",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(FriendsApplyActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    private void acceptApply(final int position){
        JSONObject requestJson = new JSONObject();
        try{
            JSONObject val1 = new JSONObject();
            val1.put("ApplicantID", applyList.get(position).getUserID());
            val1.put("ApplicantName",applyList.get(position).getUserName());
            val1.put("ApplicationTime",applyList.get(position).getApplyTime());
            requestJson.put("approveView",val1);
        }catch (JSONException ex){
            Log.i("getFriends","json对象构造错误");
        }
        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        String cookieStr = MyApplication.getCookieStr();
        Call<String> call = PostRequest.Instance.request.AgreeApplication (cookieStr,keywordView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try{
                    if(response.body()!=null){
                        Boolean aBoolean =(new JSONObject(response.body())).getBoolean("d");
                        if (aBoolean==true){
                            applyList.remove(position);
                            for(int p=0;p<applyList.size();p++){
                                AuthenticationMessageItem message = applyList.get(p);
                                if(applyList.get(position).getUserID().equals(message.getUserID())){
                                    applyList.remove(message);
                                }
                            }
                            applyAdapter.notifyDataSetChanged();
                            if (applyList.size()==0){
                                applyListView.setVisibility(View.GONE);
                                messageTip.setVisibility(View.VISIBLE);
                            }
                            Toast.makeText(FriendsApplyActivity.this,"好友添加成功",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(FriendsApplyActivity.this,"好友添加失败",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(FriendsApplyActivity.this,"解析错误",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(FriendsApplyActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    private void refuseApply(final int position){
        JSONObject requestJson = new JSONObject();
        try{
            JSONObject val1 = new JSONObject();
            val1.put("ApplicantID", applyList.get(position).getUserID());
            val1.put("ApplicantName",applyList.get(position).getUserName());
            val1.put("ApplicationTime",applyList.get(position).getApplyTime());
            requestJson.put("approveView",val1);
        }catch (JSONException ex){
            Log.i("getFriends","json对象构造错误");
        }
        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        String cookieStr = MyApplication.getCookieStr();
        Call<String> call = PostRequest.Instance.request.RefuseApplication (cookieStr,keywordView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try{
                    if(response.body()!=null){
                        Boolean aBoolean =(new JSONObject(response.body())).getBoolean("d");
                        if (aBoolean==true){
                            applyList.remove(position);
                            for(int p=0;p<applyList.size();p++){
                                AuthenticationMessageItem message = applyList.get(p);
                                if(applyList.get(position).getUserID().equals(message.getUserID())){
                                    applyList.remove(message);
                                }
                            }
                            applyAdapter.notifyDataSetChanged();
                            if (applyList.size()==0){
                                applyListView.setVisibility(View.GONE);
                                messageTip.setVisibility(View.VISIBLE);
                            }
                            Toast.makeText(FriendsApplyActivity.this,"好友申请已拒绝",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(FriendsApplyActivity.this,"好友申请拒绝失败",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(FriendsApplyActivity.this,"解析错误",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(FriendsApplyActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
}
