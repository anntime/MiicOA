package com.example.miic.oa.basic.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.miic.oa.basic.item.Contact;
import com.example.miic.oa.basic.adapter.ContactsAdapter;
import com.example.miic.base.http.PostRequest;
import com.example.miic.base.waveSideBar.WaveSideBar;
import com.example.miic.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressListActivity extends AppCompatActivity {
    private RecyclerView rvContacts;
    private WaveSideBar sideBar;

    private ArrayList<Contact> contacts = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);

        initData();

    }
    private void initView() {
        rvContacts = (RecyclerView) findViewById(R.id.rv_contacts);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        rvContacts.setAdapter(new ContactsAdapter(contacts, R.layout.item_contacts));

//        sideBar = (WaveSideBar) findViewById(R.id.side_bar);
//        sideBar.setIndexItems("A", "B", "C", "D", "E", "F", "G", "H", "I", "J","K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");
//        sideBar.setOnSelectIndexItemListener(add WaveSideBar.OnSelectIndexItemListener() {
//            @Override
//            public void onSelectIndexItem(String index) {
//                for (int i=0; i<contacts.size(); i++) {
//                    if (contacts.get(i).getIndex().equals(index)) {
//                        ((LinearLayoutManager) rvContacts.getLayoutManager()).scrollToPositionWithOffset(i, 0);
//                        return;
//                    }
//                }
//            }
//        });
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
                        Log.i("AddressListActivity",result);
                        JSONArray arr = new JSONArray(result);
                        List<String> deptList = new ArrayList<>();
                        List<Contact> contactList = new ArrayList<>();
                        for(int i=0;i<arr.length();i++){
                            JSONObject object = arr.getJSONObject(i);
                            deptList.add(object.getString("DeptName"));
                            JSONArray addBookList = object.getJSONArray("AddressBookList");
                            for (int j=0;j<addBookList.length();j++){
                                JSONObject addBookObject = addBookList.getJSONObject(j);
                                //object.getString("DeptName")
                                contactList.add(new Contact("A", addBookObject.getString("UserName"),addBookObject.getString("UserUrl")));
                            }
                        }
                        contacts.addAll(contactList);
                        initView();
                    }
                    catch (JSONException e)
                    {
                        Log.e("AddressListActivity",e.getMessage());
                    }
                }else{
                    Toast.makeText(AddressListActivity.this,"当前网络不可用，请稍后再试！",Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
//                登录失败----》隐藏进度条，用户名获取焦点。
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(AddressListActivity.this,"获取通讯录失败，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }

}
