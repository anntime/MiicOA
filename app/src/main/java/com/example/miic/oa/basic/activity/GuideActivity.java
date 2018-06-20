package com.example.miic.oa.basic.activity;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.miic.R;
import com.example.miic.oa.login.activity.LoginActivity;

import java.util.ArrayList;
import java.util.List;


public class GuideActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private List<View> views=new ArrayList<>();//准备数据源
    //在ViewPager的最后一个页面设置一个按钮，用于点击跳转到MainActivity
    private Button bt_home;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initView();//初始化view
    }
    private void initView(){
        viewPager=(ViewPager) findViewById(R.id.view_pager);
        //将每个xml文件转化为View
        LayoutInflater inflater=LayoutInflater.from(this);
        //每个xml中就放置一个imageView
        View guideOne=inflater.inflate(R.layout.guidance01,null);
        View guideTwo=inflater.inflate(R.layout.guidance02,null);
        View guideThree=inflater.inflate(R.layout.guidance03,null);
        //将view加入到list中
        views.add(guideOne);
        views.add(guideTwo);
        views.add(guideThree);
        pagerAdapter=new PagerAdapter() {
            public Object instantiateItem(ViewGroup container,int position){
                //初始化适配器，将view加到container中
                View view=views.get(position);
                container.addView(view);
                return view;
            }
            public void destroyItem(ViewGroup container,int position,Object object){
                View view=views.get(position);
                //将view从container中移除
                container.removeView(view);
            }
            @Override
            public int getCount() {
                return views.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                //判断当前的view是我们需要的对象
                return view==object;
            }
        };
        viewPager.setAdapter(pagerAdapter);
        bt_home=(Button)guideThree.findViewById(R.id.to_Login);
        bt_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(GuideActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });




    }
}
