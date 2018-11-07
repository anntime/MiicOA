package com.example.miic.meetingRoomManage.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.base.http.PostRequest;
import com.example.miic.common.MyApplication;
import com.example.miic.contractManage.adapter.ContractListItemAdapter;
import com.example.miic.contractManage.common.ContractCommon;
import com.example.miic.contractManage.item.ContractListItem;
import com.example.miic.meetingRoomManage.activity.MeetingRoomManageActivity;
import com.example.miic.meetingRoomManage.item.ApplyListItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.miic.oa.common.Setting.strToDate;

/**
 * Created by admin on 2018/10/11.
 */

public class ApplyListAdapter extends BaseAdapter {

    private Context context;
    private List<ApplyListItem> list;


    public ApplyListAdapter(Context context, List<ApplyListItem> objects){
        this.context = context;
        this.list = objects;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ApplyListItem getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View view;
        ApplyListAdapter.ViewHolder holder;
        if(convertView==null){
            view= LayoutInflater.from(context).inflate(R.layout.meeting_room_apply_list_item,null);
            holder=new ApplyListAdapter.ViewHolder(view);
            view.setTag(holder);
        }else{
            view=convertView;
            holder= (ApplyListAdapter.ViewHolder) view.getTag();
        }
       final ApplyListItem item = getItem(position);
        holder.useTime.setText(item.getStartTime()+" ~ "+item.getEndTime());
        holder.useDept.setText(item.getDeptName());
        holder.topic.setText(item.getTitle());
        //判断当前登录的人跟会议室申请的人是不是一个，决定是不是要显示这个取消按钮!!!!!!!!!!!!!!!!!!!!!!!!
        if(MyApplication.getUserName().equals(item.getUserName())){
            holder.cancelBtn.setVisibility(View.VISIBLE);
            holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //点击取消事件 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    String clickID = item.getId();
                    DeleteMeetingRoomApply(clickID,position);
                }
            });

        }else{
            holder.cancelBtn.setVisibility(View.GONE);
        }


        return view;
    }
    /**
     * 添加列表项
     * @param item
     */
    public void addItem(ApplyListItem item) {
        list.add(item);
    }
    class ViewHolder{
        TextView useTime;
        TextView useDept;
        TextView topic;
        TextView cancelBtn;
        public ViewHolder(View view){
            useTime = (TextView) view.findViewById(R.id.use_time);
            useDept = (TextView) view.findViewById(R.id.dept_name);
            topic = (TextView) view.findViewById(R.id.title);
            cancelBtn = (TextView)view.findViewById(R.id.cancel_btn);
        }
    }
    //删除会议室预约
    private void DeleteMeetingRoomApply(String clickID,final int position){
        String str = "{id:'" + clickID + "'}";
        RequestBody searchView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),str);
        Call<String> call = PostRequest.Instance.request.DeleteMeetingRoomApply(searchView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        Boolean result = jsonObject.getBoolean("d");
                        if(result==true){
                            Toast.makeText(MyApplication.getContext(), "成功取消会议室预约~", Toast.LENGTH_SHORT).show();
                            list.remove(position);
                            notifyDataSetChanged();
                        }else {
                            Toast.makeText(MyApplication.getContext(), "取消会议室预约失败~", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (JSONException ex){
                        Log.i("getMyDeptInfoList","json对象构造错误");
                    }
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


}
