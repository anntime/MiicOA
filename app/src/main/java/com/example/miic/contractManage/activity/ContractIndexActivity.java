package com.example.miic.contractManage.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.base.http.PostRequest;
import com.example.miic.common.MyApplication;
import com.example.miic.contractManage.adapter.ContractGridViewAdapter;
import com.example.miic.contractManage.common.ContractCommon;
import com.example.miic.contractManage.item.ContractGridView;
import com.example.miic.contractManage.utils.ResUtil;

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

public class ContractIndexActivity extends AppCompatActivity {
    private GridView gridView;
    private List<ContractGridView> columnGridViewList = new ArrayList<>();
    private ContractGridViewAdapter columnItemAdapter;
    private Activity mActivity =  this;
    private String projectID = "700802ea-fd46-44b1-ab97-61edb7f9223c";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_index);
        ContractCommon.ContractManageActivity = this;
        gridView = (GridView)findViewById(R.id.contract_gridview);
        setHeader();
        initGridView();
    }
    private void setHeader() {
        TextView titleTv = (TextView) findViewById(R.id.page_title);
        LinearLayout backLin = (LinearLayout)findViewById(R.id.back);
        LinearLayout rightLin = (LinearLayout)findViewById(R.id.menu);
        titleTv.setText("合同管理");
        rightLin.setVisibility(View.INVISIBLE);
        backLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initGridView(){

//        columnGridViewList = new ArrayList<ContractGridView>();
//        columnGridViewList.add(new ContractGridView("合同审核","GoApproveList"));
//        columnGridViewList.add(new ContractGridView("执行记录","GoContractExecuteList"));
//        columnGridViewList.add(new ContractGridView("合同监印","GoContractPrintList"));
//        columnGridViewList.add(new ContractGridView("合同查询","GoContractSearchList"));
//        columnGridViewList.add(new ContractGridView("合同完成","GoContractFinishList"));
//        columnGridViewList.add(new ContractGridView("合同管理","GoContractManageList"));
//        columnGridViewList.add(new ContractGridView("执行计划","GoContractPlanList"));
//        columnGridViewList.add(new ContractGridView("我的合同","GoMyContractList"));


        columnItemAdapter=new ContractGridViewAdapter(this,columnGridViewList);
        GetFunInfoList();
        gridView.setAdapter(columnItemAdapter);
        columnItemAdapter.setOnItemMyClickListener(new ContractGridViewAdapter.onItemMyClickListener() {
            @Override
            public void onMyClick(int type, int position) {
                if(position<columnGridViewList.size()) {
                    //通过view获取其内部的组件，进而进行操作
                    String clickID;
                    ContractGridView gridViewItem = columnGridViewList.get(position);
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
                        if(!dataStr.equals("[]")){
                            JSONArray dataArr = new JSONArray(dataStr);
                            for (int index = 0;index<dataArr.length();index++){
                                JSONObject item = dataArr.getJSONObject(index);
                                if(!item.getString("AndroidFunScript").equals("")) {
                                    columnGridViewList.add(new ContractGridView(item.getString("FunName"), item.getString("AndroidFunScript")));
                                }
                            }
                            columnItemAdapter.notifyDataSetChanged();
                        }else{
                            Toast.makeText(ContractIndexActivity.this,"暂没有合同权限！",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ContractIndexActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }

    //页面跳转--审核页面
    public void GoApproveList(){
        Intent intent=new Intent( this,ContractApprovalListActivity.class);
        startActivity(intent);
    }
    //我的合同申请
    public void GoMyContractList(){
        Intent intent=new Intent( this,ContractManageActivity.class);
        startActivity(intent);
    }
    //监印、执行、完成、管理、查询、计划列表页面是一样的
    public void GoContractPrintList(){
        GoContractList("jy");
    }
    public void GoContractExecuteList(){
        GoContractList("zx");
    }
    public void GoContractFinishList(){
        GoContractList("wc");
    }
    public void GoContractManageList(){
        GoContractList("gl");
    }
    public void GoContractPlanList(){
        GoContractList("jh");
    }
    public void GoContractSearchList(){
        GoContractList("cx");
    }
    public void GoContractList(String type){
        Log.i("Type:",type);
        Intent intent=new Intent( this,ContractListActivity.class);
        intent.putExtra("type",type);
        startActivity(intent);
    }
}
