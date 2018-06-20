package com.example.miic.share.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.base.http.PostRequest;
import com.example.miic.base.loadmore.LoadMoreListView;
import com.example.miic.base.waveSideBar.WaveSideBar;
import com.example.miic.common.MyApplication;
import com.example.miic.oa.search.activity.SearchActivity;
import com.example.miic.oa.search.adapter.SearchPageNewsAdapter;
import com.example.miic.oa.search.item.SearchPageNews;
import com.example.miic.share.adapter.FriendContactAdapter;
import com.example.miic.share.item.AuthenticationMessageItem;
import com.example.miic.share.item.FriendContactItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShareFriendsActivity extends AppCompatActivity {
    private EditText keywordEditText;
    private String searchKeyword;
    private SharedPreferences sf;
    private List<FriendContactItem> friendContactItemList = new ArrayList<>();
    private FriendContactAdapter friendContactAdapter;
    private RecyclerView recyclerView;
    private WaveSideBar sideBar;
    private TextView friendApply;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_friends);
        setHeader();
        initView();
        getFriendsCount();
    }
    private void setHeader() {
        TextView titleTv = (TextView) findViewById(R.id.page_title);
        LinearLayout backLin = (LinearLayout)findViewById(R.id.back);
        LinearLayout rightLin = (LinearLayout)findViewById(R.id.menu);
        ImageView menuImage = (ImageView)findViewById(R.id.menu_image);
        titleTv.setText("朋友圈好友");
        //back event

        backLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        menuImage.setImageResource(R.drawable.add);
        //send friends messgae
        rightLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShareFriendsActivity.this, AddFriendsActivity.class);
                startActivity(intent);
            }
        });
    }
    private void initView(){
        recyclerView = (RecyclerView) findViewById(R.id.share_contacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        friendContactAdapter = new FriendContactAdapter(friendContactItemList,R.layout.share_contacts_item);
        recyclerView.setAdapter(friendContactAdapter);
        sideBar = (WaveSideBar) findViewById(R.id.side_bar);
        sideBar.setIndexItems("A", "B", "C", "D", "E", "F", "G", "H", "I", "J","K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");
        sideBar.setOnSelectIndexItemListener(new WaveSideBar.OnSelectIndexItemListener() {
            @Override
            public void onSelectIndexItem(String index) {
                for (int i=0; i<friendContactItemList.size(); i++) {
                    if (friendContactItemList.get(i).getIndex().equals(index)) {
                        ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(i, 0);
                        return;
                    }
                }
            }
        });
        keywordEditText = (EditText)findViewById(R.id.search_keyword);
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
                    ((InputMethodManager) ShareFriendsActivity.this.getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(ShareFriendsActivity.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    // 提交
                    getFriendsCount();
                    keywordEditText.clearFocus();
                }
                return false;
            }
        });
        friendApply = (TextView)findViewById(R.id.friend_apply);
        friendApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShareFriendsActivity.this, FriendsApplyActivity.class);
                startActivityForResult(intent,1);
            }
        });
        getMyValidationMessageList();
    }
    //返回这个活动时，刷新页面
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                break;
            default:
                //updateCommentAndPraise();
                friendContactItemList.clear();
                getMyValidationMessageList();
                getFriendsCount();
                break;
        }
    }
    private void getFriends(int count){
        // {searchView:{"Keyword":""},page:{"pageStart":1,"pageEnd":10}}
        JSONObject requestJson = new JSONObject();
        try{
            JSONObject val1 = new JSONObject();
            val1.put("Keyword",keywordEditText.getText().toString());
            requestJson.put("searchView",val1);
            JSONObject val2 = new JSONObject();
            val2.put("pageStart",1);
            val2.put("pageEnd",count);
            requestJson.put("page",val2);
        }catch (JSONException ex){
            Log.i("getFriends","json对象构造错误");
        }
        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        String cookieStr = MyApplication.getCookieStr();
        Call<String> call = PostRequest.Instance.request.GetAddressBookList (cookieStr,keywordView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try{
                        JSONObject jsonObject = new JSONObject(response.body());
                        JSONArray jsonArray = new JSONArray(jsonObject.getString("d"));
                        List<FriendContactItem> tempList = new ArrayList<>();
                        for (int i=0;i<jsonArray.length();i++){
                            tempList.add(new FriendContactItem(jsonArray.getJSONObject(i)));
                        }
                        Collections.sort(tempList
                        );
                        friendContactItemList.addAll(tempList);
                        friendContactAdapter.notifyDataSetChanged();

                    }catch (JSONException ex){
                        Log.i("getFriends","json对象构造错误");
                    }
                }

            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ShareFriendsActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    private void getFriendsCount(){
//        {searchView:{"Keyword":""}}
        JSONObject requestJson = new JSONObject();
        try{
            JSONObject val1 = new JSONObject();
            val1.put("Keyword",keywordEditText.getText().toString());
            requestJson.put("searchView",val1);
        }catch (JSONException ex){
            Log.i("ShareFriendsActivity","json对象构造错误");
        }
        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        String cookieStr = MyApplication.getCookieStr();
        Call<String> call = PostRequest.Instance.request.GetAddressBookCount (cookieStr,keywordView);
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
                            friendContactItemList.clear();
                            getFriends(count);
                        }else{
                            Toast.makeText(ShareFriendsActivity.this,"暂时还没有好友",Toast.LENGTH_SHORT);
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
                Toast.makeText(ShareFriendsActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
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
                        if(jsonStrTemp==null||jsonStrTemp.equals("[]")){
                            friendApply.setVisibility(View.GONE);
                        }else{
                            friendApply.setVisibility(View.VISIBLE);
                        }
                    }else{
                        Toast.makeText(ShareFriendsActivity.this,"解析错误",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ShareFriendsActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
}
