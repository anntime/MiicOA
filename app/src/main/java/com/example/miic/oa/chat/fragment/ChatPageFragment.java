package com.example.miic.oa.chat.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.miic.common.MyApplication;
import com.example.miic.R;
import com.example.miic.oa.chat.item.IMChatPageListView;
import com.example.miic.oa.chat.adapter.IMChatPageListViewAdapter;
import com.example.miic.oa.chat.item.IMContactItem;
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

import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.example.miic.oa.common.Setting._AutoLoginStateCallback;
import static com.example.miic.oa.common.Setting.addConnectionListener;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("NewApi")
public class ChatPageFragment extends Fragment {

    private ListView ContactsContainer;
    private List<IMChatPageListView> contactsPageListViewList;
    private IMChatPageListViewAdapter contactsPageListViewAdapter;
    private EditText searchET;

    private LinearLayout historyBtn;

    private Context mContext;
    private View rootView;
    public ChatPageFragment() {
        this.mContext = MyApplication.getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_chat_page, container, false);

        ContactsContainer = (ListView)rootView.findViewById(R.id.contacts_container);
        contactsPageListViewList = new ArrayList<>();
        initData();

        searchET = (EditText)rootView.findViewById(R.id.search_contacts);

        Drawable drawable=getResources().getDrawable(R.drawable.find);
        drawable.setBounds(0,0,35,35);
        searchET.setCompoundDrawables(drawable,null,null,null);
        //搜索联系人
        searchET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == keyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    //隐藏键盘
                    ((InputMethodManager) mContext.getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(getActivity().getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    // 提交评论
                    String content = searchET.getText().toString();
                    searchContacts(content);
                }
                return false;
            }
        });

        //获取历史聊天记录
        historyBtn = (LinearLayout)rootView.findViewById(R.id.history_button);
        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //添加IM连接状态监听
                addConnectionListener();
                Intent intent = MyApplication.mIMKit.getConversationActivityIntent();
                startActivityForResult(intent,2);
            }
        });
        _AutoLoginStateCallback();
        return rootView;
    }
    //返回这个活动时，刷新页面
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                break;
            default:
                Log.i("ContactsActivity","返回了？？？？");
                break;
        }
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
                            List<IMContactItem> peopleList = new ArrayList<>();
                            for (int j=0;j<addBookList.length();j++){
                                JSONObject addBookObject = addBookList.getJSONObject(j);
                                peopleList.add(new IMContactItem(addBookObject));

                            }
                            contactsPageListViewList.add(new IMChatPageListView(object.getString("DeptName"),peopleList));
                        }
                        contactsPageListViewAdapter = new IMChatPageListViewAdapter(mContext,contactsPageListViewList);
                        ContactsContainer.setAdapter(contactsPageListViewAdapter);
                    }
                    catch (JSONException e)
                    {
                        Log.e("ContactsActivity",e.getMessage());
                    }
                }else{
                    Toast.makeText(mContext,"当前网络不可用，请稍后再试！",Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
//                登录失败----》隐藏进度条，用户名获取焦点。
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(mContext,"获取通讯录失败，请稍后再试！",Toast.LENGTH_SHORT).show();

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
                            List< IMContactItem > peopleList = new ArrayList<>();
                            for (int j=0;j<addBookList.length();j++){
                                JSONObject addBookObject = addBookList.getJSONObject(j);
                                peopleList.add(new IMContactItem(addBookObject));

                            }
                            contactsPageListViewList.add(new IMChatPageListView(object.getString("DeptName"),peopleList));
                        }
                        contactsPageListViewAdapter = new IMChatPageListViewAdapter(mContext,contactsPageListViewList);
                        ContactsContainer.setAdapter(contactsPageListViewAdapter);
                        searchET.setText("");
                        searchET.clearFocus();
                    }
                    catch (JSONException e)
                    {
                        Log.e("ContactsActivity",e.getMessage());
                    }
                }else{
                    Toast.makeText(mContext,"当前网络不可用，请稍后再试！",Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
//                登录失败----》隐藏进度条，用户名获取焦点。
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(mContext,"获取通讯录失败，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
}
