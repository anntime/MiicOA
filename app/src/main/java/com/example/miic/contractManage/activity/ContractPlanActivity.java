package com.example.miic.contractManage.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.base.http.PostRequest;
import com.example.miic.base.progressbar.DownLoadProgressBar;
import com.example.miic.contractManage.adapter.ContractApprovalAccFileAdapter;
import com.example.miic.contractManage.adapter.ContractExecuteTimeLineItemAdapter;
import com.example.miic.contractManage.common.ContractCommon;
import com.example.miic.contractManage.item.ContractApprovalAccFileItem;
import com.example.miic.contractManage.item.ContractExecuteTimeLineItem;
import com.example.miic.oa.common.OpenFileUtil;
import com.example.miic.oa.common.Setting;
import com.example.miic.oa.common.fileDownload.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.miic.oa.common.LvHeightUtil.setListViewHeightBasedOnChildren;
import static com.example.miic.oa.common.Setting.requestPermission;
import static com.example.miic.oa.common.Setting.stampToDate;
import static com.example.miic.oa.common.Setting.stampToTime;

public class ContractPlanActivity extends AppCompatActivity {

    private RecyclerView planRecyclerView;
    private ContractExecuteTimeLineItemAdapter planTimeLineAdapter;
    private List<ContractExecuteTimeLineItem> planDataList = new ArrayList<>();

    private List<ContractApprovalAccFileItem> contractAccList;
    private ContractApprovalAccFileAdapter contractFileAccAdapter;

    private TextView contractIDTv;
    private TextView contractNameTv;
    private TextView contractNumTv;
    private TextView contractUndertakeDeptTv;
    private TextView contractUndertakeNameTv;
    private TextView contractSignerTv;
    private TextView contractSignTimeTv;
    private TextView contractSubjectAmountTv;
    private TextView contractScottareTypeTv;

    private TextView contractOtherCompanyTv;
    private TextView contractLinkManTv;
    private TextView contractLinkTelTv;
    private TextView contractTimeTv;
    private TextView contractIndentificationItemTv;
    private TextView contractSealTypeTv;
    private TextView contractPrintNumTv;
    private TextView contractPrintRemarkTv;
    private TextView contractTypeTv;
    private TextView contractDrafttypeTv;
    private TextView contractFileTv;
    private ListView contractAccFileLv;
    private TextView contractAccFileTv;
    private TextView contractApproveStatusTv;
    private Button addExecuteBtn;




    private String ClickID;
    private Activity mActivity;
    private JSONArray Planlist;
    private long SubjectAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_plan);
        //获取上一页面传递的参数
        Intent intent = getIntent();
        ClickID= intent.getStringExtra("clickID");
        mActivity = this;
        setHeader();
        initView();
        GetDetailInfo();
        SearchContractPlan();
    }
    private void setHeader() {
        TextView titleTv = (TextView) findViewById(R.id.page_title);
        LinearLayout backLin = (LinearLayout)findViewById(R.id.back);
        LinearLayout rightLin = (LinearLayout)findViewById(R.id.menu);
        titleTv.setText("合同计划详情");
        rightLin.setVisibility(View.INVISIBLE);
        backLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView(){
        contractIDTv = (TextView)findViewById(R.id.contract_id);
        contractNameTv = (TextView)findViewById(R.id.contract_name);
        contractNumTv = (TextView)findViewById(R.id.contract_num);
        contractUndertakeDeptTv = (TextView)findViewById(R.id.contract_undertake_dept);
        contractUndertakeNameTv = (TextView)findViewById(R.id.contract_undertake_name);
        contractSignerTv = (TextView)findViewById(R.id.contract_signer);
        contractSignTimeTv = (TextView)findViewById(R.id.sign_time);
        contractSubjectAmountTv = (TextView)findViewById(R.id.subject_amount);
        contractScottareTypeTv = (TextView)findViewById(R.id.scottare_type);
        contractOtherCompanyTv = (TextView)findViewById(R.id.other_company);
        contractLinkManTv = (TextView)findViewById(R.id.link_man);
        contractLinkTelTv = (TextView)findViewById(R.id.link_tel);
        contractTimeTv = (TextView)findViewById(R.id.contract_time);
        contractIndentificationItemTv = (TextView)findViewById(R.id.contract_identification_item);
        contractSealTypeTv = (TextView)findViewById(R.id.contract_seal_type);
        contractPrintNumTv = (TextView)findViewById(R.id.contract_print_num);
        contractPrintRemarkTv = (TextView)findViewById(R.id.contract_print_remark);
        contractTypeTv = (TextView)findViewById(R.id.contract_type);
        contractDrafttypeTv = (TextView)findViewById(R.id.draft_type);
        contractFileTv = (TextView)findViewById(R.id.contract_file);
        contractAccFileLv = (ListView)findViewById(R.id.contract_acc_file);
        contractAccFileTv = (TextView)findViewById(R.id.contract_acc_file_tv);
        contractApproveStatusTv = (TextView)findViewById(R.id.contract_approve_status);
        addExecuteBtn = (Button)findViewById(R.id.add_plan_btn);
        addExecuteBtn.setOnClickListener(addExecuteListener);



        planRecyclerView = (RecyclerView) findViewById(R.id.plan_recyclerView);
        planRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        planRecyclerView.setHasFixedSize(true);
        planTimeLineAdapter = new ContractExecuteTimeLineItemAdapter(planDataList,ContractPlanActivity.this);
        planRecyclerView.setAdapter(planTimeLineAdapter);
    }
    private View.OnClickListener addExecuteListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ContractPlanActivity.this, AddPlanActivity.class);
            //获得点击的新闻的id，然后把这个id传到新的activity中。
            intent.putExtra("clickID", ClickID);
            intent.putExtra("Planlist",Planlist.toString());
            intent.putExtra("subjectAmount",SubjectAmount);
            startActivityForResult(intent, 1);
        }
    };
    //返回这个活动时，刷新页面
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                break;
            default:
                SearchContractPlan();
                break;
        }
    }
    //获取合同详细信息
    public void GetDetailInfo(){
        JSONObject requestJson = new JSONObject();
        try{
            requestJson.put("id",ClickID);
        }catch (JSONException ex){
            Log.i("GetDetailInfo","json对象构造错误");
        }
        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Call<String> call = PostRequest.Instance.request.GetDetailInfo(keywordView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        String jsonStrTemp = jsonObject.getString("d");
                        if(!jsonStrTemp.equals("[]")){
                            ContractCommon contract = new ContractCommon();
                            JSONArray arrayTemp = new JSONArray(jsonStrTemp);
                            JSONObject objectTemp = arrayTemp.getJSONObject(0);
                            contractIDTv.setText(objectTemp.getString("ContractNo"));
                            contractNameTv.setText(objectTemp.getString("ContractName"));
                            contractNumTv.setText(objectTemp.getString("ContractNum"));
                            contractUndertakeDeptTv.setText(objectTemp.getString("DeptName"));
                            contractUndertakeNameTv.setText(objectTemp.getString("CreaterName"));
                            contractSignerTv.setText(objectTemp.getString("SignatoryName"));
                            //时间样式
                            if(objectTemp.getString("SignatoryTime").contains("Date")){
                                contractSignTimeTv.setText(stampToDate(objectTemp.getString("SignatoryTime")));
                            }else{
                                contractSignTimeTv.setText(objectTemp.getString("SignatoryTime").split(" ")[0]);
                            }
                            //金额处理
                            long amount = objectTemp.getLong("SubjectAmount");
                            SubjectAmount = amount;
                            DecimalFormat df = new DecimalFormat("#,###.00");
                            String m = df.format(amount);
                            contractSubjectAmountTv.setText("￥"+m);
                            contractScottareTypeTv.setText(contract.getScottareTypeStr(objectTemp.getString("ScottareType")));
                            contractOtherCompanyTv.setText(objectTemp.getString("OtherCompany"));
                            contractLinkManTv.setText(objectTemp.getString("LinkMan"));
                            contractLinkTelTv.setText(objectTemp.getString("LinkTel"));
                            //时间样式
                            String contractStart ;
                            String contractEnd ;
                            if(objectTemp.getString("ContractBeginTime").contains("Date")){
                                contractStart = stampToDate(objectTemp.getString("ContractBeginTime"));
                            }else{
                                contractStart = objectTemp.getString("ContractBeginTime").split(" ")[0];

                            }
                            if(objectTemp.getString("IsLong").equals("2")){
                                if(objectTemp.getString("ContractEndTime").contains("Date")){
                                    contractEnd = stampToDate(objectTemp.getString("ContractEndTime"));
                                }else{
                                    contractEnd = objectTemp.getString("ContractEndTime").split(" ")[0];
                                }
                            }else{
                                //长期合同
                                contractEnd = "长期";
                            }
                            contractTimeTv.setText(contractStart+" ~ "+contractEnd);

                            Log.i("IdentificationItem",objectTemp.getString("IdentificationItem"));
                            Log.i("PrintType",objectTemp.getString("PrintType"));
                            contractIndentificationItemTv.setText(contract.getIdentificationItemStr(objectTemp.getString("IdentificationItem")));
                            contractSealTypeTv.setText(contract.getIdentificationItemStr(objectTemp.getString("IdentificationItem")+""+contract.getPrintTypeStr(objectTemp.getString("PrintType"))));
                            contractPrintNumTv.setText(objectTemp.getString("PrintCount")+"份");
                            if (objectTemp.getString("PrintRemark") != "") {
                                contractPrintRemarkTv.setText(objectTemp.getString("PrintRemark"));
                            } else {
                                contractPrintRemarkTv.setText("暂无");
                            }

                            contractTypeTv.setText(contract.getContractTypeOneStr(objectTemp.getString("ContractType1"))+"-"+contract.getContractTypeTwoStr(objectTemp.getString("ContractType2")));
                            contractDrafttypeTv.setText(contract.getContractDraftTypeStr(objectTemp.getString("DraftType")));

                            final String contractUrl = new Setting().getService() + objectTemp.getString("ContractUrl");
                            Log.i("文件地址：",contractUrl);
                            String[] arr = contractUrl.split("/");
                            final String contractName = arr[arr.length-1];
                            contractFileTv.setText(contractName);
                            contractFileTv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //下载文件(单个文件下载)
                                    Setting.downLoadSingle(contractUrl, contractName,mActivity);
                                }
                            });
                            //判断合同附件
                            JSONArray accList = objectTemp.getJSONArray("AccList");
                            if (accList != null&&accList.length()!=0) {
                                if(!accList.getJSONObject(0).isNull("ID") ){
                                    //有可能是文件，有可能是图片
                                    //只能是文件，显示link_TextView
                                    contractAccList = new ArrayList<>();
                                    for (int i = 0; i < accList.length(); i++) {
                                        String title = accList.getJSONObject(i).getString("FileName");
                                        String link = new Setting().getService() + accList.getJSONObject(i).getString("FilePath");
                                        contractAccList.add(new ContractApprovalAccFileItem(title, link,0));
                                    }
                                    //附件
                                    contractFileAccAdapter = new ContractApprovalAccFileAdapter(ContractPlanActivity.this, contractAccList);
                                    contractAccFileLv.setAdapter(contractFileAccAdapter);
                                    setListViewHeightBasedOnChildren(contractAccFileLv);
                                    contractAccFileLv.setOnItemClickListener(listItemClickListener);
                                    contractFileAccAdapter.notifyDataSetChanged();
                                    contractAccFileTv.setVisibility(View.GONE);
                                    contractAccFileLv.setVisibility(View.VISIBLE);
                                }else{
                                    contractAccFileLv.setVisibility(View.GONE);
                                    contractAccFileTv.setVisibility(View.VISIBLE);
                                    contractAccFileTv.setText("暂无合同附件");
                                }
                            }
                            contractApproveStatusTv.setText(contract.getContractApproveStatusStr(objectTemp.getString("Status")));
                        }
                    }catch (JSONException ex){
                        Log.e("GetDetailInfo",ex.getMessage());
                        Toast.makeText(ContractPlanActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }

            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ContractPlanActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //获取合同计划详情
    private void SearchContractPlan(){
        JSONObject requestJson = new JSONObject();
        try{
            requestJson.put("contractID",ClickID);
        }catch (JSONException ex){
            Log.i("ContractSearch","json对象构造错误");
        }
        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Call<String> call = PostRequest.Instance.request.SearchContractPlan(keywordView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        String jsonStrTemp = jsonObject.getString("d");
                        if(!jsonStrTemp.equals("[]")) {
                            JSONArray jsonTemp = new JSONArray(jsonStrTemp);
                            Planlist = jsonTemp;
                            float totalPlanAmount = 0;
                            for (int index = 0;index<jsonTemp.length();index++){
                                JSONObject item = jsonTemp.getJSONObject(index);
                                String executeDate;
                                String temp = item.isNull("PlanDate")?"":item.getString("PlanDate");
                                if(temp.contains("Date")){
                                    executeDate = stampToDate(temp);
                                }else{
                                    executeDate = temp;
                                }

                                String executeAmount;
                                long amount = item.getLong("PlanAmount");
                                totalPlanAmount += amount;
                                DecimalFormat df = new DecimalFormat("#,###.00");
                                executeAmount = "￥" + df.format(amount);

                                String payWay = item.getString("Remark").equals("")?"暂无":item.getString("Remark");

                                planDataList.add(new ContractExecuteTimeLineItem(executeDate, executeAmount, payWay));
                            }
                            if(totalPlanAmount>=SubjectAmount){
                                addExecuteBtn.setVisibility(View.GONE);
                            }
                            planTimeLineAdapter.notifyDataSetChanged();
                            TextView planMessage = (TextView)findViewById(R.id.plan_message);
                            planMessage.setVisibility(View.GONE);
                        }else{
                            TextView planMessage = (TextView)findViewById(R.id.plan_message);
                            planMessage.setVisibility(View.VISIBLE);
                        }
                    }catch (JSONException ex){
                        Log.e("ContractSearch",ex.getMessage());
                        Toast.makeText(ContractPlanActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(ContractPlanActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }

    //文件点击事件
    private AdapterView.OnItemClickListener listItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            if(position<contractAccList.size()){
                //通过view获取其内部的组件，进而进行操作
                ContractApprovalAccFileItem fileAcc = contractAccList.get(position);
                String title = fileAcc.getTitle();
                String link = fileAcc.getLink();
                int progress = fileAcc.getProgress();
                //下载文件
                downLoad(link, title,position);
            }
        }
    };
    private Handler mHandler  = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            publishProgress(msg.arg1,msg.arg2);
        }
    };
    public void publishProgress(final int progress,  final int positionInAdapter) {
        contractFileAccAdapter.getItem(positionInAdapter).setProgress(progress);

        if(positionInAdapter >= contractAccFileLv.getFirstVisiblePosition() &&
                positionInAdapter <= contractAccFileLv.getLastVisiblePosition()) {
            int positionInListView = positionInAdapter - contractAccFileLv.getFirstVisiblePosition();
            DownLoadProgressBar item = (DownLoadProgressBar) contractAccFileLv.getChildAt(positionInListView)
                    .findViewById(R.id.pb_mp);

            item.setProgress(progress);
        }
    }
    /**
     * 从服务器下载文件
     * @param path 下载文件的地址
     * @param FileName 文件名字
     */
    public void downLoad(final String path, final String FileName,final int listPosition) {
        Log.i("下载函数：：","下载？");
        //更新一下列表中表示下载的boolean变量，表示该条数据正在下载
        contractFileAccAdapter.getItem(listPosition).setIsdownload(true);
        if(listPosition >= contractAccFileLv.getFirstVisiblePosition() &&
                listPosition <= contractAccFileLv.getLastVisiblePosition()) {
            int positionInListView = listPosition - contractAccFileLv.getFirstVisiblePosition();
            final DownLoadProgressBar item  = (DownLoadProgressBar) contractAccFileLv.getChildAt(positionInListView)
                    .findViewById(R.id.pb_mp);
            item.setVisibility(View.VISIBLE);
            requestPermission(ContractPlanActivity.this);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(path);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setReadTimeout(5000);
                        con.setConnectTimeout(5000);
                        con.setRequestProperty("Charset", "UTF-8");
                        con.setRequestMethod("GET");
                        if (con.getResponseCode() == 200) {
                            InputStream is = con.getInputStream();//获取输入流
                            //获取文件总长度
                            int contentLength = con.getContentLength();
                            Log.i("文件长度：",contentLength+"");
                            item.setMaxProgress(contentLength);
                            FileOutputStream fileOutputStream = null;//文件输出流
                            if (is != null) {
                                //记录下载进度
                                int downloadSize = 0;
                                final String filePath = Environment.getExternalStorageDirectory().toString() + "/OA_file";
                                FileUtils fileUtils = new FileUtils(filePath);
                                /* 取得扩展名 */
                                String[] arr = path.split("\\.");
                                Log.i("下载地址：",path );
                                String end="pdf";
                                if(arr.length!=0){
                                    end =  arr[arr.length-1];
                                }
                                fileOutputStream = new FileOutputStream(fileUtils.createFile(FileName+"."+end));//指定文件保存路径，代码看下一步
                                Log.i("下载后缀：",end );
                                byte[] buf = new byte[1024];
                                int ch;
                                while ((ch = is.read(buf)) != -1) {
                                    fileOutputStream.write(buf, 0, ch);//将获取到的流写入文件中
                                    downloadSize += ch;
                                    //更新进度条
                                    Message message = mHandler.obtainMessage();
                                    Log.i("下载数量：",downloadSize+"");
                                    message.arg1 = downloadSize;
                                    message.arg2 = listPosition;
                                    mHandler.sendMessage(message);
                                    if(downloadSize==contentLength){
                                        OpenFileUtil.openFile(filePath+"/"+FileName+"."+end);
                                    }

                                }
                            }
                            if (fileOutputStream != null) {
                                fileOutputStream.flush();
                                fileOutputStream.close();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Toast.makeText(ContractPlanActivity.this,"文件下载至："+Environment.getExternalStorageDirectory().toString() + "/OA_file",Toast.LENGTH_SHORT).show();
        }
    }
}
