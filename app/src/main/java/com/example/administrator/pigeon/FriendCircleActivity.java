package com.example.administrator.pigeon;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import adapter.FriendCircleAdapter;
import bean.FriendsCircle;
import config.Config;
import io.rong.imageloader.core.ImageLoader;
import model.FriendCircleModel;
import model.UserModel;
import myapp.MyApp;
import util.NetUtil;
import view.CustomListView;

public class FriendCircleActivity extends AppCompatActivity{

    @ViewInject(R.id.customListView)
    CustomListView customListView;

    private View header;
    private FriendCircleAdapter mAdapter;
    private ImageView microIcon;
    private TextView text_name;

    private FriendCircleModel friendCircleModel;
    private List<FriendsCircle> mDynamicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendcircle);
        init();
    }

    private void init() {
        ViewUtils.inject(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("朋友圈");
        actionBar.setDisplayHomeAsUpEnabled(true);
        mDynamicList = MyApp.INSTANCE().getmDynamicList();
        friendCircleModel = new FriendCircleModel();
        mAdapter = new FriendCircleAdapter(this, R.layout.item_friendcircle_listother, mDynamicList);
        friendCircleModel.getDynamicItem(this);
        header= LayoutInflater.from(this).inflate(R.layout.micro_list_header, null);
        microIcon=(ImageView) header.findViewById(R.id.MicroIcon);
        ImageLoader.getInstance().displayImage(MyApp.INSTANCE().getUserAvatarUrl(),microIcon, Config.options);
        text_name = (TextView)header.findViewById(R.id.text_header_name);
        text_name.setText(MyApp.INSTANCE().getCurrentuser().getUsername());

        customListView=(CustomListView) findViewById(R.id.customListView);
        customListView.setVerticalScrollBarEnabled(false);
        customListView.addHeaderView(header);
        customListView.setAdapter(mAdapter);
        //下拉刷新
        customListView.setOnRefreshListener(new CustomListView.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // TODO Auto-generated method stub
                String s="下拉刷新";
                getData(s);
            }

        });
        //上拉加载更多
        customListView.setOnLoadListener(new CustomListView.OnLoadMoreListener() {

            public void onLoadMore() {
                String s="上拉加载更多";
                getData(s);
            }
        });

    }


    private void getData(String s) {
        // TODO Auto-generated method stub
        if("下拉刷新".equals(s)){
            if (MyApp.INSTANCE().getUpdate()){
                friendCircleModel.getDynamicItem(this);
            }
            customListView.onRefreshComplete();
        }else{
            if (MyApp.INSTANCE().getUpdate()){
                friendCircleModel.getDynamicItem(this);
            }
            customListView.onLoadMoreComplete(); // 加载更多完成
        }
    }

    public void onRefresh(List<FriendsCircle> list) {
        mDynamicList = list;
        mAdapter.setDatas(mDynamicList);
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.circle_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_toedit:
                startActivity(new Intent(this,EditCircleActivity.class));
                finish();
                break;
        }
        return true;
    }

}

