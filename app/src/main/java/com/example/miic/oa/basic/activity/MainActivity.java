package com.example.miic.oa.basic.activity;

import android.annotation.SuppressLint;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.miic.R;
import com.example.miic.oa.chat.fragment.ChatPageFragment;
import com.example.miic.oa.news.fragment.InfoPageFragment;
import com.example.miic.oa.main.fragment.MainPageFragment;
import com.example.miic.oa.user.fragment.MyPageFragment;
import com.example.miic.oa.work.fragment.WorkPageFragment;

import static com.example.miic.common.MyApplication.mIMKit;

public class MainActivity extends AppCompatActivity
{
    /**
     * 中间显示的5个fragment
     */
    private MainPageFragment mainPageFragment;
    private InfoPageFragment infoPageFragment;
    private WorkPageFragment workPageFragment;
    private ChatPageFragment chatPageFragment;
    private MyPageFragment myPageFragment;
    Fragment chatFragment;
    /**
     * 底部5个按钮
     */
    private LinearLayout tabBtnMain;
    private LinearLayout tabBtnInfo;
    private LinearLayout tabBtnWork;
    private LinearLayout tabBtnChat;
    private LinearLayout tabBtnMy;
    /**
     * 用于对Fragment进行管理
     */
    private FragmentManager fragmentManager;


    private long time = -2;
    public int count = 0;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        fragmentManager = getSupportFragmentManager();
        setTabSelection(0);
        if(getIntent()!=null){
            //获取上一页面传递的参数
            Intent intent = getIntent();
            String param = intent.getStringExtra("param");
            if(param!=null&&param.equals("chat")){
                setTabSelection(2);
            }else {
                setTabSelection(0);
            }
        }

        Log.i("Main2Activity-onCreate", "onCreate is invoke!!!");
        if (savedInstanceState != null) {

            setTabSelection(2);
        }

    }
    private void initViews()
    {

        tabBtnMain = (LinearLayout) findViewById(R.id.id_tab_bottom_main);
        tabBtnInfo = (LinearLayout) findViewById(R.id.id_tab_bottom_info);
        tabBtnWork = (LinearLayout) findViewById(R.id.id_tab_bottom_work);
        tabBtnChat = (LinearLayout) findViewById(R.id.id_tab_bottom_chat);
        tabBtnMy = (LinearLayout) findViewById(R.id.id_tab_bottom_my);

        tabBtnMain.setOnClickListener(bottomClickListener);
        tabBtnInfo.setOnClickListener(bottomClickListener);
        tabBtnWork.setOnClickListener(bottomClickListener);
        tabBtnChat.setOnClickListener(bottomClickListener);
        tabBtnMy.setOnClickListener(bottomClickListener);


    }
    private View.OnClickListener bottomClickListener =new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i("Main2Activity",view.toString());
            switch (view.getId())
            {
                case R.id.id_tab_bottom_main:
                    setTabSelection(0);
                    break;
                case R.id.id_tab_bottom_info:
                    setTabSelection(1);
                    break;
                case R.id.id_tab_bottom_work:
                    setTabSelection(3);
                    break;
                case R.id.id_tab_bottom_chat:
                    setTabSelection(2);
                    break;
                case R.id.id_tab_bottom_my:
                    setTabSelection(4);
                    break;
                default:
                    break;
            }
        }
    };



    /**
     * 根据传入的index参数来设置选中的tab页。
     *
     */
    @SuppressLint("NewApi")
    public void setTabSelection(int index)
    {
        // 重置按钮
        resetBtn();
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index)
        {
            case 0:
                // 当点击了消息tab时，改变控件的图片和文字颜色
                ((ImageView) tabBtnMain.findViewById(R.id.btn_tab_bottom_main))
                        .setImageResource(R.drawable.home_on);
                ((TextView) tabBtnMain.findViewById(R.id.btn_bottom_main))
                        .setTextColor(getResources().getColor(R.color.colorAccent));
                if (mainPageFragment == null)
                {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    mainPageFragment = new MainPageFragment();
                    transaction.add(R.id.id_frame_layout_content, mainPageFragment);
                } else
                {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(mainPageFragment);
                }
                break;
            case 1:
                // 当点击了消息tab时，改变控件的图片和文字颜色
                ((ImageView) tabBtnInfo.findViewById(R.id.btn_tab_bottom_info))
                        .setImageResource(R.drawable.attibutes_on);
                ((TextView) tabBtnInfo.findViewById(R.id.btn_bottom_info))
                        .setTextColor(getResources().getColor(R.color.colorAccent));
                if (infoPageFragment == null)
                {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    infoPageFragment = new InfoPageFragment();
                    transaction.add(R.id.id_frame_layout_content, infoPageFragment);
                } else
                {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(infoPageFragment);

                }
                break;
            case 3:
                // 当点击了动态tab时，改变控件的图片和文字颜色
                ((ImageView) tabBtnWork.findViewById(R.id.btn_tab_bottom_work))
                        .setImageResource(R.drawable.pen_on);
                ((TextView) tabBtnWork.findViewById(R.id.btn_bottom_work))
                        .setTextColor(getResources().getColor(R.color.colorAccent));
                if (workPageFragment == null)
                {
                    // 如果NewsFragment为空，则创建一个并添加到界面上
                    workPageFragment = new WorkPageFragment();
                    transaction.add(R.id.id_frame_layout_content, workPageFragment);
                } else
                {
                    // 如果NewsFragment不为空，则直接将它显示出来
                    transaction.show(workPageFragment);
                }
                break;
            case 2:
                // 当点击了设置tab时，改变控件的图片和文字颜色
                ((ImageView) tabBtnChat.findViewById(R.id.btn_tab_bottom_chat))
                        .setImageResource(R.drawable.chat_on);
                ((TextView) tabBtnChat.findViewById(R.id.btn_bottom_chat))
                        .setTextColor(getResources().getColor(R.color.colorAccent));
                if (chatPageFragment == null)
                {
                    // 如果SettingFragment为空，则创建一个并添加到界面上
                    chatFragment = mIMKit.getConversationFragment();
                    chatPageFragment = new ChatPageFragment();
                    transaction.add(R.id.id_frame_layout_content, chatPageFragment);
                } else
                {
                    // 如果SettingFragment不为空，则直接将它显示出来
                    transaction.show(chatPageFragment);
                }
                break;
            case 4:
                // 当点击了设置tab时，改变控件的图片和文字颜色
                ((ImageView) tabBtnMy.findViewById(R.id.btn_tab_bottom_my))
                        .setImageResource(R.drawable.user_on);
                ((TextView) tabBtnMy.findViewById(R.id.btn_bottom_my))
                        .setTextColor(getResources().getColor(R.color.colorAccent));
                if (myPageFragment == null)
                {
                    // 如果SettingFragment为空，则创建一个并添加到界面上
                    myPageFragment = new MyPageFragment();
                    transaction.add(R.id.id_frame_layout_content, myPageFragment);
                } else
                {
                    // 如果SettingFragment不为空，则直接将它显示出来
                    transaction.show(myPageFragment);
                }
                break;
        }
        transaction.commit();
    }

    /**
     * 清除掉所有的选中状态。
     */
    private void resetBtn()
    {
        ((ImageView) tabBtnMain.findViewById(R.id.btn_tab_bottom_main))
                .setImageResource(R.drawable.home);
        ((TextView) tabBtnMain.findViewById(R.id.btn_bottom_main))
                .setTextColor(getResources().getColor(R.color.news_detail_title));

        ((ImageView) tabBtnInfo.findViewById(R.id.btn_tab_bottom_info))
                .setImageResource(R.drawable.attibutes);
        ((TextView) tabBtnInfo.findViewById(R.id.btn_bottom_info))
                .setTextColor(getResources().getColor(R.color.news_detail_title));

        ((ImageView) tabBtnWork.findViewById(R.id.btn_tab_bottom_work))
                .setImageResource(R.drawable.pen);
        ((TextView) tabBtnWork.findViewById(R.id.btn_bottom_work))
                .setTextColor(getResources().getColor(R.color.news_detail_title));

        ((ImageView) tabBtnChat.findViewById(R.id.btn_tab_bottom_chat))
                .setImageResource(R.drawable.chat);
        ((TextView) tabBtnChat.findViewById(R.id.btn_bottom_chat))
                .setTextColor(getResources().getColor(R.color.news_detail_title));

        ((ImageView) tabBtnMy.findViewById(R.id.btn_tab_bottom_my))
                .setImageResource(R.drawable.user);
        ((TextView) tabBtnMy.findViewById(R.id.btn_bottom_my))
                .setTextColor(getResources().getColor(R.color.news_detail_title));
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction
     *            用于对Fragment执行操作的事务
     */
    @SuppressLint("NewApi")
    public void hideFragments(FragmentTransaction transaction)
    {
        if (mainPageFragment != null)
        {
            transaction.hide(mainPageFragment);
        }
        if (infoPageFragment != null)
        {
            transaction.hide(infoPageFragment);
        }
        if (workPageFragment != null)
        {
            transaction.hide(workPageFragment);
        }
        if (chatPageFragment != null)
        {
            transaction.hide(chatPageFragment);
        }
        if (myPageFragment != null)
        {
            transaction.hide(myPageFragment);
        }

    }

    protected void onResume(){
        super.onResume();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        Log.i("Main2Activity-onResume","回复主程序！！！");
    }
    /**
     * Activity从后台重新回到前台时被调用
     */
    protected void onRestart(){
        super.onRestart();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        Log.i("Main2Activity-onRestart","重新启动主程序！！！");
    }
    /**
     *Activity创建或者从后台重新回到前台时被调用
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.i("Main2Activity-onStart","onStart is invoke!!!");
    }
    /**
     *  Activity被覆盖到下面或者锁屏时被调用
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Main2Activity-onPause","onPause is invoke!!!");
    }

    /**
     *退出当前Activity或者跳转到新Activity时被调用
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Main2Activity-onStop","onStop is invoke!!!");
    }

    /**
     *退出当前Activity时被调用,调用之后Activity就结束了
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Main2Activity-onDestroy","onDestroy is invoke!!!");
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.clear();
    }
}
