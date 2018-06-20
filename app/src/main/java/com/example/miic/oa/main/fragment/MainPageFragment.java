package com.example.miic.oa.main.fragment;


import android.annotation.SuppressLint;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
    //总共页数
    private int mPageCount = 0;
    //每页信息数量
    private int mPageSize = 20;
    //当前页
    private int mCurrentPage=0;

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
        workIconList.add(new WorkPageIcon(R.drawable.blogger,"合同管理","IconID"));
        workIconList.add(new WorkPageIcon(R.drawable.car,"车辆管理","IconID"));
        workIconList.add(new WorkPageIcon(R.drawable.readability,"会议管理","IconID"));
        workIconmGridView=(GridView) rootView.findViewById(R.id.gv_icon_nav);
        workIconAdapter=new WorkPageIconAdapter(getActivity(),workIconList);
        workIconmGridView.setAdapter(workIconAdapter);
        //点击事件
        workIconmGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //获得点击的图标的ID，然后将id传递到下一页，
//                String clickIconID = workIconList.get(position).getIconID();
//                Intent intent=add Intent(mContext,WorkChildActivity.class);
//                intent.putExtra("clickIconID",clickIconID);
//                startActivity(intent);
                Toast.makeText(mContext,"功能暂未开放",Toast.LENGTH_SHORT).show();
            }
        });
    }
    //初始化listview--待处理事务
    private void initListView(){
        affairListView = (LoadMoreListView)rootView.findViewById(R.id.pending_item_container);
        pendingItemList = new ArrayList<>();
        pendingItemList.add(new MainPagePendingItem("1","审批：请假申请","2018-7-5",R.drawable.pen));
        pendingItemList.add(new MainPagePendingItem("1","工作：请假申请","2018-7-5",R.drawable.full_time));
        pendingItemList.add(new MainPagePendingItem("1","消息：请假申请","2018-7-5",R.drawable.comment));
        pendingItemList.add(new MainPagePendingItem("1","审批：请假申请","2018-7-5",R.drawable.pen));
        pendingItemList.add(new MainPagePendingItem("1","审批：请假申请","2018-7-5",R.drawable.pen));
        pendingItemAdapter = new MainPagePendingItemAdapter(mContext,pendingItemList);
        affairListView.setAdapter(pendingItemAdapter);
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
            startActivityForResult(intent,1);
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

//    /**
//     *下拉刷新事件定义
//     */
//    private void reFresh() {
//        pendingItemList.clear();
//        newsListView.setEnd(false);
//        mCurrentPage = 0;
//        getNewsByColumnID(ColumnID);
//        swipeRefreshLayout.setRefreshing(false);
//    }
//    /**
//     * 上滑加载事件定义
//     */
//    private void loadMore() {
//        if (mPageCount > 1) {
//            getNewsByColumnID(ColumnID);
//        } else {
//            getActivity().runOnUiThread(add Runnable() {
//                @Override
//                public void run() {
//                    newsListView.setEnd(true);
//                }
//            });
//        }
//        getActivity().runOnUiThread(add Runnable() {
//            @Override
//            public void run() {
//                pendingItemAdapter.notifyDataSetChanged();
//            }
//        });
//    }
}
