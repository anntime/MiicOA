package com.example.miic.share.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.base.RandomCode;
import com.example.miic.base.http.PostRequest;
import com.example.miic.common.MyApplication;
import com.example.miic.share.adapter.SearchUserAdapter;
import com.example.miic.share.item.FriendContactItem;
import com.example.miic.share.item.SearchUserItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFriendsActivity extends AppCompatActivity {
    private EditText searchET;
    private ListView searchResultLV;
    private List<SearchUserItem> userList = new ArrayList<>();
    private SearchUserAdapter userAdapter;
    private String searchKeyword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        setHeader();
        initView();
    }
    private void setHeader() {
        TextView titleTv = (TextView) findViewById(R.id.page_title);
        LinearLayout backLin = (LinearLayout)findViewById(R.id.back);
        LinearLayout rightLin = (LinearLayout)findViewById(R.id.menu);
        ImageView menuImage = (ImageView)findViewById(R.id.menu_image);
        titleTv.setText("添加好友");
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
        //搜索框搜索
        searchET = (EditText)findViewById(R.id.search_keyword);
        Drawable drawable=getResources().getDrawable(R.drawable.find);
        drawable.setBounds(0,0,35,35);
        searchET.setCompoundDrawables(drawable,null,null,null);
        searchKeyword = searchET.getText().toString();
        //搜索
        searchET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == keyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    //隐藏键盘
                    ((InputMethodManager) AddFriendsActivity.this.getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(AddFriendsActivity.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    // 提交
                    searchFriendCount();
                    searchET.clearFocus();
                }
                return false;
            }
        });
        searchResultLV = (ListView)findViewById(R.id.search_list);
        userAdapter = new SearchUserAdapter(AddFriendsActivity.this,userList);
        searchResultLV.setAdapter(userAdapter);
        userAdapter.setOnItemMyClickListener(new SearchUserAdapter.onItemMyClickListener(){
            public void onMyClick(int type, final int position) {
                SearchUserItem entity = userList.get(position);
                if (type == 0) {
                    //        显示弹出框
                    final EditText edt = new EditText(AddFriendsActivity.this);
                    edt.setMinLines(3);
                    edt.setText("我是"+MyApplication.getUserName()+",来自"+MyApplication.getDeptName());
                    final TextView txv = new TextView(AddFriendsActivity.this);
                    txv.setText(userList.get(position).getName());
                    new  AlertDialog.Builder(AddFriendsActivity.this)
                            .setTitle("附加消息" )
                            .setView(txv)
                            .setView(edt)
                            .setPositiveButton("确定" , new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    //添加好友
                                    addFriend(position,edt.getText().toString());
                                }
                            })
                            .setNegativeButton("取消" ,  null )
                            .show();

                }
            }
        });
    }
    private void addFriend(int position,String message){
//        {applicationInfo:{"ID":"e436a857-83af-2c7a-4618-34a1ed2f2106","AddresserID":"43D870D3-AE49-4A56-A818-2BDC18EF8F96",
// "AddresserName":"薛宁","Remark":"我是常成月,来自机械工业信息中心"}}
        JSONObject requestJson = new JSONObject();
        try{
            JSONObject val1 = new JSONObject();
            val1.put("ID", RandomCode.getCode());
            val1.put("AddresserID",userList.get(position).getUserID());
            val1.put("AddresserName",userList.get(position).getName());
            val1.put("Remark",message);
            requestJson.put("applicationInfo",val1);
        }catch (JSONException ex){
            Log.i("getFriends","json对象构造错误");
        }
        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        String cookieStr = MyApplication.getCookieStr();
        Call<String> call = PostRequest.Instance.request.Application (cookieStr,keywordView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try{
                    if(response.body()!=null){
                        Boolean aBoolean =(new JSONObject(response.body())).getBoolean("d");
                        if (aBoolean==true){
                            Toast.makeText(AddFriendsActivity.this,"好友请求发送成功",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(AddFriendsActivity.this,"好友请求发送失败",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(AddFriendsActivity.this,"解析错误",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AddFriendsActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //搜索用户
    private void searchFriend(int count){
//        {keywordView:{"Keyword":"薛宁"},page:{"pageStart":1,"pageEnd":15}}
        JSONObject requestJson = new JSONObject();
        try{
            JSONObject val1 = new JSONObject();
            val1.put("Keyword",searchET.getText().toString());
            requestJson.put("keywordView",val1);
            JSONObject val2 = new JSONObject();
            val2.put("pageStart",1);
            val2.put("pageEnd",count);
            requestJson.put("page",val2);
        }catch (JSONException ex){
            Log.i("searchFriend","json对象构造错误");
        }
        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        String cookieStr = MyApplication.getCookieStr();
        Call<String> call = PostRequest.Instance.request.SearchFriends (cookieStr,keywordView);
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
                                userList.add(new SearchUserItem(objectTemp));
                            }
                            searchResultLV.requestFocus();
                        }else{
                            Toast.makeText(AddFriendsActivity.this,"暂无数据！请稍后再试！",Toast.LENGTH_SHORT).show();
                        }
                        userAdapter.notifyDataSetChanged();
                    }else{
                        Toast.makeText(AddFriendsActivity.this,"解析错误",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AddFriendsActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //搜索用户数量
    private void searchFriendCount(){
//        {keywordView:{"Keyword":"薛宁"}}
        JSONObject requestJson = new JSONObject();
        try{
            JSONObject val1 = new JSONObject();
            val1.put("Keyword",searchET.getText().toString());
            requestJson.put("keywordView",val1);
        }catch (JSONException ex){
            Log.i("searchFriendCount","json对象构造错误");
        }
        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        String cookieStr = MyApplication.getCookieStr();
        Call<String> call = PostRequest.Instance.request.GetSearchFriendsCount (cookieStr,keywordView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println("count是：" + response.body());
                if(response.body()!=null){
                    try{
                        JSONObject jsonObject = new JSONObject(response.body());
                        int count = Integer.parseInt(jsonObject.getString("d"));
                        //判断新闻评论数量不等于0
                        if(count!=0){
                            searchFriend(count);
                        }else{
                            Toast.makeText(AddFriendsActivity.this,"暂时还没有好友",Toast.LENGTH_SHORT);
                        }
                    }catch (JSONException ex){
                        Log.i("getFriendsCount","json对象构造错误");
                    }
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(AddFriendsActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
}
