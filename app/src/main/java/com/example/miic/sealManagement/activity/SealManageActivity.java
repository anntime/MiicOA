package com.example.miic.sealManagement.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.base.http.PostRequest;
import com.example.miic.base.loadmore.LoadMoreListView;
import com.example.miic.base.loadmore.OnLoadMoreScrollListener;
import com.example.miic.common.MyApplication;
import com.example.miic.oa.news.item.InfoPageNews;
import com.example.miic.sealManagement.adapter.SealSearchResultItemAdapter;
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

public class SealManageActivity extends AppCompatActivity {

    private LinearLayout backBtn;
    private LinearLayout addSealApprove;
    private LinearLayout moreCondition;
    private LinearLayout moreConditionContainer;
    private EditText searcET;
    private RadioGroup sealType;
    private RadioButton sealTypeAll;
    private RadioButton sealTypeNow;
    private RadioButton sealTypeOut;
    private RadioGroup applyState;
    private RadioButton applyStateAll;
    private RadioButton applyStateEdit;
    private RadioButton applyStateIng;
    private RadioButton applyStateFinish;
    private RadioGroup searchYear;
    private RadioButton allYear;
    private RadioButton previousYear;
    private RadioButton lastYear;
    private RadioButton currentYear;

    private String searchKeyword="";
    private String sealTypeStr="0";
    private String sealTypeUserStr="0";
    private String applyStateStr="0";
    private String searchYearStr="";

    private LinearLayout findBtn;
    private LinearLayout addSealApply;

    private SwipeRefreshLayout swipeRefreshLayout;
    private SealSearchResultItemAdapter searchResultItemAdapter;
    private List<SealSearchResultItem> searchResultList;
    private LoadMoreListView listView;
    private TextView messageTip;

    //总共页数
    private int mPageCount = 0;
    //每页信息数量
    private int mPageSize = 10;
    //当前页
    private int mCurrentPage=0;

    private int  IsMoreConditionContainerShow = View.GONE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seal_manage);

        backBtn = (LinearLayout)findViewById(R.id.search_back);
        addSealApply = (LinearLayout)findViewById(R.id.add_seal_approve);
        moreCondition =(LinearLayout)findViewById(R.id.more_condition);
        moreConditionContainer = (LinearLayout)findViewById(R.id.more_condition_container);
        searcET = (EditText) findViewById(R.id.search_keyword);
        searcET.clearFocus();
        //印章类型
        sealType = (RadioGroup)findViewById(R.id.seal_type);
        sealTypeAll = (RadioButton)findViewById(R.id.all_seal_type);
        sealTypeNow = (RadioButton)findViewById(R.id.now_seal_type);
        sealTypeOut = (RadioButton)findViewById(R.id.out_seal_type);
        //申请状态
        applyState = (RadioGroup)findViewById(R.id.apply_state);
        applyStateAll = (RadioButton)findViewById(R.id.all_apply_state);
        applyStateEdit = (RadioButton)findViewById(R.id.edit_apply_state);
        applyStateIng = (RadioButton)findViewById(R.id.applying_state);
        applyStateFinish = (RadioButton)findViewById(R.id.finish_apply_state);
        //年份
        searchYear = (RadioGroup)findViewById(R.id.search_year);
        previousYear = (RadioButton)findViewById(R.id.previous_year);
        lastYear = (RadioButton)findViewById(R.id.last_year);
        currentYear = (RadioButton)findViewById(R.id.current_year);
        allYear = (RadioButton)findViewById(R.id.all_year);
        Calendar rightNow = Calendar.getInstance();
        int year = rightNow.get(Calendar.YEAR);
        previousYear.setText(year-2+"");
        lastYear.setText(year-1+"");
        currentYear.setText(year+"");

        findBtn = (LinearLayout)findViewById(R.id.find_seal_manage);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshview);
        searchResultList = new ArrayList<>();
        searchResultItemAdapter = new SealSearchResultItemAdapter(SealManageActivity.this,searchResultList);
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
                        intent = new Intent(SealManageActivity.this, SealApplyActivity.class);
                    }else {
                        //获得点击的新闻的id，然后把这个id传到新的activity中。
                        intent = new Intent(SealManageActivity.this, SealManageDetailActivity.class);
                    }
                    intent.putExtra("sealApplyID", clickID);
                    startActivityForResult(intent, 1);
                }
            }


        });
        messageTip = (TextView)findViewById(R.id.message_tip);
        searchKeyword = searcET.getText().toString();

        init();
//        showResult();
        GetMySealApplicationSearchCount();
    }

    private void init(){
        //返回按钮事件
        backBtn.setOnClickListener(backClickListener);
        //回车搜索
        searcET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == keyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    //隐藏键盘
                    ((InputMethodManager) SealManageActivity.this.getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(SealManageActivity.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    // 搜索用印申请
                    GetMySealApplicationSearchCount();
                    searcET.clearFocus();
                }
                return false;
            }
        });
        //更多条件点击展开
        moreCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(IsMoreConditionContainerShow==View.GONE){
                    moreConditionContainer.setVisibility(View.VISIBLE);
                    IsMoreConditionContainerShow=View.VISIBLE;
                }else {
                    moreConditionContainer.setVisibility(View.GONE);
                    IsMoreConditionContainerShow=View.GONE;
                }

            }
        });
        //印章类型单选框选中事件
        sealType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton rb = (RadioButton)SealManageActivity.this.findViewById(radioGroup.getCheckedRadioButtonId());
                switch(rb.getText().toString()){
                    case "全部":
                        sealTypeUserStr ="0";
                        break;
                    case "即时用印":
                        sealTypeUserStr = "1";
                        break;
                    case "携印外出":
                        sealTypeUserStr = "2";
                        break;
                    default:
                        break;
                }
            }
        });
        //申请状态单选框选中事件
        applyState.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = (RadioButton)SealManageActivity.this.findViewById(radioGroup.getCheckedRadioButtonId());
                switch(rb.getText().toString()){
                    case "全部":
                        applyStateStr ="0";
                        break;
                    case "编辑中":
                        applyStateStr = "1";
                        break;
                    case "审批中":
                        applyStateStr = "2";
                        break;
                    case "已完成":
                        applyStateStr = "3";
                        break;
                    default:
                        break;
                }

            }
        });
        //搜索年份单选框选中事件
        searchYear.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = (RadioButton)SealManageActivity.this.findViewById(radioGroup.getCheckedRadioButtonId());
                searchYearStr = rb.getText().toString().equals("全部")?"":rb.getText().toString() ;
            }
        });
        //放大镜搜索事件
        findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //查询印章
                GetMySealApplicationSearchCount();
            }
        });
        //新增印章申请事件
        addSealApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SealManageActivity.this,SealApplyActivity.class);
                startActivityForResult(intent, 1);
            }
        });


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

    private void GetMySealApplicationSearchCount(){
//        {keyword:{"Keyword":"","SealType":"0","ApplicationType":"0","userID":"admin","Year":""}}
        JSONObject requestJson = new JSONObject();
        try{
            JSONObject val1 = new JSONObject();
            val1.put("Keyword",searcET.getText().toString());
            val1.put("SealType",sealTypeStr);
            val1.put("ApplicationType",sealTypeUserStr);
            val1.put("Status",applyStateStr);
            val1.put("userID",MyApplication.getUserID());
            val1.put("Year",searchYearStr);
            requestJson.put("keyword",val1);
        }catch (JSONException ex){
            Log.i("SealApplicationCount","json对象构造错误");
        }
        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());

        Call<String> call = PostRequest.Instance.request.GetMySealApplicationSearchCount (keywordView);
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
                            MySealApplicationSearch(true);
                        }else{
                            messageTip.setVisibility(View.VISIBLE);
                            swipeRefreshLayout.setVisibility(View.GONE);
                            Toast.makeText(SealManageActivity.this,"暂时还没有用印申请",Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException ex){
                        Log.i("SealApplicationCount","json对象构造错误");
                    }
                }

            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(SealManageActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    private void MySealApplicationSearch(final boolean isFirstLoad){
        JSONObject requestJson = new JSONObject();
        //{keyword:{"Keyword":"","SealType":"0","ApplicationType":"0","userID":"admin","Year":""}
        // ,page:{"pageStart":1,"pageEnd":10}}
        try{
            JSONObject val = new JSONObject();
            val.put("Keyword",searcET.getText().toString());
            val.put("SealType",sealTypeStr);
            val.put("ApplicationType",applyStateStr);
            val.put("userID",MyApplication.getUserID());
            val.put("Year",searchYearStr);
            requestJson.put("keyword",val);
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
        Call<String> call = PostRequest.Instance.request.MySealApplicationSearch(newsInfoView);
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
                                listView.setFooterView(View.inflate(SealManageActivity.this, R.layout.item_listview_footer_loadmore, null), new OnLoadMoreScrollListener.OnLoadMoreStateListener() {
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
                        Toast.makeText(SealManageActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }

            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(SealManageActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
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
        MySealApplicationSearch(false);
        swipeRefreshLayout.setRefreshing(false);
    }
    /**
     * 上滑加载事件定义
     */
    private void loadMore() {
        if (mPageCount > 1 && mCurrentPage<mPageCount) {
            MySealApplicationSearch(false);
        } else {
            SealManageActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listView.setEnd(true);
                }
            });
        }
        SealManageActivity.this.runOnUiThread(new Runnable() {
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
}

