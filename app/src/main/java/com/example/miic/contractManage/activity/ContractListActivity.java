package com.example.miic.contractManage.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.example.miic.base.loadmore.LoadMoreListView;
import com.example.miic.base.loadmore.OnLoadMoreScrollListener;
import com.example.miic.base.multiLineRadioGroup.MultiLineRadioGroup;
import com.example.miic.contractManage.adapter.ContractListItemAdapter;
import com.example.miic.contractManage.common.ContractCommon;
import com.example.miic.contractManage.item.ContractListItem;
import com.example.miic.contractManage.item.ContractStatus;
import com.example.miic.contractManage.item.SwipeItem;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.mcxtzhang.commonadapter.lvgv.CommonAdapter;
import com.mcxtzhang.commonadapter.lvgv.ViewHolder;

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

import static java.lang.Integer.parseInt;

public class ContractListActivity extends AppCompatActivity {
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

    private SwipeRefreshLayout swipeRefreshLayout;
    private ContractListItemAdapter searchResultItemAdapter;
    private List<ContractListItem> searchResultList;
    private LoadMoreListView listView;
    private LinearLayout messageTip;

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
    private String Type;
    private String[] contractStatusList;
    private JSONArray DeptList = new JSONArray();
    private JSONArray DeptIDs = new JSONArray();
    private Handler cxHandler;
    private Handler glHandler;
    private Handler jhHandler;
    private Handler zxHandler;
    private Handler jyHandler;
    private Handler wcHandler;

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
        setContentView(R.layout.activity_contract_list);
        Intent intent = getIntent();
        Type = intent.getStringExtra("type");
        Log.i("Type:",Type);

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
        final JSONObject requestJson = new JSONObject();
        switch(Type){
            case "cx":
                titleTv.setText("合同查询");
                params.setMargins(0, 290, 0, 0);
                searchViewContainer.setLayoutParams(params);
                contractStatusList = new String[]{"审批中", "待监印", "待归档", "执行中", "合同完成", "合同终止", "其他"};
                GetMyQueryDeptList();
                cxHandler  = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        try{
                            JSONObject val1 = new JSONObject();
                            val1.put("Keyword","");
                            val1.put("Status","0");
                            val1.put("IdentificationItem","0");
                            val1.put("Year",Calendar.getInstance().get(Calendar.YEAR)+"");
                            val1.put("ContractTypeOne","99");
                            val1.put("ContractTypeTwo","99");
                            val1.put("DeptIDs",DeptIDs);
                            val1.put("DeptType","1");
                            requestJson.put("keyword",val1);
                        }catch (JSONException ex){
                            Log.i("onCreate","json对象构造错误");
                        }
                        GetQuerySearchCount(requestJson);
                    }
                };


                break;
            case "gl":
                titleTv.setText("合同管理");
                params.setMargins(0, 250, 0, 0);
                searchViewContainer.setLayoutParams(params);
                contractStatusList = new String[]{"待提交","审批中", "待监印", "待归档", "执行中", "合同完成", "合同终止", "其他"};
                GetMiicDeptInfoList();
                glHandler  = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        try{
                            JSONObject val1 = new JSONObject();
                            val1.put("Keyword","");
                            val1.put("Status","0");
                            val1.put("IdentificationItem","0");
                            val1.put("Year",Calendar.getInstance().get(Calendar.YEAR)+"");
                            val1.put("ContractTypeOne","99");
                            val1.put("ContractTypeTwo","99");
                            val1.put("DeptID","");
                            requestJson.put("keyword",val1);
                        }catch (JSONException ex){
                            Log.i("onCreate","json对象构造错误");
                        }
                        GetContractSearchCount(requestJson);
                    }
                };
                break;
            case "jh":
                titleTv.setText("合同执行计划记录");
                params.setMargins(0, 250, 0, 0);
                searchViewContainer.setLayoutParams(params);
                contractStatusList = new String[]{ "待归档", "执行中", "合同完成"};
                GetPlanDeptInfoList();
                jhHandler  = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        try{
                            JSONObject val1 = new JSONObject();
                            val1.put("Keyword","");
                            val1.put("IdentificationItem","0");
                            val1.put("Year",Calendar.getInstance().get(Calendar.YEAR)+"");
                            val1.put("ContractTypeOne","99");
                            val1.put("ContractTypeTwo","99");
                            JSONArray Status = new JSONArray();
                            Status.put("7");
                            Status.put("8");
                            Status.put("9");
                            val1.put("Status",Status);
                            val1.put("DeptID",DeptIDs);
                            requestJson.put("keyword",val1);
                        }catch (JSONException ex){
                            Log.i("onCreate","json对象构造错误");
                        }
                        GetContractPlanSearchCount(requestJson);
                    }
                };
                break;
            case "zx":
                titleTv.setText("合同执行记录管理");
                params.setMargins(0, 250, 0, 0);
                searchViewContainer.setLayoutParams(params);
                contractStatusList = new String[]{ "待归档", "执行中", "合同完成"};
                contractDept.append("中心领导");
                DeptIDs.put("中心领导");
                GetMiicDeptInfoList();

                zxHandler  = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        try{
                            JSONObject val1 = new JSONObject();
                            val1.put("Keyword","");
                            val1.put("IdentificationItem","0");
                            val1.put("Year",Calendar.getInstance().get(Calendar.YEAR)+"");
                            val1.put("ContractTypeOne","99");
                            val1.put("ContractTypeTwo","99");
                            JSONArray Status = new JSONArray();
                            Status.put("7");
                            Status.put("8");
                            Status.put("9");
                            val1.put("Status",Status);
                            val1.put("DeptID",DeptIDs);
                            requestJson.put("keyword",val1);
                        }catch (JSONException ex){
                            Log.i("onCreate","json对象构造错误");
                        }
                        GetCommonContractSearchCount(requestJson);
                    }
                };

                break;
            case "jy":
                titleTv.setText("合同监印");
                params.setMargins(0, 250, 0, 0);
                searchViewContainer.setLayoutParams(params);
                contractStatusList = new String[]{"待监印", "监印中(待归档)" ,"合同终止"};
                contractDept.append("中心领导");
                DeptIDs.put("中心领导");
                GetMiicDeptInfoList();
                jyHandler  = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        try{
                            JSONObject val1 = new JSONObject();
                            val1.put("Keyword","");
                            val1.put("IdentificationItem","0");
                            val1.put("Year",Calendar.getInstance().get(Calendar.YEAR)+"");
                            val1.put("ContractTypeOne","99");
                            val1.put("ContractTypeTwo","99");
                            JSONArray Status = new JSONArray();
                            Status.put("3");
                            Status.put("7");
                            Status.put("10");
                            val1.put("Status",Status);
                            val1.put("DeptID",DeptIDs);
                            requestJson.put("keyword",val1);
                        }catch (JSONException ex){
                            Log.i("onCreate","json对象构造错误");
                        }
                        GetCommonContractSearchCount(requestJson);
                    }
                };
                break;
            case "wc":
                titleTv.setText("合同完成");
                params.setMargins(0, 250, 0, 0);
                searchViewContainer.setLayoutParams(params);
                contractStatusList = new String[]{"执行中", "合同完成", "合同终止"};
                contractDept.append("中心领导");
                DeptIDs.put("中心领导");
                GetMiicDeptInfoList();
                wcHandler   = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
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
                            val1.put("DeptID",DeptIDs);
                            requestJson.put("keyword",val1);
                        }catch (JSONException ex){
                            Log.i("onCreate","json对象构造错误");
                        }
                        GetContractFinishSearchCount(requestJson);
                    }
                };

                break;
        }

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

        //列表展示相关
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshview);
        searchResultList = new ArrayList<>();
        searchResultItemAdapter = new ContractListItemAdapter(ContractListActivity.this,searchResultList);
        listView = (LoadMoreListView)findViewById(R.id.contract_result_container);
        listView.setAdapter(searchResultItemAdapter);
        searchResultItemAdapter.setOnItemMyClickListener(new ContractListItemAdapter.onItemMyClickListener(){
            public void onMyClick(int type, int position) {
                if(position<searchResultList.size()) {
                    //通过view获取其内部的组件，进而进行操作
                    String clickID;
                    ContractListItem contractItem = searchResultList.get(position);
                    clickID = contractItem.getContractID();
                    String contractName = contractItem.getContractTitle();
                    int status = parseInt(contractItem.getContractStatus());
                    Log.i("setOnItemClickListener",clickID);
                    Intent intent;
                    switch(Type){
                        case "cx":
                            //显示合同查询相关页面---合同查询点击跳转页面只会跳到合同详情页（包括合同详情、审批详情、计划详情、执行详情）
                            intent = new Intent(ContractListActivity.this, ContractDetailActivity.class);
                            //获得点击的新闻的id，然后把这个id传到新的activity中。
                            intent.putExtra("clickID", clickID);
                            startActivityForResult(intent, 1);
                            break;
                        case "gl":
                            //显示合同管理相关界面
                            if (status== ContractStatus.Implement.getIndex()){
                                //状态在执行中，页面跳转到合同执行详情页
                                intent = new Intent(ContractListActivity.this, ContractExecuteActivity.class);
                                //获得点击的新闻的id，然后把这个id传到新的activity中。
                                intent.putExtra("clickID", clickID);
                                startActivityForResult(intent, 1);
                            }else if(status == ContractStatus.ContractFinish.getIndex()||status == ContractStatus.ContractTermination.getIndex()){
                                //状态是合同完成、合同终止，页面跳转到合同详情页（包括合同详情、审批详情、计划详情、执行详情）
                                intent = new Intent(ContractListActivity.this, ContractDetailActivity.class);
                                //获得点击的新闻的id，然后把这个id传到新的activity中。
                                intent.putExtra("clickID", clickID);
                                startActivityForResult(intent, 1);
                            }
                            break;
                        case "jh":
                            //显示合同计划界面
                            intent = new Intent(ContractListActivity.this, ContractPlanActivity.class);
                            //获得点击的新闻的id，然后把这个id传到新的activity中。
                            intent.putExtra("clickID", clickID);
                            startActivityForResult(intent, 1);
                            break;
                        case "zx":
                            //显示合同执行界面
                            intent = new Intent(ContractListActivity.this, ContractExecuteActivity.class);
                            //获得点击的新闻的id，然后把这个id传到新的activity中。
                            intent.putExtra("clickID", clickID);
                            startActivityForResult(intent, 1);
                            break;
                        case "jy":
                            //显示合同监印界面
                            if(status== ContractStatus.Finish.getIndex()){
                                intent = new Intent(ContractListActivity.this, ContractPrintActivity.class);
                                //获得点击的新闻的id，然后把这个id传到新的activity中。
                                intent.putExtra("clickID", clickID);
                                intent.putExtra("contractName",contractName);
                                startActivityForResult(intent, 1);
                            }else{
                                Toast.makeText(ContractListActivity.this,"合同归档操作请前往电脑端进行操作~",Toast.LENGTH_LONG).show();
                            }
                            break;
                    }
                }
            }
        });
        messageTip = (LinearLayout)findViewById(R.id.message_tip);

        searchResultView = findViewById(R.id.search_result);
        searchProgressView = findViewById(R.id.search_progress);
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


    private void initSearchView(){
        initContractStatus(contractStatusList);
        GetIdentificationItemInfos();
        initContractTypeOne();
        initContractTypeTwo();
        initContractYear();
        contractStatus.setOnCheckChangedListener(new MultiLineRadioGroup.OnCheckedChangedListener() {
            @Override
            public void onItemChecked(MultiLineRadioGroup group, int position, boolean checked) {
                if(checked==false){
                    contractStatus.setItemChecked(0);
                    Toast.makeText(ContractListActivity.this,"请选择合同状态！",Toast.LENGTH_SHORT).show();

                }
            }
        });
        contractIdentificationItem.setOnCheckChangedListener(new MultiLineRadioGroup.OnCheckedChangedListener() {
            @Override
            public void onItemChecked(MultiLineRadioGroup group, int position, boolean checked) {
                if(checked==false){
                    Toast.makeText(ContractListActivity.this,"请选择合同标志！",Toast.LENGTH_SHORT).show();
                    contractIdentificationItem.setItemChecked(0);
                }
            }
        });
        contractYear.setOnCheckChangedListener(new MultiLineRadioGroup.OnCheckedChangedListener() {
            @Override
            public void onItemChecked(MultiLineRadioGroup group, int position, boolean checked) {
                if(checked==false){
                    Toast.makeText(ContractListActivity.this,"请选择年份！",Toast.LENGTH_SHORT).show();
                    contractYear.setItemChecked(2);
                }
            }
        });
        contractDept.setOnCheckChangedListener(new MultiLineRadioGroup.OnCheckedChangedListener() {
            @Override
            public void onItemChecked(MultiLineRadioGroup group, int position, boolean checked) {
                if(checked==false){
                    Toast.makeText(ContractListActivity.this,"请选择部门！",Toast.LENGTH_SHORT).show();
                    contractDept.setItemChecked(0);
                }
            }
        });
        contractTypeOne.setOnCheckChangedListener(new MultiLineRadioGroup.OnCheckedChangedListener() {
            @Override
            public void onItemChecked(MultiLineRadioGroup group, int position, boolean checked) {
                if(checked==false){
                    Toast.makeText(ContractListActivity.this,"请选择合同类型一！",Toast.LENGTH_SHORT).show();
                    contractTypeOne.setItemChecked(0);
                }
            }
        });
        contractTypeTwo.setOnCheckChangedListener(new MultiLineRadioGroup.OnCheckedChangedListener() {
            @Override
            public void onItemChecked(MultiLineRadioGroup group, int position, boolean checked) {
                if(checked==false){
                    Toast.makeText(ContractListActivity.this,"请选择合同类型二！",Toast.LENGTH_SHORT).show();
                    contractTypeTwo.setItemChecked(0);
                }
            }
        });
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



                    val1.put("Year",contractYear.getCheckedValues().get(0).equals("全部")?"":contractYear.getCheckedValues().get(0));

                    if(Type.equals("jh")||Type.equals("zx")||Type.equals("jy")||Type.equals("wc")){
                        if(contractDept.getCheckedValues().get(0).equals("全部")){
                            val1.put("DeptID", DeptIDs);
                        } else if(contractDept.getCheckedValues().get(0).equals("中心领导")){
                            JSONArray leaderArr = new JSONArray();
                            leaderArr.put("中心领导");
                            val1.put("DeptID",leaderArr);
                        }else {
                            for (int n=0;n<DeptList.length();n++){
                                if(DeptList.getJSONObject(n).getString("DeptName").equals(contractDept.getCheckedValues().get(0))){
                                    JSONArray arr = new JSONArray();
                                    arr.put(DeptList.getJSONObject(n).getString("ID"));
                                    val1.put("DeptID",arr);
                                }
                            }
                        }
                    }
                    if(Type.equals("cx")){
                        if(contractDept.getCheckedValues().get(0).equals("全部")){
                            val1.put("DeptIDs", DeptIDs);
                        } else if(contractDept.getCheckedValues().get(0).equals("中心领导")){
                            JSONArray leaderArr = new JSONArray();
                            leaderArr.put("中心领导");
                            val1.put("DeptIDs",leaderArr);
                        }else {
                            for (int n=0;n<DeptList.length();n++){
                                if(DeptList.getJSONObject(n).getString("DeptName").equals(contractDept.getCheckedValues().get(0))){
                                    JSONArray arr = new JSONArray();
                                    arr.put(DeptList.getJSONObject(n).getString("ID"));
                                    val1.put("DeptIDs",arr);
                                }
                            }
                        }
                    }
                    if(Type.equals("jh")||Type.equals("zx")||Type.equals("jy")||Type.equals("wc")){
                        if(contractStatus.getCheckedValues().get(0).equals("全部")){
                            JSONArray Status = new JSONArray();
                            switch(Type){
                                case "jh":
                                    Status.put("7");
                                    Status.put("8");
                                    Status.put("9");
                                    break;
                                case "zx":
                                    Status.put("7");
                                    Status.put("8");
                                    Status.put("9");
                                    break;
                                case "jy":
                                    Status.put("3");
                                    Status.put("7");
                                    Status.put("10");
                                    break;
                                case "wc":
                                    Status.put("4");
                                    Status.put("8");
                                    Status.put("9");
                                    break;
                            }
                            val1.put("Status", Status);
                        }else{
                            JSONArray arr = new JSONArray();
                            arr.put(contract.getContractStatue(contractStatus.getCheckedValues().get(0)));
                            val1.put("Status",arr);
                        }
                    }else{
                        val1.put("Status",contract.getContractStatue(contractStatus.getCheckedValues().get(0)));
                    }
                    if(Type.equals("cx")){
                        val1.put("DeptType","1");
                    }
                    requestJson.put("keyword",val1);
                }catch (JSONException ex){
                    Log.i("showSearchView","json对象构造错误");
                }
                Log.i("搜索结果",requestJson.toString());
                switch(Type){
                    case "cx":
                        //显示合同查询相关搜索界面
                        GetQuerySearchCount(requestJson);
                        break;
                    case "gl":
                        //显示合同管理相关搜索界面
                        GetContractSearchCount(requestJson);
                        break;
                    case "jh":
                        GetContractPlanSearchCount(requestJson);
                        break;
                    case "zx":case "jy":
                        //显示合同计划相关搜索界面
                        GetCommonContractSearchCount(requestJson);
                        break;
                    case "wc":
                        //显示合同完成相关搜索界面
                        GetContractFinishSearchCount(requestJson);
                        break;
                }
                popupWindow.dismiss();
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
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
                Toast.makeText(ContractListActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //获取查询部门
    public void GetMyQueryDeptList(){
        Call<String> call = PostRequest.Instance.request.GetMyQueryDeptList();
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
                                DeptIDs.put(objectTemp.getString("ID"));
                            }
                            if (arrayTemp.length()==1){
                                contractDept.remove(0);
                            }
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Message message = cxHandler.obtainMessage();
                                    cxHandler.sendMessage(message);
                                }
                            }).start();
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
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Message message = cxHandler.obtainMessage();
                                                cxHandler.sendMessage(message);
                                            }
                                        }).start();
                                    }
                                }
                                //请求失败时回调
                                @Override
                                public void onFailure(Call<String> call, Throwable throwable) {
                                    System.out.println("请求失败" + call.request());
                                    System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                                    Toast.makeText(ContractListActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
                                }
                            };
                            PostRequest.Instance.CommonAsynPost(call2, callback2);
                        }

                    }catch (JSONException ex){
                        Log.e("GetMiicDeptInfoList",ex.getMessage());
                        Toast.makeText(ContractListActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }

                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ContractListActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //获取计划查询部门
    public void GetPlanDeptInfoList(){
        Call<String> call = PostRequest.Instance.request.GetPlanDeptInfoList();
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
                                DeptIDs.put(objectTemp.getString("ID"));
                                Log.i("部门信息",objectTemp.getString("DeptName"));
                            }
                            if (arrayTemp.length()==1){
                                contractDept.remove(0);
                            }
                        }
                    }catch (JSONException ex){
                        Log.e("GetMiicDeptInfoList",ex.getMessage());
                        Toast.makeText(ContractListActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message message = jhHandler.obtainMessage();
                            jhHandler.sendMessage(message);
                        }
                    }).start();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ContractListActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
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
                                DeptIDs.put(objectTemp.getString("ID"));
                                Log.i("部门信息",objectTemp.getString("DeptName"));
                            }
                            if (arrayTemp.length()==1){
                                contractDept.remove(0);
                            }
                        }
                    }catch (JSONException ex){
                        Log.e("GetMiicDeptInfoList",ex.getMessage());
                        Toast.makeText(ContractListActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if(glHandler!=null){
                                Message message = glHandler.obtainMessage();
                                glHandler.sendMessage(message);
                            }
                            if(zxHandler!=null){
                                Message message = zxHandler.obtainMessage();
                                zxHandler.sendMessage(message);
                            }
                            if(jyHandler!=null){
                                Message message = jyHandler.obtainMessage();
                                jyHandler.sendMessage(message);
                            }
                            if(wcHandler!=null){
                                Message message = wcHandler.obtainMessage();
                                wcHandler.sendMessage(message);
                            }

                        }
                    }).start();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ContractListActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //获取合同管理查询
    public void ContractSearch(final boolean isFirstLoad ,JSONObject keyword){
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
        Call<String> call = PostRequest.Instance.request.ContractSearch(keywordView);
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
                            if(listView.getFooterViewsCount() ==0){
                                listView.setFooterView(View.inflate(ContractListActivity.this, R.layout.item_listview_footer_loadmore, null), new OnLoadMoreScrollListener.OnLoadMoreStateListener() {
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
                                        ((TextView) footView.findViewById(R.id.footer_tv_msg)).setText("没有更多的合同信息了");
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
                        Toast.makeText(ContractListActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }

            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ContractListActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //获取合同管理查询数量
    public void GetContractSearchCount(final JSONObject requestJson){
        searchJson = requestJson;

        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Call<String> call = PostRequest.Instance.request.GetContractSearchCount(keywordView);
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
                            ContractSearch(true,requestJson);
                        }else{
                            showProgress(false);
                            messageTip.setVisibility(View.VISIBLE);
                            swipeRefreshLayout.setVisibility(View.GONE);
                            Toast.makeText(ContractListActivity.this,"暂时没有符合搜索条件的合同信息",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ContractListActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }

    //获取合同查询查询
    public void QuerySearch(final boolean isFirstLoad ,JSONObject keyword){
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
        Call<String> call = PostRequest.Instance.request.QuerySearch(keywordView);
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
                            if(listView.getFooterViewsCount() ==0){
                                listView.setFooterView(View.inflate(ContractListActivity.this, R.layout.item_listview_footer_loadmore, null), new OnLoadMoreScrollListener.OnLoadMoreStateListener() {
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
                                        ((TextView) footView.findViewById(R.id.footer_tv_msg)).setText("没有更多的合同信息了");
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
                        Toast.makeText(ContractListActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }

            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ContractListActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //获取合同查询数量
    public void GetQuerySearchCount(final JSONObject requestJson){
        searchJson = requestJson;

        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Call<String> call = PostRequest.Instance.request.GetQuerySearchCount(keywordView);
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
                            QuerySearch(true,requestJson);
                        }else{
                            showProgress(false);
                            messageTip.setVisibility(View.VISIBLE);
                            swipeRefreshLayout.setVisibility(View.GONE);
                            Toast.makeText(ContractListActivity.this,"暂时没有符合搜索条件的合同信息",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ContractListActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
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
                       final List<SwipeItem> mDatas = new ArrayList<>();
                        JSONObject jsonObject = new JSONObject(response.body());
                        String jsonStrTemp = jsonObject.getString("d");
                        if(!jsonStrTemp.equals("[]")){
                            JSONArray arrayTemp = new JSONArray(jsonStrTemp);
                            for (int i=0;i<arrayTemp.length();i++){
                                JSONObject objectTemp = arrayTemp.getJSONObject(i);
                                Log.i("查询结果",objectTemp.toString());
                                mDatas.add(new SwipeItem(objectTemp));
                            }

                            listView.setAdapter(new CommonAdapter<SwipeItem>(ContractListActivity.this, mDatas, R.layout.swipe_item) {
                                @Override
                                public void convert(final ViewHolder holder, final SwipeItem swipeItem, final int position) {
                                    //((SwipeMenuLayout)holder.getConvertView()).setIos(false);//这句话关掉IOS阻塞式交互效果
                                    final SwipeItem sItem = mDatas.get(position);
                                    holder.setText(R.id.contract_title, sItem.getContractTitle());
                                    holder.setText(R.id.contract_time, sItem.getContractTime());
                                    holder.setText(R.id.contract_undertake_name, sItem.getContractUndertakeName());
                                    holder.setText(R.id.contract_state, new ContractCommon().getContractStatusStr(sItem.getContractStatus()));
                                    holder.setTextColor(R.id.contract_state, new ContractCommon().getContractStatusColorStr(sItem.getContractStatus()));
                                    final View swipeView = LayoutInflater.from(ContractListActivity.this).inflate(R.layout.swipe_item,null);
                                    if(parseInt(sItem.getContractStatus())==ContractStatus.Implement.getIndex()){
                                        //合同 执行中
                                        holder.getView(R.id.unfinish_btn).setVisibility(View.GONE);
                                    }else if(parseInt(sItem.getContractStatus())==ContractStatus.ContractFinish.getIndex()){
                                        //合同完成
                                        holder.getView(R.id.finish_btn).setVisibility(View.GONE);
                                        holder.getView(R.id.stop_btn).setVisibility(View.GONE);
                                    }else if (parseInt(sItem.getContractStatus())==ContractStatus.ContractTermination.getIndex()){
                                        holder.getView(R.id.finish_btn).setVisibility(View.GONE);
                                        holder.getView(R.id.stop_btn).setVisibility(View.GONE);
                                        holder.getView(R.id.unfinish_btn).setVisibility(View.GONE);
                                    }

                                    holder.setOnClickListener(R.id.contract_container, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(ContractListActivity.this, ContractDetailActivity.class);
                                            //获得点击的新闻的id，然后把这个id传到新的activity中。
                                            intent.putExtra("clickID", sItem.getContractID());
                                            startActivityForResult(intent, 1);
                                        }
                                    });
                                    holder.setOnClickListener(R.id.finish_btn, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            FinishEvent(sItem.getContractID());
                                            //在ListView里，点击侧滑菜单上的选项时，如果想让擦花菜单同时关闭，调用这句话
                                            ((SwipeMenuLayout) holder.getConvertView()).quickClose();
                                            notifyDataSetChanged();
                                        }
                                    });
                                    holder.setOnClickListener(R.id.unfinish_btn, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            CancelFinishEvent(sItem.getContractID());
                                            //在ListView里，点击侧滑菜单上的选项时，如果想让擦花菜单同时关闭，调用这句话
                                            ((SwipeMenuLayout) holder.getConvertView()).quickClose();
                                            notifyDataSetChanged();
                                        }
                                    });
                                    holder.setOnClickListener(R.id.stop_btn, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            IllegalFinishEvent(swipeItem.getContractID());
                                            //在ListView里，点击侧滑菜单上的选项时，如果想让擦花菜单同时关闭，调用这句话
                                            ((SwipeMenuLayout) holder.getConvertView()).quickClose();
                                            notifyDataSetChanged();
                                        }
                                    });
                                }
                            });

                            if(listView.getFooterViewsCount() ==0){
                                listView.setFooterView(View.inflate(ContractListActivity.this, R.layout.item_listview_footer_loadmore, null), new OnLoadMoreScrollListener.OnLoadMoreStateListener() {
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
                        }
                        else{
                            messageTip.setVisibility(View.VISIBLE);
                            swipeRefreshLayout.setVisibility(View.GONE);
                        }
                        searchResultItemAdapter.notifyDataSetChanged();
                    }catch (JSONException ex){
                        Log.e("ContractSearch",ex.getMessage());
                        Toast.makeText(ContractListActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }

            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ContractListActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
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
                            showProgress(false);
                            messageTip.setVisibility(View.VISIBLE);
                            swipeRefreshLayout.setVisibility(View.GONE);
                            Toast.makeText(ContractListActivity.this,"暂时没有符合搜索条件的合同信息",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ContractListActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }

    //获取合同监印、执行查询
    public void CommonContractSearch(final boolean isFirstLoad ,JSONObject keyword){
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
        Call<String> call = PostRequest.Instance.request.CommonContractSearch(keywordView);
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
                            if(listView.getFooterViewsCount() ==0){
                                listView.setFooterView(View.inflate(ContractListActivity.this, R.layout.item_listview_footer_loadmore, null), new OnLoadMoreScrollListener.OnLoadMoreStateListener() {
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
                                        ((TextView) footView.findViewById(R.id.footer_tv_msg)).setText("没有更多的合同了");
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
                        Toast.makeText(ContractListActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }

            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ContractListActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //获取合同监印、执行查询数量
    public void GetCommonContractSearchCount(final JSONObject requestJson){
        searchJson = requestJson;

        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),  requestJson.toString());
        Call<String> call = PostRequest.Instance.request.GetCommonContractSearchCount(keywordView);
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
                            CommonContractSearch(true,requestJson);
                        }else{
                            showProgress(false);
                            messageTip.setVisibility(View.VISIBLE);
                            swipeRefreshLayout.setVisibility(View.GONE);
                            Toast.makeText(ContractListActivity.this,"暂时还没有符合搜索条件的合同信息",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ContractListActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //获取合同计划查询
    public void ContractPlanSearch(final boolean isFirstLoad ,JSONObject keyword){
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
        Call<String> call = PostRequest.Instance.request.ContractPlanSearch(keywordView);
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
                            if(listView.getFooterViewsCount() ==0){
                                listView.setFooterView(View.inflate(ContractListActivity.this, R.layout.item_listview_footer_loadmore, null), new OnLoadMoreScrollListener.OnLoadMoreStateListener() {
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
                                        ((TextView) footView.findViewById(R.id.footer_tv_msg)).setText("没有更多的合同了");
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
                        Toast.makeText(ContractListActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }

            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ContractListActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //获取合同计划查询数量
    public void GetContractPlanSearchCount(final JSONObject requestJson){
        searchJson = requestJson;

        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),  requestJson.toString());
        Call<String> call = PostRequest.Instance.request.GetContractPlanSearchCount(keywordView);
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
                            ContractPlanSearch(true,requestJson);
                        }else{
                            showProgress(false);
                            messageTip.setVisibility(View.VISIBLE);
                            swipeRefreshLayout.setVisibility(View.GONE);
                            Toast.makeText(ContractListActivity.this,"暂时还没有符合搜索条件的合同信息",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ContractListActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //合同完成
    public void FinishEvent(String contractID){
        JSONObject requestJson = new JSONObject();
        try{
            JSONArray val1 = new JSONArray();
            val1.put(contractID);
            requestJson.put("contractIDs",val1);
        }catch (JSONException ex){
            Log.i("CancelFinishEvent","json对象构造错误");
        }
        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),  requestJson.toString());
        Call<String> call = PostRequest.Instance.request.SetFinishContract(keywordView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try{
                        JSONObject jsonObj = new JSONObject(response.body());
                        Boolean res = jsonObj.getBoolean("d");
                        if(res==true){
                            Toast.makeText(ContractListActivity.this,"已将合同设为完成！",Toast.LENGTH_LONG).show();
                            GetContractFinishSearchCount(searchJson);
                        }else{
                            Toast.makeText(ContractListActivity.this,"合同状态设置失败！",Toast.LENGTH_LONG).show();
                        }
                    }catch (JSONException ex){
                        Log.i("CancelFinishEvent","json对象构造错误");
                    }
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ContractListActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //合同终止
    public void IllegalFinishEvent(String contractID){
        JSONObject requestJson = new JSONObject();
        try{
            JSONArray val1 = new JSONArray();
            val1.put(contractID);
            requestJson.put("contractIDs",val1);
        }catch (JSONException ex){
            Log.i("CancelFinishEvent","json对象构造错误");
        }
        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),  requestJson.toString());
        Call<String> call = PostRequest.Instance.request.SetIllegalFinishContract(keywordView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try{
                        JSONObject jsonObj = new JSONObject(response.body());
                        Boolean res = jsonObj.getBoolean("d");
                        if(res==true){
                            Toast.makeText(ContractListActivity.this,"已将合同设为终止！",Toast.LENGTH_LONG).show();
                            GetContractFinishSearchCount(searchJson);
                        }else{
                            Toast.makeText(ContractListActivity.this,"合同状态设置失败！",Toast.LENGTH_LONG).show();
                        }
                    }catch (JSONException ex){
                        Log.i("CancelFinishEvent","json对象构造错误");
                    }
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ContractListActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //合同取消完成
    public void CancelFinishEvent(String contractID){
        JSONObject requestJson = new JSONObject();
        try{
            JSONArray val1 = new JSONArray();
            val1.put(contractID);
            requestJson.put("contractIDs",val1);
        }catch (JSONException ex){
            Log.i("CancelFinishEvent","json对象构造错误");
        }
        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),  requestJson.toString());
        Call<String> call = PostRequest.Instance.request.RecoverContractStatus(keywordView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try{
                        JSONObject jsonObj = new JSONObject(response.body());
                        Boolean res = jsonObj.getBoolean("d");
                        if(res==true){
                            Toast.makeText(ContractListActivity.this,"已将合同设为执行中！",Toast.LENGTH_LONG).show();
                            GetContractFinishSearchCount(searchJson);
                        }else{
                            Toast.makeText(ContractListActivity.this,"合同状态设置失败！",Toast.LENGTH_LONG).show();
                        }
                    }catch (JSONException ex){
                        Log.i("CancelFinishEvent","json对象构造错误");
                    }
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ContractListActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
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
        ChooseFun();
        swipeRefreshLayout.setRefreshing(false);
    }
    /**
     * 上滑加载事件定义
     */
    private void loadMore() {
        if (mPageCount > 1 && mCurrentPage<mPageCount) {
            ChooseFun();
        } else {
            ContractListActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listView.setEnd(true);
                }
            });
        }
        ContractListActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                searchResultItemAdapter.notifyDataSetChanged();
            }
        });
    }
    //根据类型选择函数
    public void ChooseFun(){
        switch(Type){
            case "cx":
                //显示合同查询相关搜索界面
                QuerySearch(false,searchJson);
                break;
            case "gl":
                //显示合同管理相关搜索界面
                ContractSearch(false,searchJson);
                break;
            case "jh":
                ContractPlanSearch(false,searchJson);
                break;
            case "zx":case "jy":
                //显示合同计划相关搜索界面
                CommonContractSearch(false,searchJson);
                break;
            case "wc":
                //显示合同完成相关搜索界面
                ContractFinishSearch(false,searchJson);
                break;
        }
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
