package com.example.miic.oa.basic.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.oa.basic.item.ContactItem;
import com.example.miic.oa.basic.item.ContactsPageListView;
import com.example.miic.oa.basic.adapter.ContactsPageListViewAdapter;
import com.example.miic.base.http.PostRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by XuKe on 2018/3/18.
 */

public class ContactsActivity extends AppCompatActivity {

    private ListView ContactsContainer;
    private List<ContactsPageListView> contactsPageListViewList;
    private ContactsPageListViewAdapter contactsPageListViewAdapter;
    private EditText searchET;
    private ImageView backBtn;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);
        ContactsContainer = (ListView)findViewById(R.id.contacts_container);
        contactsPageListViewList = new ArrayList<>();
        initData();

        searchET = (EditText)findViewById(R.id.search_contacts);
        Drawable drawable=getResources().getDrawable(R.drawable.find);
        drawable.setBounds(0,0,35,35);
        searchET.setCompoundDrawables(drawable,null,null,null);
        //搜索联系人
        searchET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == keyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    //隐藏键盘
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(ContactsActivity.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    // 提交评论
                    String content = searchET.getText().toString();
                    searchContacts(content);
                }
                return false;
            }
        });

        //返回按钮事件
        backBtn = (ImageView)findViewById(R.id.detail_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    //初始化通讯录
    private void initData() {
        //获取通讯录信息
        RequestBody View = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), "");
        Call<String> call = PostRequest.Instance.request.GetAddressBook(View);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try
                    {
                        //将字符串转换成jsonObject对象
                        String result = new JSONObject(response.body()).getString("d");
                        Log.i("ContactsActivity",result);
                        JSONArray arr = new JSONArray(result);

                        for(int i=0;i<arr.length();i++){
                            JSONObject object = arr.getJSONObject(i);
                            JSONArray addBookList = object.getJSONArray("AddressBookList");
                            List< ContactItem > peopleList = new ArrayList<>();
                            for (int j=0;j<addBookList.length();j++){
                                JSONObject addBookObject = addBookList.getJSONObject(j);
                                peopleList.add(new ContactItem(addBookObject));

                            }
                            contactsPageListViewList.add(new ContactsPageListView(object.getString("DeptName"),peopleList));
                        }
                        contactsPageListViewAdapter = new ContactsPageListViewAdapter(ContactsActivity.this,contactsPageListViewList);
                        ContactsContainer.setAdapter(contactsPageListViewAdapter);
                    }
                    catch (JSONException e)
                    {
                        Log.e("ContactsActivity",e.getMessage());
                    }
                }else{
                    Toast.makeText(ContactsActivity.this,"当前网络不可用，请稍后再试！",Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
//                登录失败----》隐藏进度条，用户名获取焦点。
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ContactsActivity.this,"获取通讯录失败，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }

    private void searchContacts(String content){
        String keyword= "{keyword:'"+content+"'}";
        //获取通讯录信息
        RequestBody View = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), keyword);
        Call<String> call = PostRequest.Instance.request.SearchAddressBookUserInfos(View);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try
                    {
                        //将字符串转换成jsonObject对象
                        String result = new JSONObject(response.body()).getString("d");
                        Log.i("ContactsActivity",result);
                        JSONArray arr = new JSONArray(result);
                        contactsPageListViewList.clear();
                        for(int i=0;i<arr.length();i++){
                            JSONObject object = arr.getJSONObject(i);
                            JSONArray addBookList = object.getJSONArray("AddressBookList");
                            List< ContactItem > peopleList = new ArrayList<>();
                            for (int j=0;j<addBookList.length();j++){
                                JSONObject addBookObject = addBookList.getJSONObject(j);
                                peopleList.add(new ContactItem(addBookObject));

                            }
                            contactsPageListViewList.add(new ContactsPageListView(object.getString("DeptName"),peopleList));
                        }
                        contactsPageListViewAdapter = new ContactsPageListViewAdapter(ContactsActivity.this,contactsPageListViewList);
                        ContactsContainer.setAdapter(contactsPageListViewAdapter);
                        searchET.setText("");
                        searchET.clearFocus();
                    }
                    catch (JSONException e)
                    {
                        Log.e("ContactsActivity",e.getMessage());
                    }
                }else{
                    Toast.makeText(ContactsActivity.this,"当前网络不可用，请稍后再试！",Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
//                登录失败----》隐藏进度条，用户名获取焦点。
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ContactsActivity.this,"获取通讯录失败，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
}
