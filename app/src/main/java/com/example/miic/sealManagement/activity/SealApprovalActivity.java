package com.example.miic.sealManagement.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.base.http.PostRequest;
import com.example.miic.base.loadmore.LoadMoreListView;
import com.example.miic.base.loadmore.OnLoadMoreScrollListener;
import com.example.miic.base.multiLineRadioGroup.MultiLineRadioGroup;
import com.example.miic.common.MyApplication;
import com.example.miic.oa.news.item.InfoPageNews;
import com.example.miic.oa.news.item.InfoPageNewsComment;
import com.example.miic.sealManagement.adapter.SealSearchResultItemAdapter;
import com.example.miic.sealManagement.common.SealCommon;
import com.example.miic.sealManagement.item.SealSearchResultItem;
import com.example.miic.share.activity.ShareFriendsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SealApprovalActivity extends AppCompatActivity {
    private LinearLayout backBtn;
    private LinearLayout searchBtn;


    private SwipeRefreshLayout swipeRefreshLayout;
    private SealSearchResultItemAdapter searchResultItemAdapter;
    private List<SealSearchResultItem> searchResultList;
    private LoadMoreListView listView;
    private TextView messageTip;

    private View searchView;
    private MultiLineRadioGroup useSealTypeGroup;
    private MultiLineRadioGroup sealTypeGroup;
    private MultiLineRadioGroup sealStateGroup;
    private MultiLineRadioGroup sealYearGroup;
    private MultiLineRadioGroup sealDeptGroup;
    //总共页数
    private int mPageCount = 0;
    //每页信息数量
    private int mPageSize = 10;
    //当前页
    private int mCurrentPage=0;

    //记录选择的选项
    private JSONObject remberJson = new JSONObject();
    private JSONObject searchJson = new JSONObject();
    private JSONArray DeptList = new JSONArray();
    private String OrgID = "";
    private String OrgName = "";
    private JSONArray DeptIDs = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seal_approval);

        backBtn = (LinearLayout)findViewById(R.id.search_back);
        searchBtn = (LinearLayout)findViewById(R.id.seal_search);

        //返回按钮事件
        backBtn.setOnClickListener(backClickListener);

        //搜索按钮监听事件
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearchView();
            }
        });

        searchView = LayoutInflater.from(this).inflate(R.layout.seal_application_search_pop_window,null);//PopupWindow对象
        //用印类型
        useSealTypeGroup = (MultiLineRadioGroup) searchView.findViewById(R.id.use_seal_type);
        //印章类型
        sealTypeGroup = (MultiLineRadioGroup) searchView.findViewById(R.id.seal_types);
        //状态
        sealStateGroup = (MultiLineRadioGroup) searchView.findViewById(R.id.seal_state);
        sealStateGroup.append("待审批");
        sealStateGroup.append("审批中");
        sealStateGroup.append("已完成");
        //年份
        sealYearGroup = (MultiLineRadioGroup) searchView.findViewById(R.id.seal_year);
        Calendar rightNow = Calendar.getInstance();
        int year = rightNow.get(Calendar.YEAR);
        sealYearGroup.append(year+1+"");
        sealYearGroup.append(year+"");
        sealYearGroup.append(year-1+"");
        sealYearGroup.append(year-2+"");
        //sealYearGroup.append(year-3+"");
        //sealYearGroup.append(year-4+"");
        //部门
        sealDeptGroup = (MultiLineRadioGroup) searchView.findViewById(R.id.seal_dept);
        getSealTypeInfos();
        getSealApplicationTypeInfos();
        //getMyDeptInfoList();
        GetMyApproveDeptList();

        //列表展示相关
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshview);
        searchResultList = new ArrayList<>();
        searchResultItemAdapter = new SealSearchResultItemAdapter(SealApprovalActivity.this,searchResultList);
        listView = (LoadMoreListView)findViewById(R.id.seal_result_container);
        listView.setAdapter(searchResultItemAdapter);
        searchResultItemAdapter.setOnItemMyClickListener(new SealSearchResultItemAdapter.onItemMyClickListener() {
            @Override
            public void onMyClick(int type, int position) {
                if(position<searchResultList.size()) {
                    //通过view获取其内部的组件，进而进行操作
                    String clickID;
                    String statue;
                    SealSearchResultItem sealItem = searchResultList.get(position);
                    clickID = sealItem.getSealApplyID();
                    statue = sealItem.getApproveState();
                    Log.i("setOnItemClickListener",clickID);
                    Intent intent;
                    if(statue.equals("1")){
                        intent = new Intent(SealApprovalActivity.this, SealApplyActivity.class);
                    }else {
                        //获得点击的新闻的id，然后把这个id传到新的activity中。
                        intent = new Intent(SealApprovalActivity.this, SealApprovalDetailActivity.class);
                    }
                    intent.putExtra("sealApplyID", clickID);
                    startActivityForResult(intent, 1);
                }
            }


        });
        messageTip = (TextView)findViewById(R.id.message_tip);


        JSONObject requestJson = new JSONObject();
        try{
            JSONObject val1 = new JSONObject();
            val1.put("Keyword","");
            val1.put("Status","0");
            val1.put("ApplicationType","0");
            val1.put("SealType","0");
            val1.put("DeptID",DeptIDs);
            val1.put("Year",Calendar.getInstance().get(Calendar.YEAR)+"");
            requestJson.put("keyword",val1);
        }catch (JSONException ex){
            Log.i("onCreate","json对象构造错误");
        }
        GetMySealApplicationSearchCount(requestJson);
    }

    //返回这个活动时，刷新页面
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                break;
            default:
                //后台请求数据
                reFresh();
                break;
        }
    }

    private void GetMySealApplicationSearchCount(final JSONObject requestJson){
        searchJson = requestJson;

        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());

        Call<String> call = PostRequest.Instance.request.GetMySealApprovalSearchCount (keywordView);
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
                            MySealApplicationSearch(true,requestJson);
                        }else{
                            messageTip.setVisibility(View.VISIBLE);
                            swipeRefreshLayout.setVisibility(View.GONE);
                            Toast.makeText(SealApprovalActivity.this,"暂时还没有用印申请",Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException ex){
                        Log.i("GetMySealApplication","json对象构造错误");
                    }
                }

            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(SealApprovalActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }

    private void MySealApplicationSearch(final boolean isFirstLoad ,JSONObject keyword){
        JSONObject requestJson = new JSONObject();
        //{keyword:{"Keyword":"","SealType":"0","ApplicationType":"0","userID":"admin","Year":""}
        // ,page:{"pageStart":1,"pageEnd":10}}
        try{
            requestJson.put("keyword",keyword.get("keyword"));
            JSONObject val1 = new JSONObject();
            val1.put("pageStart",mCurrentPage * mPageSize + 1);
            val1.put("pageEnd", (mCurrentPage + 1) *mPageSize);
            requestJson.put("page",val1);
            //当前页
            mCurrentPage=mCurrentPage+1;
        }catch (JSONException ex){
            Log.i("InfoChildFragment","json对象构造错误");
        }
        RequestBody newsInfoView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.MySealApprovalSearch(newsInfoView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
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
                                searchResultList.add(new SealSearchResultItem(objectTemp));
                            }
                            if(listView.getFooterViewsCount() ==0){
                                listView.setFooterView(View.inflate(SealApprovalActivity.this, R.layout.item_listview_footer_loadmore, null), new OnLoadMoreScrollListener.OnLoadMoreStateListener() {
                                    @Override
                                    public void onNormal(View footView) {
                                        footView.findViewById(R.id.footer_pb_loading).setVisibility(View.GONE);
                                        footView.findViewById(R.id.footer_tv_msg).setVisibility(View.GONE);
                                    }

                                    @Override
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
                                    @Override
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
                        }else{
                            messageTip.setVisibility(View.VISIBLE);
                            swipeRefreshLayout.setVisibility(View.GONE);
                        }
                        searchResultItemAdapter.notifyDataSetChanged();
                    }catch (JSONException ex){
                        Log.e("InfoChildFragment",ex.getMessage());
                        Toast.makeText(SealApprovalActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }

            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(SealApprovalActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
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
        MySealApplicationSearch(false,searchJson);
        swipeRefreshLayout.setRefreshing(false);
    }
    /**
     * 上滑加载事件定义
     */
    private void loadMore() {
        if (mPageCount > 1 && mCurrentPage<mPageCount) {
            MySealApplicationSearch(false,searchJson);
        } else {
            SealApprovalActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listView.setEnd(true);
                }
            });
        }
        SealApprovalActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                searchResultItemAdapter.notifyDataSetChanged();
            }
        });
    }

    //返回按钮绑定事件
    private View.OnClickListener backClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };

    //用印申请搜索监听事件
    private void showSearchView(){
        //启动popup windows，显示搜索页面
        final PopupWindow popupWindow=new PopupWindow(searchView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,true);
        //设置PopupWindow布局
        //popupWindow.setContentView(view1);
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
        View pView =LayoutInflater.from(this).inflate(R.layout.activity_seal_approval, null);
        popupWindow.showAtLocation(pView, Gravity.CENTER,0,0);
        //实例化控件
        LinearLayout bt_cancel= (LinearLayout) searchView.findViewById(R.id.seal_search_close);
        Button bt_submit= (Button) searchView.findViewById(R.id.seatch_button);
        final EditText inputET = (EditText) searchView.findViewById(R.id.search_keyword);

        if (remberJson.length()!=0&&remberJson!=null){
            try{
                //记录选择的选项
                inputET.setText(remberJson.getString("Keyword"));
                useSealTypeGroup.setItemChecked(Integer.parseInt(remberJson.getString("ApplicationType")));
                sealTypeGroup.setItemChecked(Integer.parseInt(remberJson.getString("SealType")));
                sealStateGroup.setItemChecked(Integer.parseInt(remberJson.getString("Status")));
                sealYearGroup.setItemChecked(Integer.parseInt(remberJson.getString("Dept")));
                sealDeptGroup.setItemChecked(Integer.parseInt(remberJson.getString("Year")));
            }catch (JSONException ex){
                Log.i("showSearchView","json对象构造错误");
            }
        }else{
            useSealTypeGroup.setItemChecked(0);
            sealTypeGroup.setItemChecked(0);
            sealStateGroup.setItemChecked(0);
            sealYearGroup.setItemChecked(0);
            sealDeptGroup.setItemChecked(0);
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
                    remberJson.put("SealType",sealTypeGroup.getCheckedItems()[0]);
                    remberJson.put("ApplicationType",useSealTypeGroup.getCheckedItems()[0]);
                    remberJson.put("Status",sealStateGroup.getCheckedItems()[0]);
                    remberJson.put("Dept",sealDeptGroup.getCheckedItems()[0]);
                    remberJson.put("Year",sealYearGroup.getCheckedItems()[0]);

                    SealCommon seal = new SealCommon();
                    JSONObject val1 = new JSONObject();
                    val1.put("Keyword",inputET.getText().toString());
                    val1.put("SealType",seal.getSealUseType(sealTypeGroup.getCheckedValues().get(0)));
                    val1.put("ApplicationType",seal.getSealType(useSealTypeGroup.getCheckedValues().get(0)));
                    val1.put("Status", seal.getSealStatueA(sealStateGroup.getCheckedValues().get(0)));
                    if(DeptList.length()==0){
                        val1.put("DeptID",DeptIDs);
                    }else{
                        for (int n=0;n<DeptList.length();n++){
                            if(DeptList.getJSONObject(n).getString("DeptName")==sealDeptGroup.getCheckedValues().get(0)){
                                JSONArray arr = new JSONArray();
                                arr.put(DeptList.getJSONObject(n).getString("DeptID"));
                                val1.put("DeptID",arr);
                            }
                        }
                    }

                    val1.put("Year",sealYearGroup.getCheckedValues().get(0).equals("全部")?"":sealYearGroup.getCheckedValues().get(0));
                    requestJson.put("keyword",val1);

                }catch (JSONException ex){
                    Log.i("showSearchView","json对象构造错误");
                }
                Log.i("搜索结果",requestJson.toString());
                GetMySealApplicationSearchCount(requestJson);
                popupWindow.dismiss();
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
    }

    //获取印章类型
    public void getSealTypeInfos(){
        Call<String> call = PostRequest.Instance.request.GetSealTypeInfos( );
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
                            sealTypeGroup.append(jsonObjTem.getString("Value"));
                        }
                    }
                    catch (JSONException ex){
                        Log.i("getSealTypeInfos","json对象构造错误");
                    }


                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(SealApprovalActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //获取用印类型
    public void getSealApplicationTypeInfos(){
        Call<String> call = PostRequest.Instance.request.GetSealApplicationTypeInfos( );
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
                            useSealTypeGroup.append(jsonObjTem.getString("Value"));
                        }
                    }
                    catch (JSONException ex){
                        Log.i("getSealApplicationType","json对象构造错误");
                    }

                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(SealApprovalActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }

    //获取用印审批部门
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
                                sealDeptGroup.append(objectTemp.getString("DeptName"));
                                DeptIDs.put(objectTemp.getString("DeptID"));
                            }
                            if (arrayTemp.length()==1){
                                sealDeptGroup.remove(0);
                            }
                        }
                    }catch (JSONException ex){
                        Log.e("GetMiicDeptInfoList",ex.getMessage());
                        Toast.makeText(SealApprovalActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
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
                                        sealDeptGroup.append(jsonObjTem.getString("DeptName"));
                                    }
                                    if (jsonArr.length()==1){
                                        sealDeptGroup.remove(0);
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
                            Toast.makeText(SealApprovalActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
                        }
                    };
                    PostRequest.Instance.CommonAsynPost(call2, callback2);
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(SealApprovalActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //获取用印申请部门
    public void getMyDeptInfoList(){
        String str = "{userID:'" + MyApplication.getUserID() + "'}";
        RequestBody userView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),str);
        Call<String> call = PostRequest.Instance.request.GetLoginInfo(userView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        Log.i("GetLoginInfoResponse",jsonObject.toString());
                        JSONObject jsonObj= new JSONObject(jsonObject.getString("d"));
                        Log.i("GetLoginInfo",jsonObj.toString());
                        Log.i("GetLoginInfo",new SealCommon().getYesOrNo("No"));

                        String manage = jsonObj.getString("Manage")+"";
                        Log.i("GetLoginInfo",manage);
                        Log.i("GetLoginInfo",(manage.equals(new SealCommon().getYesOrNo("No"))+"1"));
                        if(manage.equals(new SealCommon().getYesOrNo("No"))){
                            //不是
                            sealDeptGroup.append(jsonObj.getString("OrgName"));
                            OrgID = jsonObj.getString("OrgID");
                            OrgName = jsonObj.getString("OrgName");
                            sealDeptGroup.remove(0);
                        }else{
                            JSONObject obj = new JSONObject();
                            obj.put("isApprove",true);
                            obj.put("withMiic",true);
                            RequestBody view = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),obj.toString());

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
                                                sealDeptGroup.append(jsonObjTem.getString("DeptName"));
                                            }
                                            if (jsonArr.length()==1){
                                                sealDeptGroup.remove(0);
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
                                    Toast.makeText(SealApprovalActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
                                }
                            };
                            PostRequest.Instance.CommonAsynPost(call2, callback2);
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
                Toast.makeText(SealApprovalActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);

    }
}
