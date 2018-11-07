package com.example.miic.contractManage.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.base.http.PostRequest;
import com.example.miic.base.loadmore.LoadMoreListView;
import com.example.miic.base.loadmore.OnLoadMoreScrollListener;
import com.example.miic.base.multiLineRadioGroup.MultiLineRadioGroup;
import com.example.miic.contractManage.adapter.ContractSearchResultItemAdapter;
import com.example.miic.contractManage.common.ContractCommon;
import com.example.miic.contractManage.item.ContractSearchResultItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContractApprovalListActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private ContractSearchResultItemAdapter searchResultItemAdapter;
    private List<ContractSearchResultItem> searchResultList;
    private LoadMoreListView listView;
    private LinearLayout messageTip;
    private View searchView;
    private MultiLineRadioGroup contractStatus;
    private MultiLineRadioGroup contractIdentificationItem;
    private MultiLineRadioGroup contractYear;
    private MultiLineRadioGroup contractDept;

    private View searchResultView;
    private View searchProgressView;

    //总共页数
    private int mPageCount = 0;
    //每页信息数量
    private int mPageSize = 15;
    //当前页
    private int mCurrentPage=0;
    //记录选择的选项
    private JSONObject remberJson = new JSONObject();
    private JSONObject searchJson = new JSONObject();
    private JSONArray DeptIDs = new JSONArray();
    private JSONArray DeptList = new JSONArray();
    private Handler spHandler;
    // handler对象，用来接收消息~
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {  //这个是发送过来的消息
            // 处理从子线程发送过来的消息
            int arg1 = msg.arg1;  //获取消息携带的属性值
            int arg2 = msg.arg2;
            int what = msg.what;
            Object result = msg.obj;
            System.out.println("-arg1--->>" + arg1);
            System.out.println("-arg2--->>" + arg2);
            System.out.println("-what--->>" + what);
            System.out.println("-result--->>" + result);
            Bundle bundle = msg.getData(); // 用来获取消息里面的bundle数据
            System.out.println("-getData--->>"
                    + bundle.getStringArray("strs").length);
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_approval_list);
        setHeader();
        initView();
        showProgress(true);
    }

    private void setHeader() {
        TextView titleTv = (TextView) findViewById(R.id.page_title);
        LinearLayout backLin = (LinearLayout)findViewById(R.id.back);
        LinearLayout rightLin = (LinearLayout)findViewById(R.id.menu);
        ImageView menuImage = (ImageView)findViewById(R.id.menu_image);
        titleTv.setText("合同审批");

        backLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        menuImage.setImageResource(R.drawable.find);
        rightLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearchView();
            }
        });
    }
    private void initView(){
        searchResultView = findViewById(R.id.search_result);
        searchProgressView = findViewById(R.id.search_progress);
        searchView = LayoutInflater.from(this).inflate(R.layout.contract_search_pop_window,null);//PopupWindow对象;
        contractStatus = (MultiLineRadioGroup)searchView.findViewById(R.id.contract_status);
        initContractStatus();
        contractIdentificationItem = (MultiLineRadioGroup)searchView.findViewById(R.id.contract_identification_item);
        GetIdentificationItemInfos();
        MultiLineRadioGroup contractTypeOne = (MultiLineRadioGroup)searchView.findViewById(R.id.contract_type_one);
        contractTypeOne.setVisibility(View.GONE);
        TextView tx1 = (TextView)searchView.findViewById(R.id.contract_type_one_tv);
        tx1.setVisibility(View.GONE);
        LinearLayout ll1 = (LinearLayout)searchView.findViewById(R.id.contract_type_one_ll);
        ll1.setVisibility(View.GONE);
        MultiLineRadioGroup contractTypeTwo = (MultiLineRadioGroup)searchView.findViewById(R.id.contract_type_two);
        contractTypeTwo.setVisibility(View.GONE);
        TextView tx2 = (TextView)searchView.findViewById(R.id.contract_type_two_tv);
        tx2.setVisibility(View.GONE);
        LinearLayout ll2 = (LinearLayout)searchView.findViewById(R.id.contract_type_two_ll);
        ll2.setVisibility(View.GONE);
        contractYear = (MultiLineRadioGroup)searchView.findViewById(R.id.contract_year);
        initContractYear();
        contractDept = (MultiLineRadioGroup)searchView.findViewById(R.id.contract_dept);
        GetMyApproveDeptList();
        contractStatus.setOnCheckChangedListener(new MultiLineRadioGroup.OnCheckedChangedListener() {
            @Override
            public void onItemChecked(MultiLineRadioGroup group, int position, boolean checked) {
                if(checked==false){
                    contractStatus.setItemChecked(0);
                    Toast.makeText(ContractApprovalListActivity.this,"请选择合同状态！",Toast.LENGTH_SHORT).show();

                }
            }
        });
        contractIdentificationItem.setOnCheckChangedListener(new MultiLineRadioGroup.OnCheckedChangedListener() {
            @Override
            public void onItemChecked(MultiLineRadioGroup group, int position, boolean checked) {
                if(checked==false){
                    Toast.makeText(ContractApprovalListActivity.this,"请选择合同标志！",Toast.LENGTH_SHORT).show();
                    contractIdentificationItem.setItemChecked(0);
                }
            }
        });
        contractYear.setOnCheckChangedListener(new MultiLineRadioGroup.OnCheckedChangedListener() {
            @Override
            public void onItemChecked(MultiLineRadioGroup group, int position, boolean checked) {
                if(checked==false){
                    Toast.makeText(ContractApprovalListActivity.this,"请选择年份！",Toast.LENGTH_SHORT).show();
                    contractYear.setItemChecked(2);
                }
            }
        });
        contractDept.setOnCheckChangedListener(new MultiLineRadioGroup.OnCheckedChangedListener() {
            @Override
            public void onItemChecked(MultiLineRadioGroup group, int position, boolean checked) {
                if(checked==false){
                    Toast.makeText(ContractApprovalListActivity.this,"请选择部门！",Toast.LENGTH_SHORT).show();
                    contractDept.setItemChecked(0);
                }
            }
        });

        //列表展示相关
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshview);
        searchResultList = new ArrayList<>();
        searchResultItemAdapter = new ContractSearchResultItemAdapter(ContractApprovalListActivity.this,searchResultList);
        listView = (LoadMoreListView)findViewById(R.id.contract_result_container);
        listView.setAdapter(searchResultItemAdapter);
        searchResultItemAdapter.setOnItemMyClickListener(new ContractSearchResultItemAdapter.onItemMyClickListener() {
            @Override
            public void onMyClick(int type, int position) {
                if(position<searchResultList.size()) {
                    //通过view获取其内部的组件，进而进行操作3

                    String clickID;
                    ContractSearchResultItem contractItem = searchResultList.get(position);
                    clickID = contractItem.getContractID();
                    Log.i("setOnItemClickListener",clickID);
                    Intent intent;
                    intent = new Intent(ContractApprovalListActivity.this, ContractApprovalActivity.class);
                    intent.putExtra("clickID", clickID);
                    startActivityForResult(intent, 1);
                }
            }
        });
        messageTip = (LinearLayout)findViewById(R.id.message_tip);
        spHandler  = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                JSONObject requestApproveJson = new JSONObject();
                try{
                    JSONObject requestJson1 = new JSONObject();
                    requestJson1.put("Keyword","");
                    requestJson1.put("Status","1");
                    requestJson1.put("IdentificationItem","0");
                    requestJson1.put("Year",Calendar.getInstance().get(Calendar.YEAR)+"");
                    requestJson1.put("DeptID",DeptIDs);
                    requestApproveJson.put("keyword",requestJson1);
                }catch (JSONException ex){
                    Log.i("requestApproveJson","json对象构造错误");
                }
                GetMyApproveSearchCount(requestApproveJson);
            }
        };
    }
    //返回这个活动时，刷新页面
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                break;
            default:
                reFresh();
                break;
        }
    }
    private void showSearchView(){
        //启动popup windows，显示搜索页面
        final PopupWindow popupWindow=new PopupWindow(searchView, WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT,true);

        popupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.activitycolor)));//colorAccent
        // 设置PopupWindow是否能响应外部点击事件
        popupWindow.setOutsideTouchable(true);
        // 设置PopupWindow是否能响应点击事件
        popupWindow.setTouchable(true);
        //返回键点击，弹出
        popupWindow.setFocusable(true);
        popupWindow.update();
        //设置动画
        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        //在父布局的弹入/出位置
        View pView = LayoutInflater.from(this).inflate(R.layout.activity_contract_manage, null);
        popupWindow.showAtLocation(pView, Gravity.CENTER,0,0);
        //实例化控件
        LinearLayout bt_cancel= (LinearLayout) searchView.findViewById(R.id.contract_search_close);
        Button bt_submit= (Button) searchView.findViewById(R.id.seatch_button);
        final EditText inputET = (EditText) searchView.findViewById(R.id.search_keyword);
        //inputET.setFocusable(false);
        if (remberJson.length()!=0&&remberJson!=null){
            try{
                //记录选择的选项
                inputET.setText(remberJson.getString("Keyword"));

                contractStatus.setItemChecked(Integer.parseInt(remberJson.getString("Status")));
                contractIdentificationItem.setItemChecked(Integer.parseInt(remberJson.getString("IdentificationItem")));
                contractYear.setItemChecked(Integer.parseInt(remberJson.getString("Year")));
                contractDept.setItemChecked(Integer.parseInt(remberJson.getString("Dept")));
            }catch (JSONException ex){
                Log.i("showSearchView","json对象构造错误");
            }
        }else{
            contractStatus.setItemChecked(1);
            contractIdentificationItem.setItemChecked(0);
            contractYear.setItemChecked(2);
            contractDept.setItemChecked(0);
        }
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                //提交搜索事件
                JSONObject requestJson = new JSONObject();
                try{
                    //记录选择的选项
                    remberJson.put("Keyword",inputET.getText().toString());
                    remberJson.put("Status",contractStatus.getCheckedItems()[0]);
                    remberJson.put("IdentificationItem",contractIdentificationItem.getCheckedItems()[0]);
                    remberJson.put("Dept",contractDept.getCheckedItems()[0]);
                    remberJson.put("Year",contractYear.getCheckedItems()[0]);

                    ContractCommon contract = new ContractCommon();
                    JSONObject val1 = new JSONObject();
                    val1.put("Keyword",inputET.getText().toString());
                    val1.put("IdentificationItem",contract.getIdentificationItem(contractIdentificationItem.getCheckedValues().get(0)));
                    val1.put("Status", contract.getContractStatue(contractStatus.getCheckedValues().get(0)));
                    val1.put("Year",contractYear.getCheckedValues().get(0).equals("全部")?"":contractYear.getCheckedValues().get(0));


                    if(contractDept.getCheckedValues().get(0).equals("全部")){
                        val1.put("DeptID", DeptIDs);
                    }else {
                        for (int n=0;n<DeptList.length();n++){
                            if(DeptList.getJSONObject(n).getString("DeptName").equals(contractDept.getCheckedValues().get(0))){
                                JSONArray arr = new JSONArray();
                                arr.put(DeptList.getJSONObject(n).getString("DeptID"));
                                val1.put("DeptID",arr);
                            }
                        }
                    }
                    requestJson.put("keyword",val1);

                }catch (JSONException ex){
                    Log.i("showSearchView","json对象构造错误");
                }
                Log.i("搜索结果",requestJson.toString());
                GetMyApproveSearchCount(requestJson);
                popupWindow.dismiss();
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
    }
    private void initContractStatus(){
        contractStatus.append("待审批");
        contractStatus.append("审批中");
        contractStatus.append("已完成");
    }

    private void initContractYear(){
        Calendar rightNow = Calendar.getInstance();
        int year = rightNow.get(Calendar.YEAR);
        contractYear.append(year+1+"");
        contractYear.append(year+"");
        contractYear.append(year-1+"");
        contractYear.append(year-2+"");
    }
    //获取合同标识
    public void GetIdentificationItemInfos(){
        Call<String> call = PostRequest.Instance.request.GetIdentificationItemInfos( );
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        JSONArray jsonArr = jsonObject.getJSONArray("d");
                        for(int i=0;i<jsonArr.length();i++){
                            JSONObject jsonObjTem =  jsonArr.getJSONObject(i) ;
                            Log.i("GetIdentificationItem1",jsonObjTem.getString("Value"));
                            contractIdentificationItem.append(jsonObjTem.getString("Value"));
                        }
                    }
                    catch (JSONException ex){
                        Log.i("GetIdentificationItem","json对象构造错误");
                    }
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ContractApprovalListActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //获取合同审批部门
    public void GetMyApproveDeptList(){
        Call<String> call = PostRequest.Instance.request.GetMyApproveDeptList();
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        String jsonStrTemp = jsonObject.getString("d");
                        if(!jsonStrTemp.equals("[]")){
                            JSONArray arrayTemp = new JSONArray(jsonStrTemp);
                            DeptList = arrayTemp;
                            for (int i=0;i<arrayTemp.length();i++){
                                JSONObject objectTemp = arrayTemp.getJSONObject(i);
                                Log.i("部门信息",objectTemp.getString("DeptName"));
                                contractDept.append(objectTemp.getString("DeptName"));
                                DeptIDs.put(objectTemp.getString("DeptID"));
                            }
                            if (arrayTemp.length()==1){
                                contractDept.remove(0);
                            }
                        }
                    }catch (JSONException ex){
                        Log.e("GetMiicDeptInfoList",ex.getMessage());
                        Toast.makeText(ContractApprovalListActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }else{
                    JSONObject jsonObjRes = new JSONObject();
                    try{
                        jsonObjRes.put("isApprove",true);
                        jsonObjRes.put("withMiic",false);
                    }catch(JSONException ex){
                        Log.e("GetMiicDeptInfoList",ex.getMessage());
                    }
                    RequestBody view = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),jsonObjRes.toString());
                    Call<String> call2 = PostRequest.Instance.request.GetMyDeptInfoList(view);
                    Callback<String> callback2 = new Callback<String>() {
                        //请求成功时回调
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if(response.body()!=null){
                                try {
                                    JSONObject jsonObject = new JSONObject(response.body());
                                    Log.i("GetMyDeptResponse",jsonObject.toString());
                                    JSONArray jsonArr =new JSONArray(jsonObject.getString("d"));
                                    Log.i("GetMyDeptResponse",jsonArr.toString());
                                    DeptList = jsonArr;
                                    for(int i=0;i<jsonArr.length();i++){
                                        JSONObject jsonObjTem =  jsonArr.getJSONObject(i) ;
                                        contractDept.append(jsonObjTem.getString("DeptName"));
                                        DeptIDs.put(jsonObjTem.getString("DeptID"));
                                    }
                                    if (jsonArr.length()==1){
                                        contractDept.remove(0);
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
                            Toast.makeText(ContractApprovalListActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
                        }
                    };
                    PostRequest.Instance.CommonAsynPost(call2, callback2);
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = spHandler.obtainMessage();
                        spHandler.sendMessage(message);
                    }
                }).start();
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ContractApprovalListActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //获取我的合同审批查询
    public void MyApproveSearch(final boolean isFirstLoad ,JSONObject keyword){
        JSONObject requestJson = new JSONObject();
        try{
            requestJson.put("keyword",keyword.get("keyword"));
            JSONObject val1 = new JSONObject();
            val1.put("pageStart",mCurrentPage * mPageSize + 1);
            val1.put("pageEnd", (mCurrentPage + 1) *mPageSize);
            requestJson.put("page",val1);
            //当前页
            mCurrentPage=mCurrentPage+1;
        }catch (JSONException ex){
            Log.i("ContractSearch","json对象构造错误");
        }
        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Call<String> call = PostRequest.Instance.request.MyApproveSearch(keywordView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                showProgress(false);
                if(isFirstLoad==true){
                    searchResultList.clear();
                }
                if(response.body()!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        String jsonStrTemp = jsonObject.getString("d");
                        if(!jsonStrTemp.equals("[]")){
                            JSONArray arrayTemp = new JSONArray(jsonStrTemp);
                            for (int i=0;i<arrayTemp.length();i++){
                                JSONObject objectTemp = arrayTemp.getJSONObject(i);
                                Log.i("查询结果",objectTemp.toString());
                                searchResultList.add(new ContractSearchResultItem(objectTemp));
                            }
                            if(listView.getFooterViewsCount() ==0){
                                listView.setFooterView(View.inflate(ContractApprovalListActivity.this, R.layout.item_listview_footer_loadmore, null), new OnLoadMoreScrollListener.OnLoadMoreStateListener() {

                                    public void onNormal(View footView) {
                                        footView.findViewById(R.id.footer_pb_loading).setVisibility(View.GONE);
                                        footView.findViewById(R.id.footer_tv_msg).setVisibility(View.GONE);
                                    }

                                    public void onLoading(View footView) {
                                        footView.findViewById(R.id.footer_pb_loading).setVisibility(View.VISIBLE);
                                        footView.findViewById(R.id.footer_tv_msg).setVisibility(View.VISIBLE);
                                        ((TextView) footView.findViewById(R.id.footer_tv_msg)).setText("正在加载...");

                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                loadMore();
                                            }
                                        }).start();
                                    }
                                    public void onEnd(final View footView) {
                                        footView.findViewById(R.id.footer_pb_loading).setVisibility(View.GONE);
                                        ((TextView) footView.findViewById(R.id.footer_tv_msg)).setText("没有更多的资讯了");
                                        new Handler().postDelayed(new Runnable() {

                                            @Override
                                            public void run() {
                                                //do something
                                                footView.findViewById(R.id.footer_tv_msg).setVisibility(View.GONE);
                                            }
                                        }, 3000);    //延时3s执行
                                    }
                                });
                            }
                            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                @Override
                                public void onRefresh() {
                                    reFresh();
                                    searchResultItemAdapter.notifyDataSetChanged();
                                }
                            });
                            listView.requestFocus();
                            messageTip.setVisibility(View.GONE);
                            swipeRefreshLayout.setVisibility(View.VISIBLE);
                        }
                        else{
                            messageTip.setVisibility(View.VISIBLE);
                            swipeRefreshLayout.setVisibility(View.GONE);
                        }
                        searchResultItemAdapter.notifyDataSetChanged();
                    }catch (JSONException ex){
                        Log.e("ContractSearch",ex.getMessage());
                        Toast.makeText(ContractApprovalListActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }

            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ContractApprovalListActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //获取我的合同审批查询数量
    public void GetMyApproveSearchCount(final JSONObject requestJson){
        searchJson = requestJson;

        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),  requestJson.toString());
        Call<String> call = PostRequest.Instance.request.GetMyApproveSearchCount(keywordView);
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
                            //总共页数
                            mPageCount = (int)Math.ceil(count/20)+1;
                            //每页信息数量mPageSize = 20;
                            //当前页
                            mCurrentPage=0;
                            MyApproveSearch(true,requestJson);
                        }else{
                            showProgress(false);
                            messageTip.setVisibility(View.VISIBLE);
                            swipeRefreshLayout.setVisibility(View.GONE);
                            Toast.makeText(ContractApprovalListActivity.this,"暂时还没有符合搜索条件的合同信息",Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException ex){
                        Log.i("GetQuerySearchCount","json对象构造错误");
                    }
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ContractApprovalListActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    /**
     *下拉刷新事件定义
     */
    private void reFresh() {
        searchResultList.clear();
        listView.setEnd(false);
        mCurrentPage = 0;
        MyApproveSearch(false,searchJson);
        swipeRefreshLayout.setRefreshing(false);
    }
    /**
     * 上滑加载事件定义
     */
    private void loadMore() {
        if (mPageCount > 1 && mCurrentPage<mPageCount) {
            MyApproveSearch(false,searchJson);
        } else {
            ContractApprovalListActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listView.setEnd(true);
                }
            });
        }
        ContractApprovalListActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                searchResultItemAdapter.notifyDataSetChanged();
            }
        });
    }
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            searchResultView.setVisibility(show ? View.GONE : View.VISIBLE);
            searchResultView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    searchResultView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            searchProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            searchProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    searchProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            searchProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            searchResultView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
