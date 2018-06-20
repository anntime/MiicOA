package com.example.miic.qjManage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.base.http.PostRequest;
import com.example.miic.base.loadmore.LoadMoreListView;
import com.example.miic.common.MyApplication;
import com.example.miic.qjManage.adapter.QjSearchResultItemAdapter;
import com.example.miic.qjManage.item.QjSearchResultItem;
import com.example.miic.share.activity.ShareActivity;

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

public class QjManageActivity extends AppCompatActivity {

    private LinearLayout backBtn;
    private LinearLayout addQjApprove;
    private LinearLayout moreCondition;
    private LinearLayout moreConditionContainer;
    private EditText searcET;
    private RadioGroup qjType;
    private RadioButton qjTypeAll;
    private RadioButton qjTypeSick;
    private RadioButton qjTypeYear;
    private RadioButton qjTypeOther;
    private RadioGroup applyState;
    private RadioButton applyStateAll;
    private RadioButton applyStateEdit;
    private RadioButton applyStateIng;
    private RadioButton applyStateFinish;
    private RadioGroup searchMonth;
    private RadioButton allMonth;
    private RadioButton firstMonth;
    private RadioButton secondMonth;
    private RadioButton currentMonth;
    private RadioButton thridMonth;

    private String qjTypeStr;
    private String applyStateStr;
    private String searchMonthStr;

    private LinearLayout findBtn;
    private LinearLayout addQjApply;

    private QjSearchResultItemAdapter searchResultItemAdapter;
    private List<QjSearchResultItem> searchResultList;
    private LoadMoreListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qj_manage);

        backBtn = (LinearLayout)findViewById(R.id.search_back);
        addQjApply = (LinearLayout)findViewById(R.id.add_qj_approve);
        moreCondition =(LinearLayout)findViewById(R.id.more_condition);
        moreConditionContainer = (LinearLayout)findViewById(R.id.more_condition_container);
        searcET = (EditText) findViewById(R.id.search_keyword);
        searcET.clearFocus();
        //请假类型
        qjType = (RadioGroup)findViewById(R.id.qj_type);
        qjTypeAll = (RadioButton)findViewById(R.id.all_qj_type);
        qjTypeSick = (RadioButton)findViewById(R.id.sick);
        qjTypeYear = (RadioButton)findViewById(R.id.year);
        qjTypeOther = (RadioButton)findViewById(R.id.other);
        //申请状态
        applyState = (RadioGroup)findViewById(R.id.apply_state);
        applyStateAll = (RadioButton)findViewById(R.id.all_apply_state);
        applyStateEdit = (RadioButton)findViewById(R.id.edit_apply_state);
        applyStateIng = (RadioButton)findViewById(R.id.applying_state);
        applyStateFinish = (RadioButton)findViewById(R.id.finish_apply_state);
        //月份
        searchMonth = (RadioGroup)findViewById(R.id.search_month);
        allMonth = (RadioButton)findViewById(R.id.all_month);
        firstMonth = (RadioButton)findViewById(R.id.first_month);
        secondMonth = (RadioButton)findViewById(R.id.second_month);
        thridMonth = (RadioButton)findViewById(R.id.third_month);
        currentMonth = (RadioButton)findViewById(R.id.current_month);
        Calendar rightNow = Calendar.getInstance();
        int month = rightNow.get(Calendar.MONTH)+1;
        String[] numeric = new String[]{"零", "一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月","十月","十一月","十二月"};
        firstMonth.setText(numeric[((month-3+12)%12)]);
        secondMonth.setText(numeric[((month-2+12)%12)]);
        thridMonth.setText(numeric[((month-1+12)%12)]);
        currentMonth.setText(numeric[month]);

        findBtn = (LinearLayout)findViewById(R.id.find_qj_manage);

        searchResultList = new ArrayList<>();
        searchResultItemAdapter = new QjSearchResultItemAdapter(QjManageActivity.this,searchResultList);
        listView = (LoadMoreListView)findViewById(R.id.qj_result_container);
        listView.setAdapter(searchResultItemAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if(position<searchResultList.size()) {
                    //通过view获取其内部的组件，进而进行操作
                    String clickID = "";
                    for (int i = 0; i < searchResultList.size(); i++) {
                        QjSearchResultItem qjItem = searchResultList.get(i);
                        clickID = qjItem.getQjApplyID();
                    }
                    //获得点击的新闻的id，然后把这个id传到新的activity中。
                    Intent intent = new Intent(QjManageActivity.this, QjManageDetailActivity.class);
                    intent.putExtra("clickID", clickID);
                    startActivityForResult(intent, 1);
                }
            };
        });
        init();
        showResult();
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
                    ((InputMethodManager) QjManageActivity.this.getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(QjManageActivity.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    // 搜索用印申请
                    //searchSeal( );
                    searcET.clearFocus();
                }
                return false;
            }
        });
        //更多条件点击展开
        moreCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreConditionContainer.setVisibility(View.VISIBLE);
            }
        });
        //印章类型单选框选中事件
        qjType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton rb = (RadioButton)QjManageActivity.this.findViewById(radioGroup.getCheckedRadioButtonId());
                qjTypeStr = rb.getText().toString() ;

            }
        });
        //申请状态单选框选中事件
        applyState.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = (RadioButton)QjManageActivity.this.findViewById(radioGroup.getCheckedRadioButtonId());
                applyStateStr = rb.getText().toString() ;

            }
        });
        //搜索年份单选框选中事件
        searchMonth.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = (RadioButton)QjManageActivity.this.findViewById(radioGroup.getCheckedRadioButtonId());
                searchMonthStr = rb.getText().toString() ;
            }
        });
        //放大镜搜索事件
        findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //查询印章
                //searchSeal();
            }
        });
        //新增印章申请事件
        addQjApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(QjManageActivity.this,QjApplyActivity.class);
                startActivity(intent);
            }
        });
        GetMyLeaveList ();
    }

    //搜索结果展示
    private void showResult(){
        try {
            JSONObject json1 = new JSONObject();
            json1.put("ID","1");
            json1.put("Title","【病假】去医院看病");
            json1.put("ApplyTime","2018-2-23 13:23:45");
            json1.put("ApproveState","审核中");
            searchResultList.add(new QjSearchResultItem(json1));

            JSONObject json2 = new JSONObject();
            json2.put("ID","1");
            json2.put("Title","【事假】去机场接客人");
            json2.put("ApplyTime","2018-2-23 13:23:45");
            json2.put("ApproveState","已完成");
            searchResultList.add(new QjSearchResultItem(json2));

            JSONObject json3 = new JSONObject();
            json3.put("ID","1");
            json3.put("Title","【病假】感冒，请病假");
            json3.put("ApplyTime","2018-2-23 13:23:45");
            json3.put("ApproveState","编辑中");
            searchResultList.add(new QjSearchResultItem(json3));

            JSONObject json4 = new JSONObject();
            json4.put("ID","1");
            json4.put("Title","【事假】家中有事，请事假");
            json4.put("ApplyTime","2018-2-23 13:23:45");
            json4.put("ApproveState","已完成");
            searchResultList.add(new QjSearchResultItem(json4));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //返回按钮绑定事件
    private View.OnClickListener backClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };
    //获得请假列表
    private void GetMyLeaveList (){
        JSONObject requestJson = new JSONObject();
        try{
            JSONObject val1 = new JSONObject();
            val1.put("Year","");
            val1.put("Month","");
            requestJson.put("dateView",val1);
        }catch (JSONException ex){
            Log.i("InfoChildFragment","json对象构造错误");
        }
        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        String cookieStr = MyApplication.getCookieStr();
        Call<String> call = PostRequest.Instance.request.GetMyLeaveList (cookieStr,keywordView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try{
                        JSONObject jsonObject = new JSONObject(response.body());
                        JSONArray dataArr = jsonObject.getJSONArray("d");
                        for (int i=0;i<dataArr.length();i++){
                            JSONObject obj = dataArr.getJSONObject(i);
                            RadioButton tempButton = new RadioButton(QjManageActivity.this);
                            tempButton.setPadding(80, 0, 0, 0);                 // 设置文字距离按钮四周的距离
                            tempButton.setText(obj.getString("Value"));
                            qjType.addView(tempButton, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                        }
                    }catch (JSONException ex){
                        Log.i("ShareActivity","json对象构造错误");
                    }
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(QjManageActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //获得请假类型列表
    private void GetLeaveTypeList (){
        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), "");
        String cookieStr = MyApplication.getCookieStr();
        Call<String> call = PostRequest.Instance.request.GetLeaveTypeList (cookieStr,keywordView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try{
                        JSONObject jsonObject = new JSONObject(response.body());
                        JSONArray dataArr = jsonObject.getJSONArray("d");
                        for (int i=0;i<dataArr.length();i++){
                            JSONObject obj = dataArr.getJSONObject(i);
                            RadioButton tempButton = new RadioButton(QjManageActivity.this);
                            tempButton.setPadding(80, 0, 0, 0);                 // 设置文字距离按钮四周的距离
                            tempButton.setText(obj.getString("Value"));
                            qjType.addView(tempButton, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                        }
                    }catch (JSONException ex){
                        Log.i("ShareActivity","json对象构造错误");
                    }
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(QjManageActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //获得流程状态列表
    private void GetMySubmitStatusList (){

    }
}
