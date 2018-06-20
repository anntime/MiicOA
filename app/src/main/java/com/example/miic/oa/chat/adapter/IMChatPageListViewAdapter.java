package com.example.miic.oa.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.base.http.PostRequest;
import com.example.miic.common.MyApplication;
import com.example.miic.oa.chat.item.IMChatPageListView;
import com.example.miic.oa.chat.item.IMContactItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.miic.oa.common.LvHeightUtil.setListViewHeightBasedOnChildren;
import static com.example.miic.oa.common.Setting.addConnectionListener;

/**
 * Created by XuKe on 2018/3/19.
 */

public class IMChatPageListViewAdapter extends BaseAdapter {
    private Context context;
    private List<IMChatPageListView> contactsPageListViewList;


    public IMChatPageListViewAdapter(Context context, List<IMChatPageListView> contactsPageListViewList){
        this.contactsPageListViewList = contactsPageListViewList;
        this.context = context;
    }
    @Override
    public int getCount() {
        return contactsPageListViewList.size();
    }

    @Override
    public IMChatPageListView getItem(int i) {
        return contactsPageListViewList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view;
        ViewHolder viewHolder;
        if(convertView==null){
            view = LayoutInflater.from(context).inflate(R.layout.item_contacts_container,null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        IMChatPageListView contactsListView = contactsPageListViewList.get(i);
        viewHolder.deptName.setText(contactsListView.getDeptName());

        final List<IMContactItem> contactItemList = contactsListView.getContactsList();
        IMContactItemAdapter contactItemAdapter = new IMContactItemAdapter(context,contactItemList);
        viewHolder.peopleContainer.setAdapter(contactItemAdapter);
        setListViewHeightBasedOnChildren(viewHolder.peopleContainer);
        viewHolder.peopleContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("ContactsPageAdapter","点击联系人"+contactItemList.get(i).getUserID());
                //initIMData(contactItemList.get(i).getUserID());
                //添加IM连接状态监听
                addConnectionListener();
                final String target = contactItemList.get(i).getUserID();//消息接收者ID
                final String appkey = MyApplication.getAPPKEY(); //消息接收者appKey
                Intent intent = MyApplication.getYWIMKit().getChattingActivityIntent(target, appkey);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                context.startActivity(intent);

            }
        });
        return view;
    }
    //判断用户是否在阿里云上
    private void initIMData(final String userID) {
        //获取通讯录信息
        RequestBody View = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), "{userID:'"+userID+"'}");
        Call<String> call = PostRequest.Instance.request.GetIMUserInfo(View);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try
                    {
                        if (response.body()!=null){
                            //将字符串转换成jsonObject对象
                            String result = new JSONObject(response.body()).getString("d");
                            Log.i("ContactsActivity",result);
                            final String target = userID;//消息接收者ID
                            final String appkey = MyApplication.getAPPKEY(); //消息接收者appKey
                            Intent intent = MyApplication.getYWIMKit().getChattingActivityIntent(target, appkey);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                            context.startActivity(intent);
                        }

                    }
                    catch (JSONException e)
                    {
                        Log.e("ContactsActivity",e.getMessage());
                    }
                }else{
                    Toast.makeText(context,"当前网络不可用，请稍后再试！",Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
//                登录失败----》隐藏进度条，用户名获取焦点。
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(context,"获取失败，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }

    class ViewHolder{
        TextView deptName;
        ListView peopleContainer;
        public ViewHolder(View view){
            deptName=(TextView)view.findViewById(R.id.dept_name);
            peopleContainer = (ListView)view.findViewById(R.id.people_container);
        }
    }
}
