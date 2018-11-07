package com.example.miic.oa.main.fragment;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.base.loadmore.OnLoadMoreScrollListener;
import com.example.miic.carManage.activity.CarApprovalActivity;
import com.example.miic.carManage.activity.CarApprovalDetailActivity;
import com.example.miic.carManage.activity.CarIndexActivity;
import com.example.miic.carManage.activity.CarManageActivity;
import com.example.miic.contractManage.activity.ContractApprovalActivity;
import com.example.miic.contractManage.activity.ContractIndexActivity;
import com.example.miic.contractManage.activity.ContractManageActivity;
import com.example.miic.contractManage.adapter.ContractSearchResultItemAdapter;
import com.example.miic.contractManage.item.ContractSearchResultItem;
//import com.example.miic.meetingRoomManage.activity.MeetingRoomManageActivity;
import com.example.miic.meetingRoomManage.activity.MeetingRoomManageActivity;
import com.example.miic.oa.common.EnumMessageType;
import com.example.miic.oa.news.activity.NewsDetailActivity;
import com.example.miic.oa.search.activity.SearchActivity;
import com.example.miic.oa.work.activity.WorkChildActivity;
import com.example.miic.oa.common.BannerAdapter;
import com.example.miic.oa.common.BannerModel;
import com.example.miic.oa.main.item.MainPagePendingItem;
import com.example.miic.oa.main.adapter.MainPagePendingItemAdapter;
import com.example.miic.oa.work.item.WorkPageGridView;
import com.example.miic.oa.work.item.WorkPageIcon;
import com.example.miic.oa.work.adapter.WorkPageIconAdapter;
import com.example.miic.oa.common.Setting;
import com.example.miic.base.http.PostRequest;
import com.example.miic.base.loadmore.LoadMoreListView;
import com.example.miic.oa.common.marqueeview.MarqueeView;
import com.example.miic.sealManagement.activity.SealIndexActivity;
import com.example.miic.sealManagement.activity.SealApprovalDetailActivity;
import com.example.miic.sealManagement.activity.SealManageActivity;
import com.google.gson.JsonParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.bgabanner.BGABanner;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.example.miic.oa.common.Setting.stampToTime;
import static com.example.miic.oa.common.Setting.strToDate;
import static java.lang.Integer.parseInt;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("NewApi")
public class MainPageFragment extends Fragment implements BGABanner.Delegate<ImageView, String> {

    //轮播初始化
    private BGABanner mAlphaBanner;
    private BannerModel bannerModel;

    //图标导航初始化
    private List<WorkPageIcon> workIconList;
    private GridView workIconmGridView;
    private WorkPageIconAdapter workIconAdapter;
    //待办项列表初始化
    private LoadMoreListView affairListView;
    private List<MainPagePendingItem> pendingItemList;
    private MainPagePendingItemAdapter pendingItemAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView messageTip;
    //搜索等待控制
    private View searchResultView;
    private View searchProgressView;
    //总共页数
    private int mPageCount = 0;
    //每页信息数量
    private int mPageSize = 10;
    //当前页
    private int mCurrentPage=0;
    private JSONObject searchJson = new JSONObject();

    //滚动通知初始化
    private MarqueeView marqueeView;
    private List<String> newsIDList;

    //搜索
    private LinearLayout searchBtn;

    private View rootView;
    private Context mContext;
    public MainPageFragment() {
        // Required empty public constructor
        this.mContext = getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_main_page, container, false);
        initMainCarousel();
        initMarquee();
        initMainIconNav();
        initListView();
        //搜索按钮绑定搜索事件
        searchBtn = (LinearLayout)rootView.findViewById(R.id.search_button);
        searchBtn.setOnClickListener(searchClickListener);



        return rootView;
    }

    //初始化竖向跑马灯---滚动通知
    private void initMarquee(){
        marqueeView = rootView.findViewById(R.id.marqueeView);
        //请求后台获取滚动通知
        getMarqueeInfo();

    }
    //初始化图标导航
    private void initMainIconNav(){
        workIconList=new ArrayList<WorkPageIcon>();
        workIconList.add(new WorkPageIcon(R.drawable.pinboard,"用印管理","sealManage"));
        workIconList.add(new WorkPageIcon(R.drawable.blogger,"合同管理","contractManage"));
        workIconList.add(new WorkPageIcon(R.drawable.car,"车辆管理","carManage"));
        workIconList.add(new WorkPageIcon(R.drawable.readability,"会议室管理","meetingRoomManage"));
        workIconmGridView=(GridView) rootView.findViewById(R.id.gv_icon_nav);
        workIconAdapter=new WorkPageIconAdapter(getActivity(),workIconList);
        workIconmGridView.setAdapter(workIconAdapter);
        //点击事件
        workIconmGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //获得点击的图标的ID，然后将id传递到下一页，
                String clickIconID = workIconList.get(position).getIconID();
                Intent intent;
                switch (clickIconID){
                    case "sealManage":
                        intent=new Intent(mContext, SealIndexActivity.class);
                        startActivity(intent);
                        break;
                    case "contractManage":
                        intent=new Intent(mContext,ContractIndexActivity.class);
                        startActivity(intent);
                        break;
                    case "carManage":
                        intent=new Intent(mContext,CarIndexActivity.class);
                        startActivity(intent);
                        break;
                    case "meetingRoomManage":
                        intent=new Intent(mContext,MeetingRoomManageActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        Toast.makeText(mContext,"该功能尚未开放~",Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }
    //初始化listview--待处理事务
    private void initListView(){
        searchResultView = rootView.findViewById(R.id.search_result);
        searchProgressView = rootView.findViewById(R.id.search_progress);
        //列表展示相关
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refreshview);
        messageTip = (TextView)rootView.findViewById(R.id.message_tip);
        affairListView = (LoadMoreListView)rootView.findViewById(R.id.pending_item_container);
        pendingItemList = new ArrayList<>();

        pendingItemAdapter = new MainPagePendingItemAdapter(mContext,pendingItemList);
        affairListView.setAdapter(pendingItemAdapter);
        pendingItemAdapter.setOnItemMyClickListener(new MainPagePendingItemAdapter.onItemMyClickListener() {
            @Override
            public void onMyClick(int type, int position) {
                if(position<pendingItemList.size()) {
                    //点击了消息怎么办？？？？？？？？？？？？？？？？？？？？？
                    String messageID = pendingItemList.get(position).getMessageID();
                    int messageType = pendingItemList.get(position).getMessageType();
                    String contentID = pendingItemList.get(position).getContentID();
                    ReadMessage(messageID,messageType,contentID);
                }
            }
        });
        showProgress(true);
        GetMessageSearchCount();
        //getAffair(true);
    }
    //初始化轮播
    private void initMainCarousel(){
        this.mContext = getActivity();
        //初始化轮播
        bannerModel =new BannerModel();
        mAlphaBanner = (BGABanner)rootView.findViewById(R.id.banner_main_alpha);
        mAlphaBanner.setDelegate(this);

        //获取轮播图信息
        JSONObject requestJson = new JSONObject();
        try{
            requestJson.put("isApp","1");
        }catch (JSONException ex){
            Log.i("MainPageFragment","json对象构造错误");
        }

        RequestBody View = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.GetCarouselInfo (View);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try{
                        JSONArray jsonObject = new JSONArray(new JSONObject(response.body()).getString("d"));
                        System.out.println("body:" + jsonObject.toString());
                        //JSONObject bannerModel = add JSONObject();
                        List<String> imgs = new ArrayList<>();
                        List<String> tips = new ArrayList<>();
                        List<String> isLinks = new ArrayList<>();
                        if(jsonObject!=null){
                            for (int i=0;i<jsonObject.length();i++){
                                imgs.add(new Setting().getService()+jsonObject.getJSONObject(i).getString("Url"));
                                tips.add(jsonObject.getJSONObject(i).getString("Content").trim());
                                isLinks.add(jsonObject.getJSONObject(i).getString("IsLinks").trim());
                            }
                            bannerModel.imgs=imgs;
                            bannerModel.tips=tips;
                            bannerModel.isLinks=isLinks;
                            /**
                             * 设置是否开启自动轮播，需要在 setData 方法之前调用，并且调了该方法后必须再调用一次 setData 方法
                             * 例如根据图片当图片数量大于 1 时开启自动轮播，等于 1 时不开启自动轮播
                             */
                            mAlphaBanner.setAutoPlayAble(bannerModel.imgs.size() > 1);

                            mAlphaBanner.setAdapter(new BannerAdapter());
                            mAlphaBanner.setData(bannerModel.imgs, bannerModel.tips);


                        }
                    }catch (JSONException ex){
                        Log.i("MainPageFragment","json对象构造错误");
                    }
                }else{
                    Log.i("MainPageFragment","网络错误");
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);

    }
    public void onBannerItemClick(BGABanner banner, ImageView itemView, String model, int position) {
        if (bannerModel.isLinks.get(position).equals("1")){
            Uri uri = Uri.parse(bannerModel.tips.get(position));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }else{

        }
    }
    //搜索按钮的搜索事件
    private View.OnClickListener searchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent(mContext,SearchActivity.class);
            startActivity(intent);
        }
    };
    //跑马灯点击事件
    private MarqueeView.OnItemClickListener itemClickListener = new MarqueeView.OnItemClickListener() {
        @Override
        public void onItemClick(int position, TextView textView) {

            Intent intent=new Intent(mContext,NewsDetailActivity.class);
            intent.putExtra("clickNewsID",newsIDList.get(position));
            startActivityForResult(intent,0);
        }
    };

    //获取滚动通知
    private void getMarqueeInfo(){
        JSONObject requestJson = new JSONObject();
        try{
            requestJson.put("columnID","3834b118-7f64-7c35-c054-1fa57f64a9cf");
            requestJson.put("top","3");
            requestJson.put("isFull",true);
        }catch (JSONException ex){
            Log.i("MainPageFragment","json对象构造错误");
        }
        RequestBody newsInfoView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.GetTopNews (newsInfoView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try{
                        JSONArray jsonObject = new JSONArray(new JSONObject(response.body()).getString("d"));
                        System.out.println("body:" + jsonObject.toString());
                        List<CharSequence> list = new ArrayList<>();
                        newsIDList = new ArrayList<>();
                        if(jsonObject!=null){
                            for (int i=0;i<jsonObject.length();i++){
                                list.add(jsonObject.getJSONObject(i).getString("Title"));
                                newsIDList.add(jsonObject.getJSONObject(i).getString("ID"));
                            }
                            marqueeView.startWithList(list);
                            marqueeView.setOnItemClickListener(itemClickListener);
                        }
                    }catch (JSONException ex){
                        Log.i("MainPageFragment","json对象构造错误");
                    }
                }else{
                    Log.i("MainPageFragment","网络错误");
                }


            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }

    //获取首页消息通知
    public void MessageSearch(final boolean isFirstLoad ,JSONObject keyword){
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
        Call<String> call = PostRequest.Instance.request.MessageSearch(keywordView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                showProgress(false);
                if(isFirstLoad==true){
                    pendingItemList.clear();
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
                                pendingItemList.add(new MainPagePendingItem(objectTemp));

                            }
                            if(affairListView.getFooterViewsCount() ==0){
                                affairListView.setFooterView(View.inflate(getActivity(), R.layout.item_listview_footer_loadmore, null), new OnLoadMoreScrollListener.OnLoadMoreStateListener() {

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
                                    pendingItemAdapter.notifyDataSetChanged();
                                }
                            });
                            affairListView.requestFocus();
                            messageTip.setVisibility(View.GONE);
                            swipeRefreshLayout.setVisibility(View.VISIBLE);
                        }
                        else{
                            messageTip.setVisibility(View.VISIBLE);
                            swipeRefreshLayout.setVisibility(View.GONE);
                        }
                        pendingItemAdapter.notifyDataSetChanged();
                    }catch (JSONException ex){
                        Log.e("ContractSearch",ex.getMessage());
                        Toast.makeText(getActivity(),ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }

            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(getActivity(),"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //获取首页消息通知个数
    public void GetMessageSearchCount(){
        final JSONObject requestJson = new JSONObject();
        try{
            JSONObject requestJson1 = new JSONObject();
            requestJson1.put("Keyword","");
            requestJson.put("keyword",requestJson1);
        }catch (JSONException ex){
            Log.i("requestApproveJson","json对象构造错误");
        }
        searchJson = requestJson;
        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),  requestJson.toString());
        Call<String> call = PostRequest.Instance.request.GetMessageSearchCount(keywordView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println("count是：" + response.body());
                if(response.body()!=null){
                    try{
                        JSONObject jsonObject = new JSONObject(response.body());
                        int count = parseInt(jsonObject.getString("d"));
                        //判断新闻评论数量不等于0
                        if(count!=0){
                            //总共页数
                            mPageCount = (int)Math.ceil(count/20)+1;
                            //每页信息数量mPageSize = 20;
                            //当前页
                            mCurrentPage=0;
                            MessageSearch(true,requestJson);
                        }else{
                            showProgress(false);
                            messageTip.setVisibility(View.VISIBLE);
                            swipeRefreshLayout.setVisibility(View.GONE);
                            Toast.makeText(getActivity(),"暂时还没有符合搜索条件的合同信息",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(),"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //阅读消息操作
    public void ReadMessage(String messageID,final int messageType,final String contentID){
        JSONObject obj = new JSONObject();
        try{
            JSONArray arr = new JSONArray();
            arr.put(messageID);
            obj.put("ids",arr);
        }catch (JSONException ex){
            Log.i("ReadMessage","json解析错误");
        }
        RequestBody keywordView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),  obj.toString());
        Call<String> call = PostRequest.Instance.request.ReadMessage(keywordView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println("count是：" + response.body());
                if (response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        if(jsonObject.getBoolean("d")==true){
                            if (messageType == EnumMessageType.Comment.getIndex() ||
                                    messageType == EnumMessageType.CommentAt.getIndex() ||
                                    messageType == EnumMessageType.Praise.getIndex() ||
                                    messageType == EnumMessageType.Tread.getIndex() ||
                                    messageType == EnumMessageType.NewsApprove.getIndex()) {
                                //获得点击的新闻的id，然后把这个id传到新的activity中。
                                Intent intent=new Intent(mContext,NewsDetailActivity.class);
                                intent.putExtra("clickNewsID",contentID);
                                startActivityForResult(intent,1);
                            } else if (messageType == EnumMessageType.SealApprove.getIndex()) {

                                //获得点击的新闻的id，然后把这个id传到新的activity中。
                                Intent intent=new Intent(mContext,SealApprovalDetailActivity.class);
                                intent.putExtra("sealApplyID",contentID);
                                startActivityForResult(intent,1);
                            }else if (messageType == EnumMessageType.ContractApprove.getIndex()) {

                                //获得点击的新闻的id，然后把这个id传到新的activity中。
                                Intent intent=new Intent(mContext,ContractApprovalActivity.class);
                                intent.putExtra("clickID",contentID);
                                startActivityForResult(intent,1);
                            }else if (messageType == EnumMessageType.CarApprove.getIndex()) {
                                //获得点击的新闻的id，然后把这个id传到新的activity中。
                                Intent intent=new Intent(mContext,CarApprovalDetailActivity.class);
                                intent.putExtra("carApplyID",contentID);
                                startActivityForResult(intent,1);

                            }
                        }
                    } catch (JSONException ex) {
                        Log.i("ReadMessage", "json解析错误");
                    }
                }

            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(getActivity(),"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //返回这个活动时，刷新页面
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                break;
            case 1:
                reFresh();
            default:
                break;
        }
    }
    /**
     *下拉刷新事件定义
     */
    private void reFresh() {
        pendingItemList.clear();
        affairListView.setEnd(false);
        mCurrentPage = 0;
        MessageSearch(false,searchJson);
        swipeRefreshLayout.setRefreshing(false);
    }
    /**
     * 上滑加载事件定义
     */
    private void loadMore() {
        if (mPageCount > 1) {
            MessageSearch(false,searchJson);
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    affairListView.setEnd(true);
                }
            });
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pendingItemAdapter.notifyDataSetChanged();
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
