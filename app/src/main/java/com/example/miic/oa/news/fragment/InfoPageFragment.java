package com.example.miic.oa.news.fragment;


import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.common.MyApplication;
import com.example.miic.oa.search.activity.SearchActivity;
import com.example.miic.oa.common.BannerAdapter;
import com.example.miic.oa.common.BannerModel;
import com.example.miic.oa.news.adapter.InfoPageViewPagerAdapter;
import com.example.miic.oa.common.Setting;
import com.example.miic.base.http.PostRequest;
import com.example.miic.oa.common.magicindicator.MagicIndicator;
import com.example.miic.oa.common.magicindicator.UIUtil;
import com.example.miic.oa.common.magicindicator.ViewPagerHelper;
import com.example.miic.oa.common.magicindicator.commonnavigator.CommonNavigator;
import com.example.miic.oa.common.magicindicator.commonnavigator.abs.CommonNavigatorAdapter;
import com.example.miic.oa.common.magicindicator.commonnavigator.abs.IPagerIndicator;
import com.example.miic.oa.common.magicindicator.commonnavigator.abs.IPagerTitleView;
import com.example.miic.oa.common.magicindicator.commonnavigator.indicators.LinePagerIndicator;
import com.example.miic.oa.common.magicindicator.commonnavigator.titles.ColorTransitionPagerTitleView;
import com.example.miic.oa.common.magicindicator.commonnavigator.titles.SimplePagerTitleView;

import org.greenrobot.eventbus.EventBus;
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
public class InfoPageFragment extends Fragment implements BGABanner.Delegate<ImageView, String>  {

    /**
     * infoPageFragment start
     */
    private BGABanner mAlphaBanner;
    private BannerModel bannerModel;
    /**
     * infoPageFragment end
     */
    /**
     *滚动导航栏 start
     */
    private MagicIndicator magicIndicator;
    private ViewPager viewPager;
    private String[] titles;
    private String[] columnIDs;
    private String[] isFulls;
    private List<String> mDataList =new ArrayList<>();

    private InfoPageViewPagerAdapter viewPagerAdapter;
    private List<Fragment> fragments;
    private View rootView;
    /**
     *滚动导航栏 end
     */
    private LinearLayout searchBtn;
    private Context mContext;
    public InfoPageFragment() {
        this.mContext = getActivity();
    }

    private SharedPreferences sf;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_info_page, container, false);
        //搜索按钮绑定搜索事件
        searchBtn = (LinearLayout)rootView.findViewById(R.id.search_button);
        searchBtn.setOnClickListener(searchClickListener);
        sf = getActivity().getSharedPreferences("data",mContext.MODE_PRIVATE);
        //初始化infoFragmentPage
        initMainCarousel();
        initScrollBar();
        return rootView;
    }
    private void initScrollBar(){
        //初始化滚动导航栏
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        magicIndicator = (MagicIndicator) rootView.findViewById(R.id.navScrollBar);
        viewPager.setOffscreenPageLimit(0);
        GetMenuInfoList();
    }
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
                Toast.makeText(MyApplication.getContext(),throwable.getMessage() + "------" + throwable.getCause(),Toast.LENGTH_SHORT);
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);

    }
    public void onBannerItemClick(BGABanner banner, ImageView itemView, String model, int position) {
        if (bannerModel.isLinks.get(position).equals("1")){
            Uri uri = Uri.parse(bannerModel.tips.get(position));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }
    /**
     * 获取columnID，进行http请求获取新闻列表
     */
    public void newsSearchByColumn(final int position){
        JSONObject requestJson = new JSONObject();

        final String columnID = columnIDs[position];


        try{
            Boolean full = true;
            if(isFulls[position].equals("1")){
                full = false;
            }
            JSONObject val = new JSONObject();
            val.put("ColumnID",columnID);
            val.put("isFull",full);
            requestJson.put("keywordView",val);
            JSONObject val1 = new JSONObject();
            val1.put("pageStart","1");
            val1.put("pageEnd","10");
            requestJson.put("page",val1);
        }catch (JSONException ex){
            Toast.makeText(MyApplication.getContext(),ex.getMessage(),Toast.LENGTH_SHORT);
        }
        RequestBody newsInfoView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.NewsSearchByColumn(newsInfoView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                // 请求处理,输出结果
                System.out.println("body是：" + response.body());
                //初始化新闻列表（传递参数）
                EventBus.getDefault().post(response.body()+"<TAG><TAG>"+columnID+"<TAG><TAG>"+isFulls[position]);
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                Toast.makeText(MyApplication.getContext(),throwable.getMessage() + "------" + throwable.getCause(),Toast.LENGTH_SHORT);
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }

    //查询按钮绑定事件
    private View.OnClickListener searchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent(mContext,SearchActivity.class);
            startActivity(intent);
        }
    };
    //获取展示列表
    private void GetMenuInfoList(){

        JSONObject requestJson = new JSONObject();
        String userID=sf.getString("userID","");
        try{
            requestJson.put("userID",userID);
        }catch (JSONException ex){
            Log.i("InfoPageFrgment","json对象构造错误");
        }
        RequestBody newsInfoView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Call<String> call = PostRequest.Instance.request.GetMobileMenuInfoList (newsInfoView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try{
                        JSONArray jsonObject = new JSONArray(new JSONObject(response.body()).getString("d"));
                        System.out.println("body:" + jsonObject.toString());
                        List<String> columnIDList = new ArrayList<>();
                        List<String> titleList = new ArrayList<>();
                        List<String> isFullList = new ArrayList<>();
                        fragments =  new ArrayList<>();
                        if(jsonObject!=null){
                            for (int i=0;i<jsonObject.length();i++){
                                titleList.add(jsonObject.getJSONObject(i).getString("MenuName"));
                                columnIDList.add(jsonObject.getJSONObject(i).getString("ID"));
                                isFullList.add(jsonObject.getJSONObject(i).getString("PublishNode"));
                                //isFullList.add(true);
                                mDataList.add(jsonObject.getJSONObject(i).getString("MenuName"));
                            }
                            titles= new String[jsonObject.length()];
                            columnIDs = new String[jsonObject.length()];
                            isFulls = new String[jsonObject.length()];
                            titles = (String[])titleList.toArray(new String[jsonObject.length()]);
                            columnIDs = (String[])columnIDList.toArray(new String[jsonObject.length()]);
                            isFulls = (String[])isFullList.toArray(new String[jsonObject.length()]);
                            FragmentManager childFragmentManager = getChildFragmentManager();

                            for(int j=0;j<titles.length;j++){
                                fragments.add(new InfoChildFragment());
                            }
                            viewPagerAdapter = new InfoPageViewPagerAdapter(childFragmentManager, fragments,columnIDList);
                            viewPager.setAdapter(viewPagerAdapter);
                            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                @Override
                                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                                    System.out.println("onPageScrolled");
                                }
                                @Override
                                public void onPageSelected(int position) {
                                    System.out.println("onPageSelected");
                                    newsSearchByColumn(position);
                                }
                                @Override
                                public void onPageScrollStateChanged(int state) {
                                    System.out.println("onPageScrollStateChanged");
                                }
                            });
                            viewPager.addOnAdapterChangeListener(new ViewPager.OnAdapterChangeListener() {
                                @Override
                                public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {
                                    System.out.println("onAdapterChanged");
                                }
                            });

                            CommonNavigator commonNavigator = new CommonNavigator(mContext);
                            commonNavigator.setAdapter(new CommonNavigatorAdapter() {
                                @Override
                                public int getCount() {
                                    return mDataList == null ? 0 : mDataList.size();
                                }
                                @SuppressLint("ResourceAsColor")
                                @Override
                                public IPagerTitleView getTitleView(Context context, final int index) {
                                    SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                                    simplePagerTitleView.setText(mDataList.get(index));
                                    simplePagerTitleView.setNormalColor(Color.parseColor("#222222"));
                                    simplePagerTitleView.setSelectedColor(Color.parseColor("#ff5252"));
                                    simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            viewPager.setCurrentItem(index);
                                            newsSearchByColumn(index);
                                        }
                                    });
                                    return simplePagerTitleView;
                                }

                                @Override
                                public IPagerIndicator getIndicator(Context context) {
                                    LinePagerIndicator indicator = new LinePagerIndicator(context);
                                    indicator.setColors(Color.parseColor("#ff5252"));
                                    return indicator;
                                }
                            });
                            magicIndicator.setNavigator(commonNavigator);

                            LinearLayout titleContainer = commonNavigator.getTitleContainer(); // must after setNavigator
                            titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
                            titleContainer.setDividerDrawable(new ColorDrawable() {
                                @Override
                                public int getIntrinsicWidth() {
                                    return UIUtil.dip2px(mContext, 15);
                                }
                            });

                            ViewPagerHelper.bind(magicIndicator, viewPager);
                            newsSearchByColumn(0);
                        }

                    }catch (JSONException ex){
                        Log.i("MainPageFragment","json对象构造错误");
                    }
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
}
