package com.example.miic.sealManagement.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.base.http.PostRequest;
import com.example.miic.common.MyApplication;
import com.example.miic.sealManagement.adapter.SealGridViewAdapter;
import com.example.miic.sealManagement.common.SealCommon;
import com.example.miic.sealManagement.item.SealGridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SealIndexActivity extends AppCompatActivity {
    private GridView gridView;
    private List<SealGridView> columnGridViewList = new ArrayList<>();
    private SealGridViewAdapter columnItemAdapter;
    private Activity mActivity =  this;
    private String projectID = "1a9cf6af-f7d2-f08e-28f5-1cf17f6a53de";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seal_index);
        SealCommon.SealManageActivity = this;
        gridView = (GridView)findViewById(R.id.seal_gridview);
        setHeader();
        initGridView();
    }
    private void setHeader() {
        TextView titleTv = (TextView) findViewById(R.id.page_title);
        LinearLayout backLin = (LinearLayout)findViewById(R.id.back);
        LinearLayout rightLin = (LinearLayout)findViewById(R.id.menu);
        titleTv.setText("用印管理");
        rightLin.setVisibility(View.INVISIBLE);
        backLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initGridView(){

//        columnGridViewList = new ArrayList<SealGridView>();
//        columnGridViewList.add(new SealGridView("用印申请","GoCarAdd"));
//        columnGridViewList.add(new SealGridView("用印审核","GoContractApproveList"));
//        columnGridViewList.add(new SealGridView("监印管理","GoContractPrintList"));
//        columnGridViewList.add(new SealGridView("我的用印","GoMyContractList"));


        columnItemAdapter=new SealGridViewAdapter(this,columnGridViewList);
        GetFunInfoList();
        gridView.setAdapter(columnItemAdapter);
        columnItemAdapter.setOnItemMyClickListener(new SealGridViewAdapter.onItemMyClickListener() {
            @Override
            public void onMyClick(int type, int position) {
                if(position<columnGridViewList.size()) {
                    //通过view获取其内部的组件，进而进行操作
                    String clickID;
                    SealGridView gridViewItem = columnGridViewList.get(position);
                    clickID = gridViewItem.getColumnIconID();


                    Class FindClass = mActivity.getClass();
                    try{
                        Method m = FindClass.getMethod(clickID,new Class[]{});
                        m.invoke(mActivity, new Object[]{});
                    }catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                    catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    private void GetFunInfoList(){
        String str = "{pid:'" + projectID + "',userID:'"+ MyApplication.getUserID()+"'}";
        RequestBody searchView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),str);
        Call<String> call = PostRequest.Instance.request.GetContractFunInfoList(searchView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        Log.i("权限分配返回值：",jsonObject.toString());
                        String dataStr = jsonObject.getString("d");
                        if(dataStr!="[]"){
                            JSONArray dataArr = new JSONArray(dataStr);
                            for (int index = 0;index<dataArr.length();index++){
                                JSONObject item = dataArr.getJSONObject(index);
                                if(!item.getString("AndroidFunScript").equals("")&&!item.getString("FunCode").equals("60005")) {
                                    columnGridViewList.add(new SealGridView(item.getString("FunName"), item.getString("AndroidFunScript")));
                                }
                            }
                            columnItemAdapter.notifyDataSetChanged();
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
                Toast.makeText(SealIndexActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }

    //页面跳转--用印申请
    public void GoSealAdd(){
        Intent intent=new Intent( this,SealApplyActivity.class);
        startActivity(intent);
    }
    //我的用印
    public void GoMySealList(){
        Intent intent=new Intent( this,SealManageActivity.class);
        startActivity(intent);
    }
    //用印审核
    public void GoSealApproveList(){
        Intent intent=new Intent( this,SealApprovalActivity.class);
        startActivity(intent);
    }
    //用印监印
    public void GoSealPrintList(){
        Intent intent=new Intent( this,SealPrintActivity.class);
        startActivity(intent);
    }
}
