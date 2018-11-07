package com.example.miic.contractManage.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.base.http.PostRequest;
import com.example.miic.base.multiLineRadioGroup.MultiLineRadioGroup;
import com.example.miic.contractManage.adapter.ContractListItemAdapter;
import com.example.miic.contractManage.common.ContractCommon;
import com.example.miic.contractManage.item.ContractListItem;
import com.zhl.CBPullRefresh.CBPullRefreshListView;
import com.zhl.CBPullRefresh.SwipeMenu.SwipeMenu;
import com.zhl.CBPullRefresh.SwipeMenu.SwipeMenuCreator;
import com.zhl.CBPullRefresh.SwipeMenu.SwipeMenuItem;

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

public class ContractFinishActivity extends AppCompatActivity {

    private View searchView;
    //状态
    private MultiLineRadioGroup contractStatus;
    //标识项
    private MultiLineRadioGroup contractIdentificationItem;
    //合同类型1
    private MultiLineRadioGroup contractTypeOne;
    //合同类型2
    private MultiLineRadioGroup contractTypeTwo;
    //年份
    private MultiLineRadioGroup contractYear;
    //部门
    private MultiLineRadioGroup contractDept;
    private EditText inputET;
    private ScrollView scrollView;

    private CBPullRefreshListView mListView;
    private ContractListItemAdapter searchResultItemAdapter;
    private List<ContractListItem> searchResultList;
    private TextView messageTip;

    private View searchResultView;
    private View searchProgressView;


    //记录选择的选项
    private JSONObject remberJson = new JSONObject();
    private JSONObject searchJson = new JSONObject();
    //总共页数
    private int mPageCount = 0;
    //每页信息数量
    private int mPageSize = 10;
    //当前页
    private int mCurrentPage=0;

    //搜索类型
    private String[] contractStatusList;
    private JSONArray DeptList = new JSONArray();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_finish);
        initView();
        setHeader();
        initSearchView();
    }
    private void setHeader() {
        TextView titleTv = (TextView) findViewById(R.id.page_title);
        LinearLayout backLin = (LinearLayout)findViewById(R.id.back);
        LinearLayout rightLin = (LinearLayout)findViewById(R.id.menu);
        ImageView menuImage = (ImageView)findViewById(R.id.menu_image);
        FrameLayout.LayoutParams params= new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout searchViewContainer = (LinearLayout)searchView.findViewById(R.id.search_view_container);
        params = (FrameLayout.LayoutParams)searchViewContainer.getLayoutParams();
        showProgress(true);
        JSONObject requestJson = new JSONObject();


                titleTv.setText("合同完成");
                params.setMargins(0, 180, 0, 0);
                searchViewContainer.setLayoutParams(params);
                contractStatusList = new String[]{"执行中", "合同完成", "合同终止"};
                GetMiicDeptInfoList();
                try{
                    JSONObject val1 = new JSONObject();
                    val1.put("Keyword","");
                    val1.put("IdentificationItem","0");
                    val1.put("Year",Calendar.getInstance().get(Calendar.YEAR)+"");
                    val1.put("ContractTypeOne","99");
                    val1.put("ContractTypeTwo","99");
                    JSONArray Status = new JSONArray();
                    Status.put("4");
                    Status.put("8");
                    Status.put("9");
                    val1.put("Status",Status);
                    val1.put("DeptID","");
                    requestJson.put("keyword",val1);
                }catch (JSONException ex){
                    Log.i("onCreate","json对象构造错误");
                }
                GetContractFinishSearchCount(requestJson);

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
    private void initSearchView(){
        initContractStatus(contractStatusList);
        GetIdentificationItemInfos();
        initContractTypeOne();
        initContractTypeTwo();
        initContractYear();
    }
    private void showSearchView(){
        //启动popup windows，显示搜索页面
        final PopupWindow popupWindow=new PopupWindow(searchView, WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT,true);
        //设置PopupWindow布局
        //popupWindow.setContentView(view1);
        //设置宽高
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

        //inputET.setFocusable(false);
        if (remberJson.length()!=0&&remberJson!=null){
            try{
                //记录选择的选项
                inputET.setText(remberJson.getString("Keyword"));

                contractStatus.setItemChecked(Integer.parseInt(remberJson.getString("Status")));
                contractIdentificationItem.setItemChecked(Integer.parseInt(remberJson.getString("IdentificationItem")));
                contractTypeOne.setItemChecked(Integer.parseInt(remberJson.getString("ContractTypeOne")));
                contractTypeTwo.setItemChecked(Integer.parseInt(remberJson.getString("ContractTypeTwo")));
                contractYear.setItemChecked(Integer.parseInt(remberJson.getString("Year")));
                contractDept.setItemChecked(Integer.parseInt(remberJson.getString("DeptID")));
            }catch (JSONException ex){
                Log.i("showSearchView","json对象构造错误");
            }
        }else{
            contractStatus.setItemChecked(0);
            contractIdentificationItem.setItemChecked(0);
            contractTypeOne.setItemChecked(0);
            contractTypeTwo.setItemChecked(0);
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
                    remberJson.put("ContractTypeOne",contractTypeOne.getCheckedItems()[0]);
                    remberJson.put("ContractTypeTwo",contractTypeTwo.getCheckedItems()[0]);
                    remberJson.put("DeptID",contractDept.getCheckedItems()[0]);
                    remberJson.put("Year",contractYear.getCheckedItems()[0]);

                    ContractCommon contract = new ContractCommon();
                    JSONObject val1 = new JSONObject();
                    val1.put("Keyword",inputET.getText().toString());
                    val1.put("ContractTypeOne",contract.getContractTypeOne(contractTypeOne.getCheckedValues().get(0)));
                    val1.put("ContractTypeTwo",contract.getContractTypeTwo(contractTypeTwo.getCheckedValues().get(0)));
                    val1.put("IdentificationItem",contract.getIdentificationItem(contractIdentificationItem.getCheckedValues().get(0)));
                    val1.put("Status", contract.getContractStatue(contractStatus.getCheckedValues().get(0)));

                    if(contractDept.getCheckedValues().get(0)=="全部"){
                        val1.put("DeptID", "");
                    }else {
                        for (int n=0;n<DeptList.length();n++){
                            if(DeptList.getJSONObject(n).getString("DeptName")==contractDept.getCheckedValues().get(0)){
                                val1.put("DeptID",DeptList.getJSONObject(n).getString("DeptID"));
                            }
                        }
                    }


                    val1.put("Year",contractYear.getCheckedValues().get(0)=="全部"?"":contractYear.getCheckedValues().get(0));
                    requestJson.put("keyword",val1);

                }catch (JSONException ex){
                    Log.i("showSearchView","json对象构造错误");
                }
                Log.i("搜索结果",requestJson.toString());

                //显示合同完成相关搜索界面
                GetContractFinishSearchCount(requestJson);
                popupWindow.dismiss();
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
    }

    private void initView() {
        //初始化数据（类似于清空数据）
        remberJson = new JSONObject();
        searchJson = new JSONObject();
        //总共页数
        mPageCount = 0;
        //每页信息数量
        mPageSize = 10;
        //当前页
        mCurrentPage=0;
        //搜索界面
        searchView = LayoutInflater.from(this).inflate(R.layout.contract_search_pop_window,null);//PopupWindow对象;
        contractStatus = (MultiLineRadioGroup)searchView.findViewById(R.id.contract_status);
        contractIdentificationItem = (MultiLineRadioGroup)searchView.findViewById(R.id.contract_identification_item);
        contractTypeOne = (MultiLineRadioGroup)searchView.findViewById(R.id.contract_type_one);
        contractTypeTwo = (MultiLineRadioGroup)searchView.findViewById(R.id.contract_type_two);
        contractYear = (MultiLineRadioGroup)searchView.findViewById(R.id.contract_year);
        contractDept = (MultiLineRadioGroup)searchView.findViewById(R.id.contract_dept);
        scrollView = (ScrollView)searchView.findViewById(R.id.scroll_view);
        inputET = (EditText) searchView.findViewById(R.id.search_keyword);
        //inputET.setFocusable(false);
        scrollView.setFillViewport(true);

        //列表相关
        mListView = (CBPullRefreshListView) findViewById(R.id.listview);
        searchResultList = new ArrayList<>();
        searchResultItemAdapter = new ContractListItemAdapter(ContractFinishActivity.this,searchResultList);


        mListView.setAdapter(searchResultItemAdapter);
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadMoreEnable(true);
        mListView.setSwipeEnable(true);
        mListView.showTopSearchBar(false);

        mListView.setOnPullRefreshListener(new CBPullRefreshListView.OnPullRefreshListener() {
            @Override
            public void onRefresh() {
                searchResultList.clear();
                mCurrentPage = 0;
                //显示合同完成相关搜索界面
                ContractFinishSearch(false,searchJson);
                mListView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mListView.stopRefresh();
                    }
                }, 3000);
            }

            @Override
            public void onLoadMore() {
                if (mPageCount > 1 && mCurrentPage<mPageCount) {
                    //显示合同完成相关搜索界面
                    ContractFinishSearch(false,searchJson);
                }
                mListView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mListView.stopLoadMore();
                    }
                }, 3000);
            }

            @Override
            public void onUpdateRefreshTime(long time) {

            }

        });

        mListView.setOnMenuItemClickListener(new CBPullRefreshListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                Toast.makeText(ContractFinishActivity.this, "点击了item swipe 菜单的第" + index, Toast.LENGTH_SHORT).show();
            }
        });
        mListView.setOnItemClickListener(new CBPullRefreshListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(ContractFinishActivity.this,"点击了"+position,Toast.LENGTH_SHORT).show();
                //显示合同完成界面---待处理，可能需要添加新的空间。这个合同完成只有几个按钮就可以不需要详情页，合同完成操作、合同终止操作、取消完成操作
                //Toast.makeText(ContractListActivity.this,"合同完成操作~",Toast.LENGTH_LONG).show();
                String clickID = searchResultList.get(position).getContractID();
                Intent intent = new Intent(ContractFinishActivity.this, ContractDetailActivity.class);
                //获得点击的新闻的id，然后把这个id传到新的activity中。
                intent.putExtra("clickID", clickID);
                startActivityForResult(intent, 1);
            }
        });

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                SwipeMenuItem collectionItem = new SwipeMenuItem(getApplicationContext());
                collectionItem.setBackground(R.color.green);
                collectionItem.setWidth(dp2px(ContractFinishActivity.this, 90));
                collectionItem.setTitle("完成");
                collectionItem.setTitleSize(18);
                collectionItem.setTitleColor(Color.WHITE);
                collectionItem.setIcon(R.drawable.contract_finish_list);
                menu.addMenuItem(collectionItem);

                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                deleteItem.setBackground(R.color.red);
                deleteItem.setWidth(dp2px(ContractFinishActivity.this, 90));
                deleteItem.setTitle("终止");
                deleteItem.setIcon(R.drawable.contract_finish_list);
                deleteItem.setTitleSize(18);
                deleteItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        mListView.setMenuCreator(creator);
        messageTip = (TextView)findViewById(R.id.message_tip);

        searchResultView = findViewById(R.id.search_result);
        searchProgressView = findViewById(R.id.search_progress);
    }
    //获取全部部门
    public void GetMiicDeptInfoList(){
        Call<String> call = PostRequest.Instance.request.GetMiicDeptInfoList();
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
                                contractDept.append(objectTemp.getString("DeptName"));
                                Log.i("部门信息",objectTemp.getString("DeptName"));
                            }
                            if (arrayTemp.length()==1){
                                contractDept.remove(0);
                            }
                        }
                    }catch (JSONException ex){
                        Log.e("GetMiicDeptInfoList",ex.getMessage());
                        Toast.makeText(ContractFinishActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ContractFinishActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    private void initContractStatus(String[] contractStatusList){
        for (int i=0;i<contractStatusList.length;i++){
            contractStatus.append(contractStatusList[i]);
        }
    }
    private void initContractTypeOne(){
        contractTypeOne.append("收款");
        contractTypeOne.append("付款");
        contractTypeOne.append("其他");
    }
    private void initContractTypeTwo(){
        contractTypeTwo.append("开发合同");
        contractTypeTwo.append("采购合同");
        contractTypeTwo.append("服务合同");
        contractTypeTwo.append("销售合同");
        contractTypeTwo.append("其他合同");
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
                Toast.makeText(ContractFinishActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //获取合同完成查询
    public void ContractFinishSearch(final boolean isFirstLoad ,JSONObject keyword){
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
        Call<String> call = PostRequest.Instance.request.ContractFinishSearch(keywordView);
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
                                searchResultList.add(new ContractListItem(objectTemp));

                            }


                            mListView.requestFocus();
                            messageTip.setVisibility(View.GONE);
                            mListView.setVisibility(View.VISIBLE);
                        }
                        else{
                            messageTip.setVisibility(View.VISIBLE);
                            mListView.setVisibility(View.GONE);
                        }
                        searchResultItemAdapter.notifyDataSetChanged();
                    }catch (JSONException ex){
                        Log.e("ContractSearch",ex.getMessage());
                        Toast.makeText(ContractFinishActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }

            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ContractFinishActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //获取合同完成查询数量
    public void GetContractFinishSearchCount(final JSONObject requestJson){
        searchJson = requestJson;

        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString() );
        Call<String> call = PostRequest.Instance.request.GetContractFinishSearchCount(keywordView);
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
                            ContractFinishSearch(true,requestJson);
                        }else{
                            messageTip.setVisibility(View.VISIBLE);
                            mListView.setVisibility(View.GONE);
                            Toast.makeText(ContractFinishActivity.this,"暂时还没有符合搜索条件的合同信息",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ContractFinishActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    public int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
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
