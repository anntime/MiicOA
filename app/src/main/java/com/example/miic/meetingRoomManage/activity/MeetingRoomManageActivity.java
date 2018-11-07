package com.example.miic.meetingRoomManage.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miic.R;
import com.example.miic.base.http.PostRequest;
import com.example.miic.base.loadmore.LoadMoreListView;
import com.example.miic.base.loadmore.OnLoadMoreScrollListener;
import com.example.miic.common.MyApplication;
import com.example.miic.contractManage.activity.ContractApprovalActivity;
import com.example.miic.contractManage.activity.ContractManageActivity;
import com.example.miic.contractManage.adapter.ContractSearchResultItemAdapter;
import com.example.miic.contractManage.item.ContractSearchResultItem;
import com.example.miic.contractManage.item.ContractStatus;
import com.example.miic.meetingRoomManage.adapter.SearchResultAdapter;
import com.example.miic.meetingRoomManage.common.CalenderBaseActivity;
import com.example.miic.meetingRoomManage.item.ApplyListItem;
import com.example.miic.meetingRoomManage.item.SearchResultItem;
import com.example.miic.oa.news.item.InfoPageNewsDetailFileAcc;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.haibin.calendarview.WeekViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.miic.oa.common.LvHeightUtil.setListViewHeightBasedOnChildren;
import static com.example.miic.oa.common.Setting.stampToDate;
import static com.example.miic.oa.common.Setting.stampToTimes;
import static java.lang.Integer.parseInt;

public class MeetingRoomManageActivity extends CalenderBaseActivity implements
        CalendarView.OnCalendarSelectListener
{

    TextView mTextMonthDay;

    TextView mTextYear;

    TextView mTextLunar;

    CalendarView mCalendarView;

    RelativeLayout mRelativeTool;
    CalendarLayout mCalendarLayout;

    TextView mTodayTitle;

    private SearchResultAdapter searchResultItemAdapter;
    private List<SearchResultItem> searchResultList;
    private ListView listView;
    private TextView messageTip;

    private String SelectDate;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_meeting_room_manage;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initView(String[] arr) {
        setStatusBarDarkMode();
        mTextMonthDay = (TextView) findViewById(R.id.tv_month_day);
        mTodayTitle = (TextView) findViewById(R.id.today_title);
        mTextYear = (TextView) findViewById(R.id.tv_year);
        mTextLunar = (TextView) findViewById(R.id.tv_lunar);
        mRelativeTool = (RelativeLayout) findViewById(R.id.rl_tool);
        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        mCalendarLayout = (CalendarLayout)findViewById(R.id.calendarLayout);
        mTextMonthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MeetingRoomManageActivity.this, MeetingRoomCalenderActivity.class);
                intent.putExtra("sealApplyID", "1");
                startActivityForResult(intent, 1);
            }
        });
        findViewById(R.id.iv_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MeetingRoomManageActivity.this, MeetingRoomApplyActivity.class);
                intent.putExtra("sealApplyID", "1");
                startActivityForResult(intent, 1);

            }
        });
        //初始化列表
        listView = (ListView)findViewById(R.id.result_container);

        setListViewHeightBasedOnChildren(listView);
        messageTip = (TextView)findViewById(R.id.message_tip);

        mCalendarView.setWeekStarWithSun();
        mCalendarView.setOnCalendarSelectListener(this);
        mTextYear.setText(String.valueOf(mCalendarView.getCurYear()));
        mTextMonthDay.setText(mCalendarView.getCurMonth() + "-" + mCalendarView.getCurDay());
        mTodayTitle.setText(mCalendarView.getCurYear()+"-"+mCalendarView.getCurMonth() + "-" + mCalendarView.getCurDay()+"会议安排");
        mTextLunar.setText("今日");
        findViewById(R.id.search_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //页面跳转，不滚动，定位
        if(arr.length!=0){
            mCalendarView.scrollToCalendar(parseInt(arr[0]),parseInt(arr[1]),parseInt(arr[2]));
            Calendar can = mCalendarView.getSelectedCalendar();
            //获取选中日期的时间戳
            long timeInMillis = can.getTimeInMillis();
            //当前星期几
            int mWay = can.getWeek();
            long weekStart = timeInMillis - ((1000 * 60 * 60 * 24) * (mWay));//礼拜天
            long weekStop = timeInMillis + ((1000 * 60 * 60 * 24) * (7 - mWay-1));//礼拜六


            String  format = "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            String start = sdf.format(new Date(weekStart));
            String stop = sdf.format(new Date(weekStop));
            GetMeetingUsedInfosByDate(start,stop);
            int minYear = parseInt(start.split("-")[0]);
            int minMonth = parseInt(start.split("-")[1]);
            int minDay = parseInt(start.split("-")[2]);
            int maxYear = parseInt(stop.split("-")[0]);
            int maxMonth = parseInt(stop.split("-")[1]);
            int maxDay = parseInt(stop.split("-")[2]);
            mCalendarView.setRange(minYear,minMonth,minDay,maxYear,maxMonth,maxDay);

        }else{
            int minYear = mCalendarView.getCurrentWeekCalendars().get(0).getYear();
            int minMonth = mCalendarView.getCurrentWeekCalendars().get(0).getMonth();
            int minDay = mCalendarView.getCurrentWeekCalendars().get(0).getDay();
            int maxYear = mCalendarView.getCurrentWeekCalendars().get(mCalendarView.getCurrentWeekCalendars().size()-1).getYear();
            int maxMonth = mCalendarView.getCurrentWeekCalendars().get(mCalendarView.getCurrentWeekCalendars().size()-1).getMonth();
            int maxDay = mCalendarView.getCurrentWeekCalendars().get(mCalendarView.getCurrentWeekCalendars().size()-1).getDay();
            mCalendarView.setRange(minYear,minMonth,minDay,maxYear,maxMonth,maxDay);
            GetMeetingUsedInfosByDate(minYear+"-"+minMonth+"-"+minDay,maxYear+"-"+maxMonth+"-"+maxDay);
        }

    }

    @SuppressWarnings("unused")
    @Override
    //哪天有备注
    protected void initData() { }

    @SuppressWarnings("all")
    private Calendar getSchemeCalendar(int year, int month, int day) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(0xff00C2DE);//如果单独标记颜色、则会使用这个颜色
        return calendar;
    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {
        Toast.makeText(this, String.format("%s : OutOfRange", calendar), Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        Log.e("onDateSelected", "  -- " + calendar.getYear() + "  --  " + calendar.getMonth() + "  -- " + calendar.getDay());
        mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);
        mTextMonthDay.setText(calendar.getMonth() + "-" + calendar.getDay() );
        mTextYear.setText(String.valueOf(calendar.getYear()));
        String str = "日一二三四五六七八九";
        if(calendar.getDay()==mCalendarView.getCurDay()){
            mTextLunar.setText("今日");
        }else {
            mTextLunar.setText("周"+ str.charAt(calendar.getWeek()));
        }

        //这里写下面的会议列表
         SelectDate = calendar.getYear()+"-"+calendar.getMonth() + "-" + calendar.getDay();
        mTodayTitle.setText(SelectDate+"  会议安排");
        GetMeetingStatisticsDetailInfos( SelectDate);
    }

    //初次进入页面，通过选择的日期搜索会议室预约列表
    private void GetMeetingStatisticsDetailInfos(String selectDate){
        String str = "{date:'" + selectDate + "'}";
        RequestBody searchView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),str);
        Call<String> call = PostRequest.Instance.request.GetMeetingStatisticsDetailInfos(searchView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        Log.i("会议室预约列表：",jsonObject.toString());
                        String dataStr = jsonObject.getString("d");
                        if(!dataStr.equals("[]")){
                            JSONArray dataArr = new JSONArray(dataStr);
                            searchResultList = new ArrayList<>();


                            for (int index = 0;index<dataArr.length();index++){
                                JSONObject item = dataArr.getJSONObject(index);
                                JSONArray bookList = item.getJSONArray("BookList");
                                SearchResultItem searchResultItem =new SearchResultItem(item.getString("RoomName"),bookList.length()+"");

                                for (int j=0;j<bookList.length();j++){
                                    JSONObject tempItem = bookList.optJSONObject(j);
                                    JSONObject temp= new JSONObject();
                                    temp.put("ID",tempItem.getString("ID"));
                                    temp.put("startTime",stampToTimes(tempItem.getString("ReverseBeginTime")));
                                    temp.put("endTime",stampToTimes(tempItem.getString("ReverseEndTime")));
                                    temp.put("deptName",tempItem.getString("DeptName"));
                                    temp.put("title",tempItem.getString("MeetingTheme"));
                                    temp.put("userName",tempItem.getString("BookerName"));
                                    ApplyListItem applyListItem = new ApplyListItem(temp);
                                    searchResultItem.addItem(applyListItem);

                                }
                                searchResultList.add(searchResultItem);

                            }
                            searchResultItemAdapter = new SearchResultAdapter(MeetingRoomManageActivity.this,searchResultList);
                            listView.setAdapter(searchResultItemAdapter);
                            searchResultItemAdapter.notifyDataSetChanged();
                            listView.requestFocus();
                            listView.setVisibility(View.VISIBLE);
                            messageTip.setVisibility(View.GONE);
                            searchResultItemAdapter.setOnItemMyClickListener(new SearchResultAdapter.onItemMyClickListener() {
                                @Override
                                public void onMyClick(int position, String clickID) {
                                    DeleteMeetingRoomApply(clickID,position+"");
                                }
                            });

                        }else{
                            listView.setVisibility(View.GONE);
                            messageTip.setVisibility(View.VISIBLE);
                        }

                    }
                    catch (JSONException ex){
                        Log.i("getMyDeptInfoList","json对象构造错误");
                    }
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(MeetingRoomManageActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);

    }

    //获取一周预约情况
    private void GetMeetingUsedInfosByDate( String start,String stop){
        JSONObject search = new JSONObject();//{year:'2018',month:'10'}
        try{
            search.put("beginDate",start);
            search.put("endDate",stop);
        }catch (JSONException ex){
            Log.i("MeetingUsedInfosByDate",ex.getMessage());
        }
        RequestBody searchView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),search.toString());
        Call<String> call = PostRequest.Instance.request.GetMeetingUsedInfosByDate(searchView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        Log.i("会议室预约列表：",jsonObject.toString());
                        String dataStr = jsonObject.getString("d");
                        if(dataStr!="[]"){
                            JSONArray arr = new JSONArray(dataStr);
                            //画杠杠
                            Map<String, Calendar> map = new HashMap<>();
                            for (int index=0;index<arr.length();index++){
                                String date = stampToDate(arr.getJSONObject(index).getString("ReverseDate"));
                                int y = parseInt(date.split("-")[0]) ;
                                int m = parseInt(date.split("-")[1]) ;
                                int d = parseInt(date.split("-")[2]) ;
                                if(!map.containsKey(getSchemeCalendar(y, m, d).toString())){
                                    map.put(getSchemeCalendar(y, m, d).toString(),
                                            getSchemeCalendar(y, m, d));
                                }
                            }
                            mCalendarView.setSchemeDate(map);
                        }
                    }
                    catch (JSONException ex){
                        Log.i("getMyDeptInfoList","json对象构造错误");
                    }
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(MeetingRoomManageActivity.this,"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
    //删除会议室预约
    private void DeleteMeetingRoomApply(String clickID,final String position){
        String str = "{id:'" + clickID + "',fromManage:false}";
        RequestBody searchView = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),str);
        Call<String> call = PostRequest.Instance.request.DeleteMeetingRoomApply(searchView);
        Callback<String> callback = new Callback<String>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        Boolean result = jsonObject.getBoolean("d");
                        if(result==true){
                            Toast.makeText(MyApplication.getContext(), "成功取消会议室预约~", Toast.LENGTH_SHORT).show();
                            Calendar can = mCalendarView.getSelectedCalendar();
                            //获取选中日期的时间戳
                            long timeInMillis = can.getTimeInMillis();
                            //当前星期几
                            int mWay = can.getWeek();
                            long weekStart = timeInMillis - ((1000 * 60 * 60 * 24) * (mWay));//礼拜天
                            long weekStop = timeInMillis + ((1000 * 60 * 60 * 24) * (7 - mWay-1));//礼拜六


                            String  format = "yyyy-MM-dd";
                            SimpleDateFormat sdf = new SimpleDateFormat(format);
                            String start = sdf.format(new Date(weekStart));
                            String stop = sdf.format(new Date(weekStop));
                            GetMeetingUsedInfosByDate(start,stop);

                            GetMeetingStatisticsDetailInfos( SelectDate);

                            mCalendarView.update();


                        }else {
                            Toast.makeText(MyApplication.getContext(), "取消会议室预约失败~", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (JSONException ex){
                        Log.i("getMyDeptInfoList","json对象构造错误");
                    }
                }
            }
            //请求失败时回调
            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                System.out.println("请求失败" + call.request());
                System.out.println("错误是：" + throwable.getMessage() + "------" + throwable.getCause());
                Toast.makeText(MyApplication.getContext(),"网络错误，请稍后再试！",Toast.LENGTH_SHORT).show();
            }
        };
        PostRequest.Instance.CommonAsynPost(call, callback);
    }
}
