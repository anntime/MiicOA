package com.example.miic.contractManage.activity;

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
import com.example.miic.contractManage.adapter.ContractSearchResultItemAdapter;
import com.example.miic.contractManage.item.ContractSearchResultItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ContractManageActivity extends AppCompatActivity {

    private LinearLayout backBtn;
    private LinearLayout addContractApprove;
    private LinearLayout moreCondition;
    private LinearLayout moreConditionContainer;
    private EditText searcET;
    private RadioGroup contractType;
    private RadioButton contractTypeAll;
    private RadioButton contractTypeNow;
    private RadioButton contractTypeOut;
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

    private String contractTypeStr;
    private String applyStateStr;
    private String searchYearStr;

    private LinearLayout findBtn;
    private LinearLayout addContractApply;

    private ContractSearchResultItemAdapter searchResultItemAdapter;
    private List<ContractSearchResultItem> searchResultList;
    private LoadMoreListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_manage);

        backBtn = (LinearLayout)findViewById(R.id.search_back);
        addContractApply = (LinearLayout)findViewById(R.id.add_contract_approve);
        moreCondition =(LinearLayout)findViewById(R.id.more_condition);
        moreConditionContainer = (LinearLayout)findViewById(R.id.more_condition_container);
        searcET = (EditText) findViewById(R.id.search_keyword);
        searcET.clearFocus();
        //印章类型
        contractType = (RadioGroup)findViewById(R.id.contract_type);
        contractTypeAll = (RadioButton)findViewById(R.id.all_contract_type);
        contractTypeNow = (RadioButton)findViewById(R.id.now_contract_type);
        contractTypeOut = (RadioButton)findViewById(R.id.out_contract_type);
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

        findBtn = (LinearLayout)findViewById(R.id.find_contract_manage);

        searchResultList = new ArrayList<>();
        searchResultItemAdapter = new ContractSearchResultItemAdapter(ContractManageActivity.this,searchResultList);
        listView = (LoadMoreListView)findViewById(R.id.contract_result_container);
        listView.setAdapter(searchResultItemAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if(position<searchResultList.size()) {
                    //通过view获取其内部的组件，进而进行操作
                    String clickID = "";
                    for (int i = 0; i < searchResultList.size(); i++) {
                        ContractSearchResultItem contractItem = searchResultList.get(i);
                        clickID = contractItem.getContractApplyID();

                    }
                    //获得点击的新闻的id，然后把这个id传到新的activity中。
                    Intent intent = new Intent(ContractManageActivity.this, ContractManageDetailActivity.class);
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
                    ((InputMethodManager) ContractManageActivity.this.getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(ContractManageActivity.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    // 搜索用印申请
                    //searchContract( );
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
        contractType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton rb = (RadioButton)ContractManageActivity.this.findViewById(radioGroup.getCheckedRadioButtonId());
                contractTypeStr = rb.getText().toString() ;

            }
        });
        //申请状态单选框选中事件
        applyState.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = (RadioButton)ContractManageActivity.this.findViewById(radioGroup.getCheckedRadioButtonId());
                applyStateStr = rb.getText().toString() ;

            }
        });
        //搜索年份单选框选中事件
        searchYear.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = (RadioButton)ContractManageActivity.this.findViewById(radioGroup.getCheckedRadioButtonId());
                searchYearStr = rb.getText().toString() ;
            }
        });
        //放大镜搜索事件
        findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //查询印章
                //searchContract();
            }
        });
        //新增印章申请事件
        addContractApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ContractManageActivity.this,ContractApplyActivity.class);
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
            searchResultList.add(new ContractSearchResultItem(json1));

            JSONObject json2 = new JSONObject();
            json2.put("ID","1");
            json2.put("Title","申请开发平台证书");
            json2.put("ApplyTime","2018-2-23 13:23:45");
            json2.put("ApproveState","已完成");
            searchResultList.add(new ContractSearchResultItem(json2));

            JSONObject json3 = new JSONObject();
            json3.put("ID","1");
            json3.put("Title","理工附中合同");
            json3.put("ApplyTime","2018-2-23 13:23:45");
            json3.put("ApproveState","编辑中");
            searchResultList.add(new ContractSearchResultItem(json3));

            JSONObject json4 = new JSONObject();
            json4.put("ID","1");
            json4.put("Title","开具合同发票");
            json4.put("ApplyTime","2018-2-23 13:23:45");
            json4.put("ApproveState","已完成");
            searchResultList.add(new ContractSearchResultItem(json4));
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
