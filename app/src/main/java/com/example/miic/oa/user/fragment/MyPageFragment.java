package com.example.miic.oa.user.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.mobileim.IYWLoginService;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.example.miic.BuildConfig;
import com.example.miic.R;
import com.example.miic.base.http.PostRequest;
import com.example.miic.common.MyApplication;
import com.example.miic.oa.login.activity.FindPwdActivity;
import com.example.miic.oa.login.activity.LoginActivity;
import com.example.miic.oa.search.activity.SearchActivity;
import com.example.miic.oa.user.activity.BindPhoneActivity;
import com.example.miic.oa.user.activity.ModifyUserInfoActivity;
import com.example.miic.oa.user.activity.VersionInfoActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("NewApi")
public class MyPageFragment extends Fragment {
    //个人信息初始化
    private TextView UserName;
    private TextView UserPosition;
    private TextView UserRemark;
    private ImageView UserUrl;

    private LinearLayout ModifyUserInfo;
    private LinearLayout ModifyPwd;
    private LinearLayout VersionInfo;
    private LinearLayout ModifyPhone;

    private Button ExitsBtn;
    private SharedPreferences sf;

    private ImageView searchBtn;
    private JSONObject UserInfo;

    private View rootView;
    private Context mContext;
    public MyPageFragment() {
        this.mContext = MyApplication.getContext();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_page, container, false);
        init();
        //搜索按钮绑定搜索事件
        searchBtn = (ImageView)rootView.findViewById(R.id.search_button);
        searchBtn.setOnClickListener(searchClickListener);
        return rootView;
    }

    private void init(){
        UserName = (TextView)rootView.findViewById(R.id.my_user_name);
        UserPosition = (TextView)rootView.findViewById(R.id.my_user_position_cn);
        UserRemark = (TextView)rootView.findViewById(R.id.my_user_remark);
        UserUrl = (ImageView)rootView.findViewById(R.id.my_user_erweima);
        ModifyUserInfo = (LinearLayout)rootView.findViewById(R.id.my_modify_user_info);
        ModifyPwd = (LinearLayout)rootView.findViewById(R.id.my_modify_user_pwd);
        ModifyPhone = (LinearLayout)rootView.findViewById(R.id.my_modify_phone);
        VersionInfo = (LinearLayout)rootView.findViewById(R.id.my_version_info);
        ExitsBtn = (Button)rootView.findViewById(R.id.sign_out);
        sf = getActivity().getSharedPreferences("data",mContext.MODE_PRIVATE);
        //后台请求数据
        getUserInfo();
        //修改用户信息
        ModifyUserInfo.setOnClickListener(ModifyUserInfoClickListener);
        //修改密码
        ModifyPwd.setOnClickListener(ModifyUserPwdClickListener);
        //修改手机
        ModifyPhone.setOnClickListener(ModifyUserPhone);
        //版本信息
//        VersionInfo.setOnClickListener(VersionInfoClickListener);
        TextView versionCode = (TextView)rootView.findViewById(R.id.version_code);
        versionCode.setText(BuildConfig.VERSION_NAME);
        //退出登录
        ExitsBtn.setOnClickListener(ExitsClickListener);
    }
    private void getUserInfo(){
        JSONObject requestJson = new JSONObject();
        String userID=sf.getString("userID","");
        try{
            requestJson.put("userID",userID);
        }catch (JSONException ex){
            Log.i("MyPageFrgment","json对象构造错误");
        }
        RequestBody userView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.GetUserInfo (userView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try {
                        System.out.println("body:" + response.body());
                        JSONObject jsonObject = new JSONObject(response.body());
                        UserInfo = jsonObject;
                        JSONObject jsonTemp = jsonObject.getJSONObject("d");
                        String userName = jsonTemp.getString("UserName");
                        String userUrl = jsonTemp.getString("UserUrl");
                        String orgName = jsonTemp.getString("OrgName");
                        String deptName = jsonTemp.getString("DeptName");
                        String deptID = jsonTemp.getString("DeptID");
                        String userPosition = jsonTemp.getString("Position");
                        String userRemark = jsonTemp.getString("Remark");
                        String userKey = jsonTemp.getString("UserKey");
                        String userCode = jsonTemp.getString("UserCode");
                        SharedPreferences.Editor editor = sf.edit();
                        editor.putString("userName",userName);
                        editor.putString("deptName",deptName);
                        editor.putString("deptID",deptID);
                        editor.commit();
                        if(userName!=""){
                            UserName.setText(userName+" ("+userCode+")");
                        }
                        if(userPosition!=""){
                            UserPosition.setText(userPosition);
                        }
                        if(userRemark!=""){
                            UserRemark.setText(orgName+" "+deptName);
                        }
                        if(userUrl!=""){
                            Picasso.with(mContext).load(userUrl).into(UserUrl);
                        }
                    }catch (JSONException ex){
                        Log.e("InfoChildFragment",ex.getMessage());
                        Toast.makeText(mContext,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(mContext,"获取用户信息失败！",Toast.LENGTH_SHORT).show();
                }

            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(mContext,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    private View.OnClickListener ExitsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            SharedPreferences.Editor editor = sf.edit();
            editor.remove("isLogin");
            editor.remove("userCode");
            editor.remove("userID");
            editor.commit();
            baiChuanLogout();
            Intent intent=new Intent(getActivity(),LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    };

    private View.OnClickListener ModifyUserPwdClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent(getActivity(),FindPwdActivity.class);
            startActivity(intent);
        }
    };
    private View.OnClickListener ModifyUserPhone = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent(getActivity(),BindPhoneActivity.class);
            intent.putExtra("UserInfo",UserInfo.toString());
            startActivity(intent);
        }
    };
    private View.OnClickListener ModifyUserInfoClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent(getActivity(),ModifyUserInfoActivity.class);
            intent.putExtra("UserInfo",UserInfo.toString());
            startActivityForResult(intent,0);
        }
    };
    //返回这个活动时，刷新页面
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                break;
            default:
                //后台请求数据
                getUserInfo();
                break;
        }
    }
    //查询按钮绑定事件
    private View.OnClickListener searchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent(mContext,SearchActivity.class);
            startActivity(intent);
        }
    };
    //阿里百川登出
    private void baiChuanLogout(){
        IYWLoginService loginService = MyApplication.getYWIMKit().getLoginService();
        loginService.logout(new IWxCallback() {
            @Override
            public void onSuccess(Object... arg0) {
                //登出成功
                Log.i("即时通讯","阿里百川登出成功");
            }
            @Override
            public void onProgress(int arg0) {
                // TODO Auto-generated method stub
                Log.i("即时通讯","阿里百川onProgress");
            }
            @Override
            public void onError(int errCode, String description) {
                //登出失败，errCode为错误码,description是错误的具体描述信息
                Log.i("即时通讯","阿里百川登出失败"+description+"---"+errCode);
            }
        });

    }
}
