package com.example.miic.oa.work.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.miic.R;
import com.example.miic.base.loadmore.LoadMoreListView;
import com.example.miic.oa.search.activity.SearchActivity;
import com.example.miic.oa.work.adapter.WorkPageChildNewsAdapter;
import com.example.miic.oa.work.item.WorkPageChildNews;

import java.util.ArrayList;
import java.util.List;

public class WorkChildActivity extends AppCompatActivity {
    private LinearLayout searchBtn;
    private LinearLayout BackBtn;
    private String clickIconID;
    private TextView iconName;
    private List<WorkPageChildNews> workChildNewsList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LoadMoreListView workNewsListView;
    private WorkPageChildNewsAdapter workChildNewsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_child);
        //搜索按钮绑定搜索事件
        searchBtn = (LinearLayout)findViewById(R.id.search_button);
        searchBtn.setOnClickListener(searchClickListener);
        BackBtn = (LinearLayout)findViewById(R.id.search_back);
        BackBtn.setOnClickListener(backClickListener);
        //获取上一页面传递的参数
        Intent intent = getIntent();
        clickIconID = intent.getStringExtra("clickIconID");
        //根据iconID请求后台获取数据
        //init
        iconName = (TextView)findViewById(R.id.work_icon_name);
        iconName.setText(clickIconID);
        workChildNewsList=new ArrayList<>();
        initWorkChildNewsList();
    }

    private void initWorkChildNewsList(){
        workChildNewsList.add(new WorkPageChildNews("1", "测试1","2018-7-8","10", "11", "22"));
        workChildNewsList.add(new WorkPageChildNews("2", "测试2","2018-7-8","10", "11", "22"));
        workChildNewsList.add(new WorkPageChildNews("3", "测试3","2018-7-8","10", "11", "22"));
        workChildNewsList.add(new WorkPageChildNews("4", "测试4","2018-7-8","10", "11", "22"));
        workChildNewsList.add(new WorkPageChildNews("5", "测试5","2018-7-8","10", "11", "22"));
        workChildNewsList.add(new WorkPageChildNews("6", "测试6","2018-7-8","10", "11", "22"));
        workChildNewsList.add(new WorkPageChildNews("7", "测试7","2018-7-8","10", "11", "22"));
        workChildNewsList.add(new WorkPageChildNews("8", "测试8","2018-7-8","10", "11", "22"));
        List<WorkPageChildNews> workChildNewsLists=new ArrayList<>();
        workChildNewsLists.addAll(workChildNewsList);
        //新闻列表填充数据
        workChildNewsAdapter =new WorkPageChildNewsAdapter(WorkChildActivity.this,workChildNewsLists);
        workNewsListView = (LoadMoreListView) findViewById(R.id.work_child_news_container);
        workNewsListView.setAdapter(workChildNewsAdapter);
    }

    //查询按钮绑定事件
    private View.OnClickListener searchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent(WorkChildActivity.this,SearchActivity.class);
            startActivity(intent);
        }
    };
    //返回按钮绑定事件
    private View.OnClickListener backClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };
}
