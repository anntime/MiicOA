package com.example.miic.oa.news.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.base.http.PostRequest;
import com.example.miic.base.progressbar.DownLoadProgressBar;
import com.example.miic.oa.common.OpenFileUtil;
import com.example.miic.oa.common.Setting;
import com.example.miic.oa.common.fileDownload.FileUtils;
import com.example.miic.oa.news.adapter.InfoPageNewsDetailFileAccAdapter;
import com.example.miic.oa.news.adapter.InfoPageNewsDetailGifImageAdapter;
import com.example.miic.oa.news.item.InfoPageNewsDetailFileAcc;
import com.example.miic.oa.news.item.InfoPageNewsDetailGifImage;
import com.example.miic.oa.news.item.InfoPageNewsDetailImageAcc;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import net.cpacm.library.SimpleSliderLayout;
import net.cpacm.library.indicator.ViewpagerIndicator.CirclePageIndicator;
import net.cpacm.library.slider.ImageSliderView;
import net.cpacm.library.transformers.DefaultTransformer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.miic.oa.common.LvHeightUtil.setListViewHeightBasedOnChildren;
import static com.example.miic.oa.common.Setting.requestPermission;
import static com.example.miic.oa.common.Setting.stampToDate;

public class NewsDetailActivity extends AppCompatActivity {

    private LinearLayout backBtn;
    private TextView NewsDetailTitle;
    private TextView NewsDetailSource;
    private TextView NewsDetailTime;
    private TextView NewsDetailReadCount;
    private TextView NewsDetailContent;
    private ListView NewsDetailListView;
    private LinearLayout NewsDetailAccList;
    private TextView NewsDetailCommentCount;
    private TextView NewsDetailHeartCount;
    private LinearLayout NewsDetailCanComment;
    private ImageView NewsDetailGiveComment;
    private ImageView NewsDetailGiveHeart;
    private EditText NewsComment;
    private TextView NewsTips;

    private SimpleSliderLayout simpleSliderLayout;
    private CirclePageIndicator circlePageIndicator;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private String[] imageUrls;
    private RelativeLayout NewsDetailCarousel;


    private ListView GifImageListView;
    private List<InfoPageNewsDetailGifImage> gifImageUrls;

    private boolean isDownload;


    private String ClickID;
    private Boolean isPraise;
    private List<InfoPageNewsDetailFileAcc> fileAccList;
    private List<InfoPageNewsDetailImageAcc> imageAccList;
    private InfoPageNewsDetailFileAccAdapter newsDetailFileAccAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        //界面初始化
        NewsDetailTitle = (TextView)findViewById(R.id.news_detail_title);
        NewsDetailSource = (TextView)findViewById(R.id.news_detail_source);
        NewsDetailTime = (TextView)findViewById(R.id.news_detail_time);
        NewsDetailReadCount = (TextView)findViewById(R.id.news_detail_read_count);
        NewsDetailContent = (TextView)findViewById(R.id.news_detail_content);
        NewsDetailListView = (ListView)findViewById(R.id.news_detail_list_view);
        NewsDetailCommentCount = (TextView)findViewById(R.id.news_detail_comment_count);
        NewsDetailHeartCount = (TextView)findViewById(R.id.news_detail_heart_count);
        NewsDetailCanComment = (LinearLayout)findViewById(R.id.news_detail_can_comment);
        NewsDetailGiveComment = (ImageView)findViewById(R.id.give_comment);
        NewsDetailGiveHeart = (ImageView)findViewById(R.id.give_heart);
        NewsDetailCarousel = (RelativeLayout)findViewById(R.id.news_detail_carousel);
        NewsDetailAccList = (LinearLayout)findViewById(R.id.news_detail_acc_list);
        NewsTips = (TextView)findViewById(R.id.news_tips);

        GifImageListView = (ListView) findViewById(R.id.gif_image_list_view);

        NewsDetailContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        NewsComment = (EditText)findViewById(R.id.news_detail_input_comment);
        Drawable drawable=getResources().getDrawable(R.drawable.pencil);
        drawable.setBounds(0,0,30,30);
        NewsComment.setCompoundDrawables(drawable,null,null,null);

        //返回按钮绑定
        backBtn = (LinearLayout)findViewById(R.id.detail_back);
        backBtn.setOnClickListener(backClickListener);
        //获取上一页面传递的参数
        Intent intent = getIntent();
        String clickNewsID = intent.getStringExtra("clickNewsID");
        ClickID = clickNewsID;

        //进行http请求，获取新闻内容
        getDetailInfomation(clickNewsID);
        NewsDetailGiveComment.setOnClickListener(getCommentListListener);
        NewsDetailGiveHeart.setOnClickListener(giveHeartlistener);

        //评论事件
        NewsComment.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == keyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    //隐藏键盘
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(NewsDetailActivity.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    // 提交评论
                    String content = NewsComment.getText().toString();
                    commentNews(content);
                }
                return false;
            }
        });


    }

    //初始化gif——image的listView
    private void initNewsDetailGifImage(){
        InfoPageNewsDetailGifImageAdapter newsCommentAdapter =new InfoPageNewsDetailGifImageAdapter(NewsDetailActivity.this,gifImageUrls);
        GifImageListView.setAdapter(newsCommentAdapter);
        setListViewHeightBasedOnChildren(GifImageListView);
    }

    private void initNewsDetailCarousel(){
        imageLoader.init(ImageLoaderConfiguration.createDefault(NewsDetailActivity.this));
        simpleSliderLayout = (SimpleSliderLayout) findViewById(R.id.simple_slider);
        for (int i = 0; i < imageUrls.length; i++) {
            ImageSliderView sliderView = new ImageSliderView(NewsDetailActivity.this);
            sliderView.empty(R.drawable.image_empty);
            imageLoader.displayImage(imageUrls[i], sliderView.getImageView());
            sliderView.bundle(new Bundle());
            simpleSliderLayout.addSlider(sliderView);
        }
        simpleSliderLayout.setCycling(false);//无限循环
        simpleSliderLayout.setAutoCycling(false);//自动循环
        //simpleSliderLayout.setSliderDuration(5000);//每隔5秒进行翻页
        simpleSliderLayout.setSliderTransformDuration(1000);//翻页的速度为1秒
        simpleSliderLayout.setPageTransformer(new DefaultTransformer());//翻页动画
        simpleSliderLayout.setAnimationListener(null);//为slider设置特定的动画
        circlePageIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        simpleSliderLayout.setViewPagerIndicator(circlePageIndicator);//为viewpager设置指示器
    }
    //获取评论列表
    private View.OnClickListener getCommentListListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i("NewsDetailActivity","评论列表");
            //获得点击的新闻的id，然后把这个id传到新的activity中。
            Intent intent=new Intent(NewsDetailActivity.this,NewsCommentsActivity.class);
            intent.putExtra("NewsID",ClickID);
            startActivityForResult(intent,1);
        }
    };
    //返回这个活动时，刷新页面
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                break;
            default:
                updateCommentAndPraise();
                break;
        }
    }
    //浏览新闻
    public void browseNews(){
        JSONObject requestJson = new JSONObject();
        try{
            requestJson.put("newsID",ClickID);
        }catch (JSONException ex){
            Log.i("NewsDetailActivity","json对象构造错误");
        }
        RequestBody newsInfoView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.BrowseNews (newsInfoView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println("body:" + response.body());
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(NewsDetailActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //更新评论数量与点赞数量
    public void updateCommentAndPraise(){
        JSONObject requestJson = new JSONObject();
        try{
            requestJson.put("id",ClickID);
        }catch (JSONException ex){
            Log.i("NewsDetailActivity","json对象构造错误");
        }
        RequestBody newsInfoView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.GetDetailInformation (newsInfoView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println("body:" + response.body());
                //String res = response.body().replaceAll("//","<TAG>");
                if (response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(new JSONObject(response.body()).getString("d"));
                        JSONObject newsInfo = jsonObject.getJSONObject("NewsInfo");
                        if (newsInfo != null) {
                            /**
                             * 新闻类型有四种：
                             * 1，文件，应该可以点击一个连接或者按钮进行下载
                             * 2，是个连接，这个用题目显示是个连接，点击使用手机自带的浏览器打开
                             * 3，图片，直接展示，最好是可以左右滑动的，如果不好做就做成图片罗列展示是个listView就可以吧。
                             * 4，文字，最简单的
                             */
                            Setting setting = new Setting();
                            String publishType = newsInfo.getString("PublishType");
                            //JSONArray accList = jsonObject.getJSONArray("AccList");
                            JSONArray accList = jsonObject.isNull("AccList") ? null : jsonObject.getJSONArray("AccList");
                            System.out.println(accList);
                            NewsDetailTitle.setText(newsInfo.getString("Title"));
                            NewsDetailSource.setText(newsInfo.getString("PublishDeptName"));
                            NewsDetailTime.setText(stampToDate(newsInfo.getString("PublishTime")));
                            NewsDetailReadCount.setText("阅读量 " + newsInfo.getString("TotalBrowseCount"));
                            String canComment = newsInfo.getString("CanComment");
                            //判断是否可以评论
                            if (canComment.equals("1")) {
                                //可以评论
                                NewsDetailCommentCount.setText(newsInfo.getString("CommentCount"));
                                NewsDetailHeartCount.setText(newsInfo.getString("PraiseCount"));
                            } else {
                                NewsDetailCanComment.setVisibility(View.GONE);
                            }
                            isPraise = jsonObject.getBoolean("IsPraise");
                            if (isPraise == true) {
                                NewsDetailGiveHeart.setImageResource(R.drawable.red_heart);
                            } else {
                                NewsDetailGiveHeart.setImageResource(R.drawable.heart);
                            }
                        }
                    } catch (JSONException ex) {
                        Log.i("NewsDetailActivity","json对象构造错误");
                    }
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(NewsDetailActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //点赞
    private View.OnClickListener giveHeartlistener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //点赞操作
            Log.i("NewsDetailActivity","点赞操作");
            praiseNews();
        }
    };
    //新闻评论
    public void commentNews(String content){
        JSONObject requestJson = new JSONObject();
        try{
            JSONObject commentView  = new JSONObject();
            commentView.put("NewsID",ClickID);
            commentView.put("Content",content);
            requestJson.put("commentView",commentView);
        }catch (JSONException ex){
            Log.i("NewsDetailActivity","json对象构造错误");
        }
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.Comment (requestBody);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println("body:" + response.body());
                if(response.body()!=null){
                    try{
                        Boolean jsonStr =new JSONObject(response.body()).getBoolean("d");
                        if(jsonStr==true){
                            //评论成功
                            Log.i("NewsDetailActivity","评论成功");
                            Toast.makeText(NewsDetailActivity.this,"评论成功！",Toast.LENGTH_SHORT).show();
                            int count = Integer.parseInt(NewsDetailCommentCount.getText().toString());
                            count++;
                            NewsDetailCommentCount.setText(""+count);
                            NewsComment.setText("");
                        }else{
                            //评论失败
                            Log.i("NewsDetailActivity","评论失败");
                            Toast.makeText(NewsDetailActivity.this,"评论失败！请稍后再试。",Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException ex){
                        Log.i("NewsDetailActivity","json对象构造错误");
                    }
                }else{
                    Toast.makeText(NewsDetailActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(NewsDetailActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }

    //新闻点赞
    private void praiseNews(){
        JSONObject requestJson = new JSONObject();
        try{
            requestJson.put("newsID",ClickID);
        }catch (JSONException ex){
            Log.i("NewsDetailActivity","json对象构造错误");
        }
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.Praise (requestBody);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println("body:" + response.body());
                if(response.body()!=null){
                    try{
                        //将字符串转换成jsonObject对象
                        JSONObject result = new JSONObject(response.body()).getJSONObject("d");
                        //获取对应的值
                        boolean praiseResult = result.getBoolean("result");
                        if(praiseResult==true){
                            //点赞成功
                            Log.i("NewsDetailActivity","点赞成功");
                            //换图片
                            isPraise = !isPraise;
                            int count = Integer.parseInt(NewsDetailHeartCount.getText().toString());
                            if (isPraise==true){
                                NewsDetailGiveHeart.setImageResource(R.drawable.red_heart);
                                count++;
                                NewsDetailHeartCount.setText(""+count);
                            }else{
                                NewsDetailGiveHeart.setImageResource(R.drawable.heart);
                                count--;
                                NewsDetailHeartCount.setText(""+count);
                            }
                        }else{

                            //点赞失败
                            Log.i("NewsDetailActivity","点赞失败");
                        }
                    }catch (JSONException ex){
                        Log.i("NewsDetailActivity","json对象构造错误");
                    }
                }else{
                    Toast.makeText(NewsDetailActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(NewsDetailActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }

    //进行http请求，获取新闻内容
    public void getDetailInfomation( final String clickNewsID){
        //浏览新闻
        browseNews();
        JSONObject requestJson = new JSONObject();
        try{
            requestJson.put("id",clickNewsID);
        }catch (JSONException ex){
            Log.i("NewsDetailActivity","json对象构造错误");
        }
        RequestBody newsInfoView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestJson.toString());
        Log.i("请求信息格式",requestJson.toString());
        Call<String> call = PostRequest.Instance.request.GetDetailInformation (newsInfoView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println("body:" + response.body());
                //String res = response.body().replaceAll("//","<TAG>");
                if (response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(new JSONObject(response.body()).getString("d"));
                        JSONObject newsInfo = jsonObject.getJSONObject("NewsInfo");
                        if (newsInfo != null) {
                            /**
                             * 新闻类型有四种：
                             * 1，文件，应该可以点击一个连接或者按钮进行下载
                             * 2，是个连接，这个用题目显示是个连接，点击使用手机自带的浏览器打开
                             * 3，图片，直接展示，最好是可以左右滑动的，如果不好做就做成图片罗列展示是个listView就可以吧。
                             * 4，文字，最简单的
                             */
                            Setting setting = new Setting();
                            String publishType = newsInfo.getString("PublishType");
                            //JSONArray accList = jsonObject.getJSONArray("AccList");
                            JSONArray accList = jsonObject.isNull("AccList") ?null: jsonObject.getJSONArray("AccList");
                            System.out.println(accList);
                            NewsDetailTitle.setText(newsInfo.getString("Title"));
                            NewsDetailSource.setText(newsInfo.getString("PublishDeptName"));
                            NewsDetailTime.setText(stampToDate(newsInfo.getString("PublishTime")));
                            NewsDetailReadCount.setText("阅读量 " + newsInfo.getString("TotalBrowseCount"));
                            String canComment = newsInfo.getString("CanComment");
                            //判断是否可以评论
                            if (canComment.equals("1")) {
                                NewsDetailCommentCount.setText(newsInfo.getString("CommentCount"));
                                NewsDetailHeartCount.setText(newsInfo.getString("PraiseCount"));
                            } else {
                                NewsDetailCanComment.setVisibility(View.GONE);
                            }
                            isPraise = jsonObject.getBoolean("IsPraise");
                            if (isPraise == true) {
                                NewsDetailGiveHeart.setImageResource(R.drawable.red_heart);
                            } else {
                                NewsDetailGiveHeart.setImageResource(R.drawable.heart);
                            }
                            if (publishType.equals("0")) {
                                //文章
                                //渲染新闻详情页--最简单的，直接展示第四种新闻类型---文章
                                if (newsInfo.getString("Content") != null) {
                                    NewsDetailContent.setVisibility(View.VISIBLE);
                                    String con =newsInfo.getString("Content");
//                                    con = con.replace("\r\n","<br />");
//                                    con = con.replace(" "," ");
                                    con = con.replaceAll("<STYLE>[\\s\\S]*</STYLE>","");
                                    SpannableStringBuilder span = new SpannableStringBuilder(con);

                                    NewsDetailContent.setText(Html.fromHtml(span.toString()).toString());
                                    Log.i("新闻内容：",span.toString());
                                } else {
                                    NewsDetailContent.setVisibility(View.GONE);
                                }
                                //有附件
                                if (accList != null) {
                                    //只能是文件，显示link_TextView
                                    NewsDetailAccList.setVisibility(View.VISIBLE);
                                    fileAccList = new ArrayList<>();
                                    for (int i = 0; i < accList.length(); i++) {
                                        String title = accList.getJSONObject(i).getString("FileName");
                                        String link = setting.getService() + accList.getJSONObject(i).getString("FilePath");
                                        String fileType = accList.getJSONObject(i).getString("FileType");
                                        fileAccList.add(new InfoPageNewsDetailFileAcc(title, link, fileType, 0));

                                    }
                                    //附件
                                    newsDetailFileAccAdapter = new InfoPageNewsDetailFileAccAdapter(NewsDetailActivity.this, fileAccList);
                                    NewsDetailListView.setAdapter(newsDetailFileAccAdapter);
                                    setListViewHeightBasedOnChildren(NewsDetailListView);
                                    NewsDetailListView.setOnItemClickListener(listItemClickListener);
                                    newsDetailFileAccAdapter.notifyDataSetChanged();
                                }
                                if(newsInfo.getString("Content") == null&&accList == null){
                                    NewsTips.setVisibility(View.VISIBLE);
                                }

                            } else if (publishType.equals("2")) {
                                //图片,读取附件
                                if (newsInfo.getString("Content") != null) {
                                    NewsDetailContent.setVisibility(View.VISIBLE);
                                    NewsDetailContent.setText(Html.fromHtml(newsInfo.getString("Content")));
                                } else {
                                    NewsDetailContent.setVisibility(View.GONE);
                                }
                                if (accList != null) {
                                    ArrayList<String> images = new ArrayList<String>();
                                    gifImageUrls = new ArrayList<InfoPageNewsDetailGifImage>();
                                    for (int i = 0; i < accList.length(); i++) {
                                        String title = accList.getJSONObject(i).getString("FileName");
                                        String link = setting.getService() + accList.getJSONObject(i).getString("FilePath");

                                        Pattern p=Pattern.compile("\\.gif");//找的
                                        Matcher m=p.matcher(link);//主字符串
                                        if(m.find()){
                                            GifImageListView.setVisibility(View.VISIBLE);
                                            //包括gif
                                            gifImageUrls.add(new InfoPageNewsDetailGifImage(title,link));
                                        }else{
                                            NewsDetailCarousel.setVisibility(View.VISIBLE);
                                            //不包括gif
                                            images.add(link);
                                        }
                                    }
                                    imageUrls = (String[]) images.toArray(new String[0]);
                                    initNewsDetailCarousel();
                                    initNewsDetailGifImage();

                                    //initGifImage(Environment.getExternalStorageDirectory().toString() + "/OA_file/"+gifs.get(0));
                                }
                                if(newsInfo.getString("Content") == null&&accList == null){
                                    NewsTips.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                    } catch (JSONException ex) {
                        Log.i("NewsDetailActivity", "json对象构造错误");
                    }
                } else {
                    Toast.makeText(NewsDetailActivity.this, "网络错误，请稍后再试！", Toast.LENGTH_SHORT).show();
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(NewsDetailActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //文件点击事件
    private AdapterView.OnItemClickListener listItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            if(position<fileAccList.size()){
                //通过view获取其内部的组件，进而进行操作
                InfoPageNewsDetailFileAcc fileAcc = fileAccList.get(position);
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
    /**
     * 从服务器下载文件
     * @param path 下载文件的地址
     * @param FileName 文件名字
     */
    public void downLoad(final String path, final String FileName,final int listPosition) {
        Log.i("下载函数：：","下载？");
        //更新一下列表中表示下载的boolean变量，表示该条数据正在下载
        newsDetailFileAccAdapter.getItem(listPosition).setIsdownload(true);
        if(listPosition >= NewsDetailListView.getFirstVisiblePosition() &&
                listPosition <= NewsDetailListView.getLastVisiblePosition()) {
            int positionInListView = listPosition - NewsDetailListView.getFirstVisiblePosition();
            final DownLoadProgressBar item  = (DownLoadProgressBar) NewsDetailListView.getChildAt(positionInListView)
                    .findViewById(R.id.pb_mp);
            item.setVisibility(View.VISIBLE);
            requestPermission(NewsDetailActivity.this);
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
            Toast.makeText(NewsDetailActivity.this,"文件下载至："+Environment.getExternalStorageDirectory().toString() + "/OA_file",Toast.LENGTH_SHORT).show();
        }
    }
    public void publishProgress(final int progress,  final int positionInAdapter) {
        newsDetailFileAccAdapter.getItem(positionInAdapter).setProgress(progress);

        if(positionInAdapter >= NewsDetailListView.getFirstVisiblePosition() &&
                positionInAdapter <= NewsDetailListView.getLastVisiblePosition()) {
            int positionInListView = positionInAdapter - NewsDetailListView.getFirstVisiblePosition();
            DownLoadProgressBar item = (DownLoadProgressBar) NewsDetailListView.getChildAt(positionInListView)
                    .findViewById(R.id.pb_mp);

            item.setProgress(progress);
        }
    }

    //返回按钮绑定事件
    private View.OnClickListener backClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };
    @Override
    public void onBackPressed() {
        this.finish();
    }


}
