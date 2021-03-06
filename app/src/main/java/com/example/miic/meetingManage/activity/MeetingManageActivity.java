package com.example.miic.meetingManage.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.miic.R;
import com.example.miic.base.loadmore.LoadMoreListView;
import com.example.miic.meetingManage.adapter.MeetingSearchResultItemAdapter;
import com.example.miic.meetingManage.item.MeetingSearchResultItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MeetingManageActivity extends AppCompatActivity {

    private LinearLayout backBtn;
    private LinearLayout addMeetingApprove;
    private LinearLayout moreCondition;
    private LinearLayout moreConditionContainer;
    private EditText searcET;
    private RadioGroup meetingType;
    private RadioButton meetingTypeAll;
    private RadioButton meetingTypeNow;
    private RadioButton meetingTypeOut;
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

    private String meetingTypeStr;
    private String applyStateStr;
    private String searchYearStr;

    private LinearLayout findBtn;
    private LinearLayout addMeetingApply;

    private MeetingSearchResultItemAdapter searchResultItemAdapter;
    private List<MeetingSearchResultItem> searchResultList;
    private LoadMoreListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_manage);

        backBtn = (LinearLayout)findViewById(R.id.search_back);
        addMeetingApply = (LinearLayout)findViewById(R.id.add_meeting_approve);
        moreCondition =(LinearLayout)findViewById(R.id.more_condition);
        moreConditionContainer = (LinearLayout)findViewById(R.id.more_condition_container);
        searcET = (EditText) findViewById(R.id.search_keyword);
        searcET.clearFocus();
        //印章类型
        meetingType = (RadioGroup)findViewById(R.id.meeting_type);
        meetingTypeAll = (RadioButton)findViewById(R.id.all_meeting_type);
        meetingTypeNow = (RadioButton)findViewById(R.id.now_meeting_type);
        meetingTypeOut = (RadioButton)findViewById(R.id.out_meeting_type);
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

        findBtn = (LinearLayout)findViewById(R.id.find_meeting_manage);

        searchResultList = new ArrayList<>();
        searchResultItemAdapter = new MeetingSearchResultItemAdapter(MeetingManageActivity.this,searchResultList);
        listView = (LoadMoreListView)findViewById(R.id.meeting_result_container);
        listView.setAdapter(searchResultItemAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if(position<searchResultList.size()) {
                    //通过view获取其内部的组件，进而进行操作
                    String clickID = "";
                    for (int i = 0; i < searchResultList.size(); i++) {
                        MeetingSearchResultItem meetingItem = searchResultList.get(i);
                        clickID = meetingItem.getMeetingApplyID();

                    }
                    //获得点击的新闻的id，然后把这个id传到新的activity中。
                    Intent intent = new Intent(MeetingManageActivity.this, MeetingManageDetailActivity.class);
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
                    ((InputMethodManager) MeetingManageActivity.this.getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(MeetingManageActivity.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    // 搜索用印申请
                    //searchMeeting( );
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
        meetingType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton rb = (RadioButton)MeetingManageActivity.this.findViewById(radioGroup.getCheckedRadioButtonId());
                meetingTypeStr = rb.getText().toString() ;

            }
        });
        //申请状态单选框选中事件
        applyState.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = (RadioButton)MeetingManageActivity.this.findViewById(radioGroup.getCheckedRadioButtonId());
                applyStateStr = rb.getText().toString() ;

            }
        });
        //搜索年份单选框选中事件
        searchYear.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = (RadioButton)MeetingManageActivity.this.findViewById(radioGroup.getCheckedRadioButtonId());
                searchYearStr = rb.getText().toString() ;
            }
        });
        //放大镜搜索事件
        findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //查询印章
                //searchMeeting();
            }
        });
        //新增印章申请事件
        addMeetingApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MeetingManageActivity.this,MeetingApplyActivity.class);
                startActivity(intent);
            }
        });


    }

    //搜索结果展示
    private void showResult(){
        try {
            JSONObject json1 = new JSONObject();
            json1.put("ID","1");
            json1.put("Title","申请微信开发资质");
            json1.put("ApplyTime","2018-2-23 13:23:45");
            json1.put("ApproveState","审核中");
            searchResultList.add(new MeetingSearchResultItem(json1));

            JSONObject json2 = new JSONObject();
            json2.put("ID","1");
            json2.put("Title","申请开发平台证书");
            json2.put("ApplyTime","2018-2-23 13:23:45");
            json2.put("ApproveState","已完成");
            searchResultList.add(new MeetingSearchResultItem(json2));

            JSONObject json3 = new JSONObject();
            json3.put("ID","1");
            json3.put("Title","理工附中合同");
            json3.put("ApplyTime","2018-2-23 13:23:45");
            json3.put("ApproveState","编辑中");
            searchResultList.add(new MeetingSearchResultItem(json3));

            JSONObject json4 = new JSONObject();
            json4.put("ID","1");
            json4.put("Title","开具合同发票");
            json4.put("ApplyTime","2018-2-23 13:23:45");
            json4.put("ApproveState","已完成");
            searchResultList.add(new MeetingSearchResultItem(json4));
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
}
