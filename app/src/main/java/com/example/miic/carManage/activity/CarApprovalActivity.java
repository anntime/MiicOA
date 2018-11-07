package com.example.miic.carManage.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.base.http.PostRequest;
import com.example.miic.base.loadmore.LoadMoreListView;
import com.example.miic.base.loadmore.OnLoadMoreScrollListener;
import com.example.miic.base.multiLineRadioGroup.MultiLineRadioGroup;
import com.example.miic.carManage.adapter.CarSearchResultItemAdapter;
import com.example.miic.carManage.common.CarCommon;
import com.example.miic.carManage.item.CarSearchResultItem;
import com.example.miic.common.MyApplication;

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


public class CarApprovalActivity extends AppCompatActivity {
    private LinearLayout backBtn;
    private LinearLayout searchBtn;

    private SwipeRefreshLayout swipeRefreshLayout;
    private CarSearchResultItemAdapter searchResultItemAdapter;
    private List<CarSearchResultItem> searchResultList;
    private LoadMoreListView listView;
    private TextView messageTip;

    private View searchView;
    private MultiLineRadioGroup carNumGroup;
    private MultiLineRadioGroup carStateGroup;
    private MultiLineRadioGroup carDeptGroup;
    //总共页数
    private int mPageCount = 0;
    //每页信息数量
    private int mPageSize = 10;
    //当前页
    private int mCurrentPage=0;

    //记录选择的选项
    private JSONObject remberJson = new JSONObject();
    private JSONObject searchJson = new JSONObject();
    private JSONArray DeptIDs = new JSONArray();
    private JSONArray DeptList = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_approval);

        backBtn = (LinearLayout)findViewById(R.id.search_back);
        searchBtn = (LinearLayout)findViewById(R.id.car_search);

        //返回按钮事件
        backBtn.setOnClickListener(backClickListener);

        //搜索按钮监听事件
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearchView();
            }
        });

        searchView = LayoutInflater.from(this).inflate(R.layout.car_application_search_pop_window,null);//PopupWindow对象
        //车牌
        carNumGroup = (MultiLineRadioGroup) searchView.findViewById(R.id.car_num);
        //状态
        carStateGroup = (MultiLineRadioGroup) searchView.findViewById(R.id.car_state);
        carStateGroup.append("待审批");
        carStateGroup.append("审批中");
        carStateGroup.append("已完成");

        //部门
        carDeptGroup = (MultiLineRadioGroup) searchView.findViewById(R.id.car_dept);

        getCarNumInfos();
        //getCarStateInfos();
        //getCarDeptInfoList();
        GetMyApproveDeptList();

        //列表展示相关
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshview);

        searchResultList = new ArrayList<>();
        searchResultItemAdapter = new CarSearchResultItemAdapter(CarApprovalActivity.this,searchResultList);
        listView = (LoadMoreListView)findViewById(R.id.car_result_container);
        listView.setAdapter(searchResultItemAdapter);
        searchResultItemAdapter.setOnItemMyClickListener(new CarSearchResultItemAdapter.onItemMyClickListener() {
            @Override
            public void onMyClick(int type, int position) {
                if(position<searchResultList.size()) {
                    //通过view获取其内部的组件，进而进行操作
                    String clickID;
                    String statue;
                    CarSearchResultItem carSearchResultItem = searchResultList.get(position);
                    clickID = carSearchResultItem.getCarApplyID();
                    statue = carSearchResultItem.getApproveState();
                    Log.i("setOnItemClickListener",clickID);
                    Intent intent;
                    if(statue.equals("1")){
                        //状态为1的待提交状态，打开用车申请页，将参数传到申请页Activity。
                        intent = new Intent(CarApprovalActivity.this, CarApplyActivity.class);
                    }else {
                        //获得点击的新闻的id，然后把这个id传到新的activity中。
                        intent = new Intent(CarApprovalActivity.this, CarApprovalDetailActivity.class);
                    }
                    intent.putExtra("carApplyID", clickID);
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
            val1.put("Year",Calendar.getInstance().get(Calendar.YEAR)+"");
            val1.put("DeptID",DeptIDs);
            requestJson.put("keyword",val1);
        }catch (JSONException ex){
            Log.i("onCreate","json对象构造错误");
        }
        GetMyCarApprovalSearchCount(requestJson);
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
    //获取用车审核数量
    private void GetMyCarApprovalSearchCount(final JSONObject requestJson){
        searchJson = requestJson;

        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());

        Call<String> call = PostRequest.Instance.request.GetMyCarApprovalSearchCount (keywordView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println("count是：" + response.body());
                if(response.body()!=null){
                    try{
                        JSONObject jsonObject = new JSONObject(response.body());
                        int count = Integer.parseInt(jsonObject.getString("d"));
                        //判断数量不等于0
                        if(count!=0){
                            //总共页数
                            mPageCount = (int)Math.ceil(count/20)+1;
                            //每页信息数量mPageSize = 20;
                            //当前页
                            mCurrentPage=0;
                            MyCarApprovalSearch(true,requestJson);
                        }else{
                            messageTip.setVisibility(View.VISIBLE);
                            swipeRefreshLayout.setVisibility(View.GONE);
                            Toast.makeText(CarApprovalActivity.this,"暂时还没有用车审核",Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException ex){
                        Log.i("GetMyCarApproval","json对象构造错误");
                    }
                }

            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(CarApprovalActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }

    private void MyCarApprovalSearch(final boolean isFirstLoad ,JSONObject keyword){
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
        Call<String> call = PostRequest.Instance.request.MyCarApprovalSearch(newsInfoView);
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
                                searchResultList.add(new CarSearchResultItem(objectTemp));
                            }
                            if(listView.getFooterViewsCount() ==0){
                                listView.setFooterView(View.inflate(CarApprovalActivity.this, R.layout.item_listview_footer_loadmore, null), new OnLoadMoreScrollListener.OnLoadMoreStateListener() {
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
                                        ((TextView) footView.findViewById(R.id.footer_tv_msg)).setText("没有更多了");
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
                        Toast.makeText(CarApprovalActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }

            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(CarApprovalActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
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
        MyCarApprovalSearch(false,searchJson);
        swipeRefreshLayout.setRefreshing(false);
    }
    /**
     * 上滑加载事件定义
     */
    private void loadMore() {
        if (mPageCount > 1 && mCurrentPage<mPageCount) {
            MyCarApprovalSearch(false,searchJson);
        } else {
            CarApprovalActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listView.setEnd(true);
                }
            });
        }
        CarApprovalActivity.this.runOnUiThread(new Runnable() {
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

    //用车申请搜索监听事件
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
        View pView =LayoutInflater.from(this).inflate(R.layout.activity_car_approval, null);
        popupWindow.showAtLocation(pView, Gravity.CENTER,0,0);
        //实例化控件
        LinearLayout bt_cancel= (LinearLayout) searchView.findViewById(R.id.car_search_close);
        Button bt_submit= (Button) searchView.findViewById(R.id.seatch_button);
        final EditText inputET = (EditText) searchView.findViewById(R.id.search_keyword);

        if (remberJson.length()!=0&&remberJson!=null){
            try{
                //记录选择的选项
                inputET.setText(remberJson.getString("Keyword"));
                carNumGroup.setItemChecked(Integer.parseInt(remberJson.getString("CarNum")));
                carStateGroup.setItemChecked(Integer.parseInt(remberJson.getString("Status")));
                carDeptGroup.setItemChecked(Integer.parseInt(remberJson.getString("Dept")));
            }catch (JSONException ex){
                Log.i("showSearchView","json对象构造错误");
            }
        }else{
            carNumGroup.setItemChecked(0);
            carStateGroup.setItemChecked(0);
            carDeptGroup.setItemChecked(0);
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
                    remberJson.put("CarNum",carNumGroup.getCheckedItems()[0]);
                    remberJson.put("Status",carStateGroup.getCheckedItems()[0]);
                    remberJson.put("Dept",carDeptGroup.getCheckedItems()[0]);

                    CarCommon car = new CarCommon();
                    JSONObject val1 = new JSONObject();
                    val1.put("UserID", MyApplication.getUserID());
                    val1.put("Keyword",inputET.getText().toString());
                    val1.put("CarNum",car.getCarNum(carNumGroup.getCheckedValues().get(0)));
                    val1.put("Status",car.getCarStatueA(carStateGroup.getCheckedValues().get(0)));

                    if(carDeptGroup.getCheckedValues().get(0).equals("全部")){
                        val1.put("DeptID", DeptIDs);
                    }else {
                        for (int n=0;n<DeptList.length();n++){
                            if(DeptList.getJSONObject(n).getString("DeptName").equals(carDeptGroup.getCheckedValues().get(0))){
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
                GetMyCarApprovalSearchCount(requestJson);
                popupWindow.dismiss();
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
    }

    //获取车牌
    public void getCarNumInfos(){
        String str = "{userID:'" + MyApplication.getUserID() + "'}";
        RequestBody userView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),str);
        Call<String> call = PostRequest.Instance.request.GetCarNumInfos(userView);
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
                            carNumGroup.append(jsonObjTem.getString("Value"));
                        }
                    }
                    catch (JSONException ex){
                        Log.i("getCarNumInfos","json对象构造错误");
                    }


                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(CarApprovalActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }

    //获取用车审批部门
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
                                carDeptGroup.append(objectTemp.getString("DeptName"));
                                DeptIDs.put(objectTemp.getString("DeptID"));
                            }
                            if (arrayTemp.length()==1){
                                carDeptGroup.remove(0);
                            }
                        }
                    }catch (JSONException ex){
                        Log.e("GetMiicDeptInfoList",ex.getMessage());
                        Toast.makeText(CarApprovalActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
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
                                        carDeptGroup.append(jsonObjTem.getString("DeptName"));
                                    }
                                    if (jsonArr.length()==1){
                                        carDeptGroup.remove(0);
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
                            Toast.makeText(CarApprovalActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(CarApprovalActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }

    //获取用车申请部门
    public void getCarDeptInfoList(){
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
                        Log.i("GetLoginInfo",new CarCommon().getYesOrNo("No"));

                        String manage = jsonObj.getString("Manage")+"";
                        Log.i("GetLoginInfo",manage);
                        Log.i("GetLoginInfo",(manage.equals(new CarCommon().getYesOrNo("No"))+"1"));
                        if(manage.equals(new CarCommon().getYesOrNo("No"))){
                            //不是
                            carDeptGroup.append(jsonObj.getString("OrgName"));
                            carDeptGroup.remove(0);
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
                                            Log.i("GetCarDeptResponse",jsonObject.toString());
                                            JSONArray jsonArr =new JSONArray(jsonObject.getString("d"));
                                            Log.i("GetCarDeptResponse",jsonArr.toString());
                                            for(int i=0;i<jsonArr.length();i++){
                                                JSONObject jsonObjTem =  jsonArr.getJSONObject(i) ;
                                                carDeptGroup.append(jsonObjTem.getString("DeptName"));
                                            }
                                            if (jsonArr.length()==1){
                                                carDeptGroup.remove(0);
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
                                    Toast.makeText(CarApprovalActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
                                }
                            };
                            PostRequest.Instance.CommonAsynPost(call2, callback2);
                        }
                    }
                    catch (JSONException ex){
                        Log.i("getCarDeptInfoList","json对象构造错误");
                    }
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(CarApprovalActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);

    }

}
